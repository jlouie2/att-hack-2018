package ncc4702.hpu.edu.usszhum;

import java.util.Random;

/**
 * Created by tybo96789 on 3/10/18.
 */

public class Stuff {


    public static GameElement[][] convert(GameView view, int[][] data) {
        GameElement[][] gameobjects = new GameElement[data.length][data[0].length];
        for (int i = 0; i < data.length; i++) {
            for (int j = 0; j < data[i].length; j++) {
                int count = 0;

                //Checks the next entry in that array to see it follows the chain of 1's
                while ((j + count) < data[i].length && data[i][(j + 1) + count] != 0) count++;

                switch (count) {
                    case 0:
                        gameobjects[i][j] = null;
                        break;
                    case 1:
                        gameobjects[i][j] = new Student(view, -1, -1, -1, -1);
                        break;
                    case 2:
                        gameobjects[i][j++] = new Trash(view, -1, -1, -1, -1);
                        gameobjects[i][j - 1] = gameobjects[i][j];
                        break;
                    case 3:
                        gameobjects[i][j++] = new ThreeBlock(view, -1, -1, -1, -1);
                        gameobjects[i][j++] = gameobjects[i][j - 1];
                        gameobjects[i][j++] = gameobjects[i][j - 1];
                        break;
                    case 4:
                        int counter = 4;
                        while (count >= 0) {
                            switch (new Random().nextInt(counter)) {
                                case 1:
                                    gameobjects[i][j] = new Student(view, -1, -1, -1, -1);
                                    counter -= 1;
                                    break;
                                case 2:
                                    gameobjects[i][j++] = new Trash(view, -1, -1, -1, -1);
                                    gameobjects[i][j - 1] = gameobjects[i][j];
                                    counter -= 2;
                                    break;
                                case 3:
                                    gameobjects[i][j++] = new ThreeBlock(view, -1, -1, -1, -1);
                                    gameobjects[i][j++] = gameobjects[i][j - 1];
                                    gameobjects[i][j++] = gameobjects[i][j - 1];
                                    counter -= 3;
                                    break;
                            }

                        }
                        break;
                    default:
                        gameobjects[i][j] = null;
                        break;

                }
            }
        }
        return gameobjects;
    }

    public static GameElement[] convert(GameView view, int[] data) {
        GameElement[] gameobjects = new GameElement[data.length];
        for (int j = 0; j < data.length; j++) {
            int count = 0;

            //Checks the next entry in that array to see it follows the chain of 1's
            while ((j + count) < data.length && data[(j + 1) + count] != 0) count++;

            switch (count) {
                case 0:
                    gameobjects[j] = null;
                    break;
                case 1:
                    gameobjects[j] = new Student(view, -1, -1, -1, -1);
                    break;
                case 2:
                    gameobjects[j++] = new Trash(view, -1, -1, -1, -1);
                    gameobjects[j - 1] = gameobjects[j];
                    break;
                case 3:
                    gameobjects[j++] = new ThreeBlock(view, -1, -1, -1, -1);
                    gameobjects[j++] = gameobjects[j - 1];
                    gameobjects[j++] = gameobjects[j - 1];
                    break;
                case 4:
                    int counter = 4;
                    while (count >= 0) {
                        switch (new Random().nextInt(counter)) {
                            case 1:
                                gameobjects[j] = new Student(view, -1, -1, -1, -1);
                                counter -= 1;
                                break;
                            case 2:
                                gameobjects[j++] = new Trash(view, -1, -1, -1, -1);
                                gameobjects[j - 1] = gameobjects[j];
                                counter -= 2;
                                break;
                            case 3:
                                gameobjects[j++] = new ThreeBlock(view, -1, -1, -1, -1);
                                gameobjects[j++] = gameobjects[j - 1];
                                gameobjects[j++] = gameobjects[j - 1];
                                counter -= 3;
                                break;
                        }

                    }
                    break;
                default:
                    gameobjects[j] = null;
                    break;

            }

        }
        return gameobjects;
    }
}
