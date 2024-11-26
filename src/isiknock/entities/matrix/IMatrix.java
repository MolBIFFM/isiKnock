/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package isiknock.entities.matrix;

import tools.IValueTranslater;
import java.io.File;

/**
 *
 * @author Heiko Giese <h.giese@bioinformatik.uni-frankfurt.de>
 * @param <E> The type of values the matrix should store.
 */
public interface IMatrix<E> {
    
    public String[] getRowLabel();
    public String[] getColumnLabel();
    public E[][] getValueMatrix();
    public void setRowLabel(int rowIndex, String label);
    public String getRowLabel(int rowIndex);
    public void setColumnLabel(int columnIndex, String label);
    public String getColumnLabel(int columnIndex);
    public E setValue(int rowIndex, int columnIndex, E value);
    public E getValue(int rowIndex, int columnIndex);
    public String getValueAsString(int rowIndex, int columnIndex);
    public String getValueAsString(E value);
    public int getRowCount();
    public int getColumnCount();
    
    public IMatrix<E> copy();
    public IMatrix<E> copy(int rowCount, int columnCount);
    public IMatrix<E> copy(int rowCount, int columnCount, int[] rowOrder, int[] columnOrder);
    
    public <F> IMatrix<F> translate(IMatrix<F> emptyMatrix, IValueTranslater<E, F> valueTranslater);
    
    public void saveAs(File f, String delimiter, String textQualifier);
    
}
