package ncc4702.hpu.edu.usszhum;

/**
 * Created by Daniel Gallardo on 2/25/2018.
 */

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.Log;

public class GameElement {
    protected GameView view; // the view that contains this GameElement
    protected Paint paint = new Paint(); //Paint to draw this GamElement
    protected Rect shape; // the GameElement's rectangular bounds
    public float velocityY; //the vertical velocity of this GameElement
    private int soundId; //the sound associated with this GameElement

    //public constructor
    public GameElement(GameView view, int color, int soundId, int x,
                        int y, int width, int height, float velocityY){
        this.view = view;
        this.velocityY = velocityY;
        paint.setColor(color);
//        shape = new Rect(x, y+height, x+width, y);
        shape = new Rect(x, y, x+width, y+height);
        System.out.println(this.view.getScreenHeight());
        System.out.println(this.view.getScreenWidth());
        this.soundId = soundId;
    }

    //update GameElement position and check for wall collisions
    //TODO Possible change here
    public void update(double interval){
        //update vertical position
//        shape.offset(0,(int) (velocityY*interval));
//        shape.offset(1000,0);
        shape.offsetTo(1000, 1000);

        //if this GameElement collides with the wall, reverse direction
//        if(shape.top <0 && velocityY < 0 || shape.bottom > view.getScreenHeight() && velocityY >0)
//            velocityY *= -1; //Don't know where getScreenHeight method comes from
    }

    //draws this GameElement on the given Canvas
    public void draw(Canvas canvas){
        Log.d("DRAW", "I exist"); canvas.drawRect(shape, paint);
    }

    //plays the sound that corresponds to this type of GameElement
    public void playSound(){
        view.playSound(soundId);
    }
}
