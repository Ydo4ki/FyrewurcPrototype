package org.fw.core.state.operation;

import org.fw.core.base.Context;
import org.fw.core.base.Val;
import org.fw.core.lib.DIntFw;
import org.fw.core.lib.StrFw;
import org.fw.core.state.obj.State;

import java.io.PrintStream;
import java.util.Scanner;

public abstract class SystemOperation extends Operation {

    public static final State systemState = State.eternal();

    SystemOperation() {}

    @Override
    public final Val execute(Context context) {
        // errr ok I'm not sure how to determine if that's a system context or not
        // and it's not like it will be much useful later
        // I should probably create a random instance and call it a system context
        if (context.state() != systemState) {
            return Val.unspecified;
        }
        return execute0();
    }

    protected abstract Val execute0();


    public static class PrintOperation extends SystemOperation {
        private final PrintStream out;
        private final String string;

        public PrintOperation(PrintStream out, String string) {
            this.out = out;
            this.string = string;
        }

        @Override
        public Val execute0() {
            out.print(string);
            return Operation.unit;
        }
    }

    public static class ReadLineOperation extends SystemOperation {
        private final Scanner scanner;

        public ReadLineOperation(Scanner scanner) {
            this.scanner = scanner;
        }

        @Override
        protected Val execute0() {
            return StrFw.str(scanner.nextLine());
        }
    }

    public static class ThreadSleepOperation extends SystemOperation {
        private final long millis;

        public ThreadSleepOperation(long millis) {
            this.millis = millis;
        }

        @Override
        protected Val execute0() {
            try {
                Thread.sleep(millis, 0);
            } catch (InterruptedException e) {
                return Val.unspecified;
            }
            return Operation.unit;
        }
    }

    public static final Operation currentTimeMillis = new SystemOperation() {
        @Override
        protected Val execute0() {
            return DIntFw.dint(System.currentTimeMillis());
        }
    };

    public static final Operation nanoTime = new SystemOperation() {
        @Override
        protected Val execute0() {
            return DIntFw.dint(System.nanoTime());
        }
    };
}
