/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tools;

/**
 *
 * @author Heiko Giese <h.giese@bioinformatik.uni-frankfurt.de>
 * @param <E>
 */
public interface ITrigger<E> {

    public void trigger(E arg);

}
