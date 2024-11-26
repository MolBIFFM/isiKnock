/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package tools;

import java.awt.Color;
import java.io.FileWriter;
import java.io.IOException;
import java.io.StringWriter;

/**
 *
 * @author Heiko Giese <h.giese@bioinformatik.uni-frankfurt.de>
 */
public class SVGWriter extends FileWriter {

    private int width, height;
    protected boolean unusedHead = true;
    protected boolean unusedBody = true;
    protected boolean unusedDef = true;
    protected StringWriter head, body, def;
    
    public SVGWriter(String string, int width, int height) throws IOException {
        this(string, width, height, false);
    }
    
    public SVGWriter(String string) throws IOException {
        this(string, 0, 0, false);
    }

    public SVGWriter(String string , int width, int height, boolean bln) throws IOException {
        super(string, bln);
        this.head = new StringWriter();
        this.def =   new StringWriter();
        this.body =   new StringWriter();
        this.setSize(width, height);
    }
    
    protected SVGWriter(String string, boolean bln) throws IOException {
        super(string, bln);
    }

    public final void setSize(int width, int height) {
        this.width = width;
        this.height = height;
    }
    
    protected void writeHead(int width, int height) {
        this.writeHeadLine("<?xml version=\"1.0\" encoding=\"utf-8\"  standalone=\"no\"?>");
        this.writeHeadLine("<!DOCTYPE svg PUBLIC \"-//W3C//DTD SVG 1.1//EN\" \"http://www.w3.org/Graphics/SVG/1.1/DTD/svg11.dtd\">");
        this.writeHeadLine("<svg viewBox=\"0 0 " + width + " " + height + "\" xmlns=\"http://www.w3.org/2000/svg\" xmlns:xlink=\"http://www.w3.org/1999/xlink\">");
    }
    
    public void writeHeadLine(String line) {
        if(unusedHead){
            unusedHead = false;
        } else {
            head.write("\n");
        } 
        head.write(line);
    }
    
    public void writeDefLine(String line) {
        if(unusedDef){
            unusedDef = false;
        } else {
            def.write("\n");
        } 
        def.write("\t\t" + line);
    }
    
    public void writeLine(String line) {
        if(unusedBody){
            unusedBody = false;
        } else {
            body.write("\n");
        } 
        body.write("\t" + line);
    }

    @Override
    public void close() throws IOException {
        this.writeHead(width, height);
        this.write(head.toString() + "\n");
        head.close();
        
        if(!unusedDef) {
            this.write("\t<defs>" + "\n");
            this.write(def.toString() + "\n");
            this.write("\t</defs>" + "\n");
            def.close();
        }
        
        this.write(body.toString() + "\n");
        body.close();
        this.write("</svg>");
        super.close();
    }
    
    public static final String getSVGFillColorCSS(Color c) {
        return getSVGColorCSS(c, "fill");
    }
    
    public static final String getSVGStrokeColorCSS(Color c) {
        return getSVGColorCSS(c, "stroke");
    }
    
    public static final String getSVGColorCSS(Color c, String tag) {
        float opacity = c.getAlpha() / 255f;
        String color = tag + ": rgb(" + c.getRed() + ", " + c.getGreen() + ", " + c.getBlue() + "); " + tag + "-opacity: " + opacity + ";";
        return color;
    }

    public static final String escapeTextContent(String text) {
        text = text.replace("<", "&lt;");
        text = text.replace(">", "&gt;");
        text = text.replace("&", "&amp;");
        return text;
    } 
    
}
