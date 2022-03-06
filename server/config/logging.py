logger_dict = {
    'version': 1,
    'formatters': {
        'default_formatter': {
            'format': '[%(asctime)s] %(levelname)s in %(module)s: %(message)s',
        },
        'error_formatter': {
            'format': '%(asctime)s [%(levelname)s] :%(levelno)s: %(message)s',
        }

    },
    'handlers': {
        'wsgi_handler': {
            'class': 'logging.StreamHandler',
            'stream': 'ext://flask.logging.wsgi_errors_stream',
            'formatter': 'default_formatter'
        },
        # 'error_file_handler': {
        #     'class': 'logging.handlers.RotatingFileHandler',
        #     'formatter': 'default_formatter',
        #     'level': 'ERROR',
        #     'filename': './logs/error.log',
            # 'maxBytes': '10485760',
            # 'backupCount': '20',
            # 'encoding': 'utf8',
        # }
    },
    'root': {
        'level': 'INFO',
        'handlers': ['wsgi_handler'],
    }
}
