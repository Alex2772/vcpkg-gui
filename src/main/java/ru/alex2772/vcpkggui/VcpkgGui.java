package ru.alex2772.vcpkggui;

import ru.alex2772.vcpkggui.model.VcpkgPackage;
import ru.alex2772.vcpkggui.ui.MainWindow;

import java.util.logging.Logger;

public class VcpkgGui {
    private static MainWindow ourMainWindow;

    public static void main(String[] args) {
        initMainWindow();
    }

    public static MainWindow getMainWindow() {
        return ourMainWindow;
    }

    public static void initMainWindow() {
        ourMainWindow = new MainWindow();
    }

    public static Logger getLogger() {
        return Logger.getLogger("vcpkg");
    }

    public static void invalidateListInstalledPackages() {
        VcpkgPackage.invalidateListInstalledPackages();
        ourMainWindow.updateInstalledPackages();
    }
}
