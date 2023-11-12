package ChineseChess;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import static ChineseChess.Tool.*;

public abstract class Chess
{
    public Chess(String name, Team team, Vector2 init_pos)
    {
        this.name = name;
        this.team = team;
        this.init_pos = init_pos;
        this.pos = new Vector2(init_pos.x, init_pos.y);
        this.style = ChessStyle.defalt;
        this.img = this.getImage();
        this.info = new MoveInfo();
    }
    /*--------------------------------------
     * private静态变量
    ----------------------------------------*/
    /*棋子名*/
    public String name;
    /*棋子所属阵营*/
    public Team team;
    /*棋子位置坐标*/
    public Vector2 pos;
    /*棋子初始位置*/
    public Vector2 init_pos;
    /*棋子图片素材*/
    public BufferedImage img;
    /*当前步移动信息*/
    protected MoveInfo info;
    /*是否普通行棋，用于判断棋子是否可以出界（特殊：翻翻棋）*/
    public boolean normal_chess = true;
    /*棋子风格*/
    protected ChessStyle style;

    public final MoveInfo move(String dir, String where, Chess[][] map) throws MoveExcept, CommandExcept
    {
        String fun_name = "move";
        boolean is_legal = false;
        Chess target = null;

        log(Log.debug, fun_name, this.name + "进行移动：" + dir + where);
        this.init_info();
        is_legal = this.isLegal(dir, where, map);
        if (!is_legal)
        {
            throw new CommandExcept();
        }
        if (this.info.out_local && this.normal_chess)
        {
            throw new CommandExcept();
        }

        target = map[this.info.new_x][this.info.new_y];
        if (target != null && target.team.equals(this.team))
        {
            throw new MoveExcept();
        }

        map[this.info.new_x][this.info.new_y] = this;
        map[this.pos.x][this.pos.y] = null;

        this.info.target = target;
        this.info.x = this.pos.x;
        this.info.y = this.pos.y;
        this.updatePos(this.info.new_x, this.info.new_y);

        log(Log.debug, fun_name, this.name + "移动成功，坐标：x = " + this.pos.x + ", y = " + this.pos.y);
        return this.info;
    }

    protected abstract boolean isLegal(String dir, String where, Chess[][] map);

    protected final void changeStyle(ChessStyle style)
    {

    }

    /*
     * 每次移动记得更新位置
     */
    public final void updatePos(int new_x, int new_y)
    {
        this.pos.x = new_x;
        this.pos.y = new_y;
    }

    /*
     * 回到初始位置
     */
    public final void backToInitPos()
    {
        this.pos.x = this.init_pos.x;
        this.pos.y = this.init_pos.y;
    }

    public final int getCowByNum(String num)
    {
        if (num == null)
        {
            return -1;
        }
        switch (num)
        {
            case "1", "一" ->
            {
                if (this.team == Team.red)
                {
                    return 8;
                }
                else
                {
                    return 0;
                }
            }
            case "2", "二" ->
            {
                if (this.team == Team.red)
                {
                    return 7;
                }
                else
                {
                    return 1;
                }
            }
            case "3", "三" ->
            {
                if (this.team == Team.red)
                {
                    return 6;
                }
                else
                {
                    return 2;
                }
            }
            case "4", "四" ->
            {
                if (this.team == Team.red)
                {
                    return 5;
                }
                else
                {
                    return 3;
                }
            }
            case "5", "五" ->
            {
                return 4;
            }
            case "6", "六" ->
            {
                if (this.team == Team.red)
                {
                    return 3;
                }
                else
                {
                    return 5;
                }
            }
            case "7", "七" ->
            {
                if (this.team == Team.red)
                {
                    return 2;
                }
                else
                {
                    return 6;
                }
            }
            case "8", "八" ->
            {
                if (this.team == Team.red)
                {
                    return 1;
                }
                else
                {
                    return 7;
                }
            }
            case "9", "九" ->
            {
                if (this.team == Team.red)
                {
                    return 0;
                }
                else
                {
                    return 8;
                }
            }
            default ->
            {
                return -1;
            }
        }
    }

    protected final void init_info()
    {
        this.info.out_local = false;
        this.info.target = null;
    }

    @Override
    public String toString()
    {
        return this.team.toString() + ":" + this.name;
    }



    private BufferedImage getImage()
    {
        String fun_name = "getImage";
        try
        {
            switch (this.name)
            {
                case "车" ->
                {
                    return this.combinedChessPath("rook.png");
                }
                case "马" ->
                {
                    return this.combinedChessPath("knight.png");
                }
                case "炮" ->
                {
                    return this.combinedChessPath("cannon.png");
                }
                case "相", "象" ->
                {
                    return this.combinedChessPath("elephant.png");
                }
                case "士", "仕" ->
                {
                    return this.combinedChessPath("mandarin.png");
                }
                case "兵", "卒" ->
                {
                    return this.combinedChessPath("pawn.png");
                }
                case "帅", "将" ->
                {
                    return this.combinedChessPath("king.png");
                }
                default ->
                {
                    //this.succ_load = false;
                    return null;
                }
            }
        }
        catch (ImageNotFindExcept e)
        {
            Tool.log(Log.error, fun_name, e.toString());
        }
        return null;
    }

    private BufferedImage combinedChessPath(String chess_img_name) throws ImageNotFindExcept
    {
        String img_name = null;
        if(this.team.equals(Team.red))
        {
            img_name = "red_" + chess_img_name;
        }
        else
        {
            img_name = "black_" + chess_img_name;
        }
        try
        {
            return ImageIO.read(new File(chess_dir + separator + this.style.toString() + separator + img_name));
        }
        catch(IOException e)
        {
            throw new ImageNotFindExcept(chess_dir + separator + this.style.toString() + separator + img_name);
        }

    }



    public static int getDisByNum(String num)
    {
        return switch (num)
                {
                    case "1", "一" -> 1;
                    case "2", "二" -> 2;
                    case "3", "三" -> 3;
                    case "4", "四" -> 4;
                    case "5", "五" -> 5;
                    case "6", "六" -> 6;
                    case "7", "七" -> 7;
                    case "8", "八" -> 8;
                    case "9", "九" -> 9;
                    default -> -1;
                };
    }

    public static int getCowByNum(Turn turn, String num)
    {
        if (num == null)
        {
            return -1;
        }
        switch (num)
        {
            case "1", "一" ->
            {
                if (turn == Turn.red)
                {
                    return 8;
                }
                else
                {
                    return 0;
                }
            }
            case "2", "二" ->
            {
                if (turn == Turn.red)
                {
                    return 7;
                }
                else
                {
                    return 1;
                }
            }
            case "3", "三" ->
            {
                if (turn == Turn.red)
                {
                    return 6;
                }
                else
                {
                    return 2;
                }
            }
            case "4", "四" ->
            {
                if (turn == Turn.red)
                {
                    return 5;
                }
                else
                {
                    return 3;
                }
            }
            case "5", "五" ->
            {
                return 4;
            }
            case "6", "六" ->
            {
                if (turn == Turn.red)
                {
                    return 3;
                }
                else
                {
                    return 5;
                }
            }
            case "7", "七" ->
            {
                if (turn == Turn.red)
                {
                    return 2;
                }
                else
                {
                    return 6;
                }
            }
            case "8", "八" ->
            {
                if (turn == Turn.red)
                {
                    return 1;
                }
                else
                {
                    return 7;
                }
            }
            case "9", "九" ->
            {
                if (turn == Turn.red)
                {
                    return 0;
                }
                else
                {
                    return 8;
                }
            }
            default ->
            {
                return -1;
            }
        }
    }

}