# -*- coding: utf-8 -*-
from flask import (
    Blueprint, request, json
)

from app.storage import get_db

bp = Blueprint('location', __name__, url_prefix='/location')


# @bp.route('/data/<int:uuid>', methods=('POST', 'GET'))
@bp.route('/data', methods=('POST', 'GET'))
def data():
    db = get_db()
    uuid = int(request.args['uuid'])
    body = request.data.decode('utf-8')

    print('request url args {}  \nbody {}'.format(request.args, body))
    if request.method == 'POST':
        if uuid > 0:
            db.execute(
                'INSERT INTO lbs (uuid,body) VALUES (?,?)',
                (uuid, body)
            )
            db.commit()
            return "post sucessful"
        else:
            return 'post fail ,uuid must not empty'
    elif request.method == 'GET':
        if uuid > 0:
            locations = db.execute(
                'SELECT created,body FROM lbs WHERE uuid=?', (uuid,)).fetchall()
            # for location in locations
            # return " location get uuid:{} \nbody:{}".format(uuid,locations)
            resp_body = []
            for location in locations:
                print("location response \n>>>>:{}".format(location['body']))
                resp_body.append(json.loads(location['body']))
            # return json.dumps(locations, sort_keys=True, indent=4)
            # return json.dumps(resp_body)
            return json.jsonify(resp_body)

        else:  # get alll
            locations = db.execute(
                'SELECT uuid,body FROM lbs').fetchall()
            return " location get all \n{} ".format(locations)

    print(" response data sucessful ")
    return " invaldate request method"


@bp.route('/getIp', methods=('POST', 'GET'))
def getIp():
    # print(request.remote_addr)
    if request.environ.get('HTTP_X_FORWARDED_FOR') is None:
        print(request.environ['REMOTE_ADDR'])
    else:
        print(request.environ['HTTP_X_FORWARDED_FOR'])  # if behind a proxy
    return 'response sucessful'
