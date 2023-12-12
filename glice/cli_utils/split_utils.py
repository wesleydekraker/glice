import json
import os

from sklearn.model_selection import train_test_split, StratifiedKFold

from glice import DataFold
from glice.data.joern_dataset import get_label, get_language


def split(data_dir: str, split_file: str = "split.txt"):
    if os.path.exists(split_file):
        print("Using existing split file.")
        return

    filenames = os.listdir(data_dir)
    labels = []

    for filename in filenames:
        with open(os.path.join(data_dir, filename), "r") as f:
            data = json.load(f)

        label = "{}:{}:{}".format(data["label"], data["cwe"], get_language(data["filePath"]))
        labels.append(label)

    with open(split_file, "w") as f:
        for index, folds in enumerate(_split_subset(filenames, labels)):
            for fold in folds:
                for filename in folds[fold]:
                    f.write(f"{index}:{fold}:{filename}\n")


def _split_subset(filenames, labels):
    n_splits = 10
    k_fold = StratifiedKFold(n_splits=n_splits, shuffle=True, random_state=42)

    for train_index, test_index in k_fold.split(filenames, labels):
        train_labels = [labels[i] for i in train_index]
        train_index, valid_index = train_test_split(train_index, test_size=1 / (n_splits - 1), stratify=train_labels)

        train = [filenames[i] for i in train_index]
        valid = [filenames[i] for i in valid_index]
        test = [filenames[i] for i in test_index]

        yield {DataFold.TRAIN.name: train, DataFold.VALIDATION.name: valid, DataFold.TEST.name: test}
