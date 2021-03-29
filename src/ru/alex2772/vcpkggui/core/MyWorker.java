package ru.alex2772.vcpkggui.core;


import ru.alex2772.vcpkggui.Util;
import ru.alex2772.vcpkggui.VcpkgGui;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.concurrent.ExecutionException;
import java.util.logging.Level;

/**
 * This class solves the problem of error handling. When known exception type is thrown by the <code>myDone</code>
 * function, the error message displayed to the user with problem resolution hints.
 * @param <T> The type your <code>doInBackground</code> should return.
 */
public abstract class MyWorker<T> extends SwingWorker<T, Object> {
    @Override
    protected void done() {
        try {
            myDone();
        } catch (ExecutionException e) {
            VcpkgGui.getLogger().log(Level.WARNING, "You have probably not installed the vcpkg", e);
            int n = JOptionPane.showOptionDialog(VcpkgGui.getMainWindow(),
                    """
                            The vcpkg is not installed (vcpkg-gui is just gui wrapper around vcpkg).
                            Would you like to process to the vcpkg website to download it?
                            vcpkg-gui will be closed.""",
                    "vcpkg is not installed",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.QUESTION_MESSAGE,
                    null,
                    new String[]{"Yes, process", "No, just exit"},
                    null);

            switch (n) {
                case JOptionPane.YES_OPTION:
                    Util.openUrl("https://docs.microsoft.com/en-us/cpp/build/vcpkg?view=msvc-160#how-to-get-and-use-vcpkg");

                    // fallthrough is ok
                case JOptionPane.NO_OPTION:
                    System.exit(0);
                    break;

            }
        } catch (Exception e) {
            VcpkgGui.getLogger().log(Level.WARNING, "Unhandled exception in MyWorker", e);
        }
    }

    protected abstract void myDone() throws Exception;
}
