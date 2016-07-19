package org.zt.dubbo.filter.sqlhint.mycat;

import java.util.Map;

import org.zt.dubbo.context.RouterContext;

import com.alibaba.dubbo.common.Constants;
import com.alibaba.dubbo.common.extension.Activate;
import com.alibaba.dubbo.common.logger.Logger;
import com.alibaba.dubbo.common.logger.LoggerFactory;
import com.alibaba.dubbo.rpc.Filter;
import com.alibaba.dubbo.rpc.Invocation;
import com.alibaba.dubbo.rpc.Invoker;
import com.alibaba.dubbo.rpc.Result;
import com.alibaba.dubbo.rpc.RpcContext;
import com.alibaba.dubbo.rpc.RpcException;


/**
 * Mycat路由器，消费端
 * 
 * @author Ternence
 * @date 2016年7月14日
 */
@Activate(group = Constants.CONSUMER)
public class MycatRouterConsumerFilter implements Filter {

    private static final String ROUTER_KEY = "_mysqlrouter";
    private final Logger logger;

    public MycatRouterConsumerFilter() {
        this(LoggerFactory.getLogger(MycatRouterConsumerFilter.class));
    }

    public MycatRouterConsumerFilter(Logger logger) {
        this.logger = logger;
    }

    /**
     * 消费端
     */
    public Result invoke(Invoker<?> invoker, Invocation invocation) throws RpcException {
        try {
            // 接收到调用的时候以传入的为准
            String sqlrouter = RouterContext.get(ROUTER_KEY);
            if (sqlrouter != null && invocation.getAttachment(ROUTER_KEY) == null) {
                // 如果路由信息不为空则执行并传递该路由
                invocation.getAttachments().put(ROUTER_KEY, sqlrouter);
            }

            Result result = invoker.invoke(invocation);

            Map<String, String> attachments = result.getAttachments();
            // 返回结果时进行判断，如果当前调用链中包含了路由信息则往回传递该路由
            if (attachments.containsKey(ROUTER_KEY)) {

                // 写入上下文
                if (sqlrouter == null) {
                    RouterContext.put(ROUTER_KEY, attachments.get(ROUTER_KEY));
                }
            }

            return result;
        } catch (RuntimeException e) {
            logger.error("Got unchecked and undeclared exception which called by " + RpcContext.getContext().getRemoteHost() + ". service: " + invoker
                            .getInterface().getName() + ", method: " + invocation.getMethodName() + ", exception: " + e.getClass().getName() + ": " + e
                            .getMessage(), e);
            throw e;
        }
    }

}
