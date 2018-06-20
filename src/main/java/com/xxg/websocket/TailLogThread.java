package com.xxg.websocket;

import javax.websocket.Session;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * @author qiyu
 */
public class TailLogThread extends Thread {

    private BufferedReader reader;
    private Session session;


    public TailLogThread(InputStream in, Session session) {
        setDaemon(true);
        setName("TailLogThread");
        this.reader = new BufferedReader(new InputStreamReader(in));
        this.session = session;

    }

    @Override
    public void run() {
        int maxSize = 1024;
        char[] buffer = new char[maxSize];
        try {
            while (true) {
                int size = reader.read(buffer);
                if (size == 0) {
                    break;
                }
                // 将实时日志通过WebSocket发送给客户端，给每一行添加一个HTML换行
                session.getBasicRemote().sendText(String.copyValueOf(buffer));
                // 防止日志加载过快，影响客户端性能
                if (size == maxSize) {
                    Thread.sleep(100);
                }
            }
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }
}