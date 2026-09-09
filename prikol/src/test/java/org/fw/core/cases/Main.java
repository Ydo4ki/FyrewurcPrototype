package org.fw.core.cases;

import com.ydo4ki.esast.*;
import com.ydo4ki.fw.internal.lib.devicelib.DeviceLib;
import com.ydo4ki.fw.internal.lib.jlib._internal.JMethodFw;
import com.ydo4ki.fw.internal.lib.jlib._internal.JVMHandles;
import com.ydo4ki.fw.internal.lib.jlib.data.JCharFw;
import com.ydo4ki.fw.internal.lib.jlib.data.JIntFw;
import com.ydo4ki.fw.internal.lib.jlib.data.JLongFw;
import com.ydo4ki.fw.internal.lib.stdlib.DIntFw;
import com.ydo4ki.fw.internal.lib.stdlib.StrFw;
import org.fw.core.FW;
import org.fw.core.abstrait.Value;
import com.ydo4ki.esast.lexer.ExprOutput;
import org.fw.base.Val;
import com.ydo4ki.fw.internal.lib.memlib.MemLib;
import com.ydo4ki.fw.internal.lib.memlib.HeapFw;
import org.fw.core.state.obj.State;
import com.ydo4ki.fw.internal.lib.stdlib.state.SystemOperation;
import org.fw.core.state.operation.Operation;
import org.fw.core.vit.Vit;
import org.fw.esast.ExprVitCompilationException;
import org.fw.esast.expr.forstd.ModuleLib;
import org.fw.esast.util.FwUtils3;
import org.fw.std.DeclaredFw;
import org.fw.std.ModuleFw;
import org.fw.std.VitFw;
import org.fw.esast.expr.CompEnv;
import org.fw.esast.expr.ExprFw;
import org.fw.esast.expr.StdLib;
import org.fw.esast.expr.SyntaxResolveFw;
import org.fw.test.Tester3;

import java.io.IOException;
import java.io.PrintStream;
import java.util.Scanner;

import static org.fw.core.FW.symbol;

public class Main {

    public static final Val rtEnv = FW.lambda((arg) -> null);
    public static final Operation currentTimeMillis = new SystemOperation() {
        @Override
        protected Val apply0() {
            return DIntFw.dint(System.currentTimeMillis());
        }
    };
    public static final Operation nanoTime = new SystemOperation() {
        @Override
        protected Val apply0() {
            return DIntFw.dint(System.nanoTime());
        }
    };

    public static void main(String[] args) throws IOException {
//        Iterable<LocatedExpr<? extends Expr>> expressions = ExprOutput.valueOf(FW.class.getResourceAsStream("test-bullsandcows.fw"));
//        Iterable<LocatedExpr<? extends Expr>> expressions = ExprOutput.valueOf(FW.class.getResourceAsStream("test-memory.fw"));
//        Iterable<LocatedExpr<? extends Expr>> expressions = ExprOutput.valueOf(FW.class.getResourceAsStream("test-arrays.fw"));
//        Iterable<LocatedExpr<? extends Expr>> expressions = ExprOutput.valueOf(FW.class.getResourceAsStream("test-dvec.fw"));
//        Iterable<LocatedExpr<? extends Expr>> expressions = ExprOutput.valueOf(FW.class.getResourceAsStream("test-error0000000.fw"));
//        Iterable<LocatedExpr<? extends Expr>> expressions = ExprOutput.valueOf(FW.class.getResourceAsStream("test-internal.fw"));

//        Iterable<LocatedExpr<? extends Expr>> expressions = ExprOutput.valueOf(FW.class.getResourceAsStream("test-naive-fibonachi.fw"));

        State state = SystemOperation.systemState;
        CompEnv compEnv = CompEnv.of(CompEnv.compEnv(
                StdLib.lib.exports(),
                MemLib.lib.exports(),
                DeviceLib.lib.exports(),
                ModuleLib.ModuleCEnvFw.compEnv(ModuleFw.module(
                        DeclaredFw.declared(symbol("test-mod"), ModuleFw.module(
                                DeclaredFw.declared(symbol("test-value"), DIntFw.dint(94))
                        ))
                )),

                Tester3.testDirectivesCenv.asValue(),
                directivesCenv.asValue(),
                ModuleLib.ModuleCEnvFw.compEnv(ModuleFw.module(
                        DeclaredFw.declared(symbol("_Flush"), new FlushOperation(System.out).asVal()),
                        DeclaredFw.declared(symbol("_ReadLine"), new ReadLineOperation(new Scanner(System.in)).asVal()),
                        DeclaredFw.declared(symbol("_CurrentTimeMillis"), currentTimeMillis.asVal()),
                        DeclaredFw.declared(symbol("_NanoTime"), nanoTime.asVal()),
                        DeclaredFw.declared(symbol("_Sleep"), FW.lambda_native((arg) -> {
                            if (arg.getType() != DIntFw.dint)
                                return null;

                            return new ThreadSleepOperation(DIntFw.unwrap(arg).longValue()).asVal();
                        })),
                        DeclaredFw.declared(symbol("_JvmEnv"), JVMHandles.jvmEnv),
                        DeclaredFw.declared(symbol("heap"), HeapFw.systemHeap)
                )),
                JMethodFw.methodCallCEnv
        ));

        compEnv = CompEnv.of(CompEnv.compEnv(
                compEnv.asValue(),
                ModuleLib.ModuleCEnvFw.compEnv(FwUtils3.getOperation(FW.class, "sysoperations.fw", compEnv, true).apply(state))
        ));

//        Tester.testFw(FW.class, "test-int", compEnv);
        Iterable<LocatedExpr<? extends Expr>> expressions = ExprOutput.valueOf(Main.class.getResourceAsStream("test-int.fw"));
        for (LocatedExpr<? extends Expr> locatedExpression : expressions) {
            Expr expression = locatedExpression.getExpr();
            Vit vit;
            try {
                vit = compEnv.compile(expression);
            } catch (ExprVitCompilationException e) {
                System.err.println(expression);
                throw new RuntimeException(e);
            }
            Val val = (Val) vit.eval(rtEnv, state);
            if (val.getType() == DeclaredFw.declared) {
                compEnv = CompEnv.of(CompEnv.compEnv(ModuleLib.ModuleCEnvFw.compEnv(ModuleFw.module(val)), compEnv.asValue()));
            } else {
                System.out.println(compEnv.toExpr(val));
//                System.out.println(val);
            }
        }
    }

    public static final CompEnv directivesCenv = CompEnv.of(FW.lambda_native((arg) -> {
        if (arg.getType().equals(SyntaxResolveFw.toExprResolve)) {
            Val val = (Val) arg.get("passing");
            Val compEnv = (Val) arg.get("chain");
            if (val.getType() == DIntFw.dint) {
                return ExprFw.wrap(Symbol.of(val._UNPACK_().toString()));
            }
        }
        if (arg.getType().equals(SyntaxResolveFw.syntaxResolve)) {
            Val exprVal = (Val) arg.call(FW.symbol("expr"));
            Val compEnv = (Val) arg.call(FW.symbol("comp-env"));
            Expr expr = exprVal._UNPACK_();
            if (expr instanceof ExprList && ((ExprList) expr).getBracketsType().equals(BracketsTypes.round) && ((ExprList) expr).size() > 0) {
                Expr f = ((ExprList) expr).get(0);
                int isize = ((ExprList) expr).size();
                if (f instanceof Symbol) switch (((Symbol) f).getValue()) {
                    case "jint": {
                        if (isize != 2)
                            return null;

                        String number = ((Val) exprVal.call(DIntFw.dint(1)))._UNPACK_().toString();
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

                        String number = ((Val) exprVal.call(DIntFw.dint(1)))._UNPACK_().toString();
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

                        String token = ((Val) exprVal.call(DIntFw.dint(1)))._UNPACK_().toString();
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

    public static class ReadLineOperation extends SystemOperation {
        private final Scanner scanner;

        public ReadLineOperation(Scanner scanner) {
            this.scanner = scanner;
        }

        @Override
        protected Val apply0() {
            return StrFw.str(scanner.nextLine());
        }
    }

    public static class FlushOperation extends SystemOperation {
        private final PrintStream out;

        public FlushOperation(PrintStream out) {
            this.out = out;
        }

        @Override
        protected Val apply0() {
            out.flush();
            return Operation.unit;
        }
    }

    public static class ThreadSleepOperation extends SystemOperation {
        private final long millis;

        public ThreadSleepOperation(long millis) {
            this.millis = millis;
        }

        @Override
        protected Val apply0() {
            try {
                Thread.sleep(millis, 0);
            } catch (InterruptedException e) {
                return Operation.unit;
            }
            return Operation.unit;
        }
    }
}
