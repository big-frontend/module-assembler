import os
from flask import Flask
import sentry_sdk
from sentry_sdk.integrations.flask import FlaskIntegration
import logging

from flask.logging import default_handler

from config import DevelopmentConfig, mail_handler

from werkzeug.utils import import_string

'''
author:hawks jamesf
'''

from app import db
from app import auth
from app import location
from app import blog
from app import home
from flask_restful import Api


def register_blueprint(app):
    app.register_blueprint(auth.bp)
    app.register_blueprint(location.bp)
    app.register_blueprint(blog.bp)
    app.register_blueprint(home.bp)
    app.add_url_rule('/', endpoint='index')


def register_logging(app):
    """
    logging的配置有两种：dictConfig配置表 与 代码配置
    """
    if not app.debug:
        app.logger.addHandler(mail_handler)


def register_commands(app):
    db.oncreate_app(app)


def create_app(config_file=None, config_object=DevelopmentConfig):
    app = Flask(__name__, instance_relative_config=True)

    # 优先从文件区配置，有利于动态改变正在运行的app配置
    # if config_file is not  None:
    if config_object:
        # 1.cfg =import_string('config.DevelopmentConfig')
        # app.config.from_object(cfg)
        # 2 app.config.from_object('config.DevelopmentConfig')
        app.config.from_object(config_object)

    else:
        app.config.from_pyfile(config_file, silent=True)

    logging.info('flask env : {}'.format(app.config['SECRET_KEY']))
    app.config.from_mapping(SECRET_KEY='dev' if app.config['SECRET_KEY'] is None else app.config['SECRET_KEY'],
                            DATABASE=os.path.join(app.instance_path, 'app.sqlite'))
    # app.config.from_envvar()
    # app.config.from_json()
    # app.config.update()
    try:
        os.makedirs(app.instance_path)
    except OSError:
        pass

    sentry_sdk.init(
        dsn="https://c659bb3641e14a86b54a0d3db91ff7ea@sentry.io/2495776",
        integrations=[FlaskIntegration()]
    )
    # @app.route('/')
    # def hello_world():
    #     return 'Hello World!'
    register_commands(app)
    register_blueprint(app)
    register_logging(app)

    return app
