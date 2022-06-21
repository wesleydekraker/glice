import json
import os

from gensim.models import Word2Vec, KeyedVectors


class Token2Vec:
    def __init__(self, wv: KeyedVectors):
        self._wv = wv

    @staticmethod
    def load(data_dir: str, vocab_dir: str, vector_size: int, window: int):
        filename = Token2Vec._get_filename(vector_size, window)

        if not os.path.exists(filename):
            Token2Vec._create_word2vec(data_dir, vocab_dir, vector_size, window)

        wv = Word2Vec.load(filename).wv
        return Token2Vec(wv)

    @staticmethod
    def _create_word2vec(data_dir: str, vocab_dir: str, vector_size: int, window: int):
        print("Generating word2vec model...")

        sentences = []

        if os.path.exists(vocab_dir):
            directory = vocab_dir
            filenames = os.listdir(vocab_dir)
        else:
            directory = data_dir
            filenames = os.listdir(data_dir)

        for filename in filenames:
            filepath = os.path.join(directory, filename)

            with open(filepath, 'r') as f:
                nodes = json.load(f)["nodes"]

            sentence = [Token2Vec._get_node_value(node) for node in nodes]
            sentences.append(sentence)

        sizes = [len(sentence) for sentence in sentences]
        average_nodes = sum(sizes) / len(sizes)
        print(f"Average nodes per graph: {average_nodes}.")

        model = Word2Vec(sentences=sentences, vector_size=vector_size, window=window, min_count=5, workers=8)
        filename = Token2Vec._get_filename(vector_size, window)
        model.save(filename)
        print("Created word2vec model.")

    def get_vector(self, node):
        try:
            return self._wv[self._get_node_value(node)]
        except KeyError:
            return self._wv[self._wv.index_to_key[0]]

    @staticmethod
    def _get_filename(vector_size: int, window: int):
        return f"word2vec_vs{vector_size}_w{window}.model"

    @staticmethod
    def _get_node_value(node):
        return node["nodeType"] + ":" + node["value"]
