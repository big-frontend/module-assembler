from tqdm import tqdm
import requests

from os import path as opath
import os


def download_file(url):
    local_filename = url.split('/')[-1]
    with requests.get(url, stream=True, verify=False) as rep:
        rep.raise_for_status()
        total_size_in_bytes = int(rep.headers.get('content-length', 0))
        progress_bar = tqdm(total=total_size_in_bytes, ncols=80, unit='iB',
                            unit_scale=True, desc='正在下载项目 pid:' + str(os.getpid()))
        with open(local_filename, 'wb') as f:
            for chunk in rep.iter_content(chunk_size=8192):
                progress_bar.update(len(chunk))
                f.write(chunk)
        progress_bar.close()
        if total_size_in_bytes != 0 and progress_bar.n != total_size_in_bytes:
            print("ERROR, something went wrong")
    return local_filename


root_path = os.getcwd()


def get_local_properties_path():
    p = opath.join(root_path, 'local.properties')
    if opath.exists(p):
        return p
    # raise RuntimeError("当前目录下没有local.properties文件")
    return ''


def get_module_config_path():
    p = opath.join(root_path, 'module_config.json')
    if opath.exists(p):
        return p
    raise RuntimeError("当前目录下没有module_config.json文件")
