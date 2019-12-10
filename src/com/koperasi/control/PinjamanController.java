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
import com.jfoenix.validation.RequiredFieldValidator;
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
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.event.EventType;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Background;
import javafx.util.StringConverter;
import org.controlsfx.control.Notifications;

/**
 * FXML Controller class
 *
 * @author dzikry's
 */
public class PinjamanController implements Initializable {

    @FXML
    private JFXTextField txt_nama_anggota;
    @FXML
    private JFXDatePicker txt_tgl_pinjaman;
    @FXML
    private JFXTextField txt_jml_pinjaman;
    @FXML
    private JFXButton btn_tambah;
    @FXML
    private JFXButton btn_ubah;
    @FXML
    private JFXButton btn_hapus;
    @FXML
    private JFXComboBox<String> cb_noAnggota;
    @FXML
    private JFXComboBox<String> cb_lamaPinjaman;
    @FXML
    private JFXTextField txt_filter;
    @FXML
    private TableView<Pinjaman> TablePinjaman;
    @FXML
    private TableColumn<Pinjaman, String> no_pinjaman;
    @FXML
    private TableColumn<Pinjaman, String> tgl_pinjaman;
    @FXML
    private TableColumn<Pinjaman, String> jml_pinjaman;
    @FXML
    private TableColumn<Pinjaman, String> bunga;
    @FXML
    private TableColumn<Pinjaman, String> total_pinjaman;
    @FXML
    private TableColumn<Pinjaman, String> lama_pinjaman;
    @FXML
    private TableColumn<Pinjaman, String> angsuran;
    @FXML
    private TableColumn<Pinjaman, String> sisa_pinjaman;
    @FXML
    private TableColumn<Pinjaman, String> no_anggota;
    @FXML
    private TableColumn<Pinjaman, String> status_pinjaman;
    //Deklarasi Variable
    koneksi dbsetting;
    String driver,database,user,pass;
    Alert error = new Alert(Alert.AlertType.ERROR);
    Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION);
    ObservableList<Pinjaman> list = FXCollections.observableArrayList();
    final ObservableList options = FXCollections.observableArrayList();
    final ObservableList lama_pinjam = FXCollections.observableArrayList("1", "2", "3", "4", "5", "6", "7", "8", "9", "10");
    @FXML
    private Label limit;
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
        
        //filter data
        ObservableList data =  TablePinjaman.getItems();
        txt_filter.textProperty().addListener((ObservableValue<? extends String> observable, String oldValue, String newValue) -> {
            if (oldValue != null && (newValue.length() < oldValue.length())) {
                TablePinjaman.setItems(data);
            }
            String value = newValue.toLowerCase();
            ObservableList<Pinjaman> subentries = FXCollections.observableArrayList();

            long count = TablePinjaman.getColumns().stream().count();
            for (int i = 0; i < TablePinjaman.getItems().size(); i++) {
                for (int j = 0; j < count; j++) {
                    String entry = "" + TablePinjaman.getColumns().get(j).getCellData(i);
                    if (entry.toLowerCase().contains(value)) {
                        subentries.add(TablePinjaman.getItems().get(i));
                        break;
                    }
                }
            }
            TablePinjaman.setItems(subentries);
        });
        
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
        txt_nama_anggota.setDisable(true);
        txt_tgl_pinjaman.setDisable(true);
        txt_jml_pinjaman.setDisable(true);
        cb_lamaPinjaman.setDisable(true);
        
        //set textfield number only 
        txt_jml_pinjaman.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                if (!newValue.matches("\\d*")) {
                    txt_jml_pinjaman.setText(newValue.replaceAll("[^\\d]", ""));
                }
            }
        });
     
        //max length char
        txt_jml_pinjaman.addEventFilter(KeyEvent.KEY_TYPED, maxLength(20));
        
        //table click
        TablePinjaman.addEventHandler(MouseEvent.MOUSE_CLICKED, e -> {
            tampilfield();
        });
        
        cb_lamaPinjaman.getItems().addAll(lama_pinjam);
    }    

    private void tampilfield()
    {
        cb_noAnggota.getSelectionModel().select(TablePinjaman.getSelectionModel().getSelectedItem().getNoAnggota());
        int tahun = Integer.valueOf(TablePinjaman.getSelectionModel().getSelectedItem().getTglPinjaman().substring(0, 4));
        int hari = Integer.valueOf(TablePinjaman.getSelectionModel().getSelectedItem().getTglPinjaman().substring(8, 10));
        int bulan = Integer.valueOf(TablePinjaman.getSelectionModel().getSelectedItem().getTglPinjaman().substring(5, 7));
        txt_tgl_pinjaman.setValue(LocalDate.of(tahun, Month.of(bulan), hari));
        txt_jml_pinjaman.setText(TablePinjaman.getSelectionModel().getSelectedItem().getJmlPinjaman());
        cb_lamaPinjaman.getSelectionModel().select(TablePinjaman.getSelectionModel().getSelectedItem().getLamaPinjaman());
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
    
    private void setTanggal()
    {
        String pattern = "yyyy-MM-dd";
        txt_tgl_pinjaman.setConverter(new StringConverter<LocalDate>() {
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
            String SQL  = "SELECT no_pinjaman, tgl_pinjaman, jumlah_pinjaman, bunga, total_pinjaman, "
                            + "lama_pinjaman, angsuran, sisa_pinjaman, no_anggota, status_pinjaman "
                        + "FROM pinjaman "
                        + "WHERE aktif = '1'";
            ResultSet res = stt.executeQuery(SQL);
            while(res.next())
            {
                String no_pinjaman1 = res.getString(1);
                String tgl_pinjaman1 = res.getString(2);
                String jumlah_pinjaman1 = res.getString(3);
                String bunga1 = res.getString(4);
                String total_pinjaman1 = res.getString(5);
                String lama_pinjaman1 = res.getString(6);
                String angsuran1 = res.getString(7);
                String sisa_pinjaman1 = res.getString(8);
                String no_anggota1 = res.getString(9);
                String status_pinjaman1 = res.getString(10);
                
                list.add(new Pinjaman(no_pinjaman1, tgl_pinjaman1, jumlah_pinjaman1, bunga1, total_pinjaman1, lama_pinjaman1, angsuran1, sisa_pinjaman1, no_anggota1, status_pinjaman1));
            }
            TablePinjaman.getItems().setAll(list);
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
        no_pinjaman.setCellValueFactory(new PropertyValueFactory<>("NoPinjaman"));
        tgl_pinjaman.setCellValueFactory(new PropertyValueFactory<>("TglPinjaman"));
        jml_pinjaman.setCellValueFactory(new PropertyValueFactory<>("JmlPinjaman"));
        bunga.setCellValueFactory(new PropertyValueFactory<>("Bunga"));
        total_pinjaman.setCellValueFactory(new PropertyValueFactory<>("TotalPinjaman"));
        lama_pinjaman.setCellValueFactory(new PropertyValueFactory<>("LamaPinjaman"));
        angsuran.setCellValueFactory(new PropertyValueFactory<>("Angsuran"));
        sisa_pinjaman.setCellValueFactory(new PropertyValueFactory<>("SisaPinjaman"));
        no_anggota.setCellValueFactory(new PropertyValueFactory<>("NoAnggota"));
        status_pinjaman.setCellValueFactory(new PropertyValueFactory<>("StatusPinjaman"));
    }

    long maks = 0;
    @FXML
    private void ActionNoAnggota(ActionEvent event) {
        if(cb_noAnggota.getSelectionModel().getSelectedItem()==null)
        {
            txt_tgl_pinjaman.setDisable(true);
            txt_tgl_pinjaman.setValue(null);
        }
        else
        {            
            try
            {
                Class.forName(driver);
                Connection kon = DriverManager.getConnection(database,user,pass);
                Statement stt = kon.createStatement();
                String SQL = "SELECT nama, 2*(saldo_wajib+saldo_sukarela) "
                           + "FROM anggota "
                           + "WHERE no_anggota = '"+cb_noAnggota.getSelectionModel().getSelectedItem()+"';";
                ResultSet res = stt.executeQuery(SQL);
                while(res.next())
                {
                    txt_nama_anggota.setText(res.getString(1));                
                    maks = Long.valueOf(res.getString(2));
                }
                res.close();
                stt.close();
                kon.close();
                txt_tgl_pinjaman.setDisable(false);
                limit.setText(String.valueOf(maks));
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
    private void tgl_pinjamanOnAction(ActionEvent event) {
        if(txt_tgl_pinjaman.getValue()==null)
        {
            txt_jml_pinjaman.setDisable(true);
            txt_jml_pinjaman.setText("");
        }
        else
        {
            txt_jml_pinjaman.setDisable(false);
        }
    }

    @FXML
    private void jml_pinjamanOnRelease(KeyEvent event) {
        if(txt_jml_pinjaman.getText().isEmpty())
        {
            cb_lamaPinjaman.setDisable(true);
            cb_lamaPinjaman.getSelectionModel().select(-1);
        }
        else
        {
            cb_lamaPinjaman.setDisable(false);
//            RequiredFieldValidator validator = new RequiredFieldValidator();
//            validator.setMessage("Limit "+maks);
//            txt_jml_pinjaman.getValidators().add(validator);
//            txt_jml_pinjaman.focusedProperty().addListener(new ChangeListener<Boolean>() {
//                @Override
//                public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
//                    if(!newValue)
//                    {
//                        txt_jml_pinjaman.validate();
//                    }
//                }
//            });
            if(Long.valueOf(txt_jml_pinjaman.getText())>maks)
            {
                txt_jml_pinjaman.setStyle("-fx-text-fill: red;");
            }
            else
            {
                txt_jml_pinjaman.setStyle("-fx-text-fill: black;");
            }
        }
    }
    
    public static class Pinjaman {
        private final SimpleStringProperty NoPinjaman;
        private final SimpleStringProperty TglPinjaman;
        private final SimpleStringProperty JmlPinjaman;
        private final SimpleStringProperty Bunga;
        private final SimpleStringProperty TotalPinjaman;
        private final SimpleStringProperty LamaPinjaman;
        private final SimpleStringProperty Angsuran;
        private final SimpleStringProperty SisaPinjaman;
        private final SimpleStringProperty NoAnggota;
        private final SimpleStringProperty StatusPinjaman;
        
        public Pinjaman(String NoPinjaman, String TglPinjaman, String JmlPinjaman, String Bunga, String TotalPinjaman, 
                String LamaPinjaman, String Angsuran, String SisaPinjaman, String NoAnggota, String StatusPinjaman)
        {
            this.NoPinjaman = new SimpleStringProperty(NoPinjaman);
            this.TglPinjaman = new SimpleStringProperty(TglPinjaman);
            this.JmlPinjaman = new SimpleStringProperty(JmlPinjaman);
            this.Bunga = new SimpleStringProperty(Bunga);
            this.TotalPinjaman = new SimpleStringProperty(TotalPinjaman);
            this.LamaPinjaman = new SimpleStringProperty(LamaPinjaman);
            this.Angsuran = new SimpleStringProperty(Angsuran);
            this.SisaPinjaman = new SimpleStringProperty(SisaPinjaman);
            this.NoAnggota = new SimpleStringProperty(NoAnggota);
            this.StatusPinjaman = new SimpleStringProperty(StatusPinjaman);
        }

        public String getNoPinjaman() {
            return NoPinjaman.get();
        }

        public String getTglPinjaman() {
            return TglPinjaman.get();
        }

        public String getJmlPinjaman() {
            return JmlPinjaman.get();
        }

        public String getBunga() {
            return Bunga.get();
        }

        public String getTotalPinjaman() {
            return TotalPinjaman.get();
        }

        public String getLamaPinjaman() {
            return LamaPinjaman.get();
        }

        public String getAngsuran() {
            return Angsuran.get();
        }

        public String getSisaPinjaman() {
            return SisaPinjaman.get();
        }

        public String getNoAnggota() {
            return NoAnggota.get();
        }

        public String getStatusPinjaman() {
            return StatusPinjaman.get();
        }
        
    }
    
    private void clean_text()
    {
        cb_noAnggota.getSelectionModel().select(-1);
        txt_nama_anggota.setText("");
        txt_tgl_pinjaman.setValue(null);
        txt_jml_pinjaman.setText("");
        cb_lamaPinjaman.getSelectionModel().select(-1);
    }

    @FXML
    private void TambahData(ActionEvent event) {
        String countWajib = null;
        String countSukarela = null;
        String countStatus = null;
        try
        {
            Class.forName(driver);
            Connection kon = DriverManager.getConnection(database,user,pass);
            Statement stt = kon.createStatement();
            String SQL = "SELECT COUNT(*) "
                       + "FROM simpanan "
                       + "WHERE aktif='1' AND jenis_simpanan='WAJIB' AND no_anggota='"+cb_noAnggota.getSelectionModel().getSelectedItem()+"';";
            ResultSet res = stt.executeQuery(SQL);
            while(res.next())
            {
                countWajib = res.getString(1);
            }
            String SQL1 = "SELECT COUNT(*) "
                        + "FROM simpanan "
                        + "WHERE aktif='1' AND jenis_simpanan='SUKARELA' AND no_anggota='"+cb_noAnggota.getSelectionModel().getSelectedItem()+"';";
            ResultSet rs = stt.executeQuery(SQL1);
            while(rs.next())
            {
                countSukarela = rs.getString(1);
            }
            String SQL2 = "SELECT COUNT(*) "
                        + "FROM pinjaman "
                        + "WHERE aktif = '1' AND status_pinjaman = 'BELUM LUNAS' AND no_anggota='"+cb_noAnggota.getSelectionModel().getSelectedItem()+"';";
            ResultSet r = stt.executeQuery(SQL2);
            while(r.next())
            {
                countStatus = r.getString(1);
            }
            r.close();
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
        if(Integer.valueOf(countWajib)<3||Integer.valueOf(countSukarela)<1)
        {
            Notifications.create()
                         .title("Information")
                         .text("No Anggota yang dipilih belum memiliki simpanan wajib selama 3 bulan dan simpanan sukarela!")
                         .position(Pos.BOTTOM_RIGHT)
                         .showInformation();
        }
        else if(Integer.valueOf(countStatus)!=0)
        {
            Notifications.create()
                         .title("Information")
                         .text("No Anggota yang dipilih tidak dapat meminjam, pinjaman sebelumnya belum lunas!")
                         .position(Pos.BOTTOM_RIGHT)
                         .showInformation();
        }
        else if(txt_tgl_pinjaman.getValue()==null
                ||txt_jml_pinjaman.getText().isEmpty()
                ||txt_jml_pinjaman.getText().equals("0"))
        {
            Notifications.create()
                         .title("Information")
                         .text("Tanggal Pinjaman Dan Jumlah Pinjaman Tidak Boleh Kosong atau nol Silahkan Dilengkapi.")
                         .position(Pos.BOTTOM_RIGHT)
                         .showInformation();
        }
        else if(Long.valueOf(txt_jml_pinjaman.getText())>maks)
        {
            Notifications.create()
                         .title("Information")
                         .text("Jumlah pinjaman anda melebihi yang sudah di tentukan!")
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
                long bunga = (long) (0.05 * Long.valueOf(txt_jml_pinjaman.getText()));
                long total_pinjaman = (long) (bunga + Long.valueOf(txt_jml_pinjaman.getText()));
                long angsuran = (long) (total_pinjaman / Integer.valueOf(cb_lamaPinjaman.getSelectionModel().getSelectedItem()));
                try
                {
                    Class.forName(driver);
                    Connection kon = DriverManager.getConnection(database,
                                                                 user,
                                                                 pass);
                    Statement stt = kon.createStatement();                    
                    String SQL = "INSERT INTO pinjaman " 
                               + "VALUES "+"( '"+cb_noAnggota.getSelectionModel().getSelectedItem()+txt_tgl_pinjaman.getValue().format(DateTimeFormatter.ofPattern("ddMMyyyy"))+"',"
                                          + " '"+txt_tgl_pinjaman.getValue()+"',"
                                          + " '"+txt_jml_pinjaman.getText()+"',"
                                          + " '"+bunga+"',"
                                          + " '"+total_pinjaman+"',"
                                          + " '"+cb_lamaPinjaman.getSelectionModel().getSelectedItem()+"',"
                                          + " '"+angsuran+"',"
                                          + " '"+total_pinjaman+"',"
                                          + " '"+cb_noAnggota.getSelectionModel().getSelectedItem()+"',"
                                          + " 'BELUM LUNAS',"
                                          + " '1')";
                    stt.executeUpdate(SQL);
                    TablePinjaman.getItems().add(new Pinjaman(cb_noAnggota.getSelectionModel().getSelectedItem()+txt_tgl_pinjaman.getValue().format(DateTimeFormatter.ofPattern("ddMMyyyy")), 
                                                              txt_tgl_pinjaman.getValue().toString(), 
                                                              txt_jml_pinjaman.getText(), 
                                                              String.valueOf(bunga), 
                                                              String.valueOf(total_pinjaman), 
                                                              cb_lamaPinjaman.getSelectionModel().getSelectedItem(), 
                                                              String.valueOf(angsuran), 
                                                              String.valueOf(total_pinjaman), 
                                                              cb_noAnggota.getSelectionModel().getSelectedItem(), 
                                                              "BELUM LUNAS"));
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

    @FXML
    private void UbahData(ActionEvent event) {
        int index = TablePinjaman.getSelectionModel().getSelectedIndex();
        if (index >= 0)
        {
            if(!(cb_noAnggota.getSelectionModel().getSelectedItem().equals(TablePinjaman.getSelectionModel().getSelectedItem().getNoAnggota())
                    &&txt_tgl_pinjaman.getValue().toString().equals(TablePinjaman.getSelectionModel().getSelectedItem().getTglPinjaman())))
            {
                Notifications.create()
                             .title("Information")
                             .text("No Anggota dan Tanggal Pinjaman tidak boleh di ubah!")
                             .position(Pos.BOTTOM_RIGHT)
                             .showInformation();
            }
            else if(!TablePinjaman.getSelectionModel().getSelectedItem().getTotalPinjaman().equals(TablePinjaman.getSelectionModel().getSelectedItem().getSisaPinjaman()))
            {
                Notifications.create()
                             .title("Information")
                             .text("Data tidak dapat di ubah! Angsuran sudah berjalan.")
                             .position(Pos.BOTTOM_RIGHT)
                             .showInformation();
            }
            else if(Long.valueOf(txt_jml_pinjaman.getText())>maks)
            {
                Notifications.create()
                             .title("Information")
                             .text("Jumlah pinjaman anda melebihi yang sudah di tentukan!")
                             .position(Pos.BOTTOM_RIGHT)
                             .showInformation();
                txt_jml_pinjaman.requestFocus();
            }
            else if(txt_jml_pinjaman.getText().equals("0"))
            {
                Notifications.create()
                             .title("Information")
                             .text("Jumlah pinjaman tidak boleh bernilai nol!")
                             .position(Pos.BOTTOM_RIGHT)
                             .showInformation();
            }
            else
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
                    String JmlPinjamanBaru = txt_jml_pinjaman.getText();
                    String LamaPinjamanBaru = cb_lamaPinjaman.getSelectionModel().getSelectedItem();
                    long bunga = (long) (0.05 * Long.valueOf(txt_jml_pinjaman.getText()));
                    long total_pinjaman = (long) (bunga + Long.valueOf(txt_jml_pinjaman.getText()));
                    long angsuran = (long) (total_pinjaman / Integer.valueOf(cb_lamaPinjaman.getSelectionModel().getSelectedItem()));
                    
                    //data lama
                    String noPinjaman = TablePinjaman.getSelectionModel().getSelectedItem().getNoPinjaman();
                    String tglPinjaman = TablePinjaman.getSelectionModel().getSelectedItem().getTglPinjaman();
                    String jmlPinjamn = TablePinjaman.getSelectionModel().getSelectedItem().getJmlPinjaman();
                    String BungaLama = TablePinjaman.getSelectionModel().getSelectedItem().getBunga();
                    String totalPinjam = TablePinjaman.getSelectionModel().getSelectedItem().getTotalPinjaman();
                    String lamaPinjaman = TablePinjaman.getSelectionModel().getSelectedItem().getLamaPinjaman();
                    String AngsuranLama = TablePinjaman.getSelectionModel().getSelectedItem().getAngsuran();
                    String sisaPinjaman = TablePinjaman.getSelectionModel().getSelectedItem().getSisaPinjaman();
                    String noAnggota = TablePinjaman.getSelectionModel().getSelectedItem().getNoAnggota();
                    String statusPinjaman = TablePinjaman.getSelectionModel().getSelectedItem().getStatusPinjaman();
                    
                    try
                    {
                        Class.forName(driver);
                        Connection kon = DriverManager.getConnection(database, user, pass);
                        Statement stt = kon.createStatement();
                        String SQL  = "UPDATE pinjaman "
                                    + "SET "
                                    + "jumlah_pinjaman = REPLACE(jumlah_pinjaman,'"+jmlPinjamn+"','"+JmlPinjamanBaru+"'),"
                                    + "bunga = REPLACE(bunga,'"+BungaLama+"','"+bunga+"'),"
                                    + "total_pinjaman = REPLACE(total_pinjaman,'"+totalPinjam+"','"+total_pinjaman+"'),"
                                    + "lama_pinjaman = REPLACE(lama_pinjaman,'"+lamaPinjaman+"','"+LamaPinjamanBaru+"'),"
                                    + "angsuran = REPLACE(angsuran,'"+AngsuranLama+"','"+angsuran+"'),"
                                    + "sisa_pinjaman = REPLACE(sisa_pinjaman,'"+sisaPinjaman+"','"+total_pinjaman+"') "
                                    + "WHERE "
                                    + "no_pinjaman = '"+noPinjaman+"';";
                        stt.executeUpdate(SQL);
                        TablePinjaman.getItems().remove(index);
                        TablePinjaman.getItems().add(new Pinjaman(noPinjaman, 
                                                              tglPinjaman, 
                                                              JmlPinjamanBaru, 
                                                              String.valueOf(bunga), 
                                                              String.valueOf(total_pinjaman), 
                                                              LamaPinjamanBaru, 
                                                              String.valueOf(angsuran), 
                                                              String.valueOf(total_pinjaman), 
                                                              noAnggota, 
                                                              statusPinjaman));
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
        }
        else
        {
            if (TablePinjaman.getItems().size()==0)
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
        int index = TablePinjaman.getSelectionModel().getSelectedIndex();
        if (index >= 0)
        {
            if(TablePinjaman.getSelectionModel().getSelectedItem().getStatusPinjaman().equals("SUDAH LUNAS"))
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
                        String ST  = "START TRANSACTION;";
                        String SQL = "UPDATE pinjaman "
                                    + "SET aktif = REPLACE(aktif,'1','0') "
                                    + "WHERE no_pinjaman='"+TablePinjaman.getSelectionModel().getSelectedItem().getNoPinjaman()+"'; ";
                        String SQL1 = "UPDATE angsuran "
                                    + "SET aktif = REPLACE(aktif,'1','0') "
                                    + "WHERE no_pinjaman='"+TablePinjaman.getSelectionModel().getSelectedItem().getNoPinjaman()+"'; ";
                        String C = "COMMIT; ";
                        stt.executeQuery(ST);
                        stt.executeUpdate(SQL);
                        stt.executeUpdate(SQL1);
                        TablePinjaman.getItems().remove(index);
                        stt.executeQuery(C);
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
                Notifications.create()
                             .title("Information")
                             .text("Data tidak dapat di hapus sebelum peminjaman sudah lunas!")
                             .position(Pos.BOTTOM_RIGHT)
                             .showInformation();
            }
        }
        else
        {
            if (TablePinjaman.getItems().size()==0)
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
    
}
