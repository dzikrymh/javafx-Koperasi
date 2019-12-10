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
import java.sql.*;
import java.time.LocalDate;
import java.time.Month;
import java.time.format.DateTimeFormatter;
import java.util.Optional;
import java.util.ResourceBundle;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.util.StringConverter;
import org.controlsfx.control.Notifications;

/**
 * FXML Controller class
 *
 * @author dzikry's
 */
public class SimpananController implements Initializable {

    @FXML
    private JFXTextField txt_nama_anggota;
    @FXML
    private JFXDatePicker txt_tgl_simpanan;
    @FXML
    private JFXTextField txt_jml_simpanan;
    @FXML
    private JFXButton btn_tambah;
    @FXML
    private JFXButton btn_ubah;
    @FXML
    private JFXButton btn_hapus;
    @FXML
    private JFXComboBox<String> cb_noAnggota;
    @FXML
    private JFXComboBox<String> cb_jnsSimpanan;
    @FXML
    private JFXTextField txt_filter;
    @FXML
    private TableView<Simpanan> TableSimpanan;
    @FXML
    private TableColumn<Simpanan, String> no_simpanan;
    @FXML
    private TableColumn<Simpanan, String> tgl_simpanan;
    @FXML
    private TableColumn<Simpanan, String> jns_simpanan;
    @FXML
    private TableColumn<Simpanan, String> jml_simpanan;
    @FXML
    private TableColumn<Simpanan, String> no_anggota;
    //Deklarasi Variable
    koneksi dbsetting;
    String driver,database,user,pass;
    String kode_simpanan;
    Alert error = new Alert(Alert.AlertType.ERROR);
    Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION);
    ObservableList<Simpanan> list = FXCollections.observableArrayList();
    final ObservableList options = FXCollections.observableArrayList();
    final ObservableList jenis = FXCollections.observableArrayList("WAJIB", "SUKARELA", "POKOK");
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
        
        //set ComboBox
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
            cb_noAnggota.getItems().setAll(options);
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
        cb_jnsSimpanan.getItems().addAll(jenis);
                
        //set textfield number only 
        txt_jml_simpanan.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                if (!newValue.matches("\\d*")) {
                    txt_jml_simpanan.setText(newValue.replaceAll("[^\\d]", ""));
                }
            }
        });
        
        txt_nama_anggota.disableProperty().setValue(Boolean.TRUE);
        
        //max length char
        txt_jml_simpanan.addEventFilter(KeyEvent.KEY_TYPED, maxLength(20));
        
        //table click
        TableSimpanan.addEventHandler(MouseEvent.MOUSE_CLICKED, e -> {
            tampilfield();
        });
        
        //filter data
        ObservableList data =  TableSimpanan.getItems();
        txt_filter.textProperty().addListener((ObservableValue<? extends String> observable, String oldValue, String newValue) -> {
            if (oldValue != null && (newValue.length() < oldValue.length())) {
                TableSimpanan.setItems(data);
            }
            String value = newValue.toLowerCase();
            ObservableList<Simpanan> subentries = FXCollections.observableArrayList();

            long count = TableSimpanan.getColumns().stream().count();
            for (int i = 0; i < TableSimpanan.getItems().size(); i++) {
                for (int j = 0; j < count; j++) {
                    String entry = "" + TableSimpanan.getColumns().get(j).getCellData(i);
                    if (entry.toLowerCase().contains(value)) {
                        subentries.add(TableSimpanan.getItems().get(i));
                        break;
                    }
                }
            }
            TableSimpanan.setItems(subentries);
        });
    }
    
    private void tampilfield()
    {
        cb_noAnggota.getSelectionModel().select(TableSimpanan.getSelectionModel().getSelectedItem().getNoAnggota());
        int tahun = Integer.valueOf(TableSimpanan.getSelectionModel().getSelectedItem().getTglSimpanan().substring(0, 4));
        int hari = Integer.valueOf(TableSimpanan.getSelectionModel().getSelectedItem().getTglSimpanan().substring(8, 10));
        int bulan = Integer.valueOf(TableSimpanan.getSelectionModel().getSelectedItem().getTglSimpanan().substring(5, 7));
        txt_tgl_simpanan.setValue(LocalDate.of(tahun, Month.of(bulan), hari));
        if(TableSimpanan.getSelectionModel().getSelectedItem().getJnsSimpanan()
                .equals(cb_jnsSimpanan.getItems().get(0).toString()))
        {
            cb_jnsSimpanan.getSelectionModel().select(0);
        }
        else if(TableSimpanan.getSelectionModel().getSelectedItem().getJnsSimpanan()
                .equals(cb_jnsSimpanan.getItems().get(1).toString()))
        {
            cb_jnsSimpanan.getSelectionModel().select(1);
        }
        else
        {
            cb_jnsSimpanan.getSelectionModel().select(2);
        }
        txt_jml_simpanan.setText(TableSimpanan.getSelectionModel().getSelectedItem().getJmlSimpanan());
    }
    
    private void setTanggal()
    {
        String pattern = "yyyy-MM-dd";
        txt_tgl_simpanan.setConverter(new StringConverter<LocalDate>() {
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
    
    public EventHandler<KeyEvent> maxLength(final Integer i) {
        return new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent arg0) {

                TextField tx = (TextField) arg0.getSource();
                if (tx.getText().length() >= i) {
                    arg0.consume();
                }
            }
        };
    }

    private void loadData()
    {
        try
        {
            Class.forName(driver);
            Connection kon = DriverManager.getConnection(database, user, pass);
            Statement stt = kon.createStatement();
            String SQL  = "SELECT no_simpanan, tgl_simpanan, jenis_simpanan, jumlah_simpanan, no_anggota "
                        + "FROM simpanan "
                        + "WHERE aktif = '1'";
            ResultSet res = stt.executeQuery(SQL);
            while(res.next())
            {
                String no_simpanan1 = res.getString(1);
                String tgl_simpanan1 = res.getString(2);
                String jenis_simpanan1 = res.getString(3);
                String jumlah_simpanan1 = res.getString(4);
                String no_anggota1 = res.getString(5);
                
                list.add(new Simpanan(no_simpanan1, tgl_simpanan1, jenis_simpanan1, jumlah_simpanan1, no_anggota1));
            }
            TableSimpanan.getItems().setAll(list);
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
        no_simpanan.setCellValueFactory(new PropertyValueFactory<>("NoSimpanan"));
        tgl_simpanan.setCellValueFactory(new PropertyValueFactory<>("TglSimpanan"));
        jns_simpanan.setCellValueFactory(new PropertyValueFactory<>("JnsSimpanan"));
        jml_simpanan.setCellValueFactory(new PropertyValueFactory<>("JmlSimpanan"));
        no_anggota.setCellValueFactory(new PropertyValueFactory<>("NoAnggota"));
    }

    String saldo[] = new String[2];
    @FXML
    private void Action_cb_noAnggota(ActionEvent event) {
        try
        {
            Class.forName(driver);
            Connection kon = DriverManager.getConnection(database,user,pass);
            Statement stt = kon.createStatement();
            String SQL = "SELECT nama,saldo_wajib,saldo_sukarela "
                       + "FROM anggota "
                       + "WHERE no_anggota = "+cb_noAnggota.getSelectionModel().getSelectedItem()+";";
            ResultSet res = stt.executeQuery(SQL);
            while(res.next())
            {               
                txt_nama_anggota.setText(res.getString(1));
                saldo[0] = res.getString(2);
                saldo[1] = res.getString(3);
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

    @FXML
    private void Action_cb_jnsSimpanan(ActionEvent event) {
        if(cb_jnsSimpanan.getSelectionModel().getSelectedIndex()==0)
        {
            txt_jml_simpanan.setText("15000");
            txt_jml_simpanan.setDisable(true);
            kode_simpanan = "SW";
        }
        else if(cb_jnsSimpanan.getSelectionModel().getSelectedIndex()==1)
        {
            txt_jml_simpanan.setText("");
            txt_jml_simpanan.setDisable(false);
            kode_simpanan = "SS";
        }
        else
        {
            txt_jml_simpanan.setText("");
            txt_jml_simpanan.setDisable(true);
            kode_simpanan = "SP";
        }
    }
    
    public static class Simpanan {
        private final SimpleStringProperty NoSimpanan;
        private final SimpleStringProperty TglSimpanan;
        private final SimpleStringProperty JnsSimpanan;
        private final SimpleStringProperty JmlSimpanan;
        private final SimpleStringProperty NoAnggota;
        
        public Simpanan(String NoSimpanan, String TglSimpanan, String JnsSimpanan, String JmlSimpanan, String NoAnggota)
        {
            this.NoSimpanan = new SimpleStringProperty(NoSimpanan);
            this.TglSimpanan = new SimpleStringProperty(TglSimpanan);
            this.JnsSimpanan = new SimpleStringProperty(JnsSimpanan);
            this.JmlSimpanan = new SimpleStringProperty(JmlSimpanan);
            this.NoAnggota = new SimpleStringProperty(NoAnggota);
        }

        public String getNoSimpanan() {
            return NoSimpanan.get();
        }

        public String getTglSimpanan() {
            return TglSimpanan.get();
        }

        public String getJnsSimpanan() {
            return JnsSimpanan.get();
        }

        public String getJmlSimpanan() {
            return JmlSimpanan.get();
        }

        public String getNoAnggota() {
            return NoAnggota.get();
        }    
    }
    
    private void clean_text()
    {
        cb_noAnggota.getSelectionModel().select(-1);
        cb_jnsSimpanan.getSelectionModel().select(-1);
        txt_tgl_simpanan.setValue(null);
        txt_jml_simpanan.setText("");
        txt_nama_anggota.setText("");
    }

    @FXML
    private void TambahData(ActionEvent event) {
        if(txt_jml_simpanan.getText().isEmpty()
                ||txt_tgl_simpanan.getValue()==null
                ||cb_noAnggota.getSelectionModel().getSelectedItem()==null
                ||cb_jnsSimpanan.getSelectionModel().getSelectedItem()==null)
        {
            Notifications.create()
                         .title("information")
                         .text("Data Tidak Boleh Kosong Silahkan Dilengkapi.")
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
                            + "FROM simpanan "
                            + "WHERE aktif = '1' "
                                + "AND jenis_simpanan='WAJIB' "
                                + "AND no_anggota='"+cb_noAnggota.getSelectionModel().getSelectedItem()+"' "
                                + "AND tgl_simpanan LIKE '_____"+txt_tgl_simpanan.getValue().format(DateTimeFormatter.ofPattern("MM"))+"___';";
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
            if(count>0&&cb_jnsSimpanan.getSelectionModel().getSelectedItem().equals("WAJIB"))
            {
                Notifications.create()
                             .title("information")
                             .text("Simpanan wajib untuk bulan tersebut sudah di lakukan.")
                             .position(Pos.BOTTOM_RIGHT)
                             .showInformation();
            }
            else if(cb_jnsSimpanan.getSelectionModel().getSelectedItem().equals("POKOK"))
            {
                Notifications.create()
                             .title("information")
                             .text("Jenis simpanan tidak boleh memilih simpanan pokok!")
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
                        String SQL = "INSERT INTO simpanan " 
                                   + "VALUES "+"( '"+kode_simpanan+cb_noAnggota.getSelectionModel().getSelectedItem()+txt_tgl_simpanan.getValue().format(DateTimeFormatter.ofPattern("ddMMyyyy"))+"',"
                                              + " '"+txt_tgl_simpanan.getValue()+"',"
                                              + " '"+cb_jnsSimpanan.getSelectionModel().getSelectedItem()+"',"
                                              + " '"+txt_jml_simpanan.getText()+"',"
                                              + " '"+cb_noAnggota.getSelectionModel().getSelectedItem()+"',"
                                              + " '1')";
                        stt.executeUpdate(SQL);
                        TableSimpanan.getItems().add(new Simpanan(kode_simpanan+cb_noAnggota.getSelectionModel().getSelectedItem()+txt_tgl_simpanan.getValue().format(DateTimeFormatter.ofPattern("ddMMyyyy")), 
                                                                  txt_tgl_simpanan.getValue().toString(), 
                                                                  cb_jnsSimpanan.getSelectionModel().getSelectedItem(), 
                                                                  txt_jml_simpanan.getText(), 
                                                                  cb_noAnggota.getSelectionModel().getSelectedItem()));
                        String KategoriSimpanan[] = {"WAJIB","SUKARELA"};
                        String SQL1 = "SELECT SUM(jumlah_simpanan) "
                                    + "FROM simpanan "
                                    + "WHERE no_anggota = '"+cb_noAnggota.getSelectionModel().getSelectedItem()+"' AND jenis_simpanan = '"+KategoriSimpanan[cb_jnsSimpanan.getSelectionModel().getSelectedIndex()]+"' AND aktif = '1';";
                        ResultSet res = stt.executeQuery(SQL1);
                        String sum = null;
                        while(res.next())
                        {
                            sum = res.getString(1);
                        }
                        String KategoriSaldo[] = {"saldo_wajib","saldo_sukarela"};
                        String SQL2 = "UPDATE anggota "
                                    + "SET "+KategoriSaldo[cb_jnsSimpanan.getSelectionModel().getSelectedIndex()]+" = REPLACE("+KategoriSaldo[cb_jnsSimpanan.getSelectionModel().getSelectedIndex()]+","
                                                                                                          + " '"+saldo[cb_jnsSimpanan.getSelectionModel().getSelectedIndex()]+"',"
                                                                                                          + " '"+sum+"') "
                                    + "WHERE aktif = '1' AND no_anggota = '"+cb_noAnggota.getSelectionModel().getSelectedItem()+"';";
                        stt.executeUpdate(SQL2);
                        String SQLcommit = "COMMIT;";
                        stt.executeQuery(SQLcommit);
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
        int index = TableSimpanan.getSelectionModel().getSelectedIndex();
        if (index >= 0)
        {
            if (TableSimpanan.getSelectionModel().getSelectedItem().getJnsSimpanan().equals("POKOK"))
            {
                Notifications.create()
                             .title("Information")
                             .text("Simpanan pokok tidak dapat di ubah.")
                             .position(Pos.BOTTOM_RIGHT)
                             .showInformation();
            }
            else if (TableSimpanan.getSelectionModel().getSelectedItem().getJnsSimpanan().equals("WAJIB"))
            {
                Notifications.create()
                             .title("Information")
                             .text("Simpanan wajib tidak dapat di ubah.")
                             .position(Pos.BOTTOM_RIGHT)
                             .showInformation();
            }
            else
            {
                if (cb_noAnggota.getSelectionModel().getSelectedItem().equals(TableSimpanan.getSelectionModel().getSelectedItem().getNoAnggota())
                        ||txt_tgl_simpanan.getValue().toString().equals(TableSimpanan.getSelectionModel().getSelectedItem().getTglSimpanan())
                        ||cb_jnsSimpanan.getSelectionModel().getSelectedItem().equals(TableSimpanan.getSelectionModel().getSelectedItem().getJnsSimpanan()))
                {
                    confirmation.setTitle("Confirmation Dialog");
                    confirmation.setHeaderText(null);
                    confirmation.setResizable(false);
                    confirmation.setContentText("Apa anda yakin akan mengubah data tersebut ?");
                    confirmation.getButtonTypes().setAll(ButtonType.YES, ButtonType.NO);
                    Optional<ButtonType> result = confirmation.showAndWait();
                    if(result.get() == ButtonType.YES)
                    {
                        //data baru
                        String JumlahSimpan = txt_jml_simpanan.getText();
                        
                        //data lama
                        String noSimpanan = TableSimpanan.getSelectionModel().getSelectedItem().getNoSimpanan();
                        String tglSimpanan = TableSimpanan.getSelectionModel().getSelectedItem().getTglSimpanan();
                        String jnsSimpanan = TableSimpanan.getSelectionModel().getSelectedItem().getJnsSimpanan();
                        String jmlSimpanan = TableSimpanan.getSelectionModel().getSelectedItem().getJmlSimpanan();
                        String noAnggota = TableSimpanan.getSelectionModel().getSelectedItem().getNoAnggota();
                        try
                        {
                            Class.forName(driver);
                            Connection kon = DriverManager.getConnection(database, user, pass);
                            Statement stt = kon.createStatement();
                            String SQLstart = "START TRANSACTION;";
                            stt.executeQuery(SQLstart);
                            String SQL = "UPDATE simpanan " 
                                       + "SET "
                                       + "jumlah_simpanan=REPLACE(jumlah_simpanan,'"+jmlSimpanan+"','"+JumlahSimpan+"') "
                                       + "WHERE "
                                       + "no_simpanan='"+noSimpanan+"';";
                            stt.executeUpdate(SQL);
                            TableSimpanan.getItems().remove(index);
                            TableSimpanan.getItems().add(new Simpanan(
                                    noSimpanan, 
                                    tglSimpanan, 
                                    jnsSimpanan, 
                                    JumlahSimpan, 
                                    noAnggota));
                            String SQLsukarela = "SELECT SUM(jumlah_simpanan) "
                                               + "FROM simpanan "
                                               + "WHERE no_anggota = '"+cb_noAnggota.getSelectionModel().getSelectedItem()+"' AND jenis_simpanan = 'SUKARELA' AND aktif = '1';";
                            ResultSet rs = stt.executeQuery(SQLsukarela);
                            String sum1 = null;
                            while(rs.next())
                            {
                                sum1 = rs.getString(1);
                            }
                            if(sum1 == null)
                            {
                                sum1 = "0";
                            }
                            String SQLsaldosukarela = "UPDATE anggota "
                                                    + "SET saldo_sukarela = REPLACE(saldo_sukarela, '"+saldo[cb_jnsSimpanan.getSelectionModel().getSelectedIndex()]+"', '"+sum1+"') "
                                                    + "WHERE aktif = '1' AND no_anggota = '"+cb_noAnggota.getSelectionModel().getSelectedItem()+"';";
                            stt.executeUpdate(SQLsaldosukarela);
                            String SQLcommit = "COMMIT;";
                            stt.executeQuery(SQLcommit);
                            rs.close();
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
                                 .text("Hanya jumlah simpanan yang dapat di ubah.")
                                 .position(Pos.BOTTOM_RIGHT)
                                 .showInformation();
                }
            }
        }
        else
        {
            if (TableSimpanan.getItems().size()==0)
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
        int index = TableSimpanan.getSelectionModel().getSelectedIndex();
        if (index >= 0)
        {
            if(TableSimpanan.getSelectionModel().getSelectedItem().getJnsSimpanan().equals("POKOK"))
            {
                Notifications.create()
                             .title("Information")
                             .text("Simpanan pokok tidak dapat di hapus.")
                             .position(Pos.BOTTOM_RIGHT)
                             .showInformation();
            }
            else
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
                        String SQL  = "UPDATE simpanan "
                                    + "SET aktif = REPLACE(aktif,'1','0')"
                                    + "WHERE no_simpanan = '"+TableSimpanan.getSelectionModel().getSelectedItem().getNoSimpanan()+"'";
                        stt.executeUpdate(SQL);
                        TableSimpanan.getItems().remove(index);
                        String KategoriSimpanan[] = {"WAJIB","SUKARELA"};
                        String SQL1 = "SELECT SUM(jumlah_simpanan) "
                                    + "FROM simpanan "
                                    + "WHERE no_anggota = '"+cb_noAnggota.getSelectionModel().getSelectedItem()+"' AND jenis_simpanan = '"+KategoriSimpanan[cb_jnsSimpanan.getSelectionModel().getSelectedIndex()]+"' AND aktif = '1';";
                        ResultSet res = stt.executeQuery(SQL1);
                        String sum = null;
                        while(res.next())
                        {
                            sum = res.getString(1);
                        }
                        if(sum == null)
                        {
                            sum = "0";
                        }
                        String KategoriSaldo[] = {"saldo_wajib","saldo_sukarela"};
                        String SQL2 = "UPDATE anggota "
                                    + "SET "+KategoriSaldo[cb_jnsSimpanan.getSelectionModel().getSelectedIndex()]+" = REPLACE("+KategoriSaldo[cb_jnsSimpanan.getSelectionModel().getSelectedIndex()]+","
                                                                                                          + " '"+saldo[cb_jnsSimpanan.getSelectionModel().getSelectedIndex()]+"',"
                                                                                                          + " '"+sum+"') "
                                    + "WHERE no_anggota = '"+cb_noAnggota.getSelectionModel().getSelectedItem()+"' AND aktif = '1';";
                        stt.executeUpdate(SQL2);
                        String SQLcommit = "COMMIT";
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
        }
        else
        {
            if (TableSimpanan.getItems().size()==0)
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
