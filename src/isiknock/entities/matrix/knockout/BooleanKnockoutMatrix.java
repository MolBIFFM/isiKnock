/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package isiknock.entities.matrix.knockout;

import isiknock.entities.matrix.IMatrix;

/**
 *
 * @author Heiko Giese <h.giese@bioinformatik.uni-frankfurt.de>
 */
public class BooleanKnockoutMatrix extends AbstractKnockoutMatrix<Boolean> {

    public BooleanKnockoutMatrix(Boolean[][] matrix) {
        super(matrix);
    }
    
    public BooleanKnockoutMatrix(String[] rowLabel, String[] columnLabel, Boolean[][] matrix) {
        super(rowLabel, columnLabel, matrix);
    }

    @Override
    protected IMatrix<Boolean> createMatrix(int rowCount, int columnCount) {
        return new BooleanKnockoutMatrix(new String[rowCount], new String[columnCount], new Boolean[rowCount][columnCount]);
    }

    @Override
    protected String getNoneNullValueAsString(Boolean value) {
        return value ? "1" : "0";
    }

    
}
