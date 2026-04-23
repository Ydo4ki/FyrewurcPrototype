package org.fw.core.lib.expr.j;

import org.fw.core.FW;
import org.fw.core.FwUtils;
import org.fw.core.ast.Expr;
import org.fw.core.base.Val;
import org.fw.core.lib.DIntFw;
import org.fw.core.lib.StrFw;
import org.fw.core.lib.VitFw;
import org.fw.core.lib.expr.CompEnv;
import org.fw.core.lib.expr.ExprCallOpFw;
import org.fw.core.vit.Vit;

import java.io.File;
import java.io.IOException;
import java.math.BigInteger;

import static org.fw.core.FW.symbol;

public final class JConstInclude {
    public static final Val constInclude = FW.telephonist("const-include", (arg, context) -> {
        if (arg.type().equals(ExprCallOpFw.exprCallOp)) {
            Val size = arg.call(symbol("size"), context);
            Val cEnv = arg.call(symbol("comp-env"), context);
            int isize = size._unpack(BigInteger.class).intValue();
            if (isize != 1) {
                return Val.unspecified;
            }

            Expr e = arg.call(DIntFw.dint(0), context)._unpack();
            Val retVit = cEnv.call(CompEnv.syntaxResolve(e, CompEnv.of(cEnv)), context);
            if (!VitFw.isVit(retVit.type()))
                return retVit; // compile error idk

            Vit nameVit = VitFw.unwrap(retVit);
            assert nameVit != null;

            Val nameVal = nameVit.eval(context);
            if (!nameVal.type().equals(StrFw.str))
                return Val.unspecified;

            String name = nameVal._unpack();

            File thisFile = e.getLocation().getSourceFile();
            File folder = thisFile.getParentFile();
            File targetFile = new File(folder, name);
            try {
                return VitFw.wrap(Vit.val(FwUtils.getValueFromFile(targetFile, CompEnv.of(cEnv), context)));
            } catch (IOException ex) {
                ex.printStackTrace(System.err);
                return Val.unspecified;
            }
        }
        return Val.unspecified;
    });
}
