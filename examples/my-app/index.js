let AtomicInteger = Java.type('java.util.concurrent.atomic.AtomicInteger');

function UserDao() {

    this.users = {
        0: {name: "Alice", email: "alice@alice.kt", id: 0},
        1: {name: "Bob", email: "bob@bob.kt", id: 1},
        2: {name: "Carol", email: "carol@carol.kt", id: 2},
        3: {name: "Dave", email: "dave@dave.kt", id: 3}
    }
    
    this.lastId = new AtomicInteger(this.users.size - 1);   

    this.save = (name, email) => {
        let id = this.lastId.incrementAndGet()
        this.users[id] = {name: name, email: email, id: id}
    }

    this.findById = (id) => {
        return this.users[id]
    }

    this.findByEmail = (email) => {
        return Object.values(this.users).find(it => it.email == email )
    }

    this.update = (id, name, email) => {
        this.users[id] = {name: name, email: email, id: id}
    }

    this.delete = (id) => {
        delete this.users[id]
    }
}

let dao = new UserDao();

let zesty = Java.type('com.practicaldime.zesty.app.AppProvider');
let app = zesty.provide({
    appctx: '/users'
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
    res.status(200);
});
router.delete('/delete/{id}', function (req, res) {
    let id = req.param('id')
    dao.delete(parseInt(id))
    res.status(200);
});

let port = 8080, host = 'localhost';
router.listen(port, host, function(result){
    print(result);
});