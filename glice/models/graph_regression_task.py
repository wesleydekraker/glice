"""General task for GNN regression."""
from typing import Any, Dict, List, Tuple, Optional, Union

import tensorflow as tf
from dpu_utils.tf2utils import MLP

from glice.data import GraphDataset
from glice.models import GraphTaskModel
from glice.layers import (
    WeightedSumGraphRepresentation,
    NodesToGraphRepresentationInput,
)


class GraphRegressionTask(GraphTaskModel):
    @classmethod
    def get_default_hyperparameters(
        cls, mp_style: Optional[str] = None
    ) -> Dict[str, Any]:
        super_params = super().get_default_hyperparameters(mp_style)
        these_hypers: Dict[str, Any] = {
            "use_intermediate_gnn_results": True,
            "graph_aggregation_output_size": 32,
            "graph_aggregation_num_heads": 4,
            "graph_aggregation_layers": [32, 32],
            "graph_aggregation_dropout_rate": 0.1,
            "regression_mlp_layers": [64, 32],
            "regression_mlp_dropout": 0.1,
        }
        super_params.update(these_hypers)
        return super_params

    def __init__(self, params: Dict[str, Any], dataset: GraphDataset, name: str = None, **kwargs):
        super().__init__(params, dataset=dataset, name=name, **kwargs)
        self._node_to_graph_aggregation = None

        # Construct sublayers:
        self._weighted_avg_of_nodes_to_graph_repr = WeightedSumGraphRepresentation(
            graph_representation_size=self._params["graph_aggregation_output_size"],
            num_heads=self._params["graph_aggregation_num_heads"],
            weighting_fun="softmax",
            scoring_mlp_layers=self._params["graph_aggregation_layers"],
            scoring_mlp_dropout_rate=self._params["graph_aggregation_dropout_rate"],
            scoring_mlp_activation_fun="elu",
            transformation_mlp_layers=self._params["graph_aggregation_layers"],
            transformation_mlp_dropout_rate=self._params["graph_aggregation_dropout_rate"],
            transformation_mlp_activation_fun="elu",
        )
        self._weighted_sum_of_nodes_to_graph_repr = WeightedSumGraphRepresentation(
            graph_representation_size=self._params["graph_aggregation_output_size"],
            num_heads=self._params["graph_aggregation_num_heads"],
            weighting_fun="sigmoid",
            scoring_mlp_layers=self._params["graph_aggregation_layers"],
            scoring_mlp_dropout_rate=self._params["graph_aggregation_dropout_rate"],
            scoring_mlp_activation_fun="elu",
            transformation_mlp_layers=self._params["graph_aggregation_layers"],
            transformation_mlp_dropout_rate=self._params["graph_aggregation_dropout_rate"],
            transformation_mlp_activation_fun="elu",
        )

        self._regression_mlp = MLP(
            out_size=39,
            hidden_layers=self._params["regression_mlp_layers"],
            dropout_rate=self._params["regression_mlp_dropout"],
            use_biases=True,
            activation_fun=tf.nn.relu,
        )

    def build(self, input_shapes):
        if self._params["use_intermediate_gnn_results"]:
            # We get the initial GNN input + results for all layers:
            node_repr_size = (
                input_shapes["node_features"][-1]
                + self._params["gnn_hidden_dim"] * self._params["gnn_num_layers"]
            )
        else:
            node_repr_size = (
                input_shapes["node_features"][-1] + self._params["gnn_hidden_dim"]
            )

        node_to_graph_repr_input = NodesToGraphRepresentationInput(
            node_embeddings=tf.TensorShape((None, node_repr_size)),
            node_to_graph_map=tf.TensorShape(None),
            num_graphs=tf.TensorShape(()),
        )

        with tf.name_scope(self.__class__.__name__):
            with tf.name_scope("graph_representation_computation"):
                with tf.name_scope("weighted_avg"):
                    self._weighted_avg_of_nodes_to_graph_repr.build(
                        node_to_graph_repr_input
                    )
                with tf.name_scope("weighted_sum"):
                    self._weighted_sum_of_nodes_to_graph_repr.build(
                        node_to_graph_repr_input
                    )

            self._regression_mlp.build(
                tf.TensorShape(
                    (None, 2 * self._params["graph_aggregation_output_size"])
                )
            )

        super().build(input_shapes)

    def compute_task_output(self, batch_features: Dict[str, tf.Tensor],
                            final_node_representations: Union[tf.Tensor, Tuple[tf.Tensor, List[tf.Tensor]]],
                            training: bool) -> Any:
        if self._params["use_intermediate_gnn_results"]:
            _, intermediate_node_representations = final_node_representations
            # We want to skip the first "intermediate" representation, which is the output of
            # the initial feature -> GNN input layer:
            node_representations = tf.concat(
                (batch_features["node_features"],)
                + intermediate_node_representations[1:],
                axis=-1,
            )
        else:
            node_representations = tf.concat(
                [batch_features["node_features"], final_node_representations], axis=-1
            )

        graph_representation_layer_input = NodesToGraphRepresentationInput(
            node_embeddings=node_representations,
            node_to_graph_map=batch_features["node_to_graph_map"],
            num_graphs=batch_features["num_graphs_in_batch"],
        )
        weighted_avg_graph_repr = self._weighted_avg_of_nodes_to_graph_repr.call(
            graph_representation_layer_input, training=training
        )
        weighted_sum_graph_repr = self._weighted_sum_of_nodes_to_graph_repr.call(
            graph_representation_layer_input, training=training
        )

        graph_representations = tf.concat(
            [weighted_avg_graph_repr, weighted_sum_graph_repr], axis=-1
        )  # shape: [G, GD]

        per_graph_results = self._regression_mlp.call(
            graph_representations, training=training
        )  # shape: [G, 1]

        return per_graph_results
