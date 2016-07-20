package org.zt.dubbo.context;

import java.util.HashMap;
import java.util.Map;

/**
 * 当前上下文路由信息
 * 
 * @author Ternence
 * @date 2016年7月15日
 */
public class RouterContext {

    private static ThreadLocal<Map<String, String>> routerLocal = new ThreadLocal<Map<String, String>>();

    public static void put(String key, String value) {
        Map<String, String> map = routerLocal.get();
        if (map == null) {
            map = new HashMap<String, String>();
            routerLocal.set(map);
        }

        map.put(key, value);
    }

    public static String get(String key) {
        if (routerLocal.get() == null) {
            return null;
        }
        return routerLocal.get().get(key);
    }

    public static void cleanup() {
        if (routerLocal.get() == null) {
            return;
        }
        routerLocal.remove();
    }

    public static boolean containsKey(String routerKey) {
        if (routerLocal.get() == null) {
            return false;
        }
        return routerLocal.get().containsKey(routerKey);
    }


}
