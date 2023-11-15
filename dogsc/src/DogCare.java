import javax.swing.*;
import java.io.*;
import java.util.*;
import java.util.Timer;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import javax.imageio.ImageIO;

/**
 * 보상으로 먹이, 목욕, 장난감을 강아지에게 제공하거나 강아지를 쓰다듬은 후 친밀도 및 레벨을 상승시키는 강아지 케어 클래스입니다.
 */
public class DogCare {
    private ControlReward controlReward;
    private DogLevel dogLevel;
    private JOptionPane optionPane;
    private JFrame frame;
    private int touchCount;
    private String touchPath;
    private long current, scheduledTime;
    private Calendar calendar;

    /**
     * DogCare 클래스의 생성자입니다.
     * 클래스 초기화 시 ControlReward, DogLevel 객체를 생성하고 강아지 클릭 횟수를 초기화합니다.
     */
    public DogCare() {
        this.controlReward = new ControlReward();
        this.dogLevel = new DogLevel();
        this.touchPath = "dog_txt/touch.txt";
        this.touchCount = this.getTouchCount();
        this.resetTouchCount();
    }

    /**
     * 먹이, 목욕, 장난감 버튼 클릭 시 보상 -1, 친밀도 +10을 적용하고 친밀도 100 이상인 경우 레벨을 1 상승시킵니다.
     */
    public void careDog() {
        if (controlReward.getReward() > 0) {
            controlReward.useReward();
            dogLevel.increaseCloseness(10);
            dogLevel.increaseLevel();
        }
    }

    /**
     * 강아지 버튼 클릭 시 친밀도 +1을 적용하고, 친밀도 100 이상인 경우 레벨을 1 상승시킵니다.
     * 강아지 클릭 횟수를 증가시키고, 매일 자정에 클릭 횟수를 초기화합니다.
     */
    public void touchDog() {
        dogLevel.increaseCloseness(1);
        dogLevel.increaseLevel();
        addTouchCount();
        resetTouchCount();
    }

    /**
     * 파일에서 강아지 클릭 횟수를 읽어와 반환합니다.
     *
     * @return 현재 강아지 클릭 횟수
     */
    public int getTouchCount() {
        try {
            File touchfile = new File(touchPath);
            Scanner scanner = new Scanner(touchfile);
            while (scanner.hasNext())
                this.touchCount = scanner.nextInt();
            scanner.close();
            return this.touchCount;
        } catch (FileNotFoundException e) {
            System.out.println("file not found");
            return -1;
        }
    }

    /**
     * 강아지 클릭 횟수를 1 증가시키고 파일에 저장합니다.
     */
    public void addTouchCount() {
        try {
            this.touchCount++;

            FileWriter touchFileWriter = new FileWriter(touchPath, false);
            BufferedWriter bw = new BufferedWriter(touchFileWriter);
            bw.write(Integer.toString(this.touchCount));
            bw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 매일 자정에 강아지 클릭 횟수를 0으로 초기화합니다.
     */
    private void resetTouchCount() {
        calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);

        // 현재 날짜
        current = System.currentTimeMillis();
        // 작업을 예약할 시간
        scheduledTime = calendar.getTimeInMillis();

        // 이미 지난 경우 다음 날로 설정
        if (current > scheduledTime) {
            scheduledTime += TimeUnit.DAYS.toMillis(1);
        }

        ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

        // 실행할 작업
        Runnable task = () -> {
            try {
                this.touchCount = 0;

                // Your existing code for writing to the file
                FileWriter touchFileWriter = new FileWriter(touchPath, false);
                BufferedWriter bw = new BufferedWriter(touchFileWriter);
                bw.write(Integer.toString(this.touchCount));
                bw.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        };

        scheduler.scheduleAtFixedRate(task, scheduledTime - current, TimeUnit.DAYS.toMillis(1), TimeUnit.MILLISECONDS);
    }

    /**
     * 강아지를 10번 초과 클릭 시 경고창을 띄웁니다.
     */
    public void showTouchLimitDialog() {
        optionPane.showMessageDialog(frame, "오늘은 이미 10번을 쓰다듬었습니다.", "안내", JOptionPane.WARNING_MESSAGE);
    }
}
