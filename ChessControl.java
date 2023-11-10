package ChineseChess;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.LinkedList;


public class ChessControl 
{
    /*--------------------------------------
     * private静态变量
    ----------------------------------------*/

    /* 路径分隔符 */
    private static String separator = System.getProperty("path.separator");
    /* 图片根目录 */
    private static String img_dir = System.getProperty("user.dir");
    /* 棋子根目录 */
    private static String chess_dir = img_dir + separator + "chess";
    /* 棋盘根目录 */
    private static String map_dir = img_dir + separator + "chess";
    /* 走子提示根目录 */
    private static String remind_dir = img_dir + separator + "moveRemind";
    /* 输出根目录 */
    private static String out_dir = img_dir + separator + "out";


    /*--------------------------------------
     * public成员变量
    ----------------------------------------*/
    /*如果开启，那么对于士象炮帅的移动可以走出固定区域，参考翻翻棋*/
    public boolean wan_ning = false;

    /*该谁走棋了*/
    public Turn turn;
    /*当前棋局状态*/
    public State state;
    /*是否结束*/
    public boolean over;

    /*步数统计*/
    public int step;
    /*--------
     * 以下变量在start方法中不重新赋值
     * -------*/
    /*棋盘风格*/
    public MapStyle red_map_style;
    public MapStyle black_map_style;
    /*棋子风格*/
    public ChessStyle red_chess_style;
    public ChessStyle black_chess_style;
    /*是否盲棋*/
    public boolean blind_chess;

    /*--------------------------------------
     * private成员变量
    ----------------------------------------*/
    /*
    * 实时棋盘，是一个二维的列表，列表里面是棋子对象的引用，没有棋子的地方是None。
    * 通过name获取棋子的名字，toString方法输出格式为红/黑+棋子名字（注意红黑方的部分棋子名称不同）
    * 绘制棋盘也是读取这个二维列表的数据，通过棋子对象的引用.pos访问棋子坐标，如 引用.pos.x可以访问x坐标（坐标从0开始）
    */
    private Chess[][] map;
    /*棋子对象池,内部访问。初始化位置，只读不改。*/
    private final LinkedList<Chess> chess_list;
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
    /*获取每一步的移动信息*/
    private MoveInfo info;
    /*悔棋是否合法*/
    private boolean retract_legal;
    /*是否正确加载图片资源*/
    private boolean succ_load;



    public ChessControl(String seed)
    {
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
        /*棋盘风格*/
        this.red_map_style = MapStyle.defalt;
        this.black_map_style = MapStyle.defalt;
        /*棋子风格*/
        this.red_chess_style = ChessStyle.defalt;
        this.black_chess_style = ChessStyle.defalt;
        /*是否盲棋*/
        this.blind_chess = false;

        /* 棋盘 */
        this.map = new Chess[10][9];
        /* 棋子对象池 */
        this.chess_list = new LinkedList<>();
        /*-------------------红方初始化-------------------*/
        this.chess_list.add(new Car(Team.red, 9, 0, getImage(Team.red, "车")));
        /*-------------------黑方初始化-------------------*/

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

    public void move(String cmd) throws ChessNotFindExcept, CommandExcept, MoveExcept
    {
        String one;
        String two;
        String three;
        String four;
        Chess chess = null;

        /*--------------------命令效验---------------------*/
        if(cmd == null || cmd.length() != 4)
        {
            throw new CommandExcept();
        }
        
        one   = String.valueOf(cmd.charAt(0));
        two   = String.valueOf(cmd.charAt(1));
        three = String.valueOf(cmd.charAt(2));
        four  = String.valueOf(cmd.charAt(3));

        if(!("进".equals(three) || "退".equals(three) || "平".equals(three)))
        {
            throw new CommandExcept();
        }
        /*--------------------寻找棋子---------------------*/
        
        if("前".equals(one) || "后".equals(one))
        {
            chess = this.findChessSpec(one, two);
            
        }
        else
        {
            chess = this.findChessNormal(one, two);
        }
        if(chess == null)
        {
            throw new ChessNotFindExcept();
        }

        /*--------------------进行移动---------------------*/
        if(!this.moveChess(chess, three, four))
        {
            
        }


    }

    public void outMap()
    {

    }

    public Chess[][] getMap()
    {
        return this.map;
    }

    private Chess findChessNormal(String name, String cow)
    {
        Chess chess = null;
        //int cow = Chess.getCowByNum(this.turn, cow);

        return null;

    }

    private Chess findChessSpec(String dir, String name)
    {
        return null;

    }

    private boolean moveChess(Chess chess, String dir, String where) throws MoveExcept, CommandExcept
    {
        chess.move(dir, where, this.map);

        return true;
    }

    private BufferedImage getImage(Team belong, String chess_name)
    {
        try
        {
            switch (chess_name)
            {
                case "车":
                    return this.combinedChessPath(belong, "cannon.png");
                case "马":
                    return this.combinedChessPath(belong, "knight.png");
                case "炮":
                    return this.combinedChessPath(belong, "rook.png");
                case "相", "象":
                    return this.combinedChessPath(belong, "elephant.png");
                case "士", "仕":
                    return this.combinedChessPath(belong, "mandarin.png");
                case "兵", "卒":
                    return this.combinedChessPath(belong, "pawn.png");
                case "帅", "将":
                    return this.combinedChessPath(belong, "king.png");
                default:
                {
                    this.succ_load = false;
                    return null;
                }
            }
        }
        catch (ImageNotFindExcept e)
        {
            this.succ_load = false;
            System.out.println(e);
        }
        return null;
    }

    private BufferedImage combinedChessPath(Team belong, String chess_ing_name) throws ImageNotFindExcept
    {
        try
        {
            if(belong.equals(Team.red))
            {
                return ImageIO.read(new File(chess_dir + separator + this.red_chess_style.toString() + separator + "red_" + chess_ing_name));
            }
            else
            {
                return ImageIO.read(new File(chess_dir + separator + this.black_chess_style.toString() + separator + "black_" + chess_ing_name));
            }

        }
        catch(IOException e)
        {
            throw new ImageNotFindExcept(chess_ing_name);
        }
        
    }

    private BufferedImage getMapImage()
    {
        return null;
    }

}
