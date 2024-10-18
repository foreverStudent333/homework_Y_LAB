package habitsTrackApp.services;

import habitsTrackApp.model.Admin;
import habitsTrackApp.model.Habit;
import habitsTrackApp.model.HabitType;
import habitsTrackApp.model.User;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class InMemoryAdminManagerTest {
    private InMemoryUserManager userManager;
    private InMemoryHabitsManager habitsManager;
    private InMemoryAdminManager adminManager;
    Admin admin;
    String name;
    User user1;
    User user2;
    String email1 = "Doe@gmail.com";

    @BeforeEach
    void setUp() {
        InMemoryHistoryManager inMemoryHistoryManager = new InMemoryHistoryManager();
        habitsManager = new InMemoryHabitsManager(inMemoryHistoryManager);
        userManager = new InMemoryUserManager(habitsManager, inMemoryHistoryManager);
        adminManager = new InMemoryAdminManager(userManager, habitsManager);
        name = "AdminFirst";
        admin = new Admin(name);
        user1 = new User("123456789", email1);
        user2 = new User("sdfl_3km3", "em333@gmail.com");
    }

    @AfterEach
    void tearDown() {
    }

    @Test
    void createAdmin() {
        adminManager.createAdmin(admin);
        assertThat(adminManager.getAdminsById().size()).isEqualTo(1);
        assertThat(adminManager.getAdminsById().get(1).getName()).isEqualTo(name);
    }

    @Test
    void deleteAdmin() {
        adminManager.createAdmin(admin);
        adminManager.deleteAdmin(admin.getId());
        assertThat(adminManager.getAdminsById().size()).isEqualTo(0);
    }

    @Test
    void blockUser() {
        userManager.addNewUser(user1);
        assertThat(userManager.getUserByEmail(email1).isBlocked()).isFalse();
        adminManager.blockUser(email1);
        assertThat(userManager.getUserByEmail(email1).isBlocked()).isTrue();
    }

    @Test
    void unblockUser() {
        userManager.addNewUser(user1);
        user1.setBlocked(true);
        adminManager.unblockUser(email1);
        assertThat(userManager.getUserByEmail(email1).isBlocked()).isFalse();
    }

    @Test
    void deleteUser() {
        userManager.addNewUser(user1);
        assertThat(userManager.getUserByEmail(email1)).isNotNull();
        adminManager.deleteUser(email1);
        assertThat(userManager.getUserByEmail(email1)).isNull();
    }

    @Test
    void getAllUsers() {
        userManager.addNewUser(user1);
        userManager.addNewUser(user2);
        assertThat(adminManager.getAllUsers().size()).isEqualTo(2);
        assertThat(adminManager.getAllUsers().get(email1)).isEqualTo(user1);
    }

    @Test
    void getAllUserHabits() {
        userManager.addNewUser(user1);
        habitsManager.addNewHabit(user1, new Habit("1", "1", HabitType.DAILY, 1));
        habitsManager.addNewHabit(user1, new Habit("2", "2", HabitType.WEEKLY,1));
        habitsManager.addNewHabit(user1, new Habit("3", "3", HabitType.WEEKLY,1));
        assertThat(adminManager.getAllUserHabits(email1).size()).isEqualTo(3);
        assertThat(adminManager.getAllUserHabits(email1).getFirst().getId()).isEqualTo(1);
    }
}