package com.ydo4ki.fw.internal.lib.memlib.ints;

import org.fw.core.FW;
import org.fw.core.ast.BracketsTypes;
import org.fw.core.ast.ExprList;
import org.fw.core.ast.Symbol;
import org.fw.core.base.*;
import org.fw.core.util.bits.Bits;
import org.fw.lib.stdlib.*;
import org.fw.lib.stdlib.expr.ExprFw;
import org.fw.lib.stdlib.expr.ToExprFn;
import com.ydo4ki.fw.internal.lib.memlib.MemUtils;
import com.ydo4ki.fw.internal.lib.memlib.ReifiedTypeFw;
import com.ydo4ki.fw.internal.lib.memlib.words.BitFw;
import org.fw.core.util.FwUtils;

import java.math.BigInteger;
import java.util.Arrays;
import java.util.BitSet;
import java.util.function.BinaryOperator;
import java.util.function.UnaryOperator;

import static org.fw.core.FW.symbol;

public final class IntTypeFw {

    public static final Type int_t = FW.telephonist(arg -> {
        if (FwUtils.isTypeApiCall(arg, IntTypeFw.int_t)) {
            Val instance = CallFw.getVal(arg);
            arg = CallFw.getArg(arg);
            IntType raw_payload = instance._unpack();

            Type Int = instance.asType();
            if (FwUtils.isTypeApiCall(arg, Int)) {
                Val int_instance = CallFw.getVal(arg);
                arg = CallFw.getArg(arg);

                Bits bits = MemUtils.toBits(int_instance);

                if (arg.type() == SymbolFw.symbol) {
                    String s = arg._unpack().toString();
                    switch (s) {
                        case "neg": return raw_payload.isSigned() ? uop(int_instance, instance.asType(), raw_payload.neg) : null;
                        case "+": return bop(int_instance, instance.asType(), raw_payload.add);
                        case "-": return bop(int_instance, instance.asType(), raw_payload.sub);
                        case "*": return bop(int_instance, instance.asType(), raw_payload.mul);
                        case "/": return bop(int_instance, instance.asType(), raw_payload.div);
                        case "%": return bop(int_instance, instance.asType(), raw_payload.mod);
                    }
                }

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
                            if (sign.type() != Signedness.signedness) return null;
                            return FW.telephonist(over -> {
                                if (over.type() != Overflow.overflow) return null;
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


    private static Val uop(Val instance, Type Int, UnaryOperator<Number> operator) {
        Number value = MemUtils.toBitsAsNumber(instance);
        assert value != null;
        Number ret = operator.apply(value);
        return MemUtils.wrap(Int, ret);
    }
    private static Val bop(Val instance, Type Int, BinaryOperator<Number> operator) {
        Number value = MemUtils.toBitsAsNumber(instance);
        assert value != null;
        return FW.telephonist((arg1) -> {
            if (arg1.type().equals(Int)) {
                Number v2 = MemUtils.toBitsAsNumber(instance);
                Number ret = operator.apply(value, v2);
                return MemUtils.wrap(Int, ret);
            }
            return null;
        });
    }

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
//            return ExprFw.wrap(Symbol.of(MemUtils.toBits(arg).toString()));
            Type Int = arg.type();
            byte[] bytes = MemUtils.toBits(arg).toByteArray();
            int bitwidth = Int.get("bitwidth")._unpack(Number.class).intValue();

            BigInteger value = new BigInteger(bytes);

            BigInteger mask = BigInteger.ONE.shiftLeft(bitwidth).subtract(BigInteger.ONE);
            value = value.and(mask);

            if (value.testBit(bitwidth - 1)) {
                value = value.subtract(BigInteger.ONE.shiftLeft(bitwidth));
            }

            return ExprFw.wrap(Symbol.of(value.toString()));
        }
        return null;
    });

    public static final Lib lib = Lib.of(
            ModuleFw.module(
                    DeclaredFw.declared(symbol("IntType"), IntTypeFw.int_t),
                    DeclaredFw.declared(symbol("Signedness"), Signedness.signedness),
                    DeclaredFw.declared(symbol("Endian"), Endian.endian),
                    DeclaredFw.declared(symbol("Overflow"), Overflow.overflow)
            ),
            var -> ChainLinkFw.chain(ToExprFn.exprififier,
                    intToExpr,
                    var.call(symbol("to-expr"))
            )
    );
}

