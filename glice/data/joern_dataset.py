import json
import os
from typing import Any, Dict, List, Iterator, Tuple, Optional, Set

import numpy as np
import tensorflow as tf

from .graph_dataset import DataFold, GraphSample, GraphBatchTFDataDescription, GraphDataset
from .token2vec import Token2Vec
from .utils import compute_number_of_edge_types, get_tied_edge_types, process_adjacency_lists


class JoernGraphSample(GraphSample):
    """Data structure holding a single PPI graph."""

    def __init__(self, adjacency_lists: List[np.ndarray], node_features: List[np.ndarray], target_value: float):
        super().__init__(adjacency_lists, node_features)
        self._target_value = target_value

    @property
    def target_value(self) -> float:
        """Node labels to predict as ndarray of shape [V, C]"""
        return self._target_value


class JoernDataset(GraphDataset[JoernGraphSample]):
    @classmethod
    def get_default_hyperparameters(cls) -> Dict[str, Any]:
        super_hypers = super().get_default_hyperparameters()
        this_hypers = {
            "max_nodes_per_batch": 10000,
            "add_self_loop_edges": True,
            "tie_fwd_bkwd_edges": False,
            "w2v_vector_size": 100,
            "w2v_window": 5
        }
        super_hypers.update(this_hypers)

        return super_hypers

    def __init__(self, split_index: int, data_dir: str, vocab_dir: str, params: Dict[str, Any],
                 metadata: Optional[Dict[str, Any]] = None, **kwargs):
        super().__init__(params, metadata=metadata, **kwargs)

        self._split_index = split_index
        self._data_dir = data_dir
        self._vocab_dir = vocab_dir

        num_fwd_edge_types = 4

        self._tied_fwd_bkwd_edge_types = get_tied_edge_types(params["tie_fwd_bkwd_edges"], num_fwd_edge_types)

        self._num_edge_types = compute_number_of_edge_types(self._tied_fwd_bkwd_edge_types, num_fwd_edge_types,
                                                            params["add_self_loop_edges"])

        # Things that will be filled once we load data:
        self._loaded_data: Dict[DataFold, List[JoernGraphSample]] = {}

    @property
    def num_edge_types(self) -> int:
        return self._num_edge_types

    @property
    def node_feature_shape(self) -> Tuple:
        return self.params["w2v_vector_size"],

    # -------------------- Data Loading --------------------
    def load_data(self, folds_to_load: Optional[Set[DataFold]] = None) -> None:
        # If we haven't defined what folds to load, load all:
        if folds_to_load is None:
            folds_to_load = {DataFold.TRAIN, DataFold.VALIDATION, DataFold.TEST}

        if DataFold.TRAIN in folds_to_load:
            self._loaded_data[DataFold.TRAIN] = self.__load_data(DataFold.TRAIN)
        if DataFold.VALIDATION in folds_to_load:
            self._loaded_data[DataFold.VALIDATION] = self.__load_data(DataFold.VALIDATION)
        if DataFold.TEST in folds_to_load:
            self._loaded_data[DataFold.TEST] = self.__load_data(DataFold.TEST)

    def __load_data(self, data_fold: DataFold) -> List[JoernGraphSample]:
        if data_fold == DataFold.TRAIN:
            data_name = "train"
        elif data_fold == DataFold.VALIDATION:
            data_name = "valid"
        elif data_fold == DataFold.TEST:
            data_name = "test"
        else:
            raise ValueError("Unknown data fold '%s'" % str(data_fold))
        print(" Loading Joern %s data." % data_name)

        filenames = []

        with open("split.txt", "r") as f:
            for line in f.readlines():
                line = line.strip()
                split = line.split(":")
                split_index = int(split[0])
                fold = split[1]
                filename = split[2]

                if split_index == self._split_index and fold == data_name:
                    filenames.append(filename)

        final_graphs = []

        token2vec = Token2Vec.load(self._data_dir, self._vocab_dir, vector_size=self.params["w2v_vector_size"],
                                   window=self.params["w2v_window"])

        for filename in filenames:
            filepath = os.path.join(self._data_dir, filename)

            with open(filepath, 'r') as f:
                graph = json.load(f)

            num_nodes = len(graph["nodes"])

            edges = []
            edge_types = [key for key in graph.keys() if key.endswith("Edges")]

            for edge_type in edge_types:
                edges.append([(edge["from"], edge["to"]) for edge in graph[edge_type]])

            adjacency_lists = process_adjacency_lists(edges, num_nodes, self.params["add_self_loop_edges"],
                                                      self._tied_fwd_bkwd_edge_types)

            nodes = [token2vec.get_vector(node) for node in graph["nodes"]]

            label = 0 if graph["label"] == "good" else 1

            final_graph = JoernGraphSample(adjacency_lists, nodes, label)
            final_graphs.append(final_graph)

        return final_graphs

    # -------------------- Minibatching --------------------
    def get_batch_tf_data_description(self) -> GraphBatchTFDataDescription:
        data_description = super().get_batch_tf_data_description()
        return GraphBatchTFDataDescription(
            batch_features_types=data_description.batch_features_types,
            batch_features_shapes=data_description.batch_features_shapes,
            batch_labels_types={**data_description.batch_labels_types, "target_value": tf.float32},
            batch_labels_shapes={**data_description.batch_labels_shapes, "target_value": (None,)},
        )

    def _graph_iterator(self, data_fold: DataFold) -> Iterator[JoernGraphSample]:
        loaded_data = self._loaded_data[data_fold]
        if data_fold == DataFold.TRAIN:
            np.random.shuffle(loaded_data)
        return iter(loaded_data)

    def _new_batch(self) -> Dict[str, Any]:
        new_batch = super()._new_batch()
        new_batch["target_value"] = []
        return new_batch

    def _add_graph_to_batch(self, raw_batch, graph_sample: JoernGraphSample) -> None:
        super()._add_graph_to_batch(raw_batch, graph_sample)
        raw_batch["target_value"].append(graph_sample.target_value)

    def _finalise_batch(self, raw_batch) -> Tuple[Dict[str, Any], Dict[str, Any]]:
        batch_features, batch_labels = super()._finalise_batch(raw_batch)
        batch_labels["target_value"] = np.array(raw_batch["target_value"])
        return batch_features, batch_labels
