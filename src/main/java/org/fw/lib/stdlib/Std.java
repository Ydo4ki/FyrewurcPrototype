package org.fw.lib.stdlib;

import org.fw.core.FW;
import org.fw.core.base.*;
import org.fw.core.commons.ValAdapter;
import org.fw.core.vit.VitVal;
import org.fw.lib.stdlib.dvec.DVecFw;
import org.fw.lib.stdlib.state.LaserPointerFw;
import org.fw.lib.stdlib.state.OperationFw;
import org.fw.lib.stdlib.state.ScopeFw;
import org.fw.lib.stdlib.state.StatePointerFw;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;

public final class Std {
    public static final Val eq = EqFw.eq;
    public static final Val typeGet = TypeGetFw.typeGet;
    public static final Type call = CallFw.call_t;
    public static final Type telephonist = Val.ofTelephonist(0).asType();
    public static final Type symbol = SymbolFw.symbol;
    public static final Type bool = BoolFw.bool;
    public static final Val $true = BoolFw._true;
    public static final Val $false = BoolFw._false;

    public static final Type boxType = BoxFw.boxType;
    public static final Type dVec = DVecFw.dVec;
    public static final Type declared = DeclaredFw.declared;
    public static final Type module = ModuleFw.module;
    public static final Type vitVal = VitFw.vitVal;
    public static final Type vitVar = VitFw.vitVar;
    public static final Type vitCall = VitFw.vitCall;
    public static final Type vitInvoke = VitFw.vitInvoke;
    public static final Type constraint = ConstraintFw.constraint;
    public static final Type struct = StructFw.struct;
    public static final Type $enum = EnumFw.enumeration;
    public static final Type chainLinkType = ChainLinkFw.chainLinkType;
    public static final Type chainResolveType = ChainResolveFw.chainResolveType;
    public static final Type function = FunctionFw.function;

    public static final Type operation = OperationFw.operation;
    public static final Type scope = ScopeFw.scopePointer;
    public static final Type statePointer = StatePointerFw.statePointer;
    public static final Type laserPointer = LaserPointerFw.laserPointer;

    public static final Val std = modulerize();

    private static Val modulerize() {
        Field[] fields = Std.class.getFields();
        List<Val> declareds = new ArrayList<>();
        for (Field field : fields) {
            if (!Modifier.isStatic(field.getModifiers()))
                continue;
            String name = field.getName();

            if (name.equals("std"))
                continue;

            if (name.startsWith("$"))
                name = name.substring(1);

            if (field.getType() == Type.class) {
                char[] chars = name.toCharArray();
                chars[0] = Character.toUpperCase(chars[0]);
                name = String.valueOf(chars);
            } else {
                name = camelCaseTo_fw(name);
            }

            try {
                Object v = field.get(null);
                Val val = v instanceof ValAdapter ? ((ValAdapter) v).asVal() : (Val)v;
                declareds.add(DeclaredFw.declared(FW.symbol(name), val));
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            }
        }
        return ModuleFw.module(declareds.toArray(new Val[0]));
    }

    private static String camelCaseTo_fw(String fieldName) {
        StringBuilder result = new StringBuilder();
        char[] charArray = fieldName.toCharArray();
        for (int i = 0; i < charArray.length; i++) {
            char c = charArray[i];
            if (Character.isUpperCase(c)) {
                if (i != 0) result.append("-");
                result.append(Character.toLowerCase(c));
            } else {
                result.append(c);
            }
        }
        return result.toString();
    }

}
