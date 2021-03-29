package ru.alex2772.vcpkggui;

import ru.alex2772.vcpkggui.ui.MainWindow;

import java.util.logging.Logger;

public class VcpkgGui {
    private static MainWindow ourMainWindow = new MainWindow();

    public static void main(String[] args) {
        getMainWindow().setVisible(true);
    }

    public static MainWindow getMainWindow() {
        return ourMainWindow;
    }

    public static Logger getLogger() {
        return Logger.getLogger("vcpkg");
    }
}
