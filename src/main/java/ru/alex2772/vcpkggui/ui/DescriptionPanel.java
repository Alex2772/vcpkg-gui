package ru.alex2772.vcpkggui.ui;

import ru.alex2772.vcpkggui.model.PackageTableModel;
import ru.alex2772.vcpkggui.model.VcpkgPackage;
import ru.alex2772.vcpkggui.util.OSUtil;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.awt.event.*;

public class DescriptionPanel {
    public JPanel buttonsPanel;
    public JPanel root;
    public JLabel packageName;
    public JLabel homepage;
    public JLabel description = new JLabel("description");
    public JTable table;
    private JLabel version;
    private JScrollPane descriptionScrollPane;
    private JPanel descriptionWrap;
    private JTextField searchTextField;

    public DescriptionPanel() {
        table.getSelectionModel().setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        // init description label
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
                PackageTableModel model = (PackageTableModel) table.getModel();
                VcpkgPackage selectedPackage = model.getPackage(table.convertRowIndexToModel(table.getSelectedRow()));

                packageName.setText(selectedPackage.getName());
                version.setText(selectedPackage.getVersion());
                homepage.setText(selectedPackage.getHomepage());
                description.setText("<html>" + selectedPackage.getDescription() + "</html>");
            }
        });

    }

    /**
     * Called then the table model changed.
     * Inits search routine.
     */
    public void onTableModelChanged() {
        if (!(table.getModel() instanceof PackageTableModel))
            return;
        TableRowSorter<PackageTableModel> tableRowSorter = new TableRowSorter<>((PackageTableModel) table.getModel());
        table.setRowSorter(tableRowSorter);
        searchTextField.getDocument().addDocumentListener(new DocumentListener() {
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
                if (s.isEmpty()) {
                    tableRowSorter.setRowFilter(null);
                } else {
                    tableRowSorter.setRowFilter(RowFilter.regexFilter("(?i)" + s));
                }
            }
        });
    }
}
