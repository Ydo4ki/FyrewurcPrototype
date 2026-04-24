package org.fw.core;

import org.fw.core.ast.BracketsTypes;
import org.fw.core.ast.Expr;
import org.fw.core.ast.lexer.ExprOutput;
import org.fw.core.ast.lexer.TokenOutput;
import org.fw.core.base.Call;
import org.fw.core.base.Context;
import org.fw.core.base.Val;
import org.fw.core.lib.*;
import org.fw.core.lib.comp.*;
import org.fw.core.lib.expr.CompEnv;
import org.fw.core.lib.expr.ToExprFn;
import org.fw.core.lib.state.OperationFw;
import org.fw.core.lib.telephonist.VitiateTelephonistFw;
import org.fw.core.state.operation.Operation;
import org.fw.core.vit.RtEnv;
import org.fw.core.vit.Vit;

import java.io.File;
import java.io.IOException;

import static org.fw.core.FW.symbol;
import static org.fw.core.lib.ValsFw.eq;
import static org.fw.core.vit.Vit.*;

public class Main {

    public static final RtEnv rtEnv = RtEnv.of(ModuleFw.module(
            DeclaredFw.declared(symbol("to-expr"), ToExprFn.toExpr)
    ));
    private static final Context context = new Context(rtEnv, InternalSystemContext.context.scope());

    public static final CompEnv internalCompEnv = CompEnv.of(CompEnv.compEnv(context,
            DotGettersCEnvFw.cenv,
            InternalSymbolMapCEnvFw.valsCenv,
            ParseNumCEnvFw.parseNumCenv,
            InvokeFuncCEnvFw.invokeFuncCenv,
            DVecConstructorCEnvFw.dVecConstructorCenv,
            ParseStrCEnvFw.parseStrCenv,
            CurrentCompEnvCEnvFw.currentCompEnvCenv
    ));

    public static final Val publicModule;
    public static final CompEnv publicCompEnv;
    public static final Val runtime;

    static {
        try {
            publicModule = FwUtils.getValueFromFile(new File("int/base.fw"), internalCompEnv, context);
            publicCompEnv = CompEnv.of(CompEnv.compEnv(context, ModuleCEnvFw.compEnv(publicModule), ParseNumCEnvFw.parseNumCenv, InvokeFuncCEnvFw.invokeFuncCenv, DotGettersCEnvFw.cenv,
                    DVecConstructorCEnvFw.dVecConstructorCenv, ParseStrCEnvFw.parseStrCenv, CurrentCompEnvCEnvFw.currentCompEnvCenv));

            CompEnv env = CompEnv.of(CompEnv.compEnv(context, Main.publicCompEnv.asVal(), InternalSymbolMapCEnvFw.valsCenv));
//            runtime = FwUtils.getValueFromFile(new File("int/runtime.fw"), CompEnv.of(CompEnv.compEnv(context, internalCompEnv.asVal(), ModuleCEnvFw.compEnv(publicModule))), context);
            runtime = FwUtils.getValueFromFile(new File("int/runtime.fw"), env, context);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static void main(String... args) {
        if (args.length == 0) {
            System.out.println("Please specify the source file");
            System.exit(-1);
        }

        String fileName = args[0];
        File file = new File(fileName);
        if (!file.isFile()) {
            System.out.println("Invalid file");
            System.exit(-1);
        }

//        System.out.println(publicModule.toExpr(context));

        CompEnv env = publicCompEnv;

        try {
            Iterable<Expr> expressions = new ExprOutput(new TokenOutput(file, BracketsTypes.bracketsTypes));
            for (Expr expr : expressions) {
                Vit vit = env.compile(expr, context);
                if (vit == null) {
                    System.err.println("Compile error: " + expr);
                    System.exit(-1);
                }
                Val mainF = vit.eval(context);
                Val resultOperation = mainF.call(DVecFw.vec(runtime), context);
                if (!OperationFw.isOperation(resultOperation.type())) {
                    System.out.println("# " + resultOperation.toExpr(context));
                } else {
                    Operation operation = OperationFw.unwrap(resultOperation);
                    assert operation != null;
                    Val result = operation.execute(context);
                    if (!result.equals(Operation.unit))
                        System.out.println(result.toExpr(context));
                }
            }
        } catch (IOException e) {
            System.out.println("Error while reading your file: " + e.getMessage());
            System.exit(-1);
        }
    }

    static void main0() {

        // ok now let's describe some type
        // I don't have any ideas
        // let's just make a box

        Vit cInstance = var(symbol("arg")).call(symbol("val"));
        Vit cArg = var(symbol("arg")).call(symbol("arg"));
        Vit unpacker = var(symbol("private")).call(symbol("unpacker"));
        Vit instancer = var(symbol("private")).call(symbol("instancer"));

        Vit src = val(eq).call(call(ValsFw.typeGet, var(symbol("arg")))).call(Call.call_t.asVal())
                .call(symbol("if"))
                    .call(val(eq).call(
                            val(ValsFw.typeGet).call(cInstance))
                            .call(var(symbol("this")))
                        .call(symbol("if"))
                            .call(val(eq).call(cArg).call(symbol("unbox"))
                                    .call(symbol("if"))
                                        .call(unpacker.call(cInstance))
                                        .call(StrFw.str("I don't know what you want from me"))
                            )
                            .call(StrFw.str("Invalid instance call"))
                    )
                    .call(val(eq).call(var(symbol("arg"))).call(val(symbol("constructor")))
                            .call(symbol("if"))
                            .call(instancer)
                            .call(StrFw.str("Not understood"))
                    );


        ;

        Val vt = VitiateTelephonistFw.vitiate(src, symbol("arg"), context);
        System.out.println(vt.toExpr(context));
        Val instance = vt.call(symbol("constructor"), context).call(StrFw.str("Hello World!"), context);
        Val ret = instance.call(symbol("unbox"), context);
        System.out.println(ret.toExpr(context)); // great
        // time to add if
        // not sure how to do this btw




        // typeof is already used for exprcalls

//        System.out.println(typeGet.call(typeGet, null));
//        System.out.println(typeGet);
//        Val sho = Val.of(typeGet.asType(), "???");
//        System.out.println(sho.call(sho, null)); // lambda calculus moment
//
//        Vit vit = Vit.call(typeGet, sho);
//        System.out.println(VitFw.wrap(vit).call(symbol("func"), null));
//        System.out.println(vit.eval(null, RtEnv.of(Val.unspecified)));
    }
}
