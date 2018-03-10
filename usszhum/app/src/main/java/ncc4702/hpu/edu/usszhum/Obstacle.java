package ncc4702.hpu.edu.usszhum;

/**
 * Created by Daniel Gallardo on 2/25/2018.
 */

public class Obstacle extends GameElement {
    private int hitPenalty; //the miss penalty for this Obstacle

    //Constructor
    public Obstacle(GameView view, int color, int startColumn, int startRow, int tileSide, int width, int hitPenalty){
        super(view, color, 1,
                (startColumn + 1) * tileSide - tileSide/2, // add width of rows, subtract by half a tile to get center
                (startRow + 1) * tileSide - tileSide/2,
                tileSide * width,
                tileSide,1);
        //tileside = 1/5 screen width
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