/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
/*
 *
 *  This file ist part of the software MonaLisa.
 *  MonaLisa is free software, dependend on non-free software. For more information read LICENCE and README.
 *
 *  (c) Molekulare Bioinformatik, Goethe University Frankfurt, Frankfurt am Main, Germany
 *
 */

package knockoutmatrix.invariants;

import tools.ResourceManager;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

/**
 *
 * @author Jens Einloft and Heiko Giese
 */
public class FileUtils {

    /**
     * Extracts a resource into a temporary file.
     * 
     * @param resource
     *            The path to the resource, relative to the
     *            {@code org/monalisa/resources} directory.
     * @param prefix
     *            A prefix to use for the temporary file.
     * @param suffix
     *            A suffix to use for the temporary file.
     * @param dir 
     *            The directory to which the resource will be extracted
     * @return Returns a file instance for the temporary file.
     * @throws IOException
     */
    public static File extractResource(String resource, String prefix, String suffix, File dir) throws IOException {
        URL resURL = ResourceManager.instance().getResourceUrl(resource);
        if (resURL == null) {
            throw new FileNotFoundException();
        }
        return extractResource(resURL, prefix, suffix, dir);
    }

    /**
     * Extracts a resource into a temporary file.
     * 
     * @param resource
     *            A URL to the resource to extract.
     * @param prefix
     *            A prefix to use for the temporary file.
     * @param suffix
     *            A suffix to use for the temporary
     * @param dir 
     *            The directory to which the resource will be extracted.
     * @return Returns a file instance for the temporary file.
     * @throws IOException
     */
    public static File extractResource(URL resource, String prefix, String suffix, File dir) throws IOException {
        File file = File.createTempFile(prefix, suffix, dir);
        InputStream resStream = null;
        FileOutputStream tmpFileStream = null;
        try {
            resStream = resource.openStream();
            tmpFileStream = new FileOutputStream(file);

            byte buffer[] = new byte[1024];
            int i = 0;
            while ((i = resStream.read(buffer)) != -1)
                tmpFileStream.write(buffer, 0, i);
        } finally {
            if (resStream != null)
                resStream.close();
            if (tmpFileStream != null)
                tmpFileStream.close();
        }

        return file;
    }    
    
}
