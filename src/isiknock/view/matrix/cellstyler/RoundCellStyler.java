/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package isiknock.view.matrix.cellstyler;

import java.awt.Color;
import java.awt.Graphics;
import tools.SVGWriter;

/**
 *
 * @author Heiko Giese <h.giese@bioinformatik.uni-frankfurt.de>
 */
public class RoundCellStyler implements ICellStyler {

    @Override
    public void drawCell(int x, int y, int width, int height, Color c, Graphics g) {
        int min = Math.min(width, height);
        int border = Math.max(3, ((int) Math.round(min * 0.1 / 3)) * 3);
        int borderHalf = border / 3;

        Color cBorder = c.darker().darker();
        g.setColor(cBorder);
        g.fillOval(x, y, width, height);

        g.setColor(c.darker());
        g.fillOval(x + borderHalf, y + borderHalf, width - (2 * borderHalf), height - (2 * borderHalf));

        g.setColor(c);
        g.fillOval(x + (2 * borderHalf), y + (2 * borderHalf), width - (4 * borderHalf), height - (4 * borderHalf));
    }

    @Override
    public String getSVGCell(int width, int height, Color c, String id) {
        int min = Math.min(width, height);
        float r = min / 2f;
        int border = Math.max(3, ((int) Math.round(min * 0.1 / 3)) * 3);
        float x = r;
        float y = r;
        
        String fill = SVGWriter.getSVGFillColorCSS(c);
        String stroke = SVGWriter.getSVGStrokeColorCSS(c.darker());
        
        String tag = "<circle id=\"" + id + "\" cx=\"" + x + "\" cy=\"" + y + "\" r=\"" + r + "\" style=\"" + stroke + " stroke-width: " + border + ";" + fill + "\" />";
        return tag;
    }

}
