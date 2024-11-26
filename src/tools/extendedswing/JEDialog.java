/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tools.extendedswing;

import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import javax.swing.JDialog;

/**
 *
 * @author Heiko Giese <h.giese@bioinformatik.uni-frankfurt.de>
 */
public class JEDialog extends javax.swing.JDialog {

    public enum STATE {
        DONE, WAITING, CANCELED
    }

    private STATE state;

    public JEDialog(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        this.state = STATE.WAITING;
        this.setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);

        this.addWindowListener(new WindowListener() {
            @Override
            public void windowOpened(WindowEvent we) {

            }

            @Override
            public void windowClosing(WindowEvent we) {
                canceled();
            }

            @Override
            public void windowClosed(WindowEvent we) {
            }

            @Override
            public void windowIconified(WindowEvent we) {

            }

            @Override
            public void windowDeiconified(WindowEvent we) {

            }

            @Override
            public void windowActivated(WindowEvent we) {

            }

            @Override
            public void windowDeactivated(WindowEvent we) {

            }
        });
    }

    protected void done() {
        this.state = STATE.DONE;
        this.setVisible(false);
    }

    protected void canceled() {
        this.state = STATE.CANCELED;
        this.setVisible(false);
    }

    public STATE getState() {
        return state;
    }

    public final boolean isDone() {
        return this.state == STATE.DONE;
    }

    public final boolean isCanceled() {
        return this.state == STATE.CANCELED;
    }

    public final boolean isWaiting() {
        return this.state == STATE.WAITING;
    }

}
