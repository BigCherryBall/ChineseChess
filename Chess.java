package ChineseChess;

import java.awt.image.BufferedImage;

public abstract class Chess
{
    public String name;
    public Team team;
    public Vector2 pos;
    public Vector2 init_pos;
    public BufferedImage img;
    protected MoveInfo info;
    public boolean normal_chess = true;

    public final MoveInfo move(String dir, String where, Chess[][] map) throws MoveExcept, CommandExcept
    {
        boolean is_legal = false;
        Chess target = null;

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
        return this.info;
    }

    protected abstract boolean isLegal(String dir, String where, Chess[][] map);

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

    public Chess(String name, Team team, Vector2 init_pos, BufferedImage image)
    {
        this.name = name;
        this.team = team;
        this.init_pos = init_pos;
        this.pos = new Vector2(init_pos.x, init_pos.y);
        this.img = image;
        this.info = new MoveInfo();
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

    public static int getCowByNum(Team team, String num)
    {
        switch (num)
        {
            case "1", "一" ->
            {
                if (team == Team.red)
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
                if (team == Team.red)
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
                if (team == Team.red)
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
                if (team == Team.red)
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
                if (team == Team.red)
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
                if (team == Team.red)
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
                if (team == Team.red)
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
                if (team == Team.red)
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