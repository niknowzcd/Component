package com.architect.component;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.architect.component.annotation.ARouter;
import com.architect.component.annotation.RouterBean;
import com.architect.component.api.core.ARouterLoadGroup;
import com.architect.component.api.core.ARouterLoadPath;
import com.architect.component.apt.ARouter$$Group$$order;
import com.architect.component.apt.ARouter$$Path$$order;

import java.util.Map;

@ARouter(path = "/app/MainActivity")
public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
    }

    public void jumpOrder(View view) {

        ARouter$$Path$$order path = new ARouter$$Path$$order();
        Map<String, RouterBean> map = path.loadPath();
        RouterBean bean = map.get("/order/MainActivity");
        if (bean != null) {
            startActivity(new Intent(this, bean.getClazz()));
        }


//        ARouterLoadGroup group = new ARouter$$Group$$order();
//        Map<String, Class<? extends ARouterLoadPath>> classMap = group.loadGroup();
//        Class<? extends ARouterLoadPath> order = classMap.get("order");
//
//        try {
//            ARouter$$Path$$order pathOrder = (ARouter$$Path$$order) order.newInstance();
//            Map<String, RouterBean> map = pathOrder.loadPath();
//            RouterBean bean = map.get("/order/MainActivity");
//
//            if (bean != null) {
//                startActivity(new Intent(this, bean.getClazz()));
//            }
//
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
    }
}