from flask import (
    Blueprint, flash, redirect, request, session, url_for,
    template_rendered, render_template,
    g, current_app
)

bp = Blueprint('home', __name__, cli_group=None)


@bp.route('/')
def index():
    return render_template('tab1/index.html')


@bp.route('/tab2')
def tab2():
    return render_template('tab2/index.html')


@bp.route('/tab3')
def tab3():
    return render_template('tab3/index.html')


@bp.route('/tab4')
def tab4():
    return render_template('tab4/index.html')
