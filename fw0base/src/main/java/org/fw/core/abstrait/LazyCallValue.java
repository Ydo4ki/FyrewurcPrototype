package org.fw.core.abstrait;

import org.fw.base.Val;
import org.fw.core.NativeExecutionException;
import org.fw.core.state.obj.State;

import java.util.Objects;

public class LazyCallValue implements Value {
    private final Value a, b;
    private Value ret;

    LazyCallValue(Value a, Value b) {
        this.a = a;
        this.b = b;
    }

    void init() {
        if (ret == null) {
//            Value a = this.a;
//            while (a instanceof LazyCallValue) a = ((LazyCallValue) a).a.call(((LazyCallValue) a).b);
            ret = a.call(b);
        }
    }

    @Override
    public Value call(Value value) {
        init();
        return ret.call(value);
//        return new LazyCallValue(this, value);
    }

    @Override
    public Val asVal() throws NativeExecutionException {
        init();
        if (ret instanceof Val) return (Val) ret;
        throw new NativeExecutionException("Not val: " + ret);
    }

    @Override
    public boolean impliesEquality(Val val) {
        if (ret instanceof Val) return ret.equals((Object)val);
        return false;
    }

    @Override
    public Value invoke(State state) {
        return asVal().invoke(state);
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        LazyCallValue cRetValue = (LazyCallValue) o;
        return Objects.equals(a, cRetValue.a) && Objects.equals(b, cRetValue.b) && Objects.equals(ret, cRetValue.ret);
    }

    @Override
    public int hashCode() {
        return Objects.hash(a, b, ret);
    }
}
