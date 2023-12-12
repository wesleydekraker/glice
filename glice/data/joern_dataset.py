import json
import os
from abc import abstractmethod
from typing import Any, Dict, List, Iterator, Tuple, Optional, Set

import numpy as np
import tensorflow as tf

from .graph_dataset import DataFold, GraphSample, GraphBatchTFDataDescription, GraphDataset
from .token2vec import Token2Vec
from .utils import compute_number_of_edge_types, get_tied_edge_types, process_adjacency_lists

cwe_map = {
    15: 0,
    194: 1,
    195: 2,
    319: 3,
    476: 4,
    563: 5,
    643: 6,
    80: 7,
    98: 8,
    415: 9,
    126: 10,
    690: 11,
    457: 12,
    606: 13,
    127: 14,
    124: 15,
    401: 16,
    91: 17,
    590: 18,
    601: 19,
    90: 20,
    36: 21,
    23: 22,
    113: 23,
    789: 24,
    121: 25,
    762: 26,
    400: 27,
    122: 28,
    134: 29,
    78: 30,
    369: 31,
    197: 32,
    129: 33,
    191: 34,
    190: 35,
    89: 36,
    79: 37
}

language_map = {
    "php": "PHP",
    "cs": "C#",
    "java": "Java",
    "c": "C",
    "cpp": "C++"
}


class JoernGraphSample(GraphSample):
    """Data structure holding a single PPI graph."""

    def __init__(self, adjacency_lists: List[np.ndarray], node_features: List[np.ndarray], filename: str,
                 language: str,
                 is_safe: bool,
                 cwe: int,
                 label: int,
                 target_value: List[float]):
        super().__init__(adjacency_lists, node_features)
        self._filename = filename
        self._language = language
        self._is_safe = is_safe
        self._cwe = cwe
        self._label = label
        self._target_value = target_value

    @property
    def filename(self) -> str:
        return self._filename

    @property
    def language(self) -> str:
        return self._language

    @property
    def is_safe(self) -> bool:
        return self._is_safe

    @property
    def cwe(self) -> int:
        return self._cwe

    @property
    def label(self) -> int:
        return self._label

    @property
    def target_value(self) -> List[float]:
        """Node labels to predict as ndarray of shape [V, C]"""
        return self._target_value


class JoernDataset(GraphDataset[JoernGraphSample]):
    @classmethod
    def get_default_hyperparameters(cls) -> Dict[str, Any]:
        super_hypers = super().get_default_hyperparameters()
        this_hypers = {
            "max_nodes_per_batch": 10000,
            "add_cfg_edges": True,
            "add_reaching_def_edges": True,
            "add_cdg_edges": True,
            "add_next_edges": True,
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

        num_fwd_edge_types = 1 + int(params["add_cfg_edges"]) + int(params["add_reaching_def_edges"]) + \
                             int(params["add_cdg_edges"]) + int(params["add_next_edges"])

        self._tied_fwd_bkwd_edge_types = get_tied_edge_types(params["tie_fwd_bkwd_edges"], num_fwd_edge_types)

        self._num_edge_types = compute_number_of_edge_types(self._tied_fwd_bkwd_edge_types, num_fwd_edge_types,
                                                            params["add_self_loop_edges"])

        # Things that will be filled once we load data:
        self._loaded_data: Dict[DataFold, List[JoernGraphSample]] = {}

    @property
    def split_index(self) -> int:
        return self._split_index

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
        print(" Loading Joern %s data." % data_fold.name)

        filenames = JoernDataset._load_split(self._split_index, data_fold)

        token2vec = Token2Vec.load(self._data_dir, self._vocab_dir,
                                   vector_size=self.params["w2v_vector_size"],
                                   window=self.params["w2v_window"])

        data = []

        for filename in filenames:
            result = _parse_graph(filename, data_dir=self._data_dir, params=self._params,
                                  tied_fwd_bkwd_edge_types=self._tied_fwd_bkwd_edge_types,
                                  token2vec=token2vec)
            data.append(result)

        if data_fold == DataFold.TRAIN:
            data = self._custom_oversample(data)

        return data

    @staticmethod
    def _custom_oversample(samples: List[JoernGraphSample]):
        # Get unique combinations of 'cwe' and 'language'
        unique_combinations = set((sample.cwe, sample.language) for sample in samples)

        balanced_samples = []

        # For each unique combination
        for cwe, language in unique_combinations:
            # Separate samples into safe and unsafe
            safe_samples = [sample for sample in samples if
                            sample.cwe == cwe and sample.language == language and sample.is_safe]
            unsafe_samples = [sample for sample in samples if
                              sample.cwe == cwe and sample.language == language and not sample.is_safe]

            # Identify the group with the lesser number of samples
            if len(safe_samples) < len(unsafe_samples):
                smaller_group = safe_samples
                larger_group = unsafe_samples
            else:
                smaller_group = unsafe_samples
                larger_group = safe_samples

            # Oversample from the smaller group
            additional_samples_needed = len(larger_group) - len(smaller_group)
            random = np.random.default_rng(cwe)
            oversampled_smaller_group = random.choice(smaller_group, size=additional_samples_needed).tolist()

            balanced_combination_samples = larger_group + smaller_group + oversampled_smaller_group

            balanced_samples.extend(balanced_combination_samples)

        return balanced_samples

    @staticmethod
    def _load_split(index: int, fold: DataFold) -> List[str]:
        filenames = []

        with open("split.txt", "r") as f:
            for line in f.readlines():
                line = line.strip()
                split = line.split(":")
                line_index = int(split[0])
                line_fold = split[1]
                filename = split[2]

                if line_index == index and line_fold == fold.name:
                    filenames.append(filename)

        return filenames

    # -------------------- Minibatching --------------------
    def get_batch_tf_data_description(self) -> GraphBatchTFDataDescription:
        data_description = super().get_batch_tf_data_description()
        return GraphBatchTFDataDescription(
            batch_features_types=data_description.batch_features_types,
            batch_features_shapes=data_description.batch_features_shapes,
            batch_labels_types={**data_description.batch_labels_types, "target_value": tf.float32},
            batch_labels_shapes={**data_description.batch_labels_shapes, "target_value": (None, 39)},
        )

    def _graph_iterator(self, data_fold: DataFold) -> Iterator[JoernGraphSample]:
        loaded_data = self._loaded_data[data_fold]
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

    def get_filenames(self, data_fold: DataFold) -> list[str]:
        return [graph.filename for graph in self._loaded_data[data_fold]]

    def shuffle(self, data_fold: DataFold):
        np.random.shuffle(self._loaded_data[data_fold])


def get_label(label: str, cwe: int):
    if label == "good":
        return len(cwe_map)
    else:
        return cwe_map[cwe]


def get_language(file_path: str):
    return language_map[file_path.split(".")[-1]]


def _get_one_hot_encoding(label: str, cwe: int):
    number_of_classes = len(cwe_map) + 1
    one_hot_encoding = [0.0 for _ in range(number_of_classes)]

    index = get_label(label, cwe)
    one_hot_encoding[index] = 1.0

    return one_hot_encoding


def _parse_graph(filename: str, data_dir: str, params: Dict[str, Any],
                 tied_fwd_bkwd_edge_types: Set[int], token2vec: Token2Vec) -> JoernGraphSample:
    filepath = os.path.join(data_dir, filename)

    with open(filepath, 'r') as f:
        graph = json.load(f)

    num_nodes = len(graph["nodes"])

    edge_types = ["astEdges"]

    if params["add_cfg_edges"]:
        edge_types.append("astEdges")

    if params["add_reaching_def_edges"]:
        edge_types.append("reachingDefEdges")

    if params["add_cdg_edges"]:
        edge_types.append("cdgEdges")

    edges = []
    for edge_type in edge_types:
        edges.append([(edge["from"], edge["to"]) for edge in graph[edge_type]])

    if params["add_next_edges"]:
        next_edges = []
        for i in range(0, num_nodes - 1):
            next_edges.append((i, i + 1))

        edges.append(next_edges)

    adjacency_lists = process_adjacency_lists(edges, num_nodes, params["add_self_loop_edges"],
                                              tied_fwd_bkwd_edge_types)

    nodes = [token2vec.get_vector(node) for node in graph["nodes"]]

    language = get_language(graph["filePath"])
    is_safe = graph["label"] == "good"
    cwe = graph["cwe"]
    label = get_label(graph["label"], graph["cwe"])
    one_hot_encoding = _get_one_hot_encoding(graph["label"], graph["cwe"])

    return JoernGraphSample(adjacency_lists, nodes, filename, language, is_safe, cwe, label, one_hot_encoding)
