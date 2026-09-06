package com.ydo4ki.fw.internal.lib.devicelib;

import com.ydo4ki.fw.internal.debug.Debug;
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
import org.fw.core.util.FwUtils;
import org.fw.lib.stdlib.DeclaredFw;
import com.ydo4ki.fw.internal.lib.stdlib.StrFw;
import org.fw.lib.stdlib.dvec.DVecFw;
import org.fw.lib.stdlib.expr.CompEnv;
import org.fw.lib.stdlib.expr.ExprFw;
import org.fw.lib.stdlib.expr.Lib;
import org.fw.lib.stdlib.ModuleFw;
import org.fw.lib.stdlib.expr.StdLib;

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
                        DeclaredFw.declared(symbol("bufr"), Val.of(JOopFw.jOop, new BufferedReader(new InputStreamReader(System.in)))),
                        DeclaredFw.declared(symbol("parse-placeholder"), FW.telephonist(arg -> {
                            if (!arg.getType().equals(StrFw.str)) return null;
                            String str = arg._unpack();
                            Iterable<LocatedExpr<?>> exprs = new ExprOutput(new TokenOutput(str, null, BracketsTypes.bracketsTypes));
                            List<Val> vals = new ArrayList<>();
                            for (LocatedExpr<?> expr : exprs) {
                                vals.add(ExprFw.wrap(expr.getExpr()));
                            }
                            //noinspection SimplifyStreamApiCallChains
                            return DVecFw.vec(vals.stream().toArray(Val[]::new));
                        })),
                        DeclaredFw.declared(symbol("expr2string"), FW.telephonist(arg -> {
                            if (!ExprFw.isExpr(arg))
                                return null;
                            return StrFw.str(arg._unpack(Expr.class).toString());
                        }))
                )),
                Debug.lib.exports(),
                JMethodFw.methodCallCEnv
        ));
        try {
            Val dev = FwUtils.getOperation("org/fw/lib/dev", compEnv, true).apply(SystemOperation.systemState);
            lib = Lib.ofModule(dev);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
