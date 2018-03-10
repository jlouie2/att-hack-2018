package ncc4702.hpu.edu.usszhum;

import java.util.Random;

/**
 * Created by tybo96789 on 3/10/18.
 */

public class Stuff {



    public static GameElement[][] convert(GameView view, int[][] data)
    {
        GameElement[][] gameobjects =  new GameElement[data.length][data[0].length];
        for(int i = 0; i < data.length;i++)
        {
            for(int j = 0; j < data[i].length;j++)
            {
                switch(data[i][j])
                {
                    case 0:
                        gameobjects[i][j] = null;
                        break;
                    case 1:
                        gameobjects[i][j] = new Student(view,-1,-1,-1,-1);
                        break;
                    case 2:
                        gameobjects[i][j++] = new Trash(view,-1,-1,-1,-1);
                        gameobjects[i][j-1] = gameobjects[i][j];
                        break;
                    case 3:
                        gameobjects[i][j++] = new ThreeBlock(view,-1,-1,-1,-1);
                        gameobjects[i][j++] = gameobjects[i][j-1];
                        gameobjects[i][j++] = gameobjects[i][j-1];
                        break;
                    case 4:
                        int count = 4;
                        while(count >=0)
                        {
                            switch(new Random().nextInt(count)) {
                                case 1:
                                    gameobjects[i][j] = new Student(view, -1, -1, -1, -1);
                                    count -= 1;
                                    break;
                                case 2:
                                    gameobjects[i][j++] = new Trash(view, -1, -1, -1, -1);
                                    gameobjects[i][j - 1] = gameobjects[i][j];
                                    count -= 2;
                                    break;
                                case 3:
                                    gameobjects[i][j++] = new ThreeBlock(view, -1, -1, -1, -1);
                                    gameobjects[i][j++] = gameobjects[i][j - 1];
                                    gameobjects[i][j++] = gameobjects[i][j - 1];
                                    count -=3;
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
}