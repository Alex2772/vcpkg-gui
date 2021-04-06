package ru.alex2772.vcpkggui.ui;

import ru.alex2772.vcpkggui.VcpkgGui;
import ru.alex2772.vcpkggui.core.MyWorker;
import ru.alex2772.vcpkggui.core.VcpkgHelper;

import javax.swing.*;

public class MainWindow extends JFrame {

    private JPanel root;
    private JTable table1;
    private JButton installPackageButton;
    private JButton deletePackageButton;
    private JButton viewPackageButton;
    private JLabel version;

    public MainWindow() {
        super("vcpkg-gui");
        setContentPane(root);
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        // center window
        setSize(800, 600);
        setLocationRelativeTo(null);

        setVisible(true);



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
