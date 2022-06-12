from redis import Redis, BlockingConnectionPool
import click
from random import choice
import json


class RedisClient:
    def __init__(self, **kwargs):
        self.name = ''
        kwargs.pop('username')
        self.__db = Redis(connection_pool=BlockingConnectionPool(decode_responses=True, **kwargs))

    def get(self):
        productids = self.__db.hkeys(self.name)
        productid = choice(productids) if productids else None
        if productid:
            value = self.__db.hget(self.name, productid)
            return json.loads(value)

    def put(self, scenic_obj):
        # self.__db.set(name,value,**kwargs)
        self.__db.hset(self.name, scenic_obj['productid'], json.dumps(scenic_obj, ensure_ascii=False))

    def getAll(self):
        return self.__db.hgetall(self.name)

    def getCount(self):
        return self.__db.hlen(self.name)

    def clear(self):
        return self.__db.delete(self.name)

    def changeTable(self, name):
        self.name = name


@click.command()
# @click.argument()
@click.option('--init', is_flag=True, help='init db')
def init_db_command(init=False):
    if not init:
        return
    # db = sqlite3.connect(os.path.join(os.getcwd(), 'app.sqlite'),detect_types=sqlite3.PARSE_DECLTYPES)
    # db.row_factory = sqlite3.Row
    # with open('schema.sql') as f:
    #     db.executescript(f.read())
    # click.echo("Initialized the database")


def main():
    init_db_command()


if __name__ == '__main__':
    main()
