load('./simple_repo.js');

let dao = new Dao.UserDao();

let zesty = Java.type('com.practicaldime.zesty.app.AppProvider');
let app = zesty.provide({
    appctx: '/users',
    assets: 'www',
    engine: "freemarker"
});

let router = app.router();
router.get('/', function (req, res) {
    res.json(dao.users);
});

router.get('/{id}', function (req, res) {
    let id = req.param('id');
    res.json(dao.findById(parseInt(id)))
});

router.get('/email/{email}', function (req, res) {
    let email = req.param('email');
    res.json(dao.findByEmail(email));
});

router.post('/create', function (req, res) {
    let name = req.param('name');
    let email = req.param('email');
    dao.save(name, email);
    res.status(201);
});

router.put('/update/{id}', function (req, res) {
    let id = req.param('id')
    let name = req.param('name');
    let email = req.param('email');
    dao.update(parseInt(id), name, email);
    res.status(204);
});

router.delete('/delete/{id}', function (req, res) {
    let id = req.param('id')
    dao.delete(parseInt(id))
    res.status(205);
});

let port = 8080, host = 'localhost';
router.listen(port, host, function (result) {
    print(result);
});
