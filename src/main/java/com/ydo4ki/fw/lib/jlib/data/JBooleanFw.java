package com.ydo4ki.fw.lib.jlib.data;

import org.fw.core.base.Type;
import org.fw.core.base.Val;
import com.ydo4ki.fw.lib.jlib._internal.JClassFw;

public final class JBooleanFw {
    public static final Type jboolean = JClassFw.wrap(boolean.class).asType();

    public static Val wrap(Boolean b) {
        return b ? _true : _false;
    }

    public static final Val _true = Val.of(jboolean, true);
    public static final Val _false = Val.of(jboolean, false);
}
