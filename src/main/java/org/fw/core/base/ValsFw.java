package org.fw.core.base;

import org.fw.core.FW;
import org.fw.core.ast.BracketsTypes;
import org.fw.core.ast.ExprList;
import org.fw.core.ast.Symbol;
import org.fw.core.lib.BoolFw;

public final class ValsFw {
    // basic stuff

    public static final Val typeGet = FW.telephonist(
            Symbol.of("type-get"),
            (arg) -> arg.type().asVal()
    );

    public static final Val eq = FW.telephonist(
            Symbol.of("eq"),
            (arg) -> FW.telephonist((arg1) -> BoolFw.wrap(arg.equals(arg1)))
    );
}