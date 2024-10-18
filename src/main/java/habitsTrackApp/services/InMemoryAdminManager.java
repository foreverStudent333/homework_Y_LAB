package habitsTrackApp.services;

import habitsTrackApp.model.Admin;
import habitsTrackApp.model.Habit;
import habitsTrackApp.model.User;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Класс описывающий логику работы с классом {@link Admin} для работы консольного приложения.
 * Включает в себе реализацию crud для админа и взаимодействие с учетками пользователей {@code User}
 *
 * @author Mihail Harhan "mihaillKHn@yandex.ru"
 */
public class InMemoryAdminManager implements AdminManager {
    private final InMemoryUserManager.IdGenerator adminIdGenerator;
    final private HashMap<Integer, Admin> adminsById;
    final InMemoryUserManager inMemoryUserManager;
    final InMemoryHabitsManager inMemoryHabitsManager;

    public InMemoryAdminManager() {
        adminIdGenerator = new InMemoryUserManager.IdGenerator();
        adminsById = new HashMap<>();
        inMemoryHabitsManager = new InMemoryHabitsManager();
        inMemoryUserManager = new InMemoryUserManager();
    }

    /**
     * Создает экземпляр класса с переданными экземплярами классов {@code InMemoryUserManager}
     * и {@code InMemoryHabitsManager}
     *
     * @param inMemoryUserManager который хранит внутри себя все данные и логику для работы с {@code User}
     * @param inMemoryHabitsManager который хранит внутри себя все данные и логику для работы с {@code Habit}
     */
    public InMemoryAdminManager(InMemoryUserManager inMemoryUserManager, InMemoryHabitsManager inMemoryHabitsManager) {
        this.inMemoryUserManager = inMemoryUserManager;
        this.inMemoryHabitsManager = inMemoryHabitsManager;
        adminIdGenerator = new InMemoryUserManager.IdGenerator();
        adminsById = new HashMap<>();
    }

    @Override
    public void createAdmin(Admin admin) {
        admin.setId(adminIdGenerator.getNextFreeId());
        adminsById.put(admin.getId(), admin);
    }

    @Override
    public void deleteAdmin(int id) {
        adminsById.remove(id);
    }

    @Override
    public void blockUser(String email) {
        User user = inMemoryUserManager.getUserByEmail(email);
        if(user != null) {
            user.setBlocked(true);
        }
    }

    @Override
    public void unblockUser(String email) {
        User user = inMemoryUserManager.getUserByEmail(email);
        if(user != null) {
            user.setBlocked(false);
        }
    }

    @Override
    public void deleteUser(String email) {
        inMemoryUserManager.deleteUser(inMemoryUserManager.getUserByEmail(email));
    }

    /**
     * Для получения всех пользователей
     * @return мапу всех текущих пользователй по ключу email
     */
    @Override
    public HashMap<String, User> getAllUsers() {
        return inMemoryUserManager.getUsers();
    }

    /**
     * Для получения всех привычек пользователя
     * @param email почта по которой найдем пользователя в базе
     * @return список всех привычек пользователя
     */
    @Override
    public ArrayList<Habit> getAllUserHabits(String email) {
        return inMemoryHabitsManager.getAllUserHabits(inMemoryUserManager.getUserByEmail(email));
    }

    public HashMap<Integer, Admin> getAdminsById() {
        return adminsById;
    }
}
