from contextlib import contextmanager

import click
from flask import (
    Blueprint, flash, redirect, request, session, url_for,
    template_rendered, render_template,
    g, current_app
)
from flask.cli import with_appcontext
from werkzeug.exceptions import HTTPException, BadRequest, ClientDisconnected, Unauthorized, abort

from app.bp.auth import signin_required
from app.storage import get_db

bp = Blueprint('blog', __name__, cli_group=None)


# @bp.route('/')
# def index():
#     db = get_db()
#     posts = db.execute('SELECT p.id, title, body, created, author_id, username'
#                        ' FROM post p JOIN user u ON p.author_id = u.id'
#                        ' ORDER BY created DESC').fetchall()
#     return render_template('blog/index.html', posts=posts)


@bp.route('/create', methods=('GET', 'POST'))
@signin_required
def create():
    if request.method == 'POST':
        title = request.form['title']
        body = request.form['body']
        error = None

        if not title:
            error = 'title is required. '

        if error is not None:
            flash(error)
        else:
            db = get_db()
            db.execute('INSERT INTO post (title,body,author_id) VALUES (?, ?, ?)',
                       (title, body, g.user['id']))
            db.commit()
            return redirect(url_for('blog.index'))

    return render_template('blog/create.html')


@bp.route('/<int:id>/update', methods=('GET', 'POST'))
@signin_required
def update(id):
    post = get_post(id)
    if request.method == 'POST':
        title = request.form['title']
        body = request.form['body']
        error = None
        if not title:
            error = 'title is required'
        if error is not None:
            flash(error)
        else:
            db = get_db()
            db.execute(
                'UPDATE post SET title =? , body =? WHERE id = ?', (title, body, id))
            db.commit()
            return redirect(url_for('blog.index'))
    return render_template('blog/update.html', post=post)


def get_post(id, check_author=True):
    post = get_db().execute('SELECT p.id, title ,body,created,author_id,username'
                            ' FROM post p JOIN user u ON p.author_id = u.id'
                            ' WHERE p.id = ?', (id,)).fetchone()
    if post is None:
        abort(404, '''post id {0} doesn't exit.'''.format(id))
    if check_author and post['author_id'] != g.user['id']:
        abort(403)
    return post


@bp.route('/<int:id>/delete', methods=['POST'])
@signin_required
def delete(id):
    get_post(id)
    db = get_db()
    db.execute('DELETE FROM post WHERE id = ?', (id,))
    db.commit()
    return redirect(url_for('blog.index'))


# =================================================================

@bp.app_template_filter("print_hello_world")
def print_hello_world(s):
    return "print_hello_world: {}".format(s)


# def printHelloWorld(s):
#     return "printHelloWorld: {}".format(s)
#
#
# bp.add_app_template_filter(printHelloWorld, name='printHelloWorld')

@bp.app_context_processor
def utility_processor():
    def print():
        return 'hahaha'

    return dict(print_haha=print, user_id=session.get('user_id'))


@bp.errorhandler(BadRequest)
def handle_bad_request(e):
    return 'bad request', 400


# class InsufficientStorage(werkzeug.exceptions.HTTPException):
#       code = 507
#       description = 'Not enough storage space.'
# app.register_error_handler(InsufficientStorage, handle_507)


# def handle_bad_request(e):
#     return 'bad request', 400
# bp.register_error_handler(400,handle_bad_request)


@bp.errorhandler(Exception)
def handle_exception(e):
    if isinstance(e, HTTPException):
        return e
    elif isinstance(e, BadRequest):
        return 'bad request'
    elif isinstance(e, ClientDisconnected):
        return 'ClientDisconnected'
    elif isinstance(e, Unauthorized):
        return 'Unauthorized'


@contextmanager
def captured_templates(app):
    recorded = []

    def recored(sender, template, context, **extra):
        recorded.append((template, context))

    template_rendered.connect(recored, app)
    try:
        yield recorded
    finally:
        template_rendered.disconnect(recored, app)


# @template_rendered.connect_via(current_app)
# def when_template_rendered(sender, template, context, **extra):
#     print('when tempalte rendered')


from flask.views import View, MethodView


class Users(View):
    def dispatch_request(self):
        return render_template('xxx.html')


bp.add_url_rule('/users/', view_func=Users.as_view('get_users'))


class UserApi(MethodView):
    decorators = [signin_required]

    def get(self):
        print('user bp get')

    def post(self):
        print('user bp post')


bp.add_url_rule('/userapi/', view_func=UserApi.as_view('userapi'))
view = signin_required(UserApi.as_view('userapiv2'))
bp.add_url_rule('/userapiv2/', view_func=view)


# signals start
# @appcontext_pushed
# def pushed():
#     pass


# @appcontext_popped
# def popped():
#     pass


# @appcontext_tearing_down
# def tearing_down():
#     pass
# signals end


@bp.cli.command('create')
@click.argument('name')
@with_appcontext
def createv2(name):
    print("{} application context:{}".format(name, current_app))
