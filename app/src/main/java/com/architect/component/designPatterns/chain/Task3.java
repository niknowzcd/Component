package com.architect.component.designPatterns.chain;

public class Task3 implements ITaskChain {

    @Override
    public void doRunAction(String doNext, ITaskChain chain) {
        if ("no".equals(doNext)) {
            System.out.println("当前是任务节点3");
        } else {
            //继续执行下一个任务
            chain.doRunAction(doNext, chain);
        }
    }
}
