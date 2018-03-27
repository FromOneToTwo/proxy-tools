package com.secrething.tools.server.handler;

import com.secrething.tools.common.protocol.MessageProtocol;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by secret on 2018/3/26.
 */
public class ServerHeartHandler extends SimpleChannelInboundHandler<MessageProtocol> {
    private static final Logger logger = LoggerFactory.getLogger(ServerHeartHandler.class);
    private int times = 0;
    private final int maxTimes;

    public ServerHeartHandler(int maxTimeoutTimes) {
        this.maxTimes = maxTimeoutTimes;
    }

    @Override
    public void channelRead0(ChannelHandlerContext ctx, MessageProtocol in) throws Exception {
        times = 0;
        ctx.fireChannelRead(in);
    }

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        if (evt instanceof IdleStateEvent) {
            IdleStateEvent event = (IdleStateEvent) evt;
            if (event.state() == IdleState.READER_IDLE) {
                if (times < maxTimes) {
                    times++;
                } else{
                    logger.info("five heartbeat cycles not read, so colse this channel");
                    ctx.close();
                }

            }
        }
    }
}
