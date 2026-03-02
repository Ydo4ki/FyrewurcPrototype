package org.fw;

import org.fw.ast.BracketsTypes;
import org.fw.ast.Symbol;
import org.fw.ast.lexer.ExprOutput;
import org.fw.ast.lexer.TokenOutput;
import org.fw.base.Context;
import org.fw.base.Val;
import org.fw.lib.DIntFw;
import org.fw.lib.expr.ExprFw;
import org.fw.lib.StrFw;
import org.fw.state.obj.Scope;
import org.fw.vit.RtEnv;
import org.junit.jupiter.api.Test;

import static org.fw.FW.symbol;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class ExprTests {
    private static final Context context = new Context(RtEnv.unspecified, Scope.eternal());

    @Test
    void apiSymbol() {
        Val expr = ExprFw.wrap(Symbol.of("abalabamara"));
        assertEquals(StrFw.str("abalabamara"), expr.call(symbol("value"), context));
    }

    @Test
    void apiExprList() {
        Val expr = ExprFw.wrap(new ExprOutput(new TokenOutput("(+ 5 4)", null, BracketsTypes.bracketsTypes)).iterator().next());
        assertEquals(symbol("+"), expr.call(DIntFw.dint(0), context));
        assertEquals(symbol("5"), expr.call(DIntFw.dint(1), context));
        assertEquals(symbol("4"), expr.call(DIntFw.dint(2), context));

        assertEquals(Val.unspecified, expr.call(DIntFw.dint(55), context));

        assertEquals(DIntFw.dint(3), expr.call(symbol("size"), context));

        assertEquals(StrFw.str("()"), expr.call(symbol("brackets-type"), context));
    }

    @Test
    void exprExpr() {
        Val expr = ExprFw.wrap(new ExprOutput(new TokenOutput("(+ 5 4)", null, BracketsTypes.bracketsTypes)).iterator().next());
        assertEquals("(ExprList (Symbol \"+\") (Symbol \"5\") (Symbol \"4\"))", expr.toExpr(context).toString());

        expr = symbol("aaaaa????");
        assertEquals("(Symbol \"aaaaa????\")", expr.toExpr(context).toString());
    }
}
