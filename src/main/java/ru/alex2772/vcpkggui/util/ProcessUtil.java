package ru.alex2772.vcpkggui.util;

import ru.alex2772.vcpkggui.VcpkgGui;
import ru.alex2772.vcpkggui.ui.ProgressDialog;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;

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
     * @param process process
     * @param timeoutInSec timeout in seconds
     */
    public static void waitOrException(Process process, int timeoutInSec) throws Exception {
        if (!process.waitFor(timeoutInSec, TimeUnit.SECONDS)) {
            throw new RuntimeException("process timeout");
        }

    }


    /**
     * When process outputs a line, a status label updated on ProgressDialog
     * @param pd ProgressDialog to update status label on
     * @param process process
     */
    public static void outputToProgressDialog(ProgressDialog pd, Process process) throws Exception {
        BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));

        while (reader.ready() || process.isAlive()) {
            String line = reader.readLine();
            if (line != null && !line.isEmpty()) {
                VcpkgGui.getLogger().log(Level.INFO, process + ": " + line);
                pd.displayStateAsync(line);
            }
        }
        reader.close();
    }
}
