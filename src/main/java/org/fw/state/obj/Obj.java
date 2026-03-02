package org.fw.state.obj;

import org.fw.base.Context;
import org.fw.base.Val;
import org.fw.lib.state.StateHoleFw;

import java.util.Objects;

public interface Obj {

    Val read(Context context);

    void write(Context context, Val x);

    Scope owner();

    void move(Scope newScope);

    final class ValObj extends AbstractObj {
        private Val value;

        public ValObj(Val value, Scope owner) {
            super(owner);
            this.value = value;
        }

        @Override
        public Val read(Context context) {
            return value;
        }

        @Override
        public void write(Context context, Val x) {
            value = x;
        }
    }
}

/*

(ObjImpl
 (struct [
  (= x (mut DInt))
  (= y (mut DInt))
 ])
 (fn [(= x DInt) (= y DInt)] -> (payloadT (mutable x) (mutable y)))
 (write-handler payload x (do
    (set! payload.x x.x)
    (set! payload.y x.y)
 ))
 (read-handler payload ((struct [
    (= x DInt)
    (= y DInt)
 ]) payload.x payload.y))
)






*/