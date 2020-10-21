package com.architect.component.compiler;

import com.architect.component.annotation.ARouter;
import com.architect.component.annotation.RouterBean;
import com.google.auto.service.AutoService;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;
import com.squareup.javapoet.WildcardTypeName;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Filer;
import javax.annotation.processing.Messager;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedOptions;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;
import javax.tools.Diagnostic;

/**
 * 注意事项
 * 1.被注解的activity不能是kotlin文件，否则执行不到process函数 https://www.jianshu.com/p/d665c2b49483
 * 2.注解相关的类最好都用java文件，kotlin的话应该需要另外一套写法
 */

// AutoService则是固定的写法，加个注解即可
// 通过auto-service中的@AutoService可以自动生成AutoService注解处理器，用来注册
// 用来生成 META-INF/services/javax.annotation.processing.Processor 文件
@AutoService(Processor.class)
// 允许/支持的注解类型，让注解处理器处理
@SupportedAnnotationTypes({Constants.AROUTER_ANNOTATION_TYPES})
// 指定JDK编译版本
@SupportedSourceVersion(SourceVersion.RELEASE_7)
// 注解处理器接收的参数
@SupportedOptions({Constants.MODULE_NAME, Constants.APT_PACKAGE})
public class ARouterProcessor extends AbstractProcessor {

    // 操作Element工具类 (类、函数、属性都是Element)
    private Elements elementUtils;

    // type(类信息)工具类，包含用于操作TypeMirror的工具方法
    private Types typeUtils;

    // Messager用来报告错误，警告和其他提示信息
    private Messager messager;

    // 文件生成器 类/资源，Filter用来创建新的类文件，class文件以及辅助文件
    private Filer filer;

    // 子模块名，如：app/order/personal。需要拼接类名时用到（必传）ARouter$$Group$$order
    private String moduleName;

    // 包名，用于存放APT生成的类文件
    private String packageNameForAPT;

    // 临时map存储，用来存放路由组Group对应的详细Path类对象，生成路由路径类文件时遍历
    // key:组名"app", value:"app"组的路由路径"ARouter$$Path$$app.class"
    private Map<String, List<RouterBean>> tempPathMap = new HashMap<>();

    // 临时map存储，用来存放路由Group信息，生成路由组类文件时遍历
    // key:组名"app", value:类名"ARouter$$Path$$app.class"
    private Map<String, String> tempGroupMap = new HashMap<>();

    @Override
    public synchronized void init(ProcessingEnvironment processingEnvironment) {
        super.init(processingEnvironment);

        elementUtils = processingEnvironment.getElementUtils();
        typeUtils = processingEnvironment.getTypeUtils();
        messager = processingEnvironment.getMessager();
        filer = processingEnvironment.getFiler();

        Map<String, String> options = processingEnvironment.getOptions();
        if (!EmptyUtils.isEmpty(options)) {
            moduleName = options.get(Constants.MODULE_NAME);
            packageNameForAPT = options.get(Constants.APT_PACKAGE);
            messager.printMessage(Diagnostic.Kind.NOTE, "moduleName >>> " + moduleName);
            messager.printMessage(Diagnostic.Kind.NOTE, "packageNameForAPT >>> " + packageNameForAPT);
        }

        // 必传参数判空（乱码问题：添加java控制台输出中文乱码）
        if (EmptyUtils.isEmpty(moduleName) || EmptyUtils.isEmpty(packageNameForAPT)) {
            throw new RuntimeException("注解处理器需要的参数moduleName或者packageName为空，请在对应build.gradle配置参数");
        }
    }

    @Override
    public boolean process(Set<? extends TypeElement> set, RoundEnvironment roundEnvironment) {
        if (!EmptyUtils.isEmpty(set)) {

            Set<? extends Element> elements = roundEnvironment.getElementsAnnotatedWith(ARouter.class);
            messager.printMessage(Diagnostic.Kind.NOTE, "elements >>> " + elements.isEmpty());

            if (!EmptyUtils.isEmpty(elements)) {

                try {
                    parseElements(elements);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        return true;
    }

    // 解析所有被 @ARouter 注解的 类元素集合
    private void parseElements(Set<? extends Element> elements) throws Exception {
        TypeElement activityType = elementUtils.getTypeElement(Constants.ACTIVITY);
        TypeMirror activityMinnor = activityType.asType();

        //Element代表语言元素，比如包，类，方法等，但是Element并没有包含自身的信息，自身信息要通过Mirror来获取，
        //每个Element都指向一个TypeMirror，这个TypeMirror里有自身的信息。通过下面获方法取Element中的Mirror
        for (Element element : elements) {
            TypeMirror elementMirror = element.asType();

            // 获取每个类上的@ARouter注解中的注解值
            ARouter annotation = element.getAnnotation(ARouter.class);

            // 路由详细信息，最终实体封装类
            RouterBean bean = new RouterBean.Builder()
                    .setGroup(annotation.group())
                    .setPath(annotation.path())
                    .setElement(element)
                    .build();

            //类型工具类方法isSubtype，相当于instanceof一样  相当于 MainActivity instanceof Activity
            //用来限定注解只在Activity之上
            if (typeUtils.isSubtype(elementMirror, activityMinnor)) {
                bean.setType(RouterBean.Type.Activity);
            } else {
                // 不匹配抛出异常，这里谨慎使用！考虑维护问题
                throw new RuntimeException("@ARouter注解目前仅限用于Activity类之上");
            }

            valueOfPathMap(bean);
        }

        TypeElement groupLoadType = elementUtils.getTypeElement(Constants.AROUTE_GROUP);
        TypeElement pathLoadType = elementUtils.getTypeElement(Constants.AROUTE_PATH);


        // 第一步：生成路由组Group对应详细Path类文件，如：ARouter$$Path$$app
        createPathFile(pathLoadType);

        // 第二步：生成路由组Group类文件（没有第一步，取不到类文件），如：ARouter$$Group$$app
        createGroupFile(groupLoadType, pathLoadType);
    }


    /**
     * 生成路由组Group文件，如：ARouter$$Group$$app
     *
     * @param groupLoadType ARouterLoadGroup接口信息
     * @param pathLoadType  ARouterLoadPath接口信息
     */
    private void createGroupFile(TypeElement groupLoadType, TypeElement pathLoadType) throws Exception {

        //Map<String, Class<? extends ARouterLoadPath>>
        TypeName methodReturns = ParameterizedTypeName.get(
                ClassName.get(Map.class),
                ClassName.get(String.class),
                ParameterizedTypeName.get(ClassName.get(Class.class),
                        WildcardTypeName.subtypeOf(ClassName.get(pathLoadType)))
        );

        //public Map<String, Class<? extends ARouterLoadPath>> loadGroup()
        MethodSpec.Builder methodBuilder = MethodSpec.methodBuilder(Constants.GROUP_METHOD_NAME)
                .addAnnotation(Override.class)
                .addModifiers(Modifier.PUBLIC)
                .returns(methodReturns);

        //Map<String, Class<? extends ARouterLoadPath>> groupMap = new HashMap<>();
        methodBuilder.addStatement("$T<$T,$T> $N = new $T<>()",
                ClassName.get(Map.class),
                ClassName.get(String.class),
                ParameterizedTypeName.get(ClassName.get(Class.class),
                        WildcardTypeName.subtypeOf(ClassName.get(pathLoadType))),
                Constants.GROUP_PARAMETER_NAME,
                HashMap.class);


        for (Map.Entry<String, String> entry : tempGroupMap.entrySet()) {
            //groupMap.put("order", ARouter$$Path$$order.class);
            methodBuilder.addStatement("$N.put($S,$T.class)",
                    Constants.GROUP_PARAMETER_NAME,
                    entry.getKey(),
                    ClassName.get(packageNameForAPT, entry.getValue()));
        }

        methodBuilder.addStatement("return $N", Constants.GROUP_PARAMETER_NAME);

        // 最终生成的类文件名
        String finalClassName = Constants.GROUP_FILE_NAME + moduleName;

        // 生成类文件：ARouter$$Group$$app
        JavaFile.builder(packageNameForAPT,
                TypeSpec.classBuilder(finalClassName)
                        .addSuperinterface(ClassName.get(groupLoadType))
                        .addModifiers(Modifier.PUBLIC)
                        .addMethod(methodBuilder.build())
                        .build())
                .build()
                .writeTo(filer);
    }

    /**
     * 生成路由组Group对应详细Path，如：ARouter$$Path$$app
     *
     * @param pathLoadType ARouterLoadPath接口信息
     */
    private void createPathFile(TypeElement pathLoadType) throws Exception {
        if (EmptyUtils.isEmpty(tempPathMap)) return;

        //Map<String, RouterBean>
        TypeName methodReturns = ParameterizedTypeName.get(
                ClassName.get(Map.class),
                ClassName.get(String.class),
                ClassName.get(RouterBean.class)
        );

        // 遍历分组，每一个分组创建一个路径类文件，如：ARouter$$Path$$app
        for (Map.Entry<String, List<RouterBean>> entry : tempPathMap.entrySet()) {

            // 方法配置：public Map<String, RouterBean> loadPath() {
            MethodSpec.Builder methodBuilder = MethodSpec.methodBuilder(Constants.PATH_METHOD_NAME)
                    .addAnnotation(Override.class)
                    .addModifiers(Modifier.PUBLIC)
                    .returns(methodReturns);

            // 遍历之前：Map<String, RouterBean> pathMap = new HashMap<>();
            methodBuilder.addStatement("$T<$T,$T> $N = new $T<>()",
                    ClassName.get(Map.class),
                    ClassName.get(String.class),
                    ClassName.get(RouterBean.class),
                    Constants.PATH_PARAMETER_NAME,
                    ClassName.get(HashMap.class));

            // 一个分组，如：ARouter$$Path$$app。有很多详细路径信息，如：/app/MainActivity、/app/OtherActivity
            List<RouterBean> pathList = entry.getValue();
            for (RouterBean bean : pathList) {
                // pathMap.put("/app/MainActivity", RouterBean.create(
                //        RouterBean.Type.ACTIVITY, MainActivity.class, "/app/MainActivity", "app"));
                methodBuilder.addStatement(
                        "$N.put($S,$T.create($T.$L,$T.class,$S,$S))",
                        Constants.PATH_PARAMETER_NAME,
                        bean.getPath(),
                        ClassName.get(RouterBean.class),
                        ClassName.get(RouterBean.Type.class),
                        bean.getType(),
                        ClassName.get((TypeElement) bean.getElement()),
                        bean.getPath(),
                        bean.getGroup()
                );
            }

            // 遍历之后：return pathMap;
            methodBuilder.addStatement("return $N", Constants.PATH_PARAMETER_NAME);

            String finalClassName = Constants.PATH_FILE_NAME + entry.getKey();
            messager.printMessage(Diagnostic.Kind.NOTE, "APT生成路由Path类文件：" +
                    packageNameForAPT + "." + finalClassName);


            //生成类文件：ARouter$$Path$$app
            JavaFile.builder(packageNameForAPT,
                    TypeSpec.classBuilder(finalClassName)
                            .addSuperinterface(ClassName.get(pathLoadType))
                            .addModifiers(Modifier.PUBLIC)
                            .addMethod(methodBuilder.build())
                            .build())
                    .build()
                    .writeTo(filer);

//             非常重要一步！！！！！路径文件生成出来了，才能赋值路由组tempGroupMap
            tempGroupMap.put(entry.getKey(), finalClassName);
        }

    }

    /**
     * 赋值临时map存储，用来存放路由组Group对应的详细Path类对象，生成路由路径类文件时遍历
     *
     * @param bean 路由详细信息，最终实体封装类
     */
    private void valueOfPathMap(RouterBean bean) {
        if (checkRouterPath(bean)) {
            messager.printMessage(Diagnostic.Kind.NOTE, "RouterBean >>> " + bean.toString());

            List<RouterBean> routerBeans = tempPathMap.get(bean.getGroup());
            if (EmptyUtils.isEmpty(routerBeans)) {
                routerBeans = new ArrayList<>();
                routerBeans.add(bean);
                tempPathMap.put(bean.getGroup(), routerBeans);
            } else {
                routerBeans.add(bean);
            }
        } else {
            messager.printMessage(Diagnostic.Kind.ERROR, "@ARouter注解未按规范配置，如：/app/MainActivity");
        }
    }

    /**
     * 校验@ARouter注解的值，如果group未填写就从必填项path中截取数据
     *
     * @param bean 路由详细信息，最终实体封装类
     */
    private boolean checkRouterPath(RouterBean bean) {
        String group = bean.getGroup();
        String path = bean.getPath();

        //@ARouter注解中的path值，必须要以 / 开头（模仿阿里Arouter规范）
        if (EmptyUtils.isEmpty(path) || !path.startsWith("/")) {
            messager.printMessage(Diagnostic.Kind.ERROR, "@ARouter注解中的path值，必须要以 / 开头");
            return false;
        }

        // 比如开发者代码为：path = "/MainActivity"，最后一个 / 符号必然在字符串第1位
        if (path.lastIndexOf("/") == 0) {
            messager.printMessage(Diagnostic.Kind.ERROR, "@ARouter注解未按规范配置，如：/app/MainActivity");
            return false;
        }

        // 从第一个 / 到第二个 / 中间截取，如：/app/MainActivity 截取出 app 作为group
        String finalGroup = path.substring(1, path.indexOf("/", 1));

        // @ARouter注解中的group有赋值情况
        if (!EmptyUtils.isEmpty(group) && !group.equals(moduleName)) {
            messager.printMessage(Diagnostic.Kind.ERROR, "@ARouter注解中的group值必须和子模块名一致！");
            return false;
        } else {
            bean.setGroup(finalGroup);
        }

        return true;
    }


}
