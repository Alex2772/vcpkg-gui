package ru.alex2772.vcpkggui.core;


import ru.alex2772.vcpkggui.VcpkgGui;

import javax.swing.*;
import java.io.File;
import java.io.IOException;
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
            onError(e);
            VcpkgGui.getLogger().log(Level.WARNING, "You have probably not installed the vcpkg", e);
            VcpkgInstaller.showInstallationDialog();
        } catch (Exception e) {
            onError(e);
            VcpkgGui.getLogger().log(Level.WARNING, "Unhandled exception in MyWorker", e);
        }
    }

    protected abstract void onError(Exception e);
    protected abstract void myDone() throws Exception;
}
