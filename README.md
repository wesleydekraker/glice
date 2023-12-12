# Combining Graph Neural Networks and Program Slicing

Implementation of the MultiGLICE deep learning model. MultiGLICE detects software vulnerabilities by combining graph neural networks and program slicing.

The implementation is inspired by [FUNDED](https://github.com/HuantWang/FUNDED_NISL) and the graph neural network architecture is based on Microsoft's [tf2-gnn](https://github.com/microsoft/tf2-gnn) repo.

## Installation

This code was tested in Python 3.10 with TensorFlow 2.15. The operating system used was Ubuntu 22.04.

To perform deep learning on the GPU, install CUDA 12.2 and cuDNN 8.9. This will dramatically speed up the process.

The requirements can be installed using pip:
```
$ pip install -r glice/requirements.txt 
```

## Testing the Installation

To test if all components are set up correctly, you can run an experiment on the
Software Assurance Reference Dataset (SARD).
You can download the data for this task from https://wesleydekraker.com/glice/graphs.zip
and unzip it into a local directory (e.g., `graphs`). This dataset contains buffer vulnerabilities.
Then, you can use the script `train.py` (see `--help` for a description
of options) to train the Gated Graph Neural Network model. Run the script from the project root folder:
```
$ python3 glice/cli/train.py --model GGNN --max-epochs 100 --data-dir graphs
Setting random seed 0.
Ignoring 5644 non-vulnerable graphs.
Trying to load task/model-specific default parameters from /home/thesis/Projects/glice/glice/cli_utils/default_hypers/GLICE_GGNN.json ... File found.
 Dataset default parameters: {'max_nodes_per_batch': 10000, 'add_self_loop_edges': True, 'tie_fwd_bkwd_edges': False, 'w2v_vector_size': 100, 'w2v_window': 5}
Loading data, split index: 0.
 Loading Joern train data.
Generating word2vec model...
Average nodes per graph: 88.57912470952749.
Created word2vec model.
 Loading Joern valid data.
 Model default parameters: {'gnn_aggregation_function': 'sum', 'gnn_message_activation_function': 'relu', 'gnn_message_activation_before_aggregation': False, 'gnn_hidden_dim': 16, 'gnn_use_target_state_as_input': False, 'gnn_normalize_by_num_incoming': True, 'gnn_num_edge_MLP_hidden_layers': 0, 'gnn_message_calculation_class': 'GGNN', 'gnn_initial_node_representation_activation': 'tanh', 'gnn_dense_intermediate_layer_activation': 'tanh', 'gnn_num_layers': 4, 'gnn_dense_every_num_layers': 2, 'gnn_residual_every_num_layers': 2, 'gnn_use_inter_layer_layernorm': False, 'gnn_layer_input_dropout_rate': 0.0, 'gnn_global_exchange_mode': 'gru', 'gnn_global_exchange_every_num_layers': 2, 'gnn_global_exchange_weighting_fun': 'softmax', 'gnn_global_exchange_num_heads': 4, 'gnn_global_exchange_dropout_rate': 0.2, 'optimizer': 'Adam', 'learning_rate': 0.001, 'learning_rate_warmup_steps': None, 'learning_rate_decay_steps': None, 'momentum': 0.85, 'rmsprop_rho': 0.98, 'gradient_clip_value': None, 'gradient_clip_norm': None, 'gradient_clip_global_norm': None, 'use_intermediate_gnn_results': True, 'graph_aggregation_output_size': 32, 'graph_aggregation_num_heads': 4, 'graph_aggregation_layers': [32, 32], 'graph_aggregation_dropout_rate': 0.1, 'regression_mlp_layers': [64, 32], 'regression_mlp_dropout': 0.1}
  Model parameters overridden by task/model defaults: {'gnn_dense_every_num_layers': 3, 'gnn_dense_intermediate_layer_activation': 'tanh', 'gnn_hidden_dim': 128, 'gnn_initial_node_representation_activation': 'tanh', 'gnn_layer_input_dropout_rate': 0.3, 'gnn_message_activation_function': 'leaky_relu', 'gnn_num_layers': 3, 'gnn_residual_every_num_layers': 2, 'graph_aggregation_num_heads': 8, 'optimizer': 'Adam'}
Dataset parameters: {"max_nodes_per_batch": 10000, "add_self_loop_edges": true, "tie_fwd_bkwd_edges": false, "w2v_vector_size": 100, "w2v_window": 5}
Model parameters: {"gnn_aggregation_function": "sum", "gnn_message_activation_function": "leaky_relu", "gnn_message_activation_before_aggregation": false, "gnn_hidden_dim": 128, "gnn_use_target_state_as_input": false, "gnn_normalize_by_num_incoming": true, "gnn_num_edge_MLP_hidden_layers": 0, "gnn_message_calculation_class": "GGNN", "gnn_initial_node_representation_activation": "tanh", "gnn_dense_intermediate_layer_activation": "tanh", "gnn_num_layers": 3, "gnn_dense_every_num_layers": 3, "gnn_residual_every_num_layers": 2, "gnn_use_inter_layer_layernorm": false, "gnn_layer_input_dropout_rate": 0.3, "gnn_global_exchange_mode": "gru", "gnn_global_exchange_every_num_layers": 2, "gnn_global_exchange_weighting_fun": "softmax", "gnn_global_exchange_num_heads": 4, "gnn_global_exchange_dropout_rate": 0.2, "optimizer": "Adam", "learning_rate": 0.001, "learning_rate_warmup_steps": null, "learning_rate_decay_steps": null, "momentum": 0.85, "rmsprop_rho": 0.98, "gradient_clip_value": null, "gradient_clip_norm": null, "gradient_clip_global_norm": null, "use_intermediate_gnn_results": true, "graph_aggregation_output_size": 32, "graph_aggregation_num_heads": 8, "graph_aggregation_layers": [32, 32], "graph_aggregation_dropout_rate": 0.1, "regression_mlp_layers": [64, 32], "regression_mlp_dropout": 0.1}
Initial valid metric: Accuracy = 0.4683 | Recall = 0.6938 | Precision = 0.4781 | F1 = 0.566114.
   (Stored model metadata to models/GGNN_GLICE__2022-06-21_09-01-20_best.pkl and weights to models/GGNN_GLICE__2022-06-21_09-01-20_best.hdf5)
== Epoch 1
 Train:  0.6587 loss | Accuracy = 0.6379 | Recall = 0.7077 | Precision = 0.6210 | F1 = 0.661531 | 2442.93 graphs/s
 Valid:  0.4453 loss | Accuracy = 0.7359 | Recall = 0.9564 | Precision = 0.6637 | F1 = 0.783597 | 4801.34 graphs/s
  (Best epoch so far, target metric decreased to -0.7836 from -0.5661.)
   (Stored model metadata to models/GGNN_GLICE__2022-06-21_09-01-20_best.pkl and weights to models/GGNN_GLICE__2022-06-21_09-01-20_best.hdf5)
[...]
```

After training finished, `python3 glice/cli/test.py --trained-model models/GGNN_GLICE__2022-06-21_09-01-20_best.pkl --data-dir graphs` can be used to test the trained model.

A pretrained word2vec model is available here: https://wesleydekraker.com/glice/word2vec.zip.

To generate your own graph dataset. Download (non-)vulnerable code samples from https://wesleydekraker.com/glice/source.zip and unzip it.
Install Joern using the following commands:
```
$ cd Preprocessing
$ ./install.sh
Examining Joern installation...
Building and installing plugin - incl. domain classes for schema extension..
[...]
```

Start the interactive Joern shell to perform program slicing:
```
$ cd ..
$ rm -rf graphs
$ ./Preprocessing/joern-inst/joern-cli/joern
creating workspace directory
[...]
$ joern> importCode("source")
Using generator for language: NEWC: CCpgGenerator
Creating project `source` for code at `source`
[...]
$ joern> opts.slice.targetDepth = 4
$ joern> run.slice
The graph has been modified.
[...]
```

As an alternative to running the commands above in an interactive shell, you can also use the following command:
```
$ ./Preprocessing/joern-inst/joern-cli/joern --script Preprocessing/script.sc \
  --params sourceFolder=source,outputFolder=graphs,exportMode=method
```

The generated graphs are located in the folder "graphs".

# Code Structure

The description of the code structure is taken from the [tf2-gnn](https://github.com/microsoft/tf2-gnn) repo.

## Layers

The core functionality of the library is implemented as TensorFlow 2 (Keras) layers,
enabling easy integration into other code.


### `tf2_gnn.layers.GNN`

This implements a deep Graph Neural Network, stacking several layers of message passing.
On construction, a dictionary of hyperparameters needs to be provided (default
values can be obtained from `GNN.get_default_hyperparameters()`).
These hyperparameters configure the exact stack of GNN layers:
* `"num_layers"` sets the number of GNN message passing layers (usually, a number
  between 2 and 16)

* `"message_calculation_class"` configures the message passing style.
  This chooses the `tf2_gnn.layers.message_passing.*` layer used in each step.
  
  We currently support the following:
    * `GGNN`: Gated Graph Neural Networks ([Li et al., 2015](#li-et-al-2015)).
    * `RGCN`: Relational Graph Convolutional Networks ([Schlichtkrull et al., 2017](#schlichtkrull-et-al-2017)).
    * `RGAT`: Relational Graph Attention Networks ([Veličković et al., 2018](#veličković-et-al-2018)).
    * `RGIN`: Relational Graph Isomorphism Networks ([Xu et al., 2019](#xu-et-al-2019)).
    * `GNN-Edge-MLP`: Graph Neural Network with Edge MLPs - a variant of RGCN in which messages on edges are computed using full MLPs, not just a single layer applied to the source state.
    * `GNN-FiLM`: Graph Neural Networks with Feature-wise Linear Modulation ([Brockschmidt, 2019](#brockschmidt-2019)) - a new extension of RGCN with FiLM layers.
  
  Some of these expose additional hyperparameters; refer to their implementation for
  details.

* `"hidden_dim"` sets the size of the output of all message passing layers.

* `"layer_input_dropout_rate"` sets the dropout rate (during training) for the
  input of each message passing layer.

* `"residual_every_num_layers"` sets how often a residual connection is inserted
  between message passing layers. Concretely, a value of `k` means that every layer
  `l` that is a multiple of `k` (and only those!) will not receive the outputs of
  layer `l-1` as input, but instead the mean of the outputs of layers `l-1` and `l-k`.

* `"use_inter_layer_layernorm"` is a boolean flag indicating if `LayerNorm` should be
  used between different message passing layers.

* `"dense_every_num_layers"` configures how often a per-node representation dense layer
  is inserted between the message passing layers.
  Setting this to a large value (greather than `"num_layers"`) means that no dense
  layers are inserted at all.
  
  `"dense_intermediate_layer_activation"` configures the activation function used after
  the dense layer; the default of `"tanh"` can help stabilise training of very deep
  GNNs.

* `"global_exchange_every_num_layers"` configures how often a graph-level exchange of
  information is performed.
  For this, a graph level representation (see `tf2_gnn.layers.NodesToGraphRepresentation`
  below) is computed and then used to update the representation of each node.
  The style of this update is configured by `"global_exchange_mode"`, offering three
  modes:
    * `"mean"`, which just computes the arithmetic mean of the node and graph-level
      representation.
    * `"mlp"`, which computes a new representation using an MLP that gets the
      concatenation of node and graph level representations as input.
    * `"gru"`, which uses a GRU cell that gets the old node representation as state
      and the graph representation as input.

The `GNN` layer takes a `GNNInput` named tuple as input, which encapsulates initial
node features, adjacency lists, and auxiliary information.
The easiest way to construct such a tuple is to use the provided [dataset](datasets)
classes in combination with the provided [model](models).


### `tf2_gnn.layers.NodesToGraphRepresentation`

This implements the task of computing a graph-level representation given node-level
representations (e.g., obtained by the `GNN` layer).

Currently, this is only implemented by the `WeightedSumGraphRepresentation` layer,
which produces a graph representation by a multi-headed weighted sum of (transformed) 
node representations, configured by the following hyperparameters set in the
layer constructor:
* `graph_representation_size` sets the size of the computed representation.
  By setting this to `1`, this layer can be used to directly implement graph-level
  regression tasks.
* `num_heads` configures the number of parallel (independent) weighted sums that
  are computed, whose results are concatenated to obtain the final result.
  Note that this means that the `graph_representation_size` needs to be a multiple
  of the `num_heads` value.
* `weighting_fun` can take two values:
  * `"sigmoid"` computes a weight for each node independently by first computing
    a per-node score, which is then squashed through a sigmoid.
    This is appropriate for tasks that are related to counting occurrences of a 
    feature in a graph, where the node weight is used to ignore certain nodes.
  * `"softmax"` computes weights for all graph nodes together by first computing
    per-node scores, and then performing a softmax over all scores.
    This is appropriate for tasks that require identifying important parts of
    the graph.
* `scoring_mlp_layers`, `scoring_mlp_activation_fun`, `scoring_mlp_dropout_rate`
  configure the MLP that computes the per-node scores.
* `transformation_mlp_layers`, `transformation_mlp_activation_fun`, 
  `transformation_mlp_dropout_rate` configure the MLP that computes the
  transformed node representations that are summed up.


## Datasets

We use a sparse representation of graphs, which requires a complex batching strategy
in which the graphs making up a minibatch are joined into a single graph of many
disconnected components.
The extensible `glice.data.GraphDataset` class implements this procedure, and can
be subclassed to handle task-specific datasets and additional properties.
It exposes a `get_tensorflow_dataset` method that can be used to obtain a 
`tf.data.Dataset` that can be used in training/evaluation loops.


## Models

We provide a built-in model in `glice.models`, which can either be directly
re-used or serve as inspiration for other models:
* `glice.models.GraphBinaryClassificationTask` implements a binary classification
  model.


# References

#### Brockschmidt, 2019
Marc Brockschmidt. GNN-FiLM: Graph Neural Networks with Feature-wise Linear
Modulation. (https://arxiv.org/abs/1906.12192)

#### Li et al., 2015
Yujia Li, Daniel Tarlow, Marc Brockschmidt, and Richard Zemel. Gated Graph
Sequence Neural Networks. In International Conference on Learning
Representations (ICLR), 2016. (https://arxiv.org/pdf/1511.05493.pdf)

#### Schlichtkrull et al., 2017
Michael Schlichtkrull, Thomas N. Kipf, Peter Bloem, Rianne van den Berg,
Ivan Titov, and Max Welling. Modeling Relational Data with Graph
Convolutional Networks. In Extended Semantic Web Conference (ESWC), 2018.
(https://arxiv.org/pdf/1703.06103.pdf)

#### Veličković et al. 2018
Petar Veličković, Guillem Cucurull, Arantxa Casanova, Adriana Romero, Pietro
Liò, and Yoshua Bengio. Graph Attention Networks. In International Conference
on Learning Representations (ICLR), 2018. (https://arxiv.org/pdf/1710.10903.pdf)

#### Xu et al. 2019
Keyulu Xu, Weihua Hu, Jure Leskovec, and Stefanie Jegelka. How Powerful are
Graph Neural Networks? In International Conference on Learning Representations
(ICLR), 2019. (https://arxiv.org/pdf/1810.00826.pdf)

# Contributing

This project welcomes contributions and suggestions.  Most contributions require you to agree to a
Contributor License Agreement (CLA) declaring that you have the right to, and actually do, grant us
the rights to use your contribution.
