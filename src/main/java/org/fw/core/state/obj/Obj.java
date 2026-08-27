package org.fw.core.state.obj;

import org.fw.core.base.Val;
import org.fw.core.state.operation.Operation;

public interface Obj {

    State state();

    Obj partOf();

    default void shmert() {

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