/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package isiknock.view.matrix.sorter;

import de.molbi.mjcl.clustering.distancemeasure.ManhattanDistances;
import de.molbi.mjcl.clustering.ds.ClusterTree;
import de.molbi.mjcl.clustering.ds.Leaf;
import de.molbi.mjcl.clustering.hcl.AverageLinkageSettings;
import de.molbi.mjcl.clustering.hcl.HierarchicSettings;
import isiknock.entities.matrix.IMatrix;
import java.util.Arrays;
import tools.ISorter;
import tools.IValueTranslater;

/**
 *
 * @author Heiko Giese <h.giese@bioinformatik.uni-frankfurt.de>
 * @param <E>
 */
public class ClusterSorter<E> implements ISorter<IMatrix<E>> {

    protected final IValueTranslater<E, Double> translater;
    protected final boolean transpose;

    public <F> ClusterSorter(IValueTranslater<E, Double> translater, boolean transpose) {
        this.translater = translater;
        this.transpose = transpose;
    }

    @Override
    public int[] sort(IMatrix<E> toSort) {

        double[][] data;
        if (transpose) {
            data = new double[toSort.getColumnCount()][toSort.getRowCount()];
            for (int row = 0; row < toSort.getRowCount(); row++) {
                for (int column = 0; column < toSort.getColumnCount(); column++) {
                    data[column][row] = translater.translate(toSort.getValue(row, column));
                }
            }
        } else {
            data = new double[toSort.getRowCount()][toSort.getColumnCount()];
            for (int row = 0; row < toSort.getRowCount(); row++) {
                for (int column = 0; column < toSort.getColumnCount(); column++) {
                    data[row][column] = translater.translate(toSort.getValue(row, column));
                }
            }
        }

        HierarchicSettings settings = AverageLinkageSettings.UPGMASettings(new ManhattanDistances(), true, null, null);

        de.molbi.mjcl.clustering.hcl.HierarchicalClustering hc = new de.molbi.mjcl.clustering.hcl.HierarchicalClustering(data, settings);
        ClusterTree tree = hc.execute();

        Leaf[] leaves = tree.getLeaves();
        int[] indices = new int[leaves.length];
        Arrays.setAll(indices, (int i) -> leaves[i].getDataID());

        return indices;
    }

}
