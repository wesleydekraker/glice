import json
import os
import random
import time
from typing import Optional, Callable, Any

import numpy as np
import tensorflow as tf
from tensorflow.python.training.tracking import data_structures as tf_data_structures

from .split_utils import split
from ..data import DataFold, GraphDataset
from ..layers import get_known_message_passing_classes
from ..models import GraphTaskModel
from .model_utils import save_model, load_weights_verbosely, get_model_and_dataset


def run_test_from_args(args) -> None:
    dataset, model = get_model_and_dataset(msg_passing_implementation=None, task_name=None,
                                           split_index=args.split_index, data_dir=args.data_dir,
                                           vocab_dir=args.vocab_dir, trained_model_file=args.trained_model,
                                           cli_data_hyperparameter_overrides=args.data_param_override,
                                           cli_model_hyperparameter_overrides=args.model_param_override,
                                           folds_to_load={DataFold.TEST})
    test(model, dataset, lambda msg: print(msg))


def test(model: GraphTaskModel, dataset: GraphDataset, log_fun: Callable[[str], None]):
    log_fun("== Running on test dataset")
    test_data = dataset.get_tensorflow_dataset(DataFold.TEST)
    _, _, test_results = model.run_one_epoch(test_data, training=False)
    _, test_metric_string = model.compute_epoch_metrics(test_results)
    log_fun(test_metric_string)


def get_test_cli_arg_parser():
    import argparse

    parser = argparse.ArgumentParser(description="Test a GNN model.")
    parser.add_argument(
        "--trained-model",
        type=str,
        help="File to load model from (determines model architecture & task).",
    )
    parser.add_argument(
        "--split-index",
        dest="split_index",
        type=int,
        default=0,
        help="Index of the k-fold: 0-9.",
    )
    parser.add_argument(
        "--data-dir",
        dest="data_dir",
        type=str,
        default="graphs",
        help="Directory containing the task data.",
    )
    parser.add_argument(
        "--vocab-dir",
        dest="vocab_dir",
        type=str,
        default="vocab",
        help="Directory containing the data for the word2vec model.",
    )
    parser.add_argument(
        "--model-params-override",
        dest="model_param_override",
        type=str,
        help="JSON dictionary overriding model hyperparameter values.",
    )
    parser.add_argument(
        "--data-params-override",
        dest="data_param_override",
        type=str,
        help="JSON dictionary overriding data hyperparameter values.",
    )

    return parser
