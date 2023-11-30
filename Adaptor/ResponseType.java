package ChineseChess.Adaptor;


/**
* @description: 响应类型。本模块响应结构体只存在两种：文字信息（ResponseInfo.msg），图片(ResponseInfo.image)。根据ResponseType的类型，从而确定返回的信息中取用哪个字段。
* 例如，如果返回的ResponseInfo.type为error_text,那么ResponseInfo.msg就一定不是null，而image一定是null；
*      如果返回的ResponseInfo.type为image,那么ResponseInfo.msg就一定是null，而image一定不是null；
* image_and_info 和 info_and_image 的msg和image都不是null，这样区别的原因是告诉调用者优先使用文字还是图片。因为部分命令需要同时返回图片和文字，而两者顺序有讲究。
* 例如： 最后一步棋时（绝杀）时，应当先发送棋局图片，然后再发送棋局结算文字信息，此时响应ResponseInfo.type = image_and_info
* 例如： 最后一个棋手加入棋局时，应当先发送加入成功的提示信息，然后再发送初始棋局图片，此时响应ResponseInfo.type = info_and_image
* 你需要根据type的不同选择使用ResPonseInfo对象中的msg和image变量
* @author: BigCherryBall
* @time: 2023/11/14 21:54
*/
public enum ResponseType
{
    none,                        /*输入的命令不是本模块命令。返回：无*/
    info_text,                   /*是本模块命令，执行结果视情况而定。返回：文字信息*/
    image,                       /*是本模块命令，成功执行。返回：图片*/
    image_and_info,              /*是本模块命令，成功执行。返回：图片+文字*/
    info_and_image;              /*是本模块命令，成功执行。返回：文字+图片*/
}
