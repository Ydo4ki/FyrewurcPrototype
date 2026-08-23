package org.fw.lib.memlib.ints;

import org.fw.core.FW;
import org.fw.core.ast.BracketsTypes;
import org.fw.core.ast.ExprList;
import org.fw.core.ast.Symbol;
import org.fw.core.base.*;
import org.fw.core.util.bits.Bits;
import org.fw.lib.elib.*;
import org.fw.lib.elib.constraint.ConstraintFw;
import org.fw.lib.elib.expr.ExprFw;
import org.fw.lib.elib.expr.ToExprFn;
import org.fw.lib.memlib.MemUtils;
import org.fw.lib.memlib.ReifiedTypeFw;
import org.fw.lib.memlib.words.BitFw;
import org.fw.core.util.FwUtils;

import java.math.BigInteger;
import java.util.Arrays;
import java.util.BitSet;

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

//    public static final Type int_t_payload = StructFw.struct(
//            DeclarationFw.declaration(symbol("bitwidth"), ConstraintFw.toConstraint(DIntFw.dint)),
//            DeclarationFw.declaration(symbol("signedness"), ConstraintFw.toConstraint(signedness)),
//            DeclarationFw.declaration(symbol("overflow"), ConstraintFw.toConstraint(overflow))
//    );


    public static final Type int_t = FW.telephonist(arg -> {
        if (FwUtils.isTypeApiCall(arg, IntTypeFw.int_t)) {
            Val instance = Call.getVal(arg);
            arg = Call.getArg(arg);
            IntType raw_payload = instance._unpack();

            Type Int = instance.asType();
            if (FwUtils.isTypeApiCall(arg, Int)) {
                Val int_instance = Call.getVal(arg);
                arg = Call.getArg(arg);

                return null;
            } else if (arg.type() == SymbolFw.symbol) {
                String s = arg._unpack().toString();
                switch (s) {
                    case "Payload": {
                        long bitwidth = raw_payload.getBitWidth();
                        return TypePayloadInfo.wrap(ReifiedTypeFw.reifiedType(BitFw.bit, bitwidth));
                    }
                    case "bitwidth": return DIntFw.dint(raw_payload.getBitWidth());
                    case "signedness": return raw_payload.getSign();
                    case "overflow": return raw_payload.getOverflow();
                    case "construct": return FW.telephonist(arg1 -> {
                        if (arg1.type() != DIntFw.dint)
                            return null;

                        BigInteger value = DIntFw.unwrap0(arg1);
                        int bitwidth = Int.get("bitwidth")._unpack(Number.class).intValue();

                        Bits bits = Bits.of(BitSet.valueOf(MemUtils.reverseBytes(toBytes(value, bitwidth))), bitwidth);

                        return MemUtils.wrap(Int, bits);
                    });
                }
            }
            return null;
        }
        if (arg.type() == SymbolFw.symbol) {
            String s = arg._unpack().toString();
            switch (s) {
                case "construct": {
                    return FW.telephonist(bw -> {
                        if (bw.type() != DIntFw.dint) return null;
                        return FW.telephonist(sign -> {
                            if (sign.type() != signedness) return null;
                            return FW.telephonist(over -> {
                                if (over.type() != overflow) return null;
                                return Val.of(IntTypeFw.int_t, new IntType(DIntFw.unwrap0(bw).intValueExact(), sign, over));
                            });
                        });
                    });
//                    return wrapBuilder(int_t_payload.asVal().call(symbol("builder")));
                }
            }
        }
        return null;
    }).asType();

    static byte[] toBytes(BigInteger value, int bitwidth) {
        int byteWidth = (bitwidth + 7) / 8;

        BigInteger mask = BigInteger.ONE.shiftLeft(bitwidth).subtract(BigInteger.ONE);
        BigInteger normalized = value.and(mask);

        byte[] result = normalized.toByteArray();

        if (result.length > byteWidth) {
            result = Arrays.copyOfRange(
                    result,
                    result.length - byteWidth,
                    result.length
            );
        }

        if (result.length < byteWidth) {
            byte[] padded = new byte[byteWidth];
            System.arraycopy(
                    result, 0,
                    padded, byteWidth - result.length,
                    result.length
            );
            result = padded;
        }

        return result;
    }

//    private static Val wrapBuilder(Val val) {
//        return FW.telephonist(arg -> {
//            Val ret = val.call(arg);
//            if (ret.type() == int_t_payload) return Val.of(int_t, ret._unpack());
//            return wrapBuilder(ret);
//        });
//    }

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
        if (arg.type().asVal().type() == int_t) {
            return ExprFw.wrap(Symbol.of(MemUtils.toBits(arg).toString()));
//            Type Int = arg.type();
//            byte[] bytes = MemUtils.toBits(arg).toByteArray();
//            int bitwidth = Int.get("bitwidth")._unpack(Number.class).intValue();
//
//            BigInteger value = new BigInteger(bytes);
//
//            BigInteger mask = BigInteger.ONE.shiftLeft(bitwidth).subtract(BigInteger.ONE);
//            value = value.and(mask);
//
//            if (value.testBit(bitwidth - 1)) {
//                value = value.subtract(BigInteger.ONE.shiftLeft(bitwidth));
//            }
//
//            return ExprFw.wrap(Symbol.of(value.toString()));
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

