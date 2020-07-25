package chat;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Properties;

// 若一个类需要界面显示就需要继承JFrame类，该类被称为窗体类
// 1. 定义JFrame窗体中的组件
// 2. 在构造方法中初始化窗体组件
// 3. 使用网络编程完成数据传输（TCP UDP协议）
// 4. 实现发送按钮的监听点击事件
// 5. 实现回车键发送数据
public class ServerChatMain extends JFrame implements ActionListener, KeyListener {
    public static void main(String[] args) {
        // 调用构造方法
        new ServerChatMain();
    }
    // 属性
    // 文本域
    private JTextArea jta;
    // 滚动条
    private JScrollPane jsp;
    // 面板
    private JPanel jp;
    // 文本框
    private JTextField jtf;
    // 按钮
    private JButton jb;
    // 输出流
    private BufferedWriter bw = null;
    // 服务端端口号
    private static int serverPort;
    // 使用static静态代码块读取外部文件
    // 特点1：在类加载的时候自动执行
    // 特点2：一个类只被加载一次，因此静态代码块在程序中仅会被执行一次
    static {
        Properties prop = new Properties();
        try {
            // 加载
            prop.load(new FileReader("chat.properties"));
            //给属性赋值
            serverPort = Integer.parseInt(prop.getProperty("serverPort"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    // 构造方法
    public ServerChatMain() {
        // 初始化组件
        jta = new JTextArea();
        // 设置文本域默认不可编辑
        jta.setEditable(false);
        // 将文本域添加到滚动条中，实现滚动效果
        jsp = new JScrollPane(jta);
        // 面板
        jp = new JPanel();
        jtf = new JTextField(10);
        jb = new JButton("发送");
        // 需要将文本框和按钮添加到面板上
        jp.add(jtf);
        jp.add(jb);
        //将滚动条和面板添加到窗体中
        this.add(jsp, BorderLayout.CENTER);
        this.add(jp,BorderLayout.SOUTH);
        // 注意需要设置窗体标题，大小，位置，关闭，是否可见
        this.setTitle("ServerChat");
        this.setSize(500, 500);
        this.setLocation(500, 500);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); // 表示窗体关闭程序就退出
        this.setVisible(true);
        /********************* TCP server start ********************/
        // 给发送按钮绑定一个监听点击事件
        jb.addActionListener(this);
        // 给文本框绑定一个键盘点击事件
        jtf.addKeyListener(this);
        try {
            // 1. 创建服务端的套接字
            ServerSocket serverSocket = new ServerSocket(serverPort);

            // 2. 等待客户端连接
            Socket socket = serverSocket.accept();

            // 3. 获取socket通道的输入流(一行一行读取数据）
            InputStream in = socket.getInputStream();
            BufferedReader br = new BufferedReader(new InputStreamReader(in));

            // 4. 获取socket通道的输出流（输出流写数据也是写一行换一行刷新）
            OutputStream out = socket.getOutputStream();
            bw = new BufferedWriter(new OutputStreamWriter(out));

            // 循环读取并将数据拼接到文本域中
            String line = null;
            while ((line = br.readLine()) != null) {
                // 将读取到的数据拼接到文本域中显示
                jta.append(line + System.lineSeparator());
            }


            // 5. 关闭socket通道
            serverSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        /********************* TCP server end **********************/
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        sendDataToSocket();
    }

    @Override
    public void keyPressed(KeyEvent e) {
        // 回车键
        if (e.getKeyCode() == KeyEvent.VK_ENTER) {
            // 发送数据到socket中
            sendDataToSocket();
        }
    }
    // 定义一个方法 将数据发送到socket通道中
    private  void sendDataToSocket() {
        // 1. 获取文本框中的内容
        String text = jtf.getText();
        // 2. 拼接需要发送的内容
        text = "服务器对客户端说:" + text;
        // 3. 自己要显示
        jta.append(text + System.lineSeparator());

        try {
            // 4. 发送
            bw.write(text);
            bw.newLine();
            bw.flush();
            // 5. 清空文本框
            jtf.setText("");
        } catch (IOException ioException) {
            ioException.printStackTrace();
        }
    }
    @Override
    public void keyTyped(KeyEvent e) {

    }

    @Override
    public void keyReleased(KeyEvent e) {

    }
}
