package org.fw.core.lib.expr;

import org.fw.core.FW;
import org.fw.core.adapter.AbstractValAdapted;
import org.fw.core.base.Context;
import org.fw.core.lib.*;
import org.fw.core.util.FwUtils;
import org.fw.core.ast.*;
import org.fw.core.base.Type;
import org.fw.core.base.Val;
import org.fw.core.vit.Vit;
import org.fw.core.vit.VitCompilationException;

import java.math.BigInteger;
import java.util.Map;

import static org.fw.core.FW.symbol;
import static org.fw.core.FW.telephonist;

public final class ExprFw {
    // actually its pretty easy to make it NOT depend on strings directly
    // just make all strings related function external
    // like
    // string-value(symbol) instead of symbol.value
    // во всё точно это гениально
    // они будут просто сопоставлены со строками 1 к 1
    // I should've thought about it like 5 months ago
    public static final Type symbol = telephonist("Symbol", (arg0, context0) -> {
        return FwUtils.handleSymbols(arg0, ExprFw.symbol, context0, (instance, symbol) -> {
            String sym = ((Symbol) instance._unpack()).getValue();
            if (symbol.equals("value")) {
                return Val.of(StrFw.str, sym);
            }
            return Val.unspecified; // unknown property
        }, (arg, context) -> {
            if (arg.type().equals(ExprCallOpFw.exprCallOp)) {
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
                try {
                    return VitFw.wrap(Vit.val(ExprFw.symbol.asVal()).call(symbol("constructor")).call(VitFw.unwrap(retVit)));
                } catch (VitCompilationException e) {
                    throw new RuntimeException(e);
                }

//                return symbol(ret._unpack(String.class));
//                return ExprFw.symbol.asVal().call(symbol("constructor"), context).call(ret, context);
            } else if (arg.equals(symbol("constructor"))) {
                return telephonist("Symbol.constructor", (arg1, context1) -> {
                    if (!arg1.type().equals(StrFw.str))
                        return Val.unspecified;

                    String value = arg1._unpack();
                    Expr expr = FwUtils.parse(value).getExpr();
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
            if (arg.type().equals(ExprCallOpFw.exprCallOp)) {
                Val size = arg.call(symbol("size"), context);
                Val cEnv = arg.call(symbol("comp-env"), context);
                int isize = size._unpack(BigInteger.class).intValue();

                Vit ctor = Vit.val(DVecFw.emptyBuilder);

                for (int i = 0; i < isize; i++) {
                    Val retVit = cEnv.call(CompEnv.syntaxResolve(arg.call(DIntFw.dint(i), context)._unpack(), CompEnv.of(cEnv)), context);
                    if (!VitFw.isVit(retVit.type()))
                        return retVit; // compile error idk

                    try {
                        ctor = ctor.call(VitFw.unwrap(retVit));
                    } catch (VitCompilationException e) {
                        throw new RuntimeException(e);
                    }
                }

                ctor = Vit.val(DVecFw.dvecbf).call(ctor);
                return VitFw.wrap(Vit.val(ExprFw.exprList.asVal()).call(symbol("constructor")).call(ctor));
            } else if (arg.equals(symbol("constructor"))) {
                return FW.telephonist("ExprList.constructor", (arg1, c) -> {
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


    public static Val wrap(Expr expr) {
        if (expr instanceof ExprList) {
            return Val.of(exprList, expr);
        }
        if (expr instanceof Symbol) {
            return Val.of(symbol, expr);
        }
        throw new IllegalStateException("This should never happen: " + expr);
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

    public static final CompEnv directivesCenv = CompEnv.of(telephonist((arg, context) -> {
        if (arg.type().equals(SyntaxResolveFw.syntaxResolve)) {
            Val exprVal = arg.call(symbol("expr"), context);
            Val compEnv = arg.call(symbol("comp-env"), context);
            Expr expr = exprVal._unpack();
            if (expr instanceof ExprList && ((ExprList) expr).getBracketsType().equals(BracketsTypes.round) && ((ExprList) expr).size() > 0) {
                Expr f = ((ExprList) expr).get(0);
                int isize = ((ExprList) expr).size();
                if (f instanceof Symbol) switch (((Symbol) f).getValue()) {
                    case "symbol": {
                        if (isize != 2) return Val.unspecified;

                        Val retVit = compEnv.call(CompEnv.syntaxResolve(exprVal.call(DIntFw.dint(1), context)._unpack(), CompEnv.of(compEnv)), context);
                        if (!VitFw.isVit(retVit.type()))
                            return retVit; // compile error idk

                        return VitFw.wrap(Vit.val(ExprFw.symbol.asVal()).call(symbol("constructor")).call(VitFw.unwrap0(retVit)));
                    }
                    case "expr-list": {
                        Vit ctor = Vit.val(DVecFw.emptyBuilder);

                        for (int i = 1; i < isize + 1; i++) {
                            Val retVit = compEnv.call(CompEnv.syntaxResolve(exprVal.call(DIntFw.dint(i), context)._unpack(), CompEnv.of(compEnv)), context);
                            if (!VitFw.isVit(retVit.type()))
                                return retVit; // compile error idk

                            try {
                                ctor = ctor.call(VitFw.unwrap(retVit));
                            } catch (VitCompilationException e) {
                                throw new RuntimeException(e);
                            }
                        }

                        ctor = Vit.val(DVecFw.dvecbf).call(ctor);
                        return VitFw.wrap(Vit.val(ExprFw.exprList.asVal()).call(symbol("constructor")).call(ctor));
                    }
                }
            }
        }
        return Val.unspecified;
    }));

    public static CompEnv exports = CompEnv.of(CompEnv.compEnv(Context.outOf,
            ModuleFw.ModuleCEnvFw.compEnv(ModuleFw.module(
                    DeclaredFw.declared(symbol("Symbol"), symbol.asVal()),
                    DeclaredFw.declared(symbol("ExprList"), exprList.asVal())
            )),
            ExprFw.directivesCenv.asVal()
    ));
}
