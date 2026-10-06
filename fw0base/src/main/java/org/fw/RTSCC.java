package org.fw;

import org.fw.base.*;

public final class RTSCC {

    private RTSCC() {}

    public static final Val type = TypeGetFw.typeGet;

    public static final Type Telephonist = Val.ofTelephonist(0).asType();

    public static final Val telephonize = TelephonizeFw.telephonize;
}
