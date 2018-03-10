package ncc4702.hpu.edu.usszhum;

/**
 * Created by Daniel Gallardo on 2/25/2018.
 */

public class Obstacle extends GameElement {
    private int hitPenalty; //the miss penalty for this Obstacle
    private boolean onScreen;

    //Constructor
    public Obstacle(GameView view, int color, int startColumn, int startRow, int tileSide, int width, int hitPenalty, float velocityY){
        super(view, color, 1,
                (startColumn + 1) * tileSide - tileSide/2, // add width of rows, subtract by half a tile to get center
                (startRow + 1) * tileSide - tileSide/2,
                tileSide * width,
                tileSide,
                velocityY);
        //tileside = 1/5 screen width
        this.onScreen = true;
        this.hitPenalty = hitPenalty;
    }


    public void update(double interval){
        //update vertical position
        shape.offset(0,(int) (this.velocityY*interval));

        //if this GameElement collides with the wall, reverse direction
        if(shape.top <0 && velocityY < 0 || shape.bottom > view.getScreenHeight() && velocityY >0)
            this.onScreen = false; //Don't know where getScreenHeight method comes from
    }

    public boolean isOnScreen(){
        return this.onScreen;
    }

    //returns the miss penalty for this Obstacle
    public int getHitPenalty(){
        return hitPenalty;
    }

    public void setHitPenalty(int hitPenalty) {
        this.hitPenalty = hitPenalty;
    }
}
