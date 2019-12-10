/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.global.control;

import com.jfoenix.controls.JFXDrawer;
import com.jfoenix.controls.JFXHamburger;
import com.jfoenix.transitions.hamburger.HamburgerBackArrowBasicTransition;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.animation.FadeTransition;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.MenuItem;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.util.Duration;

/**
 * FXML Controller class
 *
 * @author dzikry's
 */
public class HomeController implements Initializable {

    @FXML
    private AnchorPane HomePane;    
    public static AnchorPane HomePaneP;

    @FXML
    private JFXHamburger hamburger;
    
    @FXML
    private AnchorPane TargetPane;
    public static AnchorPane TargetPaneP;
    
    @FXML
    private JFXDrawer drawer;
    
    @FXML
    private MenuItem changePassword;
    
    
    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        try {
            HomePaneP = HomePane;
            TargetPaneP = TargetPane;

            //menu            
            AnchorPane AP = FXMLLoader.load(getClass().getResource("/com/global/view/SlideMenu.fxml"));
            drawer.setSidePane(AP);
            
            HamburgerBackArrowBasicTransition burgerTask2 = new HamburgerBackArrowBasicTransition(hamburger);
            burgerTask2.setRate(-1);
            hamburger.addEventHandler(MouseEvent.MOUSE_PRESSED, e -> {
                burgerTask2.setRate(burgerTask2.getRate() * -1);
                burgerTask2.play();
                
                if(drawer.isShown()){
                    drawer.close();
                }else{
                    drawer.open();
                }
            });
        } catch (IOException ex) {
            Logger.getLogger(HomeController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public void screen(String url) throws IOException {
        AnchorPane target = TargetPaneP;
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
    private void ubahPassOnAction(ActionEvent event) throws IOException {
        screen("/com/global/view/ChangePassword.fxml");
    }
}
