package com.secrething.tools.client.handler;

import com.secrething.tools.client.Client;
import com.secrething.tools.client.MessageFuture;
import com.secrething.tools.common.protocol.MessageProtocol;
import com.secrething.tools.common.protocol.ResponseEntity;
import com.secrething.tools.common.utils.SerializeUtil;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.util.AttributeKey;

import java.util.concurrent.CopyOnWriteArrayList;

/**
 * @author liuzz
 * @create 2018/3/14
 */
public class ClientHandler extends SimpleChannelInboundHandler<MessageProtocol> {
    private static final CopyOnWriteArrayList<Channel> CHANNELS = new CopyOnWriteArrayList<>();
    private static final AttributeKey<Integer> CHANNEL_ID = AttributeKey.valueOf("channelId");

    public void channelRead0(ChannelHandlerContext ctx, MessageProtocol mesg) throws Exception {
        if (mesg.getMesg_type() == MessageProtocol.PROXY) {
            ResponseEntity respnse = (ResponseEntity) SerializeUtil.deserialize(mesg.getContent(), ResponseEntity.class);
            MessageFuture future = Client.futureConcurrentMap.get(mesg.getMessageUID());
            if (null != future)
                future.done(respnse);
        }
    }

    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();

    }

    public void channelActive(ChannelHandlerContext ctx) throws Exception {

    }

    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        ctx.fireChannelReadComplete();
    }
}
