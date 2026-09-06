package org.fw.lib.stdlib.expr;

import com.ydo4ki.fw.internal.lib.stdlib.DIntFw;
import org.fw.core.FW;
import org.fw.core.abstrait.Value;
import org.fw.core.ast.BracketsTypes;
import org.fw.core.ast.Expr;
import org.fw.core.ast.ExprList;
import org.fw.core.ast.Symbol;
import org.fw.core.base.*;
import org.fw.core.state.operation.Operation;
import org.fw.lib.stdlib.state.OperationFw;
import org.fw.core.util.FwUtils;
import org.fw.core.vit.Vit;
import org.fw.core.vit.VitCompilationException;
import org.fw.core.vit.VitUtils;
import org.fw.lib.stdlib.*;

import static org.fw.core.FW.symbol;
import static org.fw.core.FW.telephonist_native;

public final class DoFw {
    public static final Type unaryStoreType = FW.telephonist((arg) -> {
        if (FwUtils.isTypeApiCall(arg, DoFw.unaryStoreType)) {
            Value instance = CallFw.getVal(arg);
            arg = CallFw.getArg(arg);
            return Val.of(DoFw.unaryStoreType, arg);
        }
        return null;
    }).asType();

    public static final Val usLast = FW.telephonist_native((arg) -> {
        if (arg.getType().equals(DoFw.unaryStoreType)) {
            return arg._UNPACK();
        }
        return null;
    });

    public static final CompEnv directivesCenv = CompEnv.of(FW.telephonist_native((arg) -> {
        if (arg.getType().equals(SyntaxResolveFw.syntaxResolve)) {
            Val exprVal = arg.get("expr");
            Val compEnv = arg.get("comp-env");
            Expr expr = exprVal._UNPACK(Expr.class);
            if (expr instanceof ExprList && ((ExprList) expr).getBracketsType().equals(BracketsTypes.round) && ((ExprList) expr).size() > 0) {
                Expr f = ((ExprList) expr).get(0);
                int isize = ((ExprList) expr).size();
                if (f instanceof Symbol) switch (((Symbol) f).getValue()) {
                    case "do": {
                        try {
                            return VitFw.wrap(compileDo(exprVal, 0, isize, compEnv));
                        } catch (VitCompilationException e) {
                            return VitErrorFw.rrror(ExprFw.unwrap(e.getValue()), e.getString());
                        }
                    }
                }
            }
        }
        return null;
    }));

    private static Vit compileDo(Val exprVal, int start, int isize, Val compEnv) throws VitCompilationException {
        Vit execution = Vit.val(Val.of(DoFw.unaryStoreType, Operation.unit));
        for (int i = start; i < isize - 1; i++) {
            Expr line = exprVal.call(DIntFw.dint(i + 1))._UNPACK(Expr.class);
            if (line instanceof ExprList && ((ExprList) line).size() == 3 && ((ExprList) line).get(0).toString().equals(":")) {
                if (i == isize - 2) break;

                Expr nameE = ((ExprList) line).get(1);
                if (!(nameE instanceof Symbol))
                    throw new VitCompilationException(nameE, "Symbol expected");
                String name = ((Symbol) nameE).getValue();
                Expr valueE = ((ExprList) line).get(2);
                Vit valueV = VitUtils.simplify(VitFw.unwrap(compEnv.call(CompEnv.syntaxResolve(valueE, CompEnv.of(compEnv))), valueE));

                Val sname = symbol(name);
                // OK FINE
                Val newRtGetter = FW.telephonist((oldRt) -> FW.telephonist((varValue) -> {
                    return FW.telephonist((arg) -> {
                        if (arg.impliesEquality(sname)) {
                            return varValue;
                        }
                        return oldRt.call(arg);
                    });
                }));
                // this looks cryptic as hell
                // still probably conceptually the best way to do this

                Val newCompEnv = CompEnv.compEnv(compEnv, FW.telephonist((arg) -> {
                    if (arg.getType0().impliesEquality(SyntaxResolveFw.syntaxResolve.asVal())) {
                        Value exprVal0 = arg.get("expr");
                        if (exprVal0.equalsSymbol(name)) {
                            return VitFw.wrap(Vit.var.call(symbol(name)));
                        }
                    }
                    return null;
                }));

                Vit rest = compileDo(exprVal, i + 1, isize, newCompEnv);

                Vit evalRest = Vit.invoke(Vit.val(OperationFw._VitOperation).call(VitFw.wrap(rest)).call(Vit.call(newRtGetter, Vit.var).call(valueV)));

                execution = execution.call(evalRest);
                break;
            } else {
                Val compiled = compEnv.call(CompEnv.syntaxResolve(line, CompEnv.of(compEnv)));
                Vit cv = VitFw.unwrap(compiled, line);
                execution = execution.call(cv);
            }
        }
        return Vit.val(DoFw.usLast).call(execution);
    }

    public static final Lib lib = Lib.of(
            ModuleFw.module(
                    DeclaredFw.declared(symbol("unary-store"), Val.of(DoFw.unaryStoreType, Operation.unit)),
                    DeclaredFw.declared(symbol("unary-store-last"), DoFw.usLast)
            ),
            DoFw.directivesCenv.asVal()
    );
}
