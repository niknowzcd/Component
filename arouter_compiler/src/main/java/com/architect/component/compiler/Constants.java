package com.architect.component.compiler;

public class Constants {

    // Activity全类名
    public static final String ACTIVITY = "android.app.Activity";
    public static final String TYPE_MIRROR_CLASS = "";

    // 注解处理器中支持的注解类型
    public static final String AROUTER_ANNOTATION_TYPES = "com.architect.component.annotation.ARouter";

    // 每个子模块的模块名
    public static final String MODULE_NAME = "moduleName";
    // 包名，用于存放APT生成的类文件
    public static final String APT_PACKAGE = "packageNameForAPT";

    // 包名前缀封装
    static final String BASE_PACKAGE = "com.architect.component.api.core";
    // 路由组Group加载接口
    public static final String AROUTE_GROUP = BASE_PACKAGE + ".ARouterLoadGroup";
    // 路由组Group对应的详细Path加载接口
    public static final String AROUTE_PATH = BASE_PACKAGE + ".ARouterLoadPath";


    // 路由组Group，参数名
    public static final String GROUP_PARAMETER_NAME = "groupMap";
    // 路由组Group，方法名
    public static final String GROUP_METHOD_NAME = "loadGroup";
    // 路由组Group对应的详细Path，参数名
    public static final String PATH_PARAMETER_NAME = "pathMap";
    // 路由组Group对应的详细Path，方法名
    public static final String PATH_METHOD_NAME = "loadPath";

    // APT生成的路由组Group源文件名
    public static final String GROUP_FILE_NAME = "ARouter$$Group$$";
    // APT生成的路由组Group对应的详细Path源文件名
    public static final String PATH_FILE_NAME = "ARouter$$Path$$";

    /*********************************以下是EventBus相关的常量***************************/

    // 包名，用于存放APT生成的类文件
    public static final String SUBSCRIBE_CLASS_PACKAGE = "eventBusIndex";

    public static final String SUBSCRIBE_ANNOTATION_TYPES = "com.architect.component.annotation.eventbus.Subscribe";

    // 所有的事件订阅方法，生成索引接口
    public static final String SUBSCRIBERINFO_INDEX = "com.architect.component.annotation.eventbus.SubscriberInfoIndex";

    //静态代码块的变量名
    public static final String SUBSCRIBE_STATIC_CODE_NAME = "SUBSCRIBER_INDEX";

    // getSubscriberInfo方法的参数对象名
    public static final String SUBSCRIBE_GETSUBSCRIBERINFO_PARAMETER_NAME = "subscriberClass";

    // 通过订阅者对象（MainActivity.class）获取所有订阅方法的方法名
    public static final String SUBSCRIBE_GETSUBSCRIBERINFO_METHOD_NAME = "getSubscriberInfo";
}
