/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tools;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;

/**
 *
 * @author Heiko Giese <h.giese@bioinformatik.uni-frankfurt.de>
 */
public class Files {

    public static File[] getAllFiles(File f, FileFilter filter) {

        if (f == null || !f.isDirectory()) {
            return new File[0];
        }

        ArrayList<File> toDoDirectory = new ArrayList<>();
        ArrayList<File> foundFiles = new ArrayList<>();

        FileFilter filterDirectory = (File file) -> file.isDirectory();

        toDoDirectory.add(f);
        while (!toDoDirectory.isEmpty()) {
            File curDir = toDoDirectory.remove(0);

            File[] subDirs = curDir.listFiles(filterDirectory);
            toDoDirectory.addAll(Arrays.asList(subDirs));

            File[] files = curDir.listFiles(filter);
            foundFiles.addAll(Arrays.asList(files));
        }

        return foundFiles.toArray(new File[foundFiles.size()]);
    }

    /**
     * Returns the files name without the files extension.
     * @param fileName The name or full path of the file to process.
     * @return The name of the file without the files extension.
     */
    public static String extractFileName(String fileName) {
        return extractFileName(new File(fileName));
    }
    
    /**
     * Returns the files name without the files extension.
     * @param f The file to process.
     * @return The name of the file without the files extension.
     */
    public static String extractFileName(File f) {
        String name = f.getName();
        int dotIndex = name.lastIndexOf(".");
        if(dotIndex > 0) {
            return name.substring(0, dotIndex);
        }
        return name;
    }
    
    /**
     * Returns the files path without the files extension.
     * @param f The file to process.
     * @return The path without the files extension.
     */
    public static String extractFileNameWithPath(File f) {
        if(f == null) f = new File("");
        String path = f.getAbsolutePath();
        int dotIndex = path.lastIndexOf(".");
        if(dotIndex > 0) {
            return path.substring(0, dotIndex);
        }
        return path;
    }
    
    public static File createTempFolder(Path path, String name) throws IOException {
        File tempFolder = java.nio.file.Files.createTempDirectory(path, name).toFile();
        if(tempFolder.isDirectory() && tempFolder.listFiles().length == 0) {
            return tempFolder;
        }        
        return null;
    }
    
    public static File createTempFolder(String name) throws IOException {
        File tempFolder = java.nio.file.Files.createTempDirectory(name).toFile();
        
        if(tempFolder.isDirectory() && tempFolder.listFiles().length == 0) {
            return tempFolder;
        }        
        return null;
    }
    
    
    public static File createTempFile(String name, String ext) throws IOException {            
        File tempDir = new File(System.getProperty("java.io.tmpdir"));
        return createTempFile(name, ext, tempDir);
    }
    
    public static File createTempFile(String name, String ext, File tempDir) throws IOException {            
        if(ext == null) {
            ext = "";
        } else if (!ext.isEmpty()) {
            ext = "." + ext;
        }
        return File.createTempFile(name, ext, tempDir);
    }
    
    /**
     * Deletes the given folder and all contained files (except folders). The folder will only be deleted if all its content could be deleted first.
     * @param folder The folder to delete.
     * @return true if the folder was deleted false otherwise.
     */
    public static boolean deleteFolder(File folder) {
        if(folder.isDirectory()) {
            File[] files = folder.listFiles();
            for (File file : files) {
                if(file.isDirectory()) { //won't delete directories
                    return false;
                } else {
                    if(!file.delete()) {
                        return false;
                    }
                }
            }
            return folder.delete();
        }
        return false;
    }

}
