#!/usr/bin/python

from flup.server.fcgi import WSGIServer


def app(environ, start_response):
    start_response('200 OK', [('Content-Type', 'text/html')])
    return ('''<html>
    <head>
         <title>Hello World!</title>
    </head>
    <body>
         <h1>Hello world!</h1>
    </body>
</html>''')


if __name__ == "__main__":
    WSGIServer(app, bindAddress='/tmp/fcgi.sock').run()
