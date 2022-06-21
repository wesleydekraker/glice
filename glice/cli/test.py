from glice.cli.shared import setup_tensorflow
from glice.cli_utils.test_utils import get_test_cli_arg_parser, run_test_from_args


def run():
    parser = get_test_cli_arg_parser()
    args = parser.parse_args()

    setup_tensorflow()

    run_test_from_args(args)


if __name__ == "__main__":
    run()
