package habitsTrackApp.services;

import habitsTrackApp.model.Habit;
import habitsTrackApp.model.HabitStatus;
import habitsTrackApp.model.User;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.SortedMap;
import java.util.TreeMap;

/**
 * Класс описывающий логику работы с историей прогресса привычек пользователей.
 * Включает в себе реализацию crud для истории привычек и хранение истории в памяти приложения
 * ключ(привычка) - значение(мапа история выполнения привычки)
 *
 * @author Mihail Harhan "mihaillKHn@yandex.ru"
 */
public class InMemoryHistoryManager implements HistoryManager {
    /**
     * мапа для хранения данных по прогрессу выполнения привычки.
     * Отсортированная {@code TreeMap} по ключу {@code LocalDateTime} дате привычки, значение -
     * статус привычки в дату равную ключу
     */
    private TreeMap<LocalDateTime, HabitStatus> habitProgressHistory;
    /**
     * мапа по ключу {@code Habit} для хранения всех данных о прогрессе выполнения привычек
     */
    private final HashMap<Habit, TreeMap<LocalDateTime, HabitStatus>> habitsProgressHistoryMap;
    private HashMap<Habit, TreeMap<LocalDateTime, HabitStatus>> habitsProgressHistoryMapByUserId;

    public InMemoryHistoryManager() {
        habitProgressHistory = new TreeMap<>();
        habitsProgressHistoryMap = new HashMap<>();
        habitsProgressHistoryMapByUserId = new HashMap<>();
    }

    /**
     * Создает историю прогресса выполнения привычки
     * @param habit привычка которую надо занести в историю
     */
    @Override
    public void createHabitHistory(Habit habit) {
        TreeMap<LocalDateTime, HabitStatus> habitProgressHistory = new TreeMap<>();
        habitProgressHistory.put(habit.getStartDate(), habit.getHabitStatus());
        habitsProgressHistoryMap.put(habit, habitProgressHistory);
    }

    /**
     * Обновляет историю прогресса выполнения привычки со статусом {@code HabitType.DAILY}
     * @param habit привычка у которой нужно обновить историю прогресса выполнения
     */
    @Override
    public void updateDailyHabitHistory(Habit habit) {
        habitProgressHistory = habitsProgressHistoryMap.get(habit);
        if (habitProgressHistory.size() == 1) {
            habitProgressHistory.put(habit.getStartDate().plusSeconds(1), habit.getHabitStatus());
            habitsProgressHistoryMap.put(habit, habitProgressHistory);
        } else {
            habitProgressHistory.put(habitProgressHistory.lastKey().plusDays(1), habit.getHabitStatus());
            habitsProgressHistoryMap.put(habit, habitProgressHistory);
        }
    }

    /**
     * Обновляет историю прогресса выполнения привычки со статусом {@code HabitType.WEEKLY}
     * @param habit привычка у которой нужно обновить историю прогресса выполнения
     */
    @Override
    public void updateWeeklyHabitHistory(Habit habit) {
        habitProgressHistory = habitsProgressHistoryMap.get(habit);
        if (habitProgressHistory.size() == 1) {
            habitProgressHistory.put(habit.getStartDate().plusSeconds(1), habit.getHabitStatus());
            habitsProgressHistoryMap.put(habit, habitProgressHistory);
        } else {
            habitProgressHistory.put(habitProgressHistory.lastKey().plusDays(7), habit.getHabitStatus());
            habitsProgressHistoryMap.put(habit, habitProgressHistory);
        }
    }

    /**
     * Удаляет историю прогресса выполнения привычки
     * @param habit историю которой надо удалить
     */
    @Override
    public void deleteHabitFromHistory(Habit habit) {
        habitsProgressHistoryMap.remove(habit);
    }

    /**
     * Удаляет историю прогресса выполнения всех привычек пользователя
     * @param user историю привычек которого надо удалить
     */
    @Override
    public void deleteAllUserHabitsFromHistory(User user) {
        habitsProgressHistoryMap.entrySet().removeIf(
                entry -> (user.getId().equals(entry.getKey().getUserOwnerId()))
        );
    }

    /**
     * Фильтрует историю выполнения привычки за последние {@code days}
     *
     * @param habit историю которой фильтрует метод
     * @param days за прошедшее кол-во дней будет выдана история
     * @return возвращает отфильтрованную мапу истории выполнения {@code habit} за последние {@code days}
     */
    @Override
    public SortedMap<LocalDateTime, HabitStatus> getHabitStatisticsForGivenPeriod(Habit habit, Integer days) {
        habitProgressHistory = habitsProgressHistoryMap.get(habit);
        LocalDateTime startFromDay = habitProgressHistory.lastKey().minusDays(days);
        return habitProgressHistory.tailMap(startFromDay);
    }

    /**
     * Метод находит процент завершенных привычек среди всех привычек за последние {@code days}
     * и возвращает этот процент.
     * @param user у которого нужно найти процент завершенных привычек
     * @param days за какое кол-во прошедших дней нужно выдавать статистику
     * @return {@code Integer} число - процент завершенных привычек среди
     */
    @Override
    public Integer getSuccessPercentOfUsersFinishedHabitsForGivenPeriod(User user, Integer days) {
        int resultPercent = 0;
        LocalDateTime startFromDay;
        habitsProgressHistoryMapByUserId = getHabitsProgressHistoryMapByUserId(user.getId());
        for (Habit habit : habitsProgressHistoryMapByUserId.keySet()) {
            habitProgressHistory = habitsProgressHistoryMapByUserId.get(habit);
            startFromDay = habitProgressHistory.lastKey().minusDays(days);
            LocalDateTime habitCreationDateTime = habitProgressHistory.firstKey();
            //проверяю не задан ли поиск раньше чем была создана привычка по времени
            //если раньше то убираю первое вхождение привычки в историю т.к. это вхождение при создании привычки
            if (startFromDay.isBefore(habitCreationDateTime) ||
                    startFromDay.isEqual(habitCreationDateTime)) {
                resultPercent += getSuccessPercentOfFinishedHabit(habitProgressHistory.
                        tailMap(habitCreationDateTime.plusSeconds(1)));
            } else {
                resultPercent += getSuccessPercentOfFinishedHabit(habitProgressHistory.tailMap(startFromDay));
            }
        }
        return resultPercent / habitsProgressHistoryMapByUserId.size();
    }

    private HashMap<Habit, TreeMap<LocalDateTime, HabitStatus>> getHabitsProgressHistoryMapByUserId(Integer id) {
        HashMap<Habit, TreeMap<LocalDateTime, HabitStatus>> habitsProgressHistoryMapByUserId = new HashMap<>();
        habitsProgressHistoryMap.forEach((habit, treeMap) -> {
            if (habit.getUserOwnerId().equals(id)) {
                habitsProgressHistoryMapByUserId.put(habit, treeMap);
            }
        });
        return habitsProgressHistoryMapByUserId;
    }

    private Integer getSuccessPercentOfFinishedHabit(SortedMap<LocalDateTime, HabitStatus> habits) {
        int percentOfFinishedHabits = 0;
        for (LocalDateTime time : habits.keySet()) {
            if (habits.get(time).equals(HabitStatus.FINISHED)) {
                percentOfFinishedHabits += 100;
            }
        }
        return percentOfFinishedHabits / habits.size();
    }

    /**
     * @param habit историю выполнения которой нужно вернуть
     * @return Возвращает историю прогресса выполнения привычки
     */
    public TreeMap<LocalDateTime, HabitStatus> getHabitProgressHistory(Habit habit) {
        return habitsProgressHistoryMap.get(habit);
    }

    public HashMap<Habit, TreeMap<LocalDateTime, HabitStatus>> getHabitsProgressHistoryMap() {
        return habitsProgressHistoryMap;
    }
}
