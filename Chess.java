import java.awt.image.BufferedImage;

public abstract class Chess {
    public String name;
    public Team team;
    public Vector2 pos;
    public Vector2 init_pos;
    public BufferedImage image;

    public abstract MoveInfo move(String command, Chess[][] map);

    public abstract MoveInfo isLegal(String command);

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
        this.image = image;
    }

}