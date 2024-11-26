/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package tools;

import tools.extendedswing.EFileFilter;
import tools.extendedswing.JEFileChooser;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.Window;
import java.io.File;
import java.net.URL;
import javax.swing.JFileChooser;

/**
 *
 * @author Heiko Giese <h.giese@bioinformatik.uni-frankfurt.de>
 */
public class General {

    public static void setWindowIcon(Window window, String iconImage) {
        if (iconImage == null || iconImage.isEmpty()) {
            return;
        }
        // Set Windowicon
        Toolkit tk = window.getToolkit();
        Image image = tk.getImage(iconImage);
        int i = 50;
        while (!tk.prepareImage(image, -1, -1, window)) {
            if(i < 0) {
                if(isiknock.IsiKnock.DEBUG) System.out.println("couldn't load window icon");
                return;
            }
            try {
                Thread.sleep(100);
            } catch (Exception e) {
                //TODO Handle error
                if(isiknock.IsiKnock.DEBUG) System.out.println("couldn't load window icon");
            }
            i--;
        }
        
        window.setIconImage(image);
    }
    
    public static void setWindowIcon(Window window, URL icon) {
        if (icon == null) {
            return;
        }
        // Set Windowicon
        Toolkit tk = window.getToolkit();
        Image image = tk.getImage(icon);
        int i = 50;
        while (!tk.prepareImage(image, -1, -1, window)) {
            if(i < 0) {
                if(isiknock.IsiKnock.DEBUG) System.out.println("couldn't load window icon");
                return;
            }
            try {
                Thread.sleep(100);
            } catch (Exception e) {
                //TODO Handle error
                if(isiknock.IsiKnock.DEBUG) System.out.println("couldn't load window icon");
            }
            i--;
        }
        window.setIconImage(image);
    }

    public static enum FileChooserTyp {

        OpenFile, OpenFolder, Save
    };

    /**
     * Opens a new FileDialog.
     *
     * @param start Directory/File which the dialog will show when opened.
     * @param typ Typ the typ of the diaolog.
     * @param filters File typs which are allowed to be selected. Format
     * {{extensionFilter1, descriptionFilter1}, ..., {extensionFilterN,
     * descriptionFilterN}} eg. {{"png", "*.png (Portable Network Graphics)"}})
     * @param allowFileFilterAllFiles If true it is possible
     * to set the file filter to accept all files.
     * @param parent The dialogs parent component
     * @return The selected File/Folder otherwise null.
     */
    public static File OpenFileDialog(String start, FileChooserTyp typ, String[][] filters, boolean allowFileFilterSupportedFormats, boolean allowFileFilterAllFiles, Component parent) {
        return OpenFileDialog(start, typ, filters, allowFileFilterAllFiles ? -1 : 0, allowFileFilterSupportedFormats, allowFileFilterAllFiles, parent);
    }

    /**
     * Opens a new FileDialog.
     *
     * @param start Directory/File which the dialog will show when opened.
     * @param typ Typ the typ of the diaolog.
     * @param filters File typs which are allowed to be selected. Format
     * {{extensionFilter1, descriptionFilter1}, ..., {extensionFilterN,
     * descriptionFilterN}} eg. {{"png", "*.png (Portable Network Graphics)"}})
     * @param selectedFilter Index of the Filter that will be selected by
     * default. To select the all files filter enter -1.
     * @param allowFileFilterAllFiles If true it is possible
     * to set the file filter to accept all files.
     * @param parent The dialogs parent component
     * @return The selected File/Folder otherwise null.
     */
    public static File OpenFileDialog(String start, FileChooserTyp typ, String[][] filters, int selectedFilter, boolean allowFileFilterSupportedFormats, boolean allowFileFilterAllFiles, Component parent) {

        EFileFilter[] eFilters = null;
        //check filter
        if (filters != null) {
            for (String[] filter : filters) {
                if (filter == null || filter.length != 2 || filter[0] == null || filter[0].isEmpty() || filter[1] == null) {
                    throw new IllegalArgumentException("filters doesn't contain filter of the correct format.");
                }
            }
            eFilters = new EFileFilter[filters.length];
            if (filters.length > 0) {
                for (int i = 0; i < filters.length; i++) {
                    final String extension = filters[i][0];
                    final String description = filters[i][1];
                    eFilters[i] = new EFileFilter(extension, description);
                }
            }
        }
        return OpenFileDialog(start, typ, eFilters, selectedFilter, allowFileFilterSupportedFormats, allowFileFilterAllFiles, parent);
    }

    public static File OpenFileDialog(String start, FileChooserTyp typ, EFileFilter[] filters, int selectedFilter, boolean allowFileFilterSupportedFormats, boolean allowFileFilterAllFiles, Component parent) {

        JEFileChooser fc;
        File f = null;
        if (start.equals("")) {
            fc = new JEFileChooser();
        } else {
            f = new File(start);

            if (!f.isDirectory()) {
                //check if the file dairectory exists
                if (f.getParentFile() != null && f.getParentFile().isDirectory()) {
                    //directory exists so we can set the file as start
                    fc = new JEFileChooser(start);
                    fc.setSelectedFile(f);
                } else {
                    //directory does not exists so we open the default directory as start
                    fc = new JEFileChooser();
                }
            } else {
                //the file is a directory so we can set it as the start directory
                fc = new JEFileChooser(start);
                fc.setCurrentDirectory(f);
            }
        }
        Dimension d = new Dimension(600, 400);
        fc.setSize(d);
        fc.setPreferredSize(d);

        if (filters != null && filters.length > 0) {
            fc.resetChoosableFileFilters();
            fc.setAcceptAllFileFilterUsed(allowFileFilterAllFiles);
//            if(allowFileFilterSupportedFormats && filters.length > 1) {
//                EFileFilter ffAllSuppoted = new EFileFilter(EFileFilter.getExtensions(filters), "all formats");
//                fc.addChoosableFileFilter(ffAllSuppoted);
//            }
            for (EFileFilter filter : filters) {
                fc.addChoosableFileFilter(filter);
            }
            fc.setFileFilter(selectedFilter + (allowFileFilterAllFiles ? 1 : 0));
        }

        int state = 0;
        switch (typ) {
            case Save:
                state = fc.showSaveDialog(parent);
                break;
            case OpenFolder:
                fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
            case OpenFile:
                state = fc.showOpenDialog(parent);

                break;
        }

        if (state == JFileChooser.APPROVE_OPTION) {
            File file = fc.getSelectedFile();
//            if (typ.equals(FileChooserTyp.Save)) {
//                EFileFilter filter = fc.getEFileFilter();
//                if (filter != null) {
//                    String extension = "." + filter.getExtension();
//                    if (!filter.checkExtension(file)) {
//                        file = new File(file.getPath() + extension);
//                    }
//                }
//            }
            return file;
        } else {
            return null;
        }
    }

    
}
