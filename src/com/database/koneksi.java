/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package database;

import java.io.FileInputStream;
import java.util.Properties;
import javafx.scene.control.Alert;

/**
 *
 * @author dzikry's
 */
public class koneksi {
    public Properties mypanel, myLanguage;
    private String strNamaPanel;
    public koneksi() 
    {
    
    }
    public String SettingPanel(String nmPanel)
    {
        try
        {
            mypanel = new Properties();
            mypanel.load(new FileInputStream("lib/database.ini"));
            strNamaPanel = mypanel.getProperty(nmPanel);
        }
        catch(Exception e)
        {
            Alert a = new Alert(Alert.AlertType.ERROR);
            a.setTitle("Error");
            a.setHeaderText("Error");
            a.setResizable(false);
            a.setContentText(e.getMessage());
            a.showAndWait();
            System.err.println(e.getMessage());
            System.exit(0);
        }
        return strNamaPanel;
    }
}
