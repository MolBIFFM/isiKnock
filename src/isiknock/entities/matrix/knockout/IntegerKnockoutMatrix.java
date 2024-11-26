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
public class IntegerKnockoutMatrix extends AbstractKnockoutMatrix<Integer> {

    public IntegerKnockoutMatrix(Integer[][] matrix) {
        super(matrix);
    }
    
    public IntegerKnockoutMatrix(String[] rowLabel, String[] columnLabel, Integer[][] matrix) {
        super(rowLabel, columnLabel, matrix);
    }

    @Override
    protected IMatrix<Integer> createMatrix(int rowCount, int columnCount) {
        return new IntegerKnockoutMatrix(new String[rowCount], new String[columnCount], new Integer[rowCount][columnCount]);
    }
    
    @Override
    protected String getNoneNullValueAsString(Integer value) {
        return value.toString();
    }
}
