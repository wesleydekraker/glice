import json
from functools import partial

import optuna
from optuna import Study, Trial, TrialPruned
from optuna.trial import TrialState

from glice.cli_utils import run_train_from_args


def run_hpo_from_args(args) -> None:
    study_name = "glice-study"
    storage_name = f"sqlite:///{study_name}.db"

    pruner = optuna.pruners.MedianPruner(n_startup_trials=10, n_warmup_steps=5, interval_steps=1)
    study = optuna.create_study(study_name=study_name, storage=storage_name, direction="minimize", load_if_exists=True,
                                pruner=pruner)
    study.optimize(partial(objective, args), n_trials=1, catch=(RuntimeError,))
    print_stats(study)


def print_stats(study: Study):
    pruned_trials = study.get_trials(deepcopy=False, states=(TrialState.PRUNED,))
    complete_trials = study.get_trials(deepcopy=False, states=(TrialState.COMPLETE,))

    with open("hpo.txt", "w") as f:
        f.write("Study statistics:\n")
        f.write(f"  Number of finished trials: {len(study.trials)}\n")
        f.write(f"  Number of pruned trials: {len(pruned_trials)}\n")
        f.write(f"  Number of complete trials: {len(complete_trials)}\n")

        write_trial(study.best_trial, f, header="Best trial")

        trials = sorted(study.get_trials(deepcopy=False, states=(TrialState.COMPLETE,)), key=lambda trail: trail.value)
        for trial in trials:
            write_trial(trial, f)


def write_trial(trial, f, header="Trial"):
    f.write(f"{header}:\n")

    f.write(f"  Value: {trial.value}\n")

    f.write("  Params: \n")
    for key, value in trial.params.items():
        f.write(f"    {key}: {value}\n")

    f.write("  Attributes: \n")
    for key, value in trial.user_attrs.items():
        f.write(f"    {key}: {value}\n")


def objective(args, trial: Trial):
    model_params = [
        suggest_categorical(trial, "gnn_message_activation_function", ["tanh", "relu", "leaky_relu"]),
        suggest_categorical(trial, "gnn_hidden_dim", [4, 8, 16, 32, 64, 128, 256]),
        suggest_categorical(trial, "gnn_initial_node_representation_activation", ["tanh", "relu", "leaky_relu"]),
        suggest_categorical(trial, "gnn_dense_intermediate_layer_activation", ["tanh", "relu", "leaky_relu"]),
        suggest_int(trial, "gnn_residual_every_num_layers", 2, 4),
        suggest_int(trial, "gnn_num_layers", 3, 6),
        suggest_float(trial, "gnn_layer_input_dropout_rate", 0.1, 0.5, step=0.1),
        suggest_int(trial, "gnn_dense_every_num_layers", 2, 4),
        suggest_categorical(trial, "optimizer", ["SGD", "RMSProp", "Adam"]),
        suggest_categorical(trial, "graph_aggregation_num_heads", [4, 8, 16])
    ]

    args.model_param_override = json.dumps(dict(model_params))

    try:
        metrics = run_train_from_args(args, pruner=partial(pruner_func, trial))
    except TrialPruned:
        raise
    except Exception as e:
        raise RuntimeError(f"Trial ran into errors: {e}")

    trial.set_user_attr("best_valid_metric", metrics["best_valid_metric"])
    trial.set_user_attr("best_train_speed", metrics["best_train_speed"])

    return metrics["best_valid_loss"]


def pruner_func(trial, loss, step):
    trial.report(loss, step)

    if trial.should_prune():
        raise optuna.TrialPruned()


def suggest_categorical(trial, name, choices):
    return name, trial.suggest_categorical(name, choices)


def suggest_int(trial, name, low, high, step=1):
    return name, trial.suggest_int(name, low, high, step=step)


def suggest_float(trial, name, low, high, step):
    return name, trial.suggest_float(name, low, high, step=step)
