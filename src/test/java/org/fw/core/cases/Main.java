package org.fw.core.cases;

import org.fw.core.FW;
import org.fw.core.ast.BracketsTypes;
import org.fw.core.ast.Expr;
import org.fw.core.ast.ExprList;
import org.fw.core.ast.Symbol;
import org.fw.core.ast.lexer.ExprOutput;
import org.fw.core.base.Context;
import org.fw.core.base.TelephonistType;
import org.fw.core.base.Val;
import org.fw.core.lib.*;
import org.fw.core.lib.expr.*;
import org.fw.core.state.obj.Scope;
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
            CompEnv compEnv = CompEnv.of(CompEnv.compEnv(context,
                    VitFw.directivesCenv.asVal(),
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
                            DeclaredFw.declared(symbol("ModuleCompEnv"), ModuleFw.ModuleCEnvFw.moduleCEnvFn.asVal())
                    )),
                    ModuleFw.ModuleCEnvFw.compEnv(ModuleFw.module(
                            DeclaredFw.declared(symbol("test-mod"), ModuleFw.module(
                                    DeclaredFw.declared(symbol("test-value"), DIntFw.dint(94))
                            ))
                    )),
                    DeclaredFw.exports.asVal(),
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


    public static final CompEnv directivesCenv = CompEnv.of(telephonist((arg, context) -> {
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
