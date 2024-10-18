package habitsTrackApp.controller;

import habitsTrackApp.model.*;
import habitsTrackApp.services.InMemoryAdminManager;
import habitsTrackApp.services.InMemoryHabitsManager;
import habitsTrackApp.services.InMemoryHistoryManager;
import habitsTrackApp.services.InMemoryUserManager;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Scanner;
import java.util.SortedMap;

/**
 * Класс контроллер для запуска приложения через консоль и
 * взаимодействия с бэкендом приложения через консоль
 *
 * @author Mihail Harhan "mihaillKHn@yandex.ru"
 */

public class ControllerConsole {
    Scanner scanner;
    private final InMemoryUserManager userManager;
    private final InMemoryHabitsManager habitManager;
    private final InMemoryHistoryManager historyManager;
    private final InMemoryAdminManager adminManager;
    private User user;
    private Habit habit;
    private Admin admin;

    public ControllerConsole() {
        historyManager = new InMemoryHistoryManager();
        habitManager = new InMemoryHabitsManager(historyManager);
        userManager = new InMemoryUserManager(habitManager, historyManager);
        adminManager = new InMemoryAdminManager(userManager, habitManager);
        user = null;
        habit = null;
        admin = null;
        scanner = new Scanner(System.in);
    }

    public void printMenuAndDoCommands() {
        printMenu();
        int command = scanner.nextInt();
        String email = "";
        String password = "";
        int daysPassCounter = 0;

        while (command != 0) {
            switch (command) {
                case 1:
                    System.out.println("Введите email");
                    email = scanner.next();
                    if (userManager.getUserByEmail(email) != null) {
                        System.out.println("Такой пользователь уже есть");
                        break;
                    }
                    System.out.println("Введите пароль (не больше 50 символов)");
                    password = scanner.next();
                    user = new User(password, email);
                    userManager.addNewUser(user);
                    if (userManager.getUserByEmail(email) != null) {
                        System.out.println("Вы зарегистрированы!");
                    } else {
                        System.out.println("Некорректный email или пароль");
                    }
                    user = null;
                    break;
                case 2:
                    System.out.println("Введите email");
                    email = scanner.next();
                    if (userManager.getUserByEmail(email) == null) {
                        System.out.println("Такого пользователя нет");
                        break;
                    }
                    System.out.println("Введите пароль (не больше 50 символов)");
                    password = scanner.next();
                    user = userManager.authorizeUser(email, password);
                    if (user == null) {
                        System.out.println("Некорректный email или пароль");
                    } else if (user.isBlocked()) {
                        System.out.println("Вход не выполнен, вы заблокированы!");
                    } else {
                        System.out.println("Вы вошли в систему");
                    }
                    break;
                case 3:
                    if (user == null) {
                        System.out.println("Ты не авторизирован в системе");
                        break;
                    }
                    printMenuForUserUpdate();
                    command = scanner.nextInt();

                    switch (command) {
                        case 1:
                            System.out.println("Введи новое имя");
                            String name = scanner.next();
                            userManager.changeName(user, name);
                            System.out.println("Новое имя: " + user.getName());
                            break;
                        case 2:
                            System.out.println("Введи старый пароль");
                            String oldPas = scanner.next();
                            System.out.println("Введи новый пароль");
                            String newPas = scanner.next();
                            if (!user.getPassword().equals(oldPas)) {
                                System.out.println("Старый пароль неверный");
                                break;
                            }
                            userManager.changePassword(user, oldPas, newPas);
                            System.out.println("Новый пароль " + user.getPassword());
                            break;
                        case 3:
                            System.out.println("Введи новую почту");
                            String newEmail = scanner.next();
                            userManager.changeEmail(user, newEmail);
                            if (user.getEmail().equals(newEmail)) {
                                System.out.println("Новый пароль " + user.getEmail());
                            } else {
                                System.out.println("Пароль не изменен. Некорректный пароль или пароль" +
                                        "уже есть в системе");
                            }
                            break;
                        case 4:
                            userManager.deleteUser(user);
                            user = null;
                            System.out.println("Аккаунт удален=(");
                            break;
                        case 5:
                            user = null;
                            System.out.println("Вы вышли из системы!");
                            break;
                        case 0:
                            break;
                        default:
                            System.out.println("Такой команды нет, введите команду снова.");
                            break;
                    }
                    break;
                case 4:
                    if (user == null) {
                        System.out.println("Ты не авторизирован в системе");
                        break;
                    }
                    printMenuForHabitsUpdateAndStatistics();
                    command = scanner.nextInt();
                    String name = "";
                    String description = "";
                    int id = 0;
                    String type = "";
                    String status = "";
                    HabitStatus habitStatus = HabitStatus.NEW;
                    int days = 0;
                    DateTimeFormatter onlyDateFormat = DateTimeFormatter.ofPattern("dd-MM-yyyy");

                    switch (command) {
                        case 1:
                            System.out.println("Введите название привычки");
                            name = scanner.next();
                            System.out.println("Введите описание привычки");
                            description = scanner.next();
                            System.out.println("Выберите тип привычки по номеру (введите номер): " +
                                    "1 - daily, 2 - weekly, 3 - any");
                            type = scanner.next();
                            HabitType habitType = getHabitTypeByNumber(type);
                            habit = new Habit(name, description, habitType, user.getId());
                            habitManager.addNewHabit(user, habit);
                            System.out.println("done");
                            habit = null;
                            break;
                        case 2:
                            habitManager.getAllUserHabits(user).forEach(System.out::println);
                            break;
                        case 3:
                            System.out.println("Введите id привычки для изменения");
                            id = scanner.nextInt();
                            habit = habitManager.getHabitById(user, id);
                            if (habit == null) {
                                System.out.println("такой привычки нет");
                                break;
                            }
                            System.out.println("""
                                    Если хотите поменять статус введите 1
                                    Если хотите поменять название введите 2
                                    Если хотите поменять описание введите 3""");
                            int option = scanner.nextInt();
                            switch (option) {
                                case 1:
                                    System.out.println("Введите новый статус привычки по номеру (введите номер): " +
                                            "1 - new, 2 - in_progress, 3 - finished");
                                    status = scanner.next();
                                    if (!status.equals("1") && !status.equals("2") && !status.equals("3")) {
                                        System.out.println("Неверный статус");
                                        break;
                                    }
                                    habitStatus = getHabitStatusByNumber(status);
                                    habitManager.updateHabitStatus(user, habit, habitStatus);
                                    System.out.println("done!");
                                    break;
                                case 2:
                                    System.out.println("Введите новое название");
                                    name = scanner.next();
                                    habitManager.updateHabitName(user, habit, name);
                                    break;
                                case 3:
                                    System.out.println("Введите новое описание");
                                    description = scanner.next();
                                    habitManager.updateHabitDescription(user, habit, description);
                                    break;
                                default:
                                    System.out.println("Такой команды нет");
                                    break;
                            }
                            break;
                        case 4:
                            habitManager.setEveryHabitStatusFinished(user);
                            System.out.println("done");
                            break;
                        case 5:
                            System.out.println("С каким статусом показать все привычки (введите номер): " +
                                    "1 - new, 2 - in_progress, 3 - finished");
                            status = scanner.next();
                            if (!status.equals("1") && !status.equals("2") && !status.equals("3")) {
                                System.out.println("Неверный статус");
                                break;
                            }
                            habitStatus = getHabitStatusByNumber(status);
                            habitManager.getUserHabitsWithACertainStatus(user, habitStatus).forEach(System.out::println);
                            break;
                        case 6:
                            habitManager.getUserHabitsFilteredByStatus(user).forEach(System.out::println);
                            break;
                        case 7:
                            habitManager.getAllUserHabitsFilteredByCreationDate(user).forEach(System.out::println);
                            break;
                        case 8:
                            System.out.println("Введите id привычки");
                            id = scanner.nextInt();
                            habit = habitManager.getHabitById(user, id);
                            if (habit == null) {
                                System.out.println("такой привычки нет");
                                break;
                            }
                            System.out.println("Введите кол-во дней период статистики от 1 до 30");
                            days = scanner.nextInt();
                            if (days > 0 && days <= 30 && daysPassCounter > 0) {
                                System.out.println("Статистика за последние " + days + " дней");
                                SortedMap<LocalDateTime, HabitStatus> habitMap =
                                        historyManager.getHabitStatisticsForGivenPeriod(habit, days);
                                for (LocalDateTime date : habitMap.keySet()) {
                                    System.out.println(date.format(onlyDateFormat) + " - " + habitMap.get(date));
                                }
                            } else if (daysPassCounter == 0) {
                                System.out.println("Еще не прошел ни 1 день. Начните новый день из меню" +
                                        "5 - Начать новый день");
                            } else {
                                System.out.println("неверный период");
                            }
                            break;
                        case 9:
                            System.out.println("Введите кол-во дней период статистики от 1 до 365");
                            days = scanner.nextInt();
                            if (days > 0 && days <= 365 && daysPassCounter > 0) {
                                int percent = historyManager.
                                        getSuccessPercentOfUsersFinishedHabitsForGivenPeriod(user, days);
                                System.out.println("Успешный % выполнения привычек за последние " +
                                        days + " дней = " + percent + "%");
                            } else if (daysPassCounter == 0) {
                                System.out.println("Еще не прошел ни 1 день. Начните новый день из меню" +
                                        "5 - Начать новый день");
                            } else {
                                System.out.println("неверный период");
                            }
                            break;
                        case 10:
                            System.out.println("Введите id привычки для удаления");
                            id = scanner.nextInt();
                            habitManager.deleteHabitById(user, id);
                            break;
                        case 11:
                            user = null;
                            System.out.println("Вы вышли из системы!");
                            break;
                        case 0:
                            break;
                        default:
                            System.out.println("Такой команды нет, введите команду снова.");
                            break;
                    }
                    break;
                case 5:
                    userManager.resetAllDoneDailyHabits(user);
                    System.out.println("done!");
                    daysPassCounter++;
                    break;
                case 6:
                    userManager.resetAllDoneWeeklyHabits(user);
                    System.out.println("done!");
                    daysPassCounter++;
                    break;
                case 7:
                    user = null;
                    System.out.println("Введите ваше имя");
                    name = scanner.next();
                    admin = new Admin(name);
                    adminManager.createAdmin(admin);
                    printMenuForAdmin();
                    command = scanner.nextInt();
                    while (command != 6) {
                        switch (command) {
                            case 1:
                                System.out.println("Введите email (login) пользователя для блокировки");
                                email = scanner.next();
                                adminManager.blockUser(email);
                                break;
                            case 2:
                                System.out.println("Введите email (login) пользователя для разблокировки");
                                email = scanner.next();
                                adminManager.unblockUser(email);
                                break;
                            case 3:
                                System.out.println("Введите email (login) пользователя для удаления из базы");
                                email = scanner.next();
                                adminManager.deleteUser(email);
                                break;
                            case 4:
                                System.out.println(adminManager.getAllUsers());
                                break;
                            case 5:
                                System.out.println("Введите email (login) пользователя для " +
                                        "получения списка его привычек");
                                email = scanner.next();
                                adminManager.getAllUserHabits(email).forEach(System.out::println);
                                break;
                            default:
                                break;
                        }
                        printMenuForAdmin();
                        command = scanner.nextInt();
                    }
                    admin = null;
                    System.out.println("Вы вышли из админ панели!");
                    break;
                default:
                    System.out.println("Такой команды нет, введите команду снова.");
                    break;
            }
            printMenu();
            command = scanner.nextInt();
        }
    }


    private void printMenu() {
        System.out.println("Привет!=) Что хочешь сделать?");
        System.out.println("1 - Регистрация");
        System.out.println("2 - Авторизация");
        System.out.println("3 - Редактировать профиль пользователя");
        System.out.println("4 - Статистика и редактирование привычек пользователя");
        System.out.println("5 - Начать новый день (обнулит все выполненные ежедневные привычки");
        System.out.println("6 - Начать новую неделю (обнулит все еженедельные и ежедневные выполненные привычки");
        System.out.println("7 - Админ панель");
        System.out.println("0 - Выйти из приложения.");
    }

    private void printMenuForUserUpdate() {
        System.out.println("Выбери команду ниже");
        System.out.println("1 - Поменять имя");
        System.out.println("2 - Поменять пароль");
        System.out.println("3 - Поменять почту");
        System.out.println("4 - Удалить аккаунт");
        System.out.println("5 - Выйти из аккаунта");
        System.out.println("0 - Назад");
    }

    private void printMenuForHabitsUpdateAndStatistics() {
        System.out.println("Выбери команду ниже");
        System.out.println("1 - Создать привычку");
        System.out.println("2 - Показать все привычки");
        System.out.println("3 - Редактировать привычку (в том числе СТАТУС привычки)");
        System.out.println("4 - Установить статус finished для всех привычек");
        System.out.println("5 - Показать все привычки определенного статуса выполнения");
        System.out.println("6 - Показать все привычки отсортированные по статусу выполнения");
        System.out.println("7 - Показать все привычки отсортированные по дате создания");
        System.out.println("8 - Показать статистику выполнения привычки за указанный период");
        System.out.println("9 - Показать статистику процент успешного выполнения привычек за определенный период");
        System.out.println("10 - Удалить привычку");
        System.out.println("11 - Выйти из аккаунта");
        System.out.println("0 - Назад");
    }

    private void printMenuForAdmin() {
        System.out.println("Выбери команду ниже");
        System.out.println("1 - Заблокировать пользователя");
        System.out.println("2 - Разблокировать пользователя");
        System.out.println("3 - Удалить пользователя");
        System.out.println("4 - Получить список пользователй");
        System.out.println("5 - Получить список привычек пользователя");
        System.out.println("6 - Выйти из аккаунта / выйти из админ панели");
    }

    private HabitType getHabitTypeByNumber(String number) {
        if (number.equals("1")) {
            return HabitType.DAILY;
        } else if (number.equals("2")) {
            return HabitType.WEEKLY;
        } else {
            return HabitType.CUSTOM;
        }
    }

    private HabitStatus getHabitStatusByNumber(String number) {
        if (number.equals("1")) {
            return HabitStatus.NEW;
        } else if (number.equals("2")) {
            return HabitStatus.IN_PROGRESS;
        } else {
            return HabitStatus.FINISHED;
        }
    }
}
