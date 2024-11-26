/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package isiknock.view.matrix;

import isiknock.IsiKnock;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import tools.Strings;
import tools.UITools;
import tools.extendedswing.ChangeNameDialog;

/**
 *
 * @author Heiko Giese <h.giese@bioinformatik.uni-frankfurt.de>
 * @param <E>
 * @param <F> The type of value stored in the displayed matrix.
 */
public abstract class MatrixPanel<E extends IMatrixIllustrator<F>, F> extends JPanel implements MouseListener, MouseMotionListener, MouseWheelListener, KeyListener {

    protected final E matrixIllustrator;

    public boolean openSettingsDialog(JFrame parent) {
        MatrixIllustrationSettings<F> settings = matrixIllustrator.getSettings();
        MatrixIllustrationSettingsDialog<F> settingsDialog = settings.getSettingsDialog(parent);
        settingsDialog.setVisible(true);
        if (settingsDialog.isDone()) {
            MatrixIllustrationSettings<F> newSettings = settingsDialog.getSettings();
            if (newSettings != null) {
                matrixIllustrator.setSettings(newSettings);
                this.clearImageCache();
                this.revalidate();
                this.repaint();
                return true;
            }
        }
        return false;
    }

    public Component[] getViewMenuExtensions() {
        return null;
    }

    private Thread cacheThread;

    public MatrixPanel(E matrixIllustrator) {
        this.matrixIllustrator = matrixIllustrator;
        this.setBackground(Color.WHITE);
    }

    private final int bufferLevel = 3;
    private final int minbufferLevel = 2;
    Rectangle drawBounds;
    Rectangle imageCacheBounds;
    BufferedImage imageCache;

    @Override
    protected void paintComponent(Graphics grphcs) {
        super.paintComponent(grphcs);
        Rectangle clipBounds = grphcs.getClipBounds();
        if (imageCache == null || imageCacheBounds == null || !imageCacheBounds.contains(clipBounds)) {
            if (IsiKnock.DEBUG) {
                System.out.println("no cached image available -> painting immediately");
            }
            this.matrixIllustrator.paintImage((Graphics2D) grphcs);
        } else {
            ((Graphics2D) grphcs).drawImage(imageCache, imageCacheBounds.x, imageCacheBounds.y, this);
        }
        this.drawOverlay(grphcs);
        this.drawBounds = getBounds();

        if (isImageCacheUpdateRequired()) {
            requestImageCacheUpdateIfNeeded();
        }
    }

    private Rectangle createCacheBounds(Rectangle view, Dimension canvasSize, int cacheLevel) {
        int x = Math.max(0, view.x - ((cacheLevel * view.width) / 2));
        int y = Math.max(0, view.y - ((cacheLevel * view.height) / 2));
        int width = Math.min(canvasSize.width, cacheLevel * view.width);
        int height = Math.min(canvasSize.height, cacheLevel * view.height);

        return new Rectangle(x, y, width, height);
    }

    private boolean isImageCacheUpdateRequired() {
        return drawBounds == null || imageCache == null || imageCacheBounds == null || !imageCacheBounds.contains(createCacheBounds(drawBounds, this.matrixIllustrator.getImageSize(), minbufferLevel));
    }

    private synchronized void requestImageCacheUpdate() {
        this.clearImageCache();
        this.requestImageCacheUpdateIfNeeded();
    }

    private synchronized void requestImageCacheUpdateIfNeeded() {
        if (cacheThread == null || !cacheThread.isAlive()) {
            cacheThread = new Thread() {
                @Override
                public void run() {
                    super.run(); //To change body of generated methods, choose Tools | Templates.

                    while (isImageCacheUpdateRequired()) {
                        updateImageCache();
                    }
                }

            };
            cacheThread.start();
        }
    }

    private synchronized void updateImageCache() {
        Rectangle newCacheBounds = createCacheBounds(drawBounds, this.matrixIllustrator.getImageSize(), bufferLevel);
        BufferedImage newImageCache = new BufferedImage(newCacheBounds.width, newCacheBounds.height, BufferedImage.TYPE_INT_ARGB);
        this.matrixIllustrator.paintImage(newImageCache.createGraphics(), newCacheBounds);
        this.setImageCache(newImageCache, newCacheBounds);
    }

    protected synchronized void setImageCache(BufferedImage imageCache, Rectangle imageCacheBounds) {
        this.imageCache = imageCache;
        this.imageCacheBounds = imageCacheBounds;
        if (IsiKnock.DEBUG) {
            System.out.println("image cache updated: " + imageCacheBounds);
        }
    }

    protected synchronized void clearImageCache() {
        this.setImageCache(null, null);
    }

    @Override
    public Dimension getPreferredSize() {
        return matrixIllustrator.getImageSize();
    }

    private BufferedImage infoBox;
    private Point infoBoxPos;

    public void setInfo(Point p, String tip) {
        setInfo(p, tip == null ? null : new String[][]{
            new String[]{
                tip
            }
        });
    }

    public void setInfo(Point p, String tip, int x, int y) {
        setInfo(p, tip == null ? null : new String[][]{
            new String[]{
                tip
            }
        }, x, y);
    }

    public void setInfo(Point p, String[][] infoText, int row, int column) {
        if (infoText == null) {
            infoText = this.getDefaultInfoText(row, column);
        }
        for (int i = 0; i < infoText.length; i++) {
            for (int j = 0; j < infoText[i].length; j++) {
                infoText[i][j] = insertTags(infoText[i][j], row, column);
            }
        }
        this.infoBoxPos = p;
        this.infoBox = createOverlay(infoText);
        this.repaint();
    }

    public void setInfo(Point p, String[][] tipText) {
        this.infoBoxPos = p;
        this.infoBox = createOverlay(tipText);
        this.repaint();
    }

    private void drawOverlay(Graphics grphcs) {
//        System.out.println("overlay");
        if (infoBox != null && infoBoxPos != null) {
            Rectangle bounds = this.getVisibleRect();

            int mouseOffsetX = 20;
            int mouseOffsetY = 20;

            int x = infoBoxPos.x + mouseOffsetX;
            int y = infoBoxPos.y + mouseOffsetY;

            // Fit the info to the panel
            if (x + infoBox.getWidth() > (bounds.x + bounds.width)) {
                x = bounds.x + bounds.width - infoBox.getWidth();
            }
            if (y + infoBox.getHeight() > (bounds.y + bounds.height)) {
                y = bounds.y + bounds.height - infoBox.getHeight();
            }
            grphcs.drawImage(infoBox, x, y, this);
        }
    }

    private BufferedImage createOverlay(String[][] infoText) {
        if (infoText != null && infoBoxPos != null) {

            Font font = getFont().deriveFont(12f);
            Font fontBold = font.deriveFont(Font.BOLD);

            int textOffset = 4;
            int shadow = 6;

            int x = shadow;
            int y = shadow;

            int columns = 0;
            for (int i = 0; i < infoText.length; i++) {
                columns = Math.max(infoText[i].length, columns);
            }

            int[] maxWidths = new int[columns];
            for (int i = 0; i < infoText.length; i++) {
                for (int j = 0; j < infoText[i].length; j++) {
                    String text = infoText[i][j];
                    Font curFont;
                    if (j == 0) {
                        curFont = fontBold;
                    } else {
                        curFont = font;
                    }
                    Rectangle2D sbounds = Strings.getStringBounds(text, curFont);

                    int textWidth = (int) Math.ceil(sbounds.getWidth());
                    maxWidths[j] = Math.max(textWidth, maxWidths[j]);
                }
            }
            int textBoxWidth = 0;
            for (int maxWidth : maxWidths) {
                textBoxWidth += maxWidth;
            }
            textBoxWidth += (columns - 1) * textOffset;

            int lineHeight = (int) Math.ceil(Strings.getStringBounds("|%&!§$(){}?<>", font).getHeight()) + textOffset;

            int boxWidth = textBoxWidth + (2 * textOffset);
            int boxHeight = (infoText.length * lineHeight) + (2 * textOffset);
            int boxWidthPlusShadow = boxWidth + 2 * shadow;
            int boxHeightPlusShadow = boxHeight + 2 * shadow;

            BufferedImage bImg = new BufferedImage(boxWidthPlusShadow, boxHeightPlusShadow, BufferedImage.TYPE_INT_ARGB);

            Graphics2D g2 = (Graphics2D) bImg.createGraphics();
            g2.setFont(font);
            RenderingHints oldHints = UITools.activateAntialiasing(g2);

            UITools.drawShadow(g2, new Rectangle(x - shadow, y - shadow, boxWidth + (2 * shadow), boxHeight + (2 * shadow)), shadow, new Color(255, 255, 255, 240));
            g2.setColor(Color.BLACK);
            x += textOffset;
            y -= 2;
            for (int i = 0; i < infoText.length; i++) {
                int xOffset = 0;
                y += lineHeight;
                for (int j = 0; j < infoText[i].length; j++) {
                    if (j == 0) {
                        g2.setFont(g2.getFont().deriveFont(Font.BOLD));
                    } else {
                        g2.setFont(g2.getFont().deriveFont(Font.PLAIN));
                    }
                    g2.drawString(infoText[i][j], x + xOffset, y);
                    xOffset += maxWidths[j] + textOffset;
                }

            }

            g2.setRenderingHints(oldHints);
            return bImg;
        }
        return null;
    }

    protected String[][] getDefaultInfoText(int row, int column) {
        return new String[][]{
            new String[]{"x:", "#columnLabel#"},
            new String[]{"y:", "#rowLabel#"},
            new String[]{"value:", "#cellValue#"}
        };
    }

    protected String insertTags(String text, int row, int column) {
        String changedText = text
                .replaceAll("#rowID#", String.valueOf(row))
                .replaceAll("#rowNumber#", String.valueOf(row + 1))
                .replaceAll("#rowLabel#", this.getRowLabel(row))
                .replaceAll("#columnID#", String.valueOf(column))
                .replaceAll("#columnNumber#", String.valueOf(column + 1))
                .replaceAll("#columnLabel#", this.getColumnLabel(column))
                .replaceAll("#cellValue#", this.matrixIllustrator.getValueAsString(row, column));
        return changedText;
    }

    public void setRowLabel(int rowIndex, String label) {

        matrixIllustrator.setRowLabel(rowIndex, label);
        this.invalidate();
        this.repaint();
    }

    public String getRowLabel(int rowIndex) {
        return matrixIllustrator.getRowLabel(rowIndex);
    }

    public void setColumnLabel(int columnIndex, String label) {
        matrixIllustrator.setColumnLabel(columnIndex, label);
        this.invalidate();
        this.repaint();
    }

    public String getColumnLabel(int columnIndex) {
        return matrixIllustrator.getColumnLabel(columnIndex);
    }

    public int getRowCount() {
        return matrixIllustrator.getRowCount();
    }

    public int getColumnCount() {
        return matrixIllustrator.getColumnCount();
    }

    public MatrixIllustrationSettings getSettings() {
        return matrixIllustrator.getSettings();
    }

    int translateCoordinatesToColumnIndex(int x) {
        return matrixIllustrator.translateCoordinatesToColumnIndex(x);
    }

    int translateCoordinatesToRowIndex(int y) {
        return matrixIllustrator.translateCoordinatesToRowIndex(y);
    }

    public void saveAsSVG(File saveTo) throws IOException {
        matrixIllustrator.saveAsSVG(saveTo);
    }

    public void saveAsPNG(File saveTo) throws IOException {
        try {
            matrixIllustrator.saveAsPNG(saveTo);
        } catch (OutOfMemoryError er) {
            JOptionPane.showMessageDialog(MatrixPanel.this.getRootPane(), "Couldn't save image: Image is to large - " + isiknock.IsiKnock.APP_NAME + " needs more memory.", "Couldn't save image", JOptionPane.ERROR_MESSAGE);
        }
    }

    public void saveAsCSV(File saveTo) throws IOException {
        matrixIllustrator.saveAsCSV(saveTo);
    }

    private Point lastMousePos = new Point();
    private final Point infoMatrixPos = new Point(-1, -1);
    private boolean showInfo = false;

    protected void handleEditLable(int row, int column) {
        if (row < 0 && 0 <= column && column < getColumnCount()) {
            String oldName = this.getColumnLabel(column);
            String newName = changeName(oldName);
            if (!oldName.equals(newName)) {
                this.setColumnLabel(column, newName);
                this.requestImageCacheUpdate();
                this.revalidate();
            }
        } else if (column < 0 && 0 <= row && row < getRowCount()) {
            String oldName = this.getRowLabel(row);
            String newName = changeName(oldName);
            if (!oldName.equals(newName)) {
                this.setRowLabel(row, newName);
                this.requestImageCacheUpdate();
                this.revalidate();
            }
        }
    }

    private void updateInfo(Point lastMousePos, boolean isAltDown) {
        if (isAltDown) {
            int column = this.translateCoordinatesToColumnIndex(lastMousePos.x);
            int row = this.translateCoordinatesToRowIndex(lastMousePos.y);

            if (infoMatrixPos.x == column && infoMatrixPos.y == row) {
                return;
            }
            if (0 <= row && row < getRowCount() && 0 <= column && column < getColumnCount()) {
                setInfo(lastMousePos, (String) null, row, column);
                infoMatrixPos.x = column;
                infoMatrixPos.y = row;
            } else {
                setInfo(null, (String) null);
                infoMatrixPos.x = -1;
                infoMatrixPos.y = -1;
            }
        } else {
            setInfo(null, (String) null);
            infoMatrixPos.x = -1;
            infoMatrixPos.y = -1;
        }
    }

    private String changeName(String oldName) {
        ChangeNameDialog dia = new ChangeNameDialog(oldName, (Frame) this.getTopLevelAncestor(), true);
        dia.setVisible(true);
        if (dia.getState() == ChangeNameDialog.STATE.DONE) {
            return dia.getSelectedName();
        }
        return oldName;
    }

    @Override
    public void mouseClicked(MouseEvent me) {
        if (me.getClickCount() == 2) {
            int column = this.translateCoordinatesToColumnIndex(me.getX());
            int row = this.translateCoordinatesToRowIndex(me.getY());
            handleEditLable(row, column);
        }
    }

    @Override
    public void mousePressed(MouseEvent me) {

    }

    @Override
    public void mouseReleased(MouseEvent me) {

    }

    @Override
    public void mouseEntered(MouseEvent me) {
    }

    @Override
    public void mouseExited(MouseEvent me) {
    }

    @Override
    public void mouseDragged(MouseEvent me) {
    }

    @Override
    public void mouseMoved(MouseEvent me) {
        this.lastMousePos = me.getPoint();
        updateInfo(me.getPoint(), me.isAltDown());
    }

    @Override
    public void mouseWheelMoved(MouseWheelEvent mwe) {
        if(!IsiKnock.DEBUG) return;
        if (mwe.isControlDown()) {
            final double step = 0.01;
            int unitsToScroll = -mwe.getUnitsToScroll();
            this.matrixIllustrator.getSettings().setScale(unitsToScroll * step + this.matrixIllustrator.getSettings().getScale());
            this.matrixIllustrator.redoLayout();
            this.clearImageCache();
            this.revalidate();
            this.repaint();
        }
    }

    @Override
    public void keyTyped(KeyEvent ke) {
    }

    @Override
    public void keyPressed(KeyEvent ke) {
        if (ke.getKeyCode() == KeyEvent.VK_ALT) {
            if (!showInfo) {
                showInfo = true;
                updateInfo(lastMousePos, showInfo);
            }
        }
    }

    @Override
    public void keyReleased(KeyEvent ke) {
        if (ke.getKeyCode() == KeyEvent.VK_ALT) {
            if (showInfo) {
                showInfo = false;
                updateInfo(lastMousePos, showInfo);
            }
        }
    }

}
