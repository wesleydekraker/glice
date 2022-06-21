from glice.cli.shared import setup_tensorflow
from glice.cli_utils import get_train_cli_arg_parser
from glice.cli_utils.hpo_utils import run_hpo_from_args


def run():
    parser = get_train_cli_arg_parser()
    args = parser.parse_args()

    setup_tensorflow()

    run_hpo_from_args(args)


if __name__ == "__main__":
    run()
