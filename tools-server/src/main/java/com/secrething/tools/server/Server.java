package com.secrething.tools.server;

import com.secrething.tools.server.factory.ServerBootstrapFactory;
import com.secrething.tools.server.handler.ServerInitializer;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelOption;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * @author liuzz
 * @create 2018/3/14
 */
public class Server {
    private static final Logger logger = LoggerFactory.getLogger(Server.class);
    private int port;

    public Server(int port) {
        this.port = port;
    }

    public void run() throws Exception {
        //暂时只用 nio方式吧
        ServerBootstrap b = ServerBootstrapFactory.newNioServerBootstrap();
        try {
            b.childHandler(new ServerInitializer()).option(ChannelOption.SO_BACKLOG, Integer.valueOf(128)).childOption(ChannelOption.SO_KEEPALIVE, Boolean.valueOf(true));
            ChannelFuture f = b.bind(this.port).sync();
            logger.info("server started !");
            f.channel().closeFuture().sync();
        } finally {
            b.childGroup().shutdownGracefully();
            b.group().shutdownGracefully();
        }

    }

    public static void main(String[] args) throws Exception {
        int port = 9999;
        if (args.length == 1) {
            port = Integer.valueOf(args[0]).intValue();
        }
        (new Server(port)).run();
    }
}
