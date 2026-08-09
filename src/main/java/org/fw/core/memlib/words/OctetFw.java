package org.fw.core.memlib.words;

import org.fw.core.base.Type;
import org.fw.core.memlib.ReifiedTypeFw;

public final class OctetFw {
    public static final Type octet = ReifiedTypeFw.reifiedType(BitFw.bit, 8);
}
