package ChineseChess.Core;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.LinkedList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static ChineseChess.Core.Tool.*;


public final class ChessControl
{
    /*--------------------------------------
     * static私有变量
    ----------------------------------------*/
    private static final String normal_cmd = "^[车马炮象相仕士将帅兵卒][1-9一二三四五六七八九][进退平][1-9一二三四五六七八九]$";
    private static final Pattern normal = Pattern.compile(normal_cmd);

    private static final String special_cmd = "^[前后][车马炮象相仕士兵卒][进退平][1-9一二三四五六七八九]$";
    private static final Pattern special = Pattern.compile(special_cmd);
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
    /*赢家*/
    public Team winner;

    /*--------------------------------------
     * private成员变量
    ----------------------------------------*/
    /*
    * 实时棋盘，是一个二维的列表，列表里面是棋子对象的引用，没有棋子的地方是None。
    * 通过name获取棋子的名字，toString方法输出格式为红/黑+棋子名字（注意红黑方的部分棋子名称不同）
    * 绘制棋盘也是读取这个二维列表的数据，通过棋子对象的引用.pos访问棋子坐标，如 引用.pos.x可以访问x坐标（坐标从0开始）
    */
    private final Chess[][] map;
    /*棋子对象池,内部访问。初始化位置，只读不改。*/
    private final LinkedList<Chess> chess_list;
    /*随机名，用于图片命名；图片名称为seed.jpg*/
    private String seed;
    /*棋局开始时间：ms*/
    private long start_time;
    /*上一步结束的时间：ms*/
    private long last_step_end_time;
    /*上一步所用时间：ms*/
    public long last_step_use_time;
    /*红方用时总计：ms*/
    private long red_use_time;
    /*黑方用时总计：ms*/
    private long black_use_time;

    /*获取每一步的移动信息*/
    private MoveInfo info;
    /*悔棋是否合法*/
    private boolean retract_legal;
    /*红方棋盘图片资源*/
    private BufferedImage red_map;
    /*黑方棋盘图片资源*/
    private BufferedImage black_map;
    /*红方提示图片资源*/
    private BufferedImage red_begin;
    private BufferedImage red_end;
    /*黑方提示图片资源*/
    private BufferedImage black_begin;
    private BufferedImage black_end;
    /*输出图片*/
    public final File out_img;





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
        /*红方棋盘图片资源*/
        this.changeMapStyle(Team.red, MapStyle.defalt);
        this.red_begin = loadImg(remind_dir + separator + "默认" + separator + "red_begin.png");
        this.red_end = loadImg(remind_dir + separator + "默认" + separator + "red_end.png");
        /*黑方棋盘图片资源*/
        this.changeMapStyle(Team.black, MapStyle.defalt);
        this.black_begin = loadImg(remind_dir + separator + "默认" + separator + "black_begin.png");
        this.black_end = loadImg(remind_dir + separator + "默认" + separator + "black_end.png");
        /*输出图片*/
        this.out_img = new File(out_dir + separator + seed + ".jpg");



        /* 棋盘 */
        this.map = new Chess[10][9];
        /* 棋子对象池 */
        this.chess_list = new LinkedList<>();
        /*-------------------红方初始化-------------------*/
        this.chess_list.add(new Car(Team.red, 9, 0));
        this.chess_list.add(new Horse(Team.red, 9, 1));
        this.chess_list.add(new Elephant(Team.red, 9, 2));
        this.chess_list.add(new Guard(Team.red, 9, 3));
        this.chess_list.add(new Commander(Team.red, 9, 4));
        this.chess_list.add(new Guard(Team.red, 9, 5));
        this.chess_list.add(new Elephant(Team.red, 9, 6));
        this.chess_list.add(new Horse(Team.red, 9, 7));
        this.chess_list.add(new Car(Team.red, 9, 8));
        this.chess_list.add(new Artillery(Team.red, 7, 1));
        this.chess_list.add(new Artillery(Team.red, 7, 7));
        this.chess_list.add(new Soldier(Team.red, 6, 0));
        this.chess_list.add(new Soldier(Team.red, 6, 2));
        this.chess_list.add(new Soldier(Team.red, 6, 4));
        this.chess_list.add(new Soldier(Team.red, 6, 6));
        this.chess_list.add(new Soldier(Team.red, 6, 8));
        /*-------------------黑方初始化-------------------*/
        this.chess_list.add(new Car(Team.black, 0, 0));
        this.chess_list.add(new Horse(Team.black, 0, 1));
        this.chess_list.add(new Elephant(Team.black, 0, 2));
        this.chess_list.add(new Guard(Team.black, 0, 3));
        this.chess_list.add(new Commander(Team.black, 0, 4));
        this.chess_list.add(new Guard(Team.black, 0, 5));
        this.chess_list.add(new Elephant(Team.black, 0, 6));
        this.chess_list.add(new Horse(Team.black, 0, 7));
        this.chess_list.add(new Car(Team.black, 0, 8));
        this.chess_list.add(new Artillery(Team.black, 2, 1));
        this.chess_list.add(new Artillery(Team.black, 2, 7));
        this.chess_list.add(new Soldier(Team.black, 3, 0));
        this.chess_list.add(new Soldier(Team.black, 3, 2));
        this.chess_list.add(new Soldier(Team.black, 3, 4));
        this.chess_list.add(new Soldier(Team.black, 3, 6));
        this.chess_list.add(new Soldier(Team.black, 3, 8));

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
        this.last_step_end_time = this.start_time;
        /*上一步所用时间：ms*/
        this.last_step_use_time = 0;
        /*红方用时总计：ms*/
        this.red_use_time = 0;
        /*黑方用时总计：ms*/
        this.black_use_time = 0;
        /*赢家*/
        this.winner = null;
        /*画初始棋盘*/
        this.drawMap();

    }

    public void move(String cmd) throws ChessNotFindExcept, CommandExcept, MoveExcept
    {
        int check = 0;
        String one;
        String two;
        String three;
        String four;
        Chess chess = null;

        /*--------------------命令效验---------------------*/
        check = checkMoveCmd(cmd);
        if(check != 1 && check != 2)
        {
            throw new CommandExcept();
        }

        /*--------------------寻找棋子---------------------*/
        one   = String.valueOf(cmd.charAt(0));
        two   = String.valueOf(cmd.charAt(1));
        three = String.valueOf(cmd.charAt(2));
        four  = String.valueOf(cmd.charAt(3));
        
        if(check == 2)
        {
            chess = this.findChessSpec(one, two);
            
        }
        else
        {
            chess = this.findChessNormal(one, two, three);
        }

        if(chess == null)
        {
            throw new ChessNotFindExcept();
        }

        /*--------------------进行移动---------------------*/
        this.info = chess.move(three, four, this.map);
        /*--------------------移动成功，更新变量---------------------*/
        this.goStep();
        this.drawMap();
    }

    public void over(Team winner)
    {
        String fun_name = "over";
        this.last_step_use_time = System.currentTimeMillis() -this.last_step_end_time;

        Tool.log(Log.info, fun_name, "over调用，棋局结束，winner = " + winner.toString());
        if (this.turn == Turn.red)
        {
            this.red_use_time += this.last_step_use_time;
        }
        else
        {
            this.black_use_time += this.last_step_use_time;
        }
        this.state = State.init;
        this.winner = winner;
    }

    public void retract() throws RetractExcept
    {
        String fun_name = "retract";
        Chess moved = null;
        int x = 0;
        int y = 0;

        if(!this.retract_legal)
        {
            Tool.log(Log.info, fun_name, "悔棋失败：retract_legal=false");
            throw new RetractExcept();
        }

        moved = this.map[this.info.new_x][this.info.new_y];
        if(moved == null)
        {
            Tool.log(Log.error, fun_name, "错误：已经移动的棋子为空");
            throw new RetractExcept();
        }

        x = this.info.x;
        y = this.info.y;
        this.changeTurn();

        map[x][y] = moved;
        moved.updatePos(x, y);
        this.map[this.info.new_x][this.info.new_y] = this.info.target;

        this.info.x = this.info.new_x;
        this.info.y = this.info.new_y;
        this.info.new_x = x;
        this.info.new_y = y;

        this.retract_legal = false;
        this.step --;
        Tool.log(Log.debug, fun_name, "悔棋成功,name = " + moved.name);
    }

    /*此方法在转换轮次后进行绘制*/
    public void drawMap()
    {
        String fun_name = "drawMap";
        /*输出图片:720*820 px*/
        BufferedImage out = new BufferedImage(720, 820, BufferedImage.TYPE_INT_ARGB);
        /*获取Graphics2D对象*/
        Graphics2D graphics = out.createGraphics();

        int r = 0;
        int c = 0;
        Chess temp = null;

        /*---------------先画棋盘----------------*/
        if (this.turn == Turn.red)
        {
            graphics.drawImage(this.red_map, 0 , 0, null);
        }
        else
        {
            graphics.drawImage(this.black_map, 0 , 0, null);
        }
        /*---------------再画提示----------------*/
        if(this.info != null)
        {
            if (this.turn == Turn.red)
            {
                graphics.drawImage(this.black_begin, 8 + this.info.y * 80, 18 + this.info.x * 80, null);
                graphics.drawImage(this.black_end, 4 + this.info.new_y * 80, 14 + this.info.new_x * 80, null);
            }
            else
            {
                graphics.drawImage(this.red_begin, 8 + (8 - this.info.y) * 80, 18 + (9 - this.info.x) * 80, null);
                graphics.drawImage(this.red_end, 4 + (8 - this.info.new_y) * 80, 14 + (9 - this.info.new_x) * 80, null);
            }
        }

        /*---------------最后棋子----------------*/
        for(r = 0; r < 10; r++)
        {
            for(c = 0; c < 9;c++)
            {
                temp = this.map[r][c];
                if(temp == null)
                {
                    continue;
                }
                if (this.turn == Turn.red)
                {
                    graphics.drawImage(temp.img, 8 + temp.pos.y * 80, 18 + temp.pos.x * 80, null);
                }
                else
                {
                    int x = 9 - temp.pos.x;
                    int y = 8 - temp.pos.y;
                    graphics.drawImage(temp.img, 8 + y * 80, 18 + x * 80, null);
                }
            }
        }
        try
        {
            ImageIO.write(out, "png", this.out_img);
            graphics.dispose();
            log(Log.debug, fun_name, "成功输出图片到{" + this.out_img.getAbsolutePath() + "}");
        }
        catch (IOException e)
        {
            log(Log.error, fun_name, "绘制棋局图片到{" + this.out_img.getAbsolutePath() + "}失败");
        }


    }



    public Chess[][] getMap()
    {
        return this.map;
    }

    public void changeMapStyle(Team team, MapStyle style)
    {
        String fun_name = "changeMapStyle";
        if (team == Team.red)
        {
            this.red_map = loadImg(map_dir + separator +style.toString() + separator + "map_red.jpg");

        }
        else
        {
            this.black_map = loadImg(map_dir + separator + style.toString() + separator + "map_black.jpg");
        }
        log(Log.info, fun_name, team.toString() + "方切换棋盘风格：" + style.toString());
    }

    private Chess findChessNormal(String name, String where, String dir) throws ChessNotFindExcept
    {
        String fun_name = "findChessNormal";
        /*匹配的第一颗棋子，兵很少一列超过两个，就不考虑了*/
        Chess chess1 = null;
        Chess chess2 = null;
        Chess temp   = null;
        int cow = 0;
        int idx = 0;

        log(Log.debug, fun_name, "进入寻找棋子：" + name + where);
        cow = Chess.getCowByNum(this.turn, where);
        if (cow < 0)
        {
            return null;
        }

        for (idx = 0; idx < 10; idx++)
        {
            temp = this.map[idx][cow];
            if(temp != null && temp.name.equals(name) && temp.team.name().equals(this.turn.name()))
            {
                if (chess1 == null)
                {
                    log(Log.debug, fun_name, "找到第一颗棋子：" + temp.name + temp.pos.toString());
                    chess1 = temp;
                }
                else
                {
                    log(Log.debug, fun_name, "找到第二颗棋子：" + temp.name + temp.pos.toString());
                    chess2 = temp;
                    break;
                }
            }
        }
        if (chess1 == null)
        {
            log(Log.debug, fun_name, "棋子也没找到");
            throw new ChessNotFindExcept();
        }
        else if (chess2 != null)
        {
            if((dir.equals("进") && this.turn == Turn.red) || (dir.equals("退") && this.turn == Turn.black))
            {
                return chess2;
            }
        }
        return chess1;
    }

    private Chess findChessSpec(String dir, String name) throws ChessNotFindExcept
    {
        String fun_name = "findChessSpec";
        Chess chess1 = null;
        Chess chess2 = null;
        Chess temp   = null;
        int r = 0;
        int c = 0;

        log(Log.debug, fun_name, "进入寻找棋子：" + dir + name);
        for (r = 0; r < 10; r++)
        {
            for(c = 0; c < 9; c++)
            {
                temp = map[r][c];
                if(temp != null && temp.name.equals(name) && temp.team.name().equals(this.turn.name()))
                {
                    if (chess1 == null)
                    {
                        log(Log.debug, fun_name, "找到第一颗棋子：" + temp.name + temp.pos.toString());
                        chess1 = temp;
                    }
                    else
                    {
                        log(Log.debug, fun_name, "找到第二颗棋子：" + temp.name + temp.pos.toString());
                        chess2 = temp;
                        break;
                    }
                }
            }
        }

        if(chess1 == null || chess2 == null)
        {
            log(Log.debug, fun_name, "错误，没有找到前后两个棋子");
            throw new ChessNotFindExcept();
        }

        if((dir.equals("前") && this.turn == Turn.black) || (dir.equals("后") && this.turn == Turn.red))
        {
            log(Log.debug, fun_name, "返回棋子：" + chess2.name + chess2.pos.toString());
            return chess2;
        }

        log(Log.debug, fun_name, "返回棋子：" + chess1.name + chess1.pos.toString());
        return chess1;
    }

    /*
    * return 0: 两种形式都不匹配
    * return 1: 匹配normal
    * return 2: 匹配special
    */
    private static int checkMoveCmd(String cmd)
    {
        if (cmd == null || cmd.length() != 4) {
           return 0;
        }
        Matcher matcher = null;

        matcher = normal.matcher(cmd);
        if(matcher.matches())
        {
            return 1;
        }

        matcher = special.matcher(cmd);
        if(matcher.matches())
        {
            return 2;
        }

        return 0;
    }


    private Team getTeam()
    {
        if (this.turn == Turn.red)
        {
            return Team.red;
        }
        else
        {
            return Team.black;
        }

    }

    private void goStep()
    {
        long current = System.currentTimeMillis();

        this.step += 1;
        this.retract_legal = true;
        this.last_step_use_time = current - this.last_step_end_time;
        if (this.turn == Turn.red)
        {
            this.red_use_time +=  this.last_step_use_time;
        }
        else
        {
            this.black_use_time +=  this.last_step_use_time;
        }
        this.last_step_end_time = current;

        /*--------------------如果是将帅---------------------*/
        if(this.info.target != null && (this.info.target.name.equals("将") || this.info.target.name.equals("帅")))
        {
            this.over(getTeam());
        }

        /*最后改变轮次*/
        this.changeTurn();

    }

    private void changeTurn()
    {
        if (this.turn == Turn.red)
        {
            this.turn = Turn.black;
        }
        else
        {
            this.turn = Turn.red;
        }
    }



}
