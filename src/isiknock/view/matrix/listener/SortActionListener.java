/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package isiknock.view.matrix.listener;

import isiknock.entities.matrix.IMatrix;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import tools.ISorter;
import tools.ITrigger;

/**
 *
 * @author Heiko Giese <h.giese@bioinformatik.uni-frankfurt.de>
 * @param <E>
 * @param <F>
 */
public abstract class SortActionListener<E extends ISorter<IMatrix<F>>, F> implements ActionListener, ITrigger<E> {

    private final E sorter;

    public SortActionListener(E sorter) {
        this.sorter = sorter; 
    }

    @Override
    public void actionPerformed(ActionEvent ae) {
        trigger(sorter);
    }

}
