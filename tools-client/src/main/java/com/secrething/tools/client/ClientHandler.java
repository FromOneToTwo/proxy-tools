package com.secrething.tools.client;

import com.secrething.tools.common.MessageProtocol;
import com.secrething.tools.common.RequestEntity;
import com.secrething.tools.common.ResponseEntity;
import com.secrething.tools.common.utils.SerializeUtil;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.ReferenceCountUtil;

import java.util.UUID;

/**
 * @author liuzz
 * @create 2018/3/14
 */
public class ClientHandler extends ChannelInboundHandlerAdapter {
    private final MessageFuture messageFuture;

    public ClientHandler(MessageFuture messageFuture) {
        this.messageFuture = messageFuture;
    }

    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        try {
            MessageProtocol mesg = (MessageProtocol) msg;
            ResponseEntity respnse = (ResponseEntity) SerializeUtil.deserialize(mesg.getContent(), ResponseEntity.class);
            this.messageFuture.done(respnse);
            ctx.close();
        } finally {
            ReferenceCountUtil.release(msg);
        }

    }

    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        ctx.close();
    }

    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        RequestEntity request = messageFuture.getRequest();
        byte[] content = SerializeUtil.serialize(request);
        int contentLength = content.length;
        MessageProtocol protocol = new MessageProtocol(contentLength, content);
        protocol.setMessageUID(UUID.randomUUID().toString());
        ctx.writeAndFlush(protocol);
    }

    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        ctx.fireChannelReadComplete();
    }
}
