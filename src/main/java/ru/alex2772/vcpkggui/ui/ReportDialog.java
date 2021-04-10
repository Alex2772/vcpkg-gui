package ru.alex2772.vcpkggui.ui;

import javax.swing.*;

public class ReportDialog extends JFrame {
    private JTextArea textArea;
    private JButton okButton;
    private JPanel root;
    private JLabel reportMessage;

    public ReportDialog() {
        setContentPane(root);
        toFront();
        // center window
        setSize(800, 600);
        setLocationRelativeTo(null);

        okButton.addActionListener(e -> {
            setVisible(false);
        });

    }

    public static void show(String reportText, String message, String title) {
        ReportDialog rd = new ReportDialog();
        rd.setTitle(title);
        rd.reportMessage.setText(message);
        rd.textArea.setText(reportText);
        rd.pack();
        rd.setVisible(true);
    }
}
