package com.ydo4ki.fw.internal.lib.stdlib.state;

import org.fw.core.base.Val;
import org.fw.core.state.operation.Operation;
import com.ydo4ki.fw.internal.lib.stdlib.DIntFw;
import com.ydo4ki.fw.internal.lib.stdlib.StrFw;
import org.fw.core.state.obj.State;

import java.io.PrintStream;
import java.util.Scanner;

public abstract class SystemOperation extends Operation {

    public static final State systemState = State.eternal();

    public SystemOperation() {}

    @Override
    public final Val apply(State state) {
        // errr ok I'm not sure how to determine if that's a system context or not
        // and it's not like it will be much useful later
        // I should probably create a random instance and call it a system context
        if (state != systemState) {
            return Operation.unit;
        }
        return apply0();
    }

    protected abstract Val apply0();

    public static class FlushOperation extends SystemOperation {
        private final PrintStream out;

        public FlushOperation(PrintStream out) {
            this.out = out;
        }

        @Override
        protected Val apply0() {
            out.flush();
            return Operation.unit;
        }
    }

    public static class ReadLineOperation extends SystemOperation {
        private final Scanner scanner;

        public ReadLineOperation(Scanner scanner) {
            this.scanner = scanner;
        }

        @Override
        protected Val apply0() {
            return StrFw.str(scanner.nextLine());
        }
    }

    public static class ThreadSleepOperation extends SystemOperation {
        private final long millis;

        public ThreadSleepOperation(long millis) {
            this.millis = millis;
        }

        @Override
        protected Val apply0() {
            try {
                Thread.sleep(millis, 0);
            } catch (InterruptedException e) {
                return Operation.unit;
            }
            return Operation.unit;
        }
    }

    public static final Operation currentTimeMillis = new SystemOperation() {
        @Override
        protected Val apply0() {
            return DIntFw.dint(System.currentTimeMillis());
        }
    };

    public static final Operation nanoTime = new SystemOperation() {
        @Override
        protected Val apply0() {
            return DIntFw.dint(System.nanoTime());
        }
    };
}
