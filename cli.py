__version__ = "1.0.0"
__release_date__ = "15-Jun-2020"
import re
import argparse
import os
import subprocess
from importlib import import_module
import sys
from argparse import ArgumentParser
from abc import abstractmethod, ABC, abstractproperty

class BaseCommand(ABC):
    # _arg_parser = None
    # _subparsers = None
    _serial_no = ''
    _imeis = ''
    _brand = ''

    @staticmethod
    def create():
        parser = argparse.ArgumentParser(
            usage="command line",
            description=__doc__,
        )
        parser.add_argument('--version', action='version', version='1.0.0')
        parser.add_argument('-s', '--serial', dest='serial_no', default='',
                            help='use device with given serial')

        sps = parser.add_subparsers()
        return parser, sps

    @staticmethod
    def start(p):
        args = p.parse_args()
        args.func(args)

    def create_parser(self, p, sp) -> ArgumentParser:
        return self._create_parser(sp)

    @abstractmethod
    def _create_parser(self, p) -> ArgumentParser:
        pass

    def print_with_cmd(self, *content):
        s = ''
        for i in content:
            s = s+str(i)
        device_info = 's/{} imeis/{} b/{}'.format(
            self._serial_no, self._imeis, self._brand)
        log.info('[ {} ] {} >> {}'.format(self._subcmd_name, device_info, s))

    def parse_args(self, subparser, subcmd_name):
        subparser.set_defaults(func=self.___parse_args)
        self._subcmd_name = subcmd_name

    def ___parse_args(self, args):
        serial_no = args.serial_no
        if len(serial_no) == 0:
            devices = device.get_devices()
            device_size = len(devices)
            if device_size == 1:
                serial_no = devices[0]
            elif device_size >= 2:
                #                 raise BaseException("有多台需要指定设备 {}".format(devices))
                log.error("有多台需要指定设备 {}".format(devices))
                sys.exit(1)
            else:
                #                 raise BaseException("没有设备连接")
                log.error("没有设备连接")
                sys.exit(1)

        self._serial_no = serial_no
        self._imeis = device.get_imeis(serial_no)
        print(self._imeis)
        if len(self._imeis) == 0:
            log.error('no devices')
            sys.exit(1)
            pass
        self._brand = device.get_brand(self._serial_no)
        self._parse_args(args)
        # self.print_with_cmd('parse_args {}'.format(args))
        self.__execute()

    @abstractmethod
    def _parse_args(self, args):
        pass

    def __execute(self):
        self.print_with_cmd('execute doing ...')
        self._execute()

    @abstractmethod
    def _execute(self):
        pass


my_dir = os.path.dirname(__file__)


def all_commands():
    all_commands = {}
    for file in os.listdir(my_dir):
        if file == '__init__.py' or not file.endswith('.py'):
            continue
        py_filename = file[:-3]

        clsn = py_filename.capitalize()
        while clsn.find('_') > 0:
            h = clsn.index('_')
            clsn = clsn[0:h] + clsn[h + 1:].capitalize()
        module = import_module('.{}'.format(py_filename), package='pyadb.cmd')
        try:
            cmd = getattr(module, clsn)()
        except AttributeError as identifier:
            pass
            # raise SyntaxError('%s/%s does not define class %s' % (
            #                  __name__, file, clsn))
        name = py_filename.replace('_', '-')
        cmd.NAME = name
        all_commands[name] = cmd
    return all_commands
class Init(BaseCommand):
    def _create_parser(self, p):
        pyadb_parser = p.add_parser('init')
        pyadb_parser.add_argument('-b', '--basic', action='store_true',
                                  help='device basic info')
        pyadb_parser.add_argument('--top_activity', action='store_true',
                                  help='top activity')
        pyadb_parser.add_argument(
            '-i', '--imei', action='store_true', help='get imei')
        return pyadb_parser 
    def _parse_args(self, args: "ArgumentParser"):
        self.__basic = args.basic
    def _execute(self):
        pass
import requests
template_project_url = 'https://github.com/JamesfChen/bundles-assembler-template/archive/refs/heads/main.zip'
def download_file(url):
    local_filename = url.split('/')[-1]
    # NOTE the stream=True parameter below
    with requests.get(url, stream=True,verify=False) as r:
        r.raise_for_status()
        with open(local_filename, 'wb') as f:
            for chunk in r.iter_content(chunk_size=8192): 
                # If you have chunk encoded response uncomment if
                # and set chunk_size parameter to None.
                #if chunk: 
                f.write(chunk)
    return local_filename
import zipfile
import os
def main(arguments=None):
    local_filename = download_file(template_project_url)
    directory_to_extract_to = os.getcwd()
    with zipfile.ZipFile(local_filename, 'r') as zip_ref:
        zip_ref.extractall(directory_to_extract_to)
if __name__ == '__main__':
    main(sys.argv[1:])
