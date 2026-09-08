package com.ydo4ki.fw.internal.lib;

import com.ydo4ki.esast.Symbol;
import com.ydo4ki.fw.internal.debug.Debug;
import com.ydo4ki.fw.internal.lib.jlib._internal.JMethodFw;
import com.ydo4ki.fw.internal.lib.jlib._internal.JVMHandles;
import com.ydo4ki.fw.internal.lib.jlib.data.JOopFw;
import com.ydo4ki.fw.internal.lib.memlib.MemLib;
import com.ydo4ki.fw.internal.lib.stdlib.state.SystemOperation;
import org.fw.core.FW;
import org.fw.core.abstrait.Value;
import com.ydo4ki.esast.BracketsTypes;
import com.ydo4ki.esast.Expr;
import com.ydo4ki.esast.LocatedExpr;
import com.ydo4ki.esast.lexer.ExprOutput;
import com.ydo4ki.esast.lexer.TokenOutput;
import org.fw.base.Val;
import org.fw.esast.util.FwUtils3;
import org.fw.std.DeclaredFw;
import com.ydo4ki.fw.internal.lib.stdlib.StrFw;
import org.fw.std.dvec.DVecFw;
import org.fw.esast.expr.CompEnv;
import org.fw.esast.expr.ExprFw;
import org.fw.esast.expr.Lib;
import org.fw.std.ModuleFw;
import org.fw.esast.expr.StdLib;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import static org.fw.core.FW.symbol;

public final class DeviceLib {

    public static final Lib lib;

    static {
        CompEnv compEnv = CompEnv.of(CompEnv.compEnv(
                StdLib.lib.exports(),
                MemLib.lib.exports(),
                ModuleFw.ModuleCEnvFw.compEnv(ModuleFw.module(
                        DeclaredFw.declared(symbol("_JvmEnv"), JVMHandles.jvmEnv),
                        DeclaredFw.declared(symbol("bufr"), Val._NEW_INSTANCE_(JOopFw.jOop, new BufferedReader(new InputStreamReader(System.in)))),
                        DeclaredFw.declared(symbol("parse-placeholder"), FW.telephonist_native(arg -> {
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
                        DeclaredFw.declared(symbol("expr2string"), FW.telephonist_native(arg -> {
                            if (!ExprFw.isExpr(arg))
                                return null;
                            return StrFw.str(ExprFw.unwrap(arg).toString());
                        }))
                )),
                Debug.lib.exports(),
                JMethodFw.methodCallCEnv
        ));
        try {
            Value dev = FwUtils3.getOperation("org/fw/lib/dev.fw", compEnv, true).apply(SystemOperation.systemState);
            lib = Lib.ofModule(dev);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
