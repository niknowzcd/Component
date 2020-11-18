package com.architect.component.designPatterns.chain;

import java.util.ArrayList;
import java.util.List;

public class ChainManager implements ITaskChain {

    private List<ITaskChain> iTaskChains = new ArrayList<>();
    //当前执行哪一个任务节点
    private int index = 0;

    public void addChain(ITaskChain iTaskChain) {
        iTaskChains.add(iTaskChain);
    }

    @Override
    public void doRunAction(String doNext, ITaskChain chain) {
        if (iTaskChains.isEmpty() || index >= iTaskChains.size()) return;

        ITaskChain iTaskChain = iTaskChains.get(index);

        index++;
        iTaskChain.doRunAction(doNext, chain);
    }
}
