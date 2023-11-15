import javax.swing.*;
import java.awt.*;
import java.awt.datatransfer.*;
import java.awt.dnd.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.sql.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class Reminder extends JPanel {

    private JPanel reminderPanel;
    private List<ReminderItem> reminderItems;

    public Reminder() {
        reminderPanel = new JPanel();
        reminderItems = new ArrayList<>();

        JScrollPane scrollPane = new JScrollPane(reminderPanel);
        setLayout(new BorderLayout());
        add(scrollPane, BorderLayout.CENTER);

        reminderPanel.setLayout(new BoxLayout(reminderPanel, BoxLayout.Y_AXIS));

        loadRemindersFromDatabase();
    }

    private void loadRemindersFromDatabase() {
        try {
            CalendarDBConnection calendarDBConnection = new CalendarDBConnection();
            calendarDBConnection.getConnection();

            // 현재 날짜 이후의 리마인더를 불러오기
            Date currentDate = new Date();
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            String formattedCurrentDate = dateFormat.format(currentDate);

            String query = "SELECT * FROM calendardb WHERE (reminder = 1 OR homework = 1) AND calendardate >= ?";
            PreparedStatement preparedStatement = calendarDBConnection.prepareStatement(query);
            preparedStatement.setString(1, formattedCurrentDate);

            ResultSet resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                String eventTitle = resultSet.getString("schedule");
                String eventDateText = resultSet.getString("calendardate");

                // Parse the eventDateText into a Date object
                Date eventDate = dateFormat.parse(eventDateText);

                // 남은 일 수 계산
                long daysRemaining = daysBetween(currentDate, eventDate);

                // 리마인더 패널에 추가
                ReminderItem reminderItem = new ReminderItem(eventTitle, daysRemaining);
                reminderItems.add(reminderItem);
                addReminderItem(reminderItem);
            }

            calendarDBConnection.closeConnection();

        } catch (SQLException | ParseException e) {
            e.printStackTrace();
        }
    }

    private void addReminderItem(ReminderItem reminderItem) {
        reminderPanel.add(reminderItem);
        reminderPanel.revalidate();
        reminderPanel.repaint();
    }

    //남은날짜 계산하기
    public static long daysBetween(Date currentDate, Date eventDate) {
        Calendar currentCal = Calendar.getInstance();
        currentCal.setTime(currentDate);

        Calendar eventCal = Calendar.getInstance();
        eventCal.setTime(eventDate);

        currentCal.set(Calendar.HOUR_OF_DAY, 0);
        currentCal.set(Calendar.MINUTE, 0);
        currentCal.set(Calendar.SECOND, 0);
        currentCal.set(Calendar.MILLISECOND, 0);

        eventCal.set(Calendar.HOUR_OF_DAY, 0);
        eventCal.set(Calendar.MINUTE, 0);
        eventCal.set(Calendar.SECOND, 0);
        eventCal.set(Calendar.MILLISECOND, 0);

        long difference = eventCal.getTimeInMillis() - currentCal.getTimeInMillis();
        return difference / (24 * 60 * 60 * 1000);
    }


    //리마인더 표시하기
    private class ReminderItem extends JPanel {
        private JLabel titleLabel;
        private JLabel daysRemainingLabel;
        private String eventTitle;

        ReminderItem(String eventTitle, long daysRemaining) {
            this.eventTitle = eventTitle;
            setLayout(new FlowLayout(FlowLayout.LEFT));
            titleLabel = new JLabel(eventTitle);
            daysRemainingLabel = new JLabel("D - " + daysRemaining);
            add(daysRemainingLabel);
            add(titleLabel);

            setOpaque(true);
            setBackground(Color.WHITE);
            setBorder(BorderFactory.createLineBorder(Color.BLACK));
            setTransferHandler(new ReminderTransferHandler());
            addMouseListener(new MouseAdapter() {
                @Override
                public void mousePressed(MouseEvent e) {
                    JComponent comp = (JComponent) e.getSource();
                    TransferHandler handler = comp.getTransferHandler();
                    handler.exportAsDrag(comp, e, TransferHandler.MOVE);
                }
            });
        }

        public void updateDaysRemaining(long daysRemaining) {
            daysRemainingLabel.setText("D - " + daysRemaining);
        }
    }



    // Create a custom TransferHandler for the drag-and-drop functionality
    private class ReminderTransferHandler extends TransferHandler {
        private int sourceIndex;
        @Override
        public int getSourceActions(JComponent c) {
            return TransferHandler.MOVE;
        }

        @Override
        protected Transferable createTransferable(JComponent c) {
            if (c instanceof ReminderItem) {
                ReminderItem reminderItem = (ReminderItem) c;
                //드래그한 reminderItem 인덱스 가져오기
                sourceIndex = reminderItems.indexOf(reminderItem);
                return new StringSelection(reminderItem.eventTitle);
            }
            return null;
        }

        @Override
        protected void exportDone(JComponent source, Transferable data, int action) {
            if (action == TransferHandler.MOVE && source instanceof ReminderItem) {
                ReminderItem reminderItem = (ReminderItem) source;
                //드래그한 아이템을 제거하고 UI를 업데이트하는 처리, 예를 들어 리스트에서 제거하고 UI를 업데이트함
                reminderItems.remove(reminderItem);
                reminderPanel.remove(reminderItem);
                reminderPanel.revalidate();
                reminderPanel.repaint();

                // 이제 드롭 작업을 처리합니다.
                if (data.isDataFlavorSupported(DataFlavor.stringFlavor)) {
                    Transferable transferable = new StringSelection(reminderItem.eventTitle);
                    TransferHandler.TransferSupport support = new TransferHandler.TransferSupport(source, transferable);

                    if (support.isDrop()) {
                        // 드롭 위치 정보 가져오기
                        Point dropPoint = support.getDropLocation().getDropPoint();

                        // 좌표를 패널의 기준으로 변환
                        SwingUtilities.convertPointFromScreen(dropPoint, reminderPanel);

                        // 드롭 위치에 해당하는 인덱스 계산
                        int targetIndex = reminderPanel.getComponentAt(dropPoint).getY() / reminderItem.getHeight();

                        // targetIndex가 유효한 범위 내에 있는지 확인
                        if (targetIndex >= 0 && targetIndex <= reminderItems.size()) {
                            // 새 위치에 드래그된 항목을 삽입
                            reminderItems.add(targetIndex, reminderItem);
                            reminderPanel.add(reminderItem, targetIndex);
                            reminderPanel.revalidate();
                            reminderPanel.repaint();
                        }
                }
            }
        }}}}
