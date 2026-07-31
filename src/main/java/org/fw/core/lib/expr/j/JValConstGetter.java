package org.fw.core.lib.expr.j;

import org.fw.core.FW;
import org.fw.core.base.Type;
import org.fw.core.base.Val;
import org.fw.core.base.context.Context;
import org.fw.core.base.context.RtEnv;
import org.fw.core.lib.DIntFw;
import org.fw.core.lib.StrFw;
import org.fw.core.lib.VitFw;
import org.fw.core.lib.expr.CompEnv;
import org.fw.core.lib.expr.ExprCallOpFw;
import org.fw.core.state.obj.State;
import org.fw.core.vit.Vit;

import java.lang.reflect.Field;
import java.math.BigInteger;

import static org.fw.core.FW.symbol;

public final class JValConstGetter {
    public static final Val jval = FW.telephonist("jval", (arg) -> {
        if (arg.type().equals(ExprCallOpFw.exprCallOp)) {
            Val size = arg.call(symbol("size"));
            Val cEnv = arg.call(symbol("comp-env"));
            int isize = size._unpack(BigInteger.class).intValue();
            if (isize != 2) {
                return null;
            }

            Val retVit = cEnv.call(CompEnv.syntaxResolve(arg.call(DIntFw.dint(0))._unpack(), CompEnv.of(cEnv)));
            if (!VitFw.isVit(retVit.type()))
                return retVit; // compile error idk

            Vit nameVit = VitFw.unwrap0(retVit);
            assert nameVit != null;

            Val nameVal = nameVit.eval(RtEnv.unspecified);
            if (!nameVal.type().equals(StrFw.str))
                return null;

            String name = nameVal._unpack();

            Val fretVit = cEnv.call(CompEnv.syntaxResolve(arg.call(DIntFw.dint(1))._unpack(), CompEnv.of(cEnv)));
            if (!VitFw.isVit(fretVit.type()))
                return fretVit; // compile error idk

            Vit fnameVit = fretVit._unpack();
            assert fnameVit != null;

            Val fnameVal = fnameVit.eval(RtEnv.unspecified);
            if (!fnameVal.type().equals(StrFw.str))
                return null;

            String fieldName = fnameVal._unpack();
            try {
                Class<?> cls = Class.forName("org.fw." + name);
                Field field = cls.getField(fieldName);
                field.setAccessible(true);
                Object ret = field.get(null);

                if (ret instanceof Type) ret = ((Type) ret).asVal();
                if (ret == null)
                    throw new NullPointerException("Not initialized yet: " + fieldName + " in " + name);

                return VitFw.wrap(Vit.val((Val)ret));
            } catch (ClassNotFoundException | NoSuchFieldException | IllegalAccessException e) {
                throw new RuntimeException(e);
            }
        }
        return null;
    });
}
