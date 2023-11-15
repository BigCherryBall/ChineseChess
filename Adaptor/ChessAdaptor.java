package ChineseChess.Adaptor;


import ChineseChess.Core.ChessControl;
import ChineseChess.Core.State;
import ChineseChess.Core.Team;

import java.util.*;

interface CmdDeal
{
    boolean deal(RequestInfo info);
}


final class Player
{

    public Player(String id, String name, Team team, int idx)
    {
        this.id = id;
        this.name = name;
        this.team = team;
        if(idx < 1)
        {
            this.idx = 0;
        }
    }

    public Player(Team team, int idx)
    {
        this.id = "";
        this.team = team;
        this.name = "";
        if(idx < 1)
        {
            this.idx = 0;
        }
    }
    public String id;
    public String name;
    public final Team team;

    public int idx;

    public float use_time;

    @Override
    public String toString()
    {
        return this.name;
    }

    public String getMsgDetail()
    {
        if (this.idx < 1)
        {
            return this.team.toString() + ":" + this.name;
        }
        else
        {
            return this.team.toString() + this.idx + ":" + this.name;
        }
    }

    public String getMsg()
    {
        if (this.idx < 1)
        {
            return this.team.toString();
        }
        else
        {
            return this.team.toString() + this.idx;
        }
    }
}


class EveryChess
{
    public EveryChess(int player_count, String seed)
    {
        int idx = 0;
        this.control = new ChessControl(seed);
        this.player_count = player_count;

        if(player_count % 2 == 1)
        {
            player_count += 1;
        }
        this.players = new Player[player_count];

        if(player_count == 2)
        {
            this.players[0] = new Player(Team.red, 0);
            this.players[1] = new Player(Team.black, 0);
        }
        for(idx = 0; idx < player_count; idx ++)
        {
            if (idx % 2 == 0)
            {
                this.players[idx] = new Player(Team.red, idx / 2 + 1);
            }
            else
            {
                this.players[idx] = new Player(Team.black, idx / 2 + 1);
            }
        }

        this.random = new Random();
    }

    public Player[] players;

    public ChessControl control;

    public int player_count;

    public int has_add_count;

    private Random random;

    public String getPlayerMsg()
    {
        int idx = 0;
        StringBuilder result = new StringBuilder();

        for (idx = 0; idx < this.player_count; idx++)
        {
            result.append(this.players[idx].getMsgDetail()).append("\n");
        }

        return result.toString();
    }

    public void add(Team team, String id, String name, int location)
    {
        this.has_add_count++;
    }

    public Player add(String id, String name)
    {
        ArrayList<Integer> empty = new ArrayList<>();
        Player select = null;

        for(int idx = 0;idx<this.player_count;idx++)
        {
            String player_id = this.players[idx].id;
            if (player_id.equals(id))
            {
                return null;
            }
            else if(this.players[idx].id.equals(""))
            {
                empty.add(idx);
            }
        }
        
        select = this.players[empty.get(this.random.nextInt(0, empty.size()))];
        select.id = id;
        if(name != null)
        {
            select.name = name;
        }
        return select;
    }

}


public final class ChessAdaptor
{
    public ChessAdaptor()
    {
        dic = new HashMap<>();
        response = new ResponseInfo();
        deals = new CmdDeal[]
                {
                        this::newChess,
                        this::add,
                        this::exit,
                        this::giveUp,
                        this::retract,
                        this::changePlayer,
                        this::changeMapStyle,
                        this::negotiatePeace,
                        this::agree,
                        this::getMapStyle,
                        this::blindChess,
                        this::getMap
                };
    }
    private Map<String, EveryChess> dic;

    private ResponseInfo response;

    private CmdDeal[] deals;

    /**
    * @description:
     *@param msg 输入结构体
    * @return: ChineseChess.Adaptor.ResponseInfo
    * @author: BigCherryBall
    * @time: 2023/11/14 20:16
    */
    public ResponseInfo cmd(RequestInfo msg)
    {
        if (msg == null || msg.cmd == null || msg.id == null || msg.group == null)
        {
            this.buildResponse(ResponseType.error, "错误，参数错误");
            return this.response;
        }

        /*开始逐条匹配命令*/
        for(CmdDeal fun : this.deals)
        {
            if (fun.deal(msg))
            {
                return this.response;
            }
        }

        /*---------执行到这里就证明命令没有匹配到--------*/
        this.buildResponse(ResponseType.none, "");
        return this.response;
    }


    /*----------------实现功能的函数区----------------------*/

    private boolean newChess(RequestInfo msg)
    {
        if(!(msg.cmd.equals("象棋") || msg.cmd.equals("中国象棋")))
        {
            return false;
        }
        this.answerChangeChess(msg, 2);
        return true;
    }

    private boolean newChessSpecial(RequestInfo msg)
    {
        if(!(msg.cmd.equals("联棋") || msg.cmd.equals("四人象棋")))
        {
            return false;
        }
        this.answerChangeChess(msg, 4);
        return true;
    }
    private boolean add(RequestInfo msg)
    {
        ChessControl control = null;
        EveryChess everyChess = null;
        Player new_player = null;
        int sender_idx = -1;

        if(!(msg.cmd.equals("加入") || msg.cmd.equals("加入棋局")))
        {
            return false;
        }

        control = this.getControl(msg);
        if(control == null || control.state == State.init)
        {
            this.buildResponse(ResponseType.info, "棋局还没有初始化，发送{中国象棋}初始化棋局后才能加入");
            return true;
        }

        everyChess = this.dic.get(msg.group);
        if(control.state == State.began)
        {
            this.buildResponse(ResponseType.info, "棋局已经开始，快来观战吧：\n" + everyChess.getPlayerMsg());
            return true;
        }
        else
        {
            sender_idx = this.getSenderIdx(msg);
            if(sender_idx >= 0)
            {
                this.buildResponse(ResponseType.info, everyChess.players[sender_idx].name + "，你已经加入了棋局，耐心等待其他棋手吧！");
                return true;
            }

            new_player = everyChess.add(msg.id, msg.name);
            if(msg.name == null)
            {
                this.buildResponse(ResponseType.info, msg.id + "，加入成功，你执" + new_player.team);
            }
            else
            {
                this.buildResponse(ResponseType.info, msg.name + "，你已经加入了棋局" + new_player.team);
            }
            
            return true;
        }
    }

    private boolean exit(RequestInfo msg)
    {
        return true;
    }

    private boolean giveUp(RequestInfo msg)
    {
        return true;
    }

    private boolean retract(RequestInfo msg)
    {
        return true;
    }

    private boolean changePlayer(RequestInfo msg)
    {
        return true;
    }


    private boolean negotiatePeace(RequestInfo msg)
    {
        return true;
    }

    private boolean agree(RequestInfo msg)
    {
        return true;
    }

    private boolean getMapStyle(RequestInfo msg)
    {
        return true;
    }

    private boolean changeMapStyle(RequestInfo msg)
    {
        return true;
    }

    private boolean blindChess(RequestInfo msg)
    {
        return true;
    }
    private boolean getMap(RequestInfo msg)
    {
        return true;
    }

    /*----------------功能函数区结束----------------------*/

    private static String getCmdByLen(String cmd, int len)
    {
        if (cmd == null || cmd.length() < len)
        {
            return null;
        }

        return cmd.substring(0, len);

    }

    private void buildResponse(ResponseType type, String msg)
    {
        this.response.msg = msg;
        this.response.type = type;
    }

    private void answerChangeChess(RequestInfo msg, int player_count)
    {
        if (this.dic.containsKey(msg.group))
        {
            EveryChess every_chess = this.dic.get(msg.group);
            State state = every_chess.control.state;

            if(player_count != every_chess.player_count)
            {
                if(state == State.init || (state == State.prepare && every_chess.has_add_count == 0))
                {
                    //这里填充everychess换人的代码
                    this.buildResponse(ResponseType.info, "棋局初始化成功，发送{加入棋局}即可加入游戏，发送{加入棋局 红1}即可加入棋局执红1");
                    return;
                }
                else if (state ==State.prepare)
                {
                    this.buildResponse(ResponseType.info, "本群组已经初始化了其他棋局，不能再初始化联棋");
                    return;
                }
                else if(state == State.began)
                {
                    this.buildResponse(ResponseType.info, "棋局已经开始，快来观战吧：\n" + dic.get(msg.group).getPlayerMsg());
                    return;
                }
            }
            else
            {
                if(state == State.init)
                {
                    every_chess.control.state = State.prepare;
                    if(player_count > 2)
                    {
                        this.buildResponse(ResponseType.info, "棋局初始化成功，发送{加入棋局}即可加入游戏，发送{加入棋局 红1}即可加入棋局执红1");
                    }
                    else
                    {
                        this.buildResponse(ResponseType.info, "棋局初始化成功，发送{加入棋局}即可加入游戏，发送{加入棋局 红}即可加入棋局执红");
                    }

                    return;
                }
                else if (state == State.prepare)
                {
                    if (player_count > 2)
                    {
                        this.buildResponse(ResponseType.info, "棋局已经初始化，发送{加入棋局}即可加入游戏，发送{加入棋局 红}即可加入棋局执红");
                    }
                    else
                    {
                        this.buildResponse(ResponseType.info, "棋局已经初始化，发送{加入棋局}即可加入游戏，发送{加入棋局 红1}即可加入棋局执红1");
                    }
                    return;
                }
                else if(state == State.began)
                {
                    this.buildResponse(ResponseType.info, "棋局已经开始，快来观战吧：\n" + dic.get(msg.group).getPlayerMsg());
                    return;
                }
            }
        }

        EveryChess every_chess = new EveryChess(player_count, msg.group);
        every_chess.control.state = State.prepare;
        this.dic.put(msg.group, every_chess);

        this.buildResponse(ResponseType.info, "棋局初始化成功，发送{加入棋局}即可加入游戏，发送{加入棋局 红}即可加入棋局执红");
    }

    /**
    * @description: 必须保证已经有EveryChess对象
     *@param msg
    * @return: int
    * @author: BigCherryBall
    * @time: 2023/11/15 21:36
    */
    private int getSenderIdx(RequestInfo msg)
    {
        int idx = 0;
        Player[] players= this.dic.get(msg.group).players;
        int count = players.length;
        for(idx = 0; idx < count; idx ++)
        {
            if (players[idx].id.equals(msg.id))
            {
                return idx;
            }
        }
        return -1;
    }

    private ChessControl getControl(RequestInfo msg)
    {
        if(this.dic.containsKey(msg.group))
        {
            return this.dic.get(msg.group).control;
        }
        else
        {
            return null;
        }
    }

    private EveryChess getEveryChess(RequestInfo msg)
    {
        return this.dic.get(msg.group);
    }





}
