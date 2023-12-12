"""General task for graph binary classification."""
from typing import Any, Dict, List, Tuple, Optional, Callable

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

        return tf.nn.softmax(per_graph_regression_results)

    def compute_task_metrics(self, batch_features: Dict[str, tf.Tensor], task_output: Any,
                             batch_labels: Dict[str, tf.Tensor]) -> Dict[str, tf.Tensor]:
        ce = tf.reduce_mean(
            tf.keras.losses.categorical_crossentropy(
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

    def compute_epoch_metrics(self, task_results: List[Any], filenames: list[str], log_metrics_fun: Callable,
                              fold: str) -> Tuple[float, str]:
        batch_labels = np.concatenate([batch_task_result["batch_labels"] for batch_task_result in task_results])
        batch_output = np.concatenate([batch_task_result["task_output"] for batch_task_result in task_results])

        labels = np.argmax(batch_labels, axis=1)
        output = np.argmax(batch_output, axis=1)

        log_metrics_fun(dict(zip(filenames, output)), fold)

        weighted_recall, weighted_precision, weighted_f1 = self._get_metrics(labels, output, average='weighted')
        micro_recall, micro_precision, micro_f1 = self._get_metrics(labels, output, average='micro')
        macro_recall, macro_precision, macro_f1 = self._get_metrics(labels, output, average='macro')
        recall, precision, f1 = self._get_all_metrics(labels, output)

        return -macro_f1, f"Weighted Recall = {weighted_recall:.4f} | Weighted Precision = {weighted_precision:.4f} | Weighted F1 = {weighted_f1:4f}\n" \
                          f"Micro Recall = {micro_recall:.4f} | Micro Precision = {micro_precision:.4f} | Micro F1 = {micro_f1:4f}\n" \
                          f"Macro Recall = {macro_recall:.4f} | Macro Precision = {macro_precision:.4f} | Macro F1 = {macro_f1:4f}\n" \
                          f"Recall = {recall}\n" \
                          f"Precision = {precision}\n" \
                          f"F1 = {f1}"

    def _get_all_metrics(self, labels, output):
        recall, precision, f1 = self._get_metrics(labels, output, average=None)

        def to_string(metric):
            class_metrics = [f"{class_metric:.4f}" for class_metric in metric]
            return ", ".join(class_metrics)

        return to_string(recall), to_string(precision), to_string(f1)

    def _get_metrics(self, labels, output, average):
        recall = sk_metrics.recall_score(labels, output, average=average)
        precision = sk_metrics.precision_score(labels, output, average=average)
        f1 = sk_metrics.f1_score(labels, output, average=average)

        return recall, precision, f1
