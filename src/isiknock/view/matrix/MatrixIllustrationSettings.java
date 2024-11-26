/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package isiknock.view.matrix;

import java.awt.Color;
import java.awt.Font;
import java.awt.Toolkit;
import javax.swing.JFrame;
import isiknock.view.matrix.cellstyler.ICellStyler;
import isiknock.view.matrix.cellstyler.RoundCellStyler;
import tools.IValueTranslater;
import tools.Mathematics;

/**
 *
 * @author Heiko Giese <h.giese@bioinformatik.uni-frankfurt.de>
 * @param <E>
 */
public abstract class MatrixIllustrationSettings<E> {

    private int cellOffsetX, cellOffsetY;
    private int offsetTop, offsetRigth, offsetBottom, offsetLeft;
    private int rowLabelOffset, columnLabelOffset;
    private int cellWidth, cellHeight;
    private IValueTranslater<E, Color> valueToColor;
//    private IValueTranslater<E, String> valueToString;
    private final String fontFamily = "Arial";
    private final int fontWeight = Font.BOLD;
    private int rowLabelFontSize, columnLabelFontSize;
    private Color columnLabelColor, rowLabelColor;
    private ICellStyler cellStyler;
    
    private double scale = 1;

    public MatrixIllustrationSettings(IValueTranslater<E, Color> valueToColor) {
        this.valueToColor = valueToColor;
//        this.valueToString = valueToString;
        this.cellOffsetX = this.cellOffsetY = 10;
        this.offsetTop = this.offsetRigth = this.offsetBottom = this.offsetLeft = 10;
        this.rowLabelOffset = columnLabelOffset = 10;
        this.rowLabelFontSize = columnLabelFontSize = 18;
        this.cellWidth = cellHeight = 28;
        this.columnLabelColor = rowLabelColor = new Color(51, 51, 51);
        this.cellStyler = new RoundCellStyler();
    }

    public abstract MatrixIllustrationSettingsDialog<E> getSettingsDialog(JFrame parent);
    
    public MatrixIllustrationSettings setCellOffsetX(int cellOffsetX) {
        this.cellOffsetX = cellOffsetX;
        return this;
    }

    public MatrixIllustrationSettings setCellOffsetY(int cellOffsetY) {
        this.cellOffsetY = cellOffsetY;
        return this;
    }

    public MatrixIllustrationSettings setOffsetTop(int offsetTop) {
        this.offsetTop = offsetTop;
        return this;
    }

    public MatrixIllustrationSettings setOffsetRigth(int offsetRigth) {
        this.offsetRigth = offsetRigth;
        return this;
    }

    public MatrixIllustrationSettings setOffsetBottom(int offsetBottom) {
        this.offsetBottom = offsetBottom;
        return this;
    }

    public MatrixIllustrationSettings setOffsetLeft(int offsetLeft) {
        this.offsetLeft = offsetLeft;
        return this;
    }

    public MatrixIllustrationSettings setRowLabelOffset(int rowLabelOffset) {
        this.rowLabelOffset = rowLabelOffset;
        return this;
    }

    public MatrixIllustrationSettings setColumnLabelOffset(int columnLabelOffset) {
        this.columnLabelOffset = columnLabelOffset;
        return this;
    }

    public MatrixIllustrationSettings setValueToColor(IValueTranslater<E, Color> valueToColor) {
        this.valueToColor = valueToColor;
        return this;
    }
    
//    public MatrixIllustrationSettings setValueToString(IValueTranslater<E, String> valueToString) {
//        this.valueToString = valueToString;
//        return this;
//    }

    public MatrixIllustrationSettings setRowLabelFontSize(int rowLabelFontSize) {
        this.rowLabelFontSize = rowLabelFontSize;
        return this;
    }

    public MatrixIllustrationSettings setColumnLabelFontSize(int columnLabelFontSize) {
        this.columnLabelFontSize = columnLabelFontSize;
        return this;
    }

    public MatrixIllustrationSettings setCellWidth(int cellWidth) {
        this.cellWidth = cellWidth;
        return this;
    }

    public MatrixIllustrationSettings setCellHeight(int cellHeight) {
        this.cellHeight = cellHeight;
        return this;
    }

    public MatrixIllustrationSettings setColumnLabelColor(Color columnLabelColor) {
        this.columnLabelColor = columnLabelColor;
        return this;
    }

    public MatrixIllustrationSettings setRowLabelColor(Color rowLabelColor) {
        this.rowLabelColor = rowLabelColor;
        return this;
    }

    public MatrixIllustrationSettings setCellStyler(ICellStyler cellStyler) {
        this.cellStyler = cellStyler;
        return this;
    }
    
    private int scale(int valueToScale) {
        return (int) Math.round(valueToScale * scale);
    }

    public int getCellOffsetX() {
        return scale(cellOffsetX);
    }

    public int getCellOffsetY() {
        return scale(cellOffsetY);
    }

    public int getOffsetTop() {
        return scale(offsetTop);
    }

    public int getOffsetRigth() {
        return scale(offsetRigth);
    }

    public int getOffsetBottom() {
        return scale(offsetBottom);
    }

    public int getOffsetLeft() {
        return scale(offsetLeft);
    }

    public int getRowLabelOffset() {
        return scale(rowLabelOffset);
    }

    public int getColumnLabelOffset() {
        return scale(columnLabelOffset);
    }

    public IValueTranslater<E, Color> getValueToColor() {
        return valueToColor;
    }
    
//    public IValueTranslater<E, String> getValueToString() {
//        return valueToString;
//    }

    public Font getRowLabelFont() {
        return new Font(fontFamily, fontWeight, getRowLabelFontSize());
    }

    public Font getColumnLabelFont() {
        return new Font(fontFamily, fontWeight, getColumnLabelFontSize());
    }

    public int getRowLabelFontSize() {
        return scale(rowLabelFontSize);
    }

    public int getColumnLabelFontSize() {
        return scale(columnLabelFontSize);
    }

    public int getColumnLabelFontSizeInDots() {
        return pixelToDots(columnLabelFontSize);
    }

    private int pixelToDots(int pixel) {
        int screenRes = Toolkit.getDefaultToolkit().getScreenResolution();
        int fontSize = (int) Math.round(pixel * screenRes / 72.0);
        return fontSize;
    }

    public int getCellWidth() {
        return scale(cellWidth);
    }

    public int getCellHeight() {
        return scale(cellHeight);
    }

    public Color getColumnLabelColor() {
        return columnLabelColor;
    }

    public Color getRowLabelColor() {
        return rowLabelColor;
    }

    public ICellStyler getCellSytler() {
        return cellStyler;
    }
    
    public double getScale() {
        return this.scale;
    }
    
    public void setScale(double newScale) {
        this.scale = Mathematics.clamp(newScale, 0.2d, 1d);
    }
    
    public void resetScale() {
        this.setScale(1d);
    }

//    public static <E> MatrixIllustrationSettings<E> getOptimalSettings(int width, int height, IMatrix<E> matrix, IValueToColorTranslater<E> valueToColor) {
//
//        MatrixIllustrationSettings<E> defaultSettings = new MatrixIllustrationSettings<>(valueToColor);
//
//        String longestColumnString = "";
//        int longestColumnStringWidth = 0;
//        for (String string : matrix.getColumnLabel()) {
//            Rectangle2D stringBounds = Strings.getStringBounds(string, defaultSettings.getRowLabelFont());
//            if (longestColumnStringWidth < stringBounds.getWidth()) {
//                longestColumnString = string;
//                longestColumnStringWidth = (int) Math.ceil(stringBounds.getWidth());
//            }
//        }
//        String longestRowString = "";
//        int longestRowStringWidth = 0;
//        for (String string : matrix.getRowLabel()) {
//            Rectangle2D stringBounds = Strings.getStringBounds(string, defaultSettings.getRowLabelFont());
//            if (longestRowStringWidth < stringBounds.getWidth()) {
//                longestRowString = string;
//                longestRowStringWidth = (int) Math.ceil(stringBounds.getWidth());
//            }
//        }
//
//        float ratio;
//        
//        if(matrix.getColumnCount() > matrix.getRowCount()) {
//            
//        }
//        
//        int optimalWidth = defaultSettings.getOffsetLeft() + longestRowStringWidth + defaultSettings.getRowLabelOffset() + (matrix.getColumnCount() * (defaultSettings.getCellWidth() + defaultSettings.getCellOffsetX())) + defaultSettings.getOffsetRigth();
//        ratio = (float) width / (float) optimalWidth;
//        
//        return scale(defaultSettings, ratio);
//
////        MatrixIllustrationSettings<E> optimalSettings = new MatrixIllustrationSettings<>(valueToColor);
////
////        int drawArea = width - defaultSettings.getOffsetLeft() - longestRowStringWidth - defaultSettings.getRowLabelOffset() - defaultSettings.getOffsetRigth();
////
////        int possibleCellWidth = drawArea / matrix.getColumnCount();
////
////        float cellWidthToOffsetXRatio = 0;
////        if (defaultSettings.getCellOffsetX() > 0) {
////            cellWidthToOffsetXRatio = defaultSettings.getCellWidth() / defaultSettings.getCellOffsetX();
////        }
////        float cellWidthToHeightRatio = 1;
////        if (defaultSettings.getCellWidth() != defaultSettings.getCellHeight()) {
////            cellWidthToHeightRatio = defaultSettings.getCellWidth() / defaultSettings.getCellHeight();
////        }
////
////        float newCellWidth = possibleCellWidth * (1f / cellWidthToOffsetXRatio);
////
////        int cellWidth = (int) Math.round(newCellWidth);
////
////        int cellXOffset = possibleCellWidth - cellWidth;
////        int cellHeight = (int) Math.round(cellWidth * (1f / cellWidthToHeightRatio));
////
////        float newCellHeight = newCellWidth * (1f / cellWidthToHeightRatio);
////
////        MatrixIllustrationSettings<E> optimalSettings = new MatrixIllustrationSettings<>(valueToColor);
////
////        return optimalSettings;
//    }
//
//    public static <E> MatrixIllustrationSettings<E> scale(MatrixIllustrationSettings<E> settings, float scale) {
//
//        MatrixIllustrationSettings<E> scaledSettings = new MatrixIllustrationSettings<>(settings.getValueToColor());
//
//        scaledSettings.setCellWidth(scale(settings.getCellWidth(), scale));
//        scaledSettings.setCellHeight(scale(settings.getCellHeight(), scale));
//        scaledSettings.setCellOffsetX(scale(settings.getCellOffsetX(), scale));
//        scaledSettings.setCellOffsetY(scale(settings.getCellOffsetY(), scale));
//        scaledSettings.setColumnLabelOffset(scale(settings.getColumnLabelOffset(), scale));
//        scaledSettings.setRowLabelOffset(scale(settings.getRowLabelOffset(), scale));
//        scaledSettings.setOffsetTop(scale(settings.getOffsetTop(), scale));
//        scaledSettings.setOffsetRigth(scale(settings.getOffsetRigth(), scale));
//        scaledSettings.setOffsetBottom(scale(settings.getOffsetBottom(), scale));
//        scaledSettings.setOffsetLeft(scale(settings.getOffsetLeft(), scale));
//
//        return scaledSettings;
//    }
//
//    private static int scale(int value, float scale) {
//        return (int) Math.round(value * scale);
//    }

}
