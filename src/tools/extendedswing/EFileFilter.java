/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tools.extendedswing;

import java.io.File;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import javax.swing.filechooser.FileFilter;

/**
 *
 * @author Heiko Giese <h.giese@bioinformatik.uni-frankfurt.de>
 */
public class EFileFilter extends FileFilter {

    public static final EFileFilter FILTER_TXT = new EFileFilter(new String[]{"txt"}, "*.txt (Text Files)");
    public static final EFileFilter FILTER_CSV = new EFileFilter(new String[]{"csv"}, "*.csv (Comma-Separated Values)");
    public static final EFileFilter FILTER_JPEG = new EFileFilter(new String[]{"jpg", "jpeg"}, "*.jpeg");
    public static final EFileFilter FILTER_PNG = new EFileFilter(new String[]{"png"}, "*.png (Portable Network Graphics)");
    public static final EFileFilter FILTER_SVG = new EFileFilter(new String[]{"svg"}, "*.svg (Scalable Vector Graphics)");
    public static final EFileFilter FILTER_PNT = new EFileFilter(new String[]{"pnt"}, "*.pnt (Petri Net File)");
    public static final EFileFilter FILTER_SBML = new EFileFilter(new String[]{"xml"}, "*.xml (SBML File)");
    public static final EFileFilter FILTER_MANATEE = new EFileFilter(new String[]{"man"}, "*.man (Manatee Invariants File)");
    public static final EFileFilter FILTER_TINVARIANT = new EFileFilter(new String[]{"inf"}, "*.inf (Transition Invariants File)");

    String extension, description;
    String[] extensions;

    /**
     * Creates a new EFileFilter
     *
     * @param extension Lowercase file extension without dot (e.g csv, jpg,...)
     * @param description Description (e.g. CSV (comma-separated values))
     */
    public EFileFilter(String extension, String description) {
        this(extension.split(","), description);
    }

    /**
     * Creates a new EFileFilter
     *
     * @param extensions Lowercase file extensions without dot (e.g csv,
     * jpg,...)
     * @param description Description (e.g. CSV (comma-separated values))
     */
    public EFileFilter(String[] extensions, String description) {
        this(extensions, 0, description);
    }

    /**
     *  * Creates a new EFileFilter
     *
     * @param extensions Lowercase file extensions without dot (e.g csv,
     * jpg,...)
     * @param description Description (e.g. CSV (comma-separated values))
     * @param defaultExtensionID Location of the default extension in the the
     * extensions array.
     */
    public EFileFilter(String[] extensions, int defaultExtensionID, String description) {
        if ((extensions == null || extensions.length == 0 || extensions.length < defaultExtensionID) || description == null) {
            throw new IllegalArgumentException("Neither extensions nor description of the EFileFilter can be null!");
        } else if (extensions.length == 0 || extensions.length < defaultExtensionID) {
            throw new IllegalArgumentException("There are no extensions given!");
        } else if (extensions.length < defaultExtensionID) {
            throw new IllegalArgumentException("The defaultExtensionID is higher than the actual extension count!");
        }
        for (int i = 0; i < extensions.length; i++) {
            extensions[i] = extensions[i].trim().toLowerCase().replace(".", "");
        }

        this.extensions = extensions;
        this.extension = extensions[defaultExtensionID];
        this.description = description;
    }

    @Override
    public boolean accept(File f) {
        if (f == null) {
            return false;
        }
        if (f.isDirectory()) {
            return true;
        }
        return checkExtension(f);
    }

    /**
     * Checks if the given files extension matches one of the filters
     * extensions.
     *
     * @param f File to check
     * @return true if the files extension matches one of the filters
     * extensions, false otherwise.
     */
    public boolean checkExtension(File f) {
        if (f != null && f.getName() != null) {
            boolean matches = false;
            for (String ext : extensions) {
                matches = f.getName().toLowerCase().endsWith("." + ext);
                if (matches) {
                    break;
                }
            }
            return matches;
        }
        return false;
    }

    @Override
    public String getDescription() {
        return this.description;
    }

    public String[] getExtensions() {
        return this.extensions;
    }

    public String getExtension() {
        return this.extension;
    }

    /**
     * Compares obj to this EFileFilter
     *
     * @param obj
     * @return Returs true if obj is an instance of EFileFilter and obj has the
     * same extension as this EFileFilter. obj doesn't have to be the same
     * instance for the argument to be true.
     */
    @Override
    public boolean equals(Object obj) {
        if (obj != null && obj instanceof EFileFilter) {
            EFileFilter f = (EFileFilter) obj;
            if (extensions.length == f.getExtensions().length) {
                a:
                for (String ext : extensions) {
                    for (String fExt : f.getExtensions()) {
                        if (ext == null ? fExt == null : ext.equals(fExt)) {
                            continue a;
                        }
                    }
                    return false;
                }
                return true;
            }
        }
        return false;
    }

    @Override
    public String toString() {
        return "EFileFilter{" + "extension=" + extension + ", description=" + description + '}';
    }

    public static String getExtensions(EFileFilter[] filters) {
        String ext = "";
        final String del = ", ";
        for (EFileFilter f : filters) {
            for (String e : f.getExtensions()) {
                ext += e + del;
            }
        }
        ext = ext.substring(0, ext.length() - del.length());
        return ext;
    }

    public static EFileFilter[] addAllSupportedFormatsFileFilter(EFileFilter[] supportedFormats) {
        EFileFilter[] newFilters = new EFileFilter[supportedFormats.length + 1];
        System.arraycopy(supportedFormats, 0, newFilters, 1, supportedFormats.length);

        Set<String> extensions = new HashSet<>();
        for (EFileFilter supportedFormat : supportedFormats) {
            extensions.addAll(Arrays.asList(supportedFormat.getExtensions()));
        }
        EFileFilter allSupported = new EFileFilter(extensions.toArray(new String[extensions.size()]), "All Supported Formats");
        newFilters[0] = allSupported;

        return newFilters;
    }
}
