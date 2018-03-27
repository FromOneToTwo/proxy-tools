package com.secrething.tools.server.handler;

import com.secrething.tools.common.protocol.ProtocolDecoder;
import com.secrething.tools.common.protocol.ProtocolEncoder;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.timeout.IdleStateHandler;

/**
 * Created by secret on 2018/3/26.
 */
public class ServerInitializer extends ChannelInitializer<SocketChannel> {
    @Override
    public void initChannel(SocketChannel ch) throws Exception {
        ch.pipeline().addLast("idleStateHandler",new IdleStateHandler(5, 0,300));
        ch.pipeline().addLast("mesgEncoder",new ProtocolEncoder());
        ch.pipeline().addLast("mesgDecoder",new ProtocolDecoder());
        ch.pipeline().addLast("readWriteAllListener",new ServerHeartHandler(1));
        ch.pipeline().addLast("serverSocketHandler",new ServerSocketHandler());
    }
}
