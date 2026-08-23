package org.fw.lib.memlib.ints;

import org.fw.core.FW;
import org.fw.core.ast.BracketsTypes;
import org.fw.core.ast.ExprList;
import org.fw.core.base.*;
import org.fw.lib.elib.*;
import org.fw.lib.elib.constraint.ConstraintFw;
import org.fw.lib.elib.expr.ExprFw;
import org.fw.lib.elib.expr.ToExprFn;
import org.fw.lib.memlib.ReifiedTypeFw;
import org.fw.lib.memlib.words.BitFw;
import org.fw.core.util.FwUtils;

import static org.fw.core.FW.symbol;

public final class IntTypeFw {

    public static final Type signedness = EnumFw.enumeration("signed", "unsigned");

    public static final class Signedness { private Signedness() {}
        public static final Val signed = signedness.get("signed");
        public static final Val unsigned = signedness.get("unsigned");
    }

    public static final Type endian = EnumFw.enumeration("big", "little");

    public static final class Endian { private Endian() {}
        public static final Val big = endian.get("big");
        public static final Val little = endian.get("little");
    }

    public static final Type overflow = EnumFw.enumeration("wrap", "saturate", "trap");

    public static final class Overflow { private Overflow() {}
        public static final Val wrap = overflow.get("wrap");
        public static final Val saturate = overflow.get("saturate");
        public static final Val trap = overflow.get("trap");
    }

    public static final Type int_t_payload = StructFw.struct(
            DeclarationFw.declaration(symbol("bitwidth"), ConstraintFw.toConstraint(DIntFw.dint)),
            DeclarationFw.declaration(symbol("signedness"), ConstraintFw.toConstraint(signedness)),
            DeclarationFw.declaration(symbol("endian"), ConstraintFw.toConstraint(endian)),
            DeclarationFw.declaration(symbol("overflow"), ConstraintFw.toConstraint(overflow))
    );


    public static final Type int_t = WrapperTypeFw.wrapperType(int_t_payload, FW.telephonist(instance -> FW.telephonist(raw_payload -> FW.telephonist(arg -> {
        Type Int = instance.asType();
        if (FwUtils.isTypeApiCall(arg, Int)) {
            Val int_instance = Call.getVal(arg);
            arg = Call.getArg(arg);

            return null;
        } else if (arg.type() == SymbolFw.symbol) {
            String s = arg._unpack().toString();
            switch (s) {
                case "Payload": {
                    long bitwidth = DIntFw.unwrap0(raw_payload.call(symbol("bitwidth"))).intValueExact();
                    return TypePayloadInfo.wrap(ReifiedTypeFw.reifiedType(BitFw.bit, bitwidth));
                }
                case "bitwidth": return raw_payload.call(symbol("bitwidth"));
                case "signedness": return raw_payload.call(symbol("signedness"));
                case "endian": return raw_payload.call(symbol("endian"));
                case "overflow": return raw_payload.call(symbol("overflow"));
            }
        }
        return null;
    }))), FW.telephonist(arg -> {
        if (arg.type() == SymbolFw.symbol) {
            String s = arg._unpack().toString();
            switch (s) {
                case "construct": {
                    return wrapBuilder(int_t_payload.asVal().call(symbol("builder")));
                }
            }
        }
        return null;
    }));

    private static Val wrapBuilder(Val val) {
        return FW.telephonist(arg -> {
            Val ret = val.call(arg);
            if (ret.type() == int_t_payload) return Val.of(int_t, ret._unpack());
            return wrapBuilder(ret);
        });
    }

    public static final Val intToExpr = FW.telephonist((arg) -> {
        if (arg.type() != ToExprFn.toExprResolve)
            return null;
        Val toExpr = arg.call(symbol("chain"));
        arg = arg.call(symbol("passing"));

        if (arg == int_t.asVal()) {
            return symbol("IntType");
        }
        if (arg.type() == int_t) {
            return ExprFw.wrap(ExprList.of(BracketsTypes.round,
                    arg.type().asVal().toExpr(toExpr),
                    arg.call(symbol("bitwidth")).toExpr(toExpr),
                    arg.call(symbol("signedness")).toExpr(toExpr),
                    arg.call(symbol("endian")).toExpr(toExpr),
                    arg.call(symbol("overflow")).toExpr(toExpr)
            ));
        }
        return null;
    });

    public static final Lib lib = Lib.of(
            ModuleFw.module(
                    DeclaredFw.declared(symbol("IntType"), IntTypeFw.int_t),
                    DeclaredFw.declared(symbol("Signedness"), IntTypeFw.signedness),
                    DeclaredFw.declared(symbol("Endian"), IntTypeFw.endian),
                    DeclaredFw.declared(symbol("Overflow"), IntTypeFw.overflow)
            ),
            var -> ChainLinkFw.chain(ToExprFn.exprififier,
                    intToExpr,
                    var.call(symbol("to-expr"))
            )
    );
}

