package org.fw.memlib.lib;

import org.fw.core.FW;
import org.fw.core.base.Type;
import org.fw.core.base.Val;

public final class WordFw {
    public static final Type word = FW.telephonist("word", (arg, context) -> {
        return Val.unspecified;
    }).asType();

    public static Val wrap(short b) {
        return Val.of(word, b);
    }

    public static Short unwrap(Val val) {
        return val._unpack();
    }
}

