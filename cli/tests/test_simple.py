
import unittest

from cmds import util
from cmds.picker.property import Properties


class TestSimple(unittest.TestCase):

    def test_add_one(self):
        # self.assertEqual(add_one(5), 6)
        __local_properties_path = util.get_local_properties_path()
        __p = Properties()
        if __local_properties_path:
            __p.load(stream=open(__local_properties_path, encoding='utf-8'))


if __name__ == '__main__':
    unittest.main()