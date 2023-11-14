package ChineseChess.Core;
/**
 * ChessExcept
 */
class ChessExcept extends Exception 
{
    protected String msg;

    public ChessExcept(String msg)
    {
        this.msg = msg;
    }

    @Override
    public final String toString() {
        return this.msg;
    }

    @Override
    public final void printStackTrace()
    {
        System.out.println(this.msg);
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
 

class RetractExcept extends ChessExcept
{
    public RetractExcept()
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