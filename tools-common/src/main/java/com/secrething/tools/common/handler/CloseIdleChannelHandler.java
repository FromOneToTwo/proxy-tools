package com.secrething.tools.common.handler;

import com.secrething.tools.common.utils.MesgFormatter;
import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by secret on 2018/3/26.
 */
public class CloseIdleChannelHandler extends ChannelDuplexHandler {
    private static final Logger logger = LoggerFactory.getLogger(CloseIdleChannelHandler.class);

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {

        if (evt instanceof IdleStateEvent) {
            IdleStateEvent event = (IdleStateEvent) evt;
            if (event.state() == IdleState.ALL_IDLE) {
                String s = MesgFormatter.format("connector no receive ping packet from client,will close.,channel:{}", ctx.channel());
                System.out.println(s);
                ctx.close();
            }
        }
    }
}
