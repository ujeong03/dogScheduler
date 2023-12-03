import javax.swing.*;
import java.io.*;
import java.time.Duration;
import java.time.LocalTime;
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
import java.time.LocalDateTime;

/**
 * 보상으로 먹이, 목욕, 장난감을 강아지에게 제공하거나 강아지를 쓰다듬은 후 친밀도 및 레벨을 상승시키는 강아지 케어 클래스입니다.
 */
public class DogCare {
    private int touchCount;  // 쓰다듬기 횟수를 저장
    private String touchPath;  // 쓰다듬기 횟수가 저장된 파일 경로

    // 클래스 객체 선언
    private ControlReward controlReward;
    private DogLevel dogLevel;

    // 대화 상자 생성을 위한 객체 선언
    private JOptionPane optionPane;
    private JFrame frame;


    /**
     * DogCare 클래스의 생성자입니다.
     * 클래스 초기화 시 ControlReward, DogLevel 인스턴스를 생성하고 강아지 클릭 횟수와 경로를 초기화합니다.
     */
    public DogCare() {
        this.controlReward = new ControlReward();
        this.dogLevel = new DogLevel();
        this.touchPath = "dog_txt/touch.txt";
        this.touchCount = this.getTouchCount();
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
     * 또한 쓰다듬기 횟수를 증가시키고, 매일 자정에 쓰다듬기 횟수를 초기화합니다.
     */
    public void touchDog() {
        dogLevel.increaseCloseness(1);
        dogLevel.increaseLevel();
        addTouchCount();
    }


    /**
     * 파일에서 현재 강아지 클릭 횟수를 읽고 반환합니다.
     *
     * @return 현재 강아지 클릭 횟수
     * @exception FileNotFoundException
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
     * 강아지를 클릭할 때마다 쓰다듬기 횟수를 1씩 증가시키고 파일에 저장합니다.
     *
     * @exception IOException
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
     *
     * @exception IOException
     * @exception InterruptedException
     */
    public void resetTouchCount() {
        try {
            ProcessBuilder checkProcessBuilder = new ProcessBuilder(
                    "schtasks",
                    "/Query",
                    "/TN", "ResetTouchCount"
            );
            Process checkProcess = checkProcessBuilder.start();
            checkProcess.waitFor();

            int result = checkProcess.exitValue();

            // 사전에 예약된 작업이 없을 때만 작업 예약 (작업 예약 중복 방지)
            if (result != 0) {
                String currentWorkingDirectory = System.getProperty("user.dir");
                String command = "schtasks /Create /SC DAILY /TN ResetTouchCount /TR \"cmd /c echo 0 > " + currentWorkingDirectory + "\\dog_txt\\touch.txt\" /ST 00:00";
                ProcessBuilder processBuilder = new ProcessBuilder(command.split(" "));

                Process process = processBuilder.start();
                process.waitFor();
                System.out.println("작업을 예약했습니다.");
            } else {
                System.out.println("이미 작업이 예약되어 있습니다.");
            }
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }


    /**
     * 강아지를 10번 초과 클릭할 시 대화 상자를 띄웁니다.
     */
    public void showTouchLimitDialog() {
        optionPane.showMessageDialog(frame, "오늘은 이미 10번을 쓰다듬었습니다.", "안내", JOptionPane.WARNING_MESSAGE);
    }
}