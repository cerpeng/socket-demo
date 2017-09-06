/**
 * @author cerpengxi
 * @date 17/9/5 下午5:03
 */

import javax.swing.*;
import java.io.*;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.logging.Logger;

public class FileReceiver {
    DatagramPacket receiveDp = null;
    DatagramSocket receiveDs = null;
    int dataLen = 1024;
    public byte[] inBuff = new byte[dataLen];
    String myUserId = "cerpeng";
    private static int udpPort = 50000;
    private static int tcpPort = 9876;
    private static Logger logger = Logger
            .getLogger("FileReceiver Logger");

    public void fileReceiver() {
        try {
            // 基于TCP协议接收信息
            ServerSocket ss = new ServerSocket(tcpPort);
            // 阻塞，等待接收
            Socket s = ss.accept();
            PrintWriter pw = new PrintWriter(s.getOutputStream(), true);
            InputStreamReader isr = new InputStreamReader(s.getInputStream());
            BufferedReader br = new BufferedReader(isr);
            String fileInfo = br.readLine();
            String headInfomation[] = fileInfo.split("/");
            String fileName = headInfomation[0];
            String fileLength = headInfomation[1];
            logger.info("tcp receive content: " + fileName);
            int fileLen = Integer.parseInt(fileLength);
            // 显示面板
            Confirm rc = new Confirm(myUserId, fileName, fileLen);
            String wait = rc.getLocationpath();
            // 等待存储文件的路径的产生
            while (wait.equals("wait")) {
                wait = rc.getLocationpath();
                logger.info("waiting for location path");
            }
            String headInfo = "YES";
            pw.println(headInfo);
            ss.close();
            String filePath = rc.getLocationpath();
            logger.info("save file to path:" + fileInfo);
//            new UDPReceiver(udpPort);
            DataOutputStream fileOut = new DataOutputStream(
                    new BufferedOutputStream(new FileOutputStream(filePath)));
            receiveDs = new DatagramSocket(udpPort);
            logger.info("start receive file");

            int times = fileLen / dataLen;// 循环接收的次数
            int restSize = fileLen % dataLen;// 接收剩下的字节
            for (int i = 0; i < times; i++) {
                receiveDp = new DatagramPacket(inBuff, inBuff.length);
//                receiveDs.setSoTimeout(2000);
                receiveDs.receive(receiveDp);
                fileOut.write(inBuff, 0, receiveDp.getLength());
                fileOut.flush();
            }
            // 接收最后剩下，在inBuffer中能存下。
            if (restSize != 0) {
                receiveDp = new DatagramPacket(inBuff, inBuff.length);
//                receiveDs.setSoTimeout(2000);
                receiveDs.receive(receiveDp);
                fileOut.write(inBuff, 0, receiveDp.getLength());
                fileOut.flush();
                fileOut.close();
            }
            logger.info("receive file success");
        } catch (Exception e) {
            e.printStackTrace();
            if (receiveDs != null) {
                receiveDs.close();
            }
            JOptionPane.showMessageDialog(null,
                    "发送信息异常，请确认端口空闲，且网络连接正常", "网络异常",
                    JOptionPane.ERROR_MESSAGE);
            System.exit(1);
        }
    }

    public static void main(String[] args) {
        FileReceiver fr = new FileReceiver();
        fr.fileReceiver();
    }
}

