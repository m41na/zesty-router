let Dao = {};

;(function () {

    let AtomicInteger = Java.type('java.util.concurrent.atomic.AtomicInteger');

    function UserDao() {

        this.users = {
            0: {name: "James", email: "james@jjs.io", id: 0},
            1: {name: "Steve", email: "steve@jjs.io", id: 1},
            2: {name: "Carol", email: "carol@jjs.io", id: 2},
            3: {name: "Becky", email: "becky@jjs.io", id: 3}
        }

        this.lastId = new AtomicInteger(3);

        this.save = (name, email) => {
            let id = this.lastId.incrementAndGet()
            this.users[id] = {name: name, email: email, id: id}
            return this.users[id];
        }

        this.findById = (id) => {
            return this.users[id]
        }

        this.findByEmail = (email) => {
            return Object.values(this.users).find(it => it.email == email)
        }

        this.update = (id, name, email) => {
            this.users[id] = {name: name, email: email, id: id}
        }

        this.delete = (id) => {
            delete this.users[id]
        }
    }

    //export the class to the Dao scope
    Dao.UserDao = UserDao;
})();

