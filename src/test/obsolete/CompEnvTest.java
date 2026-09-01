package obsolete;

import org.fw.core.FW;
import org.fw.core.ast.BracketsTypes;
import org.fw.core.ast.Expr;
import org.fw.core.ast.lexer.ExprOutput;
import org.fw.core.ast.lexer.TokenOutput;
import org.fw.core.base.SymbolFw;
import org.fw.core.base.Val;
import org.fw.core.cases.Main;
import org.fw.core.state.obj.AtomObj;
import org.fw.lib.stdlib.DIntFw;
import org.fw.lib.stdlib.VitFw;
import org.fw.lib.stdlib.expr.AccumulatorsExprFw;
import org.fw.lib.stdlib.expr.CompEnv;
import com.ydo4ki.fw.internal.lib.stdlib.state.SystemOperation;
import org.fw.lib.stdlib.state.LaserPointerFw;
import org.fw.core.util.FwUtils;
import org.fw.core.vit.Vit;
import org.fw.core.vit.VitCompilationException;
import org.junit.jupiter.api.Test;

import java.util.*;

import static org.fw.core.FW.symbol;
import static org.fw.core.FW.telephonist;
import static org.fw.core.vit.Vit.*;
import static org.junit.jupiter.api.Assertions.*;

class CompEnvTest {

    //            VitiateTelephonistFw.vitiate(
//            FW.vIf(FW.vEq(val(ValsFw.typeGet).call(argExpr), val(ExprFw.exprList.asVal())),
//                    Vit.val(VitFw.vitCall.asVal()).call(symbol("builder"))
//                            .call(argCEnv.call(
//                                    val(CompEnv.syntaxResolve.asVal()).call(symbol("builder"))
//                                            .call(argExpr.call(DIntFw.dint(0)))
//                                            .call(argCEnv)
//                            ))
//                            .call(Vit.val(VitFw.vitVal.asVal()).call(symbol("construct"))
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
//        testValsMap.put("Test", TestFw.test.asVal());
        testValsMap.put("+", Val.of(AccumulatorsExprFw.exprAccumulator, symbol("+")));
        testValsMap.put("test-obj", Val.of(LaserPointerFw.laserPointer, AtomObj.of(DIntFw.dint(14), SystemOperation.systemState.scope())));
//        testValsMap.put("obj-stream", Val.of(StateHoleFw.statehole, new ObjStream(Val.unspecified, context.scope())));
    }

    public static final Val testValsCenv = FwUtils.symbolMapVitEnv(val(FW.telephonist("vals", (arg1) -> {
        if (!arg1.type().equals(SymbolFw.symbol))
            return null;
        String string = arg1._unpack().toString();
        Val ret = testValsMap.get(string);
        if (ret != null)
            return VitFw.wrap(val(ret));
        return null;
    })));

    @Test
    void testNumbers() {
        String source = "5";

        Expr expr = new ExprOutput(new TokenOutput(source, null, BracketsTypes.bracketsTypes)).iterator().next().getExpr();

        CompEnv env = CompEnv.of(CompEnv.compEnv(DIntFw.ParseDIntCEnvFw.parseNumCenv, null));
        Vit vit = null;
        try {
            vit = env.compile(expr);
        } catch (VitCompilationException e) {
            throw new RuntimeException(e);
        }
        Val vv = VitFw.wrap(vit);
//        System.out.println(vv.toExpr(context));
//        System.out.println(vv);
        assertEquals(vit.eval(Main.rtEnv, SystemOperation.systemState), DIntFw.dint(5));
        assertEquals(vv, VitFw.wrap(val(DIntFw.dint(5))));
    }

//    @Test
//    void testVals() throws VitCompilationException {
//        String source = ":";
//
//        Expr expr = new ExprOutput(new TokenOutput(source, null, BracketsTypes.bracketsTypes)).iterator().next().getExpr();
//
//        CompEnv env = CompEnv.of(CompEnv.compEnv(InternalSymbolMapCEnvFw.valsCenv, Operation.unit));
//        Vit vit = env.compile(expr);
//        Val vv = VitFw.wrap(vit);
////        System.out.println(vv.toExpr(context));
////        System.out.println(vv);
//        Assertions.assertNotNull(vit);
////        assertEquals(DeclaredFw.colon, vit.eval(context));
////        assertEquals(vv, VitFw.wrap(val(DeclaredFw.colon)));
//    }

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
//                (call 5 (call (call Symbol (Symbol "construct")) "+") 9)
//                "construct"
//
//                toExpr
//                (call BoxType (Symbol "construct") (Symbol "to-expr"))
//                (call eq toExpr (call BoxType (Symbol "construct") (Symbol "to-expr")))
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

//    @Test
//    void callTests() throws IOException {
//
//        Map<String, Val> defineds = new HashMap<>();
//
//        final Val defined = InternalSymbolMapCEnvFw.symbolMapVitEnv(val(FW.telephonist("vals", (arg1, c) -> {
//            if (!arg1.type().equals(SymbolFw.symbol))
//                return null;
//            String string = arg1._unpack().toString();
//            Val ret = defineds.get(string);
//            if (ret != null)
//                return VitFw.wrap(val(ret));
//            return null;
//        })));
//
//
////        CompEnv env = CompEnv.of(CompEnv.compEnv(context, ParseNumCEnvFw.parseNumCenv, testValsCenv, defined, InternalSymbolMapCEnvFw.valsCenv,
////                DVecConstructorCEnvFw.dVecConstructorCenv, InvokeFuncCEnvFw.invokeFuncCenv, ParseStrCEnvFw.parseStrCenv, CurrentCompEnvCEnvFw.currentCompEnvCenv));
//
//
//        CompEnv env = CompEnv.of(CompEnv.compEnv(context, Main.publicCompEnv.asVal(), testValsCenv, defined, InternalSymbolMapCEnvFw.valsCenv));
//
//        File testFolder = new File("tests");
//        for (File file : Objects.requireNonNull(testFolder.listFiles())) {
//            if (!file.getName().endsWith(".fw") || file.getName().startsWith("temp"))
//                continue;
//
//            System.out.println("========================================= " + file.getName() + " =========================================");
//            Iterable<LocatedExpr<? extends Expr>> expressions = new ExprOutput(new TokenOutput(file, BracketsTypes.bracketsTypes));
//
//            List<TestFw.TestRecord> tests = new ArrayList<>();
//            for (LocatedExpr<? extends Expr> lExpr : expressions) {
//                Expr expr = lExpr.getExpr();
//                Val vitVal = env.compileV(expr, context);
//                Vit vit = VitFw.unwrap0(vitVal);
//                if (vit == null) {
//                    fail("# " + vitVal.toExpr(context) + " from " + expr);
//                    // ok its impossible to work under such noice
//                    // ill be back later
//                    continue;
//                }
//                Val result = vit.eval(context);
//                if (result.type().equals(TestFw.test)) {
//                    tests.add(result._unpack());
////                System.out.println(result.toExpr(context));
//                } else if (result.type().equals(DeclaredFw.declared)) {
//                    Val key = DeclaredFw.getKey(result, context);
//                    Val value = DeclaredFw.getValue(result, context);
//                    if (key.type().equals(SymbolFw.symbol)) {
//                        defineds.put(key._unpack(Symbol.class).getValue(), value);
//                    }
//                } else {
//                    System.out.println(expr);
//                    System.out.println(VitFw.wrap(vit).toExpr(context));
////                System.out.println(VitFw.wrap(Vit.simplify(vit, context)).toExpr(context));
//                    System.out.println(result.toExpr(context));
//                    System.out.println("=========================================");
//                }
//
//
////            Vit vit = env.compile(expr, context);
////            Val vv = vit == null ? Val.unspecified : VitFw.wrap(vit);
////            System.out.println(vv.toExpr(context));
////            vit = Vit.simplify(vit, context);
////            vv = vit == null ? Val.unspecified : VitFw.wrap(vit);
////            System.out.println(vv.toExpr(context));
////            System.out.println(vv);
//            }
//            int i = 0;
//            for (TestFw.TestRecord test : tests) {
//                for (Vit statement : test.statements()) {
//                    Val ret = statement.eval(test.context());
//                    assertEquals(BoolFw._true, ret);
//                }
//                i++;
//                System.out.println("Passed " + i + "/" + tests.size());
//            }
//        }
//    }
}
