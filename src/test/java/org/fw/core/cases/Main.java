package org.fw.core.cases;

import org.fw.core.FW;
import org.fw.core.Tester;
import org.fw.core.ast.*;
import org.fw.core.ast.lexer.ExprOutput;
import org.fw.core.base.Val;
import org.fw.core.util.FwUtils;
import com.ydo4ki.fyrewurc.lib.devicelib.DeviceLib;
import org.fw.lib.stdlib.*;
import org.fw.lib.stdlib.expr.CompEnv;
import org.fw.lib.stdlib.expr.ExprFw;
import org.fw.lib.stdlib.expr.SyntaxResolveFw;
import org.fw.lib.stdlib.expr.ToExprFn;
import com.ydo4ki.fyrewurc.lib.jlib._internal.JVMHandles;
import com.ydo4ki.fyrewurc.lib.jlib.data.JCharFw;
import com.ydo4ki.fyrewurc.lib.jlib.data.JLongFw;
import com.ydo4ki.fyrewurc.lib.memlib.MemLib;
import com.ydo4ki.fyrewurc.lib.jlib.data.JIntFw;
import com.ydo4ki.fyrewurc.lib.memlib.HeapFw;
import org.fw.core.state.obj.State;
import org.fw.lib.stdlib.state.SystemOperation;
import org.fw.core.base.context.RtEnv;
import org.fw.core.vit.Vit;
import org.fw.core.vit.VitCompilationException;

import java.io.IOException;
import java.util.Scanner;

import static org.fw.core.FW.symbol;

public class Main {

    public static final RtEnv rtEnv = RtEnv.of(ModuleFw.module(
            DeclaredFw.declared(symbol("to-expr"), ToExprFn.toExpr)
    ));

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
                ModuleFw.ModuleCEnvFw.compEnv(ModuleFw.module(
                        DeclaredFw.declared(symbol("test-mod"), ModuleFw.module(
                                DeclaredFw.declared(symbol("test-value"), DIntFw.dint(94))
                        ))
                )),

                Tester.testDirectivesCenv.asVal(),
                directivesCenv.asVal(),
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
                StdLib.lib.exports(),
                MemLib.lib.exports(),
                DeviceLib.lib.exports()
        ));

        compEnv = CompEnv.of(CompEnv.compEnv(
                compEnv.asVal(),
                ModuleFw.ModuleCEnvFw.compEnv(FwUtils.getOperation(FW.class, "sysoperations", compEnv).apply(state))
        ));

//        Tester.testFw(FW.class, "test-int", compEnv);
        Iterable<LocatedExpr<? extends Expr>> expressions = ExprOutput.valueOf(FW.class.getResourceAsStream("test-int.fw"));
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
                System.out.println(val.toExpr(compEnv));
//                System.out.println(val);
            }
        }
    }

    public static final CompEnv directivesCenv = CompEnv.of(FW.telephonist((arg) -> {
        if (arg.type().equals(SyntaxResolveFw.toExprResolve)) {
            Val val = arg.get("passing");
            Val compEnv = arg.get("chain");
            if (val.type() == DIntFw.dint) {
                return ExprFw.wrap(Symbol.of(val._unpack().toString()));
            }
        }
        if (arg.type().equals(SyntaxResolveFw.syntaxResolve)) {
            Val exprVal = arg.call(symbol("expr"));
            Val compEnv = arg.call(symbol("comp-env"));
            Expr expr = exprVal._unpack();
            if (expr instanceof ExprList && ((ExprList) expr).getBracketsType().equals(BracketsTypes.round) && ((ExprList) expr).size() > 0) {
                Expr f = ((ExprList) expr).get(0);
                int isize = ((ExprList) expr).size();
                if (f instanceof Symbol) switch (((Symbol) f).getValue()) {
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

}
