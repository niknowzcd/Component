package com.architect.component.router;

import com.architect.component.annotation.RouterBean;
import com.architect.component.api.core.ARouterLoadPath;
import com.architect.component.order.Order_MainActivity;

import java.util.HashMap;
import java.util.Map;

public class ARouter$$Path$$orderTest implements ARouterLoadPath {
    @Override
    public Map<String, RouterBean> loadPath() {
        Map<String, RouterBean> pathMap = new HashMap<>();
        pathMap.put("/order/Order_MainActivity",
                RouterBean.create(RouterBean.Type.Activity, Order_MainActivity.class,
                        "/order/Order_MainActivity", "order"));

        pathMap.put("/order/Order_MainActivity2",
                RouterBean.create(RouterBean.Type.Activity, Order_MainActivity.class,
                        "/order/Order_MainActivity2", "order"));
        return pathMap;
    }
}
