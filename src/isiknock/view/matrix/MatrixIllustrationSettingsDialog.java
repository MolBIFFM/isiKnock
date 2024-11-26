/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package isiknock.view.matrix;

import tools.extendedswing.JEDialog;

/**
 *
 * @author Heiko Giese <h.giese@bioinformatik.uni-frankfurt.de>
 * @param <E> The type of the values stored in the matrix.
 */
public abstract class MatrixIllustrationSettingsDialog<E> extends JEDialog {
    
    public MatrixIllustrationSettingsDialog(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
    }
    
    public abstract MatrixIllustrationSettings<E> getSettings();
    
    
    
}
