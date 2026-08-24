package com.ydo4ki.fyrewurc.lib.devicelib;

import org.fw.core.FW;
import org.fw.core.base.SymbolFw;
import org.fw.core.base.Type;
import org.fw.core.base.Val;
import org.fw.lib.elib.DIntFw;
import org.fw.lib.elib.WrapperTypeFw;

import java.time.Instant;
import java.time.temporal.ChronoField;

public final class EpochMilliFw {
    public static final Type epochMilli = WrapperTypeFw.wrapperType(PrimitiveLayoutsFw.dqword, FW.telephonist(instance -> FW.telephonist(raw_payload -> FW.telephonist(arg -> {
        long value = raw_payload._unpack(Long.class);
        if (arg.type() == SymbolFw.symbol) {
            String s = arg._unpack().toString();
            Instant date = Instant.ofEpochMilli(value);
            switch (s) {
                // nah I think we will still need an entire time library
                case "raw": return raw_payload;
                case "year": return DIntFw.dint(date.get(ChronoField.YEAR));
                case "month": return DIntFw.dint(date.get(ChronoField.MONTH_OF_YEAR));
                case "days": return DIntFw.dint(date.get(ChronoField.DAY_OF_MONTH));
                case "hours": return DIntFw.dint(date.get(ChronoField.HOUR_OF_DAY));
                case "minutes": return DIntFw.dint(date.get(ChronoField.MINUTE_OF_HOUR));
                case "seconds": return DIntFw.dint(date.get(ChronoField.SECOND_OF_MINUTE));
                case "milliseconds": return DIntFw.dint(date.get(ChronoField.MILLI_OF_SECOND));
            }
        }
        return null;
    }))), FW.telephonist(arg -> {
        if (arg.type() == SymbolFw.symbol) {
            String s = arg._unpack().toString();
            switch (s) {
                case "construct": return FW.telephonist(arg1 -> {
                    if (arg1.type() != PrimitiveLayoutsFw.dqword)
                        return null;

                    return Val.of(EpochMilliFw.epochMilli, arg1._unpack());
                });
            }
        }
        return null;
    }));
}
