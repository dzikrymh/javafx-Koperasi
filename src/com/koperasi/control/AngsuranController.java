/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.koperasi.control;

import com.database.koneksi;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXDatePicker;
import com.jfoenix.controls.JFXTextField;
import java.net.URL;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.time.LocalDate;
import java.time.Month;
import java.time.format.DateTimeFormatter;
import java.util.Optional;
import java.util.ResourceBundle;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.util.StringConverter;
import org.controlsfx.control.Notifications;

/**
 * FXML Controller class
 *
 * @author dzikry's
 */
public class AngsuranController implements Initializable {

    @FXML
    private JFXTextField txt_nama_anggota;
    @FXML
    private JFXDatePicker txt_tgl_bayar;
    @FXML
    private JFXTextField txt_biaya_angsuran;
    @FXML
    private JFXButton btn_tambah;
    @FXML
    private JFXButton btn_ubah;
    @FXML
    private JFXButton btn_hapus;
    @FXML
    private JFXComboBox<String> cb_noAnggota;
    @FXML
    private JFXComboBox<String> cb_no_pinjaman;
    @FXML
    private JFXTextField txt_status_pinjaman;
    @FXML
    private JFXTextField txt_filter;
    @FXML
    private TableView<Angsuran> TableAngsuran;
    @FXML
    private TableColumn<Angsuran, String> no_angsuran;
    @FXML
    private TableColumn<Angsuran, String> tgl_bayar;
    @FXML
    private TableColumn<Angsuran, String> no_pinjaman;
    //Deklarasi Variable
    koneksi dbsetting;
    String driver,database,user,pass;
    Alert error = new Alert(Alert.AlertType.ERROR);
    Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION);
    ObservableList<Angsuran> list = FXCollections.observableArrayList();
    final ObservableList NOANGGOTA = FXCollections.observableArrayList();
    final ObservableList NOPINJAMAN = FXCollections.observableArrayList();
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
        initCol();
        loadData();
        setTanggal();
        txt_nama_anggota.setDisable(true);
        txt_biaya_angsuran.setDisable(true);
        txt_status_pinjaman.setDisable(true);
        txt_tgl_bayar.setDisable(true);
        cb_no_pinjaman.setDisable(true);
    
        //filter data
        ObservableList data =  TableAngsuran.getItems();
        txt_filter.textProperty().addListener((ObservableValue<? extends String> observable, String oldValue, String newValue) -> {
            if (oldValue != null && (newValue.length() < oldValue.length())) {
                TableAngsuran.setItems(data);
            }
            String value = newValue.toLowerCase();
            ObservableList<Angsuran> subentries = FXCollections.observableArrayList();

            long count = TableAngsuran.getColumns().stream().count();
            for (int i = 0; i < TableAngsuran.getItems().size(); i++) {
                for (int j = 0; j < count; j++) {
                    String entry = "" + TableAngsuran.getColumns().get(j).getCellData(i);
                    if (entry.toLowerCase().contains(value)) {
                        subentries.add(TableAngsuran.getItems().get(i));
                        break;
                    }
                }
            }
            TableAngsuran.setItems(subentries);
        });
        
        //set combo box no_anggota
        try
        {
            Class.forName(driver);
            Connection kon = DriverManager.getConnection(database,user,pass);
            Statement stt = kon.createStatement();
            String SQL = "SELECT no_anggota "
                       + "FROM anggota "
                       + "WHERE aktif = '1';";
            ResultSet res = stt.executeQuery(SQL);
            while(res.next())
            {               
                NOANGGOTA.add(res.getString(1));
            }
            cb_noAnggota.getItems().setAll(NOANGGOTA);
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
        
        //table click
        TableAngsuran.addEventHandler(MouseEvent.MOUSE_CLICKED, e -> {
            tampilfield();
        });
    }    
    
    private void clean_text()
    {
        txt_tgl_bayar.setDisable(true);
        cb_no_pinjaman.setDisable(true);
        
        cb_noAnggota.getSelectionModel().select(-1);
        cb_no_pinjaman.getSelectionModel().select(-1);
        txt_tgl_bayar.setValue(null);
    }
    
    private void tampilfield()
    {
        cb_no_pinjaman.getSelectionModel().select(TableAngsuran.getSelectionModel().getSelectedItem().getNoPinjaman());
        int tahun = Integer.valueOf(TableAngsuran.getSelectionModel().getSelectedItem().getTglBayar().substring(0, 4));
        int hari = Integer.valueOf(TableAngsuran.getSelectionModel().getSelectedItem().getTglBayar().substring(8, 10));
        int bulan = Integer.valueOf(TableAngsuran.getSelectionModel().getSelectedItem().getTglBayar().substring(5, 7));
        txt_tgl_bayar.setValue(LocalDate.of(tahun, Month.of(bulan), hari));
        try
        {
            Class.forName(driver);
            Connection kon = DriverManager.getConnection(database,user,pass);
            Statement stt = kon.createStatement();
            String SQL = "SELECT no_anggota "
                       + "FROM pinjaman "
                       + "WHERE no_pinjaman = "+cb_no_pinjaman.getSelectionModel().getSelectedItem()+";";
            ResultSet res = stt.executeQuery(SQL);
            while(res.next())
            {
                cb_noAnggota.getSelectionModel().select(res.getString(1));
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
    }
    
    //set format tanggal
    private void setTanggal()
    {
        String pattern = "yyyy-MM-dd";
        txt_tgl_bayar.setConverter(new StringConverter<LocalDate>() {
             DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern(pattern);

             @Override 
             public String toString(LocalDate date) {
                 if (date != null) {
                     return dateFormatter.format(date);
                 } else {
                     return "";
                 }
             }

             @Override 
             public LocalDate fromString(String string) {
                 if (string != null && !string.isEmpty()) {
                     return LocalDate.parse(string, dateFormatter);
                 } else {
                     return null;
                 }
             }
        });
    }
    
    private void loadData()
    {
        try
        {
            Class.forName(driver);
            Connection kon = DriverManager.getConnection(database, user, pass);
            Statement stt = kon.createStatement();
            String SQL  = "SELECT no_angsuran, tgl_bayar, no_pinjaman "
                        + "FROM angsuran "
                        + "WHERE aktif = '1'";
            ResultSet res = stt.executeQuery(SQL);
            while(res.next())
            {
                String no_angsuran1 = res.getString(1);
                String tgl_bayar1 = res.getString(2);
                String no_pinjaman1 = res.getString(3);
                
                list.add(new Angsuran(no_angsuran1, tgl_bayar1, no_pinjaman1));
            }
            TableAngsuran.getItems().setAll(list);
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
    
    private void initCol(){
        no_angsuran.setCellValueFactory(new PropertyValueFactory<>("NoAngsuran"));
        tgl_bayar.setCellValueFactory(new PropertyValueFactory<>("TglBayar"));
        no_pinjaman.setCellValueFactory(new PropertyValueFactory<>("NoPinjaman"));
    }

    @FXML
    private void ActionNoAnggota(ActionEvent event) {
        NOPINJAMAN.clear();
        try
        {
            Class.forName(driver);
            Connection kon = DriverManager.getConnection(database,user,pass);
            Statement stt = kon.createStatement();
            String SQL = "SELECT nama "
                       + "FROM anggota "
                       + "WHERE no_anggota = "+cb_noAnggota.getSelectionModel().getSelectedItem()+";";
            String SQL1 = "SELECT no_pinjaman "
                        + "FROM pinjaman "
                        + "WHERE no_anggota = "+cb_noAnggota.getSelectionModel().getSelectedItem()+" AND aktif = '1';";
            ResultSet res = stt.executeQuery(SQL);
            while(res.next())
            {
                txt_nama_anggota.setText(res.getString(1));
            }
            ResultSet rs = stt.executeQuery(SQL1);
            while(rs.next())
            {
                NOPINJAMAN.add(rs.getString(1));
            }
            cb_no_pinjaman.getItems().setAll(NOPINJAMAN);
            cb_no_pinjaman.setDisable(false);
            rs.close();
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
    private void ActionNoPinjaman(ActionEvent event) {
        try
        {
            Class.forName(driver);
            Connection kon = DriverManager.getConnection(database,user,pass);
            Statement stt = kon.createStatement();
            String SQL = "SELECT angsuran, status_pinjaman "
                       + "FROM pinjaman "
                       + "WHERE no_pinjaman = "+cb_no_pinjaman.getSelectionModel().getSelectedItem()+";";
            ResultSet res = stt.executeQuery(SQL);
            while(res.next())
            {
                txt_biaya_angsuran.setText(res.getString(1));
                txt_status_pinjaman.setText(res.getString(2));
                txt_tgl_bayar.setDisable(false);
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
    }
    
    public static class Angsuran {
        private final SimpleStringProperty NoAngsuran;
        private final SimpleStringProperty TglBayar;
        private final SimpleStringProperty NoPinjaman;
        
        public Angsuran(String NoAngsuran, String TglBayar, String NoPinjaman)
        {
            this.NoAngsuran = new SimpleStringProperty(NoAngsuran);
            this.TglBayar = new SimpleStringProperty(TglBayar);
            this.NoPinjaman = new SimpleStringProperty(NoPinjaman);
        }

        public String getNoAngsuran() {
            return NoAngsuran.get();
        }

        public String getTglBayar() {
            return TglBayar.get();
        }

        public String getNoPinjaman() {
            return NoPinjaman.get();
        }
        
    }

    @FXML
    private void TambahData(ActionEvent event) {
        if (cb_noAnggota.getSelectionModel().getSelectedItem() == null)
        {
            Notifications.create()
                         .title("Information")
                         .text("Pilih No Anggota.")
                         .position(Pos.BOTTOM_RIGHT)
                         .showInformation();
        }
        else
        {
            int count = 0;
            try
            {
                Class.forName(driver);
                Connection kon = DriverManager.getConnection(database,user,pass);
                Statement stt = kon.createStatement();
                String SQL  = "SELECT COUNT(*) "
                            + "FROM angsuran "
                            + "WHERE no_pinjaman = '"+cb_no_pinjaman.getSelectionModel().getSelectedItem()+"' AND tgl_bayar LIKE '"+txt_tgl_bayar.getValue().format(DateTimeFormatter.ofPattern("yyyy-MM"))+"___' AND aktif = '1';";
                ResultSet res = stt.executeQuery(SQL);
                while(res.next())
                {               
                    count = Integer.parseInt(res.getString(1));
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

            if(txt_tgl_bayar.getValue()==null)
            {
                Notifications.create()
                             .title("Information")
                             .text("Tanggal bayar tidak boleh kosong silahkan dilengkapi.")
                             .position(Pos.BOTTOM_RIGHT)
                             .showInformation();
                txt_tgl_bayar.requestFocus();
            }
            else if(txt_status_pinjaman.getText().equals("SUDAH LUNAS"))
            {
                Notifications.create()
                             .title("Information")
                             .text("No pinjaman ini sudah lunas.")
                             .position(Pos.BOTTOM_RIGHT)
                             .showInformation();
            }
            else if(count>0)
            {
                Notifications.create()
                             .title("Information")
                             .text("Angsuran bulan tersebut telah dibayar.")
                             .position(Pos.BOTTOM_RIGHT)
                             .showInformation();
            }
            else
            {
                confirmation.setTitle("Confirmation Dialog");
                confirmation.setHeaderText(null);
                confirmation.setResizable(false);
                confirmation.setContentText("Apa anda yakin data tersebut sudah benar ?");
                confirmation.getButtonTypes().setAll(ButtonType.YES, ButtonType.NO);
                Optional<ButtonType> result = confirmation.showAndWait();
                if(result.get() == ButtonType.YES)
                {
                    try
                    {
                        Class.forName(driver);
                        Connection kon = DriverManager.getConnection(database,
                                                                     user,
                                                                     pass);
                        Statement stt = kon.createStatement();
                        String SQLstart = "START TRANSACTION;";
                        stt.executeQuery(SQLstart);
                        String SQLID = "SELECT COUNT(no_angsuran)+1 "
                                     + "FROM angsuran;";
                        ResultSet rs = stt.executeQuery(SQLID);
                        String ID = null;
                        while(rs.next())
                        {
                            ID = rs.getString(1);
                        }
                        String SQL = "INSERT INTO angsuran " 
                                   + "VALUES "+"( '"+ID+"',"
                                              + " '"+txt_tgl_bayar.getValue()+"',"
                                              + " '"+cb_no_pinjaman.getSelectionModel().getSelectedItem()+"', "
                                              + " '1')";
                        stt.executeUpdate(SQL);
                        TableAngsuran.getItems().add(new Angsuran(ID, txt_tgl_bayar.getValue().toString(), cb_no_pinjaman.getSelectionModel().getSelectedItem()));
                        String sisa = null;
                        String lama_pinjaman = null;
                        String SQL1 = "SELECT sisa_pinjaman, lama_pinjaman "
                                    + "FROM pinjaman "
                                    + "WHERE no_pinjaman = '"+cb_no_pinjaman.getSelectionModel().getSelectedItem()+"' AND aktif = '1';";
                        ResultSet res = stt.executeQuery(SQL1);
                        while(res.next())
                        {
                            sisa = res.getString(1);
                            lama_pinjaman = res.getString(2);
                        }
                        long hasil = Long.parseLong(sisa) - Long.parseLong(txt_biaya_angsuran.getText());
                        int lama_pinjaman_baru = Integer.parseInt(lama_pinjaman) - 1;
                        if(lama_pinjaman_baru == 0)
                        {
                            String SQL2 = "UPDATE pinjaman "
                                        + "SET sisa_pinjaman = REPLACE(sisa_pinjaman,'"+sisa+"','"+hasil+"'), "
                                        + "lama_pinjaman = REPLACE(lama_pinjaman,'"+lama_pinjaman+"','"+lama_pinjaman_baru+"'),"
                                        + "status_pinjaman = REPLACE(status_pinjaman,'BELUM LUNAS','SUDAH LUNAS') "
                                        + "WHERE no_pinjaman = '"+cb_no_pinjaman.getSelectionModel().getSelectedItem()+"';";
                            stt.executeUpdate(SQL2);
                            txt_status_pinjaman.setText("SUDAH LUNAS");
                        }
                        else
                        {
                            String SQL2 = "UPDATE pinjaman "
                                        + "SET sisa_pinjaman = REPLACE(sisa_pinjaman,'"+sisa+"','"+hasil+"'), "
                                        + "lama_pinjaman = REPLACE(lama_pinjaman,'"+lama_pinjaman+"','"+lama_pinjaman_baru+"') "
                                        + "WHERE no_pinjaman = '"+cb_no_pinjaman.getSelectionModel().getSelectedItem()+"';";
                            stt.executeUpdate(SQL2);
                        }
                        String SQLcommit = "COMMIT;";
                        stt.executeQuery(SQLcommit);
                        rs.close();
                        res.close();
                        stt.close();
                        kon.close();
                        Notifications.create()
                                         .title("Information")
                                         .text("Data Berhasil Di Tambahkan.")
                                         .position(Pos.BOTTOM_RIGHT)
                                         .showInformation();
                        clean_text();
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
        }
    }

    @FXML
    private void UbahData(ActionEvent event) {
        int index = TableAngsuran.getSelectionModel().getSelectedIndex();
        if (index >= 0)
        {
            if(cb_no_pinjaman.getSelectionModel().getSelectedItem().equals(TableAngsuran.getSelectionModel().getSelectedItem().getNoPinjaman())
                    &&txt_tgl_bayar.getValue().toString().substring(0,7).equals(TableAngsuran.getSelectionModel().getSelectedItem().getTglBayar().substring(0, 7)))
            {
                confirmation.setTitle("Confirmation Dialog");
                confirmation.setHeaderText(null);
                confirmation.setResizable(false);
                confirmation.setContentText("Apa anda yakin akan mengubah data tersebut ?");
                confirmation.getButtonTypes().setAll(ButtonType.YES, ButtonType.NO);
                Optional<ButtonType> result = confirmation.showAndWait();
                if(result.get() == ButtonType.YES)
                {
                    //data lama
                    String noAngsuran = TableAngsuran.getSelectionModel().getSelectedItem().getNoAngsuran();
                    String tglBayarLama = TableAngsuran.getSelectionModel().getSelectedItem().getTglBayar();
                    String noPinjaman = TableAngsuran.getSelectionModel().getSelectedItem().getNoPinjaman();
                    
                    //data baru
                    String tglBayarBaru = txt_tgl_bayar.getValue().toString();
                    
                    try
                    {
                        Class.forName(driver);
                        Connection kon = DriverManager.getConnection(database, user, pass);
                        Statement stt = kon.createStatement();
                        String SQL  = "UPDATE angsuran "
                                    + "SET tgl_bayar = REPLACE(tgl_bayar,'"+tglBayarLama+"','"+tglBayarBaru+"') "
                                    + "WHERE no_angsuran = '"+noAngsuran+"';";
                        stt.executeUpdate(SQL);
                        TableAngsuran.getItems().remove(index);
                        TableAngsuran.getItems().add(new Angsuran(noAngsuran, tglBayarBaru, noPinjaman));
                        stt.close();
                        kon.close();
                        Notifications.create()
                                 .title("Information")
                                 .text("Data Berhasil Diubah.")
                                 .position(Pos.BOTTOM_RIGHT)
                                 .showInformation();
                        clean_text();
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
            else
            {
                Notifications.create()
                             .title("Information")
                             .text("No pinjaman atau bulan dan tahun bayar tidak dapat di rubah! Silahkan di perbaiki.")
                             .position(Pos.BOTTOM_RIGHT)
                             .showInformation();
            }
        }
        else
        {
            if (TableAngsuran.getItems().size()==0)
            {
                Notifications.create()
                             .title("Information")
                             .text("Data Kosong.")
                             .position(Pos.BOTTOM_RIGHT)
                             .showInformation();
            }
            else
            {
                Notifications.create()
                             .title("Information")
                             .text("Pilih data yang akan di ubah.")
                             .position(Pos.BOTTOM_RIGHT)
                             .showInformation();
            }
        }
    }

    @FXML
    private void HapusData(ActionEvent event) {
        int index = TableAngsuran.getSelectionModel().getSelectedIndex();
        if (index >= 0)
        {
            confirmation.setTitle("Confirmation Dialog");
            confirmation.setHeaderText(null);
            confirmation.setResizable(false);
            confirmation.setContentText("Apa anda yakin akan menghapus data tersebut ?");
            confirmation.getButtonTypes().setAll(ButtonType.YES, ButtonType.NO);
            Optional<ButtonType> result = confirmation.showAndWait();
            if(result.get() == ButtonType.YES)
            {
                try
                {
                    Class.forName(driver);
                    Connection kon = DriverManager.getConnection(database,user,pass);
                    Statement stt = kon.createStatement();
                    String SQLstart = "START TRANSACTION;";
                    stt.executeQuery(SQLstart);
                    String sisa = null;
                    String lama_pinjaman = null;
                    String SQL = "SELECT sisa_pinjaman, lama_pinjaman "
                                + "FROM pinjaman "
                                + "WHERE no_pinjaman = '"+TableAngsuran.getSelectionModel().getSelectedItem().getNoPinjaman()+"'";
                    ResultSet res = stt.executeQuery(SQL);
                    while(res.next())
                    {
                        sisa = res.getString(1);
                        lama_pinjaman = res.getString(2);
                    }
                    long hasil = Long.parseLong(sisa) + Long.parseLong(txt_biaya_angsuran.getText());
                    int lama_pinjaman_baru = Integer.parseInt(lama_pinjaman) + 1;
                    if(Integer.parseInt(lama_pinjaman)==0)
                    {
                        String SQL2 = "UPDATE pinjaman "
                                    + "SET sisa_pinjaman = REPLACE(sisa_pinjaman,'"+sisa+"','"+hasil+"'), "
                                    + "lama_pinjaman = REPLACE(lama_pinjaman,'"+lama_pinjaman+"','"+lama_pinjaman_baru+"'),"
                                    + "status_pinjaman = REPLACE(status_pinjaman,'SUDAH LUNAS','BELUM LUNAS') "
                                    + "WHERE no_pinjaman = '"+TableAngsuran.getSelectionModel().getSelectedItem().getNoPinjaman()+"';";
                        stt.executeUpdate(SQL2);
                        txt_status_pinjaman.setText("BELUM LUNAS");
                    }
                    else
                    {
                        String SQL2 = "UPDATE pinjaman "
                                    + "SET sisa_pinjaman = REPLACE(sisa_pinjaman,'"+sisa+"','"+hasil+"'), "
                                    + "lama_pinjaman = REPLACE(lama_pinjaman,'"+lama_pinjaman+"','"+lama_pinjaman_baru+"') "
                                    + "WHERE no_pinjaman = '"+TableAngsuran.getSelectionModel().getSelectedItem().getNoPinjaman()+"';";
                        stt.executeUpdate(SQL2);
                    }
                    String SQL3 = "UPDATE angsuran "
                                + "SET aktif = REPLACE(aktif,'1','0') "
                                + "WHERE no_angsuran='"+TableAngsuran.getSelectionModel().getSelectedItem().getNoAngsuran()+"'";
                    stt.executeUpdate(SQL3);
                    TableAngsuran.getItems().remove(index);
                    String SQLcommit = "COMMIT;";
                    stt.executeQuery(SQLcommit);
                    res.close();
                    stt.close();
                    kon.close();
                    Notifications.create()
                                 .title("Information")
                                 .text("Data Berhasil Dihapus.")
                                 .position(Pos.BOTTOM_RIGHT)
                                 .showInformation();
                    clean_text();
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
        else
        {
            if (TableAngsuran.getItems().size()==0)
            {
                Notifications.create()
                             .title("Information")
                             .text("Data Kosong.")
                             .position(Pos.BOTTOM_RIGHT)
                             .showInformation();
            }
            else
            {
                Notifications.create()
                             .title("Information")
                             .text("Pilih data yang akan di hapus.")
                             .position(Pos.BOTTOM_RIGHT)
                             .showInformation();
            }
        }
    }
    
}
