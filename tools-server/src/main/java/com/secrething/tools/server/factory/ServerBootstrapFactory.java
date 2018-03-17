package com.secrething.tools.server.factory;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.epoll.EpollEventLoopGroup;
import io.netty.channel.epoll.EpollServerSocketChannel;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;

/**
 * @author liuzz
 * @create 2018/3/15
 */
public class ServerBootstrapFactory {
    private ServerBootstrapFactory() {
        throw new UnsupportedOperationException("instance not support");
    }

    public final static ServerBootstrap newNioServerBootstrap() {
        ServerBootstrap b = new ServerBootstrap();
        return b.group(new NioEventLoopGroup(), new NioEventLoopGroup()).channel(NioServerSocketChannel.class);
    }

    public final static ServerBootstrap newEpollServerBootstrap() {
        ServerBootstrap b = new ServerBootstrap();
        return b.group(new EpollEventLoopGroup(), new EpollEventLoopGroup()).channel(EpollServerSocketChannel.class);
    }
}
