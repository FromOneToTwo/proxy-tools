package com.secrething.tools.client.handler;

import com.secrething.tools.client.Client;
import com.secrething.tools.client.MessageFuture;
import com.secrething.tools.common.protocol.MessageProtocol;
import com.secrething.tools.common.protocol.RequestEntity;
import com.secrething.tools.common.protocol.ResponseEntity;
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

    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        try {
            MessageProtocol mesg = (MessageProtocol) msg;
            if (mesg.getMesg_type() == MessageProtocol.PROXY){
                ResponseEntity respnse = (ResponseEntity) SerializeUtil.deserialize(mesg.getContent(), ResponseEntity.class);
                MessageFuture future = Client.futureConcurrentMap.get(mesg.getMessageUID());
                if (null != future)
                    future.done(respnse);
            }

        } finally {
            ReferenceCountUtil.release(msg);
        }

    }

    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        ctx.close();
    }

    public void channelActive(ChannelHandlerContext ctx) throws Exception {

    }

    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        ctx.fireChannelReadComplete();
    }
}
