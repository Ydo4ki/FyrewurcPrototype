package org.fw.core;

import org.fw.core.ast.BracketsTypes;
import org.fw.core.ast.Expr;
import org.fw.core.ast.Symbol;
import org.fw.core.ast.lexer.ExprOutput;
import org.fw.core.ast.lexer.TokenOutput;
import org.fw.core.base.Context;
import org.fw.core.base.Val;
import org.fw.core.lib.BoolFw;
import org.fw.core.lib.DIntFw;
import org.fw.core.lib.DeclaredFw;
import org.fw.core.lib.VitFw;
import org.fw.core.lib.comp.InternalSymbolMapCEnvFw;
import org.fw.core.lib.comp.InternalSystemContext;
import org.fw.core.lib.comp.ParseNumCEnvFw;
import org.fw.core.lib.expr.AccumulatorsExprFw;
import org.fw.core.lib.expr.CompEnv;
import org.fw.core.lib.expr.ExprFw;
import org.fw.core.state.obj.Obj;
import org.fw.core.state.obj.ObjStream;
import org.fw.core.lib.state.StateHoleFw;
import org.fw.core.vit.Vit;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.util.*;

import static org.fw.core.FW.symbol;
import static org.fw.core.FW.telephonist;
import static org.fw.core.vit.Vit.*;
import static org.junit.jupiter.api.Assertions.*;

class CompEnvTest {
    private static final Context context = new Context(Main.rtEnv, InternalSystemContext.context.scope());

    //            VitiateTelephonistFw.vitiate(
//            FW.vIf(FW.vEq(val(ValsFw.typeGet).call(argExpr), val(ExprFw.exprList.asVal())),
//                    Vit.val(VitFw.vitCall.asVal()).call(symbol("builder"))
//                            .call(argCEnv.call(
//                                    val(CompEnv.syntaxResolve.asVal()).call(symbol("builder"))
//                                            .call(argExpr.call(DIntFw.dint(0)))
//                                            .call(argCEnv)
//                            ))
//                            .call(Vit.val(VitFw.vitVal.asVal()).call(symbol("constructor"))
//                                    .call(val(ExprCallOpFw.exprCallOp.asVal()).call(symbol("of-expr-list"))
//                                            .call(argExpr).call(argCEnv)
//                                    )
//                            ),
//                    val(Val.unspecified)
//            ), context);


    private static final Vit arg = var(symbol("arg"));
    private static final Vit argExpr = arg.call(symbol("expr"));


    private static final Map<String, Val> testValsMap = new HashMap<>();
    static {
        testValsMap.put("Test", TestFw.test.asVal());
        testValsMap.put("+", Val.of(AccumulatorsExprFw.exprAccumulator, symbol("+")));
        testValsMap.put("test-obj", Val.of(StateHoleFw.statehole, new Obj.ValObj(DIntFw.dint(14), context.scope())));
        testValsMap.put("obj-stream", Val.of(StateHoleFw.statehole, new ObjStream(Val.unspecified, context.scope())));
    }

    public static final Val testValsCenv = InternalSymbolMapCEnvFw.symbolMapVitEnv(val(FW.telephonist("vals", (arg1, _) -> {
        if (!arg1.type().equals(ExprFw.symbol))
            return Val.unspecified;
        String string = arg1._unpack().toString();
        Val ret = testValsMap.get(string);
        if (ret != null)
            return VitFw.wrap(val(ret));
        return Val.unspecified;
    })));

    @Test
    void testNumbers() {
        String source = "5";

        Expr expr = new ExprOutput(new TokenOutput(source, null, BracketsTypes.bracketsTypes)).iterator().next();

        CompEnv env = CompEnv.of(CompEnv.compEnv(ParseNumCEnvFw.parseNumCenv, Val.unspecified, context));
        Vit vit = env.compile(expr, context);
        Val vv = vit == null ? Val.unspecified : VitFw.wrap(vit);
//        System.out.println(vv.toExpr(context));
//        System.out.println(vv);
        Assertions.assertNotNull(vit);
        assertEquals(vit.eval(context), DIntFw.dint(5));
        assertEquals(vv, VitFw.wrap(val(DIntFw.dint(5))));
    }

    @Test
    void testVals() {
        String source = ":";

        Expr expr = new ExprOutput(new TokenOutput(source, null, BracketsTypes.bracketsTypes)).iterator().next();

        CompEnv env = CompEnv.of(CompEnv.compEnv(InternalSymbolMapCEnvFw.valsCenv, Val.unspecified, context));
        Vit vit = env.compile(expr, context);
        Val vv = vit == null ? Val.unspecified : VitFw.wrap(vit);
//        System.out.println(vv.toExpr(context));
//        System.out.println(vv);
        Assertions.assertNotNull(vit);
        assertEquals(DeclaredFw.colon, vit.eval(context));
        assertEquals(vv, VitFw.wrap(val(DeclaredFw.colon)));
    }

    @Test
    void testTelemap() {

    }

//    @Test
//    void callTest() {
////        String source = "(call 5 + 9)";
////        String source = "(Symbol \"+\")";
////        String source = "+";
//        String source = """
//                (call 5 (Symbol "+") 9)
//                (call 5 (call (call Symbol (Symbol "constructor")) "+") 9)
//                "constructor"
//
//                toExpr
//                (call BoxType (Symbol "constructor") (Symbol "to-expr"))
//                (call eq toExpr (call BoxType (Symbol "constructor") (Symbol "to-expr")))
//                """;
//        Iterable<Expr> expressions = new ExprOutput(new TokenOutput(source, null, BracketsTypes.bracketsTypes));
//
//
////        CompEnv env2 = CompEnv.of(CompEnv.compEnv(parseNumCenv, CompEnv.compEnv(valsCenv, CompEnv.compEnv(invokeFuncCenv, parseStrCenv, context), context), context));
//        CompEnv env = CompEnv.of(CompEnv.compEnv(context, ParseNumCEnvFw.parseNumCenv, testValsCenv, InternalSymbolMapCEnvFw.valsCenv, InvokeFuncCEnvFw.invokeFuncCenv, ParseStrCEnvFw.parseStrCenv, CurrentCompEnvCEnvFw.currentCompEnvCenv));
//        System.out.println(env.asVal().toExpr(context));
////        System.out.println(env.asVal().toExpr(context));
////        System.out.println(env2.asVal().toExpr(context));
//        for (Expr expr : expressions) {
//            System.out.println("=========================================");
//            Vit vit = env.compile(expr, context);
//            Val vv = vit == null ? Val.unspecified : VitFw.wrap(vit);
//            System.out.println(vv.toExpr(context));
//            vit = Vit.simplify(vit, context);
//            vv = vit == null ? Val.unspecified : VitFw.wrap(vit);
//            System.out.println(vv.toExpr(context));
//            System.out.println(vv);
//        }
//    }

    @Test
    void callTests() throws IOException {

        Map<String, Val> defineds = new HashMap<>();

        final Val defined = InternalSymbolMapCEnvFw.symbolMapVitEnv(val(FW.telephonist("vals", (arg1, _) -> {
            if (!arg1.type().equals(ExprFw.symbol))
                return Val.unspecified;
            String string = arg1._unpack().toString();
            Val ret = defineds.get(string);
            if (ret != null)
                return VitFw.wrap(val(ret));
            return Val.unspecified;
        })));


//        CompEnv env = CompEnv.of(CompEnv.compEnv(context, ParseNumCEnvFw.parseNumCenv, testValsCenv, defined, InternalSymbolMapCEnvFw.valsCenv,
//                DVecConstructorCEnvFw.dVecConstructorCenv, InvokeFuncCEnvFw.invokeFuncCenv, ParseStrCEnvFw.parseStrCenv, CurrentCompEnvCEnvFw.currentCompEnvCenv));


        CompEnv env = CompEnv.of(CompEnv.compEnv(context, Main.publicCompEnv.asVal(), testValsCenv, defined, InternalSymbolMapCEnvFw.valsCenv));

        File testFolder = new File("tests");
        for (File file : Objects.requireNonNull(testFolder.listFiles())) {
            if (!file.getName().endsWith(".fw") || file.getName().startsWith("temp"))
                continue;

            System.out.println("========================================= " + file.getName() + " =========================================");
            Iterable<Expr> expressions = new ExprOutput(new TokenOutput(file, BracketsTypes.bracketsTypes));

            List<TestFw.TestRecord> tests = new ArrayList<>();
            for (Expr expr : expressions) {
                Val vitVal = env.compileV(expr, context);
                Vit vit = VitFw.unwrap(vitVal);
                if (vit == null) {
                    fail("# " + vitVal.toExpr(context) + " from " + expr);
                    // ok its impossible to work under such noice
                    // ill be back later
                    continue;
                }
                Val result = vit.eval(context);
                if (result.type().equals(TestFw.test)) {
                    tests.add(result._unpack());
//                System.out.println(result.toExpr(context));
                } else if (result.type().equals(DeclaredFw.declared)) {
                    Val key = DeclaredFw.getKey(result, context);
                    Val value = DeclaredFw.getValue(result, context);
                    if (key.type().equals(ExprFw.symbol)) {
                        defineds.put(key._unpack(Symbol.class).getValue(), value);
                    }
                } else {
                    System.out.println(expr);
                    System.out.println(VitFw.wrap(vit).toExpr(context));
//                System.out.println(VitFw.wrap(Vit.simplify(vit, context)).toExpr(context));
                    System.out.println(result.toExpr(context));
                    System.out.println("=========================================");
                }


//            Vit vit = env.compile(expr, context);
//            Val vv = vit == null ? Val.unspecified : VitFw.wrap(vit);
//            System.out.println(vv.toExpr(context));
//            vit = Vit.simplify(vit, context);
//            vv = vit == null ? Val.unspecified : VitFw.wrap(vit);
//            System.out.println(vv.toExpr(context));
//            System.out.println(vv);
            }
            int i = 0;
            for (TestFw.TestRecord test : tests) {
                for (Vit statement : test.statements()) {
                    Val ret = statement.eval(test.context());
                    assertEquals(BoolFw._true, ret);
                }
                i++;
                System.out.println("Passed " + i + "/" + tests.size());
            }
        }
    }
}
