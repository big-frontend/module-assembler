from argparse import ArgumentParser

from fast import BaseCommand

class Build(BaseCommand):
    def _create_parser(self, p):
        parser = p.add_parser('build')
        return parser

    def _parse_args(self, args: ArgumentParser):
        pass

    def _execute(self):
        pass
