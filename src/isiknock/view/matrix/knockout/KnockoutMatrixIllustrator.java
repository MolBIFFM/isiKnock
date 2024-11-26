/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package isiknock.view.matrix.knockout;

import isiknock.view.matrix.MatrixIllustrationSettings;
import java.awt.Canvas;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.awt.image.ImageObserver;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.function.IntUnaryOperator;
import javax.imageio.ImageIO;
import tools.SVGWriter;
import tools.Strings;
import tools.UITools;
import isiknock.entities.matrix.IMatrix;
import java.awt.RenderingHints;
import tools.ISorter;

/**
 *
 * @author Heiko Giese <h.giese@bioinformatik.uni-frankfurt.de>
 * @param <E> The type of the values stored in the matrix.
 */
public abstract class KnockoutMatrixIllustrator<E> implements IKnockoutMatrixIllustrator<E> {

    protected String multipleKnockoutLabel = "Multiple Knockout";

    protected IMatrix<E> matrix;
    protected MatrixIllustrationSettings<E> settings;
    protected ISorter<IMatrix<E>> rowSorter, columnSorter;
    private int[] defaultRowOrder, defaultColumnOrder;
    protected int[] rowOrder, columnOrder;

    public KnockoutMatrixIllustrator(IMatrix<E> matrix, MatrixIllustrationSettings<E> settings) {
        this.setKnockoutMatrix(matrix, settings);
    }

    private void setKnockoutMatrix(IMatrix<E> matrix, MatrixIllustrationSettings settings) {
        this.matrix = matrix;

        this.rowSorter = null;
        this.columnSorter = null;

        this.defaultRowOrder = new int[matrix.getRowCount()];
        Arrays.setAll(defaultRowOrder, (int i) -> i);
        this.defaultColumnOrder = new int[matrix.getColumnCount()];
        Arrays.setAll(defaultColumnOrder, (int i) -> i);

        this.rowOrder = defaultRowOrder;
        this.columnOrder = defaultColumnOrder;

        this.setSettings(settings);

        this.sortRow();
        this.sortColumn();
    }

    @Override
    public void saveAsCSV(File saveTo) throws IOException {
        int rowCount = this.getRowCount();
        int colCount = this.getColumnCount();
        
        IMatrix<E> copy = this.matrix.copy(rowCount, colCount, rowOrder, columnOrder);
        if (showMultipleKnockout) {
            int lastRow = rowCount - 1;
            copy.setRowLabel(lastRow, multipleKnockoutLabel);
            for (int i = 0; i < colCount; i++) {
                copy.setValue(lastRow, i, getMultipleKnockout(column(i)));
            }
        }
        copy.saveAs(saveTo, ";", "\"");
    }

    @Override
    public void setColumnSorter(ISorter sorter) {
        if (!(this.columnSorter == null && sorter == null)) {
            this.columnSorter = sorter;
            this.sortColumn();
        }
    }

    @Override
    public void setRowSorter(ISorter sorter) {
        if (!(this.rowSorter == null && sorter == null)) {
            this.rowSorter = sorter;
            this.sortRow();
        }
    }

    protected abstract E getMultipleKnockout(int column);

    protected void sortRow() {
        this.rowOrder = rowSorter != null ? rowSorter.sort(matrix) : defaultRowOrder;
        this.redoLayout();
    }

    protected void sortColumn() {
        this.columnOrder = columnSorter != null ? columnSorter.sort(matrix) : defaultColumnOrder;
        this.redoLayout();
    }

    protected void matrixModified(IMatrix matrix) {
        this.sortRow();
        this.sortColumn();
    }

    protected int[] xCoordinates;
    protected int[] yCoordinates;
    protected int[] columnStringOffset;
    protected int[] rowStringOffset;

    private int rowLabelAreaWidth;
    private int columnLabelAreaHeight;
    private int rowLabelLineOffset;
    private int columnLabelLineOffset;

    private int row(int index) {
        return rowOrder[index];
    }

    private int column(int index) {
        return columnOrder[index];
    }

    @Override
    public void redoLayout() {

        int rowCount = matrix.getRowCount() + (showMultipleKnockout ? 1 : 0);

        columnStringOffset = new int[matrix.getColumnCount()];
        rowStringOffset = new int[rowCount];

        String[] rowLabel = new String[rowCount];

        int maxRowLabelWidth = 0;
        int maxRowLabelHeight = 0;
        for (int i = 0; i < matrix.getRowCount(); i++) {
            String label = matrix.getRowLabel(row(i));
            rowLabel[i] = label;
        }
        if (showMultipleKnockout) {
            rowLabel[rowLabel.length - 1] = multipleKnockoutLabel;
        }
        for (int i = 0; i < rowLabel.length; i++) {
            String label = rowLabel[i];
            if(label == null) label = "";
            Rectangle2D stringBounds = Strings.getStringBounds(label, settings.getRowLabelFont());
            int sWidth = (int) Math.ceil(stringBounds.getWidth());
            maxRowLabelWidth = Math.max(maxRowLabelWidth, sWidth);
            maxRowLabelHeight = Math.max(maxRowLabelHeight, (int) Math.ceil(stringBounds.getHeight()));
            rowStringOffset[i] = sWidth;
        }

        rowLabelAreaWidth = maxRowLabelWidth;
        rowLabelLineOffset = (settings.getCellHeight() + maxRowLabelHeight) / 2;

        int maxColumnLabelWidth = 0;
        int maxColumnLabelHeight = 0;
        for (int i = 0; i < matrix.getColumnCount(); i++) {
            String label = matrix.getColumnLabel(column(i));
            if(label == null) label = "";
            Rectangle2D stringBounds = Strings.getStringBounds(label, settings.getColumnLabelFont());
            int sWidth = (int) Math.ceil(stringBounds.getWidth());
            maxColumnLabelWidth = Math.max(maxColumnLabelWidth, sWidth);
            maxColumnLabelHeight = Math.max(maxColumnLabelHeight, (int) Math.ceil(stringBounds.getHeight()));
            columnStringOffset[i] = sWidth;
        }
        columnLabelAreaHeight = maxColumnLabelWidth;
        columnLabelLineOffset = (settings.getCellWidth() - maxColumnLabelHeight) / 2;

        Arrays.setAll(rowStringOffset, new IntUnaryOperator() {
            @Override
            public int applyAsInt(int i) {
                return rowLabelAreaWidth - rowStringOffset[i];
            }
        });

        //relies on previos values and should always be calcualted last
        xCoordinates = new int[matrix.getColumnCount()];
        yCoordinates = new int[rowCount];
        for (int x = 0; x < xCoordinates.length; x++) {
            xCoordinates[x] = translateColumnIndexToCoordinates(x);
        }
        for (int y = 0; y < yCoordinates.length; y++) {
            yCoordinates[y] = translateRowIndexToCoordinates(y);
        }
    }

    @Override
    public void paintImage(Graphics2D g2) {
        paintImage(g2, g2.getClipBounds());
    }

    @Override
    public void paintImage(Graphics2D g2, Rectangle clipBounds) {
        UITools.activateAntialiasing(g2);
        paintImage(new GraphicsMatrix(g2), clipBounds);
    }
    
    protected void paintImage(IGraphicsMatrix g2) {
        paintImage(g2, g2.getClipBounds());
    }

    protected void paintImage(IGraphicsMatrix g2, Rectangle clipBounds) {
        
        int xOffset, yOffset;
        int xLimit, yLimit;
        if (clipBounds == null) {
            clipBounds = new Rectangle(0, 0);
            xOffset = yOffset = 0;
            xLimit = xCoordinates.length;
            yLimit = yCoordinates.length;
        } else {
            xOffset = Math.max(0, translateCoordinatesToColumnIndex(clipBounds.x) - 1);
            yOffset = Math.max(0, translateCoordinatesToRowIndex(clipBounds.y) - 1);
            xLimit = Math.min(translateCoordinatesToColumnIndex(clipBounds.x + clipBounds.width) + 1, xCoordinates.length);
            yLimit = Math.min(translateCoordinatesToRowIndex(clipBounds.y + clipBounds.height) + 1, yCoordinates.length);
        }

        //draw column label
        if (clipBounds.y <= (settings.getOffsetTop() + columnLabelAreaHeight)) {
            g2.setFont(settings.getColumnLabelFont());
            g2.setColor(settings.getColumnLabelColor());
            int y = settings.getOffsetTop() + columnLabelAreaHeight;
            for (int colIndex = xOffset; colIndex < xLimit; colIndex++) {
                int x = xCoordinates[colIndex] + columnLabelLineOffset;
                x += 3; //correction
                String label = matrix.getColumnLabel(column(colIndex));
                g2.drawRotatedString(x, y, label, -90);
            }
        }

        //draw row label
        String[] rowLabel = new String[yCoordinates.length];
        for (int i = 0; i < matrix.getRowCount(); i++) {
            rowLabel[i] = matrix.getRowLabel(row(i));
        }
        if (showMultipleKnockout) {
            rowLabel[rowLabel.length - 1] = multipleKnockoutLabel;
        }

        if (clipBounds.x <= (settings.getOffsetLeft() + rowLabelAreaWidth)) {
            g2.setFont(settings.getRowLabelFont());
            g2.setColor(settings.getRowLabelColor());
            int x = settings.getOffsetLeft();
            for (int rowIndex = yOffset; rowIndex < yLimit; rowIndex++) {
                int y = yCoordinates[rowIndex] + rowLabelLineOffset;
                String label = rowLabel[rowIndex];
                g2.drawString(x + rowStringOffset[rowIndex], y, label);
            }
        }

        //draw matrix
        int xCellDrawOffset = settings.getCellOffsetX() / 2;
        int yCellDrawOffset = settings.getCellOffsetY() / 2;
        for (int rowIndex = yOffset; rowIndex < yLimit; rowIndex++) {
            g2.setFont(settings.getColumnLabelFont());
            int y = yCoordinates[rowIndex];
            for (int colIndex = xOffset; colIndex < xLimit; colIndex++) {
                int x = xCoordinates[colIndex];

                E value;
                if (showMultipleKnockout && rowIndex == yCoordinates.length - 1) {
                    value = getMultipleKnockout(column(colIndex));
                } else {
                    value = matrix.getValue(row(rowIndex), column(colIndex));
                }
                Color c = settings.getValueToColor().translate(value);

                g2.drawCell(x + xCellDrawOffset, y + yCellDrawOffset, settings.getCellWidth(), settings.getCellHeight(), c);
            }
        }
    }

    protected void drawString(int x, int y, String string, Graphics2D g2) {
        g2.drawString(string, x, y);
    }

    @Override
    public int translateColumnIndexToCoordinates(int columnIndex) {
        return settings.getOffsetLeft() + rowLabelAreaWidth + settings.getRowLabelOffset() + (columnIndex * (settings.getCellWidth() + settings.getCellOffsetX()));
    }

    @Override
    public int translateRowIndexToCoordinates(int rowIndex) {
        return settings.getOffsetTop() + columnLabelAreaHeight + settings.getColumnLabelOffset() + (rowIndex * (settings.getCellHeight() + settings.getCellOffsetY()));
    }

    @Override
    public int translateCoordinatesToColumnIndex(int x) {
        int drawArea = x - settings.getOffsetLeft() - rowLabelAreaWidth - settings.getRowLabelOffset();
        int offset = settings.getCellWidth() + settings.getCellOffsetX();

        int skipped = drawArea / offset;
        if (drawArea < 0) {
            skipped--;
        }

        return skipped;
    }

    @Override
    public int translateCoordinatesToRowIndex(int y) {
        int drawArea = y - settings.getOffsetTop() - columnLabelAreaHeight - settings.getColumnLabelOffset();
        int offset = settings.getCellHeight() + settings.getCellOffsetY();

        int skipped = drawArea / offset;
        if (drawArea < 0) {
            skipped--;
        }

        return skipped;
    }

    @Override
    public Dimension getImageSize() {
        int width = translateColumnIndexToCoordinates(this.matrix.getColumnCount()) + settings.getOffsetRigth();
        int height = translateRowIndexToCoordinates(this.matrix.getRowCount() + 1) + settings.getOffsetBottom();
        return new Dimension(width, height);
    }

    @Override
    public void setRowLabel(int rowIndex, String label) {
        if(isShowingMultipleKnockout() && rowIndex == matrix.getRowCount()) {
            this.multipleKnockoutLabel = label;
            this.redoLayout();
            return;
        }
        matrix.setRowLabel(row(rowIndex), label);
        this.sortRow();
    }

    @Override
    public String getRowLabel(int rowIndex) {
        if(isShowingMultipleKnockout() && rowIndex == matrix.getRowCount()) {
            return multipleKnockoutLabel;
        }
        return matrix.getRowLabel(row(rowIndex));
    }

    @Override
    public void setColumnLabel(int columnIndex, String label) {
        matrix.setColumnLabel(column(columnIndex), label);
        this.sortColumn();
    }

    @Override
    public String getColumnLabel(int columnIndex) {
        return matrix.getColumnLabel(column(columnIndex));
    }

    @Override
    public String[] getRowLabel() {
        String[] label = new String[this.getRowCount()];
        Arrays.setAll(label, (int i) -> getRowLabel(i));
        return label;
    }

    @Override
    public String[] getColumnLabel() {
        String[] label = new String[this.getColumnCount()];
        Arrays.setAll(label, (int i) -> getColumnLabel(i));
        return label;
    }

    @Override
    public E getValue(int rowIndex, int columnIndex) {
        if(isShowingMultipleKnockout() && rowIndex == matrix.getRowCount()) {
            return getMultipleKnockout(columnIndex);
        }
        return matrix.getValue(row(rowIndex), column(columnIndex));
    }

    @Override
    public String getValueAsString(int rowIndex, int columnIndex) {
        return matrix.getValueAsString(getValue(rowIndex, columnIndex));
    }

    @Override
    public int getRowCount() {
        return matrix.getRowCount() + (isShowingMultipleKnockout() ? 1 : 0);
    }

    @Override
    public int getColumnCount() {
        return matrix.getColumnCount();
    }

    @Override
    public void saveAsSVG(File saveTo) throws IOException {
        Dimension d = this.getImageSize();
        try (SVGWriter svgWriter = new SVGWriter(saveTo.getAbsolutePath(), d.width, d.height)) {
            paintImage(new GraphicsMatrixSVG(svgWriter));
        }
    }

    @Override
    public void saveAsPNG(File saveTo) throws IOException {
        Dimension d = this.getImageSize();
        BufferedImage bImg = new BufferedImage(d.width, d.height, BufferedImage.TYPE_INT_ARGB);
        paintImage(bImg.createGraphics());
        ImageIO.write(bImg, "png", saveTo);
    }

    @Override
    public MatrixIllustrationSettings getSettings() {
        return settings;
    }

    @Override
    public MatrixIllustrationSettings setSettings(MatrixIllustrationSettings newSettings) {
        MatrixIllustrationSettings oldSettings = this.settings;
        this.settings = newSettings;
        this.redoLayout();
        return oldSettings;
    }

    private boolean showMultipleKnockout;

    @Override
    public void setShowMultipleKnockout(boolean show) {
        this.showMultipleKnockout = show;
        this.redoLayout();
    }

    @Override
    public boolean isShowingMultipleKnockout() {
        return showMultipleKnockout;
    }

    protected interface IGraphicsMatrix {

        public void drawCell(int x, int y, int width, int height, Color color);

        public void drawRotatedString(int x, int y, String label, int rotation);

        public void drawString(int x, int y, String label);

        public Rectangle getClipBounds();

        public void setFont(Font columnLabelFont);

        public void setColor(Color columnLabelColor);

        public FontMetrics getFontMetrics();

        public FontMetrics getFontMetrics(Font font);
        
        

    }

    protected class GraphicsMatrix implements IGraphicsMatrix {

        protected final Graphics2D g2;

        public GraphicsMatrix(Graphics2D g2) {
            this.g2 = g2;
        }

        @Override
        public void drawCell(int x, int y, int width, int height, Color color) {
            settings.getCellSytler().drawCell(x, y, width, height, color, g2);
        }

        @Override
        public void drawRotatedString(int x, int y, String string, int rotation) {

            if (string == null || string.isEmpty()) {
                return;
            }

            rotation = rotation % 360;

            FontMetrics fm = g2.getFontMetrics();
            Rectangle2D stringBounds = fm.getStringBounds(string, g2);

            if (stringBounds.getWidth() <= 0 || stringBounds.getHeight() <= 0) {
                return;
            }

            BufferedImage bImg = new BufferedImage((int) stringBounds.getWidth(), (int) stringBounds.getHeight(), BufferedImage.TYPE_INT_ARGB);

            //create an image of the label text (horizontal)
            Graphics2D g2bImg = bImg.createGraphics();
            g2bImg.setFont(g2.getFont());
            g2bImg.setColor(g2.getColor());
            UITools.activateAntialiasing(g2bImg);
            g2bImg.drawString(string, 0, bImg.getHeight() - fm.getMaxDescent());

            //rotate and draw the image of the label text
            int labelX = x + fm.getMaxDescent();
            int labelY = y;
            AffineTransform transform = new AffineTransform();
            transform.translate(labelX, labelY);
            transform.rotate((rotation) * java.lang.Math.PI / 180);
            g2.drawImage(bImg, transform, new ImageObserver() {
                @Override
                public boolean imageUpdate(Image image, int i, int i1, int i2, int i3, int i4) {
                    return false;
                }
            });

        }

        @Override
        public void drawString(int x, int y, String string) {
            RenderingHints old = UITools.activateAntialiasing(g2);
            g2.drawString(string, x, y);
            g2.setRenderingHints(old);
        }

        @Override
        public Rectangle getClipBounds() {
            return g2.getClipBounds();
        }

        @Override
        public void setFont(Font font) {
            g2.setFont(font);
        }

        @Override
        public void setColor(Color color) {
            g2.setColor(color);
        }

        @Override
        public FontMetrics getFontMetrics() {
            return g2.getFontMetrics();
        }

        @Override
        public FontMetrics getFontMetrics(Font font) {
            return g2.getFontMetrics(font);
        }

    }

    protected class GraphicsMatrixSVG implements IGraphicsMatrix {

        private final SVGWriter writer;
        private Color color;
        private Font font;
        private Map<Color, String> cache;

        public GraphicsMatrixSVG(SVGWriter writer) {
            this.writer = writer;
            this.color = Color.BLACK;
            this.font = new Font("Arial", Font.BOLD, 12);
            this.cache = new HashMap<>();
        }

        @Override
        public void drawCell(int x, int y, int width, int height, Color color) {
            String tag = this.cache.get(color);
            if (tag == null) {
                tag = "cell" + this.cache.size();
                String cellCode = settings.getCellSytler().getSVGCell(width, height, color, tag);
                writer.writeDefLine(cellCode);
                cache.put(color, tag);
            }
            writer.writeLine("<use x=\"" + x + "\" y=\"" + y + "\" xlink:href=\"#" + tag + "\" />");
        }

        @Override
        public void drawRotatedString(int x, int y, String label, int rotation) {
            String transform = "";

            if (rotation != 0) {
                x += font.getSize();
                transform = " transform=\"rotate(" + rotation + ", " + x + ", " + y + ")\"";
            }
            String fontFamily = " font-family: " + font.getFamily() + ";";
            String fill = SVGWriter.getSVGFillColorCSS(color);
            String fontWeight = font.getStyle() == Font.BOLD ? " font-weight: bold;" : "";
            String fontStyle = font.getStyle() == Font.ITALIC ? " font-style: italic;" : "";
            String fontSize = " font-size: " + font.getSize() + "px;";
            writer.writeLine("<text x=\"" + x + "\" y=\"" + y + "\" style=\"stroke: none;" + fill + fontFamily + fontSize + fontWeight + fontStyle + "\"" + transform + ">" + SVGWriter.escapeTextContent(label) + " </text>");
        }

        @Override
        public void drawString(int x, int y, String label) {
            drawRotatedString(x, y, label, 0);
        }

        @Override
        public Rectangle getClipBounds() {
            Dimension imageSize = getImageSize();
            return new Rectangle(imageSize.width, imageSize.height);
        }

        @Override
        public void setFont(Font font) {
            this.font = font;
        }

        @Override
        public void setColor(Color color) {
            this.color = color;
        }

        @Override
        public FontMetrics getFontMetrics() {
            return getFontMetrics(font);
        }

        @Override
        public FontMetrics getFontMetrics(Font font) {
            Canvas c = new Canvas();
            FontMetrics fm = c.getFontMetrics(font);
            return fm;
        }

    }

}
