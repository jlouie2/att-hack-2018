package ncc4702.hpu.edu.usszhum;

/**
 * Created by Daniel Gallardo on 2/25/2018.
 */

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;

public class GameElement {
    protected GameView view; // the view that contains this GameElement
    protected Paint paint = new Paint(); //Paint to draw this GamElement
    protected Rect shape; // the GameElement's rectangular bounds
    private float velocityY; //the vertical velocity of this GameElement
    private int soundId; //the sound associated with this GameElement

    //public constructor
    public GameElement(GameView view, int color, int soundId,
                        int y, int width){
        this.view = view;
        paint.setColor(color);
        shape = new Rect(3, y, 3+width, y+3);
        this.soundId = soundId;
    }

    //update GameElement position and check for wall collisions
    //TODO Possible change here
    public void update(double interval){
        //update vertical position
        shape.offset(0,(int) (velocityY*interval));

        //if this GameElement collides with the wall, reverse direction
        if(shape.top <0 && velocityY < 0 || shape.bottom > view.getScreenHeight() && velocityY >0)
            velocityY *= -1; //Don't know where getScreenHeight method comes from
    }

    //draws this GameElement on the given Canvas
    public void draw(Canvas canvas){
        canvas.drawRect(shape, paint);
    }

    //plays the sound that corresponds to this type of GameElement
    public void playSound(){
        view.playSound(soundId);
    }
}