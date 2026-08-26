package com.ydo4ki.fw.lib.memlib;

import org.fw.core.FW;
import org.fw.core.ast.Expr;
import org.fw.core.ast.Symbol;
import org.fw.core.base.Type;
import org.fw.core.base.Val;
import org.fw.lib.stdlib.DeclaredFw;
import org.fw.lib.stdlib.Lib;
import org.fw.lib.stdlib.ModuleFw;
import org.fw.lib.stdlib.VitFw;
import org.fw.lib.stdlib.expr.SyntaxResolveFw;
import com.ydo4ki.fw.lib.memlib.ints.IntTypeFw;
import com.ydo4ki.fw.lib.memlib.words.BinOperationsFw;
import com.ydo4ki.fw.lib.memlib.words.BitFw;
import com.ydo4ki.fw.lib.devicelib.PrimitiveLayoutsFw;
import org.fw.core.util.bits.Bits;
import org.fw.core.vit.Vit;

import java.util.BitSet;

import static org.fw.core.FW.symbol;

public final class MemLib {
    public static final Val parseReifiedBits = FW.telephonist((arg) -> {
        if (arg.type().equals(SyntaxResolveFw.syntaxResolve)) {
            Val exprVal = arg.call(symbol("expr"));
            Val compEnv = arg.call(symbol("comp-env"));
            Expr expr = exprVal._unpack();
            if (!(expr instanceof Symbol))
                return null;

            String text = ((Symbol) expr).getValue();
            if (text.startsWith("b")) {
                BitSet bitset = new BitSet();
                int size = text.length() - 1;
                if (size <= 0)
                    return null;

                for (int i = 0; i < size; i++) {
                    char c = text.charAt(i + 1);
                    boolean b;
                    if (c == '0') b = false;
                    else if (c == '1') b = true;
                    else return null;
                    bitset.set(i, b);
                }
                Type type = ReifiedTypeFw.reifiedType(BitFw.bit, size);
                Bits bits = Bits.of(bitset, size);
                return VitFw.wrap(Vit.val(MemUtils.wrap(type, bits)));
            } else if (text.startsWith("0x")) {
                int hexSize = text.length() - 2;
                if (hexSize <= 0 || (hexSize & 1) != 0)
                    return null;

                int size = hexSize / 2;
                byte[] bytes = new byte[size];

                for (int i = 0; i < size; i++) {
                    int hi = Character.digit(text.charAt(2 + i * 2), 16);
                    int lo = Character.digit(text.charAt(3 + i * 2), 16);

                    if (hi < 0 || lo < 0)
                        return null;

                    bytes[i] = (byte) ((hi << 4) | lo);
                }

                Type type = ReifiedTypeFw.reifiedType(PrimitiveLayoutsFw.octet, size);
                return VitFw.wrap(Vit.val(MemUtils.wrap(type, Bits.of(bytes))));
            }

        }
        return null;
    });

    public static final Lib lib = Lib.combine(
            Lib.of(
                    ModuleFw.module(
                            DeclaredFw.declared(symbol("Bit"), BitFw.bit.asVal()),
                            DeclaredFw.declared(symbol("ReifiedType"), ReifiedTypeFw.reifiedType.asVal()),
                            DeclaredFw.declared(symbol("bitor"), BinOperationsFw.or),
                            DeclaredFw.declared(symbol("bitand"), BinOperationsFw.and),
                            DeclaredFw.declared(symbol("bitxor"), BinOperationsFw.xor),
                            DeclaredFw.declared(symbol("bitnot"), BinOperationsFw.not)
                    ),
                    parseReifiedBits
            ),
            IntTypeFw.lib
    );
}
