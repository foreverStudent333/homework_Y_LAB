package habitsTrackApp.model;

/**
 * Класс описывающий сущность администратор
 *
 * @author Mihail Harhan
 */
public class Admin {
    private Integer id;
    private String name;

    public Admin(String name) {
        this.name = name;
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

    @Override
    public String toString() {
        return "Admin{" +
                "id=" + id +
                ", name='" + name + '\'' +
                '}';
    }
}
