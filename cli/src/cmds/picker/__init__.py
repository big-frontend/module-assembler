from argparse import ArgumentParser

from fast import BaseCommand
import json
from cmds import util
from cmds.picker.policy import Policy

class Picker(BaseCommand):
    """
    模块管理

    python  picker -fp=source -dbp=exclude -sbp=exclude
    python  picker -v all -e a -e b

    """

    def _create_parser(self, p):
        parser = p.add_parser('picker')
        parser.add_argument('--version', action='version', version='1.0.0')
        parser.add_argument('-v', '--activeBuildVariant', dest='active_build_variant',
                            default='', type=str,
                            help='变体')
        parser.add_argument('-s', '--source', dest='source_modules', action="append",
                            default=[],
                            help='以源码参与构建的模块')
        parser.add_argument('-e', '--exclude', dest='exclude_modules', action="append",
                            default=[],
                            help='以二进制包参与构建的模块')
        parser.add_argument('-b', '--binary', dest='binary_modules', action="append",
                            default=[],
                            help='不参与构建的模块')
        parser.add_argument('-fp', '--fwk_policy', dest='fwk_policy',
                            choices=[Policy.BINARY.name.lower(), Policy.SOURCE.name.lower()],
                            default=Policy.NONE.name.lower(),
                            help='fwk选择源码集成 or 二进制包集成')
        parser.add_argument('-sbp', '--static_bundle_policy', dest='static_bundle_policy',
                            choices=[Policy.BINARY.name.lower(), Policy.SOURCE.name.lower(),
                                     Policy.EXCLUDE.name.lower()],
                            default=Policy.NONE.name.lower(),
                            help='static bundle源码集成 or 二进制包集成 or 不参与集成')
        parser.add_argument('-dbp', '--dynamic_bundle_policy', dest='dynamic_bundle_policy',
                            choices=[Policy.BINARY.name.lower(), Policy.SOURCE.name.lower(),
                                     Policy.EXCLUDE.name.lower()],
                            default=Policy.NONE.name.lower(),
                            help='dynamic bundle源码集成 or 二进制包集成 or 不参与集成')
        return parser

    def _parse_args(self, args: ArgumentParser):
        pass
    def _execute(self):
        pass