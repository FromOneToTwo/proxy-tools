package com.secrething.tools.ws.server;

import com.secrething.tools.common.utils.DateUtil;
import com.secrething.tools.common.utils.ParkUtil;
import io.netty.channel.group.ChannelGroup;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;

import java.util.Date;
import java.util.concurrent.TimeUnit;

/**
 * @author liuzz
 * @create 2018/3/16
 */
public class App {
    static final String REFRESH_TIME_ID = "1234";

    public static void main(String[] args) throws InterruptedException {
        int port = 9999;
        if (args.length == 1) {
            port = Integer.valueOf(args[0]).intValue();
        }
        final WebSocketServer webSocketServer = new WebSocketServer(port);
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    webSocketServer.run();
                } catch (Exception e) {
                }
            }
        }).start();
        webSocketServer.getCountDownLatch().await();

        while (true) {
            long curr = System.currentTimeMillis();
            String date = DateUtil.getSpecificFormatTime(new Date(), "yyyy-MM-dd HH:mm:ss");
            System.out.println(date);
            if (!WebSocketServerHandler.GROUPS.isEmpty() && WebSocketServerHandler.GROUPS.containsKey(REFRESH_TIME_ID)) {
                ChannelGroup group = WebSocketServerHandler.GROUPS.get(REFRESH_TIME_ID);
                TextWebSocketFrame tws = new TextWebSocketFrame(date);
                group.writeAndFlush(tws);
            }
            /*Thread.sleep(1000);*/
            ParkUtil.park((1000 - System.currentTimeMillis() + curr), TimeUnit.MILLISECONDS);
        }
    }
}
