package org.fw.core.util;

import org.fw.core.FW;
import org.fw.core.ast.BracketsTypes;
import org.fw.core.ast.Expr;
import org.fw.core.ast.LocatedExpr;
import org.fw.core.ast.Symbol;
import org.fw.core.ast.lexer.ExprOutput;
import org.fw.core.ast.lexer.TokenOutput;
import org.fw.core.base.*;
import org.fw.core.base.BoolFw;
import org.fw.lib.elib.DeclaredFw;
import org.fw.core.base.ValsFw;
import org.fw.lib.elib.Lib;
import org.fw.lib.elib.ModuleFw;
import org.fw.lib.elib.VitFw;
import org.fw.lib.elib.constraint._Constraint;
import org.fw.lib.elib.expr.CompEnv;
import org.fw.core.state.obj.State;
import org.fw.core.state.operation.Operation;
import org.fw.core.base.context.RtEnv;
import org.fw.core.vit.Vit;
import org.fw.core.vit.VitCompilationException;
import org.fw.lib.elib.expr.ToExprFn;
import org.fw.lib.elib.state.SystemOperation;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigInteger;
import java.util.*;
import java.util.function.Predicate;

import static org.fw.core.FW.symbol;
import static org.fw.core.vit.Vit.val;
import static org.fw.core.vit.Vit.var;

// todo: replace all java File with FwFiles or something like that so they won't be attached to the actual filesystem
public final class FwUtils {
    private FwUtils() throws InstantiationException
        { throw new InstantiationException(); }


    @Deprecated
    public static Val handleSymbols(Val arg, Type type, SHandler handler, TelephonistType.CallFunction orStatic) throws Exception {
        return handleSymbols(arg, type, handler, (instance, arg1) -> null, orStatic);
    }

    @Deprecated
    public static Val handleSymbols(Val arg, Type type, SHandler handler, NSHandler nonSymbolicHandler, TelephonistType.CallFunction orStatic) throws Exception {
        if (isTypeApiCall(arg, type)) {
            Val instance = Call.getVal(arg);
            Val callArg = Call.getArg(arg);
            if (!callArg.type().equals(SymbolFw.symbol)) {
                return nonSymbolicHandler.handle(instance, callArg);
            }
            String symbol = callArg._unpack(Symbol.class).getValue();
            return handler.handle(instance, symbol);
        }
        return orStatic.call(arg);
    }

    public static LocatedExpr<? extends Expr> parse(String name) {
        return new ExprOutput(new TokenOutput(name, null, BracketsTypes.bracketsTypes)).iterator().next();
    }

    public static boolean isTypeApiCall(Val call, Type type) {
        if (call.type().equals(Call.call_t)) {
            Val val = Call.getVal(call);
            return val.type().equals(type);
        }
        return false;
    }

    public static boolean isTypeApiCall(_Constraint arg, Type type) {
        _Constraint argType = arg.typeConstraint();
        if (argType.implies(_Constraint.equals(Call.call_t.asVal()))) {
            _Constraint valc = arg.call(_Constraint.equals(symbol("val")));
            _Constraint valType = valc.typeConstraint();
            return valType.implies(_Constraint.equals(type.asVal()));
        }
        return false;
    }

    public static Vit isTypeApiCall(Vit call, Type type) {
        return ValsFw.eq(Vit.call(ValsFw.typeGet, call), Vit.val(Call.call_t.asVal()))
                .call(symbol("and"))
                .call(ValsFw.eq(Vit.call(ValsFw.typeGet, call.call(symbol("val"))), Vit.val(type.asVal())));
    }

    public static Val getValueFromFile(File file, CompEnv compEnv) throws IOException {
        return State.performAndDie(s -> {
            try {
                return getValueFromFile(file, compEnv, RtEnv.unspecified, s);
            } catch (IOException e) {
                sneakyThrow(e);
                return null;
            }
        });
    }

    @SuppressWarnings("unchecked")
    private static <T extends Throwable> void sneakyThrow(Throwable t) throws T {
        throw (T) t;
    }

    public static Val getValueFromFile(File file, CompEnv compEnv, RtEnv rtEnv, State state) throws IOException {
        Iterable<LocatedExpr<? extends Expr>> expressions = new ExprOutput(new TokenOutput(file, BracketsTypes.bracketsTypes));
        Val result = Operation.unit; // this will be returned if the file is empty


        Map<String, Val> defineds = new HashMap<>();

        final Val defined = symbolMapVitEnv(val(FW.telephonist("vals", (arg1) -> {
            if (!arg1.type().equals(SymbolFw.symbol))
                return null;
            String string = arg1._unpack().toString();
            Val ret = defineds.get(string);
            if (ret != null)
                return VitFw.wrap(val(ret));
            return null;
        })));


        CompEnv env = CompEnv.of(CompEnv.compEnv(compEnv.asVal(), defined));

        for (LocatedExpr<? extends Expr> lExpr : expressions) {
            Expr expr = lExpr.getExpr();
            Vit vit = null;
            try {
                vit = env.compile(expr);
            } catch (VitCompilationException e) {
                throw new RuntimeException("Cannot compile: " + expr, e);
            }
            result = vit.eval(rtEnv, state);
            if (result.type().equals(DeclaredFw.declared)) {
                Val key = DeclaredFw.getKey(result);
                Val value = DeclaredFw.getValue(result);
                if (key.type().equals(SymbolFw.symbol)) {
                    defineds.put(key._unpack(Symbol.class).getValue(), value);
                }
            }
        }
        return result;
    }

    public static <T> Set<T> mergeImmut(Set<T> a, Set<T> b) {
        if (a.isEmpty()) return b;
        if (b.isEmpty()) return a;
        Set<T> set = new HashSet<>(a);
        set.addAll(b);
        return set;
    }

    public static Val valify(Predicate<Val> tester) {
        return FW.telephonist((arg) -> BoolFw.wrap(tester.test(arg)));
    }

    public static Vit equals(Vit a, Vit b) {
        return val(ValsFw.eq).call(a).call(b);
    }

    public static Val symbolMapVitEnv(Vit telemap) {
        Vit arg = var(symbol("arg"));
        Vit argExpr = arg.call(symbol("expr"));
        Vit parseArg = telemap.call(argExpr);
        return FW.telephonist((arg1) -> {
            if (Unspecified.isUnspecified(arg1)) return null;
            else return parseArg.eval();
        });
//        return VitiateTelephonistFw.vitiate(
//                FW.vIf(val(eq).call(parseArg).call(null).call(symbol("not")),
//                        parseArg,
//                        val(null)
//                ), symbol("arg"), InternalSystemContext.context);
    }

    public static Operation getOperation(Class<?> cls, String filename, final CompEnv compEnv) throws IOException {
        return getOperation(cls.getPackage().getName().replace(".", "/") + "/" + filename, compEnv);
    }

    public static Operation getOperation(String filename, final CompEnv compEnv) throws IOException {
        InputStream in = Thread.currentThread().getContextClassLoader().getResourceAsStream(filename + ".fw");
        if (in == null)
            throw new IOException("Source not found: " + filename + ".fw");

        Iterable<LocatedExpr<? extends Expr>> expressions = ExprOutput.valueOf(in);
        return new Operation() {
            @Override
            public Val apply(State state) {
                CompEnv compEnv1 = compEnv;
                Val val = Operation.unit;
                for (LocatedExpr<? extends Expr> locatedExpression : expressions) {
                    Expr expression = locatedExpression.getExpr();
                    Vit vit;
                    try {
                        vit = compEnv1.compile(expression);
                    } catch (VitCompilationException e) {
                        System.err.println(expression);
                        throw new RuntimeException(e);
                    }
                    val = vit.eval(RtEnv.unspecified, state);
                    if (val.type() == DeclaredFw.declared) {
                        compEnv1 = CompEnv.of(CompEnv.compEnv(ModuleFw.ModuleCEnvFw.compEnv(ModuleFw.module(val)), compEnv1.asVal()));
                    }
                    if (val != Operation.unit)
                        System.out.println(val.toExpr(ToExprFn.toExpr));
                }
                return val;
            }

            @Override
            protected boolean isPure0() {
                return false;
            }
        };
    }

    public static Lib l(Class<?> caller, Lib lib0, String... files) {
        try {
            for (String file : files) {
                lib0 = Lib.combine(lib0,
                        Lib.ofCEnv(ModuleFw.ModuleCEnvFw.compEnv(
                                getOperation(caller, file, CompEnv.of(lib0.exports()))
                                        .apply(SystemOperation.systemState)))
                );
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return lib0;
    }

    @FunctionalInterface
    public interface SHandler {
        Val handle(Val instance, String symbol);
    }

    @FunctionalInterface
    public interface NSHandler {
        Val handle(Val instance, Val arg);
    }

    public interface BoolBinaryOperator {
        boolean apply(boolean a, boolean b);
    }

    public interface BigBinaryOperator {
        BigInteger apply(BigInteger a, BigInteger b);
    }
}
