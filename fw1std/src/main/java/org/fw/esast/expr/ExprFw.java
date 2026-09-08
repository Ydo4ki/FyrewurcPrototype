package org.fw.esast.expr;

import com.ydo4ki.esast.*;
import com.ydo4ki.fw.internal.lib.stdlib.DIntFw;
import com.ydo4ki.fw.internal.lib.stdlib.StrFw;
import org.fw.base.*;
import org.fw.core.FW;
import org.fw.core.abstrait.Value;

import org.fw.esast.util.FwUtils3;
import com.ydo4ki.fw.internal.lib.ConstraintFw;
import org.fw.std.DeclaredFw;
import org.fw.std.ModuleFw;
import org.fw.std.VitFw;
import org.fw.std.dvec.DVecBuilderFw;
import org.fw.std.dvec.DVecFw;
import org.fw.core.util.FwUtils;
import org.fw.core.vit.Vit;
import org.fw.esast.ExprVitCompilationException;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

import static org.fw.core.FW.symbol;
import static org.fw.core.FW.lambda_native;

public final class ExprFw {
    public static final Val symbolConstructor = FW.lambda_native("stringToSymbol", (arg1) -> {
        if (!arg1.getType().equals(StrFw.str))
            return null;

        String value = arg1._UNPACK_();
        Expr expr = FwUtils3.parse(value).getExpr();
        if (expr instanceof Symbol)
            return Val._NEW_INSTANCE_(SymbolFw.symbol, expr.toString());

        return null;
    });

    public static final Val symbolToString = FW.lambda_native("symbolToString", (arg) -> {
        if (arg.getType() == SymbolFw.symbol) {
            return StrFw.str(((Symbol) unwrap(arg)).getValue());
        }
        return null;
    });

    public static final Type exprList = FW.lambda_native("ExprList", (arg0) -> {
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

                    ExprList list = instance._UNPACK_();
                    int index = i.intValue();
                    if (index >= list.size() || index < 0)
                        return null; // out of range

                    return ExprFw.wrap(list.get(index));
                }).handle(instance1, callArg);
            }
            String symbol1 = ((Symbol) unwrap(callArg)).getValue();
            return ((FwUtils.SHandler) (instance, symbol) -> {
                ExprList list = instance._UNPACK_();
                if (symbol.equals("size")) {
                    return DIntFw.dint(list.size());
                }
                if (symbol.equals("brackets-type")) {
                    BracketsType bt = list.getBracketsType();
                    return Val._NEW_INSTANCE_(ExprFw.bracketsType, bt);
                }
                return null; // unknown property
            }).handle(instance1, symbol1);
        }
        return ((Type.TelephonistType.NativeLambdaCallFunction) (arg) -> {
            if (arg.equalsSymbol("construct")) {
                return FW.lambda_native("ExprList.constructor", (bt) -> {
                    if (!bt.getType().equals(ExprFw.bracketsType))
                        return null;
                    return FW.lambda_native((valuesDvec) -> {
                        if (!valuesDvec.getType().equals(DVecFw.dVec))
                            return null;

                        Val[] values = valuesDvec._UNPACK_();
                        Expr[] actualValues = new Expr[values.length];
                        for (int i = 0; i < values.length; i++) {
                            Val value = values[i];
                            if (!isExpr(value))
                                return null;

                            actualValues[i] = (Expr) unwrap(value);
                        }
                        ExprList result = ExprList.of((BracketsType) bt._UNPACK_(), actualValues); // todo: add other bracket types

                        return ExprFw.wrap(result);
                    });
                });

            }
            return null;
        }).call(arg0);
    }).asType(); // bruh
    public static final Val isExpr = ConstraintFw.constraint(
            Vit.val(FW.lambda_native(a -> BoolFw.wrap(isExpr(a)))).call(Vit.var)
    );
    @Deprecated
    public static final Val isExprBugged = ConstraintFw.constraint(
            Vit.val(FW.lambda_native(passingArg
                    -> BoolFw.wrap(!passingArg.getType().equals(SymbolFw.symbol) && !passingArg.getType().equals(exprList))))
    );

    public static final Type bracketsType = FW.lambda_native("BracketsType", (arg) -> {
        if (FwUtils.isTypeApiCall(arg, ExprFw.bracketsType)) {
            Val instance = (Val) CallFw.getVal(arg);
            arg = (Val) CallFw.getArg(arg);
            BracketsType bt = instance._UNPACK_();
            if (arg.getType() == SymbolFw.symbol) switch (((Symbol) unwrap(arg)).getValue()) {
                case "open":
                    return StrFw.str(String.valueOf(bt.open()));
                case "close":
                    return StrFw.str(String.valueOf(bt.close()));
            }
        } else {
            if (arg.getType() == SymbolFw.symbol) switch (((Symbol) unwrap(arg)).getValue()) {
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

    public static final Val roundBrackets = Val._NEW_INSTANCE_(bracketsType, BracketsTypes.round);
    public static final Val squareBrackets = Val._NEW_INSTANCE_(bracketsType, BracketsTypes.square);
    public static final Val bracesBrackets = Val._NEW_INSTANCE_(bracketsType, BracketsTypes.braces);


    public static Val wrap(Expr expr) {
        if (expr instanceof ExprList) {
            return Val._NEW_INSTANCE_(exprList, expr);
        }
        if (expr instanceof Symbol) {
            return Val._NEW_INSTANCE_(SymbolFw.symbol, expr.toString());
        }
        throw new IllegalStateException("This should never happen: " + expr);
    }

    public static boolean isExpr(Val val) {
        return val.getType().equals(exprList) || val.getType().equals(SymbolFw.symbol);
    }

    public static final CompEnv directivesCenv = CompEnv.of(FW.lambda_native((arg) -> {
        if (arg.getType().equals(SyntaxResolveFw.syntaxResolve)) {
            Val exprVal = (Val) (Val) arg.call(FW.symbol("expr"));
            Value compEnv = (Val) arg.call(FW.symbol("comp-env"));
            Expr expr = (Expr) unwrap(exprVal);
            if (expr instanceof ExprList && ((ExprList) expr).getBracketsType().equals(BracketsTypes.round) && ((ExprList) expr).size() > 0) {
                Expr f = ((ExprList) expr).get(0);
                int isize = ((ExprList) expr).size();
                if (f instanceof Symbol) switch (((Symbol) f).getValue()) {
                    case "symbol": {
                        if (isize != 2) return null;

                        Val val = ((Val) (Val) exprVal.call(DIntFw.dint(1)));
                        Val retVit = (Val) compEnv.call(CompEnv.syntaxResolve((Expr) unwrap(val), CompEnv.of(compEnv)));
                        if (!VitFw.isVit(retVit.getType()))
                            return retVit; // compile error idk

                        return VitFw.wrap(Vit.val(ExprFw.symbolConstructor).call((Vit) retVit._UNPACK_()));
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
                            Val retVit = (Val) compEnv.call(CompEnv.syntaxResolve(eee, CompEnv.of(compEnv)));
                            if (!VitFw.isVit(retVit.getType()))
                                return retVit; // compile error idk

                            try {
                                ctor = ctor.call(VitFw.unwrap(retVit, eee));
                            } catch (ExprVitCompilationException e) {
                                throw new RuntimeException(e);
                            }
                        }

                        ctor = Vit.val(DVecBuilderFw.dvecbf).call(ctor);
                        return VitFw.wrap(Vit.val(ExprFw.exprList.asVal()).call(symbol("construct")).call(Val._NEW_INSTANCE_(bracketsType, bt)).call(ctor));
                    }
                }
            }
        }
        return null;
    }));

    public static final CompEnv esast2exprCenv = CompEnv.of(FW.lambda_native((arg) -> {
        if (arg.getType().equals(SyntaxResolveFw.toExprResolve)) {
            Val val = (Val) (Val) arg.get("passing");
            Value compEnv = (Val) arg.get("chain");

            Type type = val.getType();
            if (type.equals(exprList)) {
                List<Expr> content = new ArrayList<>();
//                content.add(type.asVal().toExpr(CompEnv.of(compEnv)));
                content.add(Symbol.of("expr-list"));
                ExprList el = val._UNPACK_();
                content.add(ExprList.of(el.getBracketsType()));
                for (Expr expr : el) {
                    Value value = wrap(expr);
                    content.add(CompEnv.of(compEnv).toExpr(value));
                }
                return wrap(ExprList.of(BracketsTypes.round, content));
            } else if (type.equals(SymbolFw.symbol)) {
                String str = val._UNPACK_().toString();
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
                    ExprFw.directivesCenv.asValue(),
                    esast2exprCenv.asValue()
            )
    );

    public static Expr unwrap(Val v) {
        if (v.getType() == exprList) return (ExprList) v._UNPACK_();
        if (v.getType() == SymbolFw.symbol) return Symbol.of((String) v._UNPACK_());
        return null;
    }
}
