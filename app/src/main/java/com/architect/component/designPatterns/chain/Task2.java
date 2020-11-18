package com.architect.component.designPatterns.chain;

public class Task2 implements ITaskChain {

    @Override
    public void doRunAction(String doNext, ITaskChain chain) {
        if ("ok".equals(doNext)) {
            System.out.println("当前是任务节点2");
        } else {
            //继续执行下一个任务
            chain.doRunAction(doNext, chain);
        }
    }
}
