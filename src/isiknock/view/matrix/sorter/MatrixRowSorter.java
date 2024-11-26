/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package isiknock.view.matrix.sorter;

import isiknock.entities.matrix.IMatrix;
import tools.AlphabeticalSorter;
import tools.ISorter;

/**
 *
 * @author Heiko Giese <h.giese@bioinformatik.uni-frankfurt.de>
 * @param <E>
 */
public class MatrixRowSorter<E> implements ISorter<IMatrix<E>>{

    protected final AlphabeticalSorter sorter;

    public MatrixRowSorter(boolean ascending) {
        this.sorter = new AlphabeticalSorter(ascending);
    }
    
    @Override
    public int[] sort(IMatrix toSort) {
        return sorter.sort(toSort.getRowLabel());
    }
    
}
