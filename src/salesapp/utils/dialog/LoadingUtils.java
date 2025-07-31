package salesapp.utils.dialog;

import javax.swing.*;
import java.awt.*;

public class LoadingUtils {

    public static JDialog showLoadingDialog(String message) {
        JDialog dialog = new JDialog((JFrame) null, "Please Wait", true);
        JPanel panel = new JPanel(new BorderLayout());
        panel.add(new JLabel(message, SwingConstants.CENTER), BorderLayout.CENTER);

        dialog.getContentPane().add(panel);
        dialog.setSize(200, 100);
        dialog.setLocationRelativeTo(null);
        dialog.setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
        dialog.setResizable(false);
 
        SwingUtilities.invokeLater(() -> dialog.setVisible(true));
        return dialog;
    }

    public static void closeDialog(JDialog dialog) {
        if (dialog != null && dialog.isShowing()) {
            dialog.dispose();
        }
    }
}
