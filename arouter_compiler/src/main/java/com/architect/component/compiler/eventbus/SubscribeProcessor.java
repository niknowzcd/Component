package com.architect.component.compiler.eventbus;


import com.architect.component.annotation.eventbus.SimpleSubscriberInfo;
import com.architect.component.annotation.eventbus.Subscribe;
import com.architect.component.annotation.eventbus.SubscriberInfo;
import com.architect.component.annotation.eventbus.SubscriberMethod;
import com.architect.component.annotation.eventbus.ThreadMode;
import com.architect.component.compiler.Constants;
import com.architect.component.compiler.EmptyUtils;
import com.google.auto.service.AutoService;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.CodeBlock;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterSpec;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;

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
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;
import javax.tools.Diagnostic;

@AutoService(Processor.class)
@SupportedSourceVersion(SourceVersion.RELEASE_7)
@SupportedAnnotationTypes({Constants.SUBSCRIBE_ANNOTATION_TYPES})
@SupportedOptions({Constants.APT_PACKAGE, Constants.SUBSCRIBE_CLASS_PACKAGE})
public class SubscribeProcessor extends AbstractProcessor {

    private Elements elementUtils;
    private Types typeUtils;
    private Messager messager;
    private Filer filer;

    // APT包名
    private String packageName;

    // APT类名
    private String className;

    private final Map<TypeElement, List<ExecutableElement>> methodsByClass = new HashMap<>();

    @Override
    public synchronized void init(ProcessingEnvironment processingEnvironment) {
        super.init(processingEnvironment);

        elementUtils = processingEnvironment.getElementUtils();
        typeUtils = processingEnvironment.getTypeUtils();
        messager = processingEnvironment.getMessager();
        filer = processingEnvironment.getFiler();

        // 通过ProcessingEnvironment去获取对应的参数
        Map<String, String> options = processingEnvironment.getOptions();
        if (!EmptyUtils.isEmpty(options)) {
            packageName = options.get(Constants.APT_PACKAGE);
            className = options.get(Constants.SUBSCRIBE_CLASS_PACKAGE);
            messager.printMessage(Diagnostic.Kind.NOTE,
                    "packageName >>> " + packageName + " / className >>> " + className);
        }

        // 必传参数判空（乱码问题：添加java控制台输出中文乱码）
        if (EmptyUtils.isEmpty(packageName) || EmptyUtils.isEmpty(className)) {
            messager.printMessage(Diagnostic.Kind.ERROR, "注解处理器需要的参数为空，请在对应build.gradle配置参数");
        }
    }

    @Override
    public boolean process(Set<? extends TypeElement> set, RoundEnvironment roundEnvironment) {
        if (!EmptyUtils.isEmpty(set)) {
            Set<? extends Element> elements = roundEnvironment.getElementsAnnotatedWith(Subscribe.class);
            if (!EmptyUtils.isEmpty(elements)) {
                try {
                    parseElements(elements);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

        return false;
    }

    private void parseElements(Set<? extends Element> elements) throws Exception {
        for (Element element : elements) {
            //限制注解只在方法之上
            if (element.getKind() != ElementKind.METHOD) {
                //直接抛出异常，中断程序
                messager.printMessage(Diagnostic.Kind.ERROR, "仅解析@Subscribe注解在方法上元素");
                return;
            }

            //强制转换程方法元素
            ExecutableElement method = (ExecutableElement) element;
            if (checkMethdo(method)) {
                TypeElement classElement = (TypeElement) method.getEnclosingElement();

                //以类名为key，保存订阅方法
                List<ExecutableElement> executableElements = methodsByClass.get(classElement);
                if (executableElements == null) {
                    executableElements = new ArrayList<>();
                    methodsByClass.put(classElement, executableElements);
                }
                executableElements.add(method);
            }
        }

        createFile();
    }

    /**
     * 核心函数
     * 创建类的时候，先把你想要的目标代码写出来，然后从里到外开始创建
     */
    private void createFile() throws Exception {
        // 从静态代码块开始  SUBSCRIBER_INDEX = new HashMap<Class, SubscriberInfo>();
        CodeBlock.Builder codeBlock = CodeBlock.builder();
        codeBlock.addStatement("$N = new $T<$T,$T>()",
                Constants.SUBSCRIBE_STATIC_CODE_NAME,
                HashMap.class,
                Class.class,
                SubscriberInfo.class);

        //遍历所有加了Subscriber注解的类
        for (Map.Entry<TypeElement, List<ExecutableElement>> entry : methodsByClass.entrySet()) {
            CodeBlock.Builder contentBlock = CodeBlock.builder();
            CodeBlock contentCode = null;
            String format;

            //遍历每个类下所有的Subscriber方法
            for (int i = 0; i < entry.getValue().size(); i++) {
                ExecutableElement executableElement = entry.getValue().get(i);
                Subscribe subscribe = executableElement.getAnnotation(Subscribe.class);
                List<? extends VariableElement> parameters = executableElement.getParameters();
                String methodName = executableElement.getSimpleName().toString();

                //参数不能是基本数据类型
                TypeMirror mirror = parameters.get(0).asType();
                if (!mirror.getKind().isPrimitive()) {
                    TypeElement parameterElement = (TypeElement) typeUtils.asElement(mirror);

                    if (i == entry.getValue().size() - 1) {
                        format = "new $T($T.class, $S, $T.class, $T.$L, $L, $L)";
                    } else {
                        format = "new $T($T.class, $S, $T.class, $T.$L, $L, $L),\n";
                    }

                    //new SubscriberMethod(MainActivity.class, "abc", UserInfo.class, ThreadMode.POSTING, 0, false)
                    contentCode = contentBlock.add(format,
                            SubscriberMethod.class,
                            ClassName.get(entry.getKey()),
                            methodName,
                            ClassName.get(parameterElement),
                            ThreadMode.class,
                            subscribe.threadMode(),
                            subscribe.priority(),
                            subscribe.sticky())
                            .build();

                    messager.printMessage(Diagnostic.Kind.NOTE, "contentCode >> " + contentCode);
                }
            }

            if (contentCode != null) {
                // putIndex(new EventBeans(MainActivity.class, new SubscriberMethod[] {)
                codeBlock.beginControlFlow("putIndex(new $T($T.class, new $T[]",
                        SimpleSubscriberInfo.class,
                        ClassName.get(entry.getKey()),
                        SubscriberMethod.class)
                        .add(contentCode)
                        .endControlFlow("))");
            } else {
                //todo 如果传进来的参数是基本类型的参数，就不生成对应的代码体 ？ EventBus也是没有生成对应的方法的，待处理
                messager.printMessage(Diagnostic.Kind.NOTE, "注解处理器双层循环发生错误！");
            }
        }

        // putIndex方法参数：putIndex(SubscriberInfo info)
        ParameterSpec putIndexParameter = ParameterSpec.builder(
                ClassName.get(SubscriberInfo.class),
                "info")
                .build();

        // putIndex方法配置：private static void putIndex(SubscriberMethod info) {
        MethodSpec.Builder putIndexBuidler = MethodSpec
                .methodBuilder("putIndex") // 方法名
                .addModifiers(Modifier.PRIVATE, Modifier.STATIC) // private static修饰符
                .addParameter(putIndexParameter); // 添加方法参数

        // putIndex方法内容：SUBSCRIBER_INDEX.put(info.getSubscriberClass(), info);
        putIndexBuidler.addStatement("$N.put($N.getSubscriberClass(), $N)",
                Constants.SUBSCRIBE_STATIC_CODE_NAME,
                "info",
                "info");


        // getSubscriberInfo方法参数：Class subscriberClass
        ParameterSpec getSubscriberInfoParameter = ParameterSpec.builder(
                ClassName.get(Class.class),
                Constants.SUBSCRIBE_GETSUBSCRIBERINFO_PARAMETER_NAME)
                .build();
        // getSubscriberInfo方法配置：public SubscriberMethod getSubscriberInfo(Class<?> subscriberClass) {
        MethodSpec.Builder getSubscriberInfoBuidler = MethodSpec
                .methodBuilder(Constants.SUBSCRIBE_GETSUBSCRIBERINFO_METHOD_NAME) // 方法名
                .addAnnotation(Override.class) // 重写方法注解
                .addModifiers(Modifier.PUBLIC) // public修饰符
                .addParameter(getSubscriberInfoParameter) // 方法参数
                .returns(SubscriberInfo.class); // 方法返回值

        // getSubscriberInfo方法内容：return SUBSCRIBER_INDEX.get(subscriberClass);
        getSubscriberInfoBuidler.addStatement("return $N.get($N)",
                Constants.SUBSCRIBE_STATIC_CODE_NAME,
                Constants.SUBSCRIBE_GETSUBSCRIBERINFO_PARAMETER_NAME);


        // 全局属性：Map<Class<?>, SubscriberMethod>
        TypeName fieldType = ParameterizedTypeName.get(
                ClassName.get(Map.class),
                ClassName.get(Class.class),
                ClassName.get(SubscriberInfo.class)
        );

        // 通过Element工具类，获取SubscriberInfoIndex类型
        TypeElement subscriberIndexType = elementUtils.getTypeElement(Constants.SUBSCRIBERINFO_INDEX);

        // 构建类
        TypeSpec typeSpec = TypeSpec.classBuilder(className)
                // 实现SubscriberInfoIndex接口
                .addSuperinterface(ClassName.get(subscriberIndexType))
                // 该类的修饰符
                .addModifiers(Modifier.PUBLIC, Modifier.FINAL)
                // 添加静态块（很少用的api）
                .addStaticBlock(codeBlock.build())
                // 全局属性：private static final Map<Class<?>, SubscriberMethod> SUBSCRIBER_INDEX
                .addField(fieldType, Constants.SUBSCRIBE_STATIC_CODE_NAME, Modifier.PRIVATE, Modifier.STATIC, Modifier.FINAL)
                // 第一个方法：加入全局Map集合
                .addMethod(putIndexBuidler.build())
                // 第二个方法：通过订阅者对象（MainActivity.class）获取所有订阅方法
                .addMethod(getSubscriberInfoBuidler.build())
                .build();

        // 生成类文件：EventBusIndex
        JavaFile.builder(packageName, // 包名
                typeSpec) // 类构建完成
                .build() // JavaFile构建完成
                .writeTo(filer); // 文件生成器开始生成类文件

    }

    /**
     * Subscribe方法必须是public修饰的，并且参数只能有一个
     */
    private boolean checkMethdo(ExecutableElement method) {
        if (method.getModifiers().contains(Modifier.STATIC) || !method.getModifiers().contains(Modifier.PUBLIC)) {
            messager.printMessage(Diagnostic.Kind.ERROR, "仅支持public修饰的订阅方法");
            return false;
        }

        List<? extends VariableElement> parameters = method.getParameters();
        if (parameters.size() != 1) {
            messager.printMessage(Diagnostic.Kind.ERROR, "仅支持一个方法");
            return false;
        }

        return true;
    }


}
