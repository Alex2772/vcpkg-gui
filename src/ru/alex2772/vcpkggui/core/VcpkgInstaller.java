package ru.alex2772.vcpkggui.core;

import ru.alex2772.vcpkggui.VcpkgGui;
import ru.alex2772.vcpkggui.ui.ProgressDialog;
import ru.alex2772.vcpkggui.util.FileUtil;
import ru.alex2772.vcpkggui.util.OSUtil;
import ru.alex2772.vcpkggui.util.ProcessUtil;

import javax.swing.*;
import java.io.File;
import java.io.IOException;
import java.util.logging.Level;

public class VcpkgInstaller {


    /**
     * Downloads, compiles and installs the vcpkg program. Displays GUI dialog showing progress.
     * Required git and platform build tools
     */
    public static void install() {
        // we will try to install vcpkg using git
        new ProgressDialog("Installing vcpkg", new ProgressDialog.Callback() {
            @Override
            public void doInBackground(ProgressDialog pd) throws Exception {
                File vcpkgDir = new File("vcpkg");
                if (vcpkgDir.isDirectory()) {
                    FileUtil.deleteDirectory(vcpkgDir);
                }
                pd.displayStateAsync(-1, "Cloning repository");
                // git clone repo
                try {
                    Process proc = new ProcessBuilder(OSUtil.getGitExecutable(), "clone", "https://github.com/Microsoft/vcpkg")
                            .redirectOutput(ProcessBuilder.Redirect.PIPE)
                            .redirectError(ProcessBuilder.Redirect.PIPE)
                            .start();

                    ProcessUtil.waitOrException(proc, 120);
                    ProcessUtil.ensureZeroExitCode(proc);
                } catch (IOException e) {
                    throw new IllegalStateException("Looks like git is not installed. Download git: https://git-scm.com/downloads");
                }

                pd.displayStateAsync(50, "Compiling");

                // do compilation
                {
                    Process proc = new ProcessBuilder(OSUtil.isWindows() ? vcpkgDir.getAbsolutePath() + "\\bootstrap-vcpkg.bat" : "./bootstrap-vcpkg.sh")
                            .directory(vcpkgDir)
                            //.redirectOutput(ProcessBuilder.Redirect.PIPE)
                            .redirectError(ProcessBuilder.Redirect.PIPE)
                            .start();

                    ProcessUtil.outputToProgressDialog(pd, proc);
                    ProcessUtil.ensureZeroExitCode(proc);
                }
            }

            @Override
            public void onSuccess() {

                // try to get version
                Config.getConfig().mVcpkgLocation = "vcpkg";
                try {
                    String version = VcpkgHelper.getVersion();
                    if (version.equals("unknown") || version.isEmpty()) {
                        throw new RuntimeException(); // force to execute catch block
                    }

                    // when reached here, installation is ok
                    JOptionPane.showMessageDialog(null,
                            "Successful auto install. \n\n" +
                            "Version: " + version,
                            "Your vcpkg installation is now valid",
                            JOptionPane.INFORMATION_MESSAGE);


                    Config.getConfig().save();
                    VcpkgGui.initMainWindow();
                } catch (Exception e) {
                    // installation is bad, show error message
                    VcpkgGui.getLogger().log(Level.WARNING, "Bad auto install", e);
                    JOptionPane.showMessageDialog(null,
                                          "Bad auto install.\n\nError message: " + e.getMessage(),
                                          "Bad auto install",
                                           JOptionPane.ERROR_MESSAGE);
                }
            }
        });
    }

    private enum InstallationMessage {

        /**
         * If vcpkg location in config is empty, this message is displayed.
         */
        NOT_INSTALLED {
            @Override
            public String toString() {
                return "The vcpkg is not installed (vcpkg-gui is just gui wrapper around vcpkg)";
            }
        },

        /**
         * During normal vcpkg-gui execution something went wrong with vcpkg installation.
         */
        INSTALLATION_HAS_BROKEN_OR_MOVED {
            @Override
            public String toString() {
                return "Your current vcpkg installation has broken or moved.";
            }
        },

        /**
         * User explicitly specified existing installation and this installation is broken
         */
        SPECIFIED_INSTALLATION_IS_BROKEN {
            public Exception mException;


            @Override
            public String toString() {
                return "The folder you specified either does not contain vcpkg executable or contains a broken vcpkg " +
                        "installation. You should either try again or do auto install.\n\n" +
                        
                        "Error message: " + mException.getMessage();
            }

            @Override
            public void setException(Exception e) {
                mException = e;
            }
        };

        public void setException(Exception e) {}
    }

    /**
     * \brief Show the vcpkg installation dialog which offers either to auto install vcpkg or specify already existing
     *        installation.
     */
    public static void showInstallationDialog() {
        InstallationMessage message = Config.getConfig().mVcpkgLocation.isEmpty() ?
                                      InstallationMessage.NOT_INSTALLED : InstallationMessage.INSTALLATION_HAS_BROKEN_OR_MOVED;

        // user can specify invalid path so using endless loop
        for (;;) {
            int n = JOptionPane.showOptionDialog(VcpkgGui.getMainWindow(),

                    message +
                            "\n\nWould you like to install vcpkg automatically? git and platform build tools required.",
                    "No valid vcpkg",
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
                    return;

                case JOptionPane.NO_OPTION: // specify existing vcpkg
                    JFileChooser j = new JFileChooser();
                    j.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
                    if (j.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
                        File selectedFile = j.getSelectedFile();
                        Config.getConfig().mVcpkgLocation = selectedFile.getPath();

                        // try to get version
                        try {
                            String version = VcpkgHelper.getVersion();
                            if (version.equals("unknown") || version.isEmpty()) {
                                throw new RuntimeException(); // force to execute catch block
                            }

                            // when reached here, installation is ok
                            JOptionPane.showMessageDialog(null,
                                                          "You have specified valid vcpkg installation. \n\n" +
                                                           "Version: " + version,
                                                          "Your vcpkg installation is now valid",
                                                           JOptionPane.INFORMATION_MESSAGE);


                            Config.getConfig().save();
                            VcpkgGui.initMainWindow();
                            return;
                        } catch (Exception e) {
                            // installation is bad, show the dialog again with specified installation is broken message
                            message = InstallationMessage.SPECIFIED_INSTALLATION_IS_BROKEN;
                            message.setException(e);
                            continue;
                        }

                    } else {
                        System.exit(0);
                    }




                case JOptionPane.CANCEL_OPTION: // close option
                    System.exit(0);
                    return;

            }
        }
    }
}
