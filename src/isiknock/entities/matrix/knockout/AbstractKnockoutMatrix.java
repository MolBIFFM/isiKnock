/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package isiknock.entities.matrix.knockout;

import isiknock.entities.matrix.IMatrix;
import tools.IValueTranslater;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Heiko Giese <h.giese@bioinformatik.uni-frankfurt.de>
 */
public abstract class AbstractKnockoutMatrix<E> implements IMatrix<E> {

    private final String[] rowLabel, columnLabel;
    private final E[][] matrix;

    public AbstractKnockoutMatrix(E[][] matrix) {
        this(new String[matrix != null ? matrix.length : 0], new String[matrix != null ? matrix[0].length : 0], matrix);
    }

    public AbstractKnockoutMatrix(String[] rowLabel, String[] columnLabel, E[][] matrix) {

        if (matrix == null) {
            throw new IllegalArgumentException("Matrix can't be null!");
        }
        if (matrix.length == 0) {
            throw new IllegalArgumentException("Matrix can't be empty!");
        }
        final int columnCount = matrix[0].length;
        if (rowLabel == null) {
            rowLabel = new String[matrix.length];
        } else if (rowLabel.length != matrix.length) {
            throw new IllegalArgumentException("Number of row lables does not match number of rows!");
        }
        if (columnLabel == null) {
            columnLabel = new String[columnCount];
        } else if (columnLabel.length != columnCount) {
            throw new IllegalArgumentException("Number of column lables does not match number of columns!");
        }

        Arrays.stream(matrix).forEach((row) -> {
            if (row.length != columnCount) {
                throw new IllegalArgumentException("The matrix must contain an equal number of columns.");
            }
        });

        this.rowLabel = rowLabel;
        this.columnLabel = columnLabel;
        this.matrix = matrix;
    }

    @Override
    public String[] getRowLabel() {
        return rowLabel;
    }

    @Override
    public String[] getColumnLabel() {
        return columnLabel;
    }

    @Override
    public E[][] getValueMatrix() {
        return matrix;
    }

    @Override
    public void setRowLabel(int rowIndex, String label) {
        setLabel(rowIndex, rowLabel, label);
    }

    @Override
    public void setColumnLabel(int columnIndex, String label) {
        setLabel(columnIndex, columnLabel, label);
    }

    private void setLabel(int index, String[] list, String value) {
        if (index < 0 || list.length <= index) {
            throw new IndexOutOfBoundsException("There is no label with index: " + index);
        }
        list[index] = value;
    }

    @Override
    public E setValue(int rowIndex, int columnIndex, E value) {
        if (rowIndex < 0 || matrix.length <= rowIndex) {
            throw new IndexOutOfBoundsException("There is no row with index: " + rowIndex);
        }
        if (columnIndex < 0 || matrix[rowIndex].length <= columnIndex) {
            throw new IndexOutOfBoundsException("There is no column with index: " + columnIndex);
        }
        E oldValue = matrix[rowIndex][columnIndex];
        matrix[rowIndex][columnIndex] = value;
        return oldValue;
    }

    @Override
    public E getValue(int rowIndex, int columnIndex) {
        return matrix[rowIndex][columnIndex];
    }

    @Override
    public int getColumnCount() {
        return matrix[0].length;
    }

    @Override
    public int getRowCount() {
        return matrix.length;
    }

    @Override
    public String getRowLabel(int rowIndex) {
        return rowLabel[rowIndex];
    }

    @Override
    public String getColumnLabel(int columnIndex) {
        return columnLabel[columnIndex];
    }

    @Override
    public void saveAs(File f, String delimiter, String textQualifier) {
        String encoding = "UTF-8";
        PrintStream s;
        try {
            s = new PrintStream(f, encoding);

            for (int i = 0; i < this.getColumnCount(); i++) {
                s.print(delimiter);
                s.print(textQualifier + this.columnLabel[i] + textQualifier);
            }
            for (int i = 0; i < this.getRowCount(); i++) {
                s.println();
                s.print(textQualifier + this.rowLabel[i] + textQualifier);
                for (int j = 0; j < this.getColumnCount(); j++) {
                    s.print(delimiter);
                    s.print(valueToString(i, j, textQualifier));
                }
            }
            s.close();
        } catch (FileNotFoundException | UnsupportedEncodingException ex) {
            Logger.getLogger(AbstractKnockoutMatrix.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    @Override
    public <F> IMatrix<F> translate(IMatrix<F> emptyMatrix, IValueTranslater<E, F> valueTranslater) {
        IMatrix<F> copy = emptyMatrix.copy(getRowCount(), getColumnCount());
        for (int i = 0; i < getRowCount(); i++) {
            copy.setRowLabel(i, getRowLabel(i));
        }
        for (int i = 0; i < getColumnCount(); i++) {
            copy.setColumnLabel(i, getColumnLabel(i));
        }
        for (int i = 0; i < matrix.length; i++) {
            for (int j = 0; j < matrix[i].length; j++) {
                copy.setValue(i, j, valueTranslater.translate(matrix[i][j]));
            }
        }
        return copy;
    }

    @Override
    public IMatrix<E> copy() {
        return copy(getRowCount(), getColumnCount());
    }

    @Override
    public IMatrix<E> copy(int rowCount, int columnCount) {
        IMatrix<E> copy = createMatrix(rowCount, columnCount);
        fillMatrix(copy);
        return copy;
    }

    @Override
    public IMatrix<E> copy(int rowCount, int columnCount, int[] rowOrder, int[] columnOrder) {
        IMatrix<E> copy = createMatrix(rowCount, columnCount);
        fillMatrix(copy, rowOrder, columnOrder);
        return copy;
    }

    protected void fillMatrix(IMatrix<E> emptyMatrix) {
        int[] rowOrder = new int[getRowCount()];
        for (int i = 0; i < rowOrder.length; i++) {
            rowOrder[i] = i;
        }
        int[] columnOrder = new int[getColumnCount()];
        for (int i = 0; i < columnOrder.length; i++) {
            columnOrder[i] = i;
        }
        fillMatrix(emptyMatrix, rowOrder, columnOrder);
    }

    protected void fillMatrix(IMatrix<E> emptyMatrix, int[] rowOrder, int[] columnOrder) {
        int maxRows = Math.min(getRowCount(), rowOrder.length);
        int maxColumns = Math.min(getColumnCount(), columnOrder.length);

        for (int rowIdx = 0; rowIdx < maxRows; rowIdx++) {
            emptyMatrix.setRowLabel(rowIdx, getRowLabel(rowOrder[rowIdx]));
        }
        for (int columnIdx = 0; columnIdx < maxColumns; columnIdx++) {
            emptyMatrix.setColumnLabel(columnIdx, getColumnLabel(columnOrder[columnIdx]));
        }
        for (int rowIdx = 0; rowIdx < maxRows; rowIdx++) {
            for (int columnIdx = 0; columnIdx < maxColumns; columnIdx++) {
                emptyMatrix.setValue(rowIdx, columnIdx, getValue(rowOrder[rowIdx], columnOrder[columnIdx]));
            }
        }
    }

    protected String valueToString(int rowIndex, int columnIndex, String textQualifier) {
        return getValue(rowIndex, columnIndex) == null ? textQualifier + "null" + textQualifier : this.getValueAsString(rowIndex, columnIndex);
    }

    @Override
    public String getValueAsString(int rowIndex, int columnIndex) {
        E value = getValue(rowIndex, columnIndex);
        return getValueAsString(value);
    }

    @Override
    public String getValueAsString(E value) {
        return value == null ? "null" : getNoneNullValueAsString(value);
    }

    protected abstract String getNoneNullValueAsString(E value);

    protected abstract IMatrix<E> createMatrix(int rowCount, int columnCount);

}
