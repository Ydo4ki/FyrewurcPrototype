package org.fw.lib.stdlib.expr;

import com.ydo4ki.fw.internal.lib.stdlib.DIntFw;
import com.ydo4ki.fw.internal.lib.stdlib.StrFw;
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
import static org.fw.core.FW.telephonist_native;

public final class ExprFw {
    public static final Val symbolConstructor = FW.telephonist_native("stringToSymbol", (arg1) -> {
        if (!arg1.getType().equals(StrFw.str))
            return null;

        String value = arg1._UNPACK();
        Expr expr = FwUtils.parse(value).getExpr();
        if (expr instanceof Symbol)
            return Val.of(SymbolFw.symbol, expr.toString());

        return null;
    });

    public static final Val symbolToString = FW.telephonist_native("symbolToString", (arg) -> {
        if (arg.getType() == SymbolFw.symbol) {
            return StrFw.str(arg._UNPACK(Symbol.class).getValue());
        }
        return null;
    });

    public static final Type exprList = FW.telephonist_native("ExprList", (arg0) -> {
        // unknown property
        // out of range
        // out of range
        // todo: add other bracket types
        if (FwUtils.isTypeApiCall(arg0, ExprFw.exprList)) {
            Val instance1 = (Val) CallFw.getVal(arg0);
            Val callArg = (Val) CallFw.getArg(arg0);
            if (!callArg.getType().equals(SymbolFw.symbol)) {
                return ((FwUtils.NSHandler) (instance, arg1) -> {
                    BigInteger i = DIntFw.unwrap(arg1);
                    if (i == null) return null;
                    if (i.bitLength() > 32)
                        return null; // out of range

                    ExprList list = instance._UNPACK();
                    int index = i.intValue();
                    if (index >= list.size() || index < 0)
                        return null; // out of range

                    return ExprFw.wrap(list.get(index));
                }).handle(instance1, callArg);
            }
            String symbol1 = callArg._UNPACK(Symbol.class).getValue();
            return ((FwUtils.SHandler) (instance, symbol) -> {
                ExprList list = instance._UNPACK();
                if (symbol.equals("size")) {
                    return DIntFw.dint(list.size());
                }
                if (symbol.equals("brackets-type")) {
                    BracketsType bt = list.getBracketsType();
                    return Val.of(ExprFw.bracketsType, bt);
                }
                return null; // unknown property
            }).handle(instance1, symbol1);
        }
        return ((Type.TelephonistType.NativeCallFunction) (arg) -> {
            if (arg.equalsSymbol("construct")) {
                return FW.telephonist_native("ExprList.constructor", (bt) -> {
                    if (!bt.getType().equals(ExprFw.bracketsType))
                        return null;
                    return FW.telephonist_native((valuesDvec) -> {
                        if (!valuesDvec.getType().equals(DVecFw.dVec))
                            return null;

                        Val[] values = valuesDvec._UNPACK();
                        Expr[] actualValues = new Expr[values.length];
                        for (int i = 0; i < values.length; i++) {
                            Val value = values[i];
                            if (!isExpr(value))
                                return null;

                            actualValues[i] = value._UNPACK(Expr.class);
                        }
                        ExprList result = ExprList.of(bt._UNPACK(BracketsType.class), actualValues); // todo: add other bracket types

                        return ExprFw.wrap(result);
                    });
                });

            }
            return null;
        }).call(arg0);
    }).asType(); // bruh
    public static final Val isExpr = ConstraintFw.constraint(
            Vit.val(FW.telephonist_native(a -> BoolFw.wrap(isExpr(a)))).call(Vit.var)
    );
    @Deprecated
    public static final Val isExprBugged = ConstraintFw.constraint(
            Vit.val(FW.telephonist_native(passingArg
                    -> BoolFw.wrap(!passingArg.getType().equals(SymbolFw.symbol) && !passingArg.getType().equals(exprList))))
    );

    public static final Type bracketsType = FW.telephonist_native("BracketsType", (arg) -> {
        if (FwUtils.isTypeApiCall(arg, ExprFw.bracketsType)) {
            Val instance = (Val) CallFw.getVal(arg);
            arg = (Val) CallFw.getArg(arg);
            BracketsType bt = instance._UNPACK();
            if (arg.getType() == SymbolFw.symbol) switch (arg._UNPACK(Symbol.class).getValue()) {
                case "open":
                    return StrFw.str(String.valueOf(bt.open()));
                case "close":
                    return StrFw.str(String.valueOf(bt.close()));
            }
        } else {
            if (arg.getType() == SymbolFw.symbol) switch (arg._UNPACK(Symbol.class).getValue()) {
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
            return Val.of(SymbolFw.symbol, expr.toString());
        }
        throw new IllegalStateException("This should never happen: " + expr);
    }

    public static boolean isExpr(Val val) {
        return val.getType().equals(exprList) || val.getType().equals(SymbolFw.symbol);
    }

    public static final CompEnv directivesCenv = CompEnv.of(FW.telephonist_native((arg) -> {
        if (arg.getType().equals(SyntaxResolveFw.syntaxResolve)) {
            Val exprVal = arg.call(symbol("expr"));
            Val compEnv = arg.call(symbol("comp-env"));
            Expr expr = exprVal._UNPACK(Expr.class);
            if (expr instanceof ExprList && ((ExprList) expr).getBracketsType().equals(BracketsTypes.round) && ((ExprList) expr).size() > 0) {
                Expr f = ((ExprList) expr).get(0);
                int isize = ((ExprList) expr).size();
                if (f instanceof Symbol) switch (((Symbol) f).getValue()) {
                    case "symbol": {
                        if (isize != 2) return null;

                        Val retVit = compEnv.call(CompEnv.syntaxResolve(exprVal.call(DIntFw.dint(1))._UNPACK(Expr.class), CompEnv.of(compEnv)));
                        if (!VitFw.isVit(retVit.getType()))
                            return retVit; // compile error idk

                        return VitFw.wrap(Vit.val(ExprFw.symbolConstructor).call(retVit._UNPACK(Vit.class)));
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
                            if (!VitFw.isVit(retVit.getType()))
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

    public static final CompEnv esast2exprCenv = CompEnv.of(FW.telephonist_native((arg) -> {
        if (arg.getType().equals(SyntaxResolveFw.toExprResolve)) {
            Val val = arg.get("passing");
            Val compEnv = arg.get("chain");

            Type type = val.getType();
            if (type.equals(exprList)) {
                List<Expr> content = new ArrayList<>();
//                content.add(type.asVal().toExpr(CompEnv.of(compEnv)));
                content.add(Symbol.of("expr-list"));
                ExprList el = val._UNPACK();
                content.add(ExprList.of(el.getBracketsType()));
                for (Expr expr : el) {
                    content.add(wrap(expr).toExpr(CompEnv.of(compEnv)));
                }
                return wrap(ExprList.of(BracketsTypes.round, content));
            } else if (type.equals(SymbolFw.symbol)) {
                String str = val._UNPACK().toString();
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
        if (v.getType() == exprList) return v._UNPACK(ExprList.class);
        if (v.getType() == SymbolFw.symbol) return v._UNPACK(Symbol.class);
        return null;
    }
}
