/*
 *
 *  This file ist part of the software MonaLisa.
 *  MonaLisa is free software, dependend on non-free software. For more information read LICENCE and README.
 *
 *  (c) Department of Molecular Bioinformatics, Institue of Computer Science, Johann Wolfgang
 *  Goethe-University Frankfurt am Main, Germany
 *
 */

package tools;

import java.net.URL;


/**
 * The global resource manager. Requests to resources should all go through the
 * (singleton) instance of this class.
 * @author Konrad Rudolph
 */
public final class ResourceManager {
    private static final Class<ResourceManager> TYPE = ResourceManager.class;
    
    private static class InstanceHolder {
        public static ResourceManager INSTANCE = new ResourceManager();
    }
 
    /**
     * Returns the singleton instance of this class.
     * @return 
     */
    public static ResourceManager instance() { return InstanceHolder.INSTANCE; }
    
    private ResourceManager() { }
    
    /**
     * Creates a URL object for a given resource.
     * @param name The name of the resource file.
     * @return Returns a URL pointing to that resource.
     */
    public URL getResourceUrl(String name) {
        return TYPE.getResource(name);
    }
}
