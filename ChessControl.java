package ChineseChess;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.LinkedList;


public class ChessControl {


    /*挖坑用，如果开启，那么对于士象炮帅可以移除固定区域，参考翻翻棋*/
    public boolean wan_ning = false;
    /*
    * 实时棋盘，是一个二维的列表，列表里面是棋子对象的引用，没有棋子的地方是None。
    * 通过name获取棋子的名字，toString方法输出格式为红/黑+棋子名字（注意红黑方的部分棋子名称不同）
    * 绘制棋盘也是读取这个二维列表的数据，通过棋子对象的引用.pos访问棋子坐标，如 引用.pos.x可以访问x坐标（坐标从0开始）
    */
    private Chess[][] map;
    /*棋子对象池,内部访问。初始化位置，只读不改。*/
    private final LinkedList<Chess> chess_list;
    /*该谁走棋了*/
    public Turn turn;
    /*当前棋局状态*/
    public State state;
    /*是否结束*/
    public boolean over;
    /*获取每一步的移动信息*/
    private MoveInfo info;
    /*悔棋是否合法*/
    private boolean retract_legal;
    /*步数统计*/
    public int step;
    /*随机名，用于图片命名；图片名称为seed.jpg*/
    private String seed;
    /*棋局开始时间：ms*/
    private long start_time;
    /*上一步结束的时间：ms*/
    private long end_time;
    /*红方用时总计：ms*/
    private long red_use_time;
    /*黑方用时总计：ms*/
    private long black_use_time;
    /*上一步，也就是最新步用时：ms*/
    private long last_step_use_time;


    public ChessControl(String seed)
    {
        this.map = new Chess[10][9];
        this.chess_list = new LinkedList<>();
        /*-------------------红方初始化-------------------*/
        this.chess_list.add(new Car(Team.red, 9, 0, getImage("车")));
        /*-------------------黑方初始化-------------------*/


        /*该谁走棋了*/
        this.turn = Turn.red;
        /*当前棋局状态*/
        this.state = State.init;
        /*是否结束*/
        this.over = false;
        /*获取每一步的移动信息*/
        this.info = null;
        /*悔棋是否合法*/
        this.retract_legal = false;
        /*步数统计*/
        this.step = 0;
        /*随机名，用于图片命名；图片名称为seed.jpg*/
        this.seed = seed;
    }

    public void start()
    {
        int i = 0;
        int j = 0;

        /*-------------------清空棋盘-------------------*/
        for(i = 0;i < 10;i++)
        {
            for(j = 0;j <9;j++)
            {
                this.map[i][j] = null;
            }
        }
        /*-------------------从棋子对象池里面读取棋子，放在对应位置-------------------*/
        for(Chess chess : chess_list)
        {
            chess.backToInitPos();
            this.map[chess.init_pos.x][chess.init_pos.y] = chess;
        }
        /*-------------------参数赋值-------------------*/
        /*该谁走棋了*/
        this.turn = Turn.red;
        /*当前棋局状态*/
        this.state = State.began;
        /*是否结束*/
        this.over = false;
        /*获取每一步的移动信息*/
        this.info = null;
        /*悔棋是否合法*/
        this.retract_legal = false;
        /*步数统计*/
        this.step = 0;
        /*随机名，用于图片命名；图片名称为seed.jpg*/
        this.seed = seed;
        /*棋局开始时间：ms*/
        this.start_time = System.currentTimeMillis();
        /*上一步结束的时间：ms*/
        this.end_time = this.start_time;
        /*红方用时总计：ms*/
        this.red_use_time = 0;
        /*黑方用时总计：ms*/
        this.black_use_time = 0;
        /*上一步，也就是最新步用时：ms*/
        this.last_step_use_time = 0;
    }

    public Chess[][] getMap()
    {
        return this.map;
    }

    private BufferedImage getImage(String name)
    {
        try
        {
            switch (name)
            {
                case "车":
                    return ImageIO.read(new File("background.jpg"));
            }
        }
        catch (IOException e)
        {
            e.printStackTrace();

        }
        return null;
    }
}
