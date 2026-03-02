package org.fw.lib.expr;

import org.fw.FW;
import org.fw.FwUtils;
import org.fw.ast.*;
import org.fw.base.Type;
import org.fw.base.Val;
import org.fw.lib.*;
import org.fw.vit.Vit;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

import static org.fw.FW.symbol;
import static org.fw.FW.telephonist;

public final class ExprFw {
    public static final Type symbol = telephonist("Symbol", (arg0, context0) -> {
        return FwUtils.handleSymbols(arg0, ExprFw.symbol, context0, (instance, symbol) -> {
            String sym = ((Symbol)instance._unpack()).getValue();
            if (symbol.equals("value")) {
                return Val.of(StrFw.str, sym);
            }
            return Val.unspecified; // unknown property
        }, (arg, context) -> {
            if (arg.type().equals(ExprFw.toExpr)) {
                Val instance = BoxFw.unbox(arg);
                if (!instance.type().equals(ExprFw.symbol)) return Val.unspecified; // wrong type
                String str = instance._unpack().toString();
                str = str.replace("\"", "\\\"");
                return ExprFw.wrap(ExprList.of(BracketsTypes.round, Symbol.of("Symbol"), Symbol.of('"' + str + '"')));
            } else if (arg.type().equals(ExprCallOpFw.exprCallOp)) {
                Val size = arg.call(symbol("size"), context);
                Val cEnv = arg.call(symbol("comp-env"), context);
                int isize = size._unpack(BigInteger.class).intValue();
                if (isize != 1) return Val.unspecified;

                Val retVit = cEnv.call(CompEnv.syntaxResolve(arg.call(DIntFw.dint(0), context)._unpack(), CompEnv.of(cEnv)), context);
                if (!VitFw.isVit(retVit.type()))
                    return retVit; // compile error idk
//                Val ret = VitFw.eval.call(retVit, context).call(Val.unspecified, context);
//                if (!ret.type().equals(StrFw.str))
//                    return Val.unspecified;
                return VitFw.wrap(Vit.val(ExprFw.symbol.asVal()).call(symbol("constructor")).call(VitFw.unwrap(retVit)));

//                return symbol(ret._unpack(String.class));
//                return ExprFw.symbol.asVal().call(symbol("constructor"), context).call(ret, context);
            } else if (arg.equals(symbol("constructor"))) {
                return telephonist("Symbol.constructor", (arg1, context1) -> {
                    if (!arg1.type().equals(StrFw.str))
                        return Val.unspecified;

                    String value = arg1._unpack();
                    Expr expr = FwUtils.parse(value);
                    if (expr instanceof Symbol)
                        return ExprFw.wrap(expr);

                    return Val.unspecified;
                });
            }
            return Val.unspecified;
        });
    }).asType();

    public static final Type exprList = telephonist("ExprList", (arg0, context0) -> {
        return FwUtils.handleSymbols(arg0, ExprFw.exprList, context0, (instance, symbol) -> {
            ExprList list = instance._unpack();
            if (symbol.equals("size")) {
                return DIntFw.dint(list.size());
            }
            if (symbol.equals("brackets-type")) {
                BracketsType bt = list.getBracketsType();
                return StrFw.str(bt.toString());
            }
            return Val.unspecified; // unknown property
        }, (instance, arg1) -> {
            BigInteger i = DIntFw.unwrap(arg1);
            if (i == null) return Val.unspecified;
            if (i.bitLength() > 32)
                return Val.unspecified; // out of range

            ExprList list = instance._unpack();
            int index = i.intValue();
            if (index >= list.size() || index < 0)
                return Val.unspecified; // out of range

            return ExprFw.wrap(list.get(index));
        }, (arg, context) -> {
            if (arg.type().equals(ExprFw.toExpr)) {
                Val instance = BoxFw.unbox(arg);
                if (!instance.type().equals(ExprFw.exprList)) return Val.unspecified; // wrong type
                List<Expr> content = new ArrayList<>();
                content.add(Symbol.of("ExprList"));
                ExprList el = instance._unpack();
                for (Expr expr : el) {
                    content.add(wrap(expr).toExpr(context));
                }
                return ExprFw.wrap(ExprList.of(BracketsTypes.round, content));
            } else if (arg.type().equals(ExprCallOpFw.exprCallOp)) {
                Val size = arg.call(symbol("size"), context);
                Val cEnv = arg.call(symbol("comp-env"), context);
                int isize = size._unpack(BigInteger.class).intValue();

                Vit ctor = Vit.val(DVecFw.emptyBuilder);

                for (int i = 0; i < isize; i++) {
                    Val retVit = cEnv.call(CompEnv.syntaxResolve(arg.call(DIntFw.dint(i), context)._unpack(), CompEnv.of(cEnv)), context);
                    if (!VitFw.isVit(retVit.type()))
                        return retVit; // compile error idk

                    ctor = ctor.call(VitFw.unwrap(retVit));
                }

                ctor = Vit.val(DVecFw.dvecbf).call(ctor);
                return VitFw.wrap(Vit.val(ExprFw.exprList.asVal()).call(symbol("constructor")).call(ctor));
            } else if (arg.equals(symbol("constructor"))) {
                return FW.telephonist("ExprList.constructor", (arg1, _) -> {
                    if (!arg1.type().equals(DVecFw.dVec))
                        return Val.unspecified;

                    Val[] values = arg1._unpack();
                    Expr[] actualValues = new Expr[values.length];
                    for (int i = 0; i < values.length; i++) {
                        Val value = values[i];
                        if (!isExpr(value))
                            return Val.unspecified;

                        actualValues[i] = value._unpack();
                    }
                    ExprList result = ExprList.of(BracketsTypes.round, actualValues); // todo: other bracket types

                    return ExprFw.wrap(result);
                });
            }
            return Val.unspecified;
        });
    }).asType(); // bruh

    public static final Type toExpr = Val.of(BoxFw.boxType, symbol("to-expr")).asType();

    public static Val wrap(Expr expr) {
        switch (expr) {
            case ExprList list -> {
                return Val.of(exprList, list);
            }
            case Symbol sym -> {
                return Val.of(symbol, sym);
            }
            default -> throw new IllegalStateException("This should never happen: " + expr);
        }
    }

    public static boolean isExpr(Val val) {
        return val.type().equals(exprList) || val.type().equals(symbol);
    }

    public static final Val expr = telephonist("expr", (arg, context) -> {
        if (arg.type().equals(ExprCallOpFw.exprCallOp)) {
            Val size = arg.call(symbol("size"), context);
            Val cEnv = arg.call(symbol("comp-env"), context);

            int isize = size._unpack(BigInteger.class).intValue();
            if (isize != 1)
                return Val.unspecified;

            return VitFw.wrap(Vit.val(arg.call(DIntFw.dint(0), context)));
        }
        return Val.unspecified;
    });
}
