package com.ydo4ki.fyrewurc.lib.devicelib;

import org.fw.core.base.Type;
import org.fw.lib.elib.DeclaredFw;
import org.fw.lib.elib.Lib;
import org.fw.lib.elib.ModuleFw;
import com.ydo4ki.fyrewurc.lib.memlib.ReifiedTypeFw;
import com.ydo4ki.fyrewurc.lib.memlib.words.BitFw;

import static org.fw.core.FW.symbol;

public final class PrimitiveLayoutsFw {
    public static final Type octet = ReifiedTypeFw.reifiedType(BitFw.bit, 8);
    public static final Type word = ReifiedTypeFw.reifiedType(octet, 2);
    public static final Type dword = ReifiedTypeFw.reifiedType(word, 2);
    public static final Type qword = ReifiedTypeFw.reifiedType(word, 4);
    public static final Type dqword = ReifiedTypeFw.reifiedType(qword, 2);
    public static final Type oword = ReifiedTypeFw.reifiedType(word, 8); // no difference
    public static final Type yword = ReifiedTypeFw.reifiedType(octet, 32);
    public static final Type zword = ReifiedTypeFw.reifiedType(octet, 64);
    public static final Type vword = ReifiedTypeFw.reifiedType(octet, 128);
    public static final Type hword = ReifiedTypeFw.reifiedType(octet, 256);
    public static final Type xword = ReifiedTypeFw.reifiedType(octet, 512);

    public static final Type nibble = ReifiedTypeFw.reifiedType(BitFw.bit, 4);
    public static final Type triad = ReifiedTypeFw.reifiedType(octet, 3);
    public static final Type fword = ReifiedTypeFw.reifiedType(word, 3);
    public static final Type tword = ReifiedTypeFw.reifiedType(octet, 10);

    public static final Lib lib = Lib.ofModule(ModuleFw.module(
            DeclaredFw.declared(symbol("Octet"), octet),
            DeclaredFw.declared(symbol("Word"), word),
            DeclaredFw.declared(symbol("DWord"), dword),
            DeclaredFw.declared(symbol("QWord"), qword),
            DeclaredFw.declared(symbol("DQWord"), dqword),
            DeclaredFw.declared(symbol("OWord"), oword),
            DeclaredFw.declared(symbol("YWord"), yword),
            DeclaredFw.declared(symbol("ZWord"), zword),
            DeclaredFw.declared(symbol("VWord"), vword),
            DeclaredFw.declared(symbol("HWord"), hword),
            DeclaredFw.declared(symbol("XWord"), xword),

            DeclaredFw.declared(symbol("Nibble"), nibble),
            DeclaredFw.declared(symbol("Triad"), triad),
            DeclaredFw.declared(symbol("FWord"), fword),
            DeclaredFw.declared(symbol("TWord"), tword)
    ));
}
