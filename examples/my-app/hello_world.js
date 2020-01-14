let zesty = Java.type('com.practicaldime.zesty.app.AppProvider');

let app = zesty.provide({});
print('zesty app is configured');

let router = app.router();

let Date = Java.type('java.util.Date');
let DateFormat = Java.type('java.text.SimpleDateFormat');

function now() {
    return new DateFormat("hh:mm:ss a").format(new Date());
}

router.get('/', function (req, res) {
    res.send(app.status().concat(" @ ").concat(now()));
});

let port = 8080, host = 'localhost';
router.listen(port, host, function (result) {
    print(result);
});
