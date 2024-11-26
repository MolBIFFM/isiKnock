/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package petrinet.io;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.xml.stream.XMLStreamException;
import org.sbml.jsbml.Model;
import org.sbml.jsbml.Reaction;
import org.sbml.jsbml.SBMLDocument;
import org.sbml.jsbml.SBMLReader;
import org.sbml.jsbml.Species;
import org.sbml.jsbml.SpeciesReference;
import petrinet.IPetrinet;
import petrinet.PseudoPetrinet;

/**
 *
 * @author Heiko Giese <h.giese@bioinformatik.uni-frankfurt.de>
 */
public class SBMLParser implements IPetrinetParser {

    @Override
    public IPetrinet parse(File file) throws FileNotFoundException, IOException {

        final String reversableNameSuffix = "_rev";
        
        List<String> places = new ArrayList<>();
        List<String> transitions = new ArrayList<>();
        
        Map<String, Integer> mapPlaceToIndex = new HashMap<>();
        Map<String, Integer> mapTransitionToIndex = new HashMap<>();
  
        SBMLReader reader = new SBMLReader();
        SBMLDocument doc;

        try {
            doc = reader.readSBML(file);

            Model model = doc.getModel();

            for (Species s : model.getListOfSpecies()) {
                String name = s.getName();
                String id = s.getId();
                if (name.equals("")) {
                    name = id;
                }
                mapPlaceToIndex.put(id, places.size());
                places.add(name);
            }

            for (Reaction reaction : model.getListOfReactions()) {
                String name = reaction.getName();
                String id = reaction.getId();
                if (name.equals("")) {
                    name = id;
                }
                boolean reversible = reaction.getReversible();
                
                mapTransitionToIndex.put(id, transitions.size());
                transitions.add(name);
                if(reversible) {
                    mapTransitionToIndex.put(id + reversableNameSuffix, places.size());
                    transitions.add(name + reversableNameSuffix);
                }
            }
            
            int[][] adjacencyMatrix = new int[places.size()][transitions.size()];
            int[][] multiplierMatrix = new int[places.size()][transitions.size()];
            
            for (Reaction reaction : model.getListOfReactions()) {
                String id = reaction.getId();

                int tIdx = mapTransitionToIndex.get(id);
                
                for (SpeciesReference reactant : reaction.getListOfReactants()) {
                    int pIdx = mapPlaceToIndex.get(reactant.getSpecies());
                    adjacencyMatrix[pIdx][tIdx] = 1;
                    multiplierMatrix[pIdx][tIdx] = (int)reactant.getStoichiometry();
                }
                
                for (SpeciesReference product : reaction.getListOfProducts()) {
                    int pIdx = mapPlaceToIndex.get(product.getSpecies());
                    adjacencyMatrix[pIdx][tIdx] = -1;
                    multiplierMatrix[pIdx][tIdx] = (int)product.getStoichiometry();
                }
            }
            
            return new PseudoPetrinet(places.toArray(new String[places.size()]), transitions.toArray(new String[transitions.size()]), adjacencyMatrix, multiplierMatrix);
            

        } catch (XMLStreamException ex) {
        }
        return null;
    }
    
}
