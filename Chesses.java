import java.awt.image.BufferedImage;
import java.io.File;

class Horse extends Chess {
    @Override
    public MoveInfo move(String command, Chess[][] map) {
        MoveInfo info = this.isLegal(command, map);
        return info;

    }

    @Override
    public MoveInfo isLegal(String command, Chess[][] map)
    {
        MoveInfo info = new MoveInfo();
        String dir = String.valueOf(command.charAt(0));
        String where = String.valueOf(command.charAt(1));
        int cow = getCowByNum(where);
        int dy = cow - this.pos.y;

        //马不可以平
        if(dir.equals("平"))
        {
            return null;
        }
        //进
        else if (dir.equals("进"))
        {
            //横着走
            if(dy == 2)
            {
                if(this.pos.y > 6)
                {
                    return null;
                }
            }
        }

        return info;
    }



    public Horse(String name, Team team, int x, int y, BufferedImage img) {
        super(name, team, new Vector2(x, y), img);
    }

}
