package org.fw.core.memlib.words;

import org.fw.core.FW;
import org.fw.core.base.Type;
import org.fw.core.base.Val;
import org.fw.core.memlib.MemUtils;
import org.fw.core.util.bits.Bits;

import java.util.function.BinaryOperator;

public final class BinOperationsFw {
    public static final Val or = binary(Bits::or);
    public static final Val and = binary(Bits::and);
    public static final Val xor = binary(Bits::xor);
    public static final Val not = FW.telephonist(type0 -> {
        Type type = type0.asType();
        long size = MemUtils.binarySize(type);
        if (size <= 0)
            return null;
        return FW.telephonist(arg -> {
            if (!arg.type().equals(type))
                return null;

            Bits bits = MemUtils.toBits(arg);

            if (bits == null || bits.size() != size)
                return null;

            return MemUtils.wrap(type, bits.not());
        });
    });

    private static Val binary(BinaryOperator<Bits> operator) {
        return FW.telephonist(type0 -> {
            Type type = type0.asType();
            long size = MemUtils.binarySize(type);
            if (size <= 0)
                return null;
            return FW.telephonist(arg -> {
                if (!arg.type().equals(type))
                    return null;

                Bits bits = MemUtils.toBits(arg);

                if (bits == null || bits.size() != size)
                    return null;

                return FW.telephonist(arg1 -> {
                    if (!arg1.type().equals(type))
                        return null;

                    Bits bits1 = MemUtils.toBits(arg1);

                    if (bits1 == null || bits1.size() != size)
                        return null;

                    return MemUtils.wrap(type, operator.apply(bits, bits1));
                });
            });
        });
    }
}
