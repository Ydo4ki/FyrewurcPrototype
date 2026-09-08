package org.fw.core.state.operation;

import org.fw.core.abstrait.Value;
import org.fw.core.state.obj.State;
import org.fw.core.vit.Vit;

import java.util.Objects;

public final class VitOperation extends Operation {
    private final Vit vit;
    private final Value rtEnv; // ok storing this just seems easier

    VitOperation(Vit vit, Value rtEnv) {
        this.vit = Objects.requireNonNull(vit);
        this.rtEnv = Objects.requireNonNull(rtEnv);
    }

    @Override
    public Value apply(State state) {
        return vit.eval(rtEnv, state);
    }

    public Vit vit() {
        return vit;
    }
}
