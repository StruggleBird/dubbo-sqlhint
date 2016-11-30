package com.yunnex.sqlhint.aspect;

import java.lang.reflect.Method;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.yunnex.sqlhint.RouterInfo;
import com.yunnex.sqlhint.annotation.MasterOnly;
import com.yunnex.sqlhint.annotation.Scope;
import com.yunnex.sqlhint.dubbo.context.RouterConsts;
import com.yunnex.sqlhint.dubbo.context.RouterContext;
import com.yunnex.sqlhint.masterslave.MasterSlaveHint;


/**
 * SQL 路由切面配置
 * 
 * @author Ternence
 * @date 2016年11月30日
 */
@Aspect
@Component
public class SqlRouterAspect {

    @Autowired
    private MasterSlaveHint msHint;

    @Around("@annotation(com.yunnex.sqlhint.annotation.MasterOnly)) and args(..)")
    public void masterOnlyAspect(ProceedingJoinPoint joinPoint) throws Throwable {

        process(joinPoint, msHint.getRouteMasterHint());

    }

    @Around("@annotation(com.yunnex.sqlhint.annotation.SlaveOnly))  and args(..)")
    public void slaveOnlyAspect(ProceedingJoinPoint joinPoint) throws Throwable {

        process(joinPoint, msHint.getRouteSlaveHint());

    }

    /**
     *
     * @param joinPoint
     * @param sqlHint
     * @throws Throwable
     * @date 2016年11月30日
     * @author Ternence
     */
    private void process(ProceedingJoinPoint joinPoint, String sqlHint) throws Throwable {
        try {


            Signature signature = joinPoint.getSignature();

            MethodSignature methodSignature = (MethodSignature) signature;
            Method method = methodSignature.getMethod();

            RouterInfo info = new RouterInfo(msHint.getRouteMasterHint());
            if (method != null) {
                MasterOnly annotation = method.getAnnotation(MasterOnly.class);
                Scope scope = annotation.scope();
                info.setScope(scope);
            }

            RouterContext.put(RouterConsts.ROUTER_KEY, info);
            joinPoint.proceed();
        } finally {
            RouterContext.cleanup();
        }
    }

}
