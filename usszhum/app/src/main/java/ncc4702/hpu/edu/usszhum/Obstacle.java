package ncc4702.hpu.edu.usszhum;

/**
 * Created by Daniel Gallardo on 2/25/2018.
 */

public class Obstacle extends GameElement {
    private int hitPenalty; //the miss penalty for this Obstacle

    //Constructor
    public Obstacle(GameView view, int color, int hitPenalty, int y, int width){
        super(view, color, hitPenalty, y, width);
        this.hitPenalty = hitPenalty;
    }

    //returns the miss penalty for this Obstacle
    public int getHitPenalty(){
        return hitPenalty;
    }

    public void setHitPenalty(int hitPenalty) {
        this.hitPenalty = hitPenalty;
    }
}
