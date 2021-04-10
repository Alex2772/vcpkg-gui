package ru.alex2772.vcpkggui.ui;

import ru.alex2772.vcpkggui.VcpkgGui;

import javax.swing.*;
import java.awt.event.WindowEvent;
import java.util.concurrent.CancellationException;
import java.util.logging.Level;

public class ProgressDialog extends JFrame {
    private JLabel stageLabel;
    private JLabel titleLabel;
    private JProgressBar progressBar;
    private JButton cancelButton;
    private JPanel root;

    /**
     * Displays progress dialog window. Calls setVisible by itself.
     * @param taskName task name which will be displayed
     * @param callback callback to do in background
     */
    public ProgressDialog(String taskName, Callback callback) {
        super(taskName);
        setContentPane(root);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setResizable(false);
        pack();
        titleLabel.setText(taskName);
        stageLabel.setText("");
        progressBar.setMaximum(100);
        progressBar.setValue(0);
        progressBar.setIndeterminate(true);

        // center window
        setLocationRelativeTo(null);

        setVisible(true);

        SwingWorker<Object, Object> worker = new SwingWorker<>() {

            @Override
            protected Object doInBackground() throws Exception {
                callback.doInBackground(ProgressDialog.this);
                return null;
            }

            @Override
            protected void done() {
                try {
                    get();
                    callback.onSuccess();
                    setVisible(false);
                } catch (CancellationException e) {
                    dispatchEvent(new WindowEvent(ProgressDialog.this, WindowEvent.WINDOW_CLOSING));
                } catch (Exception e) {
                    VcpkgGui.getLogger().log(Level.WARNING, "Could not " + stageLabel.getText().toLowerCase(), e);
                    setVisible(false);
                    JOptionPane.showMessageDialog(VcpkgGui.getMainWindow(),
                            "Error has occurred while " + stageLabel.getText().toLowerCase() + ":\n\n" +
                            e.getMessage(),
                            taskName,
                            JOptionPane.OK_OPTION);
                    System.exit(-1);
                }
            }
        };

        cancelButton.addActionListener(e -> {
            worker.cancel(true);
            dispatchEvent(new WindowEvent(ProgressDialog.this, WindowEvent.WINDOW_CLOSING));
        });

        worker.execute();
    }

    /**
     * Updates display info about running process. This function can be called from any thread.
     * @param percentage done percentage (up to 100). if percentage < 0, runs progress bar into the indeterminate mode
     * @param stageName stage name (for label)
     */
    public void displayStateAsync(int percentage, String stageName) {
        SwingUtilities.invokeLater(() -> {
            if (percentage < 0) {
                progressBar.setIndeterminate(true);
            } else {
                progressBar.setIndeterminate(false);
                progressBar.setValue(percentage);
                progressBar.setMaximum(100);
            }

            displayState(stageName);
        });
    }

    /**
     * Updates display info about running process. This function can be called from any thread
     * @param stageName stage name (for label)
     */
    public void displayStateAsync(String stageName) {
        SwingUtilities.invokeLater(() -> {
            displayState(stageName);
        });
    }

    /**
     * Non-async label update
     * @param stageName stage name to be displayed
     */
    private void displayState(String stageName) {
        // limit length of the message or it will break layout for some unknown reason
        final int MAX_LENGTH = 60;
        if (stageName.length() > MAX_LENGTH) {
            stageLabel.setText(stageName.substring(0, MAX_LENGTH) + "...");
        } else {
            stageLabel.setText(stageName);
        }
    }

    public interface Callback {
        void doInBackground(ProgressDialog pd) throws Exception;
        void onSuccess();
    }
}
