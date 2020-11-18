package com.architect.component.designPatterns.chain;

interface ITaskChain {

    /**
     * @param doNext 是否需要执行下一个任务节点
     * @param chain  下一个任务节点
     */
    void doRunAction(String doNext, ITaskChain chain);
}
