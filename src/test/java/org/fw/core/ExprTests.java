package org.fw.core;

import org.fw.core.ast.BracketsTypes;
import org.fw.core.ast.Symbol;
import org.fw.core.ast.lexer.ExprOutput;
import org.fw.core.ast.lexer.TokenOutput;
import org.fw.core.base.context.Context;
import org.fw.core.base.Val;
import org.fw.core.cases.Main;
import org.fw.core.lib.DIntFw;
import org.fw.core.lib.expr.ExprFw;
import org.fw.core.lib.StrFw;
import org.fw.core.state.obj.State;
import org.junit.jupiter.api.Test;

import static org.fw.core.FW.symbol;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class ExprTests {
    private static final Context context = new Context(Main.rtEnv, State.eternal());

    @Test
    void apiSymbol() {
        Val expr = ExprFw.wrap(Symbol.of("abalabamara"));
        assertEquals(StrFw.str("abalabamara"), ExprFw.symbolToString.call(expr, context));
    }

    @Test
    void apiExprList() {
        Val expr = ExprFw.wrap(new ExprOutput(new TokenOutput("(+ 5 4)", null, BracketsTypes.bracketsTypes)).iterator().next().getExpr());
        assertEquals(symbol("+"), expr.call(DIntFw.dint(0), context));
        assertEquals(symbol("5"), expr.call(DIntFw.dint(1), context));
        assertEquals(symbol("4"), expr.call(DIntFw.dint(2), context));

//        assertEquals(Unspecified.unspecified, expr.call(DIntFw.dint(55), context));

        assertEquals(DIntFw.dint(3), expr.call(symbol("size"), context));

        assertEquals(StrFw.str("()"), expr.call(symbol("brackets-type"), context));
    }

    @Test
    void exprExpr() {
        Val expr = ExprFw.wrap(new ExprOutput(new TokenOutput("(+ 5 4)", null, BracketsTypes.bracketsTypes)).iterator().next().getExpr());
        assertEquals("(ExprList (Symbol \"+\") (Symbol \"5\") (Symbol \"4\"))", expr.toExpr(context).toString());

        expr = symbol("aaaaa????");
        assertEquals("(Symbol \"aaaaa????\")", expr.toExpr(context).toString());
    }
}
