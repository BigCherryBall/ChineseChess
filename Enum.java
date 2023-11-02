
enum Team
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
}

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