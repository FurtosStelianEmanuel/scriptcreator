/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package system;

import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.InputEvent;
import java.net.URISyntaxException;
import java.util.StringTokenizer;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Manel
 */
public class SystemManager {

    public static String getPathToJar() {
        try {
            String x = SystemManager.class.getProtectionDomain().getCodeSource().getLocation().toURI().getPath();
            StringBuilder b = new StringBuilder(x);
            b.delete(0, 1);
            return b.toString();
        } catch (URISyntaxException ex) {
            Logger.getLogger(SystemManager.class.getName()).log(Level.SEVERE, null, ex);
            return "Error";
        }
    }

    public static String get_path(String finalName) {
        String full = getPathToJar();
        StringTokenizer b = new StringTokenizer(full, "/\\");
        String x = "";
        while (b.hasMoreElements()) {
            String y = b.nextToken();
            if (!(y.toLowerCase().equals(finalName.toLowerCase())) && !(y.toLowerCase().equals("store"))) {
                x += y + "\\";
            }
        }
        return x;
    }

    public static Dimension getScreenSize() {
        return Toolkit.getDefaultToolkit().getScreenSize();
    }
    public static String PATH = SystemManager.get_path("oradentscript.jar");
    
    public static final int LEFT_CLICK=InputEvent.BUTTON1_DOWN_MASK;
    public static final int MIDDLE_CLICK=InputEvent.BUTTON2_DOWN_MASK;
    public static final int RIGHT_CLICK=InputEvent.BUTTON3_DOWN_MASK;
    
    public static final int SCROLL_UP=-1;
    public static final int SCROLL_DOWN=1;
    
}
