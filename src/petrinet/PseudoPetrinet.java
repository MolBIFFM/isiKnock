/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package petrinet;

/**
 *
 * @author Heiko Giese <h.giese@bioinformatik.uni-frankfurt.de>
 */
public class PseudoPetrinet implements IPetrinet {
    
    private final String[] places, transitions;
    private final int[][] adjacencyMatrix, multiplierMatrix;

    public PseudoPetrinet(String[] places, String[] transitions, int[][] adjacencyMatrix, int[][] multiplierMatrix) {
        this.places = places;
        this.transitions = transitions;
        this.adjacencyMatrix = adjacencyMatrix;
        this.multiplierMatrix = multiplierMatrix;
    }

    @Override
    public String[] getPlaces() {
        return places;
    }

    @Override
    public String[] getTrasitions() {
        return transitions;
    }

    @Override
    public int[][] getAdjacencyMatrix() {
        return adjacencyMatrix;
    }

    @Override
    public int[][] getMultiplierMatrix() {
        return multiplierMatrix;
    }
    
    
    
}
