import java.io.File;

class Horse extends Chess {


    @Override
    public MoveInfo move(String command, Chess[][] map) {
        throw new UnsupportedOperationException("Unimplemented method 'move'");
    }

    @Override
    public MoveInfo isLegal(String command) {
        throw new UnsupportedOperationException("Unimplemented method 'isLegal'");
    }

    public Horse(Team team, int x, int y, String img)
    {
        this.team = team;
        this.init_pos.x = x;
        this.init_pos.y = y;
        new File(".");
    }
    
}
