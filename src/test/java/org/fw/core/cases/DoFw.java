package org.fw.core.cases;

import org.fw.core.FW;
import org.fw.core.ast.BracketsTypes;
import org.fw.core.ast.Expr;
import org.fw.core.ast.ExprList;
import org.fw.core.ast.Symbol;
import org.fw.core.base.Call;
import org.fw.core.base.Context;
import org.fw.core.base.Type;
import org.fw.core.base.Val;
import org.fw.core.lib.DIntFw;
import org.fw.core.lib.DeclaredFw;
import org.fw.core.lib.ModuleFw;
import org.fw.core.lib.VitFw;
import org.fw.core.lib.expr.CompEnv;
import org.fw.core.lib.expr.ExprFw;
import org.fw.core.lib.expr.SyntaxResolveFw;
import org.fw.core.lib.state.OperationFw;
import org.fw.core.util.FwUtils;
import org.fw.core.vit.Vit;
import org.fw.core.vit.VitCompilationException;

import static org.fw.core.FW.symbol;
import static org.fw.core.FW.telephonist;

public class DoFw {
    public static final Type unaryStoreType = FW.telephonist((arg, context) -> {
        if (FwUtils.isTypeApiCall(arg, DoFw.unaryStoreType, context)) {
            Val instance = Call.getVal(arg, context);
            arg = Call.getArg(arg, context);
            return Val.of(DoFw.unaryStoreType, arg);
        }
        return Val.unspecified;
    }).asType();

    public static final Val usLast = FW.telephonist((arg, context) -> {
        if (arg.type().equals(DoFw.unaryStoreType)) {
            return arg._unpack();
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
                    case "do": {
                        try {
                            return VitFw.wrap(compileDo(exprVal, 0, isize, compEnv, context));
                        } catch (VitCompilationException e) {
                            return e.getValue();
                        }
                    }
                }
            }
        }
        return Val.unspecified;
    }));

    private static Vit compileDo(Val exprVal, int start, int isize, Val compEnv, Context context) throws VitCompilationException {
        Vit execution = Vit.val(Val.of(DoFw.unaryStoreType, Val.unspecified));
        for (int i = start; i < isize - 1; i++) {
            Expr line = exprVal.call(DIntFw.dint(i + 1), context)._unpack();
            if (line instanceof ExprList && ((ExprList) line).size() == 3 && ((ExprList) line).get(0).toString().equals(":")) {
                if (i == isize - 2) break;

                Expr nameE = ((ExprList) line).get(1);
                if (!(nameE instanceof Symbol))
                    throw new VitCompilationException(Val.unspecified); // syntax error: symbol expected
                String name = ((Symbol) nameE).getValue();
                Expr valueE = ((ExprList) line).get(2);
                Vit valueV = Vit.simplify(VitFw.unwrap(compEnv.call(CompEnv.syntaxResolve(valueE, CompEnv.of(compEnv)), context)));

                // OK FINE
                Val newRtGetter = FW.telephonist((oldRt, context1) -> FW.telephonist((varValue, context3) -> {
                    return FW.telephonist((arg, context2) -> {
                        if (arg.type().equals(ExprFw.symbol) && arg._unpack(Symbol.class).getValue().equals(name)) {
                            return varValue;
                        }
                        return oldRt.call(arg, context2);
                    });
                }));
                // this looks cryptic as hell
                // still probably conceptually the best way to do this

                Val newCompEnv = CompEnv.compEnv(context, compEnv, FW.telephonist((arg, context1) -> {
                    if (arg.type().equals(SyntaxResolveFw.syntaxResolve)) {
                        Val exprVal0 = arg.call(symbol("expr"), context);
                        Expr expr = exprVal0._unpack();
                        if (expr instanceof Symbol && ((Symbol) expr).getValue().equals(name)) {
                            return VitFw.wrap(Vit.var.call(symbol(name)));
                        }
                    }
                    return Val.unspecified;
                }));

                Vit rest = compileDo(exprVal, i + 1, isize, newCompEnv, context);

                Vit evalRest = Vit.invoke(Vit.val(OperationFw._VitOperation).call(VitFw.wrap(rest)).call(Vit.call(newRtGetter, Vit.var).call(valueV)));

                execution = execution.call(evalRest);
                break;
            } else {
                Val compiled = compEnv.call(CompEnv.syntaxResolve(line, CompEnv.of(compEnv)), context);
                Vit cv = VitFw.unwrap(compiled);
                execution = execution.call(cv);
            }
        }
        return Vit.val(DoFw.usLast).call(execution);
    }

    public static CompEnv exports = CompEnv.of(CompEnv.compEnv(Context.outOf,
            ModuleFw.ModuleCEnvFw.compEnv(ModuleFw.module(
                    DeclaredFw.declared(symbol("unary-store"), Val.of(DoFw.unaryStoreType, Val.unspecified)),
                    DeclaredFw.declared(symbol("unary-store-last"), DoFw.usLast)
            )),
            DoFw.directivesCenv.asVal()
    ));
}
