package org.fw.constarint;

import org.fw.base.Context;
import org.fw.base.Val;

public sealed interface Term {
    Val evaluate(Val val, Context context);

    record Var() implements Term { // for now we're working with only one variable // lmao who would've thought
        @Override
        public Val evaluate(Val val, Context context) {
            return val;
        }
    }
    record Const(Val val) implements Term {
        @Override
        public Val evaluate(Val val, Context context) {
            return val();
        }
    }
    record Call(Term right, Term left) implements Term {
        @Override
        public Val evaluate(Val val, Context context) {
            Val l = left().evaluate(val, context);
            Val r = right().evaluate(val, context);
            return l.call(r, context);
        }
    }
}
