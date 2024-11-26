/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tools;

import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.Window;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

/**
 *
 * @author Heiko Giese <h.giese@bioinformatik.uni-frankfurt.de>
 */
public class OS {
    public static boolean openFile(File f) {
        if (Desktop.isDesktopSupported()) {
            Desktop desktop = Desktop.getDesktop();
            if (desktop.isSupported(Desktop.Action.OPEN)) {
                try {
                    desktop.open(f);
                    return true;
                } catch (IOException ex) {

                }
            }
        }
        return false;
    }
    
    /**
     * Opens the given url in the systems standard browser
     *
     * @param f
     */
    public static boolean openURLinBrowser(File f) {
        return openURLinBrowser("file:///" + f.getAbsolutePath().replace("\\", "/"));
    }

    /**
     * Opens the given url in the systems standard browser
     *
     * @param url
     */
    public static boolean openURLinBrowser(String url) {
        Boolean worked = false;
        if (Desktop.isDesktopSupported()) {
            Desktop desktop = Desktop.getDesktop();
            if (desktop.isSupported(Desktop.Action.BROWSE)) {
                //open url in standard browser
                URI uri;
                try {
//                    String encoded = java.net.URLEncoder.encode(url, "ISO-8859-1");
                    uri = new URI(url);
                    desktop.browse(uri);
                    worked = true;
                } catch (URISyntaxException ex) {
                    //TODO Handle Error
                    System.out.println(ex.getMessage());
                } catch (IOException ex) {
                    //TODO Handle Error
                    System.out.println(ex.getMessage());
                }
            }
        } else {
            String os = System.getProperty("os.name").toLowerCase();
            Runtime rt = Runtime.getRuntime();
            try {
                if (os.contains("win")) {

                    // this doesn't support showing urls in the form of "page.html#nameLink" 
                    rt.exec("rundll32 url.dll,FileProtocolHandler " + url);
                    worked = true;
                } else if (os.contains("mac")) {

//                    rt.exec(new String[]{"osascript", "-e", "open location \"" + url + "\""});
                    rt.exec("open " + url);
                    worked = true;
                } else if (os.contains("nix") || os.contains("nux")) {

                    // Do a best guess on unix until we get a platform independent way
                    // Build a list of browsers to try, in this order.
                    String[] browsers = {"epiphany", "firefox", "mozilla", "konqueror",
                        "netscape", "opera", "links", "lynx"};

                    // Build a command string which looks like "browser1 "url" || browser2 "url" ||..."
                    StringBuilder cmd = new StringBuilder();
                    for (int i = 0; i < browsers.length; i++) {
                        cmd.append(i == 0 ? "" : " || ").append(browsers[i]).append(" \"").append(url).append("\" ");
                    }

                    rt.exec(new String[]{"sh", "-c", cmd.toString()});
                    worked = true;
                }
            } catch (Exception e) {

            }
        }
        return worked;
    }
    
    public static GraphicsDevice getWindowsGraphicsDevice(Window w) {
        GraphicsConfiguration config = w.getGraphicsConfiguration();
        return config.getDevice();
    }
    
    public static Dimension getWindowsGraphicsDeviceResoluion(Window w) {
        GraphicsDevice windowScreen = getWindowsGraphicsDevice(w);
        return new Dimension(windowScreen.getDisplayMode().getWidth(), windowScreen.getDisplayMode().getHeight());
    }

    public static int getWindowMonitorID(Window w) {
        GraphicsDevice windowScreen = getWindowsGraphicsDevice(w);
        GraphicsEnvironment env = GraphicsEnvironment.getLocalGraphicsEnvironment();
        GraphicsDevice[] allScreens = env.getScreenDevices();
        int screenIndex = -1;
        for (int i = 0; i < allScreens.length; i++) {
            if (allScreens[i].equals(windowScreen)) {
                screenIndex = i;
                break;
            }
        }
        return screenIndex;
    }
    
    public static String getFileSeperator() {
        return System.getProperty("file.separator");
    }

    public static String getLineSeperator() {
        return System.getProperty("line.separator");
    }
}
