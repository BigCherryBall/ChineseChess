import java.awt.image.BufferedImage;
import java.io.File;


/**
 * Horse马
 */
class Horse extends Chess {
    @Override
    protected boolean isLegal(String dir, String where, Chess[][] map)
    {
        int cow = getCowByNum(where);
        int dy = cow - this.pos.y;
        int dx = 0;

        //马不可以平,页不可以越界
        if(dir.equals("平") || cow < 0)
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
            if(dy == 2)
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
            else if(dy == -2)
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
            else if(dy == 1 || dy == -1)
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
            if(dy == 2)
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
            else if(dy == -2)
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
            else if(dy == 1 || dy == -1)
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


    public Horse(String name, Team team, int x, int y, BufferedImage img)
    {
        super(name, team, new Vector2(x, y), img);
    }

}

/**
 * Car车
 */
class Car extends Chess
{
    public Car(String name, Team team, int x, int y, BufferedImage image)
    {
        super(name, team, new Vector2(x, y), image);
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
                for(idx = this.pos.y + 1; idx < cow; idx++)
                {
                    if(map[this.pos.x][idx] != null)
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
                for(idx = this.pos.y - 1; idx > cow; idx--)
                {
                    if(map[this.pos.x][idx] != null)
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
            for(idx = this.pos.x - 1; idx > this.pos.x - dx; idx--)
            {
                if(map[idx][this.pos.y] != null)
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
            for(idx = this.pos.x + 1; idx < this.pos.x + dx; idx++)
            {
                if(map[idx][this.pos.y] != null)
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
 *  Elephant象
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
        if(dir.equals("平") || cow < 0)
        {
            return false;
        }
        //进
        if (dir.equals("进") && this.team.equals(Team.red))
        {
            if (this.pos.x < 6)
            {
                return false;
            }

            dx = -2;
            if(dy == 2)
            {
                //撇脚
                if (map[this.pos.x - 1][this.pos.y + 1] != null)
                {
                    return false;
                }

            }
            else if(dy == -2)
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
            if (this.pos.x > 8)
            {
                return false;
            }

            dx = 2;
            if(dy == 2)
            {
                //撇脚
                if (map[this.pos.x + 1][this.pos.y + 1] != null)
                {
                    return false;
                }

            }
            else if(dy == -2)
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
            if (this.pos.x > 3)
            {
                return false;
            }

            dx = 2;
            if(dy == 2)
            {
                //撇脚
                if (map[this.pos.x + 1][this.pos.y + 1] != null)
                {
                    return false;
                }

            }
            else if(dy == -2)
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
            if (this.pos.x < 1)
            {
                return false;
            }

            dx = -2;
            if(dy == 2)
            {
                //撇脚
                if (map[this.pos.x - 1][this.pos.y + 1] != null)
                {
                    return false;
                }

            }
            else if(dy == -2)
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
        return true;
    }

    public Elephant(String name, Team team, int x, int y, BufferedImage image)
    {
        super(name, team, new Vector2(x, y), image);
    }
}
