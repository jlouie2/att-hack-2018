package ncc4702.hpu.edu.usszhum;

import java.util.Random;

/**
 * Created by tybo96789 on 3/10/18.
 */

public class Stuff {



    public static GameElement[][] convert(int[][] data)
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
                        gameobjects[i][j] = new Student(null,-1,-1,-1,-1);
                        break;
                    case 2:
                        gameobjects[i][j++] = new Trash(null,-1,-1,-1,-1);
                        gameobjects[i][j-1] = gameobjects[i][j];
                        break;
                    case 3:
                        gameobjects[i][j++] = new ThreeBlock(null,-1,-1,-1,-1);
                        gameobjects[i][j++] = gameobjects[i][j-1];
                        gameobjects[i][j++] = gameobjects[i][j-1];
                        break;
                    case 4:
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
