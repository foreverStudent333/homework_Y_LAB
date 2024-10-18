package habitsTrackApp.model;

import java.time.LocalDateTime;

/**
 * Класс описывающий сущность привычка
 *
 * @author Mihail Harhan
 */
public class Habit {
    private Integer id;
    private Integer userOwnerId;
    private String name;
    private String description;
    private HabitType habitType;
    private HabitStatus habitStatus;
    private LocalDateTime startDate;
    private int streak;

    public Habit(String name, String description, HabitType habitType, Integer userOwnerId) {
        this.name = name;
        this.description = description;
        this.habitType = habitType;
        this.habitStatus = HabitStatus.NEW;
        this.startDate = LocalDateTime.now();
        this.streak = 0;
        this.userOwnerId = userOwnerId;
    }

    public Habit(String name, String description, Integer userOwnerId) {
        this.name = name;
        this.description = description;
        this.habitType = HabitType.CUSTOM;
        this.habitStatus = HabitStatus.NEW;
        this.startDate = LocalDateTime.now();
        this.streak = 0;
        this.userOwnerId = userOwnerId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public HabitType getHabitType() {
        return habitType;
    }

    public void setHabitType(HabitType habitType) {
        this.habitType = habitType;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public LocalDateTime getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDateTime startDate) {
        this.startDate = startDate;
    }

    public HabitStatus getHabitStatus() {
        return habitStatus;
    }

    public void setHabitStatus(HabitStatus habitStatus) {
        this.habitStatus = habitStatus;
    }

    public int getStreak() {
        return streak;
    }

    public void setStreak(int streak) {
        this.streak = streak;
    }

    public Integer getUserOwnerId() {
        return userOwnerId;
    }

    public void setUserOwnerId(Integer userOwnerId) {
        this.userOwnerId = userOwnerId;
    }

    @Override
    public String toString() {
        return "Habit{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", habitType=" + habitType +
                ", habitStatus=" + habitStatus +
                ", startDate=" + startDate +
                ", streak=" + streak +
                '}';
    }
}
