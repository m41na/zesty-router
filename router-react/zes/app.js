let zesty = Java.type('com.practicaldime.zesty.app.AppProvider');

let app = zesty.provide({
    "assets": "build"
});

let router = app.router();

router.get('/', function (req, res, promise) {
    res.render('index.html', {});
    promise.complete();
});

router.get('/ssr', function (req, res, promise) {
    res.render('index.js', {});
    promise.complete();
});

let port = 8080, host = 'localhost';
router.listen(port, host, function (result) {
    print(result);
});
