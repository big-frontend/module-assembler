from fwk import BaseCommand
from enum import Enum, unique

class Create(BaseCommand):
    def _create_parser(self, p):
        pyadb_parser = p.add_parser('create')
        pyadb_parser.add_argument('-p', '--package', dest='package', default='', type=str,
                                  help='模块包名')
        pyadb_parser.add_argument('-n', '--name', dest='path', default='', type=str,
                                  help='模块名字')
        pyadb_parser.add_argument(
            '--type',
            dest="type",
            choices=[
                TYPE.NONE.name.lower(),
                TYPE.FLUTTER.name.lower(),
                TYPE.REACTNATIVE.name.lower(),
                TYPE.HTML5.name.lower(),
                TYPE.NATIVE_STACTIC.name.lower(),
                TYPE.NATIVE_DYNIMAC.name.lower(),
            ],
            default=TYPE.NONE.name.lower(),
            help="模块类型"
        )
        return pyadb_parser

    def _parse_args(self, args: "ArgumentParser"):
        self.__basic = args.basic

    def _execute(self):
        pass


@unique
class TYPE(Enum):
    NONE = 0
    FLUTTER = 1
    REACTNATIVE = 2
    HTML5 = 3
    NATIVE_STACTIC = 4
    NATIVE_DYNIMAC = 5
