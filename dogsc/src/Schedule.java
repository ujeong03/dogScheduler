import java.util.Date;
/**
 * Schedule 클래스는 캘린더 일정을 나타내는 데이터 모델입니다.
 */
public class Schedule {
    private int id;
    private String text;
    private Date date;
    private boolean isReminder;
    private boolean isHomework;
    /**
     * Schedule 클래스의 생성자입니다.
     *
     * @param id        일정 고유 식별자
     * @param text      일정 내용
     * @param date      일정 날짜
     * @param isReminder 리마인더 여부
     * @param isHomework 과제 여부
     */
    public Schedule(int id, String text, Date date, boolean isReminder, boolean isHomework) {
        this.id = id;
        this.text = text;
        this.date = date;
        this.isReminder = isReminder;
        this.isHomework = isHomework;
    }

    /**
     * 일정 고유 식별자를 반환합니다.
     *
     * @return 일정 고유 식별자
     */
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
    /**
     * 일정 내용을 반환합니다.
     *
     * @return 일정 내용
     */
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
    /**
     * 리마인더 여부를 설정합니다.
     *
     * @return 리마인더 여부
     */
    public boolean isReminder() {
        return isReminder;
    }

    public void setReminder(boolean reminder) {
        isReminder = reminder;
    }
    /**
     * 과제 여부를 반환합니다.
     *
     * @return 과제 여부
     */
    public boolean isHomework() {
        return isHomework;
    }

    public void setHomework(boolean homework) {
        isHomework = homework;
    }
}
