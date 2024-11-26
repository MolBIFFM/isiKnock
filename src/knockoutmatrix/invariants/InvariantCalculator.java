/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package knockoutmatrix.invariants;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.NoSuchFileException;
import java.util.EnumSet;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import knockoutmatrix.Inv_parser;
import tools.Files;
import tools.OS;

/**
 *
 * @author Modified by Heiko Giese <h.giese@bioinformatik.uni-frankfurt.de>
 * originally by Jens Einloft
 */
public class InvariantCalculator {

    public static enum Flag {

        TINV, MANA, MANABF, PINV,
    }
    
    private static final int MAX_NB_OF_RESULT_FILES_OUTPUT_TO_CONSOLE = 3;
                
    public static final File calcTInvariants(File pntFile) throws IOException {
        return calcInvariants(pntFile, EnumSet.of(Flag.TINV));
    }

    public static final File calcManateeInvariants(File pntFile, boolean fast) throws IOException {
        return calcInvariants(pntFile, EnumSet.of(fast ? Flag.MANA : Flag.MANABF));
    }

    public static final File calcPInvariants(File pntFile) throws IOException {
        return calcInvariants(pntFile, EnumSet.of(Flag.PINV));
    }

    public static List<List<String>> extractInvariants(File resultFile) throws IOException {

        if (resultFile != null && resultFile.exists()) { //just to make sure
            return Inv_parser.parse(resultFile.getAbsolutePath());
        }

        return null;
    }

    public static void saveInvariants(File saveTo, List<String> invariantSetLabels, List<List<List<String>>> invariantSets) throws IOException {
        if (invariantSets == null || invariantSetLabels == null || invariantSetLabels.size() != invariantSets.size()) {
            if (isiknock.IsiKnock.DEBUG) {
                System.out.println("Saving invariants failed");
            }
            return;
        }

        try (FileWriter writer = new FileWriter(saveTo)) {
            int count = 1;
            for (int i = 0; i < invariantSetLabels.size(); i++) {
                String label = invariantSetLabels.get(i);
                List<List<String>> invariants = invariantSets.get(i);

                if (i > 0) {
                    writer.write(OS.getLineSeperator());
                }
                writer.write(label + OS.getLineSeperator());

                for (List<String> transitions : invariants) {
                    String line = count++ + ".Invariant:";
                    for (String transition : transitions) {
                        line += " " + transition;
                    }
                    writer.write(line + OS.getLineSeperator());
                }
            }
        }
    }

    private static File calcInvariants(File pntFile, EnumSet<Flag> flags) throws IOException {
        startCalculation(pntFile, flags);
        File resFile = evaluateResult(pntFile, flags)[0];
        return resFile;
    }

    public static Process startCalculation(File pntFile, EnumSet<Flag> flags) throws IOException {
        if (pntFile == null || !pntFile.exists() || !pntFile.canRead()) {
            throw new NoSuchFileException(pntFile != null ? pntFile.getAbsolutePath() : "null");
        }

        File tempDir = pntFile.getParentFile();
        if (tempDir == null || !tempDir.isDirectory()) { //just in case
            tempDir = new File(System.getProperty("java.io.tmpdir"));
        }

        File toolFile = null;
        try {

            //get tool for calculation
            String toolFileName;
            String prefix = "invariant_calc_";
            String suffix = "_bin";

            final String osName = System.getProperty("os.name").toLowerCase();
            if (osName.startsWith("windows")) {
                toolFileName = "inv_win.exe";
            } else if (osName.startsWith("mac")) {
                toolFileName = "inv_mac";
            } else if (osName.contains("nix") || osName.contains("nux")) {
                toolFileName = "inv_unix";
            } else {
                System.out.println("Unsupported OS: Using unix/linux version of the invariant calculation software!");
                toolFileName = "inv_unix";
            }

            toolFile = FileUtils.extractResource("/resources/algorithms/invariants/" + toolFileName, prefix, suffix, tempDir);
//            if (isiknock.IsiKnock.DEBUG) {
//                System.out.println("toolfilepath " + toolFile.getAbsoluteFile());
//            }
            toolFile.deleteOnExit();
            toolFile.setExecutable(true);

            //parameters
            String param = "";
            for (Flag flag : flags) {
                String para = "";
                switch (flag) {
                    case MANA:
                        para = "MI";
                        break;
                    case MANABF:
                        para = "MIBF";
                        break;
                    case PINV:
                        para = "PI";
                        break;
                    case TINV:
                        para = "TI";
                        break;
                }
                param += param.isEmpty() ? para : "/" + para;
            }

            //calculate
            ProcessBuilder pb = new ProcessBuilder(toolFile.getAbsolutePath(), pntFile.getAbsolutePath(), param);
            pb.directory(tempDir);
            if (isiknock.IsiKnock.DEBUG) {
                System.out.println("pnt file: " + pntFile.getAbsolutePath());
            }
            if (isiknock.IsiKnock.DEBUG) {
                System.out.println("tool file: " + toolFile.getAbsolutePath());
                System.out.println("    parameter: " + param);
            }
            //workaround for error "text file busy" which occurs for some unknown reason when executing parralel calculations
            //we wait shortly and retry the calculation if the file is busy
            int retries = 3;
            while (retries > 0) {
                try {
                    Process p = pb.start();
                    //read output
                    BufferedReader br = new BufferedReader(new InputStreamReader(p.getInputStream()));
                    String line;
                    //write output to console
                    while ((line = br.readLine()) != null) {
                        if (!line.trim().isEmpty()) {
                            System.out.println(line);
                        }
                    }
                    p.waitFor();
                    return p;
                } catch (IOException ex) {
                    if (isiknock.IsiKnock.DEBUG) {
                        System.out.println("retry: " + pntFile.getName());
                    }
                    Thread.sleep(10); //lets give it some time before we try again
                }
                retries--;
            }
        } catch (IOException | InterruptedException ex) {
            Logger.getLogger(InvariantCalculator.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            if (!isiknock.IsiKnock.DEBUG && toolFile != null) {
                toolFile.delete();
            }
        }

        return null;
    }

    public static File[] evaluateResult(File pntFile, EnumSet<Flag> flags) {

        File[] resFiles;
        if (pntFile == null || flags == null) {
            resFiles = null;
        } else {
            resFiles = new File[flags.size()];
            String baseFileName = Files.extractFileNameWithPath(pntFile);
            int i = 0;
            for (Flag flag : flags) {
                String ext = "";
                switch (flag) {
                    case MANA:
                    case MANABF:
                        ext = "man";
                        break;
                    case PINV:
                        ext = "pi";
                        break;
                    case TINV:
                        ext = "inv";
                        break;
                }
                String resultFilePath = baseFileName + "." + ext;
                File resFile = new File(resultFilePath);
                if (resFile.exists()) { //just to make sure
                    resFile.deleteOnExit();
                }

                resFiles[i++] = resFile;
            }

            if (isiknock.IsiKnock.DEBUG) {
                int count = 0;
                for (File resFile : resFiles) {
                    if(++count > MAX_NB_OF_RESULT_FILES_OUTPUT_TO_CONSOLE ) {
                        System.out.println("... (" + (resFiles.length - MAX_NB_OF_RESULT_FILES_OUTPUT_TO_CONSOLE) + " additional result file(s))");
                        break;
                    };
                    System.out.println("result file: " + (resFile != null ? (resFile.getAbsolutePath() + (resFile.exists() ? "" : " -> file does not exist anymore")) : "no results file created"));
                }
            }
        }

        return resFiles;
    }

}
