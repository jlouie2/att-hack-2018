package ncc4702.hpu.edu.usszhum;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.media.AudioAttributes;
import android.media.SoundPool;
import android.os.Build;
import android.os.Bundle;
import android.util.AttributeSet;
import android.util.Log;
import android.util.SparseIntArray;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;

import java.util.ArrayList;
import java.util.Random;

//more imports from 6.13.1

/**
 * Created by Daniel Gallardo on 2/25/2018.
 */

public class GameView extends SurfaceView implements SurfaceHolder.Callback {
    private static final String TAG = "CannonView";//for logging errors

    //constants for game play
    public static final int MISS_PENALTY = 2; //seconds deducted on a miss
    public static final int HIT_REWARD = 3; //secondsadded on a hit

    //constants for the Cannon
    public static final double CANNON_BASE_RADIUS_PERCENT = 3.0 / 40;
    public static final double CANNON_BARREL_WIDTH_PERCENT = 3.0 / 40;
    public static final double CANNON_BARREL_LENGTH_PERCENT = 1.0 / 10;

    //constants for the Player
    public static final double CANNONBALL_RADIUS_PERCENT = 3.0 / 80;
    public static final double CANNONBALL_SPEED_PERCENT = 3.0 / 2;

    //constants for the targets
    public static final double TARGET_WIDTH_PERCENT = 1.0 / 40;
    public static final double TARGET_LENGTH_PERCENT = 3.0 / 20;
    public static final double TARGET_FIRST_X_PERCENT = 3.0 / 5;
    public static final double TARGET_SPACING_PERCENT = 1.0 / 60;
    public static final double TARGET_PIECES = 9;
    public static final double TARGET_MIN_SPEED_PERCENT = 3.0 / 4;
    public static final double TARGET_MAX_SPEED_PERCENT = 6.0 / 4;

    //constants for the Obstacle
    public static final double BLOCKER_WIDTH_PERCENT = 1.0 / 40;
    public static final double BLOCKER_LENGTH_PERCENT = 1.0 / 4;
    public static final double BLOCKER_X_PERCENT = 1.0 / 2;
    public static final double BLOCKER_SPEED_PERCENT = 1.0;

    //text size 1/18 of screen width
    public static final double TEXT_SIZE_PERCENT = 1.0 / 18;

    private CannonThread cannonThread;// controls the game loop also WTF where did this come from???
    private Activity activity; //to display Game Over dialog in GUI thread
    private boolean dialogIsDisplayed = false;

    //game objects
    private Cannon cannon;
    private Obstacle mObstacle;
    private ArrayList<Target> targets;

    //dimension variables
    private int screenWidth;
    private int screenHeight;

    //variables for the game loop and tracing statistics
    private boolean gameOver; //is the game over?
    private double timeLeft; //time remaning in seconds
    private int shotsFired; //shots the user has fired
    private double totalElapsedTime; //elapsed seconds

    //constants and variables for managing sounds
    public static final int TARGET_SOUND_ID = 0;
    public static final int CANNON_SOUND_ID = 1;
    public static final int BLOCKER_SOUND_ID = 2;
    private SoundPool soundPool; //plays sound effects
    private SparseIntArray soundMap; //maps IDs to SoundPool

    //Paint variables used when drwing each item on the screen
    private Paint textPaint; //paint used to draw text
    private Paint backgroundPaint; //paint used to clear the drawing area

    //6.4.8 Adding the CannonView to fragment_main.xml
    public GameView(Context context, AttributeSet attrs) {
        super(context, attrs);//call superclass constructor
        activity = (Activity) context; //store reference to MainActivity

        //register SurfaceHolder.Callback listener
        getHolder().addCallback(this);

        //configure audio attributes for game audio
        AudioAttributes.Builder attrBuilder = new AudioAttributes.Builder();
        attrBuilder.setUsage(AudioAttributes.USAGE_GAME);

        //initialize SoundPool to play the app's three sound effects
        SoundPool.Builder builder = new SoundPool.Builder();
        builder.setMaxStreams(1);
        builder.setAudioAttributes(attrBuilder.build());
        soundPool = builder.build();

        //create Map of sounds and pre-load sounds
        soundMap = new SparseIntArray(3); //create new SparseIntArray
        soundMap.put(TARGET_SOUND_ID, soundPool.load(context, R.raw.target_hit, 1));
        soundMap.put(CANNON_SOUND_ID, soundPool.load(context, R.raw.cannon_fire, 1));
        soundMap.put(BLOCKER_SOUND_ID, soundPool.load(context, R.raw.blocker_hit, 1));

        textPaint = new Paint();
        backgroundPaint = new Paint();
        backgroundPaint.setColor(Color.WHITE);
    }

    //called when the size of the SurfaceView changes, such as when it's first added to the View hierarchy
    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);

        screenWidth = w;
        screenHeight = h;

        //configure text properties
        textPaint.setTextSize((int) (TEXT_SIZE_PERCENT * screenHeight));//THIS IS THE LINE WITH THE ERRORS
        textPaint.setAntiAlias(true);//smoothes the text
    }

    //get width of the game screen
    public int getScreenWidth() {
        return screenWidth;
    }

    //get height of the game screen
    public int getScreenHeight() {
        return screenHeight;
    }

    //plays a sound with the given soundId in soundMap
    public void playSound(int soundID) {
        soundPool.play(soundMap.get(soundID), 1, 1, 1, 0, 1f);
    }

    //reset all the screen elements and start a new game
    public void newGame() {
        //consturct a new Cannon
        cannon = new Cannon(this, (int) (CANNON_BASE_RADIUS_PERCENT * screenHeight), (int) (CANNON_BARREL_LENGTH_PERCENT * screenWidth), (int) (CANNON_BARREL_WIDTH_PERCENT * screenHeight));

        Random random = new Random();//for determining random velocities
        targets = new ArrayList<>();

        //initialize targetX for the first target from the left
        int targetX = (int) (TARGET_FIRST_X_PERCENT * screenWidth);

        //clculate Y coordinate of Targets
        int targetY = (int) ((0.5 - TARGET_LENGTH_PERCENT / 2) * screenHeight);

        //add TARGET_PIECES Targets to the Target list
        for (int n = 0; n < TARGET_PIECES; n++) {

            //determine a random velocity between min and max values for Target n
            double velocity = screenHeight * (random.nextDouble() * (TARGET_MAX_SPEED_PERCENT - TARGET_MIN_SPEED_PERCENT) + TARGET_MIN_SPEED_PERCENT);

            //alternate Target colors between dark and light
            int color = (n % 2 == 0) ? getResources().getColor(R.color.dark, getContext().getTheme()) : getResources().getColor(R.color.light, getContext().getTheme());

            velocity *= -1; //reverse the initial velocity for next Target

            //create and add a new Target to the Target list
            targets.add(new Target(this, color, HIT_REWARD, targetX, targetY, (int) (TARGET_WIDTH_PERCENT * screenWidth), (int) (TARGET_LENGTH_PERCENT * screenHeight), (int) velocity));

            //increase the x coordinate to position the next Target more to the right
            targetX += (TARGET_WIDTH_PERCENT + TARGET_SPACING_PERCENT) * screenWidth;
        }

        //create a new Obstacle
        mObstacle = new Obstacle(this, Color.BLACK, MISS_PENALTY,
                (int) (BLOCKER_X_PERCENT * screenWidth),
                (int) ((0.5 - BLOCKER_X_PERCENT / 2) * screenHeight),
                (int) (BLOCKER_WIDTH_PERCENT * screenWidth),
                (int) (BLOCKER_LENGTH_PERCENT * screenHeight),
                (float) (BLOCKER_SPEED_PERCENT) * screenHeight);

        timeLeft = 10; //start the countdown at 10 seconds

        shotsFired = 0; //set the initial number of shots fired
        totalElapsedTime = 0.0; //set the time elapsed to zero

        if (gameOver) {//start a new game after the last game ended
            gameOver = false;
            cannonThread = new CannonThread(getHolder());//create thread
            cannonThread.start();//star the game loop thread
        }

        hideSystemBars();
    }

    //called repeatedly by the CannonThread to update game elements
    private void updatePositions(double totalElapsedTimeMS) {
        double interval = totalElapsedTimeMS / 1000.0; //convert to seconds

        //update cannonball's position if it is on the screen
        if (cannon.getPlayer() != null)
            cannon.getPlayer().update(interval);

        mObstacle.update(interval);// update the mObstacle's position

        for (GameElement target : targets)
            target.update(interval);//update the target's position
        timeLeft -= interval; //subtract from time left

        if (timeLeft <= 0) {
            timeLeft = 0.0;
            gameOver = true;//the game is over
            cannonThread.setRunning(false);//terminate thread
            showGameOverDialog(R.string.lose);//show the losing dialog
        }

        //if all pieces have been hit
        if (targets.isEmpty()) {
            cannonThread.setRunning(false);//terminate thread
            showGameOverDialog(R.string.win);//show winning dialog
            gameOver = true;
        }
    }

    //aligns the barrel and fires a cannonball if a cannonball is not already on screen
    public void alignAndFireCannonball(MotionEvent event) {
        //get the location of the touch in this view
        Point touchPoint = new Point((int) event.getX(), (int) event.getY());

        //compute the tourch's distance from center of the scree on the y-axis
        double centerMinusY = (screenHeight / 2 - touchPoint.y);

        double angle = 0; //initialize angle to 0

        //calculate the angle the barrel makes with the horizontal
        angle = Math.atan2(touchPoint.x, centerMinusY);

        //point the barrel at thte point where the scree was touched
        cannon.align(angle);

        //fire Player if there is not already a Player on screen
        if (cannon.getPlayer() == null || !cannon.getPlayer().isOnScreen()) {
            cannon.fireCannonball();
            ++shotsFired;
        }
    }
    @SuppressLint("ValidFragment")
    //display an AlertDialog when the game ends
    private void showGameOverDialog(final int messageId) {
        //DialogFragment to display game stats and start new game
        final DialogFragment gameResult =
                new DialogFragment(){
                    //create an AlertDialog and return it
                    @Override
                    public Dialog onCreateDialog(Bundle bundle) {
                        //create dialog displaying String resource for messageId
                        AlertDialog.Builder builder =
                                new AlertDialog.Builder(getActivity());
                        builder.setTitle(getResources().getString(messageId));
                        //display number of shots fired and total time elapsed
                        builder.setMessage(getResources().getString(R.string.results_format, shotsFired, totalElapsedTime));
                        builder.setPositiveButton(R.string.reset_game,
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialogIsDisplayed = false;
                                        newGame();//set up and start a new game
                                    }
                                });
                        return builder.create();
                    }
                };
        //in GUI thread, use FragmentManager to display the DialogFragment
        activity.runOnUiThread(
                new Runnable(){
                    public void run(){
                        showSystemBars();//exit immersive mode
                        dialogIsDisplayed = true;
                        gameResult.setCancelable(false);//modal dialog
                        gameResult.show(activity.getFragmentManager(),"results");
                    }
                }
        );
    }

    //DRAW SHIZ
    //draws the game to the given Canvas
    public void drawGameElements(Canvas canvas){
        //clear the background
        canvas.drawRect(0,0,canvas.getWidth(),canvas.getHeight(),backgroundPaint);


        //display time remaining
        canvas.drawText(getResources().getString(R.string.time_remaining_format, timeLeft),50,100,textPaint);
        //canvas.drawText("HELLO?????????",150,100,textPaint);


        cannon.draw(canvas);//draw the cannon

        //draw the GameElements
        if(cannon.getPlayer() != null && cannon.getPlayer().isOnScreen()) cannon.getPlayer().draw(canvas);

        mObstacle.draw(canvas);//draw the mObstacle

        //draw all of the Targets
        for(GameElement target : targets)
            target.draw(canvas);
    }

    //checks if the ball collides with the Obstacle or any of the Targets and handles the collisions
    public void testForCollisions(){
        //remove any of the targets that the Player collides with
        if(cannon.getPlayer() != null && cannon.getPlayer().isOnScreen()){
            for(int n = 0; n < targets.size();n++){
                if(cannon.getPlayer().collidesWith(targets.get(n))){
                    targets.get(n).playSound();//play Target hit sound

                    //add hit rewards time to remaining time
                    timeLeft += targets.get(n).getHitReward();

                    cannon.removeCannonball();//remove Player from the game
                    targets.remove(n);//remove the Target that was hit
                    --n;
                    break;
                }
            }
        }
        else{//remove the Player if it should not be on the screen
            cannon.removeCannonball();
        }

        //chek if ball collides with mObstacle
        if(cannon.getPlayer() != null && cannon.getPlayer().collidesWith(mObstacle)){
            mObstacle.playSound();

            //reverse ball direction
            cannon.getPlayer().reverseVelocityX();

            //deduct mObstacle's miss penalty from remaining time
            timeLeft -= mObstacle.getMissPenalty();
        }
    }

    //stops the game: called by CannonGameFragment's onPause method
    public void stopGame(){
        if(cannonThread != null){
            cannonThread.setRunning(false);//tell the thread to terminate
        }
    }

    //release resources: called by CannonGame's onDestroy method
    public void releaseResources(){
        soundPool.release();//release all resources used by the SoundPool
        soundPool = null;
    }

    //6.13.13
    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height){}

    //called when surface is first created
    @Override
    public void surfaceCreated(SurfaceHolder holder){
        if(!dialogIsDisplayed){
            newGame();//set up and start a new game
            cannonThread = new CannonThread(holder);//create thread
            cannonThread.setRunning(true);//start game running
            cannonThread.start();//start the game loop thread
        }
    }

    //called when the surface is destroyed
    @Override
    public void surfaceDestroyed(SurfaceHolder holder){
        //ensure that thread terminatees properly
        boolean retry = true;
        cannonThread.setRunning(false);//terminate cannonThread

        while(retry){
            try{
                cannonThread.join();//wait for cannonThread to finish
                retry = false;
            }
            catch(InterruptedException e){
                Log.e(TAG, "Thread Interrupted", e);
            }
        }
    }

    //called when the user touches the screen in this activity
    @Override
    public boolean onTouchEvent(MotionEvent e){
        //get int repreesenting the type of action which caused this event
        int action = e.getAction();

        //the user touched the screen or dragged along the screen
        if(action == MotionEvent.ACTION_DOWN || action == MotionEvent.ACTION_MOVE){
            //fire the cannonball toward the touch point
            alignAndFireCannonball(e);
        }
        return true;
    }
    private class CannonThread extends Thread{
        private SurfaceHolder surfaceHolder; //for manipulating canvas
        private boolean threadIsRunning = true;//running by default

        //initializes the surface holder
        public CannonThread(SurfaceHolder holder){
            surfaceHolder = holder;
            setName("CannonThread");
        }

        //changes running state
        public void setRunning(boolean running){
            threadIsRunning = running;
        }

        //controls the game loop
        @Override
        public void run(){
            Canvas canvas = null; //used for drawing
            long previousFrameTime = System.currentTimeMillis();

            while(threadIsRunning){
                try{
                    //get Canvas for exclusive drawing from this thread
                    canvas = surfaceHolder.lockCanvas();

                    //lock the surfaceHolder for drawing
                    synchronized (surfaceHolder){
                        long currentTime = System.currentTimeMillis();
                        double elapsedTimeMS = currentTime-previousFrameTime;
                        totalElapsedTime+=elapsedTimeMS/1000.0;
                        updatePositions(elapsedTimeMS);//update game state
                        testForCollisions();//test for GameElement collisions
                        drawGameElements(canvas);//draw using the canvas
                        previousFrameTime = currentTime;//update previous time
                    }
                }finally{
                    //display canva's contents on the CannonView and enable other threads to use the Canvas
                    if(canvas != null)
                        surfaceHolder.unlockCanvasAndPost(canvas);
                }
            }
        }
    }

    //hide system bars and app bar
    private void hideSystemBars(){
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT)
            setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE |
                    View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION |
                    View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN |
                    View.SYSTEM_UI_FLAG_HIDE_NAVIGATION |
                    View.SYSTEM_UI_FLAG_FULLSCREEN |
                    View.SYSTEM_UI_FLAG_IMMERSIVE);
    }

    //show system bars and app bar
    private void showSystemBars(){
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT)
            setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE |
                    View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION |
                    View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
    }

}