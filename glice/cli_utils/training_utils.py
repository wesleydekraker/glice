import os
import random
import time
from typing import Optional, Callable, Dict

import numpy as np
import tensorflow as tf

from .split_utils import split
from ..data import DataFold, GraphDataset
from ..layers import get_known_message_passing_classes
from ..models import GraphTaskModel
from .model_utils import save_model, load_weights_verbosely, get_model_and_dataset


def make_run_id(model_name: str, task_name: str, run_name: Optional[str] = None) -> str:
    """Choose a run ID, based on the --run-name parameter and the current time."""
    if run_name is not None:
        return run_name
    else:
        return "%s_%s__%s" % (model_name, task_name, time.strftime("%Y-%m-%d_%H-%M-%S"))


def log_line(log_file: str, msg: str):
    with open(log_file, "a") as log_fh:
        log_fh.write(msg + "\n")

    print(msg)


def train_loop(model: GraphTaskModel, dataset: GraphDataset, max_epochs: int,
               patience: int, log_fun: Callable[[str], None], log_metrics_fun: Callable,
               save_model_fun: Callable[[GraphTaskModel], None], pruner) -> dict:
    train_data = dataset.get_tensorflow_dataset(DataFold.TRAIN).prefetch(3)
    valid_data = dataset.get_tensorflow_dataset(DataFold.VALIDATION).prefetch(3)

    initial_valid_loss, _, initial_valid_results = model.run_one_epoch(valid_data, training=False)
    best_valid_metric, best_val_str = model.compute_epoch_metrics(initial_valid_results,
                                                                  dataset.get_filenames(DataFold.VALIDATION),
                                                                  log_metrics_fun, "valid")
    log_fun(f"Initial valid metric: {best_val_str}.")
    save_model_fun(model)
    best_valid_epoch = 0
    best_valid_loss = initial_valid_loss
    best_train_speed = 0
    train_time_start = time.time()

    for epoch in range(1, max_epochs + 1):
        log_fun(f"== Epoch {epoch}")
        dataset.shuffle(DataFold.TRAIN)
        train_loss, train_speed, train_results = model.run_one_epoch(train_data, training=True)
        train_metric, train_metric_string = model.compute_epoch_metrics(train_results,
                                                                        dataset.get_filenames(DataFold.TRAIN),
                                                                        log_metrics_fun, "train")
        log_fun(f" Train:  {train_loss:.4f} loss | {train_metric_string} | {train_speed:.2f} graphs/s")
        valid_loss, valid_speed, valid_results = model.run_one_epoch(valid_data, training=False)
        valid_metric, valid_metric_string = model.compute_epoch_metrics(valid_results,
                                                                        dataset.get_filenames(DataFold.VALIDATION),
                                                                        log_metrics_fun, "valid")
        log_fun(f" Valid:  {valid_loss:.4f} loss | {valid_metric_string} | {valid_speed:.2f} graphs/s")

        if pruner is not None:
            pruner(valid_loss, epoch - 1)

        # Save if good enough.
        if valid_metric < best_valid_metric or (valid_metric == best_valid_metric and valid_loss < best_valid_loss):
            log_fun(f"  (Best epoch so far, target metric decreased to "
                    f"{valid_metric:.4f} from {best_valid_metric:.4f}.)")
            save_model_fun(model)
            best_valid_metric = valid_metric
            best_valid_epoch = epoch
        elif epoch - best_valid_epoch >= patience:
            total_time = time.time() - train_time_start
            log_fun(f"Stopping training after {patience} epochs without improvement on validation metric.")
            log_fun(f"Training took {total_time}s. Best validation metric: {best_valid_metric}")
            break

        if valid_loss < best_valid_loss:
            best_valid_loss = valid_loss

        if train_speed > best_train_speed:
            best_train_speed = train_speed

    return dict(best_valid_metric=best_valid_metric, best_valid_loss=best_valid_loss, best_train_speed=best_train_speed)


def train(model: GraphTaskModel, dataset: GraphDataset, log_fun: Callable[[str], None],
          log_metrics_fun: Callable, run_id: str, max_epochs: int, patience: int, save_dir: str,
          pruner: Callable):

    save_file = os.path.join(save_dir, f"{run_id}_best.pkl")

    def save_model_fun(model: GraphTaskModel):
        save_model(save_file, model, dataset)

    train_metrics = train_loop(model, dataset, max_epochs=max_epochs, patience=patience, log_fun=log_fun,
                               log_metrics_fun=log_metrics_fun, save_model_fun=save_model_fun, pruner=pruner)

    return train_metrics, save_file


def run_train_from_args(args, pruner=None) -> dict:
    task = "GLICE"

    # Get the housekeeping going and start logging:
    os.makedirs(args.save_dir, exist_ok=True)
    run_id = make_run_id(args.model, task, args.run_name)
    metrics_map = {}

    def log_metrics(metrics: Dict[str, int], fold: str):
        metrics_file = os.path.join(args.save_dir, f"{run_id}_{fold}.log")

        if not os.path.exists(metrics_file) or fold not in metrics_map:
            filenames = list(metrics.keys())
            metrics_map[fold] = filenames

            with open(metrics_file, "w") as f:
                f.write(",".join(filenames) + "\n")

        filenames = metrics_map[fold]

        with open(metrics_file, "a") as f:
            for filename in filenames:
                if filename == filenames[-1]:
                    f.write(str(metrics[filename]) + "\n")
                else:
                    f.write(str(metrics[filename]) + ",")

    log_file = os.path.join(args.save_dir, f"{run_id}.log")

    def log(msg: str):
        log_line(log_file, msg)

    log(f"Setting random seed {args.random_seed}.")
    random.seed(args.random_seed)
    np.random.seed(args.random_seed)
    tf.random.set_seed(args.random_seed)

    split(args.data_dir)

    dataset, model = get_model_and_dataset(msg_passing_implementation=args.model, task_name=task,
                                           split_index=args.split_index, data_dir=args.data_dir,
                                           vocab_dir=args.vocab_dir, trained_model_file=args.load_saved_model,
                                           cli_data_hyperparameter_overrides=args.data_param_override,
                                           cli_model_hyperparameter_overrides=args.model_param_override,
                                           folds_to_load={DataFold.TRAIN, DataFold.VALIDATION},
                                           load_weights_only=args.load_weights_only,
                                           disable_tf_function_build=args.disable_tf_func)

    log(f"Dataset parameters: {dataset._params}")
    log(f"Model parameters: {model._params}")

    train_metrics, trained_model_path = train(model, dataset, log_fun=log, log_metrics_fun=log_metrics, run_id=run_id,
                                              max_epochs=args.max_epochs, patience=args.patience,
                                              save_dir=args.save_dir, pruner=pruner)

    if args.run_test:
        log("== Running on test dataset")
        log(f"Loading data.")
        dataset.load_data({DataFold.TEST})
        log(f"Restoring best model state from {trained_model_path}.")
        load_weights_verbosely(trained_model_path, model)

        test_data = dataset.get_tensorflow_dataset(DataFold.TEST)
        _, _, test_results = model.run_one_epoch(test_data, training=False)
        _, test_metric_string = model.compute_epoch_metrics(test_results, dataset.get_filenames(DataFold.TEST),
                                                            log_metrics, "test")
        log(f" Test:  {test_metric_string}")

    return train_metrics


def get_train_cli_arg_parser(default_model_type: Optional[str] = None):
    """
    Get an argparse argument parser object with common options for training
    GNN-based models.

    Args:
        default_model_type: If provided, the model type is downgraded from a
            positional parameter on the command line to an option with the
            given default value.
    """
    import argparse

    parser = argparse.ArgumentParser(description="Train a GNN model.")

    parser.add_argument(
        "--model",
        type=str,
        choices=sorted(get_known_message_passing_classes()),
        default=default_model_type,
        help="GNN model type to train.",
    )
    parser.add_argument(
        "--split-index",
        dest="split_index",
        type=int,
        default=0,
        help="Index of the k-fold: 0-9.",
    )
    parser.add_argument(
        "--save-dir",
        dest="save_dir",
        type=str,
        default="models",
        help="Path in which to store the trained model and log.",
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
    parser.add_argument(
        "--max-epochs",
        dest="max_epochs",
        type=int,
        default=10000,
        help="Maximal number of epochs to train for.",
    )
    parser.add_argument(
        "--patience",
        dest="patience",
        type=int,
        default=25,
        help="Maximal number of epochs to continue training without improvement.",
    )
    parser.add_argument(
        "--seed",
        dest="random_seed",
        type=int,
        default=0,
        help="Random seed to use.",
    )
    parser.add_argument(
        "--run-name",
        dest="run_name",
        type=str,
        help="A human-readable name for this run.",
    )
    parser.add_argument(
        "--load-saved-model",
        dest="load_saved_model",
        help="Optional location to load initial model weights from. Should be model stored in earlier run.",
    )
    parser.add_argument(
        "--load-weights-only",
        dest="load_weights_only",
        action="store_true",
        help="Optional to only load the weights of the model rather than class and dataset for further training (used in fine-tuning on pretrained network). Should be model stored in earlier run.",
    )
    parser.add_argument(
        "--disable-tf-func",
        dest="disable_tf_func",
        action="store_true",
        help="Optional to disable the building of tf function graphs and run in eager mode.",
    )
    parser.add_argument(
        "--run-test",
        dest="run_test",
        action="store_true",
        default=False,
        help="Run on testset after training.",
    )
    parser.add_argument(
        "--hyperdrive-arg-parse",
        dest="hyperdrive_arg_parse",
        action="store_true",
        help='Enable hyperdrive argument parsing, in which unknown options "--key val" are interpreted as hyperparameter "key" with value "val".',
    )

    return parser
