package org.fw.core.cases;

import org.fw.core.FW;
import org.fw.core.ast.BracketsTypes;
import org.fw.core.ast.Expr;
import org.fw.core.ast.ExprList;
import org.fw.core.ast.Symbol;
import org.fw.core.ast.lexer.ExprOutput;
import org.fw.core.base.Call;
import org.fw.core.base.Context;
import org.fw.core.base.Type;
import org.fw.core.base.Val;
import org.fw.core.lib.*;
import org.fw.core.lib.constraint.ConstraintFw;
import org.fw.core.lib.expr.*;
import org.fw.core.state.obj.Scope;
import org.fw.core.util.FwUtils;
import org.fw.core.vit.RtEnv;
import org.fw.core.vit.Vit;
import org.fw.core.vit.VitCompilationException;
import org.fw.core.vit.VitVal;

import static org.fw.core.FW.symbol;
import static org.fw.core.FW.telephonist;

public class Main {

    public static final RtEnv rtEnv = RtEnv.of(ModuleFw.module(
            DeclaredFw.declared(symbol("to-expr"), ToExprFn.toExpr)
    ));

    public static void main(String[] args) {
        Iterable<Expr> expressions = ExprOutput.valueOf(FW.class.getResourceAsStream("test.fw"));

        Scope.performAndDie(null, scope -> {
            Context context = new Context(rtEnv, scope);
            CompEnv compEnv;
            compEnv = CompEnv.of(CompEnv.compEnv(context,
                    VitFw.directivesCenv.asVal(),
                    ModuleFw.ModuleCEnvFw.compEnv(ModuleFw.module(
                            DeclaredFw.declared(symbol("VitVal"), VitFw.vitVal.asVal()),
                            DeclaredFw.declared(symbol("VitVar"), VitFw.vitVar.asVal()),
                            DeclaredFw.declared(symbol("VitCall"), VitFw.vitCall.asVal()),
                            DeclaredFw.declared(symbol("VitInvoke"), VitFw.vitInvoke.asVal()),
                            DeclaredFw.declared(symbol("eval-vit"), VitFw.evalVit)
                    )),
                    ExprGetFw.getterCEnv,
                    DIntFw.exports.asVal(),
                    ExprFw.directivesCenv.asVal(),
                    StrFw.ParseStrCEnvFw.parseStrCenv,
                    DVecFw.DVecConstructorCEnvFw.dVecConstructorCenv,
                    ModuleFw.ModuleCEnvFw.compEnv(ModuleFw.module(
                            DeclaredFw.declared(symbol("Telephonist"), Val.ofTelephonist(0))
                    )),
                    ModuleFw.ModuleCEnvFw.compEnv(ModuleFw.module(
                            DeclaredFw.declared(symbol("Module"), ModuleFw.module.asVal()),
                            DeclaredFw.declared(symbol("ModuleCompEnv"), ModuleFw.ModuleCEnvFw.moduleCompEnv.asVal())
                    )),
                    ModuleFw.ModuleCEnvFw.compEnv(ModuleFw.module(
                            DeclaredFw.declared(symbol("Function"), FunctionFw.function.asVal())
                    )),
                    ModuleFw.ModuleCEnvFw.compEnv(ModuleFw.module(
                            DeclaredFw.declared(symbol("test-mod"), ModuleFw.module(
                                    DeclaredFw.declared(symbol("test-value"), DIntFw.dint(94))
                            ))
                    )),
                    DeclaredFw.exports.asVal(),
                    ConstraintFw.exports.asVal(),
                    useDirectivesCenv.asVal(),
                    directivesCenv.asVal()
            ));

            for (Expr expression : expressions) {
                Vit vit;
                try {
                    vit = compEnv.compile(expression, context);
                } catch (VitCompilationException e) {
                    throw new RuntimeException(e);
                }
                Val val = vit.eval(context);
                System.out.println(val.toExpr(context));
            }
            return null;
        });
    }

    public static final Type unaryStoreType = FW.telephonist((arg, context) -> {
        if (FwUtils.isTypeApiCall(arg, Main.unaryStoreType, context)) {
            Val instance = Call.getVal(arg, context);
            arg = Call.getArg(arg, context);
            return Val.of(Main.unaryStoreType, arg);
        }
        return Val.unspecified;
    }).asType();

    public static final Val usLast = FW.telephonist((arg, context) -> {
        if (arg.type().equals(unaryStoreType)) {
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
                    case "compile-vit": {
                        if (isize != 2)
                            return Val.unspecified;

                        return VitFw.wrap(Vit.val(
                                compEnv.call(CompEnv.syntaxResolve(exprVal.call(DIntFw.dint(1), context)._unpack(), CompEnv.of(compEnv)), context)
                        ));
                    }
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
        Vit execution = Vit.val(Val.of(unaryStoreType, Val.unspecified));
        for (int i = start; i < isize - 1; i++) {
            Expr line = exprVal.call(DIntFw.dint(i + 1), context)._unpack();
            if (line instanceof ExprList && ((ExprList) line).size() == 3 && ((ExprList) line).get(0).toString().equals(":")) {
                if (i == isize - 2) break;

                Expr nameE = ((ExprList) line).get(1);
                if (!(nameE instanceof Symbol))
                    throw new VitCompilationException(Val.unspecified); // syntax error: symbol expected
                String name = ((Symbol) nameE).getValue();
                Expr valueE = ((ExprList) line).get(2);
                Vit valueV = VitFw.unwrap(compEnv.call(CompEnv.syntaxResolve(valueE, CompEnv.of(compEnv)), context));

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

                Vit evalRest = Vit.val(VitFw.evalVit).call(VitFw.wrap(rest)).call(Vit.call(newRtGetter, Vit.var).call(valueV));

                execution = execution.call(evalRest);
                break;
            } else {
                Val compiled = compEnv.call(CompEnv.syntaxResolve(line, CompEnv.of(compEnv)), context);
                Vit cv = VitFw.unwrap(compiled);
                execution = execution.call(cv);
            }
        }
        return Vit.val(usLast).call(execution);
    }

    public static final CompEnv useDirectivesCenv = CompEnv.of(telephonist((arg, context) -> {
        if (arg.type().equals(SyntaxResolveFw.syntaxResolve)) {
            Val exprVal = arg.call(symbol("expr"), context);
            Val compEnv = arg.call(symbol("comp-env"), context);
            Expr expr = exprVal._unpack();
            if (expr instanceof ExprList && ((ExprList) expr).getBracketsType().equals(BracketsTypes.round) && ((ExprList) expr).size() > 0) {
                Expr f = ((ExprList) expr).get(0);
                int isize = ((ExprList) expr).size();
                if (f instanceof Symbol) switch (((Symbol) f).getValue()) {
                    case "usem": {
                        if (isize != 3)
                            return Val.unspecified;

                        Val moduleVit = compEnv.call(CompEnv.syntaxResolve(exprVal.call(DIntFw.dint(1), context)._unpack(), CompEnv.of(compEnv)), context);
                        if (!VitFw.isVit(moduleVit.type()))
                            return Val.unspecified; // could not compile module

                        Vit vit = Vit.simplify(moduleVit._unpack(Vit.class), context);
                        if (!(vit instanceof VitVal))
                            return Val.unspecified; // this is meant to be known at compile-time

                        Val newCompEnv = CompEnv.compEnv(context,
                                compEnv,
                                ModuleFw.ModuleCEnvFw.compEnv(((VitVal) vit).val())
                        );

                        Val value = newCompEnv.call(CompEnv.syntaxResolve(exprVal.call(DIntFw.dint(2), context)._unpack(), CompEnv.of(newCompEnv)), context);
                        if (!VitFw.isVit(value.type()))
                            return value; // error idk

                        return value;
                    }
                    case "usec": {
                        if (isize != 3)
                            return Val.unspecified;

                        Val cEnvVit = compEnv.call(CompEnv.syntaxResolve(exprVal.call(DIntFw.dint(1), context)._unpack(), CompEnv.of(compEnv)), context);
                        if (!VitFw.isVit(cEnvVit.type()))
                            return Val.unspecified; // could not compile cenv

                        Vit vit = Vit.simplify(cEnvVit._unpack(Vit.class), context);
                        if (!(vit instanceof VitVal))
                            return Val.unspecified; // this is meant to be known at compile-time

                        Val newCompEnv = CompEnv.compEnv(context,
                                compEnv,
                                ((VitVal) vit).val()
                        );

                        Val value = newCompEnv.call(CompEnv.syntaxResolve(exprVal.call(DIntFw.dint(2), context)._unpack(), CompEnv.of(newCompEnv)), context);
                        if (!VitFw.isVit(value.type()))
                            return value; // error idk

                        return value;
                    }
                }
            }
        }
        return Val.unspecified;
    }));
}
