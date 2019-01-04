package com.practicaldime.zesty.examples;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import com.practicaldime.zesty.app.AppProvider;
import com.practicaldime.zesty.app.AppServer;

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

		public int getId() {
			return id;
		}

		public void setId(int id) {
			this.id = id;
		}

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

		public String getEmail() {
			return email;
		}

		public void setEmail(String email) {
			this.email = email;
		}
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
