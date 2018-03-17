package com.secrething.tools.ws.server;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelOption;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.CountDownLatch;


/**
 * @author liuzz
 * @create 2018/3/14
 */
public class WebSocketServer {
    private static final Logger logger = LoggerFactory.getLogger(WebSocketServer.class);
    private int port;

    public WebSocketServer(int port) {
        this.port = port;
    }
    private final CountDownLatch countDownLatch = new CountDownLatch(1);
    public void run() throws Exception {
        ServerBootstrap b = ServerBootstrapFactory.newNioServerBootstrap();
        try {
            b.childHandler(new WebSocketServerInitializer()).option(ChannelOption.SO_BACKLOG, Integer.valueOf(128)).childOption(ChannelOption.SO_KEEPALIVE, Boolean.valueOf(true));
            ChannelFuture f = b.bind(this.port).sync();
            countDownLatch.countDown();
            System.out.println("server started !");
            f.channel().closeFuture().sync();
        } finally {
            b.childGroup().shutdownGracefully();
            b.group().shutdownGracefully();
        }

    }

    public CountDownLatch getCountDownLatch() {
        return countDownLatch;
    }
}
