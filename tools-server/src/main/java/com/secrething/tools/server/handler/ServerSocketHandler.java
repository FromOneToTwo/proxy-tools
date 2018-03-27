package com.secrething.tools.server.handler;

import com.secrething.tools.common.manage.HttpPoolManage;
import com.secrething.tools.common.protocol.MessageProtocol;
import com.secrething.tools.common.protocol.Param;
import com.secrething.tools.common.protocol.RequestEntity;
import com.secrething.tools.common.protocol.ResponseEntity;
import com.secrething.tools.common.utils.MesgFormatter;
import com.secrething.tools.common.utils.SerializeUtil;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.SimpleChannelInboundHandler;
import net.sf.cglib.reflect.FastClass;
import net.sf.cglib.reflect.FastMethod;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author liuzz
 * @create 2018/3/14
 */
public class ServerSocketHandler extends SimpleChannelInboundHandler<MessageProtocol> {
    private static final FastClass fastClass;
    private static final Logger logger = LoggerFactory.getLogger(ServerSocketHandler.class);

    static {
        Class<HttpPoolManage> clzz = HttpPoolManage.class;
        fastClass = FastClass.create(clzz);
    }

    public void channelRead0(ChannelHandlerContext ctx, MessageProtocol inputMsg) throws Exception {

        if (MessageProtocol.PROXY == inputMsg.getMesg_type()){
            Object result = "request fail";
            RequestEntity request = null;
            ResponseEntity respnseModel = new ResponseEntity();
            try {
                request = SerializeUtil.deserialize(inputMsg.getContent(), RequestEntity.class);
                respnseModel.setRequest(request);
                Param[] params = request.getParams();
                Object[] rparams = new Object[params.length];
                Class[] paramTypes = new Class[params.length];
                for (int i = 0; i < params.length; i++) {
                    paramTypes[i] = params[i].getParamType();
                    rparams[i] = params[i].getTarget();
                }
                logger.info(MesgFormatter.format("request={}", request.toString()));
                FastMethod method = fastClass.getMethod(request.getMethodName(), paramTypes);
                result = method.invoke(null, rparams);
                logger.info("result={}", result);
            } catch (Throwable e) {
                logger.error("", e);
                respnseModel.setThrowable(e);
            }
            respnseModel.setResult(result);
            byte[] resb = SerializeUtil.serialize(respnseModel);
            MessageProtocol outMsg = new MessageProtocol(resb.length, resb);
            outMsg.setMessageUID(inputMsg.getMessageUID());
            outMsg.setMesg_type(MessageProtocol.PROXY);
            ctx.writeAndFlush(outMsg);
        }else
            ctx.fireChannelRead(inputMsg);

    }

    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        ctx.close();
    }

    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        ctx.flush();
    }
}
