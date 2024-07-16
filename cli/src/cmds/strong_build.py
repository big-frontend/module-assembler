# GIT_PREVIOUS_SUCCESSFUL_COMMIT
# GIT_COMMIT
# git diff $GIT_COMMIT $GIT_PREVIOUS_SUCCESSFUL_COMMIT --name-status
# git diff ae80330a 425cd6d6 --name-only
# import collections
# import json
# import os
# import subprocess
# import sys
# from os import path as opath
# from subprocess import PIPE

# root_path = opath.dirname(opath.dirname(__file__))
# module_config_path = opath.join(root_path, 'module_config.json')
# all_modules = dict()
# with open(module_config_path, encoding='utf-8') as f:
#     module_config = json.load(f)
#     for m in module_config['allModules']:
#         project_dir = ''
#         if m.get("projectDir"):
#             project_dir = m["projectDir"]
#         else:
#             project_dir = f"xxxx目录/{m['sourcePath'].split(':')[1]}"
#         all_modules[project_dir] = m

# # test case
# os.environ['GIT_COMMIT'] = '425cd6d6'
# os.environ['GIT_PREVIOUS_SUCCESSFUL_COMMIT'] = 'ae80330a'


# def picker(modules, path):
#     for project_dir, m in modules.items():
#         words = ''
#         q = collections.deque()
#         for w in project_dir:
#             if w == '.': continue
#             if w != '/':
#                 words += w
#             if w == '/' and words:
#                 q.append(words)
#                 words = ''
#         q.append(words)
#         for f in path.split('/'):
#             if f == q[0]:
#                 q.popleft()
#                 if not q: return m["simpleName"]
#     return None


# if __name__ == '__main__':
#     cmd_list = f"git diff {os.environ['GIT_PREVIOUS_SUCCESSFUL_COMMIT']} {os.environ['GIT_COMMIT']}  --name-only"
#     completedProcess = subprocess.run(cmd_list, shell=True, stdout=PIPE, stderr=PIPE)
#     returncode = completedProcess.returncode
#     if returncode == 0:
#         ret = completedProcess.stdout.decode('utf-8').strip()
#         ret = ret.split() if ret else ''
#         source_modules = set()
#         for path in ret:
#             simple_name = picker(all_modules, path)
#             if simple_name: source_modules.add(simple_name)
#         s_list = ''
#         for s in source_modules:
#             s_list += f'-s {s} '
#         print(source_modules)
#         subprocess.run(
#             "python  module_manager.py -v devPhoneDebug -fp=binary -dbp=exclude -sbp=binary",
#             shell=True)
#         subprocess.run(f"python  module_manager.py {s_list}", shell=True)

#     else:
#         sys.stderr.write(f"returnCode={returncode}")
