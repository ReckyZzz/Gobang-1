package socket;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Scanner;

public class Client {

    final static String charset = "GB2312";

    private static String id;

    public static void main(String[] args) throws UnknownHostException, IOException {

        // 要连接的服务端IP地址和端口
        String host = "47.95.120.196";
        int port = 5555;
        // 与服务端建立连接
        final Socket socket = new Socket(host, port);
        // 建立连接后获得输出流
        OutputStream outputStream = socket.getOutputStream();

        // 发送消息进程
        Thread sendThread = new Thread(new Runnable() {

            public void run() {
                Scanner sc = new Scanner(System.in);
                while (true) {
                    String info = sc.next();
                    try {
                        socket.getOutputStream().write(pack(id + ":" + info));
                    } catch (UnsupportedEncodingException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    } catch (IOException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }

                }

            }
        });

        // 接受消息的进程
        Thread getThread = new Thread(new Runnable() {

            @Override
            public void run() {
                try {
                    id = getInfo(socket);
                    System.out.println("您已经进入游戏,您的游戏id号码为" + id);

                    while (true) {
                        try {
                            String info = getInfo(socket);

                            System.out.println(info);
                        } catch (Exception e) {
                            continue;
                        }

                    }
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                // TODO Auto-generated method stub
                catch (Exception e1) {
                    // TODO Auto-generated catch block
                    e1.printStackTrace();
                }

            }
        });
        getThread.start();
        sendThread.start();

    }

    // 读取输入流
    public static String read(InputStream in, int begin, int len) throws IOException {

        byte[] b = new byte[1024];
        in.read(b, 0, len);
        return new String(b, 0, len, charset);

    }

    public static byte[] pack(String info) throws UnsupportedEncodingException {
        byte b[] = info.getBytes(charset);
        String len = b.length + "";
        while (len.length() < 8) {
            len = "0" + len;
        }

        String s = len + info;

        return s.getBytes(charset);

    }

    // 从socket中获取打包过来的数据，先获取长度，再获取真正的内容
    public static String getInfo(Socket socket) throws Exception {

        InputStream in = socket.getInputStream();
        byte b[] = new byte[1024];

        String len = read(in, 0, 8);

        String info = read(in, 0, Integer.valueOf(len));

        return info;
    }

}