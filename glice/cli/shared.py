import tensorflow as tf
import os
import warnings


def setup_tensorflow():
    os.environ['TF_CPP_MIN_LOG_LEVEL'] = '1'
    tf.get_logger().setLevel("ERROR")

    warnings.simplefilter("ignore")

    gpus = tf.config.list_physical_devices('GPU')
    if gpus:
        for gpu in gpus:
            tf.config.experimental.set_memory_growth(gpu, True)
