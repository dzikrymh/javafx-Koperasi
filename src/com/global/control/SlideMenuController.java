/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.global.control;

import com.jfoenix.controls.JFXButton;
import java.io.IOException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.animation.Animation;
import javafx.animation.FadeTransition;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;

/**
 * FXML Controller class
 *
 * @author dzikry's
 */
public class SlideMenuController implements Initializable {

    @FXML
    private JFXButton KelolaDataAnggota;
    @FXML
    private JFXButton KelolaDataSimpanan;
    @FXML
    private JFXButton KelolaDataPinjaman;
    @FXML
    private JFXButton KelolaDataAngsuran;
    @FXML
    private JFXButton KelolaLaporan;
    @FXML
    private JFXButton SignOut;
    @FXML
    private AnchorPane SlideMenu;
    @FXML
    private Label time;
    @FXML
    private Label date;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        getTanggal();
    }
    
    private void getTanggal() {
        Timeline timeline = new Timeline(
          new KeyFrame(Duration.seconds(0),
            new EventHandler<ActionEvent>() {
              @Override public void handle(ActionEvent actionEvent) {
                Calendar ti = Calendar.getInstance();
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm:ss");
                time.setText(simpleDateFormat.format(ti.getTime()));
                Date skrg = new Date();
                SimpleDateFormat tgl = new SimpleDateFormat("dd-MMM-yyyy");
                date.setText(tgl.format(skrg));
              }
            }
          ),
          new KeyFrame(Duration.seconds(1))
        );
        timeline.setCycleCount(Animation.INDEFINITE);
        timeline.play();
        
    }
    
    public void screen(String url) throws IOException {
        AnchorPane target = HomeController.TargetPaneP;
        FadeTransition ft = new FadeTransition(Duration.millis(3000), target);
        ft.setFromValue(0.0);
        ft.setToValue(1.0);
        ft.play();
        if(!target.getChildren().isEmpty())
        {
            target.getChildren().remove(0);
            Parent root = FXMLLoader.load(getClass().getResource(url));
            target.getChildren().setAll(root);
        }
        else
        {
            Parent root = FXMLLoader.load(getClass().getResource(url));
            target.getChildren().setAll(root);
        }        
    }
    
    @FXML
    private void fungsi_btn(ActionEvent event) {
        try {
            JFXButton btn = (JFXButton) event.getSource();
            switch(btn.getText())
            {
                case "Kelola Data Anggota":screen("/com/koperasi/view/anggota.fxml");
                break;
                case "Kelola Data Simpanan":screen("/com/koperasi/view/simpanan.fxml");
                break;
                case "Kelola Data Pinjaman":screen("/com/koperasi/view/pinjaman.fxml");
                break;
                case "Kelola Data Angsuran":screen("/com/koperasi/view/angsuran.fxml");
                break;
                case "Kelola Laporan":screen("/com/koperasi/view/laporan.fxml");
                break;
            }
        } catch (IOException ex) {
            Logger.getLogger(SlideMenuController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @FXML
    private void SignOut(ActionEvent event) {
        try{
            Stage s = (Stage) HomeController.HomePaneP.getScene().getWindow();
            s.close();
            Parent root = FXMLLoader.load(getClass().getResource("/com/global/view/Login.fxml"));
            Stage stage = new Stage(StageStyle.UNDECORATED);
            FadeTransition ft = new FadeTransition(Duration.millis(3000), root);
            ft.setFromValue(0.0);
            ft.setToValue(1.0);
            ft.play();
            Scene scene = new Scene(root);
            stage.setTitle("Koperasi Rukun Tetangga Sindangsari 1");
            stage.setScene(scene);
            stage.show();
        }
        catch(Exception ex)
        {
            System.err.println(ex.getMessage());
            Alert a = new Alert(Alert.AlertType.ERROR);
            a.setTitle("Error");
            a.setHeaderText("Error");
            a.setResizable(false);
            a.setContentText(ex.getMessage());
            a.showAndWait();
        }
    }
    
}
