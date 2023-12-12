import json
import os
import re

from fasttext import FastText
import fasttext


class Token2Vec:
    def __init__(self, model: FastText):
        self._model = model
        self._cache = {}

    @staticmethod
    def load(data_dir: str, vocab_dir: str, vector_size: int, window: int):
        filename = Token2Vec._get_filename(vector_size, window)

        if not os.path.exists(filename):
            Token2Vec._create_word2vec(data_dir, vocab_dir, vector_size, window)

        model = fasttext.load_model(filename)
        return Token2Vec(model)

    @staticmethod
    def _create_word2vec(data_dir: str, vocab_dir: str, vector_size: int, window: int):
        print("Generating fasttext model...")

        if os.path.exists(vocab_dir):
            directory = vocab_dir
            filenames = os.listdir(vocab_dir)
        else:
            directory = data_dir
            filenames = os.listdir(data_dir)

        with open('sentences.txt', 'w') as s:
            for filename in filenames:
                filepath = os.path.join(directory, filename)

                with open(filepath, 'r') as f:
                    nodes = json.load(f)["nodes"]

                sentence = " ".join([Token2Vec._get_node_value(node) for node in nodes])
                s.write(sentence + "\n")

        model = fasttext.train_unsupervised('sentences.txt', dim=vector_size)
        filename = Token2Vec._get_filename(vector_size, window)
        model.save_model(filename)
        print("Created word2vec model.")

    def get_vector(self, node):
        node_value = self._get_node_value(node)
        if node_value in self._cache:
            return self._cache[node_value]
        else:
            vector = self._model.get_word_vector(self._get_node_value(node))
            self._cache[node_value] = vector
            return vector

    @staticmethod
    def _get_filename(vector_size: int, window: int):
        return f"word2vec_vs{vector_size}_w{window}.bin"

    @staticmethod
    def _get_node_value(node):
        return node["nodeType"] + ":" + re.sub(r'\s', '-', node["value"])
