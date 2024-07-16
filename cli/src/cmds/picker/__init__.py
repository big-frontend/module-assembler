from argparse import ArgumentParser

from fast import BaseCommand
import json
from cmds import util



class Picker(BaseCommand):
    """
    模块管理

    python  picker -fp=source -dbp=exclude -sbp=exclude
    python  picker -v all -e a -e b

    """

    def _create_parser(self, p):
        pass
    def _parse_args(self, args: ArgumentParser):
        pass
    def _execute(self):
        pass