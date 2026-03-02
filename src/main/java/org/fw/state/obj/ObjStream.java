package org.fw.state.obj;

import org.fw.base.Context;
import org.fw.base.Val;
import org.fw.lib.DIntFw;
import org.fw.lib.state.StateHoleFw;

import java.util.Objects;

public final class ObjStream extends AbstractObj {

    private Val initialValue;
    private Obj lastGiven;

    public ObjStream(Val initialValue, Scope owner) {
        super(owner);
        this.initialValue = initialValue;
    }

    @Override
    public Val read(Context context) {
        if (lastGiven == null)
            lastGiven = new ValObj(initialValue, context.scope());
        return Val.of(StateHoleFw.statehole, lastGiven);
    }

    @Override
    public void write(Context context, Val x) {
        initialValue = x;
        lastGiven = null;
    }
}

// so we need to somehow
// get the access to some local object
// which would be different for each single scope/context
// and we can't use Var
// because 1. we just gave user full control over it 2. that wouldn't make any sense semantically
// I cannot add another instruction just for this
// it could be something like
// (VitInvoke (ReadOperation <GetLocalObjStream>))
// but the <GetLocalObjStream> must be a local object
// and where do we get that
// ok maybe
// we can avoid adding extra instruction by adding one more primitive operation
// like
// (VitInvoke (GetLocalScopeOperation))
// that would be just an analogue of Var
// but for the objects world
// and it would return local scope
// just like Var returns local RtEnv

