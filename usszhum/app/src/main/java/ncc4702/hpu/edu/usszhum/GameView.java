package ncc4702.hpu.edu.usszhum;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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
import java.util.LinkedList;
import java.util.Random;

//more imports from 6.13.1

/**
 * Created by Daniel Gallardo on 2/25/2018.
 */

public class GameView extends SurfaceView implements SurfaceHolder.Callback {
    private static final String TAG = "GameView";//for logging errors

    //constants for game play
    public static final int GPA_START = 100;
    public static final int ONE_TILE_PENALTY = 5;
    public static final int TWO_TILE_PENALTY = 10;
    public static final int THREE_TILE_PENALTY = 15;

    public static final double OBSTACLE_SPEED_PERCENTAGE =  1.0;

    public static final int MISS_PENALTY = 2; //seconds deducted on a miss
    public static final int HIT_REWARD = 3; //secondsadded on a hit

    //constants for hall rows
    public static final int ROW_WIDTH = 1/5;

    //constants for the Hallway
    public static final int HALLWAY_WIDTH_PERCENT = 1/5;
    public static final int[][] STAGE_1 = {
            {0,0,0,0,0},
            {0,0,0,0,0},
            {0,1,0,0,0},
            {0,0,0,1,1},
            {0,1,1,0,0},
            {0,0,1,0,0},
            {0,0,0,0,0},
            {1,0,0,0,0},
            {1,1,1,0,0},
            {0,0,0,0,1},
            {0,0,1,0,0},
            {0,1,1,1,1},
            {0,0,0,0,0},
            {1,0,1,1,0},
            {0,0,1,1,1},
            {0,0,0,0,0},
            {1,0,1,1,1},
            {0,0,0,0,0},
            {1,0,0,0,1},
            {0,0,1,0,0},


    };

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
    ArrayList<int[]> level;
    private Obstacle mObstacle;//USED TO BE BLOCKER
    private Hallway hall;
    //private ArrayList<Obstacle> targets;//USED TO BE TARGETS

    //dimension variables
    private int screenWidth;
    private int screenHeight;
    private int tileSize;

    //variables for the game loop and tracing statistics
    private boolean gameOver; //is the game over?
    private int currentGPA;
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
    @SuppressLint("NewApi")//DOES MAGIC STUFF
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
        //TODO CURRENTLY COMMENTED OUT AS THERE ARE NO SOUNDS YET
//        soundMap.put(TARGET_SOUND_ID, soundPool.load(context, R.raw.target_hit, 1));
//        soundMap.put(CANNON_SOUND_ID, soundPool.load(context, R.raw.cannon_fire, 1));
//        soundMap.put(BLOCKER_SOUND_ID, soundPool.load(context, R.raw.blocker_hit, 1));

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
        tileSize = screenWidth * HALLWAY_WIDTH_PERCENT;

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

    //get tile size
    public int getTileSize() {return tileSize;}

    //plays a sound with the given soundId in soundMap
    public void playSound(int soundID) {
        soundPool.play(soundMap.get(soundID), 1, 1, 1, 0, 1f);
    }

    //reset all the screen elements and start a new game
    public void newGame() {
        level = new ArrayList<>();
        for(int r = 0; r < STAGE_1.length; r++){
            level.add(STAGE_1[r]);
        }

        //construct new Hallway
        hall = new Hallway(this, getResources().getColor(R.color.hallColor,
                getContext().getTheme()), 0,
                HALLWAY_WIDTH_PERCENT * screenWidth,
                level);

        totalElapsedTime = 0.0; //set the time elapsed to zero

        if (gameOver) {//start a new game after the last game ended
            gameOver = false;
            cannonThread = new CannonThread(getHolder());//create thread
            cannonThread.start();//star the game loop thread
        }

        hideSystemBars();
    }

    //called repeatedly by the CannonThread to update game elements
    //TODO THIS PART NEEDS A BIT OF WORK
    private void updatePositions(double totalElapsedTimeMS) {
        double interval = totalElapsedTimeMS / 1000.0; //convert to seconds

        if(this.hall.getDistanceRemaining() > 0){
            this.hall.update(interval);
//            ArrayList<GameElement[]> obstacleRows = this.hall.getGameElements();
//
//            for(GameElement[] row: obstacleRows){
//                for(GameElement obs: row){
//                    if(obs != null){
//                        obs.update(interval);
//                    }
//                }
//            }
            this.hall.updateElements(interval);

        }

//
//        //update cannonball's position if it is on the screen
//        if (cannon.getPlayer() != null)
//            cannon.getPlayer().update(interval);
//
//        //mObstacle.update(interval);// update the mObstacle's position WAS COMMENTED OUT
//
//        for (GameElement target : targets)
//            target.update(interval);//update the target's position
//        timeLeft -= interval; //subtract from time left
//
//        if (timeLeft <= 0) {
//            timeLeft = 0.0;
//            gameOver = true;//the game is over
//            cannonThread.setRunning(false);//terminate thread
//            showGameOverDialog(R.string.lose);//show the losing dialog
//        }
//
//        //if all pieces have been hit
//
//        if (targets.isEmpty()) {
//            cannonThread.setRunning(false);//terminate thread
//            showGameOverDialog(R.string.win);//show winning dialog
//            gameOver = true;
//        }
    }

    public static GameElement[] convert(GameView view, int[] data) {
        GameElement[] gameobjects = new GameElement[data.length];
        for (int j = 0; j < data.length; j++) {
            int count = 0;

            //Checks the next entry in that array to see it follows the chain of 1's
            while ((j + count) < data.length && data[(j) + count] != 0) count++;

            switch (count) {
                case 0:
                    Log.d(TAG, "null");
                    gameobjects[j] = null;
                    break;
                case 1:
                    Log.d(TAG, "Student");
                    gameobjects[j] = new Student(view, Color.BLACK, ONE_TILE_PENALTY, -view.getTileSize(), view.getTileSize(), (float)(view.getHeight() * OBSTACLE_SPEED_PERCENTAGE));
                    break;
                case 2:
                    Log.d(TAG, "trash");
                    gameobjects[j++] = new Trash(view, Color.BLACK, TWO_TILE_PENALTY, -view.getTileSize(), view.getTileSize() * 2, (float)(view.getHeight() * OBSTACLE_SPEED_PERCENTAGE));
                    gameobjects[j - 1] = gameobjects[j];
                    break;
                case 3:
                    Log.d(TAG, "ThreeBlock");
                    gameobjects[j++] = new ThreeBlock(view, Color.BLACK, THREE_TILE_PENALTY, -view.getTileSize(), view.getTileSize() * 3, (float)(view.getHeight() * OBSTACLE_SPEED_PERCENTAGE));
                    gameobjects[j++] = gameobjects[j - 1];
                    gameobjects[j++] = gameobjects[j - 1];
                    break;
                case 4:
                    Log.d(TAG, "FOUR");
                    int counter = 4;
                    while (count >= 0) {
                        switch (new Random().nextInt(counter)) {
                            case 1:
                                Log.d(TAG, "student");
                                gameobjects[j] = new Student(view, Color.BLACK, ONE_TILE_PENALTY, -view.getTileSize(),  view.getTileSize(), (float)(view.getHeight() * OBSTACLE_SPEED_PERCENTAGE));
                                counter -= 1;
                                break;
                            case 2:
                                Log.d(TAG, "trash");
                                gameobjects[j++] = new Trash(view, Color.BLACK, TWO_TILE_PENALTY, -view.getTileSize(), view.getTileSize() * 2, (float)(view.getHeight() * OBSTACLE_SPEED_PERCENTAGE));
                                gameobjects[j - 1] = gameobjects[j];
                                counter -= 2;
                                break;
                            case 3:
                                Log.d(TAG, "threeblock");
                                gameobjects[j++] = new ThreeBlock(view, Color.BLACK, THREE_TILE_PENALTY, -view.getTileSize(), view.getTileSize() * 3, (float)(view.getHeight() * OBSTACLE_SPEED_PERCENTAGE));
                                gameobjects[j++] = gameobjects[j - 1];
                                gameobjects[j++] = gameobjects[j - 1];
                                counter -= 3;
                                break;
                        }

                    }
                    break;
                default:
                    Log.d(TAG, "default");
                    gameobjects[j] = null;
                    break;

            }

        }
        return gameobjects;
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
//        cannon.align(angle);

        //fire Player if there is not already a Player on screen
//        if (cannon.getPlayer() == null || !cannon.getPlayer().isOnScreen()) {
//            cannon.fireCannonball();
//            ++shotsFired;
//        }
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

        //Tyler attempt here
//        Bitmap bitfact = BitmapFactory.decodeResource(this.getResources(),R.drawable.player_1);
//        Bitmap resized = Bitmap.createScaledBitmap(bitfact,200,200,false);
//        canvas.drawBitmap(resized,100,100,this.backgroundPaint);

        //display time remaining
        //canvas.drawText(getResources().getString(R.string.time_remaining_format, timeLeft),50,100,textPaint);
        //canvas.drawText("HELLO?????????",150,100,textPaint);


//      hallway
        this.hall.draw(canvas);

        //student
        Bitmap bitfact = BitmapFactory.decodeResource(this.getResources(),R.drawable.player_1);
        Bitmap resized = Bitmap.createScaledBitmap(bitfact,200,200,false);
        canvas.drawBitmap(resized,(screenWidth-200)/2,screenHeight-275,this.backgroundPaint);

        //obstacles
        this.hall.drawElements(canvas);
//        ArrayList<GameElement[]> obstacleRows = this.hall.getGameElements();
//
//        for(GameElement[] row: obstacleRows){
//            for(GameElement obs: row){
//                if(obs != null){
//                    Log.d("DRAWCANVAS", "Row:" + row + " obs:"+obs);
//                    obs.draw(canvas);
//                }
//            }
//        }

        //draw the GameElements
        //cannon.getPlayer().draw(canvas);//THIS USED TO BE AN IF STATEMENT DO NOT NEED

        //mObstacle.draw(canvas);//draw the mObstacle//DONT HAVE THAT

        //draw all of the Targets
//        for(GameElement target : targets)
//            target.draw(canvas);
    }

    //checks if the ball collides with the Obstacle or any of the Targets and handles the collisions
    public void testForCollisions(){
        //remove any of the targets that the Player collides with
//        if(cannon.getPlayer() != null){//IF DOES NEED TO CHANGE
//            for(int n = 0; n < targets.size();n++){
//                if(cannon.getPlayer().collidesWith(targets.get(n))){
//                    targets.get(n).playSound();//play Target hit sound
//
//                    //add hit rewards time to remaining time
//                    timeLeft += targets.get(n).getHitReward();
//
//                    cannon.removeCannonball();//remove Player from the game
//                    targets.remove(n);//remove the Target that was hit
//                    --n;
//                    break;
//                }
//            }
//        }
//        else{//remove the Player if it should not be on the screen
//            cannon.removeCannonball();
//        }

        //check if ball collides with mObstacle
        //TODO FIX THIS SHIZ
//        if(cannon.getPlayer().collidesWith(mObstacle)){
//            mObstacle.playSound();
//
//            //reverse ball direction
//            //cannon.getPlayer().reverseVelocityX();DONT NEED IT
//
//            //deduct mObstacle's miss penalty from remaining time
//            timeLeft -= mObstacle.getHitPenalty();//WAS getMissPenalty now getHitPenalty
//        }
    }

    //stops the game: called by CannonGameFragment's onPause method
    public void stopGame(){
        Log.d("STOP", "why?");
//        if(cannonThread != null){
//            cannonThread.setRunning(false);//tell the thread to terminate
//        }
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
//            alignAndFireCannonball(e);
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
                        Log.d("COUNT", "ttlelapsed:" + totalElapsedTime);
                        updatePositions(elapsedTimeMS);//update game state//THIS WAS COMMENTED OUT
                        testForCollisions();//test for GameElement collisions
                        drawGameElements(canvas);//draw using the canvas
                        previousFrameTime = currentTime;//update previous time
                    }
                }finally{
                    Log.d("THREAD", "I've stopped");
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