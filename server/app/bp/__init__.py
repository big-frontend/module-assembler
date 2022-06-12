from app.bp import blog, auth, home, location
from flask import Flask, render_template
from flask_restful import Api


def init(app: Flask):
    # bp.add_resource()
    # bp.add_resource()
    app.register_blueprint(home.bp)
    app.register_blueprint(auth.bp)
    app.register_blueprint(location.bp)
    app.register_blueprint(blog.bp)
    app.add_url_rule('/', endpoint='index')

    @app.route('/')
    def index():
        return render_template('tab1/index.html')
