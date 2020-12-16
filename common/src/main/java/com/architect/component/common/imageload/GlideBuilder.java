package com.architect.component.common.imageload;

public final class GlideBuilder {

    /**
     * 构建Glide对象。
     * 很多参数都在这里设置
     * 参考 源码 GlideBuilder 386行
     */
    public Glide build() {
        RequestManagerRetriver requestManagerRetriver = new RequestManagerRetriver();
        return new Glide(requestManagerRetriver);
    }
}
