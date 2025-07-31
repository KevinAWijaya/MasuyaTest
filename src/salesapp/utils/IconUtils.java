package salesapp.utils;

import java.awt.Image;
import java.net.URL;
import javax.swing.ImageIcon;

public class IconUtils {

    public static ImageIcon getIcon(String fileName, int width, int height) {
        URL url = IconUtils.class.getClassLoader().getResource("resources/icons/" + fileName);
        if (url != null) {
            ImageIcon original = new ImageIcon(url);
            Image scaled = original.getImage().getScaledInstance(width, height, Image.SCALE_SMOOTH);
            return new ImageIcon(scaled);
        } else {
            System.err.println("Icon not found: " + fileName);
            return null;
        }
    }
}
