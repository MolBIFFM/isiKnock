/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package isiknock.view.matrix.cellstyler;

import java.awt.Color;
import java.awt.Graphics;

/**
 *
 * @author Heiko Giese <h.giese@bioinformatik.uni-frankfurt.de>
 */
public interface ICellStyler {
    
    public void drawCell(int x, int y, int width, int height, Color c, Graphics g);
    public String getSVGCell(int width, int height, Color c, String id);
    
}
