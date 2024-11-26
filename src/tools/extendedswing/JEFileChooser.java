/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package tools.extendedswing;

import java.io.File;
import java.util.ArrayList;
import javax.swing.JFileChooser;
import static javax.swing.JFileChooser.SAVE_DIALOG;
import javax.swing.JOptionPane;
import javax.swing.filechooser.FileFilter;

/**
 *
 * @author Heiko Giese <h.giese@bioinformatik.uni-frankfurt.de>
 */
public class JEFileChooser extends JFileChooser {

    private ArrayList<EFileFilter> filter;
    private EFileFilter filterAllFiles = new EFileFilter("*", "all files") {
        @Override
        public boolean accept(File f) {
            return true;
        }
    };

    /**
     * Constructs a
     * <code>EFileChooser</code> pointing to the user's default directory. This
     * default depends on the operating system. It is typically the "My
     * Documents" folder on Windows, and the user's home directory on Unix.
     */
    public JEFileChooser() {
        super();
        this.init();
    }

    /**
     * Constructs a
     * <code>EFileChooser</code> using the given path. Passing in a
     * <code>null</code> string causes the file chooser to point to the user's
     * default directory. This default depends on the operating system. It is
     * typically the "My Documents" folder on Windows, and the user's home
     * directory on Unix.
     *
     * @param currentDirectoryPath a <code>String</code> giving the path to a
     * file or directory
     */
    public JEFileChooser(String currentDirectoryPath) {
        super(currentDirectoryPath);
        this.init();
    }

    /**
     * Constructs a
     * <code>EFileChooser</code> using the given
     * <code>File</code> as the path. Passing in a
     * <code>null</code> file causes the file chooser to point to the user's
     * default directory. This default depends on the operating system. It is
     * typically the "My Documents" folder on Windows, and the user's home
     * directory on Unix.
     *
     * @param currentDirectory a <code>File</code> object specifying the path to
     * a file or directory
     */
    public JEFileChooser(File currentDirectory) {
        super(currentDirectory);
        this.init();
    }

    private void init() {
        this.filter = new ArrayList<EFileFilter>(5);
        super.setAcceptAllFileFilterUsed(false);
        this.resetChoosableFileFilters();
        this.addChoosableFileFilter(filterAllFiles);
    }

    @Override
    public void approveSelection() {
        File f = getSelectedFile();
        if (f != null && f.exists() && getDialogType() == SAVE_DIALOG) {
            int result = JOptionPane.showConfirmDialog(this, "The file exists, overwrite?", "Existing file", JOptionPane.YES_NO_OPTION);
            switch (result) {
                case JOptionPane.YES_OPTION:
                    super.approveSelection();
                    return;
                case JOptionPane.NO_OPTION:
                case JOptionPane.CLOSED_OPTION:
                case JOptionPane.CANCEL_OPTION:
                    return;
            }
        }
        super.approveSelection();
    }

    @Override
    public File getSelectedFile() {
        File file = super.getSelectedFile();
        if (getDialogType() == SAVE_DIALOG) {
            if(file == null) {
                return null;
            }
            EFileFilter ff = getEFileFilter();
            if (ff != null && ff.getExtension() != null && !ff.getExtension().equals("*")) {
                String extension = "." + ff.getExtension();
                if (!ff.checkExtension(file)) {
                    file = new File(file.getPath() + extension);
                }
            }
        }
        return file;
    }

    @Override
    public void setAcceptAllFileFilterUsed(boolean b) {
        if (b && !this.filter.contains(filterAllFiles)) {
            this.filter.add(0, filterAllFiles);
            this.updateFileFilters();
        }
    }

    public void addChoosableFileFilter(EFileFilter filter) {
        if (!this.filter.contains(filter)) {
            this.filter.add(filter);
            this.updateFileFilters();
        }
    }

    @Deprecated
    @Override
    public void addChoosableFileFilter(FileFilter filter) {
        super.addChoosableFileFilter(filter);
    }

    public void setFileFilter(int index) {
        if (this.filter.size() > index) {
            this.setFileFilter(this.filter.get(index));
        }
    }

    public void setFileFilter(EFileFilter filter) {
        if (filter == null) {
            return;
        }
        if (!this.filter.contains(filter)) {
            this.addChoosableFileFilter(filter);
        }
        super.setFileFilter(filter);
    }

    @Deprecated
    @Override
    public FileFilter[] getChoosableFileFilters() {
        return super.getChoosableFileFilters();
    }

    public EFileFilter[] getChoosableEFileFilters() {
        return this.filter.toArray(new EFileFilter[this.filter.size()]);
    }

    private void updateFileFilters() {
        if (this.filter.isEmpty()) {
            return;
        }
        super.resetChoosableFileFilters();
        for (EFileFilter f : this.filter) {
            super.addChoosableFileFilter(f);
        }
        super.setFileFilter(this.filter.get(0));
    }

    @Deprecated
    @Override
    public void setFileFilter(FileFilter filter) {
        super.setFileFilter(filter);
    }

    public EFileFilter getEFileFilter() {
        FileFilter f = super.getFileFilter();
        return (f instanceof EFileFilter) ? (EFileFilter) f : null;
    }

    @Deprecated
    @Override
    public FileFilter getFileFilter() {
        return super.getFileFilter();
    }

    @Override
    public void resetChoosableFileFilters() {
        super.resetChoosableFileFilters();
        this.filter.clear();
    }

//    public static class EFileFilter extends FileFilter {
//
//        String extension, description;
//        String[] extensions;
//
//        /**
//         * Creates a new EFileFilter
//         *
//         * @param extension Lowercase file extension without dot (e.g csv,
//         * jpg,...)
//         * @param description Description (e.g. CSV (comma-separated values))
//         */
//        public EFileFilter(String extension, String description) {
//            this(extension.split(","), description);
//        }
//
//        /**
//         * Creates a new EFileFilter
//         *
//         * @param extensions Lowercase file extensions without dot (e.g csv,
//         * jpg,...)
//         * @param description Description (e.g. CSV (comma-separated values))
//         */
//        public EFileFilter(String[] extensions, String description) {
//            this(extensions, 0, description);
//        }
//
//        /**
//         *  * Creates a new EFileFilter
//         *
//         * @param extensions Lowercase file extensions without dot (e.g csv,
//         * jpg,...)
//         * @param description Description (e.g. CSV (comma-separated values))
//         * @param defaultExtensionID Location of the default extension in the
//         * the extensions array.
//         */
//        public EFileFilter(String[] extensions, int defaultExtensionID, String description) {
//            if ((extensions == null || extensions.length == 0 || extensions.length < defaultExtensionID) || description == null) {
//                throw new IllegalArgumentException("Neither extensions nor description of the EFileFilter can be null!");
//            } else if (extensions.length == 0 || extensions.length < defaultExtensionID) {
//                throw new IllegalArgumentException("There are no extensions given!");
//            } else if (extensions.length < defaultExtensionID) {
//                throw new IllegalArgumentException("The defaultExtensionID is higher than the actual extension count!");
//            }
//            for (int i = 0; i < extensions.length; i++) {
//                extensions[i] = extensions[i].trim().toLowerCase().replace(".", "");
//            }
//
//            this.extensions = extensions;
//            this.extension = extensions[defaultExtensionID];
//            this.description = description;
//        }
//
//        @Override
//        public boolean accept(File f) {
//            if(f == null) {
//                return false;
//            }
//            if (f.isDirectory()) {
//                return true;
//            }
//            return checkExtension(f);
//        }
//
//        /**
//         * Checks if the given files extension matches one of the filters
//         * extensions.
//         *
//         * @param f File to check
//         * @return true if the files extension matches one of the filters
//         * extensions, false otherwise.
//         */
//        public boolean checkExtension(File f) {
//            if (f != null && f.getName() != null) {
//                boolean matches = false;
//                for (String ext : extensions) {
//                    matches = f.getName().toLowerCase().endsWith("." + ext);
//                    if (matches) {
//                        break;
//                    }
//                }
//                return matches;
//            }
//            return false;
//        }
//
//        @Override
//        public String getDescription() {
//            return this.description;
//        }
//
//        public String[] getExtensions() {
//            return this.extensions;
//        }
//
//        public String getExtension() {
//            return this.extension;
//        }
//
//        /**
//         * Compares obj to this <class>EFileFilter</class>
//         *
//         * @param obj
//         * @return Returs true if obj is an instance of
//         * <class>EFileFilter</class> and obj has the same extension as this
//         * <class>EFileFilter</class>. obj doesn't have to be the same instance
//         * for the argument to be true.
//         */
//        @Override
//        public boolean equals(Object obj) {
//            if (obj != null && obj instanceof EFileFilter) {
//                EFileFilter f = (EFileFilter) obj;
//                return this.getExtension().equals(f.getExtension());
//            }
//            return false;
//        }
//
//        @Override
//        public String toString() {
//            return "EFileFilter{" + "extension=" + extension + ", description=" + description + '}';
//        }
//    }
}
