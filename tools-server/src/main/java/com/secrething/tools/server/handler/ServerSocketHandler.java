package com.secrething.tools.server.handler;

import com.secrething.tools.common.HttpPoolManage;
import com.secrething.tools.common.MessageProtocol;
import com.secrething.tools.common.RequestEntity;
import com.secrething.tools.common.ResponseEntity;
import com.secrething.tools.common.utils.MesgFormatter;
import com.secrething.tools.common.utils.SerializeUtil;
import com.secrething.tools.common.utils.TypeUtil;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import net.sf.cglib.reflect.FastClass;
import net.sf.cglib.reflect.FastMethod;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author liuzz
 * @create 2018/3/14
 */
public class ServerSocketHandler extends ChannelInboundHandlerAdapter {
    private static final FastClass fastClass;
    private static final Logger logger = LoggerFactory.getLogger(ServerSocketHandler.class);
    static {
        Class<HttpPoolManage> clzz = HttpPoolManage.class;
        fastClass = FastClass.create(clzz);
    }

    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        MessageProtocol inputMsg = (MessageProtocol) msg;
        Object result = "request fail";
        RequestEntity request = null;
        ResponseEntity respnseModel = new ResponseEntity();
        try {
            request = SerializeUtil.deserialize(inputMsg.getContent(), RequestEntity.class);
            respnseModel.setRequest(request);
            Object[] params = request.getParams();
            Class[] paramTypes = new Class[params.length];
            for (int i = 0; i < params.length; i++) {
                paramTypes[i] = TypeUtil.getBasicType(params[i].getClass());
            }
            logger.info(MesgFormatter.format("request={}", request.toString()));
            FastMethod method = fastClass.getMethod(request.getMethodName(), paramTypes);
            result = method.invoke(null, params);
            logger.info("result={}",result);
        } catch (Throwable e) {
            logger.error("",e);
            respnseModel.setThrowable(e);
        }
        respnseModel.setResult(result);
        byte[] resb = SerializeUtil.serialize(respnseModel);
        MessageProtocol outMsg = new MessageProtocol(resb.length, resb);
        outMsg.setMessageUID(inputMsg.getMessageUID());
        ctx.writeAndFlush(outMsg);
    }

    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        ctx.close();
    }

    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        ctx.flush();
    }
}
