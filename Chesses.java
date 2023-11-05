package ChineseChess;

import java.awt.image.BufferedImage;
import java.io.File;


/**
 * Horse马
 */
class Horse extends Chess
{
    @Override
    protected boolean isLegal(String dir, String where, Chess[][] map)
    {
        int cow = getCowByNum(where);
        int dy = cow - this.pos.y;
        int dx = 0;

        //马不可以平,也不可以越界
        if (dir.equals("平") || cow < 0)
        {
            return false;
        }
        //进
        else if ((dir.equals("进") && this.team.equals(Team.red)) || (dir.equals("退") && this.team.equals(Team.black)))
        {
            if (this.pos.x < 1)
            {
                return false;
            }
            //横着走
            if (dy == 2)
            {
                //撇脚
                if (map[this.pos.x][this.pos.y + 1] != null)
                {
                    return false;
                }
                else
                {
                    dx = -1;

                }
            }
            else if (dy == -2)
            {
                //撇脚
                if (map[this.pos.x][this.pos.y - 1] != null)
                {
                    return false;
                }
                else
                {
                    dx = -1;
                }
            }
            else if (dy == 1 || dy == -1)
            {
                if (this.pos.x < 2)
                {
                    return false;
                }
                //撇脚
                if (map[this.pos.x - 1][this.pos.y] != null)
                {
                    return false;
                }
                else
                {
                    dx = -2;
                }
            }
            else
            {
                return false;
            }
        }

        else if ((dir.equals("退") && this.team.equals(Team.red)) || (dir.equals("进") && this.team.equals(Team.black)))
        {
            if (this.pos.x > 8)
            {
                return false;
            }
            //横着走
            if (dy == 2)
            {
                //撇脚
                if (map[this.pos.x][this.pos.y + 1] != null)
                {
                    return false;
                }
                else
                {
                    dx = 1;

                }
            }
            else if (dy == -2)
            {
                //撇脚
                if (map[this.pos.x][this.pos.y - 1] != null)
                {
                    return false;
                }
                else
                {
                    dx = 1;
                }
            }
            else if (dy == 1 || dy == -1)
            {
                if (this.pos.x > 7)
                {
                    return false;
                }
                //撇脚
                if (map[this.pos.x + 1][this.pos.y] != null)
                {
                    return false;
                }
                else
                {
                    dx = 2;
                }
            }
            else
            {
                return false;
            }
        }

        this.info.new_x = this.pos.x + dx;
        this.info.y = this.pos.y + dy;
        return true;
    }


    public Horse(Team team, int x, int y, BufferedImage img)
    {
        super("马", team, new Vector2(x, y), img);
    }

}

/**
 * Car车
 */
class Car extends Chess
{
    public Car(Team team, int x, int y, BufferedImage image)
    {
        super("车", team, new Vector2(x, y), image);
    }


    @Override
    public boolean isLegal(String dir, String where, Chess[][] map)
    {
        int cow = 0;
        int idx = 0;
        int dx = 0;
        if (dir.equals("平"))
        {
            cow = getCowByNum(where);
            if (cow < 0)
            {
                return false;
            }
            else if (cow > this.pos.y)
            {
                //路径上没有棋子
                for (idx = this.pos.y + 1; idx < cow; idx++)
                {
                    if (map[this.pos.x][idx] != null)
                    {
                        return false;
                    }
                }
            }
            else if (cow == this.pos.y)
            {
                return false;
            }
            else
            {
                //路径上没有棋子
                for (idx = this.pos.y - 1; idx > cow; idx--)
                {
                    if (map[this.pos.x][idx] != null)
                    {
                        return false;
                    }
                }
            }

            this.info.new_x = this.pos.x;
            this.info.new_y = cow;
            return true;
        }
        dx = getDisByNum(where);
        if (dx < 0)
        {
            return false;
        }
        if ((dir.equals("进") && this.team.equals(Team.red)) || (dir.equals("退") && this.team.equals(Team.black)))
        {
            if (this.pos.x - dx < 0)
            {
                return false;
            }
            for (idx = this.pos.x - 1; idx > this.pos.x - dx; idx--)
            {
                if (map[idx][this.pos.y] != null)
                {
                    return false;
                }
            }
            this.info.new_x = this.pos.x - dx;
            this.info.new_y = this.pos.y;
        }
        else if ((dir.equals("退") && this.team.equals(Team.red)) || (dir.equals("进") && this.team.equals(Team.black)))
        {
            if (this.pos.x + dx > 9)
            {
                return false;
            }
            for (idx = this.pos.x + 1; idx < this.pos.x + dx; idx++)
            {
                if (map[idx][this.pos.y] != null)
                {
                    return false;
                }
            }
            this.info.new_x = this.pos.x + dx;
            this.info.new_y = this.pos.y;
        }

        return true;
    }


}


/**
 * Elephant象
 */
class Elephant extends Chess
{
    @Override
    protected boolean isLegal(String dir, String where, Chess[][] map)
    {
        int cow = getCowByNum(where);
        int dy = cow - this.pos.y;
        int dx = 0;

        //象不可以平,也不可以越界
        if (dir.equals("平") || cow < 0)
        {
            return false;
        }
        //进
        if (dir.equals("进") && this.team.equals(Team.red))
        {
            dx = -2;
            if (dy == 2)
            {
                //撇脚
                if (map[this.pos.x - 1][this.pos.y + 1] != null)
                {
                    return false;
                }

            }
            else if (dy == -2)
            {
                //撇脚
                if (map[this.pos.x - 1][this.pos.y - 1] != null)
                {
                    return false;
                }

            }
            else
            {
                return false;
            }
        }
        else if (dir.equals("退") && this.team.equals(Team.red))
        {
            if (this.pos.x > 7)
            {
                return false;
            }

            dx = 2;
            if (dy == 2)
            {
                //撇脚
                if (map[this.pos.x + 1][this.pos.y + 1] != null)
                {
                    return false;
                }

            }
            else if (dy == -2)
            {
                //撇脚
                if (map[this.pos.x + 1][this.pos.y - 1] != null)
                {
                    return false;
                }
            }
            else
            {
                return false;
            }
        }
        else if (dir.equals("进") && this.team.equals(Team.black))
        {
            dx = 2;
            if (dy == 2)
            {
                //撇脚
                if (map[this.pos.x + 1][this.pos.y + 1] != null)
                {
                    return false;
                }

            }
            else if (dy == -2)
            {
                //撇脚
                if (map[this.pos.x + 1][this.pos.y - 1] != null)
                {
                    return false;
                }

            }
            else
            {
                return false;
            }
        }
        else if (dir.equals("退") && this.team.equals(Team.black))
        {
            if (this.pos.x < 2)
            {
                return false;
            }

            dx = -2;
            if (dy == 2)
            {
                //撇脚
                if (map[this.pos.x - 1][this.pos.y + 1] != null)
                {
                    return false;
                }

            }
            else if (dy == -2)
            {
                //撇脚
                if (map[this.pos.x - 1][this.pos.y - 1] != null)
                {
                    return false;
                }
            }
            else
            {
                return false;
            }
        }
        else
        {
            return false;
        }

        this.info.new_x = this.pos.x + dx;
        this.info.new_y = this.pos.y + dy;
        /*命令OK,下面作是否出界的判断*/
        if (this.team.equals(Team.red))
        {
            if (this.info.new_x < 5)
            {
                this.info.out_local = true;
            }
        }
        else
        {
            if (this.info.new_x > 4)
            {
                this.info.out_local = true;
            }
        }
        return true;
    }

    public Elephant(Team team, int x, int y, BufferedImage image)
    {
        super(initName(team), team, new Vector2(x, y), image);
    }

    private static String initName(Team team)
    {
        if (team.equals(Team.red))
        {
            return "相";
        }
        else
        {
            return "象";
        }
    }
}


/**
 * Guard士
 */
class Guard extends Chess
{
    @Override
    protected boolean isLegal(String dir, String where, Chess[][] map)
    {
        int cow = getCowByNum(where);
        int dy = cow - this.pos.y;
        int dx = 1;

        if (dy != 1 && dy != -1)
        {
            return false;
        }
        //仕不可以平,也不可以越界
        if (dir.equals("平") || cow < 0)
        {
            return false;
        }

        if (this.team.equals(Team.red))
        {
            if (dir.equals("进"))
            {
                if (this.pos.x < 1)
                {
                    return false;
                }
                dx = -1;
            }
            else
            {
                if (this.pos.x > 8)
                {
                    return false;
                }
            }
        }
        else
        {
            if (dir.equals("退"))
            {
                if (this.pos.x < 1)
                {
                    return false;
                }
                dx = -1;
            }
            else
            {
                if (this.pos.x > 8)
                {
                    return false;
                }
            }
        }


        this.info.new_x = this.pos.x + dx;
        this.info.y = cow;
        /*命令ok，开始判断是否出界*/
        if (cow > 5 || cow < 3)
        {
            this.info.out_local = true;
        }
        else if (this.team.equals(Team.red) && this.info.new_x < 7)
        {
            this.info.out_local = true;
        }
        else if (this.team.equals(Team.black) && this.info.new_x > 2)
        {
            this.info.out_local = true;
        }
        return true;
    }

    public Guard(String name, Team team, int x, int y, BufferedImage image)
    {
        super(name, team, new Vector2(x, y), image);
    }
}


/**
 * Artillery炮
 */
class Artillery extends Chess
{
    @Override
    protected boolean isLegal(String dir, String where, Chess[][] map)
    {
        int cow = 0;
        int dis = 0;
        int idx = 0;
        int dx = 0;
        int dy = 0;
        int x = this.pos.x;
        int y = this.pos.y;
        int chess_count = 0;

        if(dir.equals("平"))
        {
            cow = getCowByNum(where);
            if (cow<0)
            {
                return false;
            }
            /*查找路径上有几颗棋子*/
            if(cow > y)
            {
                for (idx = y + 1;idx<cow;idx++)
                {
                    if(map[x][idx] != null)
                    {
                        chess_count++;
                    }
                }
            }
            else if (cow < y)
            {
                for (idx = cow + 1;idx<y;idx++)
                {
                    if(map[x][idx] != null)
                    {
                        chess_count++;
                    }
                }
            }
            else
            {
                return false;
            }
            /*判断路径上的棋子数量是否合法*/
            /*为0则终点不能有棋子*/
            if(chess_count ==0)
            {
                if(map[x][cow] !=null)
                {
                    return false;
                }
            }
            /*为1则终点有棋子*/
            else if (chess_count == 1)
            {
                if(map[x][cow] ==null)
                {
                    return false;
                }
            }
            /*超过1则不合法*/
            else
            {
                return false;
            }
            /*平只改变y坐标*/
            dy = cow - y;
        }
        else
        {
            dis = getDisByNum(where);
            if(dis < 0)
            {
                return false;
            }
            /*进退只改变x坐标*/
            if ((this.team.equals(Team.red) && dir.equals("进")) || (this.team.equals(Team.black) && dir.equals("退")))
            {
                dx = -dis;
            }
            else
            {
                dx = dis;
            }
            /*是否移出棋盘*/
            if(x + dx < 0 || x + dx >9)
            {
                return false;
            }

            /*查找路径上有几颗棋子*/
            if(dx > 0)
            {
                for (idx = x + 1;idx<x + dx;idx++)
                {
                    if(map[idx][y] != null)
                    {
                        chess_count++;
                    }
                }
            }
            else
            {
                for (idx = x + dx;idx<x;idx++)
                {
                    if(map[idx][y] != null)
                    {
                        chess_count++;
                    }
                }
            }

            /*判断路径上的棋子数量是否合法*/
            /*为0则终点不能有棋子*/
            if(chess_count ==0)
            {
                if(map[x+dx][y] !=null)
                {
                    return false;
                }
            }
            /*为1则终点有棋子*/
            else if (chess_count == 1)
            {
                if(map[x + dx][y] ==null)
                {
                    return false;
                }
            }
            /*超过1则不合法*/
            else
            {
                return false;
            }
        }

        this.info.new_x = x + dx;
        this.info.new_y = y + dy;
        return true;
    }

    public Artillery(Team team, int x, int y, BufferedImage image)
    {
        super("炮", team, new Vector2(x, y), image);
    }
}

class Soldier extends Chess
{
    @Override
    protected boolean isLegal(String dir, String where, Chess[][] map)
    {
        int cow = 0;
        int dis = 0;
        int dx = 0;
        int dy = 0;
        int x = this.pos.x;
        int y = this.pos.y;
        if(dir.equals("退"))
        {
            return false;
        }
        else if (dir.equals("平"))
        {
            cow = getCowByNum(where);
            if(cow < 0)
            {
                return false;
            }
            /*未过河小兵不能平*/
            if((this.team.equals(Team.red) && x > 4) || (this.team.equals(Team.black) && x < 5))
            {
                return false;
            }

            dy = cow - y;
            /*小兵只能移动一格*/
            if (dy != -1 && dy !=1)
            {
                return false;
            }
        }
        /*只剩下"进"的情况了*/
        else if(this.team.equals(Team.red))
        {
            dis = getDisByNum(where);
            /*只能进一格，到底了不能进*/
            if (dis != 1 || x < 1)
            {
                return false;
            }
            dx = -1;
        }
        else if(this.team.equals(Team.black))
        {
            dis = getDisByNum(where);
            /*只能进一格，到底了不能进*/
            if (dis != 1 || x > 8)
            {
                return false;
            }
            dx = 1;
        }

        this.info.new_x = x + dx;
        this.info.new_y = y + dy;
        return true;
    }

    public Soldier(Team team, int x, int y, BufferedImage image)
    {
        super(initName(team), team, new Vector2(x,y), image);
    }

    private static String initName(Team team)
    {
        if (team.equals(Team.red))
        {
            return  "兵";
        }
        else
        {
            return  "卒";
        }
    }

}


class Commander extends Chess
{
    @Override
    protected boolean isLegal(String dir, String where, Chess[][] map)
    {
        int cow = 0;
        int dis = 0;
        int dx = 0;
        int dy = 0;
        int x = this.pos.x;
        int y = this.pos.y;
        int idx = 0;
        boolean out = false;

        if (dir.equals("平"))
        {
            cow = getCowByNum(where);
            if(cow < 0)
            {
                return false;
            }
            dy = cow - y;
            /*将帅只能移动一格*/
            if (dy != -1 && dy !=1)
            {
                return false;
            }
            /*有没有离开皇宫*/
            if(cow < 3 || cow>5)
            {
                out = true;
            }
        }
        /*"进"的情况*/
        else if(dir.equals("进"))
        {
            dis = getDisByNum(where);
            if(this.team.equals(Team.red))
            {
                /*只能进一格，到底了不能进*/
                if(dis != 1)
                {
                    /*对脸笑*/
                    int row = x - dis;
                    if(row < 0 || map[row][y] == null || !map[row][y].name.equals("将"))
                    {
                        return false;
                    }
                    else
                    {
                        /*目标棋子是对面的将，进行路径检测*/
                        for(idx = x-1;idx > row; idx--)
                        {
                            if(map[idx][y] != null)
                            {
                                return false;
                            }
                        }

                        this.info.new_x = row;
                        this.info.new_y = y;
                        return true;
                    }
                }
                /*到底了不能进*/
                if (x < 1)
                {
                    return false;
                }
                /*标记出界*/
                else if (x < 8)
                {
                    out = true;
                }
                dis = -1;
            }
            else
            {
                /*只能进一格，到底了不能进*/
                if(dis != 1)
                {
                    /*对脸笑*/
                    int row = x + dis;
                    if(row > 9 || map[row][y] == null || !map[row][y].name.equals("帅"))
                    {
                        return false;
                    }
                    else
                    {
                        /*目标棋子是对面的帅，进行路径检测*/
                        for(idx = x+1;idx < row; idx++)
                        {
                            if(map[idx][y] != null)
                            {
                                return false;
                            }
                        }

                        this.info.new_x = row;
                        this.info.new_y = y;
                        return true;
                    }
                }
                if (x > 8)
                {
                    return false;
                }
                else if (x > 1)
                {
                    out = true;
                }
                dis = 1;
            }
            dx = dis;
        }
        /*退*/
        else
        {
            dis = getDisByNum(where);
            /*只能进一格，到底了不能进*/
            if (dis != 1)
            {
                return false;
            }
            if(this.team.equals(Team.red))
            {
                if (x > 8)
                {
                    return false;
                }
                else if (x < 6)
                {
                    out = true;
                }
                dis = 1;
            }
            else
            {
                if (x < 1)
                {
                    return false;
                }
                else if (x > 3)
                {
                    out = true;
                }
                dis = -1;
            }
            dx = dis;
        }

        this.info.new_x = x + dx;
        this.info.new_y = y + dy;
        this.info.out_local = out;
        return true;
    }

    private static String initName(Team team)
    {
        if (team.equals(Team.red))
        {
            return "帅";
        }
        else
        {
            return "将";
        }
    }

    public Commander(Team team, Vector2 init_pos, BufferedImage image)
    {
        super(initName(team), team, init_pos, image);
    }
}