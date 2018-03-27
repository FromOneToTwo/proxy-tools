package com.secrething.tools.server.handler;

import com.secrething.tools.common.contant.ConfigProp;
import com.secrething.tools.common.protocol.ProtocolDecoder;
import com.secrething.tools.common.protocol.ProtocolEncoder;
import com.secrething.tools.common.utils.NumberCastUtil;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.timeout.IdleStateHandler;

/**
 * Created by secret on 2018/3/26.
 */
public class ServerInitializer extends ChannelInitializer<SocketChannel> {
    public static final String READ_TIMEOUT_SECOND = "read_timeout_second";
    public static final String WRITE_TIMEOUT_SECOND = "write_timeout_second";
    public static final String ALL_TIMEOUT_SECOND = "all_timeout_second";
    public static final String MAX_TIMEOUT_TIMES = "max_timeout_times";

    @Override
    public void initChannel(SocketChannel ch) throws Exception {
        int readerIdleTimeSeconds = NumberCastUtil.intCast(ConfigProp.getConfig(READ_TIMEOUT_SECOND), 5);
        int writerIdleTimeSeconds = NumberCastUtil.intCast(ConfigProp.getConfig(WRITE_TIMEOUT_SECOND), 10);
        int allIdleTimeSeconds = NumberCastUtil.intCast(ConfigProp.getConfig(ALL_TIMEOUT_SECOND), 300);
        int maxTimeoutTimes = NumberCastUtil.intCast(ConfigProp.getConfig(MAX_TIMEOUT_TIMES), 3);
        ch.pipeline().addLast("idleStateHandler", new IdleStateHandler(readerIdleTimeSeconds, writerIdleTimeSeconds, allIdleTimeSeconds));
        ch.pipeline().addLast("mesgEncoder", new ProtocolEncoder());
        ch.pipeline().addLast("mesgDecoder", new ProtocolDecoder());
        ch.pipeline().addLast("readWriteAllListener", new ServerHeartHandler(maxTimeoutTimes));
        ch.pipeline().addLast("serverSocketHandler", new ServerSocketHandler());
    }
}
