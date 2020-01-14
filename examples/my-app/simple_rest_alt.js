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
    res.render('users', {users: dao.users});
});

router.get('/{id}', function (req, res) {
    let id = req.param('id');
    let user = dao.findById(parseInt(id));
    res.render('user', {user: user});
});

router.get('/email/{email}', function (req, res) {
    let email = req.param('email');
    res.json(dao.findByEmail(email));
});

router.post('/create', function (req, res) {
    let name = req.param('name');
    let email = req.param('email');
    let user = dao.save(name, email);
    res.redirect(app.resolve("/" + user.id));
});

router.put('/update/{id}', function (req, res) {
    let id = req.param('id');
    let name = req.param('name');
    let email = req.param('email');
    dao.update(parseInt(id), name, email);
    res.render('user', {user: {id, name, email}});
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
