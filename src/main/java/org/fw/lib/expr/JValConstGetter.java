package org.fw.lib.expr;

import org.fw.FW;
import org.fw.base.Type;
import org.fw.base.Val;
import org.fw.lib.DIntFw;
import org.fw.lib.StrFw;
import org.fw.lib.VitFw;
import org.fw.vit.Vit;

import java.lang.reflect.Field;
import java.math.BigInteger;

import static org.fw.FW.symbol;

public final class JValConstGetter {
    public static final Val jval = FW.telephonist("jval", (arg, context) -> {
        if (arg.type().equals(ExprCallOpFw.exprCallOp)) {
            Val size = arg.call(symbol("size"), context);
            Val cEnv = arg.call(symbol("comp-env"), context);
            int isize = size._unpack(BigInteger.class).intValue();
            if (isize != 2) {
                return Val.unspecified;
            }

            Val retVit = cEnv.call(CompEnv.syntaxResolve(arg.call(DIntFw.dint(0), context)._unpack(), CompEnv.of(cEnv)), context);
            if (!VitFw.isVit(retVit.type()))
                return retVit; // compile error idk

            Vit nameVit = VitFw.unwrap(retVit);
            assert nameVit != null;

            Val nameVal = nameVit.eval(context);
            if (!nameVal.type().equals(StrFw.str))
                return Val.unspecified;

            String name = nameVal._unpack();

            Val fretVit = cEnv.call(CompEnv.syntaxResolve(arg.call(DIntFw.dint(1), context)._unpack(), CompEnv.of(cEnv)), context);
            if (!VitFw.isVit(fretVit.type()))
                return fretVit; // compile error idk

            Vit fnameVit = VitFw.unwrap(fretVit);
            assert fnameVit != null;

            Val fnameVal = fnameVit.eval(context);
            if (!fnameVal.type().equals(StrFw.str))
                return Val.unspecified;

            String fieldName = fnameVal._unpack();
            try {
                Class<?> cls = Class.forName("org.fw." + name);
                Field field = cls.getField(fieldName);
                field.setAccessible(true);
                Object ret = field.get(null);

                if (ret instanceof Type type) ret = type.asVal();
                if (ret == null)
                    throw new NullPointerException("Not initialized yet: " + fieldName + " in " + name);

                return VitFw.wrap(Vit.val((Val)ret));
            } catch (ClassNotFoundException | NoSuchFieldException | IllegalAccessException e) {
                throw new RuntimeException(e);
            }

        }
        return Val.unspecified;
    });
}
