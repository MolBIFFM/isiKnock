/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package isiknock.view.matrix.sorter;

import isiknock.entities.matrix.IMatrix;

/**
 *
 * @author Heiko Giese <h.giese@bioinformatik.uni-frankfurt.de>
 * @param <E>
 */
public class MatrixColumnSorter<E> extends MatrixRowSorter<E> {

    public MatrixColumnSorter(boolean ascending) {
        super(ascending);
    }
 
    @Override
    public int[] sort(IMatrix toSort) {
        return sorter.sort(toSort.getColumnLabel());
    }
    
}
