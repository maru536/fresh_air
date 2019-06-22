package team.perfect.fresh_air.DAO;

import javax.persistence.Entity;
import javax.persistence.Id;

import com.google.gson.JsonObject;

@Entity
public class User {
    @Id
    String id;
    String passwd;

    public User() {}

    public User(String id, String passwd) {
        this.id = id;
        this.passwd = passwd;
    }

    public User(JsonObject user) {
        this(user.get("id").getAsString(), user.get("passwd").getAsString());
    }

    public String getId() {
        return this.id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getPasswd() {
        return this.passwd;
    }

    public void setPasswd(String passwd) {
        this.passwd = passwd;
    }
}