package ru.alex2772.vcpkggui.ui;

import ru.alex2772.vcpkggui.VcpkgGui;
import ru.alex2772.vcpkggui.core.MyWorker;
import ru.alex2772.vcpkggui.core.VcpkgHelper;

import javax.swing.*;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.logging.Level;
import java.util.logging.Logger;

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

        new MyWorker<String>() {

            @Override
            protected String doInBackground() throws Exception {
                return VcpkgHelper.getVersion();
            }

            @Override
            protected void myDone() throws Exception {
                version.setText(get());
            }
        }.execute();
    }

}
