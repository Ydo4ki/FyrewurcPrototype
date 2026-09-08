package com.ydo4ki.fw.internal.lib.jlib.data;

import org.fw.base.Type;
import org.fw.base.Val;
import com.ydo4ki.fw.internal.lib.jlib._internal.JClassFw;

public final class JBooleanFw {
    public static final Type jboolean = JClassFw.wrap(boolean.class).asType();

    public static Val wrap(Boolean b) {
        return b ? _true : _false;
    }

    public static final Val _true = Val._NEW_INSTANCE_(jboolean, true);
    public static final Val _false = Val._NEW_INSTANCE_(jboolean, false);
}
