package org.fw.core.state.obj;

import org.fw.core.base.Context;
import org.fw.core.base.Val;
import org.fw.core.lib.StrFw;

import java.util.Scanner;

//public final class ObjSystemConsole extends AbstractObj {
//
//    public ObjSystemConsole(State owner) {
//        super(owner);
//    }
//
//    @Override
//    public Val read(Context context) {
//        return StrFw.str(new Scanner(System.in).nextLine());
//    }
//
//    @Override
//    public void write(Context context, Val x) {
//        if (x.type().equals(StrFw.str)) {
//            System.out.println(x._unpack(String.class));
//        }
//        // just for debugging
////        else {
////            System.out.println(x.toExpr(context));
////        }
//    }
//}
