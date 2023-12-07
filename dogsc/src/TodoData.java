import java.util.Date;

/**
 * todoDB의 데이터 정보를 이용하기 위한 객체
 * @author ujeong
 */
public class TodoData {
    /**정렬을 위한 index*/
    private int orderIndex; //정렬을 위한 index
    /**투두 날짜*/
    private String todoDate; //투두 날짜
    /**투두 내용*/
    private String todoText; //투두 내용
    /**달성여부*/
    private int completed; //달성 여부

    /**
     * orderIndex를 저장하기 위한 메서드
      * @return orderIndex
     */
    public int getOrderIndex() {
        return orderIndex;
    }

    /**
     * orderIndex를 설정
     * @param orderIndex
     */
    public void setOrderIndex(int orderIndex) {
        this.orderIndex = orderIndex;
    }

    /**
     * todoDate를 저장하기 위한 메서드
     * @return todoDate
     */
    public String getTodoDate() {
        return todoDate;
    }

    /**
     * todoDate를 설정하기 위한 메서드
     * @param todoDate
     */
    public void setTodoDate(String todoDate) {
        this.todoDate = todoDate;
    }

    /**
     * todoText를 설정하기 위한 메서드
     * @param todoText
     */
    public void setTodoText(String todoText) {
        this.todoText = todoText;
    }

    /**
     * 달성 여부를 확인하는 매서드
     * @return 달성 여부
     */
    public int isCompleted() {
        return completed;
    }

    /**
     * 달성 여부를 설정하는 메서드
     * @param completed
     */
    public void setCompleted(int completed) {
        this.completed = completed;
    }
}
