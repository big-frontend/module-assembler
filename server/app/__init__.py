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

    if not app.debug:
        app.logger.addHandler(mail_handler)
    # @app.route('/')
    # def hello_world():
    #     return 'Hello World!'

    from . import db
    db.oncreate_app(app)

    from . import auth
    app.register_blueprint(auth.bp)

    from . import location
    app.register_blueprint(location.bp)

    from . import blog
    app.register_blueprint(blog.bp)

    from . import home
    app.register_blueprint(home.bp)
    app.add_url_rule('/', endpoint='index')
    return app


