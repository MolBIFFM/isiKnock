/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package isiknock;

import isiknock.language.Language;
import isiknock.view.MainWindow;
import javax.swing.JOptionPane;

/**
 *
 * @author Heiko Giese <h.giese@bioinformatik.uni-frankfurt.de>
 */
public class IsiKnock {
 
    public static final String APP_NAME = "isiKnock";
    public static final String APP_VERSION = "1.0.2";
    public static final String HELP_URL = "http://www.bioinformatik.uni-frankfurt.de/tools/isiKnock";
    
    public static boolean DEBUG = false;
    
    
    public static void main(String[] args) {
        Language.CURRENT = Language.DEFAULT;
     
        MainWindow mw  = new MainWindow();
        mw.setLocationRelativeTo(null);
        mw.setVisible(true);
        
        if(DEBUG) {
            JOptionPane.showMessageDialog(mw, APP_NAME + " is running in debug mode", "Debug Mode Enabled", JOptionPane.INFORMATION_MESSAGE);
        }
    }
            
    
}
