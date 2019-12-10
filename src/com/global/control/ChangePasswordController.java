/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.global.control;

import com.database.koneksi;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXTextField;
import com.jfoenix.validation.RequiredFieldValidator;
import java.net.URL;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ResourceBundle;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyEvent;
import org.controlsfx.control.Notifications;

/**
 * FXML Controller class
 *
 * @author dzikry's
 */
public class ChangePasswordController implements Initializable {

    @FXML
    private JFXButton Save;
    @FXML
    private JFXTextField oldPassword;
    @FXML
    private JFXTextField newPassword;
    @FXML
    private JFXTextField retypeNewPassword;
    @FXML
    private JFXComboBox<String> cb_no_anggota;
    
    //Deklarasi Variable
    koneksi dbsetting;
    String driver,database,user,pass;
    Alert error = new Alert(Alert.AlertType.ERROR);
    Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION);
    final ObservableList options = FXCollections.observableArrayList();

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        dbsetting = new koneksi();
        driver = dbsetting.SettingPanel("DBDriver");
        database = dbsetting.SettingPanel("DBDatabase");
        user = dbsetting.SettingPanel("DBUsername");
        pass = dbsetting.SettingPanel("DBPassword");
        
        try
        {
            Class.forName(driver);
            Connection kon = DriverManager.getConnection(database,user,pass);
            Statement stt = kon.createStatement();
            String SQL  = "SELECT no_anggota "
                        + "FROM anggota "
                        + "WHERE aktif = '1';";
            ResultSet res = stt.executeQuery(SQL);
            while(res.next())
            {               
                options.add(res.getString(1));
            }
            cb_no_anggota.getItems().setAll(options);
            res.close();
            stt.close();
            kon.close();
        }
        catch(Exception e)
        {
            System.err.println(e.getMessage());
            error.setTitle("Error");
            error.setHeaderText("Error");
            error.setResizable(false);
            error.setContentText(e.getMessage());
            error.showAndWait();
        }
    }
    

    @FXML
    private void SavePassword(ActionEvent event) {
        String noAnggota = null;
        String password = null;
        try
        {
            Class.forName(driver);
            Connection kon = DriverManager.getConnection(database,user,pass);
            Statement stt = kon.createStatement();
            String SQL  = "SELECT no_anggota, password "
                        + "FROM login "
                        + "WHERE username = 'ADMIN';";
            ResultSet res = stt.executeQuery(SQL);
            while(res.next())
            {               
                noAnggota = res.getString(1);
                password = res.getString(2);
            }
            res.close();
            stt.close();
            kon.close();
        }
        catch(Exception e)
        {
            System.err.println(e.getMessage());
            error.setTitle("Error");
            error.setHeaderText("Error");
            error.setResizable(false);
            error.setContentText(e.getMessage());
            error.showAndWait();
        }
        if(oldPassword.getText().isEmpty()
                &&newPassword.getText().isEmpty()
                &&retypeNewPassword.getText().isEmpty())
        {
            Notifications.create()
                         .title("Information")
                         .text("Data tidak boleh kosong, silahkan dilengkapi.")
                         .position(Pos.BOTTOM_RIGHT)
                         .showInformation();
            oldPassword.requestFocus();
        }
        else if(!newPassword.getText().toUpperCase().equals(retypeNewPassword.getText().toUpperCase()))
        {
            Notifications.create()
                         .title("Information")
                         .text("Kata Sandi baru tidak sama dengan pengulangan kata sandi baru, silahkan diperbaiki.")
                         .position(Pos.BOTTOM_RIGHT)
                         .showInformation();
            newPassword.requestFocus();
        }
        else if(!oldPassword.getText().toUpperCase().equals(password))
        {
            Notifications.create()
                         .title("Information")
                         .text("Kata sandi lama tidak sesuai, silahkan diperbaiki.")
                         .position(Pos.BOTTOM_RIGHT)
                         .showInformation();
            oldPassword.requestFocus();
        }
        else
        {
            try
            {
                Class.forName(driver);
                Connection kon = DriverManager.getConnection(database,user,pass);
                Statement stt = kon.createStatement();
                String SQL  = "UPDATE login "
                            + "SET "
                            + "password = REPLACE(password,'"+oldPassword.getText().toUpperCase()+"','"+newPassword.getText().toUpperCase()+"'), "
                            + "no_anggota = REPLACE(no_anggota,'"+noAnggota+"','"+cb_no_anggota.getSelectionModel().getSelectedItem()+"') "
                            + "WHERE username = 'ADMIN';";
                stt.executeUpdate(SQL);
                stt.close();
                kon.close();
                Notifications.create()
                             .title("Information")
                             .text("Kata sandi telah berubah.")
                             .position(Pos.BOTTOM_RIGHT)
                             .showInformation();
            }
            catch(Exception e)
            {
                System.err.println(e.getMessage());
                error.setTitle("Error");
                error.setHeaderText("Error");
                error.setResizable(false);
                error.setContentText(e.getMessage());
                error.showAndWait();
            }
        }
    }   

    @FXML
    private void retypeValidator(KeyEvent event) {
        if(!newPassword.getText().equals(retypeNewPassword.getText()))
        {
            retypeNewPassword.setStyle("-fx-text-fill: red;");
        }
        else
        {
            retypeNewPassword.setStyle("-fx-text-fill: black;");
        }
    }
}
