"""General task for graph binary classification."""
from typing import Any, Dict, List, Tuple, Optional

import numpy as np
import tensorflow as tf

from sklearn import metrics as sk_metrics
from glice.models.graph_regression_task import GraphRegressionTask


class GraphBinaryClassificationTask(GraphRegressionTask):
    @classmethod
    def get_default_hyperparameters(cls, mp_style: Optional[str] = None) -> Dict[str, Any]:
        super_params = super().get_default_hyperparameters(mp_style)
        these_hypers: Dict[str, Any] = {}
        super_params.update(these_hypers)
        return super_params

    def compute_task_output(self, batch_features: Dict[str, tf.Tensor], final_node_representations: tf.Tensor,
                            training: bool) -> Any:
        per_graph_regression_results = super().compute_task_output(
            batch_features, final_node_representations, training
        )

        return tf.nn.sigmoid(per_graph_regression_results)

    def compute_task_metrics(self, batch_features: Dict[str, tf.Tensor], task_output: Any,
                             batch_labels: Dict[str, tf.Tensor]) -> Dict[str, tf.Tensor]:
        ce = tf.reduce_mean(
            tf.keras.losses.binary_crossentropy(
                y_true=batch_labels["target_value"],
                y_pred=task_output,
                from_logits=False,
            )
        )

        num_graphs = tf.cast(batch_features["num_graphs_in_batch"], tf.float32)

        return {
            "loss": ce,
            "batch_labels": batch_labels["target_value"],
            "task_output": tf.cast(tf.math.round(task_output), tf.int32),
            "num_graphs": num_graphs,
        }

    def compute_epoch_metrics(self, task_results: List[Any]) -> Tuple[float, str]:
        labels = np.concatenate([batch_task_result["batch_labels"] for batch_task_result in task_results])
        output = np.concatenate([batch_task_result["task_output"] for batch_task_result in task_results])

        epoch_acc = sk_metrics.accuracy_score(labels, output)
        epoch_recall = sk_metrics.recall_score(labels, output)
        epoch_precision = sk_metrics.precision_score(labels, output)
        epoch_f1 = sk_metrics.f1_score(labels, output)

        return -epoch_f1, f"Accuracy = {epoch_acc:.4f} | Recall = {epoch_recall:.4f} | " \
                           f"Precision = {epoch_precision:.4f} | F1 = {epoch_f1:4f}"
