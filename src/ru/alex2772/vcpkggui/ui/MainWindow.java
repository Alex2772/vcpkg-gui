package ru.alex2772.vcpkggui.ui;

import ru.alex2772.vcpkggui.VcpkgGui;
import ru.alex2772.vcpkggui.core.MyWorker;
import ru.alex2772.vcpkggui.core.VcpkgHelper;
import ru.alex2772.vcpkggui.model.PackageTableModel;
import ru.alex2772.vcpkggui.model.VcpkgPackage;

import javax.swing.*;
import java.util.ArrayList;
import java.util.List;

public class MainWindow extends JFrame {

    private JPanel root;
    private JLabel version;
    private JTabbedPane tabbedPane1;
    private JTable installedTable;

    public MainWindow() {
        super("vcpkg-gui");
        setContentPane(root);
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        // center window
        setSize(800, 600);
        setLocationRelativeTo(null);

        setVisible(true);

        installedTable.setVisible(false);

        updateVersion();
        updateInstalledPackages();
    }

    private void updateInstalledPackages() {
        new MyWorker<List<VcpkgPackage>>() {

            @Override
            protected List<VcpkgPackage> doInBackground() throws Exception {
                List<VcpkgPackage> list = new ArrayList<>();
                for (int i = 0; i < 100; ++i) {
                    list.add(new VcpkgPackage("zlib", "0.0.1", "x64-linux"));
                    list.add(new VcpkgPackage("gnoe", "0.0.2", "x64-linux"));
                    list.add(new VcpkgPackage("oasd", "0.0.3", "x64-linux"));
                }
                return list;
            }

            @Override
            protected void onError(Exception e) {
                VcpkgGui.getMainWindow().setVisible(false);
            }

            @Override
            protected void myDone() throws Exception {

                installedTable.setVisible(true);
                installedTable.setModel(new PackageTableModel(get()));
            }
        }.execute();
    }

    private void updateVersion() {
        new MyWorker<String>() {

            @Override
            protected String doInBackground() throws Exception {
                return VcpkgHelper.getVersion();
            }

            @Override
            protected void onError(Exception e) {
                VcpkgGui.getMainWindow().setVisible(false);
            }

            @Override
            protected void myDone() throws Exception {
                version.setText("vcpkg version " + get());
            }
        }.execute();
    }

}
