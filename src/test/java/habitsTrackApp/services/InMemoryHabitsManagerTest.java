package habitsTrackApp.services;

import habitsTrackApp.model.Habit;
import habitsTrackApp.model.HabitStatus;
import habitsTrackApp.model.HabitType;
import habitsTrackApp.model.User;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static org.assertj.core.api.Assertions.assertThat;

class InMemoryHabitsManagerTest {
    private InMemoryUserManager userManager;
    private InMemoryHabitsManager habitsManager;
    private InMemoryAdminManager adminManager;
    User user1;
    String email1 = "Doe@gmail.com";
    Habit habit1;
    Habit habit2;
    Habit habit3;

    @BeforeEach
    void setUp() {
        InMemoryHistoryManager inMemoryHistoryManager = new InMemoryHistoryManager();
        habitsManager = new InMemoryHabitsManager(inMemoryHistoryManager);
        userManager = new InMemoryUserManager(habitsManager, inMemoryHistoryManager);
        user1 = new User("123456789", email1);
        userManager.addNewUser(user1);
        habit1 = new Habit("1", "1", HabitType.DAILY, 1);
        habit2 = new Habit("2", "2", HabitType.WEEKLY, 1);
        habit3 = new Habit("3", "3", HabitType.DAILY, 1);
    }

    @AfterEach
    void tearDown() {
    }

    @Test
    void addNewHabit() {
        habitsManager.addNewHabit(user1, habit1);
        assertThat(habitsManager.getAllUserHabits(user1).size()).isEqualTo(1);
        habitsManager.addNewHabit(user1, habit2);
        habitsManager.addNewHabit(user1, habit3);
        assertThat(habitsManager.getAllUserHabits(user1).size()).isEqualTo(3);
        assertThat(habitsManager.getHabitById(user1,3)).isEqualTo(habit3);
    }

    @Test
    void deleteHabitById() {
        habitsManager.addNewHabit(user1, habit1);
        habitsManager.addNewHabit(user1, habit2);
        habitsManager.deleteHabitById(user1,1);
        assertThat(habitsManager.getAllUserHabits(user1).size()).isEqualTo(1);
        assertThat(habitsManager.getHabitById(user1,2)).isEqualTo(habit2);
    }

    @Test
    void updateHabitName() {
        habitsManager.addNewHabit(user1, habit1);
        habitsManager.updateHabitName(user1, habit1, "New Name");
        assertThat(habit1.getName()).isEqualTo("New Name");
    }

    @Test
    void updateHabitDescription() {
        habitsManager.addNewHabit(user1, habit1);
        habitsManager.updateHabitDescription(user1, habit1, "New Description");
        assertThat(habit1.getDescription()).isEqualTo("New Description");
    }

    @Test
    void updateHabitStatus() {
        habitsManager.addNewHabit(user1, habit1);
        assertThat(habit1.getHabitStatus()).isEqualTo(HabitStatus.NEW);
        habitsManager.updateHabitStatus(user1,habit1, HabitStatus.FINISHED);
        assertThat(habit1.getHabitStatus()).isEqualTo(HabitStatus.FINISHED);
    }

    @Test
    void setEveryHabitStatusFinished() {
        habitsManager.addNewHabit(user1, habit1);
        habitsManager.addNewHabit(user1, habit2);
        habitsManager.addNewHabit(user1, habit3);
        habitsManager.setEveryHabitStatusFinished(user1);
        ArrayList<Habit> habits = habitsManager.getAllUserHabits(user1);
        habits.forEach(habit -> {
            assertThat(habit.getHabitStatus()).isEqualTo(HabitStatus.FINISHED);
        });
    }

    @Test
    void getHabitById() {
        habitsManager.addNewHabit(user1, habit1);
        habitsManager.addNewHabit(user1, habit2);
        assertThat(habitsManager.getHabitById(user1,2)).isEqualTo(habit2);
    }

    @Test
    void getAllUserHabits() {
        habitsManager.addNewHabit(user1, habit1);
        habitsManager.addNewHabit(user1, habit2);
        habitsManager.addNewHabit(user1, habit3);
        assertThat(habitsManager.getAllUserHabits(user1).size()).isEqualTo(3);
    }

    @Test
    void getUserHabitsWithACertainStatus() {
        habitsManager.addNewHabit(user1, habit1);
        habitsManager.addNewHabit(user1, habit2);
        habitsManager.addNewHabit(user1, habit3);
        habitsManager.setEveryHabitStatusFinished(user1);
        habit1.setHabitStatus(HabitStatus.IN_PROGRESS);
        ArrayList<Habit> habits = habitsManager.getUserHabitsWithACertainStatus(user1,HabitStatus.FINISHED);
        habits.forEach(habit -> {
            assertThat(habit.getHabitStatus()).isEqualTo(HabitStatus.FINISHED);
        });
        assertThat(habits.size()).isEqualTo(2);
    }

    @Test
    void getUserHabitsFilteredByStatus() {
        habitsManager.addNewHabit(user1, habit1);
        habitsManager.addNewHabit(user1, habit2);
        habitsManager.addNewHabit(user1, habit3);
        habit3.setHabitStatus(HabitStatus.FINISHED);
        habit1.setHabitStatus(HabitStatus.FINISHED);
        habit2.setHabitStatus(HabitStatus.IN_PROGRESS);
        ArrayList<Habit> habits = habitsManager.getUserHabitsFilteredByStatus(user1);
        assertThat(habits.getFirst().getHabitStatus()).isEqualTo(HabitStatus.IN_PROGRESS);
        assertThat(habits.getLast().getHabitStatus()).isEqualTo(HabitStatus.FINISHED);
        assertThat(habits.size()).isEqualTo(3);
    }

    @Test
    void getAllUserHabitsFilteredByCreationDate() {
        habitsManager.addNewHabit(user1, habit1);
        habitsManager.addNewHabit(user1, habit2);
        habitsManager.addNewHabit(user1, habit3);
        ArrayList<Habit> habits = habitsManager.getAllUserHabitsFilteredByCreationDate(user1);
        assertThat(habits.getFirst()).isEqualTo(habit1);
        assertThat(habits.getLast()).isEqualTo(habit3);
        assertThat(habits.size()).isEqualTo(3);
    }
}