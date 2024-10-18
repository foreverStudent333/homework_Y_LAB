package habitsTrackApp.services;

import habitsTrackApp.model.Habit;
import habitsTrackApp.model.HabitStatus;
import habitsTrackApp.model.User;

import java.time.LocalDateTime;
import java.util.SortedMap;

/**
 * Интерфейс описывающий логику работы сохранения истории всех привычек
 *
 * @author Mihail Harhan "mihaillKHn@yandex.ru"
 */
public interface HistoryManager {
    void createHabitHistory(Habit habit);

    void updateDailyHabitHistory(Habit habit);

    void updateWeeklyHabitHistory(Habit habit);

    void deleteHabitFromHistory(Habit habit);

    void deleteAllUserHabitsFromHistory(User user);

    SortedMap<LocalDateTime, HabitStatus> getHabitStatisticsForGivenPeriod(Habit habit, Integer days);

    Integer getSuccessPercentOfUsersFinishedHabitsForGivenPeriod(User user, Integer days);
}
