package ru.alex2772.vcpkggui.util;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

public class ProcessUtil {
    /**
     * If process exit code is nonzero an exception thrown with process output.
     * @param p process
     */
    public static void ensureZeroExitCode(Process p) throws IOException, RuntimeException {
        if (p.exitValue() != 0) {
            throw new RuntimeException(new String(p.getErrorStream().readAllBytes()));
        }
    }


    /**
     * Waits for process. If timeout is expired, an exception is thrown.
     * @param process
     * @param timeoutInSec
     */
    public static void waitOrException(Process process, int timeoutInSec) throws Exception {
        if (!process.waitFor(timeoutInSec, TimeUnit.SECONDS)) {
            throw new RuntimeException("process timeout");
        }

    }
}
