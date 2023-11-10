package ChineseChess;
/**
 * ChessExcept
 */
class ChessExcept extends Exception 
{
    String msg;

    public ChessExcept(String msg)
    {
        this.msg = msg;
    }

    @Override
    public String toString() {
        return this.msg;
    }
}

class CommandExcept extends ChessExcept
{
    public CommandExcept()
    {
        super("命令不合法");
    }
}
    
class MoveExcept extends ChessExcept
{
    public MoveExcept()
    {
        super("移动不合法");
    }
}



class ChessNotFindExcept extends ChessExcept
{
    public ChessNotFindExcept()
    {
        super("目标棋子没有找到");
    }
}
 

class BackExcept extends ChessExcept
{
    public BackExcept()
    {
        super("当前步不是最新步，无法悔棋");
    }
}

class ImageNotFindExcept extends ChessExcept
{
    public ImageNotFindExcept(String notice)
    {
        super("[error : ImageNotFind] 图片没有找到:" + notice);
    }
}