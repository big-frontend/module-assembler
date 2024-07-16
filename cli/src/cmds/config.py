from argparse import ArgumentParser

from fast import BaseCommand


class Config(BaseCommand):
    def _create_parser(self, p):
        pyadb_parser = p.add_parser('config')
        return pyadb_parser

    def _parse_args(self, args: ArgumentParser):
        pass

    def _execute(self):
        pass
        print("asfdasfsad")
