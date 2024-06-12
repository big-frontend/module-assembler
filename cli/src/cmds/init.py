"""
项目工程初始化
"""
from argparse import ArgumentParser

from fast import BaseCommand
import zipfile
import os
from fast import log
from cmds.util import download_file

template_project_url = 'https://github.com/JamesfChen/bundles-assembler-template/archive/refs/heads/main.zip'


class Init(BaseCommand):
    def _create_parser(self, p):
        pyadb_parser = p.add_parser('init')
        pyadb_parser.add_argument('-p', '--package', dest='package', default='', type=str,
                                  help='项目包名')
        pyadb_parser.add_argument('-n', '--name', dest='name', default='', type=str,
                                  help='项目名字')

        return pyadb_parser

    def _parse_args(self, args: ArgumentParser):
        self.__pkg = args.package
        self.__name = args.name
        pass

    def _execute(self):
        if not self.__pkg:
            raise RuntimeError("不存在项目包名")
        if not self.__name:
            raise RuntimeError("不存在项目名")
            pass
        directory_to_extract_to = os.getcwd()
        if os.path.exists(os.path.join(directory_to_extract_to, self.__name)):
            log.info("已经存在项目")
            return
        local_filename = download_file(template_project_url)
        with zipfile.ZipFile(local_filename, 'r') as zip_ref:
            zip_ref.extractall(directory_to_extract_to)
            os.rename('bundles-assembler-template-main', self.__name)
