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

class InMemoryUserManagerTest {
    private InMemoryUserManager userManager;
    private InMemoryHabitsManager habitsManager;
    private InMemoryHistoryManager historyManager;
    User user1;
    User user2;
    User user3;
    String email1 = "Doe@gmail.com";
    String email2 = "ez@mail.ru";
    String password1 = "123456789";
    String password2 = "1q2w3e4r";

    @BeforeEach
    void setUp() {
        historyManager = new InMemoryHistoryManager();
        habitsManager = new InMemoryHabitsManager(historyManager);
        userManager = new InMemoryUserManager(habitsManager, historyManager);
        user1 = new User("123456789", email1);
        //user2 = неверный юзер т.к. неверный email
        user2 = new User("sdfl_3km3", "incorrect");
        user3 = new User("1q2w3e4r", email2);
        userManager.addNewUser(user1);
    }

    @AfterEach
    void tearDown() {
    }

    @Test
    void addNewUser() {
        userManager.addNewUser(user1);
        userManager.addNewUser(user2);
        userManager.addNewUser(user3);
        assertThat(userManager.getUsers().size()).isEqualTo(2);
    }

    @Test
    void getUserByEmail() {
        assertThat(userManager.getUserByEmail(email1)).isEqualTo(user1);
        assertThat(userManager.getUserByEmail("notCorrectEmail")).isNull();
        assertThat(userManager.getUserByEmail("notInBaseEmail@mail.ru")).isNull();
    }

    @Test
    void authorizeUser() {
        assertThat(userManager.authorizeUser(email1, password1)).isEqualTo(user1);
        assertThat(userManager.authorizeUser("notCorrectEmail", password1)).isNull();
        assertThat(userManager.authorizeUser(email1, "NotCorrectPas")).isNull();
    }

    @Test
    void deleteUser() {
        userManager.deleteUser(user1);
        assertThat(userManager.getUsers().size()).isEqualTo(0);
    }

    @Test
    void changePassword() {
        userManager.changePassword(user1, password1, "newOne");
        assertThat(user1.getPassword()).isEqualTo("newOne");
        userManager.changePassword(user1, "incorrectOldPas", "newTwo");
        assertThat(user1.getPassword()).isEqualTo("newOne");
    }

    @Test
    void changeName() {
        userManager.changeName(user1, "newName");
        assertThat(user1.getName()).isEqualTo("newName");
    }

    @Test
    void changeEmail() {
        userManager.changeEmail(user1, "newEmail");
        assertThat(user1.getEmail()).isEqualTo("newEmail");
    }

    @Test
    void resetAllDoneDailyHabits() {
        habitsManager.addNewHabit(user1, new Habit("1", "1", HabitType.DAILY, 1));
        habitsManager.addNewHabit(user1, new Habit("2", "2", HabitType.DAILY,1));
        habitsManager.addNewHabit(user1, new Habit("3", "3", HabitType.DAILY,1));
        habitsManager.setEveryHabitStatusFinished(user1);
        userManager.resetAllDoneDailyHabits(user1);
        ArrayList<Habit> habits = habitsManager.getAllUserHabits(user1);
        habits.forEach(habit -> {
            assertThat(habit.getHabitStatus()).isEqualTo(HabitStatus.IN_PROGRESS);
        });
    }

    @Test
    void resetAllDoneWeeklyHabits() {
        habitsManager.addNewHabit(user1, new Habit("1", "1", HabitType.DAILY, 1));
        habitsManager.addNewHabit(user1, new Habit("2", "2", HabitType.WEEKLY,1));
        habitsManager.addNewHabit(user1, new Habit("3", "3", HabitType.WEEKLY,1));
        habitsManager.setEveryHabitStatusFinished(user1);
        userManager.resetAllDoneWeeklyHabits(user1);
        ArrayList<Habit> habits = habitsManager.getAllUserHabits(user1);
        habits.forEach(habit -> {
            assertThat(habit.getHabitStatus()).isEqualTo(HabitStatus.IN_PROGRESS);
        });
    }
}