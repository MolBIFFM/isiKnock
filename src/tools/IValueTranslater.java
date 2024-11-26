/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tools;

/**
 *
 * @author Heiko Giese <h.giese@bioinformatik.uni-frankfurt.de>
 * @param <E> The type of the value to be translated.
 * @param <F> The type of the value to be translated to.
 */
public interface IValueTranslater<E,F> {
        
    public F translate(E toTranslate);
    
}
