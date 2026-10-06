package org.fw.base;

import com.ydo4ki.callers.Callers;
import org.fw.core.FW;

import java.util.HashSet;
import java.util.Set;
import java.util.function.BiFunction;
import java.util.function.Function;

/**
 * Class for smoother transition from old APIs that used to be public, such as Val.of/newInstance and unpack
 */
public final class MagicPowers {

    static {
        try {
            Class.forName("org.fw.base.Val");
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    private static final MagicPowers THE_MAGIC_POWERS = new MagicPowers();

    /** caller sensitive */
    public static MagicPowers getMagicPowers() {
        if (isCertifiedWizard(Callers.getCallerClass()))
            return THE_MAGIC_POWERS;
        throw new SecurityException("You are not a certified wizard.");
    }

    private MagicPowers() {
        if (THE_MAGIC_POWERS != null) throw new SecurityException("There is only one instance of " + MagicPowers.class);
    }

    public Val newInstance(Type type, Object value) {
        return newInstance.apply(type, value);
    }

    public Object unpackVal(Val val) {
        return getValue.apply(val);
    }

    /* wizard verification */

    private static final Set<Class<?>> foreignCertificates = new HashSet<>();
    static {
        foreignCertificates.add(FW.class);
        foreignCertificates.add(Type.class);
        foreignCertificates.add(CallFw.class);
        foreignCertificates.add(DefinitiveValEnv.class);
        foreignCertificates.add(InstancerFw.class);
        foreignCertificates.add(UnpackerFw.class);
        foreignCertificates.add(EqFw.class);
        foreignCertificates.add(TelephonizeFw.class);
    }

    private static boolean isCertifiedWizard(Class<?> callerClass) {
        return foreignCertificates.contains(callerClass);
    }

    /* accessors */

    private static BiFunction<Type, Object, Val> newInstance;
    private static Function<Val, Object> getValue;

    static void registerValAccessors(
            BiFunction<Type, Object, Val> newInstance,
            Function<Val, Object> getValue
    ) {
        if (MagicPowers.newInstance != null || Callers.getCallerClass() != Val.class)
            throw new SecurityException("You could've just used reflection to set the field."); // also why

        MagicPowers.newInstance = newInstance;
        MagicPowers.getValue = getValue;
    }
}
