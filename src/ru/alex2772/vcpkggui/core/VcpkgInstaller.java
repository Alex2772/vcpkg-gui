package ru.alex2772.vcpkggui.core;

import ru.alex2772.vcpkggui.VcpkgGui;
import ru.alex2772.vcpkggui.ui.ProgressDialog;
import ru.alex2772.vcpkggui.util.FileUtil;
import ru.alex2772.vcpkggui.util.OSUtil;
import ru.alex2772.vcpkggui.util.ProcessUtil;

import java.io.File;
import java.util.concurrent.TimeUnit;

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
                {
                    Process proc = new ProcessBuilder("git", "clone", "https://github.com/Microsoft/vcpkg")
                            .redirectOutput(ProcessBuilder.Redirect.PIPE)
                            .redirectError(ProcessBuilder.Redirect.PIPE)
                            .start();

                    ProcessUtil.waitOrException(proc, 60);
                    ProcessUtil.ensureZeroExitCode(proc);
                }

                pd.displayStateAsync(50, "Compiling");

                // do compilation
                {
                    Process proc = new ProcessBuilder(OSUtil.isWindows() ? "bootstrap-vcpkg.bat" : "./bootstrap-vcpkg.sh")
                            .directory(vcpkgDir)
                            //.redirectOutput(ProcessBuilder.Redirect.PIPE)
                            .redirectError(ProcessBuilder.Redirect.PIPE)
                            .start();

                    ProcessUtil.waitOrException(proc, 300);
                    ProcessUtil.ensureZeroExitCode(proc);
                }
            }

            @Override
            public void onSuccess() {
                VcpkgGui.getMainWindow().setVisible(true);
            }
        });

    }
}
