package org.fw;

import com.ydo4ki.fw.internal.debug.Debug;
import com.ydo4ki.fw.internal.lib.DeviceLib;
import com.ydo4ki.fw.internal.lib.jlib._internal.JMethodFw;
import com.ydo4ki.fw.internal.lib.jlib._internal.JVMHandles;
import com.ydo4ki.fw.internal.lib.jlib.data.JOopFw;
import com.ydo4ki.fw.internal.lib.memlib.MemLib;
import com.ydo4ki.fw.internal.lib.stdlib.state.SystemOperation;
import org.fw.core.FW;
import com.ydo4ki.esast.BracketsTypes;
import com.ydo4ki.esast.LocatedExpr;
import com.ydo4ki.esast.lexer.ExprOutput;
import com.ydo4ki.esast.lexer.TokenOutput;
import org.fw.base.Val;
import org.fw.core.state.obj.State;
import org.fw.core.state.operation.Operation;
import org.fw.esast.util.FwUtils3;
import org.fw.std.DeclaredFw;
import org.fw.std.ModuleFw;
import com.ydo4ki.fw.internal.lib.stdlib.StrFw;
import org.fw.std.dvec.DVecFw;
import org.fw.esast.expr.CompEnv;
import org.fw.esast.expr.ExprFw;
import org.fw.esast.expr.StdLib;

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
                        DeclaredFw.declared(symbol("bufr"), Val._NEW_INSTANCE_(JOopFw.jOop, new BufferedReader(new InputStreamReader(System.in)))),
                        DeclaredFw.declared(symbol("parse-placeholder"), FW.lambda_native(arg -> {
                            if (!arg.getType().equals(StrFw.str)) return null;
                            String str = arg._UNPACK_();
                            Iterable<LocatedExpr<?>> exprs = new ExprOutput(new TokenOutput(str, null, BracketsTypes.bracketsTypes));
                            List<Val> vals = new ArrayList<>();
                            for (LocatedExpr<?> expr : exprs) {
                                vals.add(ExprFw.wrap(expr.getExpr()));
                            }
                            //noinspection SimplifyStreamApiCallChains
                            return DVecFw.vec(vals.stream().toArray(Val[]::new));
                        })),
                        DeclaredFw.declared(symbol("expr2string"), FW.lambda_native(arg -> {
                            if (!ExprFw.isExpr(arg))
                                return null;
                            return StrFw.str(ExprFw.unwrap(arg).toString());
                        }))
                )),
                Debug.lib.exports(),
                JMethodFw.methodCallCEnv
        ));

        CompEnv internalCompEnv = CompEnv.of(CompEnv.compEnv(
                compEnv.asValue()
        ));

        Val sysoperations = ModuleFw.ModuleCEnvFw.compEnv((Val) FwUtils3.getOperation(FW.class, "sysoperations.fw", internalCompEnv, false).apply(state));

        compEnv = CompEnv.of(CompEnv.compEnv(
                compEnv.asValue(),
                sysoperations
        ));

        internalCompEnv = CompEnv.of(CompEnv.compEnv(
                internalCompEnv.asValue(),
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
        Operation operation = FwUtils3.getOperation("org/fw/shell.fw", internalCompEnv, true);
        operation.apply(state);
    }
}
