package org.fw.core.lib.expr;

import org.fw.core.FW;
import org.fw.core.base.*;
import org.fw.core.base.context.RtEnv;
import org.fw.core.lib.*;
import org.fw.core.lib.constraint.ConstraintFw;
import org.fw.core.lib.dvec.DVecBuilderFw;
import org.fw.core.lib.dvec.DVecFw;
import org.fw.core.util.FwUtils;
import org.fw.core.ast.*;
import org.fw.core.vit.Vit;
import org.fw.core.vit.VitCompilationException;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

import static org.fw.core.FW.symbol;
import static org.fw.core.FW.telephonist;

public final class ExprFw {
    public static final Val symbolConstructor = telephonist("stringToSymbol", (arg1) -> {
        if (!arg1.type().equals(StrFw.str))
            return null;

        String value = arg1._unpack();
        Expr expr = FwUtils.parse(value).getExpr();
        if (expr instanceof Symbol)
            return Val.of(SymbolFw.symbol, expr);

        return null;
    });

    public static final Val symbolToString = telephonist("symbolToString", (arg) -> {
        if (arg.type() == SymbolFw.symbol) {
            return StrFw.str(arg._unpack(Symbol.class).getValue());
        }
        return null;
    });

    public static final Type exprList = telephonist("ExprList", (arg0) -> {
        return FwUtils.handleSymbols(arg0, ExprFw.exprList, (instance, symbol) -> {
            ExprList list = instance._unpack();
            if (symbol.equals("size")) {
                return DIntFw.dint(list.size());
            }
            if (symbol.equals("brackets-type")) {
                BracketsType bt = list.getBracketsType();
                return StrFw.str(bt.toString());
            }
            return null; // unknown property
        }, (instance, arg1) -> {
            BigInteger i = DIntFw.unwrap(arg1);
            if (i == null) return null;
            if (i.bitLength() > 32)
                return null; // out of range

            ExprList list = instance._unpack();
            int index = i.intValue();
            if (index >= list.size() || index < 0)
                return null; // out of range

            return ExprFw.wrap(list.get(index));
        }, (arg) -> {
            if (arg.equals(symbol("constructor"))) {
                return FW.telephonist("ExprList.constructor", (valuesDvec) -> {
                    if (!valuesDvec.type().equals(DVecFw.dVec))
                        return null;

                    return FW.telephonist((bt) -> {
                        if (!bt.type().equals(ExprFw.bracketsType))
                            return null;

                        Val[] values = valuesDvec._unpack();
                        Expr[] actualValues = new Expr[values.length];
                        for (int i = 0; i < values.length; i++) {
                            Val value = values[i];
                            if (!isExpr(value))
                                return null;

                            actualValues[i] = value._unpack();
                        }
                        ExprList result = ExprList.of(bt._unpack(BracketsType.class), actualValues); // todo: add other bracket types

                        return ExprFw.wrap(result);
                    });
                });
            }
            return null;
        });
    }).asType(); // bruh
    public static final Val isExpr = ConstraintFw.constraint(
            Vit.val(telephonist(passingArg
                    -> BoolFw.wrap(!passingArg.type().equals(SymbolFw.symbol) && !passingArg.type().equals(exprList))))
    );
    public static final Val esastToExpr = telephonist((arg) -> {
        if (arg.type() != ToExprFn.toExprResolve)
            return null;
        Val toExpr = arg.call(symbol("chain"));
        arg = arg.call(symbol("passing"));

        Type type = arg.type();
        if (type.equals(exprList)) {
            List<Expr> content = new ArrayList<>();
            content.add(type.asVal().toExpr(toExpr));
            ExprList el = arg._unpack();
            for (Expr expr : el) {
                content.add(wrap(expr).toExpr(toExpr));
            }
            return wrap(ExprList.of(BracketsTypes.round, content));
        } else if (type.equals(SymbolFw.symbol)) {
            String str = arg._unpack().toString();
            str = str.replace("\"", "\\\"");
            return wrap(ExprList.of(BracketsTypes.round, Symbol.of("symbol"), Symbol.of('"' + str + '"')));
        }
        return null;
    });

    public static final Type bracketsType = telephonist("BracketsType", (arg) -> {
        if (FwUtils.isTypeApiCall(arg, ExprFw.bracketsType)) {
            Val instance = Call.getVal(arg);
            arg = Call.getArg(arg);
            BracketsType bt = instance._unpack();
            if (arg.type() == SymbolFw.symbol) switch (arg._unpack(Symbol.class).getValue()) {
                case "open":
                    return StrFw.str(String.valueOf(bt.open()));
                case "close":
                    return StrFw.str(String.valueOf(bt.close()));
            }
        } else {
            if (arg.type() == SymbolFw.symbol) switch (arg._unpack(Symbol.class).getValue()) {
                case "round":
                    return ExprFw.roundBrackets;
                case "square":
                    return ExprFw.squareBrackets;
                case "braces":
                    return ExprFw.bracesBrackets;
            }
        }
        return null;
    }).asType();

    public static final Val roundBrackets = Val.of(bracketsType, BracketsTypes.round);
    public static final Val squareBrackets = Val.of(bracketsType, BracketsTypes.square);
    public static final Val bracesBrackets = Val.of(bracketsType, BracketsTypes.braces);


    public static Val wrap(Expr expr) {
        if (expr instanceof ExprList) {
            return Val.of(exprList, expr);
        }
        if (expr instanceof Symbol) {
            return Val.of(SymbolFw.symbol, expr);
        }
        throw new IllegalStateException("This should never happen: " + expr);
    }

    public static boolean isExpr(Val val) {
        return val.type().equals(exprList) || val.type().equals(SymbolFw.symbol);
    }

    @Deprecated
    public static final Val expr = telephonist("expr", (arg) -> {
        if (arg.type().equals(ExprCallOpFw.exprCallOp)) {
            Val size = arg.call(symbol("size"));
            Val cEnv = arg.call(symbol("comp-env"));

            int isize = size._unpack(BigInteger.class).intValue();
            if (isize != 1)
                return null;

            return VitFw.wrap(Vit.val(arg.call(DIntFw.dint(0))));
        }
        return null;
    });

    public static final CompEnv directivesCenv = CompEnv.of(telephonist((arg) -> {
        if (arg.type().equals(SyntaxResolveFw.syntaxResolve)) {
            Val exprVal = arg.call(symbol("expr"));
            Val compEnv = arg.call(symbol("comp-env"));
            Expr expr = exprVal._unpack();
            if (expr instanceof ExprList && ((ExprList) expr).getBracketsType().equals(BracketsTypes.round) && ((ExprList) expr).size() > 0) {
                Expr f = ((ExprList) expr).get(0);
                int isize = ((ExprList) expr).size();
                if (f instanceof Symbol) switch (((Symbol) f).getValue()) {
                    case "symbol": {
                        if (isize != 2) return null;

                        Val retVit = compEnv.call(CompEnv.syntaxResolve(exprVal.call(DIntFw.dint(1))._unpack(), CompEnv.of(compEnv)));
                        if (!VitFw.isVit(retVit.type()))
                            return retVit; // compile error idk

                        return VitFw.wrap(Vit.val(ExprFw.symbolConstructor).call(retVit._unpack(Vit.class)));
                    }
                    case "expr-list": {
                        Vit ctor = Vit.val(DVecBuilderFw.emptyBuilder);

                        for (int i = 1; i < isize + 1; i++) {
                            Val retVit = compEnv.call(CompEnv.syntaxResolve(exprVal.call(DIntFw.dint(i))._unpack(), CompEnv.of(compEnv)));
                            if (!VitFw.isVit(retVit.type()))
                                return retVit; // compile error idk

                            try {
                                ctor = ctor.call(VitFw.unwrap(retVit));
                            } catch (VitCompilationException e) {
                                throw new RuntimeException(e);
                            }
                        }

                        ctor = Vit.val(DVecBuilderFw.dvecbf).call(ctor);
                        return VitFw.wrap(Vit.val(ExprFw.exprList.asVal()).call(symbol("constructor")).call(ctor).call(roundBrackets));
                    }
                }
            }
        }
        return null;
    }));

    public static CompEnv exports = CompEnv.of(CompEnv.compEnv(
            ModuleFw.ModuleCEnvFw.compEnv(ModuleFw.module(
                    DeclaredFw.declared(symbol("Symbol"), SymbolFw.symbol.asVal()),
                    DeclaredFw.declared(symbol("ExprList"), exprList.asVal()),
                    DeclaredFw.declared(symbol("BracketsType"), bracketsType.asVal())
            )),
            ExprFw.directivesCenv.asVal()
    ));
}
