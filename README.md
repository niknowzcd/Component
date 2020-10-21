> 0.1版本功能如下

##### 在组件化的框架下，通过apt生成的路由实现module下Activity的跳转

```java
RouterManager.getInstance()
  .build("/order/Order_MainActivity")
  .withString("name", "张三")
  .navigation(this);
```


