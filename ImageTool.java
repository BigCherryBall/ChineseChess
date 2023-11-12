package ChineseChess;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;


 class CombineImages {

    public static void main(String[] args)
    {
       main1(0,0);
    }
   public static void main1(int x, int y) {
      try {
         File back = new File("background.jpg");
         System.out.println(back.getAbsolutePath());
         // 加载背景图
         BufferedImage background = ImageIO.read(back);
         
         // 加载a.png图片
         BufferedImage imageA = ImageIO.read(new File("a.png"));
         
         // 加载b.png图片
         BufferedImage imageB = ImageIO.read(new File("b.png"));
         
         // 创建新的BufferedImage对象，宽度和高度与背景图相同
         BufferedImage combined = new BufferedImage(background.getWidth(), background.getHeight(), BufferedImage.TYPE_INT_ARGB);
         
         // 获取Graphics2D对象
         Graphics2D g = combined.createGraphics();
         
         // 绘制背景图
         g.drawImage(background, 0, 0, null);
         
         // 绘制a.png图片
         g.drawImage(imageA, x, y, null); // 替换x和y为图片在背景图上的位置
         
         // 绘制b.png图片
         g.drawImage(imageB, x, y, null); // 替换x和y为图片在背景图上的位置
         
         // 保存合并后的图片到c.jpg
         File output = new File("c.jpg");
         ImageIO.write(combined, "png", output);
         
         // 关闭Graphics2D对象
         g.dispose();

         System.out.println("图片保存成功");

         
      } catch (IOException e) {
         e.printStackTrace();
      }
   }
}