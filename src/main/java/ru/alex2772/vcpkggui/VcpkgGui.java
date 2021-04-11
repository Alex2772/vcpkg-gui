package ru.alex2772.vcpkggui;

import ru.alex2772.vcpkggui.model.VcpkgPackage;
import ru.alex2772.vcpkggui.ui.MainWindow;

import javax.swing.*;
import java.util.logging.Level;
import java.util.logging.Logger;

public class VcpkgGui {
    private static MainWindow ourMainWindow;

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            getLogger().log(Level.INFO, "Could not set LaF", e);
        }
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
