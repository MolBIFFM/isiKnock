package knockoutmatrix;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.file.AccessDeniedException;
import java.nio.file.NoSuchFileException;
import java.util.List;

/**
 * This class takes a adjacencymatrix, a list of places and a list of
 * transitions and creates a pnt file as output.
 *
 * @author boerje
 */
public class PntCreator {

    public static void createPntFile(File saveTo, List<List<Integer>> matrix, List<String> places, List<String> transitions, List<List<Integer>> multiplierMatrix) throws IOException, NoSuchFileException, AccessDeniedException {

        if(saveTo == null) {
            throw new NoSuchFileException(null);
        } else if(!saveTo.canWrite()) {
            throw new AccessDeniedException(saveTo.getAbsolutePath());
        }
         
        FileOutputStream fos = new FileOutputStream(saveTo);
        BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(fos));

        //write Petri net file
        writer.write("P   M   PRE,POST  NETZ 0");
        writer.newLine();

        for (int i = 0; i < places.size(); i++) { //write the input & output-part
            String input = "";
            String output = "";
            String line = " " + i + " 0 ";
            for (int j = 0; j < matrix.get(i).size(); j++) {
                if (matrix.get(i).get(j) == -1) { //input
                    if (multiplierMatrix.get(i).get(j) != 1) {
                        input += j + ": " + multiplierMatrix.get(i).get(j) + " ";
                    } else {
                        input += j + " ";
                    }
                } else if (matrix.get(i).get(j) == 1) { //output
                    if (multiplierMatrix.get(i).get(j) != 1) {
                        output += j + ": " + multiplierMatrix.get(i).get(j) + " ";
                    } else {
                        output += j + " ";
                    }
                }
            }
            if (input.length() >= 1) {
                writer.write(line + input.substring(0, input.length() - 1) + ", " + output);
                writer.newLine();
            } else {
                writer.write(line + ", " + output);
                writer.newLine();
            }
        }

        writer.write("@");
        writer.newLine();
        writer.write("place nr.             name capacity time");
        writer.newLine();

        for (int i = 0; i < places.size(); i++) { //write the places
            String str = "";
            int blanks = 17 - places.get(i).length();
            String padded = " ";
            if (blanks > 1) {
                padded = String.format("%-" + blanks + "s", str);
            }

            writer.write("       " + i + ": " + places.get(i) + padded + "oo 0");
            writer.newLine();

        }

        writer.write("@");
        writer.newLine();
        writer.write("trans nr.             name priority time");
        writer.newLine();

        for (int i = 0; i < transitions.size(); i++) { //write the transitions
            String str = "";
            int blanks = 17 - transitions.get(i).length();
            String padded = " ";
            if (blanks > 1) {
                padded = String.format("%-" + blanks + "s", str);
            }

            writer.write("       " + i + ": " + transitions.get(i) + padded + "0 0");
            writer.newLine();
        }

        writer.write("@");
        writer.newLine();

        writer.close();

    }

}
