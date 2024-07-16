
from argparse import ArgumentParser

from fast import BaseCommand
import json
from cmds import util
from enum import Enum, unique
from cmds.picker import Properties


class Picker(BaseCommand):
    """
    模块管理

    python  script/module_manager.py -fp=source -dbp=exclude -sbp=exclude
    python  script/module_manager.py -v all -e a -e b

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
        self.__args = args
        self.__local_properties_path = util.get_local_properties_path()
        self.__p = Properties()
        self.__p.load(open(self.__local_properties_path,encoding='utf-8'))
        self.__fwk_modules = dict()
        self.__nsbundle_modules = dict()
        self.__ndbundle_modules = dict()
        with open(util.get_module_config_path(), encoding='utf-8') as f:
            module_config = json.load(f)
            for m in module_config['allModules']:
                if m['group'] == 'fast':
                    self.__fwk_modules[m["simpleName"]] = m
                elif not m.get('dynamic'):
                    self.__nsbundle_modules[m["simpleName"]] = m
                elif m['dynamic']:
                    self.__ndbundle_modules[m["simpleName"]] = m

    def _execute(self):
        args = self.__args
        print(args)
        if args.active_build_variant:
            self.__p['activeBuildVariant'] = args.active_build_variant

        if not self.__p['activeBuildVariant']:
            self.__p['activeBuildVariant'] = 'all'
        # includeAll
        if not self.__p.hasProperty('sourceModules'):
            for k in self.__fwk_modules.keys():
                self.__p['sourceModules'] += f'{k},'
            for k in self.__nsbundle_modules.keys():
                self.__p['sourceModules'] += f'{k},'
            for k in self.__ndbundle_modules.keys():
                self.__p['excludeModules'] += f'{k},'
        #     self.__p.store(open(local_properties_path, 'w'))
        # if not self.__p['excludeModules']:
        #     pass

        if self.__p['activeBuildVariant'] == 'all' and args.binary_modules:
            raise RuntimeError("all 模式下，不支持组件化,请将所有binary模块转换成source 或者 exclude")

        if args.source_modules: self.sourceify(args.source_modules)
        if args.exclude_modules: self.execludeify(args.exclude_modules)
        if args.binary_modules: self.binaryify(args.binary_modules)
        if args.fwk_policy == Policy.BINARY.name.lower():
            self.binaryify(self.__fwk_modules.keys())
        elif args.fwk_policy == Policy.SOURCE.name.lower():
            self.sourceify(self.__fwk_modules.keys())

        if args.static_bundle_policy == Policy.BINARY.name.lower():
            self.binaryify(self.__nsbundle_modules.keys())
        elif args.static_bundle_policy == Policy.SOURCE.name.lower():
            self.sourceify(self.__nsbundle_modules.keys())
        elif args.static_bundle_policy == Policy.EXCLUDE.name.lower():
            a = []
            home_module = []
            for _, m in self.__nsbundle_modules.items():
                if m.get("group") != 'home':
                    a.append(m['simpleName'])
                else:
                    home_module.append(m['simpleName'])
            self.execludeify(a)
            self.binaryify(home_module)

        if args.dynamic_bundle_policy == Policy.BINARY.name.lower():
            self.binaryify(self.__ndbundle_modules.keys())
        elif args.dynamic_bundle_policy == Policy.SOURCE.name.lower():
            self.sourceify(self.__ndbundle_modules.keys())
        elif args.dynamic_bundle_policy == Policy.EXCLUDE.name.lower():
            self.execludeify(self.__ndbundle_modules.keys())
        self.__p.store(open(self.__local_properties_path, 'w', encoding='utf-8'))

    def duplicate(self, sources, excludes, binaries):
        # 重复就抛出异常
        pass

    def remove_module(self, property_name, m):
        n = ''
        for e in self.__p[property_name].split(','):
            if e and e != m:
                n += f'{e},'
        self.__p[property_name] = n

    def sourceify(self, modules):
        source_modules = [e for e in self.__p['sourceModules'].split(",") if e]
        for s in modules:
            if s not in source_modules:
                self.__p['sourceModules'] += f'{s},'

            self.remove_module('excludeModules', s)

    def execludeify(self, modules):
        exclude_modules = [e for e in self.__p['excludeModules'].split(",") if e]
        for e in modules:
            if e not in exclude_modules:
                self.__p['excludeModules'] += f'{e},'

            self.remove_module('sourceModules', e)

    def binaryify(self, modules):
        source_modules = [e for e in self.__p['sourceModules'].split(",") if e]
        exclude_modules = [e for e in self.__p['excludeModules'].split(",") if e]
        for b in modules:
            if b in source_modules:
                self.remove_module('sourceModules', b)

            if b in exclude_modules:
                self.remove_module('excludeModules', b)


@unique
class Policy(Enum):
    NONE = 0
    SOURCE = 1
    EXCLUDE = 2
    BINARY = 3
