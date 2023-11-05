package ChineseChess;
class Vector2 
{
    public int x = 0;
    public int y = 0;

    @Override
    public String toString()
    {
        return "("+ x + "," + y + ")";
    }

    public Vector2(int x, int y)
    {
        this.x = x;
        this.y = y;
    }

    public Vector2()
    {
        
    }
}

/**
 * 记录移动前后的点，和移动后位置原有的棋子
 */
class MoveInfo 
{
    int x;
    int y;
    int new_x;
    int new_y;
    Chess target;
    /*是否移出了被限制的区域，比如士象帅*/
    boolean out_local;
}
