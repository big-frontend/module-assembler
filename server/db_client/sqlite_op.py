import sqlite3
import click
import os


class SqliteClient:
    def __init__(self, **kwargs):
        self.name = ''
        kwargs.pop('username')
        self.__conn = sqlite3.connect(os.path.join(os.getcwd(), 'app.sqlite'), detect_types=sqlite3.PARSE_DECLTYPES)

    def get(self):
        print('sqlite client')

    def put(self):
        pass


@click.command()
# @click.argument()
@click.option('--init', is_flag=True, help='init db')
def init_db_command(init=False):
    if not init:
        return
    db = sqlite3.connect(os.path.join(os.getcwd(), 'app.sqlite'), detect_types=sqlite3.PARSE_DECLTYPES)
    db.row_factory = sqlite3.Row
    with open('schema.sql') as f:
        db.executescript(f.read())
    click.echo("Initialized the database")


def main():
    init_db_command()


if __name__ == '__main__':
    main()
