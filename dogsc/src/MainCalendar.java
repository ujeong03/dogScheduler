import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class MainCalendar extends JPanel {
    private Date currentDate;
    private Date currentStartDate;
    private JButton prevButton;
    private JButton nextButton;
    private JButton openCalendarButton;
    private JPanel calendarPanel;
    private CalendarDBConnection calendarDB;


    public MainCalendar() {
        calendarDB = new CalendarDBConnection();
        setLayout(new BorderLayout());

        // 캘린더 버튼생성
        JButton openCalendarButton = new JButton("CALENDAR");
        // 캘린더 버튼 작동
        openCalendarButton.addActionListener((new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new CalendarWindow();
            }
        }));


        // Initialize with the current week's start and end dates
        Calendar calendar = Calendar.getInstance();
        currentStartDate = getStartOfWeek(calendar.getTime());
        currentDate = currentStartDate; // Initialize currentDate

        // 이전 주 버튼
        prevButton = new JButton("<");
        prevButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                showPreviousWeek();
            }
        });

        // 다음 주 버튼
        nextButton = new JButton(">");
        nextButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                showNextWeek();
            }
        });

        // 캘린더 패널
        calendarPanel = new JPanel();
        calendarPanel.setLayout(new GridLayout(2, 7)); // 7일씩 나열

        updateCalendar(); // 캘린더 업데이트

        // 버튼과 캘린더 패널을 프레임에 추가
        add(openCalendarButton,BorderLayout.NORTH);
        add(prevButton, BorderLayout.WEST);
        add(nextButton, BorderLayout.EAST);
        add(calendarPanel, BorderLayout.CENTER);
    }

    private void showPreviousWeek() {
        // 이전 주로 이동
        currentDate = getPreviousWeekStart(currentDate);
        updateCalendar();
    }

    private void showNextWeek() {
        // 다음 주로 이동
        currentDate = getNextWeekStart(currentDate);
        updateCalendar();
    }

    private void updateCalendar() {
        calendarPanel.removeAll();

        // 현재 주와 다음 주의 일자를 가져와서 표시
        List<Date> weekDates = getWeekDates(currentDate);
        for (Date date : weekDates) {
            final Date currentDate = date;
            JButton dateButton = new JButton(new SimpleDateFormat("MM-dd").format(date));
            dateButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    // 해당 날짜의 일정 불러오기
                    showScheduleForDate(currentDate);
                }
            });
            calendarPanel.add(dateButton);
        }

        // Display dates for the next week
        List<Date> nextWeekDates = getWeekDates(getNextWeekStart(currentStartDate));
        for (Date date : nextWeekDates) {
            final Date currentDate = date;
            JButton dateButton = new JButton(new SimpleDateFormat("MM-dd").format(date));
            dateButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    showScheduleForDate(currentDate);
                }
            });
            calendarPanel.add(dateButton);
        }

        // UI 업데이트
        revalidate();
        repaint();
    }

    private void showScheduleForDate(Date date) {
        // 해당 날짜의 일정을 가져와서 표시
        List<String> schedules = calendarDB.getSchedulesForDate(date);
        StringBuilder message = new StringBuilder();
        for (String schedule : schedules) {
            message.append(schedule).append("\n");
        }
        JOptionPane.showMessageDialog(this, message.toString(), "일정", JOptionPane.INFORMATION_MESSAGE);
    }

    private List<Date> getWeekDates(Date startDate) {
        // 주의 시작일을 기준으로 7일간의 일자를 가져옴
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(startDate);
        calendar.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY); // 주의 시작일로 설정
        Date current = calendar.getTime();

        // 7일간의 일자를 리스트에 추가
        List<Date> weekDates = new ArrayList<>();
        for (int i = 0; i < 7; i++) {
            weekDates.add(current);
            calendar.add(Calendar.DAY_OF_MONTH, 1);
            current = calendar.getTime();
        }

        return weekDates;
    }

    private Date getPreviousWeekStart(Date startDate) {
        // 이전 주의 시작일을 가져옴
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(startDate);
        calendar.add(Calendar.DAY_OF_MONTH, -7); // 7일 전으로 이동
        calendar.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY); // 주의 시작일로 설정
        return calendar.getTime();
    }

    private Date getNextWeekStart(Date startDate) {
        // 다음 주의 시작일을 가져옴
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(startDate);
        calendar.add(Calendar.DAY_OF_MONTH, 7); // 7일 후로 이동
        calendar.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY); // 주의 시작일로 설정
        return calendar.getTime();
    }

    private Date getStartOfWeek(Date startDate) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(startDate);
        calendar.add(Calendar.DAY_OF_WEEK, -6); // Move to the end of the week
        return calendar.getTime();
    }
}
