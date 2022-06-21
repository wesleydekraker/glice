import os

from sklearn.model_selection import KFold, train_test_split


def split(data_dir: str, split_file: str = "split.txt"):
    if os.path.exists(split_file):
        print("Using existing split file.")
        return

    filenames = os.listdir(data_dir)
    bad_filenames = []
    good_filenames = []

    for filename in filenames:
        part = filename.replace(".txt", "")
        label = part.split("-")[-1]

        if label == "bad":
            bad_filenames.append(filename)
        else:
            good_filenames.append(filename)

    if len(bad_filenames) > len(good_filenames):
        ignored_count = len(bad_filenames) - len(good_filenames)
        print(f"Ignoring {ignored_count} vulnerable graphs.")
        bad_filenames = bad_filenames[:len(good_filenames)]
    elif len(good_filenames) > len(bad_filenames):
        ignored_count = len(good_filenames) - len(bad_filenames)
        print(f"Ignoring {ignored_count} non-vulnerable graphs.")
        good_filenames = good_filenames[:len(bad_filenames)]

    with open(split_file, "w") as f:
        for index, (bad_split, good_split) in enumerate(zip(_split_subset(bad_filenames), _split_subset(good_filenames))):
            for fold in bad_split:
                for filename in bad_split[fold] + good_split[fold]:
                    f.write(f"{index}:{fold}:{filename}\n")


def _split_subset(filenames):
    n_splits = 10
    k_fold = KFold(n_splits=10)

    for train_index, test_index in k_fold.split(filenames):
        train_index, valid_index = train_test_split(train_index, test_size=1 / (n_splits - 1))

        train = [filenames[i] for i in train_index]
        valid = [filenames[i] for i in valid_index]
        test = [filenames[i] for i in test_index]

        yield {"train": train, "valid": valid, "test": test}
