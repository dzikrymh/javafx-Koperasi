/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.global.control;

import com.database.koneksi;
import com.jfoenix.controls.JFXPasswordField;
import com.jfoenix.controls.JFXTextField;
import java.net.URL;
import java.sql.*;
import java.util.ResourceBundle;
import javafx.animation.FadeTransition;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;
import org.controlsfx.control.Notifications;

/**
 * FXML Controller class
 *
 * @author dzikry's
 */
public class LoginController implements Initializable {

    //Deklarasi Variable
    koneksi dbsetting;
    String driver,database,user,pass;
    
    @FXML
    private JFXTextField username;
    @FXML
    private JFXPasswordField password;
    @FXML
    private AnchorPane LoginPane;
    @FXML
    private AnchorPane rootLogin;
    
    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        password.setOnKeyPressed(new EventHandler<KeyEvent>(){
            @Override
            public void handle(KeyEvent ke)
            {
                if(ke.getCode().equals(KeyCode.ENTER))
                {
                    signin(username.getText().toUpperCase(),
                           password.getText().toUpperCase());
                }
            }
        });
        username.setOnKeyPressed(new EventHandler<KeyEvent>(){
            @Override
            public void handle(KeyEvent event) {
                if(event.getCode().equals(KeyCode.ENTER))
                {
                    signin(username.getText().toUpperCase(),
                           password.getText().toUpperCase());
                }
            }
        });
    }
    
    private void signin(String User, String Pass)
    {
        dbsetting = new koneksi();
        driver = dbsetting.SettingPanel("DBDriver");
        database = dbsetting.SettingPanel("DBDatabase");
        user = dbsetting.SettingPanel("DBUsername");
        pass = dbsetting.SettingPanel("DBPassword");
        
        try
        {
            Class.forName(driver);
            Connection kon = DriverManager.getConnection(database, user, pass);
            Statement stt = kon.createStatement();
            String SQL  = "SELECT * "
                        + "FROM login "
                        + "WHERE username = '"+User+"' "
                        + "AND password = '"+Pass+"';";
            ResultSet res = stt.executeQuery(SQL);
            String u = null;
            String p = null;
            while(res.next())
            {
                u = res.getString(1);
                p = res.getString(2);
            }
            
            if(u!=null&&p!=null)
            {
                //TODO : Masuk Menu Utama Aplikasi
                Parent root = FXMLLoader.load(getClass().getResource("/com/global/view/Home.fxml"));
                Stage stage = new Stage(StageStyle.DECORATED);
                stage.setTitle("Koperasi Rukun Tetangga Sindangsari 1");
                stage.getIcons().add(new Image("/com/images/agreement.png"));
                FadeTransition ft = new FadeTransition(Duration.millis(3000), root);
                ft.setFromValue(0.0);
                ft.setToValue(1.0);
                ft.play();
                stage.setScene(new Scene(root));
                stage.setResizable(false);
                stage.initModality(Modality.APPLICATION_MODAL);
                stage.show();
                
                Stage s = (Stage) LoginPane.getScene().getWindow();
                s.close();
            }
            else
            {
                Notifications
                        .create()
                        .title("Information")
                        .text("Username atau Password anda salah, silahkan ulangi.")
                        .position(Pos.BOTTOM_RIGHT)
                        .showInformation();
                username.requestFocus();
            }
            
            res.close();
            stt.close();
            kon.close();
        }
        catch(Exception e)
        {
            System.err.println(e.getMessage());
            Alert a = new Alert(Alert.AlertType.ERROR);
            a.setTitle("Message");
            a.setHeaderText("Error");
            a.setResizable(false);
            a.setContentText(e.getMessage());
            a.showAndWait();
        }
    }

    @FXML
    private void CloseOnAction(ActionEvent event) 
    {
        System.exit(0);
    }

    @FXML
    private void signin_onAction(ActionEvent event) 
    {
        if(username.getText().toUpperCase().isEmpty()&&password.getText().toUpperCase().isEmpty())
        {
            Notifications
                    .create()
                    .title("Information")
                    .text("Username atau Password tidak boleh kosong.")
                    .position(Pos.BOTTOM_RIGHT)
                    .showInformation();
            username.requestFocus();
        }
        else if(!username.getText().toUpperCase().equals("ADMIN"))
        {
            Notifications
                    .create()
                    .title("Information")
                    .text("Username atau Password anda salah, silahkan ulangi.")
                    .position(Pos.BOTTOM_RIGHT)
                    .showInformation();
            username.requestFocus();
        }
        else
        {
            signin(username.getText().toUpperCase(),password.getText().toUpperCase());
        }
    }
    
}
