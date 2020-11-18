package com.architect.component.designPatterns.chain;

public class Test {

    /**
     * 责任链设计模式，参考okHttp的 Interceptor
     */

    public static void main(String[] args) {
        ChainManager chainManager = new ChainManager();
        chainManager.addChain(new Task1());
        chainManager.addChain(new Task2());
        chainManager.addChain(new Task3());

        chainManager.doRunAction("ok", chainManager);
    }
}
