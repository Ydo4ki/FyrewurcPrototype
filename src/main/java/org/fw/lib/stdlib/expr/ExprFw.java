package org.fw.lib.stdlib.expr;

import org.fw.core.FW;
import org.fw.core.base.*;
import org.fw.lib.stdlib.*;
import org.fw.lib.stdlib.ConstraintFw;
import org.fw.lib.stdlib.dvec.DVecBuilderFw;
import org.fw.lib.stdlib.dvec.DVecFw;
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
    public static final Val symbolConstructor = FW.telephonist("stringToSymbol", (arg1) -> {
        if (!arg1.type().equals(StrFw.str))
            return null;

        String value = arg1._unpack();
        Expr expr = FwUtils.parse(value).getExpr();
        if (expr instanceof Symbol)
            return Val.of(SymbolFw.symbol, expr);

        return null;
    });

    public static final Val symbolToString = FW.telephonist("symbolToString", (arg) -> {
        if (arg.type() == SymbolFw.symbol) {
            return StrFw.str(arg._unpack(Symbol.class).getValue());
        }
        return null;
    });

    public static final Type exprList = FW.telephonist("ExprList", (arg0) -> {
        return FwUtils.handleSymbols(arg0, ExprFw.exprList, (instance, symbol) -> {
            ExprList list = instance._unpack();
            if (symbol.equals("size")) {
                return DIntFw.dint(list.size());
            }
            if (symbol.equals("brackets-type")) {
                BracketsType bt = list.getBracketsType();
                return Val.of(ExprFw.bracketsType, bt);
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
            if (arg.equalsSymbol("construct")) {
                return FW.telephonist("ExprList.constructor", (bt) -> {
                    if (!bt.type().equals(ExprFw.bracketsType))
                        return null;
                    return FW.telephonist((valuesDvec) -> {
                        if (!valuesDvec.type().equals(DVecFw.dVec))
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
            Vit.val(FW.telephonist(a -> BoolFw.wrap(isExpr(a)))).call(Vit.var)
    );
    @Deprecated
    public static final Val isExprBugged = ConstraintFw.constraint(
            Vit.val(FW.telephonist(passingArg
                    -> BoolFw.wrap(!passingArg.type().equals(SymbolFw.symbol) && !passingArg.type().equals(exprList))))
    );

    public static final Type bracketsType = FW.telephonist("BracketsType", (arg) -> {
        if (FwUtils.isTypeApiCall(arg, ExprFw.bracketsType)) {
            Val instance = CallFw.getVal(arg);
            arg = CallFw.getArg(arg);
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

    public static final CompEnv directivesCenv = CompEnv.of(FW.telephonist((arg) -> {
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
                        if (isize < 2)
                            return VitErrorFw.rrror(expr, "2 or more elements expected");

                        Expr bracketsSource = ((ExprList) expr).get(1);
                        if (!(bracketsSource instanceof ExprList) || ((ExprList) bracketsSource).size() > 0)
                            return VitErrorFw.rrror(bracketsSource, "Empty ExprList expected");

                        BracketsType bt = ((ExprList) bracketsSource).getBracketsType();

                        for (int i = 2; i < isize; i++) {
                            Expr eee = ((ExprList) expr).get(i);
                            Val retVit = compEnv.call(CompEnv.syntaxResolve(eee, CompEnv.of(compEnv)));
                            if (!VitFw.isVit(retVit.type()))
                                return retVit; // compile error idk

                            try {
                                ctor = ctor.call(VitFw.unwrap(retVit, eee));
                            } catch (VitCompilationException e) {
                                throw new RuntimeException(e);
                            }
                        }

                        ctor = Vit.val(DVecBuilderFw.dvecbf).call(ctor);
                        return VitFw.wrap(Vit.val(ExprFw.exprList.asVal()).call(symbol("construct")).call(Val.of(bracketsType, bt)).call(ctor));
                    }
                }
            }
        }
        return null;
    }));

    public static final CompEnv esast2exprCenv = CompEnv.of(FW.telephonist((arg) -> {
        if (arg.type().equals(SyntaxResolveFw.toExprResolve)) {
            Val val = arg.get("passing");
            Val compEnv = arg.get("chain");

            Type type = val.type();
            if (type.equals(exprList)) {
                List<Expr> content = new ArrayList<>();
//                content.add(type.asVal().toExpr(CompEnv.of(compEnv)));
                content.add(Symbol.of("expr-list"));
                ExprList el = val._unpack();
                content.add(ExprList.of(el.getBracketsType()));
                for (Expr expr : el) {
                    content.add(wrap(expr).toExpr(CompEnv.of(compEnv)));
                }
                return wrap(ExprList.of(BracketsTypes.round, content));
            } else if (type.equals(SymbolFw.symbol)) {
                String str = val._unpack().toString();
                str = str.replace("\"", "\\\"");
                return wrap(ExprList.of(BracketsTypes.round, Symbol.of("symbol"), Symbol.of('"' + str + '"')));
            }
            return null;
        }
        return null;
    }));

    public static final Lib lib = Lib.of(
            ModuleFw.module(
                    DeclaredFw.declared(symbol("Symbol"), SymbolFw.symbol.asVal()),
                    DeclaredFw.declared(symbol("ExprList"), exprList.asVal()),
                    DeclaredFw.declared(symbol("BracketsType"), bracketsType.asVal()),
                    DeclaredFw.declared(symbol("symbolToStr"), symbolToString),
                    DeclaredFw.declared(symbol("strToSymbol"), symbolConstructor)
            ),
            CompEnv.compEnv(
                    ExprFw.directivesCenv.asVal(),
                    esast2exprCenv.asVal()
            )
    );

    public static Expr unwrap(Val v) {
        if (v.type() == exprList) return v._unpack(ExprList.class);
        if (v.type() == SymbolFw.symbol) return v._unpack(Symbol.class);
        return null;
    }
}
