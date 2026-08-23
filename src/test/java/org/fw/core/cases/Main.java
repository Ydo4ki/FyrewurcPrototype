package org.fw.core.cases;

import org.fw.core.FW;
import org.fw.core.ast.*;
import org.fw.core.ast.lexer.ExprOutput;
import org.fw.core.base.BoolFw;
import org.fw.core.base.Val;
import org.fw.lib.elib.*;
import org.fw.lib.elib.expr.CompEnv;
import org.fw.lib.elib.expr.SyntaxResolveFw;
import org.fw.lib.elib.expr.ToExprFn;
import org.fw.lib.jlib._internal.JVMHandles;
import org.fw.lib.jlib.data.JCharFw;
import org.fw.lib.jlib.data.JLongFw;
import org.fw.lib.memlib.MemLib;
import org.fw.lib.jlib.data.JIntFw;
import org.fw.lib.memlib.HeapFw;
import org.fw.core.state.operation.Operation;
import org.fw.core.state.operation.OperationFw;
import org.fw.core.state.obj.State;
import org.fw.lib.elib.state.SystemOperation;
import org.fw.core.base.context.RtEnv;
import org.fw.core.vit.Vit;
import org.fw.core.vit.VitCompilationException;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import static org.fw.core.FW.symbol;
import static org.fw.core.FW.telephonist;

public class Main {

    public static final RtEnv rtEnv = RtEnv.of(ModuleFw.module(
            DeclaredFw.declared(symbol("to-expr"), ToExprFn.toExpr)
    ));

    public static void main(String[] args) {
//        Iterable<LocatedExpr<? extends Expr>> expressions = ExprOutput.valueOf(FW.class.getResourceAsStream("test-bullsandcows.fw"));
//        Iterable<LocatedExpr<? extends Expr>> expressions = ExprOutput.valueOf(FW.class.getResourceAsStream("test-memory.fw"));
//        Iterable<LocatedExpr<? extends Expr>> expressions = ExprOutput.valueOf(FW.class.getResourceAsStream("test-arrays.fw"));
//        Iterable<LocatedExpr<? extends Expr>> expressions = ExprOutput.valueOf(FW.class.getResourceAsStream("test-dvec.fw"));
//        Iterable<LocatedExpr<? extends Expr>> expressions = ExprOutput.valueOf(FW.class.getResourceAsStream("test-error0000000.fw"));
//        Iterable<LocatedExpr<? extends Expr>> expressions = ExprOutput.valueOf(FW.class.getResourceAsStream("test-internal.fw"));
        Iterable<LocatedExpr<? extends Expr>> expressions = ExprOutput.valueOf(FW.class.getResourceAsStream("test-int.fw"));
//        Iterable<LocatedExpr<? extends Expr>> expressions = ExprOutput.valueOf(FW.class.getResourceAsStream("test-naive-fibonachi.fw"));

        State state = SystemOperation.systemState;
        CompEnv compEnv;
        compEnv = CompEnv.of(CompEnv.compEnv(
                EssentiaLibstd.lib.exports(),
                MemLib.lib.exports(),
                ModuleFw.ModuleCEnvFw.compEnv(ModuleFw.module(
                        DeclaredFw.declared(symbol("_Flush"), new SystemOperation.FlushOperation(System.out).asVal()),
                        DeclaredFw.declared(symbol("_ReadLine"), new SystemOperation.ReadLineOperation(new Scanner(System.in)).asVal()),
                        DeclaredFw.declared(symbol("_CurrentTimeMillis"), SystemOperation.currentTimeMillis.asVal()),
                        DeclaredFw.declared(symbol("_NanoTime"), SystemOperation.nanoTime.asVal()),
                        DeclaredFw.declared(symbol("_Sleep"), FW.telephonist((arg) -> {
                            if (arg.type() != DIntFw.dint)
                                return null;

                            return new SystemOperation.ThreadSleepOperation(DIntFw.unwrap(arg).longValue()).asVal();
                        })),
                        DeclaredFw.declared(symbol("_JvmEnv"), JVMHandles.jvmEnv),
                        DeclaredFw.declared(symbol("heap"), HeapFw.systemHeap)
                )),
                directivesCenv.asVal(),

                ModuleFw.ModuleCEnvFw.compEnv(ModuleFw.module(
                        DeclaredFw.declared(symbol("test-mod"), ModuleFw.module(
                                DeclaredFw.declared(symbol("test-value"), DIntFw.dint(94))
                        ))
                ))
        ));

        List<Iterable<LocatedExpr<? extends Expr>>> additionals = new ArrayList<>();
        additionals.add(ExprOutput.valueOf(FW.class.getResourceAsStream("operationfns.fw")));
        additionals.add(ExprOutput.valueOf(FW.class.getResourceAsStream("sysoperations.fw")));

        for (Iterable<LocatedExpr<? extends Expr>> additional1 : additionals) {
            CompEnv perFileCE = compEnv;
            for (LocatedExpr<? extends Expr> locatedExpression : additional1) {
                Expr expression = locatedExpression.getExpr();
                Vit vit;
                try {
                    vit = perFileCE.compile(expression);
                } catch (VitCompilationException e) {
                    System.err.println(expression);
                    throw new RuntimeException(e);
                }
                Val val = vit.eval(rtEnv, state);
                if (val.type() == DeclaredFw.declared) {
                    perFileCE = CompEnv.of(CompEnv.compEnv(ModuleFw.ModuleCEnvFw.compEnv(ModuleFw.module(val)), perFileCE.asVal()));
                } else {
                    compEnv = CompEnv.of(CompEnv.compEnv(
                            compEnv.asVal(),
                            ModuleFw.ModuleCEnvFw.compEnv(val)
                    ));
                }
            }
        }

        for (LocatedExpr<? extends Expr> locatedExpression : expressions) {
            Expr expression = locatedExpression.getExpr();
            Vit vit;
            try {
                vit = compEnv.compile(expression);
            } catch (VitCompilationException e) {
                System.err.println(expression);
                throw new RuntimeException(e);
            }
            Val val = vit.eval(rtEnv, state);
            if (val.type() == DeclaredFw.declared) {
                compEnv = CompEnv.of(CompEnv.compEnv(ModuleFw.ModuleCEnvFw.compEnv(ModuleFw.module(val)), compEnv.asVal()));
            } else {
                System.out.println(val.toExpr(rtEnv));
//                System.out.println(val);
            }
        }
    }

    public static final CompEnv directivesCenv = CompEnv.of(FW.telephonist((arg) -> {
        if (arg.type().equals(SyntaxResolveFw.syntaxResolve)) {
            Val exprVal = arg.call(symbol("expr"));
            Val compEnv = arg.call(symbol("comp-env"));
            Expr expr = exprVal._unpack();
            if (expr instanceof ExprList && ((ExprList) expr).getBracketsType().equals(BracketsTypes.round) && ((ExprList) expr).size() > 0) {
                Expr f = ((ExprList) expr).get(0);
                int isize = ((ExprList) expr).size();
                if (f instanceof Symbol) switch (((Symbol) f).getValue()) {
                    case "assert!": {
                        if (isize != 2)
                            return null;

                        Val condition = compEnv.call(CompEnv.syntaxResolve(exprVal.call(DIntFw.dint(1))._unpack(), CompEnv.of(compEnv)));
                        if (!VitFw.isVit(condition.type()))
                            return null;
                        Vit vitOperation = Vit.call(OperationFw._VitOperation, condition).call(Vit.var);
                        Vit assertOperation = Vit.call(FW.telephonist(arg1 ->
                                new AssertOperation(arg1._unpack(Operation.class)).asVal()), vitOperation);
                        return VitFw.wrap(Vit.invoke(assertOperation));
                    }
                    case "jint": {
                        if (isize != 2)
                            return null;

                        String number = exprVal.call(DIntFw.dint(1))._unpack().toString();
                        int b;
                        try {
                            if (number.startsWith("0x")) {
                                b = Integer.parseUnsignedInt(number.substring(2), 16);
                            } else {
                                b = Integer.parseInt(number);
                            }
                        } catch (RuntimeException e) {
                            return null;
                        }
                        return VitFw.wrap(Vit.val(JIntFw.wrap(b)));
                    }
                    case "jlong": {
                        if (isize != 2)
                            return null;

                        String number = exprVal.call(DIntFw.dint(1))._unpack().toString();
                        long b;
                        try {
                            if (number.startsWith("0x")) {
                                b = Long.parseUnsignedLong(number.substring(2), 16);
                            } else {
                                b = Long.parseLong(number);
                            }
                        } catch (RuntimeException e) {
                            return null;
                        }
                        return VitFw.wrap(Vit.val(JLongFw.wrap(b)));
                    }
                    case "jchar": {
                        if (isize != 2)
                            return null;

                        String token = exprVal.call(DIntFw.dint(1))._unpack().toString();
                        char b;
                        try {
                            if (token.length() == 3 && token.charAt(0) == '\'' && token.charAt(2) == '\'') {
                                b = token.charAt(1);
                            } else {
                                b = (char)Integer.parseInt(token);
                            }
                        } catch (RuntimeException e) {
                            return null;
                        }
                        return VitFw.wrap(Vit.val(JCharFw.wrap(b)));
                    }
                }
            }
        }
        return null;
    }));

    static class AssertOperation extends Operation {
        private final Operation _assert;

        AssertOperation(Operation anAssert) {
            _assert = anAssert;
        }

        @Override
        public Val apply(State state) {
            Val ret = _assert.apply(state);
            if (ret == BoolFw._true) return Operation.unit;
            else throw new AssertionError(_assert.toString());
        }

        @Override
        protected boolean isPure0() {
            return false;
        }
    }
}
