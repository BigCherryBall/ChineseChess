package ChineseChess.Adaptor;

public class ResponseInfo
{
    /**
    * @description: 状态码。-2：输入参数不；。-1：非本模块命令；0：是本模块命令并且成功执行；1：是本模块命令但是参数不对；2：是本模块命令但是执行失败；
    * @author: BigCherryBall
    * @time: 2023/11/14 21:54
    */
    public ResponseType type;

    public String msg;
}
