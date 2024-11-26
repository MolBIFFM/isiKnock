/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package petrinet.io;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;
import java.util.regex.MatchResult;
import java.util.regex.Pattern;
import petrinet.IPetrinet;
import petrinet.PseudoPetrinet;

/**
 *
 * @author Heiko Giese <h.giese@bioinformatik.uni-frankfurt.de>
 * based on Code from the MonaLisa Project by Jens Einloft.
 */
public class PntParser implements IPetrinetParser {

    @Override
    public IPetrinet parse(File file) throws FileNotFoundException, IOException {

        BufferedReader reader = new BufferedReader(new FileReader(file));

        // Skip the header line, but save the name of the Petri net.
        Pattern whitespace = Pattern.compile("\\s");
        String header = reader.readLine();
        String[] headerParts = whitespace.split(header);
        String idAndNamePart = headerParts[headerParts.length - 1];

        int id = 0;
        String name = null;
        if (!idAndNamePart.contains(":")) {
            id = Integer.parseInt(idAndNamePart);
        } else {
            int indexOfDoublePoint = idAndNamePart.indexOf(":");
            id = Integer.parseInt(idAndNamePart.substring(0, indexOfDoublePoint));
            name = idAndNamePart.substring(indexOfDoublePoint + 1, idAndNamePart.length());
        }

        Map<Integer, List<Integer>> placesInArcs = new HashMap<>();
        Map<Integer, List<Integer>> placesOutArcs = new HashMap<>();

        Map<Integer, List<Integer>> placesInMultiplier = new HashMap<>();
        Map<Integer, List<Integer>> placesOutMultiplier = new HashMap<>();

        // Net structure section.
        String line;
        while (!(line = reader.readLine()).equals("@")) {
            Scanner scanner = new Scanner(line);
            int placeId = scanner.nextInt();

            Long tokens = scanner.nextLong();

            boolean hasInput = true;
            if (scanner.hasNext(",")) {
                scanner.next();
                hasInput = false;
            }

            // List of input arcs.
            if (hasInput) {
                List<Integer> inArcs = new ArrayList<>();
                placesInArcs.put(placeId, inArcs);
                List<Integer> inMultiplier = new ArrayList<>();
                placesInMultiplier.put(placeId, inMultiplier);

                boolean endOfInArcs = false;
                while (scanner.hasNext()) {
                    String scan = scanner.next();
                    int transitionId;
                    int weight = 1;
                    if (scan.contains(":")) {
                        transitionId = Integer.parseInt(scan.substring(0, scan.indexOf(":")));
                        scan = scanner.next();
                        if (scan.contains(",")) {
                            weight = Integer.parseInt(scan.substring(0, scan.indexOf(",")));
                            endOfInArcs = true;
                        } else {
                            weight = Integer.parseInt(scan);
                        }
                    } else if (scan.contains(",")) {
                        transitionId = Integer.parseInt(scan.substring(0, scan.indexOf(",")));
                        endOfInArcs = true;
                    } else {
                        transitionId = Integer.parseInt(scan);
                    }

                    inArcs.add(transitionId);
                    inMultiplier.add(weight);

                    if (endOfInArcs) {
                        break;
                    }
                }
            }

            // List of output arcs.
            List<Integer> outArcs = new ArrayList<>();
            placesOutArcs.put(placeId, outArcs);
            List<Integer> outMultiplier = new ArrayList<>();
            placesOutMultiplier.put(placeId, outMultiplier);
            while (scanner.hasNext()) {
                String token = scanner.next();
                int transitionId;
                int weight = 1;
                if (token.contains(":")) {
                    transitionId = Integer.parseInt(token.substring(0, token.indexOf(":")));
                    weight = Integer.parseInt(scanner.next());
                } else {
                    transitionId = Integer.parseInt(token);
                }
                
                outArcs.add(transitionId);
                outMultiplier.add(weight);
            }
        }

        
        
        // Skip header line.
        reader.readLine();

        // Place data section.
        Set<Integer> placeIDs = new HashSet<>(placesInArcs.size()); //place IDs should be unique
        List<String> placeNames = new ArrayList<>(placesInArcs.size()); //place names should but may not be unique
                
        while (!(line = reader.readLine()).equals("@")) {
            Scanner scanner = new Scanner(line);
            scanner.next("(\\d+)\\s*:");
            MatchResult idMatch = scanner.match();
            
            int placeId = Integer.parseInt(idMatch.group(1));
            String placeName = scanner.next();
            int capacity = scanner.hasNext("oo") && scanner.next("oo") != null ? -1 : scanner.nextInt();
            int time = scanner.nextInt();
            
            placeIDs.add(placeId);
            placeNames.add(placeName);
        }

        // Skip header line.
        reader.readLine();

        // Transition data section.
        List<String> transitionNames = new ArrayList<>();
        while (!(line = reader.readLine()).equals("@")) {
            Scanner scanner = new Scanner(line);
            scanner.next("(\\d+)\\s*:");
            MatchResult idMatch = scanner.match();
            
            int transitionId = Integer.parseInt(idMatch.group(1));
            String transitionName = scanner.next();
            int priority = scanner.nextInt();
            int time = scanner.nextInt();
            
            transitionNames.add(transitionName);
        }
        
        int[][] adjecencyMatrix = new int[placeNames.size()][transitionNames.size()];
        int[][] multiplierMatrix = new int[placeNames.size()][transitionNames.size()];

        for (Integer pID : placeIDs) {
            List<Integer> inArcs = placesInArcs.get(pID);
            if(inArcs != null) {
                List<Integer> inMultiplier = placesInMultiplier.get(pID);
                for (int i = 0; i < inArcs.size(); i++) {
                    final int tID = inArcs.get(i);
                    adjecencyMatrix[pID][tID] = -1;
                    multiplierMatrix[pID][tID] = inMultiplier.get(i);
                }
            }
            
            List<Integer> outArcs = placesOutArcs.get(pID);
            if(outArcs != null) {
                List<Integer> outMultiplier = placesOutMultiplier.get(pID);
                for (int i = 0; i < outArcs.size(); i++) {
                    final int tID = outArcs.get(i);
                    adjecencyMatrix[pID][tID] = 1;
                    multiplierMatrix[pID][tID] = outMultiplier.get(i);
                }
            }
        }
        
        return new PseudoPetrinet(
                placeNames.toArray(new String[placeNames.size()]), 
                transitionNames.toArray(new String[transitionNames.size()]), 
                adjecencyMatrix, 
                multiplierMatrix);

    }
    
    

}
