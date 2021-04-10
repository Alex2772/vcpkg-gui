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
    private JPanel descriptionAvailableWrap;
    private JPanel descriptionInstalledWrap;

    private DescriptionPanel mDescriptionInstalled = new DescriptionPanel();
    private DescriptionPanel mDescriptionAvailable = new DescriptionPanel();

    public MainWindow() {
        super("vcpkg-gui");
        setContentPane(root);
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        // center window
        setSize(800, 600);
        setLocationRelativeTo(null);

        setVisible(true);

        descriptionInstalledWrap.add(mDescriptionInstalled.root);
        descriptionAvailableWrap.add(mDescriptionAvailable.root);

        descriptionInstalledWrap.setVisible(false);
        descriptionAvailableWrap.setVisible(false);

        updateVersion();
        updateInstalledPackages();
        updateAvailablePackages();
    }

    private void updateInstalledPackages() {
        new MyWorker<List<VcpkgPackage>>() {

            @Override
            protected List<VcpkgPackage> doInBackground() throws Exception {
                return VcpkgHelper.getInstalledPackages();
            }

            @Override
            protected void onError(Exception e) {
                VcpkgGui.getMainWindow().setVisible(false);
            }

            @Override
            protected void myDone() throws Exception {

                descriptionInstalledWrap.setVisible(true);
                mDescriptionInstalled.table.setModel(new PackageTableModel(get()));
            }
        }.execute();
    }

    private void updateAvailablePackages() {
        new MyWorker<List<VcpkgPackage>>() {

            @Override
            protected List<VcpkgPackage> doInBackground() throws Exception {
                return VcpkgHelper.getAvailablePackages();
            }

            @Override
            protected void onError(Exception e) {
                VcpkgGui.getMainWindow().setVisible(false);
            }

            @Override
            protected void myDone() throws Exception {

                descriptionAvailableWrap.setVisible(true);
                mDescriptionAvailable.table.setModel(new PackageTableModel(get()));
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
