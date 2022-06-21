"""Functions to convert from string parameters to their values."""
import json
from typing import List, Dict, Any
from distutils.util import strtobool


def to_bool(val) -> bool:
    """ Accepts a boolean or str value, and returns the boolean equivalent, converting if necessary """

    if type(val) == bool:
        return val
    else:
        return bool(strtobool(val))


def str_to_list_of_ints(val) -> List[int]:
    """Accepts a str or list, returns list of ints. Specifically useful when 
    num_hidden_units of a set of layers is specified as a list of ints"""
    if type(val) == list:
        return val
    else:

        return [int(v) for v in json.loads(val)]

