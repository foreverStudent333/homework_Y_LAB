package habitsTrackApp.model;

/**
 * Класс описывающий сущность пользователь
 *
 * @author Mihail Harhan
 */
public class User {
    private Integer id;
    private String password;
    private String email;
    private String name = "";
    private boolean isBlocked = false;

    public User(String password, String email) {
        this.password = password;
        this.email = email;
    }

    public User(String password, String email, String name) {
        this.password = password;
        this.email = email;
        this.name = name;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public boolean isBlocked() {
        return isBlocked;
    }

    public void setBlocked(boolean blocked) {
        isBlocked = blocked;
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", password='" + password + '\'' +
                ", email='" + email + '\'' +
                ", name='" + name + '\'' +
                ", isBlocked=" + isBlocked +
                '}';
    }
}
