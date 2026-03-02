package org.fw.adapter;

import org.fw.base.Val;

public abstract class AbstractValAdapted implements ValAdapter {
    private final Val val;

    protected AbstractValAdapted(Val val) {
        this.val = val;
    }

    @Override
    public Val asVal() {
        return val;
    }
}
