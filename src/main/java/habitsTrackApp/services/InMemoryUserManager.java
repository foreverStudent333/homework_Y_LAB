package habitsTrackApp.services;

import habitsTrackApp.model.HabitStatus;
import habitsTrackApp.model.HabitType;
import habitsTrackApp.model.User;

import java.util.HashMap;
import java.util.regex.Pattern;

/**
 * Класс описывающий логику работы с классом {@link User} для работы консольного приложения.
 * Включает в себе реализацию crud для пользователя и хранения всех пользователей
 * ключ(email) - значение(пользователь)
 *
 * @author Mihail Harhan "mihaillKHn@yandex.ru"
 */
public class InMemoryUserManager implements UserManager {
    private final IdGenerator userIdGenerator;
    private final HashMap<String, User> users;
    final InMemoryHabitsManager inMemoryHabitsManager;
    final InMemoryHistoryManager inMemoryHistoryManager;

    public InMemoryUserManager() {
        userIdGenerator = new IdGenerator();
        users = new HashMap<>();
        inMemoryHabitsManager = new InMemoryHabitsManager();
        inMemoryHistoryManager = new InMemoryHistoryManager();
    }

    /**
     * Создает экземпляр класса с переданными экземплярами классов {@code InMemoryHabitsManager}
     * и {@code InMemoryHistoryManager}
     *
     * @param inMemoryHabitsManager  который хранит внутри себя все данные и логику для работы с {@code Habit}
     * @param inMemoryHistoryManager который хранит внутри себя все данные и
     *                               логику для работы с историей выполнения привычек
     */
    public InMemoryUserManager(InMemoryHabitsManager inMemoryHabitsManager,
                               InMemoryHistoryManager inMemoryHistoryManager) {
        userIdGenerator = new IdGenerator();
        users = new HashMap<>();
        this.inMemoryHabitsManager = inMemoryHabitsManager;
        this.inMemoryHistoryManager = inMemoryHistoryManager;
    }

    /**
     * Добавляет нового юзера в базу, проверяет валидность email и выдает юзеру уникальный id
     *
     * @param user новый, которого нужно занести в базу
     */
    @Override
    public void addNewUser(User user) {
        String email = user.getEmail();
        String password = user.getPassword();
        if (users.containsKey(email) || password.length() > 50) {
            return;
        }
        if (!isEmailValid(email)) {
            return;
        }

        user.setId(userIdGenerator.getNextFreeId()); //присваиваю всем юзер айди для внутренней системы(не видно юзерам)
        users.put(email, user);
    }

    @Override
    public User getUserByEmail(String email) {
        return users.get(email);
    }

    /**
     * @return {@code HashMap<String, User>} пользователей по ключу email
     */
    public HashMap<String, User> getUsers() {
        return users;
    }

    /**
     * Авторизовывает юзера в системе, проверяет корректность его email и пароля
     *
     * @param email    по которому юзер находится в базе
     * @param password пароль который нужно проверить принадлежит ли {@code user} и корректный ли
     * @return {@code User} из базы юзеров
     */
    @Override
    public User authorizeUser(String email, String password) {
        User user = getUserByEmail(email);
        if (user == null) {
            return null;
        }
        if (user.getPassword().equals(password)) {
            return user;
        } else {
            return null;
        }
    }

    /**
     * Удаляет юзера из базы и всю его историю выполнения привычек
     *
     * @param user которого нужно удалить
     */
    @Override
    public void deleteUser(User user) {
        inMemoryHistoryManager.deleteAllUserHabitsFromHistory(user);
        users.remove(user.getEmail());
    }

    @Override
    public void changePassword(User user, String oldPassword, String newPassword) {
        User oldUser = getUserByEmail(user.getEmail());
        if (oldUser == null) {
            return;
        }
        if (oldUser.getPassword().equals(oldPassword)) {
            oldUser.setPassword(newPassword);
        }
    }

    @Override
    public void changeName(User user, String newName) {
        User oldUser = getUserByEmail(user.getEmail());
        if (oldUser == null) {
            return;
        }
        oldUser.setName(newName);
    }

    @Override
    public void changeEmail(User user, String newEmail) {
        User oldUser = getUserByEmail(user.getEmail());
        User newUser = getUserByEmail(user.getEmail());
        newUser.setEmail(newEmail);
        if (oldUser == null || users.containsKey(newEmail)) {
            return;
        }
        addNewUser(newUser);
        if (users.containsKey(newEmail)) {
            deleteUser(oldUser);
        }
    }

    /**
     * Метод вызывается при смене дня. Сбрасывает статус прогресса выполнения ежедневных привычек юзера
     * с {@code HabitStatus.IN_PROGRESS} на {@code HabitStatus.IN_PROGRESS}
     *
     * @param user у которого нужно обновить статус привычек
     */
    @Override
    public void resetAllDoneDailyHabits(User user) {
        inMemoryHabitsManager.getAllUserHabits(user).forEach(e -> {
            inMemoryHistoryManager.updateDailyHabitHistory(e);
            HabitType habitType = e.getHabitType();
            if (habitType.equals(HabitType.WEEKLY)) {
                //do nothing
            } else if (habitType.equals(HabitType.DAILY) && e.getHabitStatus().equals(HabitStatus.FINISHED)) {
                e.setHabitStatus(HabitStatus.IN_PROGRESS);
                e.setStreak(e.getStreak() + 1);
            } else {
                e.setStreak(0);
            }
        });
    }

    /**
     * Метод вызывается при смене недели. Сбрасывает статус прогресса выполнения ежедневных
     * и еженедельных привычек юзера с {@code HabitStatus.IN_PROGRESS} на {@code HabitStatus.IN_PROGRESS}
     *
     * @param user у которого нужно обновить статус привычек
     */
    @Override
    public void resetAllDoneWeeklyHabits(User user) {
        inMemoryHabitsManager.getAllUserHabits(user).forEach(e -> {
            HabitType habitType = e.getHabitType();
            if (habitType.equals(HabitType.WEEKLY)) {
                inMemoryHistoryManager.updateWeeklyHabitHistory(e);
            } else {
                inMemoryHistoryManager.updateDailyHabitHistory(e);
            }

            if (habitType.equals(HabitType.WEEKLY) || habitType.equals(HabitType.DAILY)
                    && e.getHabitStatus().equals(HabitStatus.FINISHED)) {
                e.setHabitStatus(HabitStatus.IN_PROGRESS);
                e.setStreak(e.getStreak() + 1);
            } else {
                e.setStreak(0);
            }
        });
    }

    @Override
    public void changePasswordByEmailRecoveryCode(User user, String newPassword) {
        //TODO
    }

    public InMemoryHabitsManager getInMemoryHabitsManager() {
        return inMemoryHabitsManager;
    }

    public InMemoryHistoryManager getInMemoryHistoryManager() {
        return inMemoryHistoryManager;
    }

    /**
     * проверка валидности почты email
     *
     * @param email который нужно проверить
     * @return {@code true} если email валидный
     */
    private boolean isEmailValid(String email) {
        String regex = "^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$";
        Pattern pattern = Pattern.compile(regex, Pattern.CASE_INSENSITIVE);
        // простая проверка на корректный емейл
        return pattern.matcher(email).matches();
    }

    public static final class IdGenerator {
        private int nextFreeId = 1;

        public int getNextFreeId() {
            return nextFreeId++;
        }
    }
}
