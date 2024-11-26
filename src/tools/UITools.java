/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tools;

import java.awt.AlphaComposite;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Composite;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.RadialGradientPaint;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.Stroke;
import java.awt.image.BufferedImage;
import javax.swing.JComponent;

/**
 *
 * @author Heiko Giese <h.giese@bioinformatik.uni-frankfurt.de>
 */
public class UITools {
    public static RenderingHints activateAntialiasing(Graphics g) {
        return activateAntialiasing((Graphics2D) g);
    }

    public static RenderingHints activateAntialiasing(Graphics2D g2) {
        RenderingHints hints = g2.getRenderingHints();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);
        return hints;
    }
    
    public static void drawShadow(Graphics2D g2, JComponent c, int selectioneffectsize) {
        drawShadow(g2, new Rectangle(0, 0, c.getWidth(), c.getHeight()), selectioneffectsize, c.getBackground());
    }

    public static void drawShadow(Graphics2D g2, Rectangle bounds, int selectioneffectsize, Color background) {

        int doubleselectioneffectsize = selectioneffectsize * 2;

        RenderingHints oldHints = activateAntialiasing(g2);
        Paint oldPaint = g2.getPaint();
        Stroke oldStroke = g2.getStroke();

//        g2.setPaint(xSelectionOverlay);
//        g2.fillRect(r.x, r.y, r.width, r.height); //overlay
        Float modCorner = 0.2f;
        Float mod = (doubleselectioneffectsize * modCorner);
        Color xFadeFrom = new Color(40, 40, 40, 200);
        Color xFadeTo = new Color(40, 40, 40, 0);
//        Color xFadeFrom = new Color(255, 0, 0, 200);
//        Color xFadeTo = new Color(255, 0, 0, 0);

        int x = bounds.x;
        int y = bounds.y;
        Graphics2D gDraw;
        boolean alphaBlend = false;
        BufferedImage cache = null;
        if (background.getAlpha() < 255) {
            alphaBlend = true;
            cache = new BufferedImage(bounds.width, bounds.height, BufferedImage.TYPE_INT_ARGB);
            gDraw = cache.createGraphics();
            UITools.activateAntialiasing(gDraw);
            x = 0;
            y = 0;
        } else {
            gDraw = g2;
        }

        Rectangle r = new Rectangle(x + doubleselectioneffectsize - 1, y + doubleselectioneffectsize - 1, bounds.width - (2 * doubleselectioneffectsize) + 1, bounds.height - (2 * doubleselectioneffectsize) + 1);

        //draw borders 
        gDraw.setPaint(new GradientPaint(r.x, r.y - mod, xFadeFrom, r.x, r.y - doubleselectioneffectsize, xFadeTo));
        gDraw.fillRect(r.x, r.y - doubleselectioneffectsize, r.width, doubleselectioneffectsize); //N

        gDraw.setPaint(new GradientPaint(r.x + r.width + mod, r.y, xFadeFrom, r.x + r.width + doubleselectioneffectsize, r.y, xFadeTo));
        gDraw.fillRect(r.x + r.width, r.y, doubleselectioneffectsize, r.height); //E

        gDraw.setPaint(new GradientPaint(r.x, r.y + r.height + mod, xFadeFrom, r.x, r.y + r.height + doubleselectioneffectsize, xFadeTo));
        gDraw.fillRect(r.x, r.y + r.height, r.width, doubleselectioneffectsize); //S

        gDraw.setPaint(new GradientPaint(r.x - mod, r.y, xFadeFrom, r.x - doubleselectioneffectsize, r.y, xFadeTo));
        gDraw.fillRect(r.x - doubleselectioneffectsize, r.y, doubleselectioneffectsize, r.height); //W

        //draw corners
        gDraw.setPaint(new RadialGradientPaint(r.x + r.width, r.y, doubleselectioneffectsize, new float[]{modCorner, 1.0f}, new Color[]{xFadeFrom, xFadeTo}));
        gDraw.fillRect(r.x + r.width, r.y - doubleselectioneffectsize, doubleselectioneffectsize, doubleselectioneffectsize); //NE

        gDraw.setPaint(new RadialGradientPaint(r.x + r.width, r.y + r.height, doubleselectioneffectsize, new float[]{modCorner, 1.0f}, new Color[]{xFadeFrom, xFadeTo}));
        gDraw.fillRect(r.x + r.width, r.y + r.height, doubleselectioneffectsize, doubleselectioneffectsize); //SE

        gDraw.setPaint(new RadialGradientPaint(r.x, r.y + r.height, doubleselectioneffectsize, new float[]{modCorner, 1.0f}, new Color[]{xFadeFrom, xFadeTo}));
        gDraw.fillRect(r.x - doubleselectioneffectsize, r.y + r.height, doubleselectioneffectsize, doubleselectioneffectsize); //SW

        gDraw.setPaint(new RadialGradientPaint(r.x, r.y, doubleselectioneffectsize, new float[]{modCorner, 1.0f}, new Color[]{xFadeFrom, xFadeTo}));
        gDraw.fillRect(r.x - doubleselectioneffectsize, r.y - doubleselectioneffectsize, doubleselectioneffectsize, doubleselectioneffectsize); //NW

        gDraw.setPaint(oldPaint);

        Rectangle inside = new Rectangle(r.x - selectioneffectsize, r.y - selectioneffectsize, r.width + doubleselectioneffectsize + 1, r.height + doubleselectioneffectsize + 1);
        gDraw.setColor(new Color(background.getRed(), background.getGreen(), background.getBlue(), 255));
        gDraw.fillRoundRect(inside.x, inside.y, inside.width, inside.height, doubleselectioneffectsize, doubleselectioneffectsize);

        int strokesize = 2;
        gDraw.setStroke(new BasicStroke(strokesize, BasicStroke.CAP_BUTT, BasicStroke.JOIN_ROUND));
        gDraw.setColor(xFadeFrom);
//        g2.setColor(Color.GREEN);
        gDraw.drawRoundRect(inside.x - (strokesize - 1), inside.y - (strokesize - 1), inside.width + (2 * (strokesize - 1)) - 1, inside.height + (2 * (strokesize - 1)) - 1, doubleselectioneffectsize, doubleselectioneffectsize);

        gDraw.setRenderingHints(oldHints);
        gDraw.setPaint(oldPaint);
        gDraw.setStroke(oldStroke);

        if (alphaBlend) {
            Composite oldComposite = g2.getComposite();
            AlphaComposite composite = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, Mathematics.clamp(((float) background.getAlpha() / 255f), 0f, 255f));
            g2.setComposite(composite);
            g2.drawImage(cache, bounds.x, bounds.y, null);
            g2.setComposite(oldComposite);
        }

    }
    
}
