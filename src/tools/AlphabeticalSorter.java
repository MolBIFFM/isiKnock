/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tools;

import java.util.Arrays;

/**
 *
 * @author Heiko Giese <h.giese@bioinformatik.uni-frankfurt.de>
 */
public class AlphabeticalSorter implements ISorter<String[]> {

    final boolean ascending;

    public AlphabeticalSorter(boolean ascending) {
        this.ascending = ascending;
    }
    
    @Override
    public int[] sort(String[] toSort) {
        Integer[] indices = new Integer[toSort.length];
        Arrays.setAll(indices, (int i) -> i);
        Arrays.sort(indices, (Integer t, Integer t1) -> toSort[t].compareToIgnoreCase(toSort[t1]));
        int[] ind = new int[indices.length];
        if(ascending) {
            Arrays.setAll(ind, (int i) -> indices[i]);
        } else {
            Arrays.setAll(ind, (int i) -> indices[indices.length - i - 1]);
        }
        return ind;
    }

}
