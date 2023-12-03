public class TodoItem {
    private String todoText;
    private int isCompleted;
    private int orderIndex;

    public TodoItem(String todoText, int isCompleted, int orderIndex) {
        this.todoText = todoText;
        this.isCompleted = isCompleted;
        this.orderIndex = orderIndex;
    }

    // Getter 및 Setter 메서드
    public String getTodoText() {
        return todoText;
    }

    public void setTodoText(String todoText) {
        this.todoText = todoText;
    }

    public int getIsCompleted() {
        return isCompleted;
    }

    public void setIsCompleted(int isCompleted) {
        this.isCompleted = isCompleted;
    }

    public int getOrderIndex() {
        return orderIndex;
    }

    public void setOrderIndex(int orderIndex) {
        this.orderIndex = orderIndex;
    }
}

