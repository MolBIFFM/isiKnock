/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package tools.extendedswing;

//import event.EKeyListener;
import java.awt.*;
import javax.swing.JPanel;

/**
 *
 * @author Heiko Giese <h.giese@bioinformatik.uni-frankfurt.de>
 */
public class JEPanel extends JPanel {

    private GridBagConstraints gbc;
    private GridBagLayout gbl;
    protected Double constrain;

    public JEPanel() {
        this.init();
    }

    public final void setAllSizes(int width, int height) {
        this.setBounds(0, 0, width, height);
        Dimension d = new Dimension(width, height);
        this.setSize(d);
        this.setPreferredSize(d);
        this.setMinimumSize(d);
        this.setMaximumSize(d);
    }

    private void init() {
        this.constrain = 1.0;
        this.gbl = new GridBagLayout();
        this.gbc = new GridBagConstraints();

        this.setLayout(gbl);
    }

    public void addComponent(Component c, int x, int y, int width, int height, double weightx, double weighty, int inTop, int inRight, int inBottom, int inLeft, int anchor) {
        gbc.fill = GridBagConstraints.BOTH;
        gbc.gridx = x;
        gbc.gridy = y;
        gbc.gridwidth = width;
        gbc.gridheight = height;
        gbc.weightx = weightx;
        gbc.weighty = weighty;
        gbc.anchor = anchor;
        gbc.insets = new Insets(inTop, inLeft, inBottom, inRight);
        gbl.setConstraints(c, gbc);
        this.add(c, gbc);
    }
    
    public void addComponent(Component c, int x, int y, int width, int height, double weightx, double weighty, int inTop, int inRight, int inBottom, int inLeft) {
        this.addComponent(c, x, y, width, height, weightx, weighty, inTop, inRight, inBottom, inLeft, GridBagConstraints.PAGE_START);
    }
   
}
