package ChineseChess.Core;

public enum Team
{
    red("红"),
    black("黑");

    private String name;

    private Team(String name)
    {
        this.name = name;
    }

    public String toString()
    {
        return this.name;
    }

    public Turn toTurn()
    {
        if(this.name.equals("红"))
        {
            return Turn.red;
        }
        else
        {
            return Turn.black;
        }
    }
}
