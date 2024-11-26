/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tools;

import java.awt.Font;
import java.awt.font.FontRenderContext;
import java.awt.geom.Rectangle2D;

/**
 *
 * @author Heiko Giese <h.giese@bioinformatik.uni-frankfurt.de>
 */
public class Strings {
    public static Rectangle2D getStringBounds(String text, Font font) {
        return getStringBounds(text, font, new FontRenderContext(null, false, false));
    }

    public static Rectangle2D getStringBounds(String text, Font font, FontRenderContext frc) {
        return font.getStringBounds(text, frc);
    }
}
