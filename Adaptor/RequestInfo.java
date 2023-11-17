package ChineseChess.Adaptor;

public class RequestInfo
{
    /*---------必要参数，以下4个变量不能为null，否则会直接返回ResponseType = none---------*/
    /*输入的命令，即发送者发出的文字消息*/
    public String cmd;
    /*发送这个消息的人的唯一辨识号，可以理解为QQ中的QQ号，微信中的微信号*/
    public String id;
    /*发送者的昵称，用于在消息提示时更明确。例如QQ可以使用QQ昵称或者群昵称*/
    public String name;
    /*发送消息的人所在的群体的唯一辨识号，可以理解为QQ中的QQ群号，微信中的微信群号。因为下棋一定是一个群体而非单人。如果后续考虑到人机对弈，那么这个变量可以同id*/
    public String group;
    

    /*----------非必要参数，可以为空----------*/
    /*发送消息的人所提到的其他人的id,可以理解为他@了谁。可以为null，不影响绝大部分功能的使用。个别命令需要艾特别人才能完成。*/
    public String mentioned_id;

}
