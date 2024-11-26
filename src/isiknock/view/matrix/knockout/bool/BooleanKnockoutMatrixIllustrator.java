/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package isiknock.view.matrix.knockout.bool;

import isiknock.entities.matrix.IMatrix;
import isiknock.view.matrix.MatrixIllustrationSettings;
import isiknock.view.matrix.knockout.CachedKnockoutMatrixIllustrator;

/**
 *
 * @author Heiko Giese <h.giese@bioinformatik.uni-frankfurt.de>
 */
public class BooleanKnockoutMatrixIllustrator extends CachedKnockoutMatrixIllustrator<Boolean> {

    public BooleanKnockoutMatrixIllustrator(IMatrix<Boolean> matrix, MatrixIllustrationSettings<Boolean> settings) {
        super(matrix, settings);
    }

    @Override
    protected Boolean getMultipleKnockout(int column) {
        for (int i = 0; i < this.matrix.getRowCount(); i++) {
            Boolean value = this.matrix.getValue(i, column);
            if(value == null || value) {
                return true;
            }
        }
        return false;
    }
    
}
