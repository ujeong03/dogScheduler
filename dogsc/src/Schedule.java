import java.util.Date;

public class Schedule {
    private int id;
    private String text;
    private Date date;
    private boolean isReminder;
    private boolean isHomework;

    public Schedule(int id, String text, Date date, boolean isReminder, boolean isHomework) {
        this.id = id;
        this.text = text;
        this.date = date;
        this.isReminder = isReminder;
        this.isHomework = isHomework;
    }

    // Getters and Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public boolean isReminder() {
        return isReminder;
    }

    public void setReminder(boolean reminder) {
        isReminder = reminder;
    }

    public boolean isHomework() {
        return isHomework;
    }

    public void setHomework(boolean homework) {
        isHomework = homework;
    }
}
