package com.architect.component.test;

import com.architect.component.api.core.ARouterLoadGroup;
import com.architect.component.api.core.ARouterLoadPath;

import java.util.HashMap;
import java.util.Map;

public class ARouter$$Group$$orderTest implements ARouterLoadGroup {

    @Override
    public Map<String, Class<? extends ARouterLoadPath>> loadGroup() {
        Map<String, Class<? extends ARouterLoadPath>> groupMap = new HashMap<>();
        groupMap.put("order", ARouter$$Path$$orderTest.class);
        return groupMap;
    }
}
