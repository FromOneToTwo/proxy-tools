package com.secrething.tools.common;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

/**
 * @author liuzz
 * @create 2018/3/14
 */
public class ProtocolEncoder extends MessageToByteEncoder<MessageProtocol> {
    protected void encode(ChannelHandlerContext ctx, MessageProtocol msg, ByteBuf out) throws Exception {
        out.writeInt(msg.getHead_data());
        out.writeBytes(msg.getMessageUID().getBytes());
        out.writeInt(msg.getContentLength());
        out.writeBytes(msg.getContent());
    }
}
