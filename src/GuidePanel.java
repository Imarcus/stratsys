import javax.swing.*;
import java.awt.*;

/**
 * Created by Marcus on 2016-08-16.
 */
public class GuidePanel extends JPanel {
    public GuidePanel() {
        setBorder(BorderFactory.createLineBorder(Color.black));
    }

    public Dimension getPreferredSize() {
        return new Dimension(250,200);
    }

    public void paintComponent(Graphics g) {
        super.paintComponent(g);

        // Draw Text
        g.drawString("This is my custom Panel!",10,20);
    }
}
