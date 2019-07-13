package team.perfect.fresh_air.DAO;

import javax.persistence.Entity;
import javax.persistence.Id;

import com.google.gson.JsonObject;

@Entity
public class User {
    @Id
    String userId;
    String passwd;
    boolean usingMeasuredDust;

    public User() {
    }

    public User(String userId, String passwd, boolean usingMeasuredDust) {
        this.userId = userId;
        this.passwd = passwd;
        this.usingMeasuredDust = usingMeasuredDust;
    }

    public User(JsonObject user) throws NullPointerException, ClassCastException, IllegalStateException {
        this.userId = user.get(Key.USER_ID.getKey()).getAsString();
        this.passwd = user.get(Key.PASSWORD.getKey()).getAsString();

        try {
            this.usingMeasuredDust = user.get(Key.USING_MEASURED_DUST.getKey()).getAsBoolean();
        } catch (NullPointerException | ClassCastException | IllegalStateException e) {
            this.usingMeasuredDust = false;
        }
    }

    public String getUserId() {
        return this.userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getPasswd() {
        return this.passwd;
    }

    public void setPasswd(String passwd) {
        this.passwd = passwd;
    }

    public boolean isUsingMeasuredDust() {
        return this.usingMeasuredDust;
    }

    public void setIsUsingMeasuredDust(boolean isUsingMeasuredDust) {
        this.usingMeasuredDust = isUsingMeasuredDust;
    }

    public enum Key {
        USER_ID("userId"), PASSWORD("passwd"), USING_MEASURED_DUST("usingMeasuredDust");

        private String key;

        Key(String key) {
            this.key = key;
        }

        public String getKey() {
            return key;
        }
    }
}