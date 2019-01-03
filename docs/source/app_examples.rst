App Examples
=============

A few examples are included here to highlight usage scenarios. The syntax used will be JavaScript although the same the Java
implementation would almost literary be the same.

Hello World App
^^^^^^^^^^^^^^^^

This is just a simple application which responds with the current time when the root context is accessed.

* Create a new project directory and add a :code:`lib` folder to it as well.::

    mkdir -p my-app/lib;

* Download the :code:`zesty-router[version].jar` and place it in the *lib* folder.

* *cd* into *my-app* folder and create a new file, :code:`index.js`.

* In the :code:`index.js` file, import the :code:`AppProvider` class.::

    let zesty = Java.type('com.practicaldime.zesty.app.AppProvider');

* Initialize the application using some initial configuration.::

    let app = zesty.provide({});    
    print('zesty app is configured');

* Create a router to add handlers.::

    let router = app.router();

* Add a function to fetch the current time. For a little bit or fun, let's also format the time.::

    let Date = Java.type('java.util.Date');
    let DateFormat = Java.type('java.text.SimpleDateFormat');

    function now() {
        return new DateFormat("hh:mm:ss a").format(new Date());
    }

* Add a route to handle the root context.::

    router.get('/', function (req, res) {
        res.send(app.status().concat(" @ ").concat(now()));
    });

* Configure the host and port to listen for requests.::

    let port = 8080, host = 'localhost';
    router.listen(port, host, function(result){
        print(result);
    });

* Start the application and start serving time.::

    jjs --language=es6 -ot -scripting -J-Djava.class.path=./lib/zesty-router-0.1.0-shaded.jar index.js

Simple REST App
^^^^^^^^^^^^^^^^

Let's begin with creating a simple database access class.::

    let AtomicInteger = Java.type('java.util.concurrent.atomic.AtomicInteger');

    function UserDao() {

        this.users = {
            0: {name: "Alice", email: "alice@alice.kt", id: 0},
            1: {name: "Bob", email: "bob@bob.kt", id: 1},
            2: {name: "Carol", email: "carol@carol.kt", id: 2},
            3: {name: "Dave", email: "dave@dave.kt", id: 3}
        }

        this.lastId = new AtomicInteger(this.users.size - 1)

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

Next, let's create the API service.::

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

::

    **Please check again soon. The material is continually getting updated**