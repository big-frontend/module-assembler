from flask import (
    Blueprint
)

bp = Blueprint('profile', __name__, url_prefix='/api')


@bp.route('',  methods=('POST', 'GET'))
def profile():
    pass
