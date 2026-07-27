package org.fw.core.state.obj;

import org.fw.core.base.context.Context;
import org.fw.core.base.Val;
import org.fw.core.state.operation.Operation;

public interface Obj {

    State state();

    default void shmert() {

    }

    final class ValObj extends AbstractObj {
        private Val value;

        public ValObj(Val value, State owner) {
            super(owner);
            this.value = value;
        }

        public Val read(Context context) {
            if (state() != context.state())
                return Operation.unit; // c'mon at least use exceptions you're getting too far with this
            return value;
        }

        public void write(Context context, Val x) {
            if (state() != context.state())
                return;
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

what the hell is this




*/