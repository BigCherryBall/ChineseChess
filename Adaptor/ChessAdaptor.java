package ChineseChess.Adaptor;


import ChineseChess.Core.*;

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
        this.number = Math.max(1, idx);
    }

    public Player(Team team, int number)
    {
        this.id = null;
        this.team = team;
        this.name = null;
        this.number = number;
    }
    public String id;
    public String name;
    public final Team team;

    public final int number;

    public long use_time;

    @Override
    public String toString()
    {
        return this.team.toString() + this.number;
    }

    public String getMsgWithNum()
    {
        return this.team.toString() + this.number + ":" + this.name;
    }

    public String getMsg()
    {
        return this.team.toString() + ":" + this.name;
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
        control = new ChessControl(seed);
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
            result.append(this.players[idx].getMsgWithNum()).append("\n");
        }

        return result.toString();
    }

    public void initPlayers(int count)
    {
        int idx = 0;
        this.player_count = count;
        this.has_add_count = 0;
        this.players = new Player[count];

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

    /**
    * @description: 加入成功则返回null，加入失败则返回失败原因
     *@param id id
     *@param name name
     *@param team team
     *@param number number
    * @return: java.lang.String
    * @author: BigCherryBall
    * @time: 2023/11/18 10:20
    */
    public String addByNum(String id, String name, Team team, int number)
    {
        if(number < 1 || number > this.player_count / 2)
        {
            return "加入失败，选择的位置编号不正确";
        }

        if(team == null)
        {
            return "加入失败：内部参数错误3，请帮忙踢作者一脚: ChessAdaptor.EveryChess.add()";
        }

        int select;
        if(Team.red == team)
        {
            select = number * 2 - 2;
        }
        else
        {
            select = number * 2 - 1;
        }

        if(this.players[select].id != null)
        {
            return "加入失败，该位置已经有人了";
        }

        this.addByIdx(id, name, select);
        return null;
    }

    public void addByIdx(String id, String name, int idx)
    {
        Player select = this.players[idx];
        select.id = id;
        select.name = name;
        this.has_add_count++;
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
                        this::newChessSpecial,
                        this::add,
                        this::exit,
                        this::moveChess,
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
    private final Map<String, EveryChess> dic;

    private final ResponseInfo response;

    private final CmdDeal[] deals;



    /**
    * @description: 唯一接口
     *@param msg 输入结构体
    * @return: ChineseChess.Adaptor.ResponseInfo
    * @author: BigCherryBall
    * @time: 2023/11/14 20:16
    */
    public ResponseInfo cmd(RequestInfo msg)
    {
        if (msg == null || msg.cmd == null || msg.id == null || msg.group == null || msg.name == null)
        {
            this.buildResponse(ResponseType.none, null, null);
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
        EveryChess current = this.dic.get(msg.group);
        boolean add_succ = false;
        String sub = null;
        String result = null;
        int idx = -1;
        String notice = null;

        if(msg.cmd.equals("加入") || msg.cmd.equals("加入棋局"))
        {
            if(this.addIllegal(msg, current))
            {
                return true;
            }
            else
            {
                //加入棋局
                idx = this.getRandomEmptyIdx(null, current);
                current.addByIdx(msg.id, msg.name, idx);
            }


            if(current.player_count == 2)
            {
                notice = msg.name + "，加入成功，你执" + current.players[idx].team.toTurn();
            }
            else
            {
                notice = msg.name + "，加入成功，你执" + current.players[idx].toString();
            }
            if(this.fullAndBegin(current))
            {
                this.buildResponse(ResponseType.info_and_image, notice, current.control.out_img);
            }
            else
            {
                this.buildResponse(ResponseType.info_text, notice, null);
            }
            return true;
        }

        if(!msg.cmd.startsWith("加入棋局 "))
        {
            return false;
        }
        if(this.addIllegal(msg, current))
        {
            return true;
        }

        sub = msg.cmd.substring(5);
        if(sub.length() == 1)
        {
            Team team = null;
            if(sub.equals("红"))
            {
                team = Team.red;
            }
            else if (sub.equals("黑"))
            {
                team = Team.black;
            }
            else
            {
                this.buildResponse(ResponseType.info_text, "命令错误，请选择你要加入哪方，只有红与黑的选择", null);
                return true;
            }

            idx = this.getRandomEmptyIdx(team, current);
            if(idx < 0)
            {
                this.buildResponse(ResponseType.info_text, "加入失败，" + team + "方棋手已满", null);
                return true;
            }

            current.addByIdx(msg.id, msg.name, idx);
            if(current.player_count == 2)
            {
                notice = msg.name + "，加入成功，你执" + team;
            }
            else
            {
                notice = msg.name + "，加入成功，你执" + current.players[idx].toString();
            }

            if(this.fullAndBegin(current))
            {
                this.buildResponse(ResponseType.info_and_image, notice, current.control.out_img);
            }
            else
            {
                this.buildResponse(ResponseType.info_text, notice, null);
            }
            return true;
        }
        else if (sub.length() == 2)
        {
            char name = sub.charAt(0);
            char location = sub.charAt(1);
            Team team = null;
            int select = -1;


            if(name == '红')
            {
                team = Team.red;
            }
            else if (name == '黑')
            {
                team = Team.black;
            }
            else
            {
                this.buildResponse(ResponseType.info_text, "请选择你要加入哪方，只有红与黑的选择", null);
                return true;
            }

            if(Character.isDigit(location))
            {
                select = location - '0';
            }
            else
            {
                this.buildResponse(ResponseType.info_text, "位置选择错误，请输入数字可以选择位置，比如{加入棋局 黑1}即可加入执黑方1手", null);
                return true;
            }

            result = current.addByNum(msg.id, msg.name, team, select);
            if(result != null)
            {
                this.buildResponse(ResponseType.info_text, result, null);
                return true;
            }

            if(this.fullAndBegin(current))
            {
                this.buildResponse(ResponseType.info_and_image, msg.name + "，加入成功，你执" + sub, current.control.out_img);
            }
            else
            {
                this.buildResponse(ResponseType.info_text, msg.name + "，加入成功，你执" + sub, null);
            }
            return true;
        }
        else
        {
            this.buildResponse(ResponseType.none, null, null);
            return true;
        }
    }

    private boolean exit(RequestInfo msg)
    {
        EveryChess current = this.dic.get(msg.group);
        Player sender = null;
        int sender_idx = -1;

        if(!(msg.cmd.equals("退出棋局")))
        {
            return false;
        }

        if(current == null || current.control.state == State.init)
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

        sender = current.players[sender_idx];
        if(current.control.state == State.prepare)
        {
            sender.clear();
            this.buildResponse(ResponseType.info_text, sender.name  + "，退出成功", null);
            current.has_add_count--;
        }
        else
        {
            this.buildResponse(ResponseType.info_text, "棋局已经开始，无法退出。请使用认输或者掀棋盘命令退出", null);
        }
        return true;
    }

    private boolean moveChess(RequestInfo msg)
    {
        EveryChess current = this.dic.get(msg.group);
        int sender_idx = -1;
        if(msg.cmd.length() != 4)
        {
            return false;
        }

        char dir = msg.cmd.charAt(2);
        if(dir != '进' && dir != '退' && dir != '平')
        {
            return false;
        }

        if(current == null  || current.control.state != State.began)
        {
            this.buildResponse(ResponseType.none, null, null);
            return true;
        }

        sender_idx = getSenderIdx(msg);
        if(sender_idx < 0)
        {
            this.buildResponse(ResponseType.none, null, null);
            return true;
        }

        if(current.control.step % current.player_count != sender_idx)
        {
            this.buildResponse(ResponseType.none, null, null);
            return true;
        }

        try
        {
            current.control.move(msg.cmd);
        }
        catch (ChessExcept e)
        {
            this.buildResponse(ResponseType.info_text, e.toString(), null);
            return true;
        }

        current.players[sender_idx].use_time += current.control.last_step_use_time;
        if(current.control.blind_chess)
        {
            this.buildResponse(ResponseType.info_text, current.players[sender_idx].name + "，落子成功", null);
        }
        else
        {
            this.buildResponse(ResponseType.image, null, current.control.out_img);
        }
        return true;
    }

    private boolean giveUp(RequestInfo msg)
    {
        EveryChess current = this.dic.get(msg.group);
        int sender_idx = -1;
        int i = 0;
        int temp = 0;
        Player sender = null;
        StringBuilder result = new StringBuilder();

        if(!(msg.cmd.equals("认输") || msg.cmd.equals("投降")))
        {
            return false;
        }

        if(current == null || current.control.state != State.began)
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

        sender = current.players[sender_idx];
        if(sender_idx != current.control.step % current.player_count)
        {
            this.buildResponse(ResponseType.info_text, sender.name + "，当前不该你行棋，你无法认输", null);
            return true;
        }

        if(sender.team == Team.black)
        {
            i = 1;
            temp = 1;
            current.control.over(Team.red);
        }
        else
        {
            current.control.over(Team.black);
        }
        sender.use_time += current.control.last_step_use_time;

        result.append(sender.team.toString()).append("方(");
        for (; temp < current.player_count; temp = temp + 2)
        {
            result.append(current.players[temp].name);
            if(temp < current.player_count - 2)
            {
                result.append("，");
            }
        }

        temp = i;
        result.append(")认输，恭喜");
        for (temp = 1 - temp; temp < current.player_count; temp = temp + 2)
        {
            result.append(current.players[temp].name);
            if(temp < current.player_count - 2)
            {
                result.append("，");
            }
        }

        result.append("获得胜利!!!\n用时详情：\n");
        for(Player player : current.players)
        {
            result.append("->");
            result.append(player.name);
            result.append(":");
            result.append(getTimeStr(player.use_time));
            result.append("\n");
        }

        this.buildResponse(ResponseType.info_text, result.toString(), null);
        return true;
    }

    private boolean retract(RequestInfo msg)
    {
        EveryChess current = this.dic.get(msg.group);
        int sender_idx = -1;

        if(!(msg.cmd.equals("悔棋")))
        {
            return false;
        }

        if(current == null || current.control.state != State.began)
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
        if(current.players[sender_idx].team.toTurn() == current.control.turn)
        {
            this.buildResponse(ResponseType.info_text, "悔棋失败，该你行棋，现在不能悔棋", null);
        }
        else
        {
            try
            {
                current.control.retract();
            }
            catch (ChessExcept e)
            {
                this.buildResponse(ResponseType.info_text, e.toString(), null);
                return true;
            }
            if(current.control.blind_chess)
            {
                this.buildResponse(ResponseType.info_text, current.players[sender_idx].name + "，悔棋成功", null);
            }
            else
            {
                this.buildResponse(ResponseType.image, null, current.control.out_img);
            }
        }
        return true;
    }

    private boolean changePlayer(RequestInfo msg)
    {
        return false;
    }


    private boolean negotiatePeace(RequestInfo msg)
    {
        return false;
    }

    private boolean agree(RequestInfo msg)
    {
        return false;
    }

    private boolean getMapStyle(RequestInfo msg)
    {
        return false;
    }

    private boolean changeMapStyle(RequestInfo msg)
    {
        return false;
    }

    private boolean blindChess(RequestInfo msg)
    {
        EveryChess current = this.dic.get(msg.group);
        int sender_idx = -1;
        if(!msg.cmd.equals("盲棋"))
        {
            return false;
        }

        if(current == null  || current.control.state == State.init)
        {
            this.buildResponse(ResponseType.info_text, "棋局还没有初始化，发送{中国象棋}或者{联棋}初始化棋局并且加入后才能设置盲棋", null);
            return true;
        }

        sender_idx = getSenderIdx(msg);
        if(sender_idx < 0 && current.control.state == State.prepare)
        {
            this.buildResponse(ResponseType.info_text, "你还没有加入，要先加入后才能设置是否盲棋", null);
            return true;
        }
        else if (sender_idx >= 0)
        {
            char result = '关';
            current.control.blind_chess = (! current.control.blind_chess);
            if(current.control.blind_chess)
            {
                result = '开';
            }
            this.buildResponse(ResponseType.info_text, "盲棋" + result, null);
            return true;
        }

        this.buildResponse(ResponseType.none, null, null);
        return true;
    }
    private boolean getMap(RequestInfo msg)
    {
        EveryChess current = this.dic.get(msg.group);
        int sender_idx = -1;
        if(!msg.cmd.equals("棋盘"))
        {
            return false;
        }

        if(current == null  || current.control.state != State.began)
        {
            this.buildResponse(ResponseType.none, null, null);
            return true;
        }

        sender_idx = getSenderIdx(msg);
        if(sender_idx < 0)
        {
            this.buildResponse(ResponseType.none, null, null);
            return true;
        }

        this.buildResponse(ResponseType.image, null, current.control.out_img);
        return true;
    }

    /*----------------匹配命令函数区结束----------------------*/

    private static String splitCmd(String cmd, int start, int end)
    {
        if (cmd.length() <= end - start)
        {
            return cmd;
        }

        return cmd.substring(start, end);

    }

    private void buildResponse(ResponseType type, String msg, File image)
    {
        this.response.msg = msg;
        this.response.type = type;
        this.response.image = image;
    }

    private void answerChangeChess(RequestInfo msg, int player_count)
    {
        EveryChess current = this.dic.get(msg.group);
        if (current == null)
        {
            EveryChess every_chess = new EveryChess(player_count, msg.group);
            every_chess.control.state = State.prepare;
            this.dic.put(msg.group, every_chess);

            this.buildResponse(ResponseType.info_text, "棋局初始化成功，发送{加入棋局}即可加入游戏，发送{加入棋局 红}即可加入棋局执红", null);
            return;
        }

        State state = current.control.state;

        if(player_count != current.player_count)
        {
            if(state == State.init || (state == State.prepare && current.has_add_count == 0))
            {
                current.initPlayers(player_count);
                this.buildResponse(ResponseType.info_text, "棋局初始化成功，发送{加入棋局}即可加入游戏，发送{加入棋局 红1}即可加入棋局执红1", null);
            }
            else if (state ==State.prepare)
            {
                this.buildResponse(ResponseType.info_text, "本群组已经初始化了其他棋局，不能再进行初始化操作", null);
            }
            else if(state == State.began)
            {
                this.buildResponse(ResponseType.info_text, "棋局已经开始，快来观战吧：\n" + dic.get(msg.group).getPlayerMsg(), null);
            }
        }
        else
        {
            if(state == State.init)
            {
                current.control.state = State.prepare;
                if(player_count > 2)
                {
                    this.buildResponse(ResponseType.info_text, "棋局初始化成功，发送{加入棋局}即可加入游戏，发送{加入棋局 红1}即可加入棋局执红1", null);
                }
                else
                {
                    this.buildResponse(ResponseType.info_text, "棋局初始化成功，发送{加入棋局}即可加入游戏，发送{加入棋局 红}即可加入棋局执红", null);
                }

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
            }
            else if(state == State.began)
            {
                this.buildResponse(ResponseType.info_text, "棋局已经开始，快来观战吧：\n" + dic.get(msg.group).getPlayerMsg(), null);
            }
        }
        
    }

    private boolean addIllegal(RequestInfo msg, EveryChess current)
    {
        int sender_idx = -1;
        if(current == null || current.control.state == State.init)
        {
            this.buildResponse(ResponseType.info_text, "棋局还没有初始化，发送{中国象棋}或者{联棋}初始化棋局后才能加入", null);
            return true;
        }

        if(current.control.state == State.began)
        {
            this.buildResponse(ResponseType.info_text, "棋局已经开始，快来观战吧：\n" + current.getPlayerMsg(), null);
            return true;
        }

        sender_idx = this.getSenderIdx(msg);
        if(sender_idx >= 0)
        {
            this.buildResponse(ResponseType.info_text, current.players[sender_idx].name + "，你已经加入了棋局，耐心等待其他棋手吧！", null);
            return true;
        }
        return false;
    }

    /**
    * @description: 必须保证已经有EveryChess对象
     *@param msg msg
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
            if (msg.id.equals(players[idx].id))
            {
                return idx;
            }
        }
        return -1;
    }

    private int getRandomEmptyIdx(Team team, EveryChess current)
    {
        ArrayList<Integer> empty = new ArrayList<>();
        int size = 0;

        for(int idx = 0;idx<current.player_count;idx++)
        {
            if((team == Team.red && idx % 2 == 1) || (team == Team.black && idx % 2 == 0))
            {
                continue;
            }
            if(current.players[idx].id == null)
            {
                empty.add(idx);
            }
        }
        size = empty.size();
        if(size == 0)
        {
            return -1;
        }
        return empty.get(current.random.nextInt(0, empty.size()));
    }


    public static String getTimeStr(long milliseconds)
    {
        int totalSeconds = (int) (milliseconds / 1000);
        int hour =  totalSeconds / 3600;
        int minute = (totalSeconds % 3600) / 60;
        int remainingSeconds = totalSeconds % 60;

        if (hour > 0)
        {
            return hour + "时" + minute + "分" + remainingSeconds + "秒";
        }
        else if (minute > 0)
        {
            return minute + "分" + remainingSeconds + "秒";
        }
        else
        {
            return remainingSeconds + "秒";
        }
    }

    private boolean fullAndBegin(EveryChess current)
    {

        if(current.has_add_count == current.player_count)
        {
            current.control.start();
            return true;
        }
        else
        {
            return false;
        }
    }

}
