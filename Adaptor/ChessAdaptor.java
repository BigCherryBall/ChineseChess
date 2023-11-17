package ChineseChess.Adaptor;


import ChineseChess.Core.ChessControl;
import ChineseChess.Core.State;
import ChineseChess.Core.Team;

import java.io.File;
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
        this.number = Math.max(0, idx);
    }

    public Player(Team team, int idx)
    {
        this.id = null;
        this.team = team;
        this.name = null;
        this.number = Math.max(0, idx);
    }
    public String id;
    public String name;
    public final Team team;

    public int number;

    public float use_time;

    @Override
    public String toString()
    {
        return this.name;
    }

    public String getMsgDetail()
    {
        if (this.number < 1)
        {
            return this.team.toString() + ":" + this.name;
        }
        else
        {
            return this.team.toString() + this.number + ":" + this.name;
        }
    }

    public String getMsg()
    {
        if (this.number < 1)
        {
            return this.team.toString();
        }
        else
        {
            return this.team.toString() + this.number;
        }
    }

    public void clear()
    {
        this.id = null;
        this.name = null;
        this.use_time = 0;
    }
}


class EveryChess
{
    public EveryChess(int player_count, String seed)
    {
        this.control = new ChessControl(seed);
        this.initPlayers(player_count);
        this.random = new Random();
    }

    public Player[] players;

    public ChessControl control;

    public int player_count;

    public int has_add_count;

    public Random random;

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

    public void initPlayers(int count)
    {
        int idx = 0;
        this.player_count = count;
        this.has_add_count = 0;
        this.players = new Player[count];

        if(this.player_count == 2)
        {
            this.players[0] = new Player(Team.red, 0);
            this.players[1] = new Player(Team.black, 0);
            return;
        }
        for(idx = 0; idx < this.player_count; idx ++)
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
        this.current = null;
    }
    private Map<String, EveryChess> dic;

    private ResponseInfo response;

    private CmdDeal[] deals;

    private EveryChess current;

    /**
    * @description:
     *@param msg 输入结构体
    * @return: ChineseChess.Adaptor.ResponseInfo
    * @author: BigCherryBall
    * @time: 2023/11/14 20:16
    */
    public ResponseInfo cmd(RequestInfo msg)
    {
        if (msg == null || msg.cmd == null || msg.id == null || msg.group == null || msg.name == null)
        {
            this.buildResponse(ResponseType.error_text, "错误，参数错误", null);
            return this.response;
        }

        /*新建棋局单独判断*/
        if(this.newChess(msg) || this.newChessSpecial(msg))
        {
            return this.response;
        }

        /*可以通过this.current来判断群组有没有初始化棋局*/
        this.current = null;
        if(this.dic.containsKey(msg.group))
        {
            this.current = this.dic.get(msg.group);
            
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
        this.buildResponse(ResponseType.none, null, null);
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

        boolean add_succ = false;
        int sender_idx = -1;

        if(!(msg.cmd.equals("加入") || msg.cmd.equals("加入棋局")))
        {
            return false;
        }

        if(this.current == null || this.current.control.state == State.init)
        {
            this.buildResponse(ResponseType.info_text, "棋局还没有初始化，发送{中国象棋}或者{联棋}初始化棋局后才能加入", null);
            return true;
        }

        if(this.current.control.state == State.began)
        {
            this.buildResponse(ResponseType.info_text, "棋局已经开始，快来观战吧：\n" + this.current.getPlayerMsg(), null);
            return true;
        }
        else
        {
            sender_idx = this.getSenderIdx(msg);
            if(sender_idx >= 0)
            {
                this.buildResponse(ResponseType.info_text, this.current.players[sender_idx].name + "，你已经加入了棋局，耐心等待其他棋手吧！", null);
                return true;
            }
        ///需要改
            add_succ = this.addPlayer(msg);
            if(msg.name == null)
            {
                this.buildResponse(ResponseType.info_text, msg.id + "，加入成功，你执" , null);
            }
            else
            {
                this.buildResponse(ResponseType.info_text, msg.name + "，你已经加入了棋局" , null);
            }
            ///
            
            return true;
        }
    }

    private boolean exit(RequestInfo msg)
    {
        Player sender = null;
        int sender_idx = -1;

        if(!(msg.cmd.equals("退出棋局")))
        {
            return false;
        }

        if(this.current == null)
        {
            this.buildResponse(ResponseType.none, null, null);
            return true;
        }

        sender_idx =  this.getSenderIdx(msg);
        if(sender_idx < 0)
        {
            this.buildResponse(ResponseType.none, null, null);
            return true;
        }

        sender = this.current.players[sender_idx];
        this.buildResponse(ResponseType.info_text, sender.name  + "，退出成功", null);
        sender.clear();
        this.current.has_add_count--;
        
        return true;
    }

    private boolean giveUp(RequestInfo msg)
    {
        int sender_idx = -1;
        Player sender = null;
        StringBuffer result = new StringBuffer();

        if(!(msg.cmd.equals("认输") || msg.cmd.equals("投降")))
        {
            return false;
        }

        if(this.current == null || this.current.control.state != State.began)
        {
            this.buildResponse(ResponseType.none, null, null);
            return true;
        }

        sender_idx = this.getSenderIdx(msg);
        if(sender_idx < 0)
        {
            this.buildResponse(ResponseType.none, null, null);
            return true;
        }

        sender = this.current.players[sender_idx];

        if(this.current.player_count == 2)
        {
            result.append(sender.team.toString()).append("方(").append(sender.name).append(")认输，恭喜")
                    .append(this.current.players[1 - sender_idx].name).append("获得胜利");
            this.buildResponse(ResponseType.none, result.toString(), null);
            return true;
        }
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

    private void buildResponse(ResponseType type, String msg, File image)
    {
        this.response.msg = msg;
        this.response.type = type;
        this.response.image = image;
    }

    private void answerChangeChess(RequestInfo msg, int player_count)
    {
        if (this.current == null)
        {
            EveryChess every_chess = new EveryChess(player_count, msg.group);
            every_chess.control.state = State.prepare;
            this.dic.put(msg.group, every_chess);

            this.buildResponse(ResponseType.info_text, "棋局初始化成功，发送{加入棋局}即可加入游戏，发送{加入棋局 红}即可加入棋局执红", null);
            return;
        }

        State state = this.current.control.state;

        if(player_count != this.current.player_count)
        {
            if(state == State.init || (state == State.prepare && this.current.has_add_count == 0))
            {
                this.current.initPlayers(player_count);
                this.buildResponse(ResponseType.info_text, "棋局初始化成功，发送{加入棋局}即可加入游戏，发送{加入棋局 红1}即可加入棋局执红1", null);
                return;
            }
            else if (state ==State.prepare)
            {
                this.buildResponse(ResponseType.info_text, "本群组已经初始化了其他棋局，不能再进行初始化操作", null);
                return;
            }
            else if(state == State.began)
            {
                this.buildResponse(ResponseType.info_text, "棋局已经开始，快来观战吧：\n" + dic.get(msg.group).getPlayerMsg(), null);
                return;
            }
        }
        else
        {
            if(state == State.init)
            {
                this.current.control.state = State.prepare;
                if(player_count > 2)
                {
                    this.buildResponse(ResponseType.info_text, "棋局初始化成功，发送{加入棋局}即可加入游戏，发送{加入棋局 红1}即可加入棋局执红1", null);
                }
                else
                {
                    this.buildResponse(ResponseType.info_text, "棋局初始化成功，发送{加入棋局}即可加入游戏，发送{加入棋局 红}即可加入棋局执红", null);
                }

                return;
            }
            else if (state == State.prepare)
            {
                if (player_count > 2)
                {
                    this.buildResponse(ResponseType.info_text, "棋局已经初始化，发送{加入棋局}即可加入游戏，发送{加入棋局 红}即可加入棋局执红", null);
                }
                else
                {
                    this.buildResponse(ResponseType.info_text, "棋局已经初始化，发送{加入棋局}即可加入游戏，发送{加入棋局 红1}即可加入棋局执红1", null);
                }
                return;
            }
            else if(state == State.began)
            {
                this.buildResponse(ResponseType.info_text, "棋局已经开始，快来观战吧：\n" + dic.get(msg.group).getPlayerMsg(), null);
                return;
            }
        }
        
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

    private boolean addPlayer(RequestInfo msg)
    {
        ArrayList<Integer> empty = new ArrayList<>();
        Player select = null;

        for(int idx = 0;idx<this.current.player_count;idx++)
        {
            String player_id = this.current.players[idx].id;
            if (player_id.equals(msg.id))
            {
                return false;
            }
            else if(this.current.players[idx].id == null)
            {
                empty.add(idx);
            }
        }
        
        select = this.current.players[empty.get(this.current.random.nextInt(0, empty.size()))];
        select.id = msg.id;
        select.name = msg.name;

        return true;
    }

}
