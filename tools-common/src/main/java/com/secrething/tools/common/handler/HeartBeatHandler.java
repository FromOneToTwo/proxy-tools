package com.secrething.tools.common.handler;

import com.secrething.tools.common.protocol.MessageProtocol;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Created by wuhui on 16/6/4. 心跳响应Handler
 */
public class HeartBeatHandler extends SimpleChannelInboundHandler<MessageProtocol> {

    private static final Logger logger = LoggerFactory.getLogger(HeartBeatHandler.class);

    private int times = 0;

    private int maxIdleTimes;


    public HeartBeatHandler(int maxIdleTimes) {
        this.maxIdleTimes = maxIdleTimes;
    }

    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, MessageProtocol protocol) throws Exception {
        if (MessageProtocol.HEART == protocol.getMesg_type()) {
            Channel channel = channelHandlerContext.channel();
            String heartMsg = "heart live";
            byte[] bytes = heartMsg.getBytes();
            MessageProtocol messageProtocol = new MessageProtocol(bytes.length, bytes);
            if (false) {
                times++;
                if (times > maxIdleTimes) {
                    logger.info("close channel while not a room for a while. channel {} maxIdleTimes {}", channel, maxIdleTimes);
                    channelHandlerContext.close();
                }
            } else {
                times = 0;
            }

            channelHandlerContext.writeAndFlush(messageProtocol);
        } else {
            channelHandlerContext.fireChannelRead(protocol);
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        logger.error("process heartbeat error. channel info {}", ctx.channel(), cause);
        ctx.channel().close();
    }
}
