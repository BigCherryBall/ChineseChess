import java.awt.image.BufferedImage;

public abstract class Chess {
    public String name;
    public Team team;
    public Vector2 pos;
    public Vector2 init_pos;
    public BufferedImage img;
    protected MoveInfo info;

    public MoveInfo move(String dir, String where, Chess[][] map) throws MoveExcept, CommandExcept
    {
        boolean legal = this.isLegal(dir, where, map);
        Chess target = null;

        if(!legal)
        {
            throw new CommandExcept();
        }

        target = map[info.new_x][info.new_y];
        if (target != null && target.team.equals(this.team))
        {
            throw new MoveExcept();
        }

        this.info.target = target;
        this.info.x = this.pos.x;
        this.info.y = this.pos.y;
        return this.info;
    }

    protected abstract boolean isLegal(String dir, String where, Chess[][] map);

    /*
     * 每次移动记得更新位置
     */
    public void updatePos(int new_x, int new_y)
    {
        this.pos.x = new_x;
        this.pos.y = new_y;
    }

    /*
     * 回到初始位置
     */
    public void backToInitPos()
    {
        this.pos.x = this.init_pos.x;
        this.pos.y = this.init_pos.y;
    }

    public int getCowByNum(String num) {
        switch (num) {
            case "1", "一" -> {
                if (this.team == Team.red) {
                    return 8;
                }
                else {
                    return 0;
                }
            }
            case "2", "二" -> {
                if (this.team == Team.red) {
                    return 7;
                }
                else {
                    return 1;
                }
            }
            case "3", "三" -> {
                if (this.team == Team.red) {
                    return 6;
                }
                else {
                    return 2;
                }
            }
            case "4", "四" -> {
                if (this.team == Team.red) {
                    return 5;
                }
                else {
                    return 3;
                }
            }
            case "5", "五" -> {
                return 4;
            }
            case "6", "六" -> {
                if (this.team == Team.red) {
                    return 3;
                }
                else {
                    return 5;
                }
            }
            case "7", "七" -> {
                if (this.team == Team.red) {
                    return 2;
                }
                else {
                    return 6;
                }
            }
            case "8", "八" -> {
                if (this.team == Team.red) {
                    return 1;
                }
                else {
                    return 7;
                }
            }
            case "9", "九" -> {
                if (this.team == Team.red) {
                    return 0;
                }
                else {
                    return 8;
                }
            }
        }

        return -1;
    }

    @Override
    public String toString() {
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


    public static int getDisByNum(String num) {
        return switch (num) {
            case "1", "一" -> 1;
            case "2", "二" -> 2;
            case "3", "三" -> 3;
            case "4", "四" -> 4;
            case "5", "五" -> 5;
            case "6", "六" -> 6;
            case "7", "七" -> 7;
            case "8", "八" -> 8;
            case "9", "九" -> 9;
            default ->       -1;
        };
    }

}