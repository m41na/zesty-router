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

Let's create a file :code:`simple_rest.js` and begin by creating a simple database access class. Since :code:`nashorn` 
does not use the :code:`class` keyword, we will use the :code:`function` syntax instead.::

    let AtomicInteger = Java.type('java.util.concurrent.atomic.AtomicInteger');

    function UserDao() {

        this.users = {
            0: {name: "James", email: "james@jjs.io", id: 0},
            1: {name: "Steve", email: "steve@jjs.io", id: 1},
            2: {name: "Carol", email: "carol@jjs.io", id: 2},
            3: {name: "Becky", email: "becky@jjs.io", id: 3}
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
        res.status(204);
    });
    
    router.delete('/delete/{id}', function (req, res) {
        let id = req.param('id')
        dao.delete(parseInt(id))
        res.status(205);
    });

    let port = 8080, host = 'localhost';
    router.listen(port, host, function(result){
        print(result);
    });

Start the application and listen for requests.::

    jjs --language=es6 -ot -scripting -J-Dlogback.configurationFile=../lib/app-logback.xml \
    -J-Djava.class.path=../lib/zesty-router-0.1.0-shaded.jar simple_rest.js

For comparison, the Java equilavent of :code:`simple_rest.js` would be.::

    public class SimpleRest {
        
        static class User {
            
            private int id;
            private String name;
            private String email;

            public User(String name, String email, int id) {
                super();
                this.id = id;
                this.name = name;
                this.email = email;
            }
            //omitted getters and setters
        }

        static class UserDao {

            private AtomicInteger lastId;
            private Map<Integer, User> users = new HashMap<>();
            
            public UserDao() {
                users.put(0, new User("James", "james@jjs.io", 0));
                users.put(1, new User("Steve", "steve@jjs.io", 1));
                users.put(2, new User("Carol", "carol@jjs.io", 2));
                users.put(3, new User("Becky", "becky@jjs.io", 3));
                lastId = new AtomicInteger(users.size() - 1);
            }
            
            public Map<Integer, User> all(){
                return this.users;
            }	    

            public void save(String name, String email) {
                int id = lastId.incrementAndGet();
                users.put(id, new User(name, email, id));
            }

            public User findById(int id) {
                return this.users.get(id);
            }

            public User findByEmail(String email){
                return users.values().stream()
                        .filter(user -> user.getEmail().equals(email))
                        .findFirst()
                        .orElse(null);
            }

            public void update(int id, String name, String email) {
                users.put(id, new User(name, email, id));
            }

            public void delete(int id) {
                users.remove(id);
            }
        }
        
        public static void main(String...args) {
            UserDao dao = new UserDao();
            
            Map<String, String> config = new HashMap<>();
            config.put("appctx", "/users");
            AppServer app = AppProvider.provide(config);
            
            app.router()
                .get("/", (req, res) -> {
                    res.json(dao.all());
                    return null;
                })
                .get("/{id}", (req, res) -> {
                    String id = req.param("id");
                    res.json(dao.findById(Integer.valueOf(id)));
                    return null;
                })
                .get("/email/{email}", (req, res) -> {
                    String email = req.param("email");
                    res.json(dao.findByEmail(email));
                    return null;
                })
                .post("/create", (req, res) -> {
                    String name = req.param("name");
                    String email = req.param("email");
                    dao.save(name, email);
                    res.status(201);
                    return null;
                })
                .put("/update/{id}", (req, res) -> {
                    String id = req.param("id");
                    String name = req.param("name");
                    String email = req.param("email");
                    dao.update(Integer.valueOf(id), name, email);
                    res.status(204);
                    return null;
                })
                .delete("/delete/{id}", (req, res) -> {
                    String id = req.param("id");
                    dao.delete(Integer.valueOf(id));
                    res.status(205);
                    return null;
                })
                .listen(8080, "localhost", (result) ->{
                    System.out.println(result);
                });
        }
    }

Adding a Page
^^^^^^^^^^^^^^^^^^^^^

Let's now create a home page for the :code:`simple_rest` app we have going. To do this, create a folder :code:`www` in the project's
root directory, and add a new file :code:`index.html`.::

    <!DOCTYPE html>
    <html>
        <head>
            <title>Index Page</title>
            <meta charset="UTF-8">
            <meta name="viewport" content="width=device-width, initial-scale=1.0">
            <style>
                * {
                    margin: 0px;
                    padding: 0px;
                }
                #wrapper {
                    width: 900px;
                    margin: 0px auto;
                    display: grid;
                    justify-content: center;
                    align-content: center;
                    grid-template-columns: repeat(3, 20vmin);
                    grid-template-rows: repeat(5, 20vmin);
                    grid-gap: 10px;
                }
                #wrapper .content {
                    display: grid;
                    align-content: center;
                    justify-content: center;
                }
                #wrapper .content:nth-child(even) {background: #eee}
                #wrapper .content:nth-child(odd) {background: #ccc}
            </style>
        </head>
        <body>
            <div id="wrapper">
                <div class="content">A</div>
                <div class="content">B</div>
                <div class="content">C</div>
                <div class="content">D</div>
                <div class="content">E</div>
                <div class="content">F</div>
            </div>
        </body>
    </html>

In the :code:`sample_rest.js` file, configure the assets parameters in the :code:`AppProvider`.::

    let app = zesty.provide({
        appctx: '/users',
        assets: 'www'
    });

Restart the application and navigate to the root context :code:`http://localhost:8080`. Before adding the :code:`index.html`,
the response was a :code:`404 - Not found` error. Now you should expect to see the index page.

A Freemarker Template
^^^^^^^^^^^^^^^^^^^^^^

The previous example used a plain :code:`html` page. This example uses a Freemarker template to display the users from the 
:code:`simple_rest` application. Let's create one. Copy the :code:`index.html` file and rename it to :code:`index.ftl`. 
This will be layout page for other pages. Let's begin with extracting the css into a new file, :code:`index.css`::

    * {
        margin: 0px;
        padding: 0px;
    }
    #wrapper {
        width: 900px;
        margin: 0px auto;
        display: grid;
        justify-content: center;
        align-content: center;
        grid-template-columns: repeat(3, 20vmin);
        grid-template-rows: repeat(5, 20vmin);
        grid-gap: 10px;
    }
    #wrapper .content {
        display: grid;
        align-content: center;
        justify-content: center;
    }
    #wrapper .content:nth-child(even) {background: #eee}
    #wrapper .content:nth-child(odd) {background: #ccc}

Refactor the :code:`index.ftl` page to make it a macro.::

    <#macro page>
    <!DOCTYPE html>
    <html>
        <head>
            <title>Index Page</title>
            <meta charset="UTF-8">
            <meta name="viewport" content="width=device-width, initial-scale=1.0">
            <link type="text/css" rel="stylesheet" href="/index.css">
        </head>
        <body>
            <div id="wrapper">
                <#nested>
            </div>
        </body>
    </html>
    </#macro>

Now create a template for displaying user data, and call it :code:`users.ftl`.::

    <#import "index.ftl" as u>
    <@u.page>
    <#list users?values as user>
    <div class="content">
        <p class="name">${user.name}</p>
        <p class="email">${user.email}</p>
        <p class="link">
        <a href="#" onclick="removeUser(event, '/users/delete/${user.id}')">delete</a>
        </p>
    </div>
    </#list>
    <script>
        function removeUser(e, url){
            e.preventDefault();
            fetch(url, {method: 'DELETE'})
                .then(res=>console.log(res))
                .catch(err=>console.log(err));
        }
    </script>
    </@u.page>

Now create a route to render this :code:`users.ftl` page on the :code:`/users` context.::

    router.get('/', function (req, res) {
        res.render('users', {users: dao.users});
    });

You will notice that I added a :code:`delete` link. Refactor the delete route to redirect to the :code:`/users` 
context to reload the page upon deletion.::

    router.delete('/delete/{id}', function (req, res) {
        let id = req.param('id')
        dao.delete(parseInt(id))
        res.redirect(app.resolve('/'));
    });

And finally, let's configure the :code:`AppProvider` to be aware of the view engine.::

    let app = zesty.provide({
        appctx: '/users',
        assets: 'www',
        engine: "freemarker"
    }); 

Restart the application and navigate to the root context :code:`http://localhost:8080/users`.

::

    **Please check again soon. The material is continually getting updated**