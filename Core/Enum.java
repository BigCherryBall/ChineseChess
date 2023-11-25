package ChineseChess.Core;



enum Turn
{
    red("红"),
    black("黑");

    private String name;
    
    private Turn(String name)
    {
        this.name = name;
    }

    public String toString()
    {
        return this.name;
    }
}


enum ChessStyle
{
    defalt("默认");



    private String name;
    ChessStyle(String name)
    {
        this.name = name;
    }

    @Override
    public String toString()
    {
        return this.name;
    }
}

enum Log
{
    error(1, "[error]"),
    warn(2, "[warn]"),
    info(3, "[info]"),
    debug(4, "[debug]");

    private final int rank;
    private final String msg;

    Log(int rank, String msg)
    {
        this.rank = rank;
        this.msg = msg;
    }

    public int getRank()
    {
        return this.rank;
    }

    public String getMsg()
    {
        return this.msg;
    }
}