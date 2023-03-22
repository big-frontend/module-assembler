import argparse
import json
import re
import sys
import time
from enum import Enum, unique
from io import TextIOWrapper
from os import path as opath
import subprocess,platform

def main() -> None:
    parser = argparse.ArgumentParser(
        usage="fast build",
        description=__doc__,
    )
    parser.add_argument('--version', action='version', version='1.0.0')
    parser.add_argument('-env', '--active-server-api-env', dest='active_server_api_env',default='', type=str,
                        help='服务api环境 eg. fast -env=test')
    parser.add_argument('-v', '--active-build-variant', dest='active_build_variant',default='', type=str,
                        help='变体 eg. fast -v=debug')
    parser.add_argument('-sm', '--source-module', dest='source_modules', action="append",default=[],
                        help='以源码参与构建的模块 eg. fast -sm=flight-module -sm=hotel-module')
    parser.add_argument('-em', '--exclude-module', dest='exclude_modules', action="append",default=[],
                        help='以二进制包参与构建的模块 eg. fast -em=flight-module -em=hotel-module')
    parser.add_argument('-bm', '--binary-module', dest='binary_modules', action="append",default=[],
                        help='不参与构建的模块 eg. fast -bm=flight-module -bm=hotel-module')
    parser.add_argument('--ndbify-modules', dest='ndb_modules', action="append",default=[],
                        help='native static bundle 转成 native dynamic  bundle')
    parser.add_argument('-fp', '--fwk-policy', dest='fwk_policy',
                        choices=[Policy.BINARY.name.lower(), Policy.SOURCE.name.lower()],
                        default=Policy.NONE.name.lower(),
                        help='fwk选择源码集成 or 二进制包集成 eg. fast -fp=source')
    parser.add_argument('-sbp', '--static-bundle-policy', dest='static_bundle_policy',
                        choices=[Policy.BINARY.name.lower(), Policy.SOURCE.name.lower(),
                                 Policy.EXCLUDE.name.lower()],
                        default=Policy.NONE.name.lower(),
                        help='static bundle源码集成 or 二进制包集成 or 不参与集成 eg. fast -sbp=exclude')
    parser.add_argument('-dbp', '--dynamic-bundle-policy', dest='dynamic_bundle_policy',
                        choices=[Policy.BINARY.name.lower(), Policy.SOURCE.name.lower(),
                                 Policy.EXCLUDE.name.lower()],
                        default=Policy.NONE.name.lower(),
                        help='dynamic bundle源码集成 or 二进制包集成 or 不参与集成 eg. fast -dbp=exclude')
    parser.add_argument('--task', dest='gradle_task', default='', type=str,required =True,
                        help='gradle 执行的任务 eg. fast --task=app:debug')
    parser.add_argument('--task-arg', dest='gradle_task_args',  action="append",default=[],
                        help='gradle 执行的任务的参数  eg. fast --task=app:debug --task_arg=name:xiaoming')

    parser.set_defaults(func=parse_args)
    parser.parse_args()
    args = parser.parse_args()
    args.func(args)

def parse_args(args):
    # 1.读取配置文件数据
    root_path = opath.dirname(opath.dirname(__file__))
    local_properties_path = opath.join(root_path, 'local.properties')
    module_config_path = opath.join(root_path, 'module_config.json')
    local_props = Properties()
    local_props.load(open(local_properties_path,encoding='utf-8'))
    fwk_modules = dict()
    nsbundle_modules = dict()
    ndbundle_modules = dict()
    with open(module_config_path, encoding='utf-8') as f:
        module_config = json.load(f)
        for m in module_config['allModules']:
            if m['group'] == 'fwk':
                fwk_modules[m["simpleName"]] = m
            elif not m.get('dynamic'):
                nsbundle_modules[m["simpleName"]] = m
            elif m['dynamic']:
                ndbundle_modules[m["simpleName"]] = m

    print(args)
    # 2.构建环境预配置
    if args.active_build_variant:
        local_props['activeBuildVariant'] = args.active_build_variant

    if args.active_server_api_env:
        local_props['activeServerApiEnv'] = args.active_server_api_env

    if not local_props['activeServerApiEnv']:
        local_props['activeServerApiEnv'] = 'production'

    if not local_props['activeBuildVariant']:
        local_props['activeBuildVariant'] = 'all'
    #includeAll
    if not local_props.hasProperty('sourceModules'):
        for k in fwk_modules.keys():
            local_props['sourceModules'] +=f'{k},'
        for k in nsbundle_modules.keys():
            local_props['sourceModules'] +=f'{k},'
        for k in ndbundle_modules.keys():
            local_props['excludeModules'] +=f'{k},'

    if local_props['activeBuildVariant'] == 'all' and args.binary_modules:
        raise BaseException("all 模式下，不支持组件化,请将所有binary模块转换成source 或者 exclude")

    if args.source_modules: sourceify(local_props,args.source_modules)
    if args.exclude_modules: execludeify(local_props,args.exclude_modules)
    if args.binary_modules: binaryify(local_props,args.binary_modules)
    if args.ndb_modules: ndbify(module_config_path,module_config,args.ndb_modules)
    if args.fwk_policy == Policy.BINARY.name.lower():
        binaryify(local_props,fwk_modules.keys())
    elif args.fwk_policy == Policy.SOURCE.name.lower():
        sourceify(local_props,fwk_modules.keys())

    if args.static_bundle_policy == Policy.BINARY.name.lower():
        binaryify(local_props,nsbundle_modules.keys())
    elif args.static_bundle_policy == Policy.SOURCE.name.lower():
        sourceify(local_props,nsbundle_modules.keys())
    elif args.static_bundle_policy == Policy.EXCLUDE.name.lower():
        a = []
        home_module = []
        for _, m in nsbundle_modules.items():
            if m.get("group") != 'home':
                a.append(m['simpleName'])
            else:
                home_module.append(m['simpleName'])
        execludeify(local_props,a)
        binaryify(local_props,home_module)

    if args.dynamic_bundle_policy == Policy.BINARY.name.lower():
        binaryify(local_props,ndbundle_modules.keys())
    elif args.dynamic_bundle_policy == Policy.SOURCE.name.lower():
        sourceify(local_props,ndbundle_modules.keys())
    elif args.dynamic_bundle_policy == Policy.EXCLUDE.name.lower():
        execludeify(local_props,ndbundle_modules.keys())
    local_props.store(open(local_properties_path, 'w',encoding='utf-8'))

    # 3.执行gradle任务
    is_windows = "Windows" in platform.system()
    cmd_list = 'gradlew.bat ' if is_windows else 'gradle '
    cmd_list += args.gradle_task +' '
    for arg in args.gradle_task_args:
       cmd_list += f'-{arg} '
    print(cmd_list)
    subprocess.run(cmd_list,shell=True)

def duplicate(sources, excludes, binaries):
    # 重复就抛出异常
    pass


def remove_module(p, property_name, m):
    n = ''
    for e in p[property_name].split(','):
        if e and e != m:
            n += f'{e},'
    p[property_name] = n


def sourceify(p,modules):
    source_modules = [e for e in p['sourceModules'].split(",") if e]
    for s in modules:
        if s not in source_modules:
            p['sourceModules'] += f'{s},'
        remove_module(p, 'excludeModules', s)


def execludeify(p,modules):
    exclude_modules = [e for e in p['excludeModules'].split(",") if e]
    for e in modules:
        if e not in exclude_modules:
            p['excludeModules'] += f'{e},'

        remove_module(p, 'sourceModules', e)


def binaryify(p,modules):
    source_modules = [e for e in p['sourceModules'].split(",") if e]
    exclude_modules = [e for e in p['excludeModules'].split(",") if e]
    for b in modules:
        if b in source_modules:
            remove_module(p, 'sourceModules', b)

        if b in exclude_modules:
            remove_module(p, 'excludeModules', b)

def ndbify(module_config_path,module_config,modules):
    for m in module_config['allModules']:
        if m['simpleName'] in modules:
            m['format'] = 'ndbundle'
            m['dynamic'] = 'local-plugin'
    with open(module_config_path,'w', encoding='utf-8') as f:
        f.write(json.dumps(module_config,indent =2,ensure_ascii =False))

@unique
class Policy(Enum):
    NONE = 0
    SOURCE = 1
    EXCLUDE = 2
    BINARY = 3

class IllegalArgumentException(Exception):

    def __init__(self, lineno, msg):
        self.lineno = lineno
        self.msg = msg

    def __str__(self):
        s = 'Exception at line number %d => %s' % (self.lineno, self.msg)
        return s


class Properties(object):
    """ A Python replacement for java.util.Properties """

    def __init__(self, props=None):

        # Note: We don't take a default properties object
        # as argument yet

        # Dictionary of properties.
        self._props = {}
        # Dictionary of properties with 'pristine' keys
        # This is used for dumping the properties to a file
        # using the 'store' method
        self._origprops = {}

        # Dictionary mapping keys from property
        # dictionary to pristine dictionary
        self._keymap = {}

        self.othercharre = re.compile(r'(?<!\\)(\s*\=)|(?<!\\)(\s*\:)')
        self.othercharre2 = re.compile(r'(\s*\=)|(\s*\:)')
        self.bspacere = re.compile(r'\\(?!\s$)')

    def __str__(self):
        s = '{'
        for key, value in self._props.items():
            s = ''.join((s, key, '=', value, ', '))

        s = ''.join((s[:-2], '}'))
        return s

    def __parse(self, lines):
        """ Parse a list of lines and create
        an internal property dictionary """

        # Every line in the file must consist of either a comment
        # or a key-value pair. A key-value pair is a line consisting
        # of a key which is a combination of non-white space characters
        # The separator character between key-value pairs is a '=',
        # ':' or a whitespace character not including the newline.
        # If the '=' or ':' characters are found, in the line, even
        # keys containing whitespace chars are allowed.

        # A line with only a key according to the rules above is also
        # fine. In such case, the value is considered as the empty string.
        # In order to include characters '=' or ':' in a key or value,
        # they have to be properly escaped using the backslash character.

        # Some examples of valid key-value pairs:
        #
        # key     value
        # key=value
        # key:value
        # key     value1,value2,value3
        # key     value1,value2,value3 \
        #         value4, value5
        # key
        # This key= this value
        # key = value1 value2 value3

        # Any line that starts with a '#' is considerered a comment
        # and skipped. Also any trailing or preceding whitespaces
        # are removed from the key/value.

        # This is a line parser. It parses the
        # contents like by line.

        lineno = 0
        i = iter(lines)

        for line in i:
            lineno += 1
            line = line.strip()
            # Skip null lines
            if not line: continue
            # Skip lines which are comments
            if line[0] == '#': continue
            # Some flags
            escaped = False
            # Position of first separation char
            sepidx = -1
            # A flag for performing wspace re check
            flag = 0
            # Check for valid space separation
            # First obtain the max index to which we
            # can search.
            m = self.othercharre.search(line)
            if m:
                first, last = m.span()
                start, end = 0, first
                flag = 1
                wspacere = re.compile(r'(?<![\\\=\:])(\s)')
            else:
                if self.othercharre2.search(line):
                    # Check if either '=' or ':' is present
                    # in the line. If they are then it means
                    # they are preceded by a backslash.

                    # This means, we need to modify the
                    # wspacere a bit, not to look for
                    # : or = characters.
                    wspacere = re.compile(r'(?<![\\])(\s)')
                start, end = 0, len(line)

            m2 = wspacere.search(line, start, end)
            if m2:
                # print 'Space match=>',line
                # Means we need to split by space.
                first, last = m2.span()
                sepidx = first
            elif m:
                # print 'Other match=>',line
                # No matching wspace char found, need
                # to split by either '=' or ':'
                first, last = m.span()
                sepidx = last - 1
                # print line[sepidx]

            # If the last character is a backslash
            # it has to be preceded by a space in which
            # case the next line is read as part of the
            # same property
            while line[-1] == '\\':
                # Read next line
                nextline = i.next()
                nextline = nextline.strip()
                lineno += 1
                # This line will become part of the value
                line = line[:-1] + nextline

            # Now split to key,value according to separation char
            if sepidx != -1:
                key, value = line[:sepidx], line[sepidx + 1:]
            else:
                key, value = line, ''

            self.processPair(key, value)

    def processPair(self, key, value):
        """ Process a (key, value) pair """

        oldkey = key
        oldvalue = value

        # Create key intelligently
        keyparts = self.bspacere.split(key)
        # print keyparts

        strippable = False
        lastpart = keyparts[-1]

        if lastpart.find('\\ ') != -1:
            keyparts[-1] = lastpart.replace('\\', '')

        # If no backspace is found at the end, but empty
        # space is found, strip it
        elif lastpart and lastpart[-1] == ' ':
            strippable = True

        key = ''.join(keyparts)
        if strippable:
            key = key.strip()
            oldkey = oldkey.strip()

        oldvalue = self.unescape(oldvalue)
        value = self.unescape(value)

        self._props[key] = value.strip()

        # Check if an entry exists in pristine keys
        if key in self._keymap:
            oldkey = self._keymap.get(key)
            self._origprops[oldkey] = oldvalue.strip()
        else:
            self._origprops[oldkey] = oldvalue.strip()
            # Store entry in keymap
            self._keymap[key] = oldkey

    def escape(self, value):

        # Java escapes the '=' and ':' in the value
        # string with backslashes in the store method.
        # So let us do the same.
        newvalue = value.replace(':', '\:')
        newvalue = newvalue.replace('=', '\=')

        return newvalue

    def unescape(self, value):

        # Reverse of escape
        newvalue = value.replace('\:', ':')
        newvalue = newvalue.replace('\=', '=')

        return newvalue

    def load(self, stream):
        """ Load properties from an open file stream """

        # For the time being only accept file input streams
        if type(stream) is not TextIOWrapper:
            raise TypeError('Argument should be a file object!')
        # Check for the opened mode
        if stream.mode != 'r':
            raise ValueError('Stream should be opened in read-only mode!')

        try:
            lines = stream.readlines()
            self.__parse(lines)
        except IOError as e:
            raise
    def hasProperty(self, key):
        return key in self._props
    def getProperty(self, key):
        """ Return a property for the given key """

        return self._props.get(key, '')

    def setProperty(self, key, value):
        """ Set the property for the given key """

        if type(key) is str and type(value) is str:
            self.processPair(key, value)
        else:
            raise TypeError('both key and value should be strings!')

    def propertyNames(self):
        """ Return an iterator over all the keys of the property
        dictionary, i.e the names of the properties """

        return self._props.keys()

    def list(self, out=sys.stdout):
        """ Prints a listing of the properties to the
        stream 'out' which defaults to the standard output """

        out.write('-- listing properties --\n')
        for key, value in self._props.items():
            out.write(''.join((key, '=', value, '\n')))

    def store(self, out, header=""):
        """ Write the properties list to the stream 'out' along
        with the optional 'header' """

        if out.mode[0] != 'w':
            raise ValueError('Steam should be opened in write mode!')

        try:
            out.write(''.join(('#', header, '\n')))
            # Write timestamp
            tstamp = time.strftime('%a %b %d %H:%M:%S %Z %Y', time.localtime())
            out.write(''.join(('#', tstamp, '\n')))
            # Write properties from the pristine dictionary
            for prop, val in self._origprops.items():
                out.write(''.join((prop, '=', self.escape(val), '\n')))

            out.close()
        except IOError as e:
            raise

    def getPropertyDict(self):
        return self._props

    def __getitem__(self, name):
        """ To support direct dictionary like access """

        return self.getProperty(name)

    def __setitem__(self, name, value):
        """ To support direct dictionary like access """

        self.setProperty(name, value)

    def __getattr__(self, name):
        """ For attributes not found in self, redirect
        to the properties dictionary """

        try:
            return self.__dict__[name]
        except KeyError:
            if hasattr(self._props, name):
                return getattr(self._props, name)
if __name__ == "__main__":
    try:
        main()
    except KeyboardInterrupt:
        pass