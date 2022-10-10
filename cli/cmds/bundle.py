from fwk import BaseCommand


class Bundle(BaseCommand):
    def _create_parser(self, p):
        pyadb_parser = p.add_parser('bundle')
        pyadb_parser.add_argument('-p', '--package', dest='package', default='', type=str,
                                  help='项目包名')
        pyadb_parser.add_argument('-n', '--name', dest='name', default='', type=str,
                                  help='项目名字')

        return pyadb_parser

    def _parse_args(self, args: "ArgumentParser"):
        self.__basic = args.basic

    def _execute(self):
        pass
