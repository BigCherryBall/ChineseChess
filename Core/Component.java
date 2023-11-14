package ChineseChess.Core;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

class Vector2
{
    public int x = 0;
    public int y = 0;

    @Override
    public String toString()
    {
        return "("+ x + "," + y + ")";
    }

    public Vector2(int x, int y)
    {
        this.x = x;
        this.y = y;
    }

    public Vector2()
    {
        
    }
}

/**
 * 记录移动前后的点，和移动后位置原有的棋子
 */
class MoveInfo 
{
    int x;
    int y;
    int new_x;
    int new_y;
    Chess target;
    /*是否移出了被限制的区域，比如士象帅*/
    boolean out_local;
}

class Tool
{
    /*--------------------------------------
     * private静态变量
    ----------------------------------------*/
    /* 路径分隔符 */
    public static final String separator = File.separator;
    /* 图片根目录 */
    public static final String img_dir = System.getProperty("user.dir") + separator + "ChineseChess" + separator + "image";
    /* 棋子根目录 */
    public static final String chess_dir = img_dir + separator + "chess";
    /* 棋盘根目录 */
    public static final String map_dir = img_dir + separator + "map";
    /* 走子提示根目录 */
    public static final String remind_dir = img_dir + separator + "moveRemind";
    /* 输出根目录 */
    public static final String out_dir = img_dir + separator + "out";
    /*日志打印等级*/
    private static int log_rank = 3;



    public static void log(Log rank, String func, String msg)
    {
        if (log_rank >= rank.getRank())
        {
            System.out.println(rank.getMsg() + "[" + func + "] " + msg);
        }
    }

    public static void setLogRank(int rank)
    {
        if (rank > 3)
            log_rank = 4;
        else
            log_rank = Math.max(rank, 0);
    }

    public static BufferedImage loadImg(String path)
    {
        try
        {
            return ImageIO.read(new File(path));
        }
        catch (IOException e)
        {
            log(Log.error, "loadImg", path + "图片加载失败");
        }
        return null;
    }

}
