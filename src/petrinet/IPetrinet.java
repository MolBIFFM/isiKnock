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
public interface IPetrinet {
    
    public String[] getPlaces();

    public String[] getTrasitions();

    public int[][] getAdjacencyMatrix();

    public int[][] getMultiplierMatrix();
    
}
