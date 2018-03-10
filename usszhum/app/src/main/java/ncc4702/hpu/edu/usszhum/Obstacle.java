package ncc4702.hpu.edu.usszhum;

/**
 * Created by Daniel Gallardo on 2/25/2018.
 */

public class Obstacle extends GameElement {
    private int missPenalty; //the miss penalty for this Obstacle

    //Constructor
    public Obstacle(GameView view, int color, int missPenalty, int x, int y, int width, int length, float velocityY){
        super(view, color, GameView.BLOCKER_SOUND_ID, x, y, width, length, velocityY);
        this.missPenalty = missPenalty;
    }

    //returns the miss penalty for this Obstacle
    public int getMissPenalty(){
        return missPenalty;
    }

}
