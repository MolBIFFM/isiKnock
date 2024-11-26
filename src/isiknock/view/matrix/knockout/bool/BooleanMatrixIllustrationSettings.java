/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package isiknock.view.matrix.knockout.bool;

import java.awt.Color;
import javax.swing.JFrame;
import isiknock.view.matrix.MatrixIllustrationSettings;
import isiknock.view.matrix.MatrixIllustrationSettingsDialog;

/**
 *
 * @author Heiko Giese <h.giese@bioinformatik.uni-frankfurt.de>
 */
public class BooleanMatrixIllustrationSettings extends MatrixIllustrationSettings<Boolean> {
    
    protected Color trueColor, falseColor, nullColor;
    
    public BooleanMatrixIllustrationSettings() {
        this(Color.RED, new Color(112,173,71));
    }
    
    public BooleanMatrixIllustrationSettings(Color trueColor, Color falseColor) {
        this(trueColor, falseColor, Color.BLACK);
    }
    
    public BooleanMatrixIllustrationSettings(Color trueColor, Color falseColor, Color nullColor) {
        super((Boolean value) -> value != null ? (value ? trueColor : falseColor) : nullColor);
        this.trueColor = trueColor;
        this.falseColor = falseColor;
        this.nullColor = nullColor;
    }

    public Color getTrueColor() {
        return trueColor;
    }

    public BooleanMatrixIllustrationSettings setTrueColor(Color trueColor) {
        this.trueColor = trueColor;
        return this;
    }

    public Color getFalseColor() {
        return falseColor;
    }

    public BooleanMatrixIllustrationSettings setFalseColor(Color falseColor) {
        this.falseColor = falseColor;
        return this;
    }

    @Override
    public MatrixIllustrationSettingsDialog<Boolean> getSettingsDialog(JFrame parent) {
        return new BooleanKnockoutMatrixIllustrationSettingsDialog(this, parent, true);
    }
    
}
