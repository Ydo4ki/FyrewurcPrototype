package org.fw.base;

import org.fw.core.util.FwUtils;

@Deprecated // todo: replace with other implementations of value
public final class Unspecified {
    public static final Val isUnspecified = FwUtils.valify(val -> val.asVal(null) == null);

}
