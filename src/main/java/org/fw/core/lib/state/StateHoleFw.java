package org.fw.core.lib.state;

import org.fw.core.FW;
import org.fw.core.base.Type;
import org.fw.core.base.Val;
import org.fw.core.state.obj.Obj;

// and object handle
public final class StateHoleFw {
    // todo: custom stateholes using Obj.partOf()
    public static final Type statehole = FW.telephonist("Statehole", (arg, context) -> {
        // Ok I'm actually not sure if I'm allowed to create objects just like that
        // if we store Scope inside the context
        // and inside and Object
        // and Scope itself have no idea what's inside
        // then everything's fine
        // But if we'll eventually need the scope to know that by some reason
        // creating an object will become a side effect by itself
        // so we won't be able to do it inside call
        // so we need to create an object and scope at the same time

        // LMAO how is this still empty
        // well that's right

        return Val.unspecified;
    }).asType();

    public static Val wrap(Obj obj) {
        return Val.of(statehole, obj);
    }

    public static Obj unwrap(Val statehole) {
        if (statehole.type().equals(StateHoleFw.statehole))
            return statehole._unpack();
        return null;
    }
}
