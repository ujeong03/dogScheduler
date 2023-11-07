public class DogCare {
    private ControlReward controlReward;
    private DogLevel dogLevel;
    int closeness;

    public DogCare() {
        this.controlReward = new ControlReward();
        this.dogLevel = new DogLevel();
    }

    public void feedDog() {
        if (controlReward.getReward() > 0){
            controlReward.useReward();
            dogLevel.increaseCloseness(10);
            dogLevel.increaseLevel();
        }
    }

    public void bathDog() {
        if (controlReward.getReward() > 0){
            controlReward.useReward();
            dogLevel.increaseCloseness(10);
            dogLevel.increaseLevel();
        }
    }

    public void playDog() {
        if (controlReward.getReward() > 0){
            controlReward.useReward();
            dogLevel.increaseCloseness(10);
            dogLevel.increaseLevel();
        }
    }

    public void touchDog() {
        dogLevel.increaseCloseness(1);
        dogLevel.increaseLevel();
    }

    public DogLevel getDogLevel() {
        return dogLevel;
    }
}