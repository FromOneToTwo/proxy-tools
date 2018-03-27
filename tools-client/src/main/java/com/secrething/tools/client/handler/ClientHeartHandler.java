package com.secrething.tools.client.handler;

import com.secrething.tools.common.protocol.MessageProtocol;
import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;

import java.util.UUID;

/**
 * Created by secret on 2018/3/26.
 */
public class ClientHeartHandler extends ChannelDuplexHandler {
    int m = 0;
    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        if (evt instanceof IdleStateEvent) {
            IdleStateEvent event = (IdleStateEvent) evt;
            if (event.state() == IdleState.WRITER_IDLE && m < 3) {
                String s = "not die";
                byte[] bytes = s.getBytes();
                MessageProtocol protocol = new MessageProtocol(bytes.length, bytes);
                protocol.setMessageUID(UUID.randomUUID().toString());
                protocol.setMesg_type(MessageProtocol.HEART);
                ctx.channel().writeAndFlush(protocol);
                m ++ ;
            }
        }
    }
}
