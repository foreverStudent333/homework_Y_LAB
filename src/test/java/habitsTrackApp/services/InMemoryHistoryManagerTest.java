package habitsTrackApp.services;

import habitsTrackApp.model.Habit;
import habitsTrackApp.model.HabitStatus;
import habitsTrackApp.model.HabitType;
import habitsTrackApp.model.User;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.TreeMap;

import static org.assertj.core.api.Assertions.assertThat;

class InMemoryHistoryManagerTest {
    private InMemoryHistoryManager historyManager;
    private TreeMap<LocalDateTime, HabitStatus> habitProgressHistory;
    private HashMap<Habit, TreeMap<LocalDateTime, HabitStatus>> habitsProgressHistoryMap;
    private HashMap<Habit, TreeMap<LocalDateTime, HabitStatus>> habitsProgressHistoryMapByUserId;
    User user1;
    Habit habit1;
    Habit habit2;
    Habit habit3;

    @BeforeEach
    void setUp() {
        historyManager = new InMemoryHistoryManager();
        habitProgressHistory = new TreeMap<>();
        habitsProgressHistoryMap = new HashMap<>();
        habitsProgressHistoryMapByUserId = new HashMap<>();
        user1 = new User("123456789", "email1@bk.ru");
        habit1 = new Habit("1", "1", HabitType.DAILY, 1);
        habit2 = new Habit("2", "2", HabitType.WEEKLY, 1);
        habit3 = new Habit("3", "3", HabitType.DAILY, 1);
    }

    @AfterEach
    void tearDown() {
    }

    @Test
    void createHabitHistory() {
        historyManager.createHabitHistory(habit1);
        habit1.setHabitStatus(HabitStatus.FINISHED);
        habit1.setStartDate(habit1.getStartDate().plusDays(1));
        historyManager.createHabitHistory(habit1);
        System.out.println(historyManager.getHabitProgressHistory(habit1));
    }

    @Test
    void updateDailyHabitHistory() {
        historyManager.createHabitHistory(habit1);
        habit1.setHabitStatus(HabitStatus.FINISHED);
        habit1.setStartDate(habit1.getStartDate().plusDays(1));
        historyManager.updateDailyHabitHistory(habit1);
        assertThat(historyManager.getHabitProgressHistory(habit1).size()).isEqualTo(2);
        assertThat(historyManager.getHabitProgressHistory(habit1).get(habit1.getStartDate().plusSeconds(1))).
                isEqualTo(HabitStatus.FINISHED);
    }

    @Test
    void updateWeeklyHabitHistory() {
        historyManager.createHabitHistory(habit2);
        habit2.setHabitStatus(HabitStatus.FINISHED);
        habit2.setStartDate(habit2.getStartDate().plusDays(1));
        historyManager.updateWeeklyHabitHistory(habit2);
        assertThat(historyManager.getHabitProgressHistory(habit2).size()).isEqualTo(2);
        assertThat(historyManager.getHabitProgressHistory(habit2).get(habit2.getStartDate().plusSeconds(1))).
                isEqualTo(HabitStatus.FINISHED);
    }

    @Test
    void deleteHabitFromHistory() {
        historyManager.createHabitHistory(habit1);
        historyManager.createHabitHistory(habit2);
        historyManager.createHabitHistory(habit3);
        historyManager.deleteHabitFromHistory(habit2);
        assertThat(historyManager.getHabitProgressHistory(habit2)).isNull();
        assertThat(historyManager.getHabitsProgressHistoryMap().size()).isEqualTo(2);
    }

    @Test
    void deleteAllUserHabitsFromHistory() {
        user1.setId(1);
        historyManager.createHabitHistory(habit1);
        historyManager.createHabitHistory(habit2);
        historyManager.createHabitHistory(habit3);
        historyManager.deleteAllUserHabitsFromHistory(user1);
        assertThat(historyManager.getHabitProgressHistory(habit1)).isNull();
        assertThat(historyManager.getHabitsProgressHistoryMap()).isEmpty();
    }

    @Test
    void getHabitStatisticsForGivenPeriod() {
    }

    @Test
    void getSuccessPercentOfUsersFinishedHabitsForGivenPeriod() {
    }
}