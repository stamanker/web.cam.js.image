package ua.od.stamanker.web.webcam;

import com.github.sarxos.webcam.Webcam;
import com.github.sarxos.webcam.WebcamPanel;
import com.github.sarxos.webcam.WebcamResolution;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.ArrayList;

public class PanelExample {

        public static void main(String[] args) {
            final Webcam webcam = Webcam.getDefault();
            webcam.setViewSize(WebcamResolution.VGA.getSize());

            final WebcamPanel panel = new WebcamPanel(webcam);
            panel.setFPSDisplayed(true);
            panel.setImageSizeDisplayed(true);
            panel.setMirrored(true); //rotation: https://github.com/sarxos/webcam-capture/issues/581
            panel.setDrawMode(WebcamPanel.DrawMode.FILL);

            final JCheckBox flip = new JCheckBox();
            flip.setSelected(true);
            flip.setAction(new AbstractAction("Flip") {
                @Override
                public void actionPerformed(ActionEvent e) {
                    panel.setMirrored(flip.isSelected());
                }
            });

            JFrame window = new JFrame("Test webcam panel");
            window.setLayout(new FlowLayout());
            window.add(panel);
            window.add(flip);
            window.setResizable(true);
            window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            window.pack();
            window.setVisible(true);
        }
}
