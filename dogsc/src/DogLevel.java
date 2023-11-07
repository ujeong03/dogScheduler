import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

public class DogLevel {
    private int growthStage;
    private String closenessPath, levelPath;
    private int closeness, level;
    private ControlReward rewardController;
    private DogCare dogCare;

    public DogLevel() {
        this.closenessPath = "dog_txt/closeness.txt";
        this.levelPath = "dog_txt/level.txt";
    }


    public int getCloseness() {
        try {
            File closenessfile = new File(closenessPath);
            Scanner scanner = new Scanner(closenessfile);
            while (scanner.hasNext())
                closeness = scanner.nextInt();
            scanner.close();
            return closeness;
        } catch (FileNotFoundException e) {
            System.out.println("file not found");
            return -1;
        }
    }

    public int getLevel() {
        try {
            File levelfile = new File(levelPath);
            Scanner scanner = new Scanner(levelfile);
            while (scanner.hasNext())
                level = scanner.nextInt();
            scanner.close();
            return level;
        } catch (FileNotFoundException e) {
            System.out.println("file not found");
            return -1;
        }
    }

    //todo 친밀도 num만큼 상승시킨 후 파일에 저장
    public void increaseCloseness(int num) {
        closeness = getCloseness();
        closeness += num;

    }

    //todo 레벨 1 상승시킨 후 파일에 저장, 친밀도 0으로 초기화 후 파일에 저장
    public void increaseLevel() {
        level = getLevel();
        level++;

        closeness = 0;
    }

    public int growUp() {
        level = getLevel();

        if (level >= 40) {
            growthStage = 4;
        } else if (level >= 30) {
            growthStage = 3;
        } else if (level >= 20) {
            growthStage = 2;
        } else if (level >= 10) {
            growthStage = 1;
        } else if (level >= 0) {
            growthStage = 0;
        }
        return growthStage;
    }
}