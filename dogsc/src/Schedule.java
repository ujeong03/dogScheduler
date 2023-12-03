import java.util.Date;

public class Schedule {
    private int id; // 일정의 고유 ID
    private String text; // 일정의 내용
    private String date; // 일정의 날짜
    private boolean reminder; // 리마인더 여부
    private boolean homework; // 과제 여부

    // 생성자
    public Schedule(int id, String text, String date, boolean reminder, boolean homework) {
        this.id = id;
        this.text = text;
        this.date = date;
        this.reminder = reminder;
        this.homework = homework;
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

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public boolean isReminder() {
        return reminder;
    }

    public void setReminder(boolean reminder) {
        this.reminder = reminder;
    }

    public boolean isHomework() {
        return homework;
    }

    public void setHomework(boolean homework) {
        this.homework = homework;
    }
}
