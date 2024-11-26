/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package isiknock.view.matrix.knockout;

import isiknock.view.matrix.MatrixIllustrationSettings;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.awt.image.ImageObserver;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import tools.UITools;
import isiknock.entities.matrix.IMatrix;

/**
 *
 * @author Heiko Giese <h.giese@bioinformatik.uni-frankfurt.de>
 * @param <E> The type of the values stored in the matrix.
 */
public abstract class CachedKnockoutMatrixIllustrator<E> extends KnockoutMatrixIllustrator<E> {

    private Map<Color, BufferedImage> imageCache;

    public CachedKnockoutMatrixIllustrator(IMatrix<E> matrix, MatrixIllustrationSettings<E> settings) {
        super(matrix, settings);
    }

    @Override
    public void redoLayout() {
        this.imageCache = new HashMap<>();
        Set<Color> colors = new HashSet<>();
        for (int rowIdx = 0; rowIdx < matrix.getRowCount(); rowIdx++) {
            for (int colIdx = 0; colIdx < matrix.getColumnCount(); colIdx++) {
                Color c = settings.getValueToColor().translate(matrix.getValue(rowIdx, colIdx));
                colors.add(c);
            }
        }
        for (Color color : colors) {
            if (!imageCache.containsKey(color)) {
                BufferedImage bImg = new BufferedImage(settings.getCellWidth(), settings.getCellHeight(), BufferedImage.TYPE_INT_ARGB);
                Graphics2D g2 = (Graphics2D)bImg.createGraphics();
                UITools.activateAntialiasing(g2);
                settings.getCellSytler().drawCell(0, 0, settings.getCellWidth(), settings.getCellHeight(), color, g2);
                imageCache.put(color, bImg);
            }
        }
        super.redoLayout();
    }

    @Override
    public void paintImage(Graphics2D g2) {
        super.paintImage(new GraphicsMatrixCached(g2));
    }    

    protected class GraphicsMatrixCached extends KnockoutMatrixIllustrator.GraphicsMatrix {

        public GraphicsMatrixCached(Graphics2D g2) {
            super(g2);
        }

        @Override
        public void drawCell(int x, int y, int width, int height, Color color) {
            BufferedImage bImg = imageCache.get(color);
            if (bImg == null) {
                throw new IllegalStateException("Image cache is out of sync.");
            }
            g2.drawImage(bImg, x, y, new ImageObserver() {
                @Override
                public boolean imageUpdate(Image image, int i, int i1, int i2, int i3, int i4) {
                    return false;
                }
            });
        }

    }

}
