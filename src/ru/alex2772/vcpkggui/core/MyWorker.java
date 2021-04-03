package ru.alex2772.vcpkggui.core;


import ru.alex2772.vcpkggui.VcpkgGui;

import javax.swing.*;
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
            int n = JOptionPane.showOptionDialog(VcpkgGui.getMainWindow(),
                        Config.getConfig().mVcpkgLocation.isEmpty() ?
                            """
                            The vcpkg is not installed (vcpkg-gui is just gui wrapper around vcpkg).
                            
                            Would you like to install vcpkg automatically? git and platform build tools required.                
                            """
                            :
                            """
                            Your current vcpkg installation has broken or moved.
                            
                            Would you like to install vcpkg automatically? git and platform build tools required.   
                            """,
                    "vcpkg is not installed",
                    JOptionPane.YES_NO_CANCEL_OPTION,
                    JOptionPane.QUESTION_MESSAGE,
                    null,
                    new String[]{
                            "Yes, install automatically",
                            "No, I want to specify existing vcpkg location",
                            "No, just exit"
                    },
                    null);

            switch (n) {
                case JOptionPane.YES_OPTION: // autoinstall
                    VcpkgInstaller.install();
                    break;

                case JOptionPane.NO_OPTION: // specify existing vcpkg
                    JFileChooser j = new JFileChooser();
                    j.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
                    Integer opt = j.showOpenDialog(null);

                    break;

                case JOptionPane.CANCEL_OPTION: // close option
                    System.exit(0);
                    break;

            }
        } catch (Exception e) {
            onError(e);
            VcpkgGui.getLogger().log(Level.WARNING, "Unhandled exception in MyWorker", e);
        }
    }

    protected abstract void onError(Exception e);
    protected abstract void myDone() throws Exception;
}
