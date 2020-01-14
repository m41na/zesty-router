let zesty = Java.type('com.practicaldime.zesty.app.AppProvider');
let app = zesty.provide({});

let port = 8080, host = 'localhost';
app.router()
    .wordpress("/var/www/wordpress", "http://127.0.0.1:9000")
    .listen(port, host, function (result) {
        print(result);
    });
