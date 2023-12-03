import java.io.*;
import java.util.Scanner;

/**
 * 강아지의 친밀도, 레벨을 반환하거나 상승시키며, 강아지를 성장시키는 강아지 레벨 클래스
 */
public class DogLevel {
    private String closenessPath, levelPath;  // 각각 친밀도와 레벨을 저장하는 파일 경로
    private int closeness, level;  // 각각 친밀도와 레벨을 저장
    private String dogImagePath;  // 성장 단계에 해당하는 강아지 이미지 경로


    /**
     * DogLevel 클래스의 생성자입니다.
     * 클래스 초기화 시 현재의 친밀도와 레벨과 각 값이 저장된 파일 경로, 그리고 현재 성장 단계에 해당하는 강아지 이미지 경로를 초기화합니다.
     */
    public DogLevel() {
        this.closenessPath = "dog_txt/closeness.txt";
        this.levelPath = "dog_txt/level.txt";
        this.closeness = this.getCloseness();
        this.level = this.getLevel();
        this.dogImagePath = this.growUp(this.getLevel());
    }


    /**
     * 파일에서 현재 친밀도를 읽고 반환합니다.
     *
     * @return 현재 친밀도
     * @exception FileNotFoundException
     */
    public int getCloseness() {
        try {
            File closenessfile = new File(closenessPath);
            Scanner scanner = new Scanner(closenessfile);

            while (scanner.hasNext())
                this.closeness = scanner.nextInt();
            scanner.close();

            return this.closeness;
        } catch (FileNotFoundException e) {
            System.out.println("file not found");
            return -1;
        }
    }


    /**
     * 파일에서 현재 레벨을 읽고 반환합니다.
     *
     * @return 현재 레벨
     * @exception FileNotFoundException
     */
    public int getLevel() {
        try {
            File levelfile = new File(levelPath);
            Scanner scanner = new Scanner(levelfile);

            while (scanner.hasNext())
                this.level = scanner.nextInt();
            scanner.close();

            return this.level;
        } catch (FileNotFoundException e) {
            System.out.println("file not found");
            return -1;
        }
    }


    /**
     * 보상이나 쓰다듬기에 해당하는 친밀도를 상승시키고 파일에 저장합니다.
     *
     * @param num 증가시킬 친밀도
     * @exception IOException
     */
    public void increaseCloseness(int num) {
        this.closeness += num;

        try {
            FileWriter closenessFileWriter = new FileWriter(closenessPath, false);
            BufferedWriter bw = new BufferedWriter(closenessFileWriter);
            bw.write(Integer.toString(this.closeness));
            bw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    /**
     * 친밀도가 100 이상이면 친밀도를 100 감소시키고, 레벨을 1 증가시킨 후 파일에 저장합니다.
     *
     * @exception IOException
     */
    public void increaseLevel() {
        if (this.closeness >= 100) {
            try {
                this.closeness -= 100;

                FileWriter closenessFileWriter = new FileWriter(closenessPath, false);
                BufferedWriter bw1 = new BufferedWriter(closenessFileWriter);
                bw1.write(Integer.toString(this.closeness));
                bw1.close();

                FileWriter levelFileWriter = new FileWriter(levelPath, false);
                BufferedWriter bw2 = new BufferedWriter(levelFileWriter);
                bw2.write(Integer.toString(++this.level));
                bw2.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    /**
     * 특정 레벨에 도달 시 성장한 강아지의 이미지 경로를 반환합니다.
     *
     * @param level 강아지의 현재 레벨
     * @return 각 성장 단계에 해당하는 강아지의 이미지 경로
     */
    public String growUp(int level) {
        if (level >= 50) {
            this.dogImagePath = "image/dog_image/dog_big_costume.png";
        } else if (level >= 40) {
            this.dogImagePath = "image/dog_image/dog_big.png";
        } else if (level >= 30) {
            this.dogImagePath = "image/dog_image/dog_middle_costume.png";
        } else if (level >= 20) {
            this.dogImagePath = "image/dog_image/dog_middle.png";
        } else if (level >= 10) {
            this.dogImagePath = "image/dog_image/dog_small_costume.png";
        } else if (level >= 0) {
            this.dogImagePath = "image/dog_image/dog_small.png";
        }
        return this.dogImagePath;
    }
}