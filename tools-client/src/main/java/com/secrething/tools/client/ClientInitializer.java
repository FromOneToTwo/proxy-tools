package com.secrething.tools.client;

import com.secrething.tools.client.handler.ClientHandler;
import com.secrething.tools.client.handler.ClientHeartHandler;
import com.secrething.tools.common.handler.CloseIdleChannelHandler;
import com.secrething.tools.common.protocol.ProtocolDecoder;
import com.secrething.tools.common.protocol.ProtocolEncoder;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.timeout.IdleStateHandler;

/**
 * Created by secret on 2018/3/26.
 */
public class ClientInitializer extends ChannelInitializer<SocketChannel> {
    @Override
    public void initChannel(SocketChannel ch) throws Exception {
        ch.pipeline().addLast("idleStateHandler",new IdleStateHandler(0, 15,0));
        ch.pipeline().addLast("mesgEncoder",new ProtocolEncoder());
        ch.pipeline().addLast("mesgDecoder",new ProtocolDecoder());
        ch.pipeline().addLast("clientHeartHandler",new ClientHeartHandler());
        ch.pipeline().addLast("clientHandler",new ClientHandler());
    }
}
