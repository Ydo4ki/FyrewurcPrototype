package org.fw.esast.expr;

import com.ydo4ki.fw.internal.lib.stdlib.DIntFw;
import org.fw.base.CallFw;
import org.fw.base.Type;
import org.fw.base.Val;
import org.fw.core.FW;
import org.fw.core.abstrait.Value;
import com.ydo4ki.esast.BracketsTypes;
import com.ydo4ki.esast.Expr;
import com.ydo4ki.esast.ExprList;
import com.ydo4ki.esast.Symbol;

import org.fw.core.state.operation.Operation;
import org.fw.esast.expr.forstd.VitLib;
import org.fw.std.DeclaredFw;
import org.fw.std.ModuleFw;
import org.fw.std.VitFw;
import org.fw.std.state.OperationLibFw;
import org.fw.core.util.FwUtils;
import org.fw.core.vit.Vit;
import org.fw.esast.ExprVitCompilationException;
import org.fw.core.vit.VitUtils;

import static org.fw.core.FW.symbol;
import static org.fw.core.FW.lambda_native;

public final class DoFw {
    public static final Type unaryStoreType = FW.lambda((arg) -> {
        if (FwUtils.isTypeApiCall(arg, DoFw.unaryStoreType)) {
            Value instance = CallFw.getVal(arg);
            arg = CallFw.getArg(arg);
            return Val._NEW_INSTANCE_(DoFw.unaryStoreType, arg);
        }
        return null;
    }).asType();

    public static final Val usLast = FW.lambda_native((arg) -> {
        if (arg.getType().equals(DoFw.unaryStoreType)) {
            return arg._UNPACK_();
        }
        return null;
    });

    public static final CompEnv directivesCenv = CompEnv.of(FW.lambda_native((arg) -> {
        if (arg.getType().equals(SyntaxResolveFw.syntaxResolve)) {
            Val exprVal = (Val) arg.get("expr");
            Val compEnv = (Val) arg.get("comp-env");
            Expr expr = ExprFw.unwrap(exprVal);
            if (expr instanceof ExprList && ((ExprList) expr).getBracketsType().equals(BracketsTypes.round) && ((ExprList) expr).size() > 0) {
                Expr f = ((ExprList) expr).get(0);
                int isize = ((ExprList) expr).size();
                if (f instanceof Symbol) switch (((Symbol) f).getValue()) {
                    case "do": {
                        try {
                            return VitFw.wrap(compileDo(exprVal, 0, isize, compEnv));
                        } catch (ExprVitCompilationException e) {
                            return VitErrorFw.rrror(ExprFw.unwrap(e.getValue()), e.getString());
                        }
                    }
                }
            }
        }
        return null;
    }));

    private static Vit compileDo(Val exprVal, int start, int isize, Value compEnv) throws ExprVitCompilationException {
        Vit execution = Vit.val(Val._NEW_INSTANCE_(DoFw.unaryStoreType, Operation.unit));
        for (int i = start; i < isize - 1; i++) {
            Val val = exprVal.call(DIntFw.dint(i + 1)).asVal();
            Expr line = ExprFw.unwrap(val);
            if (line instanceof ExprList && ((ExprList) line).size() == 3 && ((ExprList) line).get(0).toString().equals(":")) {
                if (i == isize - 2) break;

                Expr nameE = ((ExprList) line).get(1);
                if (!(nameE instanceof Symbol))
                    throw new ExprVitCompilationException(nameE, "Symbol expected");
                String name = ((Symbol) nameE).getValue();
                Expr valueE = ((ExprList) line).get(2);
                Vit valueV = VitUtils.simplify(VitLib.unwrap((Val)compEnv.call(CompEnv.syntaxResolve(valueE, CompEnv.of(compEnv))), valueE));

                Val sname = symbol(name);
                // OK FINE
                Val newRtGetter = FW.lambda((oldRt) -> FW.lambda((varValue) -> {
                    return FW.lambda((arg) -> {
                        if (arg.impliesEquality(sname)) {
                            return varValue;
                        }
                        return oldRt.call(arg);
                    });
                }));
                // this looks cryptic as hell
                // still probably conceptually the best way to do this

                Value newCompEnv = CompEnv.compEnv(compEnv, FW.lambda((arg) -> {
                    if (arg.getTypeValue().impliesEquality(SyntaxResolveFw.syntaxResolve.asVal())) {
                        Value exprVal0 = arg.get("expr");
                        if (exprVal0.impliesEquality(sname)) {
                            return VitFw.wrap(Vit.var.call(sname));
                        }
                    }
                    return null;
                }));

                Vit rest = compileDo(exprVal, i + 1, isize, newCompEnv);

                Vit evalRest = Vit.invoke(Vit.val(OperationLibFw._VitOperation).call(VitFw.wrap(rest)).call(Vit.call(newRtGetter, Vit.var).call(valueV)));

                execution = execution.call(evalRest);
                break;
            } else {
                Value compiled = compEnv.call(CompEnv.syntaxResolve(line, CompEnv.of(compEnv)));
                Vit cv = VitLib.unwrap((Val) compiled, line);
                execution = execution.call(cv);
            }
        }
        return Vit.val(DoFw.usLast).call(execution);
    }

    public static final Lib lib = Lib.of(
            ModuleFw.module(
                    DeclaredFw.declared(symbol("unary-store"), Val._NEW_INSTANCE_(DoFw.unaryStoreType, Operation.unit)),
                    DeclaredFw.declared(symbol("unary-store-last"), DoFw.usLast)
            ),
            DoFw.directivesCenv.asValue()
    );
}
