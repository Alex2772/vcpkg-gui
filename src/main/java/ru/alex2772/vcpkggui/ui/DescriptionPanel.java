package ru.alex2772.vcpkggui.ui;

import ru.alex2772.vcpkggui.core.PlatformChecker;
import ru.alex2772.vcpkggui.core.VcpkgHelper;
import ru.alex2772.vcpkggui.model.PackageTableModel;
import ru.alex2772.vcpkggui.model.VcpkgPackage;
import ru.alex2772.vcpkggui.util.OSUtil;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.TableRowSorter;
import java.awt.event.*;

public class DescriptionPanel {
    public JPanel root;
    public JLabel packageName;
    public JLabel homepage;
    public JLabel description = new JLabel("description");
    public JTable table;
    private JLabel version;
    private JScrollPane descriptionScrollPane;
    private JPanel descriptionWrap;
    private JTextField searchTextField;
    private JLabel installedLabel;
    private JButton actionButton;
    private JPanel packageActionsPanel;

    public DescriptionPanel() {
        // init description label
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        description.setLocation(0, 0);
        description.setHorizontalAlignment(SwingConstants.LEFT);
        description.setVerticalAlignment(SwingConstants.TOP);

        // a workaround for word wrapping on description label
        descriptionWrap.setLayout(null);
        descriptionWrap.add(description);
        descriptionWrap.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                super.componentResized(e);
                description.setSize(descriptionWrap.getWidth(), descriptionWrap.getHeight());
            }
        });

        homepage.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                super.mouseClicked(e);
                if (!homepage.getText().isEmpty()) {
                    OSUtil.openUrl(homepage.getText());
                }
            }
        });
        table.getSelectionModel().addListSelectionListener(e -> {
            if (table.getModel() instanceof PackageTableModel) {
                if (table.getSelectedRow() < 0)
                    return;

                PackageTableModel model = (PackageTableModel) table.getModel();
                VcpkgPackage selectedPackage = model.getPackage(table.convertRowIndexToModel(table.getSelectedRow()));

                packageName.setText(selectedPackage.getName());
                version.setText(selectedPackage.getVersion());
                homepage.setText(selectedPackage.getHomepage());
                description.setText("<html>" + selectedPackage.getDescription() + "</html>");

                // remove all actions listeners for action button
                for (ActionListener al : actionButton.getActionListeners()) {
                    actionButton.removeActionListener(al);
                }

                // check for package installed or not
                installedLabel.setVisible(selectedPackage.isInstalled());

                if (selectedPackage.isInstalled()) {
                    actionButton.setText("Uninstall");
                    actionButton.setEnabled(true);
                    actionButton.addActionListener(e1 -> {
                        VcpkgHelper.uninstall(selectedPackage);
                    });
                } else {
                    if (PlatformChecker.check(selectedPackage.getSupportedPlatform())) {
                        actionButton.setText("Install");
                        actionButton.setEnabled(true);
                        actionButton.addActionListener(e1 -> {
                            VcpkgHelper.install(selectedPackage);
                        });
                    } else {
                        actionButton.setText("Unsupported platform");
                        actionButton.setEnabled(false);
                    }
                }

                packageActionsPanel.setVisible(true);
            }
        });

        packageActionsPanel.setVisible(false);
    }

    /**
     * Called then the table model changed.
     * Inits search routine.
     */
    public void onTableModelChanged() {
        if (!(table.getModel() instanceof PackageTableModel))
            return;
        table.removeColumn(table.getColumnModel().getColumn(3));
        table.setRowSorter(null);
        table.setRowSelectionInterval(0, 0);
        searchTextField.getDocument().addDocumentListener(new DocumentListener() {
            private TableRowSorter<PackageTableModel> tableRowSorter;

            @Override
            public void insertUpdate(DocumentEvent e) {
                update();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                update();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                update();
            }


            private void update() {
                String s = searchTextField.getText().trim();
                if (table.getRowSorter() == null) {
                    tableRowSorter = new TableRowSorter<>((PackageTableModel) table.getModel());
                    table.setRowSorter(tableRowSorter);
                }
                if (s.isEmpty()) {
                    tableRowSorter.setRowFilter(null);
                } else {
                    tableRowSorter.setRowFilter(RowFilter.regexFilter("(?i)" + s));
                }
            }
        });
    }
}
