/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package knockoutmatrix;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Inv_parser {

    /**
     * Parses an invariant calculation file.
     * @param filepath
     * @return 
     */
    public static List<List<String>> parse (String filepath) {
        
        List<List<String>> tinvs = new ArrayList<>();
            
        try {
            File file = new File(filepath);
            if (isiknock.IsiKnock.DEBUG) {
                System.out.println("parsing inv file: " + filepath);
            }

            List<String> lines = new ArrayList<>();
            try (BufferedReader br = new BufferedReader(new FileReader(file))) {
                for (String line; (line = br.readLine()) != null;) {
                    lines.add(line);
                }
            }

            for (String line : lines) {
                int identifierEndPosition = line.indexOf(":");
                if (identifierEndPosition > -1) {
                    line = line.substring(identifierEndPosition+1).trim();
                    List<String> tinv = new ArrayList<>();
                    
                    String[] transitions = line.split(" ", -1);
                    for (String transition : transitions) {
                        transition = transition.trim();
                        if (!transition.isEmpty()) {
                            tinv.add(transition);
                        }
                    }
                    tinvs.add(tinv);
                }
            }
        } catch (IOException e) {
        }
        return tinvs;
    }
    
}
