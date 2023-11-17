package ChineseChess.Adaptor;

import java.io.File;

public class ResponseInfo
{
    /**
    * @description: 输出类型。本模块对命令的响应只存在两种，提示信息：字符串类型；图片：文件类型。通过ResponseType可以获知返回的信息储存在哪个变量中.
    * 然后读取对应变量即可。
    * @author: BigCherryBall
    * @time: 2023/11/14 21:54
    */
    public ResponseType type;

    public String msg;

    public File image;
}
