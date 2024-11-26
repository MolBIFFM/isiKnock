/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package petrinet.io;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import petrinet.IPetrinet;

/**
 *
 * @author Heiko Giese <h.giese@bioinformatik.uni-frankfurt.de>
 */
public interface IPetrinetParser {
    
    public IPetrinet parse(File file) throws FileNotFoundException, IOException;
    
}
