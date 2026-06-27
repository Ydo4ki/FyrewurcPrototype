package org.fw.core.cases;

import org.fw.core.FW;
import org.fw.core.util.FwUtils;
import org.fw.core.base.Context;
import org.fw.core.base.Type;
import org.fw.core.base.Val;
import org.fw.core.lib.*;
import org.fw.core.lib.comp.DVecConstructorCEnvFw;
import org.fw.core.lib.comp.InvokeFuncCEnvFw;
import org.fw.core.lib.comp.ModuleCEnvFw;
import org.fw.core.lib.comp.ParseDIntCEnvFw;
import org.fw.core.lib.constraint.ConstraintFw;
import org.fw.core.lib.expr.CompEnv;
import org.fw.core.lib.expr.ToExprFn;
import org.fw.core.state.obj.Scope;
import org.fw.core.vit.RtEnv;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.Collections;

import static org.fw.core.FW.symbol;

public class LocSerial {
    public static void main(String[] args) throws IOException {
        File saves = new File(".saves");
        saves.mkdirs();
        File file = new File(saves, "locations.fw");


        GameLoc[] locations = loadLocations(file);

        System.out.println(Arrays.toString(locations));

        GameLoc[] locations1 = Arrays.copyOf(locations, locations.length + 1);
        locations1[locations1.length - 1] = new GameLoc(-345983745, 194, 38);

        saveLocations(locations1, file);
    }


    static final Type loc = StructFw.struct(
            DeclarationFw.declaration(symbol("world"), ConstraintFw.toConstraint(DIntFw.dint.asVal())),
            DeclarationFw.declaration(symbol("x"), ConstraintFw.toConstraint(DIntFw.dint.asVal())),
            DeclarationFw.declaration(symbol("y"), ConstraintFw.toConstraint(DIntFw.dint.asVal()))
    );

    static final Val module = ModuleFw.module(
            DeclaredFw.declared(symbol("loc"), loc.asVal())
    );

    static final Context context = new Context(RtEnv.of(ModuleFw.module(
            DeclaredFw.declared(symbol("to-expr"), ToExprFn.toExpr)
    )), Scope.eternal());

    static final CompEnv compEnv = CompEnv.of(CompEnv.compEnv(context,
            ModuleCEnvFw.compEnv(module),
            InvokeFuncCEnvFw.invokeFuncCenv,
            DVecConstructorCEnvFw.dVecConstructorCenv,
            ParseDIntCEnvFw.parseNumCenv
    ));

    public static GameLoc[] loadLocations(File file) throws IOException {
        Val val = FwUtils.getValueFromFile(file, compEnv, context);

        int len = DIntFw.unwrap(val.call(symbol("size"), context)).intValue();
        GameLoc[] locations = new GameLoc[len];
        for (int i = 0; i < len; i++) {
            Val locI = val.call(DIntFw.dint(i), context);
            locations[i] = new GameLoc(
                    DIntFw.unwrap(locI.call(symbol("world"), context)).longValue(),
                    DIntFw.unwrap(locI.call(symbol("x"), context)).intValue(),
                    DIntFw.unwrap(locI.call(symbol("y"), context)).intValue()
            );
        }
        return locations;
    }

    public static void saveLocations(GameLoc[] locs, File file) throws IOException {
        Val converter = FW.telephonist((arg, context1) -> {
            if (arg.asType().equals(loc)) return symbol("loc");
            return ToExprFn.toExpr.call(arg, context1);
        });

        Context context1 = new Context(RtEnv.of(ModuleFw.module(
                DeclaredFw.declared(symbol("to-expr"), converter)
        )), Scope.eternal());

        Val[] values = new Val[locs.length];
        for (int i = 0; i < values.length; i++) {
            GameLoc gl = locs[i];
            values[i] = StructFw.instance(loc,
                    DIntFw.dint(gl.world),
                    DIntFw.dint(gl.x),
                    DIntFw.dint(gl.y)
            );
        }

        Val dvec = DVecFw.vec(values);

        String string = dvec.toExpr(context1).toString();

        Files.write(file.toPath(), Collections.singleton(string));
    }


    static class GameLoc {
        public final long world;
        public final int x, y;

        public GameLoc(long world, int x, int y) {
            this.world = world;
            this.x = x;
            this.y = y;
        }

        @Override
        public String toString() {
            return "GameLoc{" +
                    "world=" + world +
                    ", x=" + x +
                    ", y=" + y +
                    '}';
        }
    }
}
