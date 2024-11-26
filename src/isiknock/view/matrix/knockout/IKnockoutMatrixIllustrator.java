/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package isiknock.view.matrix.knockout;

import isiknock.view.matrix.IMatrixIllustrator;

/**
 *
 * @author Heiko Giese <h.giese@bioinformatik.uni-frankfurt.de>
 * @param <E> The type of the values stored in the matrix.
 */
public interface IKnockoutMatrixIllustrator<E> extends IMatrixIllustrator<E>{
    
    public void setShowMultipleKnockout(boolean show);
    public boolean isShowingMultipleKnockout();
    
}
