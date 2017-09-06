/**
 * @author cerpengxi
 * @date 17/9/5 下午5:03
 */

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.logging.Logger;

class Confirm implements ActionListener {
    JLabel jl;
    JButton jb1, jb2;
    JPanel jp1, jp2;
    String headInfo = null;
    String myUserId = null;
    int fileLen = 0;
    float result = 0f;
    JFrame jf = null;
    private static String locationpath = "wait";
    private static Logger logger = Logger
            .getLogger("Confirm Logger");

    public Confirm(String myUserId, String headInfo, int fileLen) {
        jf = new JFrame();
        this.headInfo = headInfo;
        this.myUserId = myUserId;
        this.fileLen = fileLen;
        result = fileLen;
        System.out.println(myUserId + headInfo);
        // 创建
        jl = new JLabel(myUserId + " send file:【 " + headInfo + " 】，file size" + result
                + "KB,receive or not");
        jb1 = new JButton("YES");
        jb1.addActionListener(this);
        jb2 = new JButton("NO");
        jb2.addActionListener(this);
        jp1 = new JPanel();
        jp2 = new JPanel();
        // 布局管理设置
        // 添加组件
        jp1.add(jl);
        jp2.add(jb1);
        jp2.add(jb2);
        jf.add(jp1, "Center");
        jf.add(jp2, "South");
        // 设置属性
        jf.setSize(500, 120);
        jf.setTitle("MESSAGE");
        jf.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        jf.setVisible(true);
        jf.setLocation(550, 300);
    }

    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == jb1) {
            jf.dispose();
            // FileDialogTest fdt=new FileDialogTest();
            JFrame jf = new JFrame();
            FileDialog fd = new FileDialog(jf, "选择保存文件路径", FileDialog.SAVE);
            fd.setVisible(true);
            System.out.println("保存位置" + fd.getDirectory() + fd.getFile());
            logger.info("save path: " + fd.getDirectory() + fd.getFile());
            String filePath = fd.getDirectory() + fd.getFile();
            locationpath = filePath.replaceAll("\\\\", "/");
            logger.info("save path: " + locationpath);

        }
    }

    public String getLocationpath() {
        logger.info("save path: " + locationpath);
        return locationpath;
    }
}
