import javax.swing.*;
import java.awt.*;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.*;
import java.util.stream.Collectors;

public class CalendarWindow extends JFrame {
    private Calendar calendar;
    private JLabel monthLabel;
    private JPanel calendarPanel;
    private JButton prevButton, nextButton;
    private final String[] dayNames = {"일", "월", "화", "수", "목", "금", "토"};
    private Map<Integer, List<String>> dailySchedules = new HashMap<>();
    private CalendarDBConnection dbConnection;

    public CalendarWindow() {
        dbConnection = new CalendarDBConnection(); // 데이터베이스 연결 객체 생성
        calendar = Calendar.getInstance(Locale.getDefault());
        setTitle("캘린더");
        setSize(800, 1000);
        setLayout(new BorderLayout());
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        JPanel monthPanel = new JPanel();
        monthPanel.setLayout(new BoxLayout(monthPanel, BoxLayout.X_AXIS));

        prevButton = new JButton("<");
        prevButton.addActionListener(e -> navigateMonths(-1));
        nextButton = new JButton(">");
        nextButton.addActionListener(e -> navigateMonths(1));

        monthLabel = new JLabel("", JLabel.CENTER);
        monthLabel.setFont(new Font("Arial", Font.BOLD, 24));

        monthPanel.add(Box.createHorizontalGlue());
        monthPanel.add(prevButton);
        monthPanel.add(Box.createHorizontalStrut(10));
        monthPanel.add(monthLabel);
        monthPanel.add(Box.createHorizontalStrut(10));
        monthPanel.add(nextButton);
        monthPanel.add(Box.createHorizontalGlue());

        add(monthPanel, BorderLayout.NORTH);

        calendarPanel = new JPanel();
        calendarPanel.setLayout(new BorderLayout());
        add(calendarPanel, BorderLayout.CENTER);

        JPanel headerPanel = new JPanel(new GridLayout(1, 7, 5, 5));
        for (String dayName : dayNames) {
            JLabel dayLabel = new JLabel(dayName, JLabel.CENTER);
            if ("일".equals(dayName)) {
                dayLabel.setForeground(Color.RED);
            } else if ("토".equals(dayName)) {
                dayLabel.setForeground(Color.BLUE);
            }
            headerPanel.add(dayLabel);
        }
        calendarPanel.add(headerPanel, BorderLayout.NORTH);

        updateCalendar();
        setVisible(true);
    }

    private void navigateMonths(int delta) {
        calendar.add(Calendar.MONTH, delta);
        updateCalendar();
    }

    private void updateCalendar() {
        calendarPanel.removeAll();

        JPanel daysPanel = new JPanel(new GridLayout(0, 7, 5, 5));
        daysPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        calendarPanel.add(daysPanel, BorderLayout.CENTER);

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy . MM", Locale.KOREA);
        monthLabel.setText(sdf.format(calendar.getTime()));

        calendar.set(Calendar.DAY_OF_MONTH, 1);
        int startDayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);
        int maxDay = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);

        loadSchedulesFromDB(); // 데이터베이스에서 일정 로드

        for (int i = 1; i < startDayOfWeek; i++) {
            daysPanel.add(new JLabel(""));
        }

        for (int day = 1; day <= maxDay; day++) {
            JPanel dayPanel = new JPanel();
            dayPanel.setLayout(new BoxLayout(dayPanel, BoxLayout.Y_AXIS));
            JLabel dayLabel = new JLabel(String.valueOf(day));
            dayPanel.add(dayLabel);

            if (dailySchedules.containsKey(day)) {
                String scheduleText = dailySchedules.get(day).stream()
                        .collect(Collectors.joining(", "));
                JLabel scheduleLabel = new JLabel(scheduleText);
                dayPanel.add(scheduleLabel);
            }

            final int finalDay = day;
            dayPanel.addMouseListener(new java.awt.event.MouseAdapter() {
                public void mouseClicked(java.awt.event.MouseEvent evt) {
                    showDateWindow(finalDay, dayPanel);
                }
            });

            daysPanel.add(dayPanel);
        }

        daysPanel.revalidate();
        daysPanel.repaint();
    }

    private void loadSchedulesFromDB() {
        dailySchedules.clear();
        calendar.set(Calendar.DAY_OF_MONTH, 1);
        Date startOfMonth = calendar.getTime();
        calendar.add(Calendar.MONTH, 1);
        calendar.add(Calendar.DAY_OF_MONTH, -1);
        Date endOfMonth = calendar.getTime();

        List<String> schedules = dbConnection.getSchedulesForDateRange(startOfMonth, endOfMonth);
        for (String schedule : schedules) {
            // 예시: "5: Meeting at noon"
            String[] parts = schedule.split(": ");
            int day = Integer.parseInt(parts[0]);
            String scheduleText = parts[1];

            dailySchedules.computeIfAbsent(day, k -> new ArrayList<>()).add(scheduleText);
        }
    }

    private void showDateWindow(int day, JPanel dayPanel) {
        DateDetailDialog dateDetailDialog = new DateDetailDialog(this, "날짜 정보", true, day, dayPanel, dbConnection);
        dateDetailDialog.setVisible(true);
    }

    public void updateDayButton(int day, List<String> schedules) {
        dailySchedules.put(day, schedules);
        updateCalendar();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(CalendarWindow::new);
    }
}
