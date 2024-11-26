/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package isiknock.view.matrix;

import isiknock.entities.matrix.IMatrix;
import tools.ISorter;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.io.File;
import java.io.IOException;

/**
 *
 * @author Heiko Giese <h.giese@bioinformatik.uni-frankfurt.de>
 * @param <E> The type of the values stored in the matrix.
 */
public interface IMatrixIllustrator<E> {

    public void paintImage(Graphics2D g2);
    
    public void paintImage(Graphics2D g2, Rectangle clipBounds);

    public void saveAsSVG(File saveTo) throws IOException;

    public void saveAsPNG(File saveTo) throws IOException;

    public void saveAsCSV(File saveTo) throws IOException;

    public Dimension getImageSize();

    public int translateColumnIndexToCoordinates(int columnIndex);

    public int translateCoordinatesToColumnIndex(int x);

    public int translateCoordinatesToRowIndex(int y);

    public int translateRowIndexToCoordinates(int rowIndex);

    public void setRowLabel(int rowIndex, String label);

    public String getRowLabel(int rowIndex);
    
    public String[] getRowLabel();

    public void setColumnLabel(int columnIndex, String label);

    public String getColumnLabel(int columnIndex);
    
    public String[] getColumnLabel();

    public int getRowCount();

    public int getColumnCount();
    
    public void setColumnSorter(ISorter<IMatrix<E>> sorter);
    
    public void setRowSorter(ISorter<IMatrix<E>> sorter);
    
    public E getValue(int rowIndex, int columnIndex);
    
    public String getValueAsString(int rowIndex, int columnIndex);

    public MatrixIllustrationSettings<E> getSettings();
    
    public MatrixIllustrationSettings<E> setSettings(MatrixIllustrationSettings<E> newSettings);

    public void redoLayout();
    
}
