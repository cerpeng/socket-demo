/**
 * @author cerpengxi
 * @date 17/9/5 下午5:02
 */

import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.Socket;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

public class FileSender extends JFrame {
    FileDialog fileDialog = null;
    DatagramSocket datagramSocket = null;
    DatagramPacket datagramPacket = null;
    private static int sendDataLength = 10240;
    public byte[] sendBuff = new byte[sendDataLength];
    public InetAddress udpIP = null;
    private static int udpPort = 50000;
    private static int tcpPort = 9876;
    private static Logger logger = Logger
            .getLogger("FileSender Logger");
    private static String host = "127.0.0.1";

    public void fileSender() {
        try {
            fileDialog = new FileDialog(this, "please open the file", FileDialog.LOAD);
            fileDialog.setVisible(true);
            String filePath = fileDialog.getDirectory() + fileDialog.getFile();
            String location = filePath.replaceAll("\\\\", "/");
            logger.info("absolute path: " + filePath);
            DataInputStream dis = new DataInputStream(new BufferedInputStream(
                    new FileInputStream(location)));
            int fileLength = dis.available();

            logger.info("file length: " + fileLength);

            // 基于TCP协议发送文件请求
            Socket s = new Socket(host, tcpPort);
            PrintWriter pw = new PrintWriter(s.getOutputStream(), true);
            String headInfo = fileDialog.getFile() + "/" + fileLength;
            pw.println(headInfo);
            // 阻塞等待对方确认
            InputStreamReader isr = new InputStreamReader(s.getInputStream());
            BufferedReader br = new BufferedReader(isr);
            String info = br.readLine();
            logger.info("received notice content: " + info);
            if (info.equals("YES")) {
                s.close();
                logger.info("start send file");
                // 基于UDP协议传输文件
                datagramSocket = new DatagramSocket();
                udpIP = InetAddress.getByName(host);
                while (dis.read(sendBuff) != -1) {
                    datagramPacket = new DatagramPacket(sendBuff, sendBuff.length,
                            udpIP, udpPort);
                    datagramSocket.send(datagramPacket);
                    // 限制传输速度
                    TimeUnit.MICROSECONDS.sleep(1);
                }
                datagramSocket.close();
            } else {
                JOptionPane.showMessageDialog(null, "refuse to receive file", "message",
                        JOptionPane.WARNING_MESSAGE);
                dis.close();
                s.close();
            }
            logger.info("send file success");
            dis.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public static void main(String[] args) {
        FileSender fc = new FileSender();
        fc.fileSender();
    }
}
