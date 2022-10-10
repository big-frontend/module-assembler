from fwk import BaseCommand
import sys
import requests
import zipfile
import os
from fwk import log
from tqdm import tqdm
template_project_url = 'https://github.com/JamesfChen/bundles-assembler-template/archive/refs/heads/main.zip'


def download_file(url):
    local_filename = url.split('/')[-1]
    with requests.get(url, stream=True, verify=False) as rep:
        rep.raise_for_status()
        total_size_in_bytes = int(rep.headers.get('content-length', 0))
        progress_bar = tqdm(total=total_size_in_bytes, ncols=80,unit='iB',
                            unit_scale=True, desc='正在下载项目 pid:' + str(os.getpid()))
        with open(local_filename, 'wb') as f:
            for chunk in rep.iter_content(chunk_size=8192):
                progress_bar.update(len(chunk))
                f.write(chunk)
        progress_bar.close()
        if total_size_in_bytes != 0 and progress_bar.n != total_size_in_bytes:
            print("ERROR, something went wrong")
    return local_filename


class Init(BaseCommand):
    def _create_parser(self, p):
        pyadb_parser = p.add_parser('init')
        pyadb_parser.add_argument('-p', '--package', dest='package', default='', type=str,
                                  help='项目包名')
        pyadb_parser.add_argument('-n', '--name', dest='name', default='', type=str,
                                  help='项目名字')

        return pyadb_parser

    def _parse_args(self, args: "ArgumentParser"):
        self.__pkg = args.package
        self.__name = args.name
        pass

    def _execute(self):
        if not self.__pkg:
            raise BaseException("不存在项目包名")
        if not self.__name:
            raise BaseException("不存在项目名")
            pass
        directory_to_extract_to = os.getcwd()
        if os.path.exists(os.path.join(directory_to_extract_to, self.__name)):
            log.info("已经存在项目")
            return
        local_filename = download_file(template_project_url)
        with zipfile.ZipFile(local_filename, 'r') as zip_ref:
            zip_ref.extractall(directory_to_extract_to)
            os.rename('bundles-assembler-template-main', self.__name)
