from urllib.parse import urlparse
from importlib import import_module
import sys
import os


class Singleton(type):
    __inst = {}

    def __call__(cls, *args, **kwargs):
        if cls not in cls.__inst:
            cls.__inst[cls] = super(Singleton, cls).__call__(*args)
        return cls.__inst[cls]


class DbClient:
    __metaclass__ = Singleton

    def __init__(self, db_conn):
        self.db_conn = db_conn
        self.parse_dbConn(db_conn)
        self.__init_dbclinet()

    @classmethod
    def parse_dbConn(cls, db_conn):
        db_conf = urlparse(db_conn)
        cls.db_type = db_conf.scheme.lower().strip()
        cls.db_host = db_conf.hostname
        cls.db_port = db_conf.port
        cls.db_user = db_conf.username
        cls.db_pwd = db_conf.password
        cls.db_name = db_conf.path[1:]
        return cls

    def __init_dbclinet(self):
        py_filename = '%s_op' % self.db_type
        # assert py_filename,'py_filename error,Not support db type:{}'.format(self.db_type)
        module = __import__(py_filename)
        # module = import_module(py_filename, package='db')
        clsn_name = '%sClient' % self.db_type.capitalize()
        self.client = getattr(module, clsn_name)(host=self.db_host,
                                                 port=self.db_port,
                                                 username=self.db_user,
                                                 password=self.db_pwd,
                                                 db=self.db_name)

    def get(self, **kwargs):
        return self.client.get(**kwargs)

    def put(self, key, **kwargs):
        return self.client.put(key, **kwargs)

    def update(self, key, value, **kwargs):
        return self.client.update(key, value, **kwargs)

    def delete(self, key, **kwargs):
        return self.client.delete(key, **kwargs)

    def exists(self, key, **kwargs):
        return self.client.exists(key, **kwargs)

    def pop(self, **kwargs):
        return self.client.pop(**kwargs)

    def getAll(self):
        return self.client.getAll()

    def clear(self):
        return self.client.clear()

    def changeTable(self, name):
        self.client.changeTable(name)

    def getCount(self):
        return self.client.getCount()


sys.path.append(os.path.dirname(os.path.abspath(__file__)))


def main():
    db_conn = 'redis://:pwd@127.0.0.1:6379/0'
    inst = DbClient(db_conn)
    inst.get()


if __name__ == '__main__':
    main()
