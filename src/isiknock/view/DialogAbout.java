/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package isiknock.view;

import isiknock.language.Language;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JLayeredPane;
import tools.extendedswing.JEDialog;
import tools.extendedswing.JEPanel;

/**
 *
 * @author Heiko Giese <h.giese@bioinformatik.uni-frankfurt.de>
 */
public class DialogAbout extends JEDialog {

    public DialogAbout(Frame center) {
        super(center, true);
        int width = 550;
        int height = 400;
        Dimension d = new Dimension(width, height);
        this.setSize(d);
        this.setPreferredSize(d);
        this.setUndecorated(true);
        this.setLocationRelativeTo(center);

        JLayeredPane pane = this.getLayeredPane();
        JLabel cancel = new JLabel("x");
        cancel.setForeground(Color.BLACK);
        cancel.setOpaque(false);
        cancel.setVerticalAlignment(0);
        cancel.setHorizontalAlignment(0);
        int cancelOffset = 0;
        int cancelwidth = 20;
        cancel.setBounds(width - cancelOffset - cancelwidth, cancelOffset, cancelwidth, cancelwidth);
        cancel.addMouseListener(new MouseListener() {
            @Override
            public void mouseClicked(MouseEvent arg0) {
                close();
            }

            @Override
            public void mouseEntered(MouseEvent arg0) {
            }

            @Override
            public void mouseExited(MouseEvent arg0) {
            }

            @Override
            public void mousePressed(MouseEvent arg0) {
            }

            @Override
            public void mouseReleased(MouseEvent arg0) {
            }
        });

        JEPanel pan = new JEPanel();
        pan.setBackground(Color.WHITE);
        JLabel jLblLogo = new JLabel(new ImageIcon(DialogAbout.class.getResource("/resources/images/logo.png")));
        pan.setAllSizes(width, height);
        int y = 0;
        pan.addComponent(jLblLogo, 0, y++, 1, 1, 1.0, 0.0, 15, 10, 10, 10);

        String[] aboutTxt = Language.CURRENT.aboutTxt;
        for (int i = 0; i < aboutTxt.length; i++) {
            JLabel jLblRow = new JLabel(aboutTxt[i]);
            jLblRow.setOpaque(false);
            jLblRow.setHorizontalAlignment(JLabel.CENTER);
            pan.addComponent(jLblRow, 0, y++, 1, 1, 1.0, 0.0, 0, 0, 0, 0);
        }
        
        JLabel jLblMolbiLogo = new JLabel(new ImageIcon(DialogAbout.class.getResource("/resources/images/molbi_logo.png")));
        pan.addComponent(jLblMolbiLogo, 0, y++, 1, 1, 1.0, 0.0, 15, 10, 10, 10);
        
//        pan.addComponent(new SpacerPanel(), 0, y, 1, 1, 1.0, 1.0, 0, 0, 0, 0);

        pane.add(pan, 10);
        pane.add(cancel, 0);
    }

    public void close() {
        this.setVisible(false);
    }

    public static void main(String args[]) {
        java.awt.EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                new DialogAbout(null).setVisible(true);
            }
        });

    }
}
