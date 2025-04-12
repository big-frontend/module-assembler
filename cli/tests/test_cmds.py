
import pytest

from cmds import util
from cmds.picker.property import Properties


class Fruit:
    def __init__(self, name):
        self.name = name

    def __eq__(self, other):
        return self.name == other.name


@pytest.fixture
def my_fruit():
    return Fruit("apple")


@pytest.fixture
def fruit_basket(my_fruit):
    return [Fruit("banana"), my_fruit]


def test_my_fruit_in_basket(my_fruit, fruit_basket):
    __local_properties_path = util.get_local_properties_path()
    __p = Properties()
    if __local_properties_path:
        __p.load(path=__local_properties_path)
    assert my_fruit in fruit_basket

