package org.fw;

import com.ydo4ki.fw.internal.debug.Debug;
import com.ydo4ki.fw.internal.lib.devicelib.DeviceLib;
import com.ydo4ki.fw.internal.lib.jlib._internal.JMethodFw;
import com.ydo4ki.fw.internal.lib.jlib._internal.JVMHandles;
import com.ydo4ki.fw.internal.lib.jlib.data.JOopFw;
import com.ydo4ki.fw.internal.lib.memlib.MemLib;
import com.ydo4ki.fw.internal.lib.stdlib.state.SystemOperation;
import org.fw.core.FW;
import org.fw.core.ast.BracketsTypes;
import org.fw.core.ast.Expr;
import org.fw.core.ast.LocatedExpr;
import org.fw.core.ast.lexer.ExprOutput;
import org.fw.core.ast.lexer.TokenOutput;
import org.fw.core.base.Val;
import org.fw.core.state.obj.State;
import org.fw.core.state.operation.Operation;
import org.fw.core.util.FwUtils;
import org.fw.lib.stdlib.DeclaredFw;
import org.fw.lib.stdlib.ModuleFw;
import com.ydo4ki.fw.internal.lib.stdlib.StrFw;
import org.fw.lib.stdlib.dvec.DVecFw;
import org.fw.lib.stdlib.expr.CompEnv;
import org.fw.lib.stdlib.expr.ExprFw;
import org.fw.lib.stdlib.expr.StdLib;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import static org.fw.core.FW.symbol;

public final class Shell {
    public static void main(String[] args) throws IOException {
        State state = SystemOperation.systemState;
        CompEnv compEnv = CompEnv.of(CompEnv.compEnv(
                StdLib.lib.exports(),
                MemLib.lib.exports(),
                DeviceLib.lib.exports(),
                ModuleFw.ModuleCEnvFw.compEnv(ModuleFw.module(
                        DeclaredFw.declared(symbol("_JvmEnv"), JVMHandles.jvmEnv),
                        DeclaredFw.declared(symbol("bufr"), Val.of(JOopFw.jOop, new BufferedReader(new InputStreamReader(System.in)))),
                        DeclaredFw.declared(symbol("parse-placeholder"), FW.telephonist_native(arg -> {
                            if (!arg.getType().equals(StrFw.str)) return null;
                            String str = arg._UNPACK();
                            Iterable<LocatedExpr<?>> exprs = new ExprOutput(new TokenOutput(str, null, BracketsTypes.bracketsTypes));
                            List<Val> vals = new ArrayList<>();
                            for (LocatedExpr<?> expr : exprs) {
                                vals.add(ExprFw.wrap(expr.getExpr()));
                            }
                            //noinspection SimplifyStreamApiCallChains
                            return DVecFw.vec(vals.stream().toArray(Val[]::new));
                        })),
                        DeclaredFw.declared(symbol("expr2string"), FW.telephonist_native(arg -> {
                            if (!ExprFw.isExpr(arg))
                                return null;
                            return StrFw.str(arg._UNPACK(Expr.class).toString());
                        }))
                )),
                Debug.lib.exports(),
                JMethodFw.methodCallCEnv
        ));

        CompEnv internalCompEnv = CompEnv.of(CompEnv.compEnv(
                compEnv.asVal()
        ));

        Val sysoperations = ModuleFw.ModuleCEnvFw.compEnv(FwUtils.getOperation(FW.class, "sysoperations", internalCompEnv, false).apply(state));

        compEnv = CompEnv.of(CompEnv.compEnv(
                compEnv.asVal(),
                sysoperations
        ));

        internalCompEnv = CompEnv.of(CompEnv.compEnv(
                internalCompEnv.asVal(),
                sysoperations
        ));
//        BufferedReader reader = ;
//
//        System.out.print("Enter your text: ");
//        try {
//            // Read the full line
//            String input = reader.readLine();
//            System.out.println("You entered: " + input);
//        } catch (IOException e) {
//            System.err.println("Error reading input: " + e.getMessage());
//        }
        Operation operation = FwUtils.getOperation("org/fw/shell", internalCompEnv, true);
        operation.apply(state);
    }
}
