package com.secrething.tools.common.protocol;

import com.secrething.tools.common.contant.ConstantValue;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

import java.util.List;
import java.util.UUID;

/**
 * @author liuzz
 * @create 2018/3/14
 */
public class ProtocolDecoder extends ByteToMessageDecoder {
    private final int BASE_LENGTH = 16;

    public ProtocolDecoder() {
    }

    protected void decode(ChannelHandlerContext ctx, ByteBuf buffer, List<Object> out) throws Exception {
        if(buffer.readableBytes() >= BASE_LENGTH) {
            while(true) {
                int beginReader = buffer.readerIndex();
                buffer.markReaderIndex();
                if(ConstantValue.HEAD_DATA == buffer.readInt()) {
                    int msgType = buffer.readInt();
                    byte[] uidBytes = new byte[36];
                    buffer.readBytes(uidBytes);
                    String messageUID = new String(uidBytes);
                    int length = buffer.readInt();
                    if(buffer.readableBytes() < length) {
                        buffer.readerIndex(beginReader);
                        return;
                    }

                    byte[] data = new byte[length];
                    buffer.readBytes(data);
                    MessageProtocol protocol = new MessageProtocol(data.length, data);
                    protocol.setMessageUID(messageUID);
                    protocol.setMesg_type(msgType);
                    out.add(protocol);
                    break;
                }

                buffer.resetReaderIndex();
                buffer.readByte();
                if(buffer.readableBytes() < BASE_LENGTH) {
                    return;
                }
            }
        }

    }

    public static void main(String[] args) {
        for (int i = 0; i < 10; i++) {
            System.out.println(UUID.randomUUID().toString().getBytes().length);
        }
    }
}
