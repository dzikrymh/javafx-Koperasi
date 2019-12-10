/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.koperasi.control;

import com.database.koneksi;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.Initializable;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXDatePicker;
import com.jfoenix.controls.JFXTextField;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.time.LocalDate;
import java.time.Month;
import java.time.format.DateTimeFormatter;
import java.util.Optional;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
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
public class AnggotaController implements Initializable {

    @FXML
    private JFXTextField txt_nama;
    @FXML
    private JFXDatePicker txt_tgl_masuk;
    @FXML
    private JFXTextField txt_rt;
    @FXML
    private JFXTextField txt_hp;
    @FXML
    private JFXButton btn_tambah;
    @FXML
    private JFXButton btn_ubah;
    @FXML
    private JFXButton btn_hapus;
    @FXML
    private TableColumn<Anggota, String> no_anggota;
    @FXML
    private TableColumn<Anggota, String> nama;
    @FXML
    private TableColumn<Anggota, String> tgl_masuk;
    @FXML
    private TableColumn<Anggota, String> hp;
    @FXML
    private TableColumn<Anggota, String> rt;
    @FXML
    private TableColumn<Anggota, String> sp;
    @FXML
    private TableColumn<Anggota, String> sw;
    @FXML
    private TableColumn<Anggota, String> ss;
    @FXML
    private JFXTextField txt_filter;
    @FXML
    private TableView<Anggota> TableAnggota;
    //Deklarasi Variable
    koneksi dbsetting;
    String driver,database,user,pass;
    ObservableList<Anggota> list = FXCollections.observableArrayList();
    Alert error = new Alert(Alert.AlertType.ERROR);
    Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION);
            
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
        
        // force the field to be numeric only
        txt_rt.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                if (!newValue.matches("\\d*")) {
                    txt_rt.setText(newValue.replaceAll("[^\\d]", ""));
                }
            }
        });
        txt_hp.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                if (!newValue.matches("\\d*")) {
                    txt_hp.setText(newValue.replaceAll("[^\\d]", ""));
                }
            }
        });
        
        //membatasi panjang karaktercdengan memanggil fungsi
        txt_rt.addEventFilter(KeyEvent.KEY_TYPED, maxLength(2));
        txt_hp.addEventFilter(KeyEvent.KEY_TYPED, maxLength(15));
        txt_nama.addEventFilter(KeyEvent.KEY_TYPED, maxLength(30));
        
        //FilterData
        ObservableList data =  TableAnggota.getItems();
        txt_filter.textProperty().addListener((ObservableValue<? extends String> observable, String oldValue, String newValue) -> {
            if (oldValue != null && (newValue.length() < oldValue.length())) {
                TableAnggota.setItems(data);
            }
            String value = newValue.toLowerCase();
            ObservableList<Anggota> subentries = FXCollections.observableArrayList();

            long count = TableAnggota.getColumns().stream().count();
            for (int i = 0; i < TableAnggota.getItems().size(); i++) {
                for (int j = 0; j < count; j++) {
                    String entry = "" + TableAnggota.getColumns().get(j).getCellData(i);
                    if (entry.toLowerCase().contains(value)) {
                        subentries.add(TableAnggota.getItems().get(i));
                        break;
                    }
                }
            }
            TableAnggota.setItems(subentries);
        });
        
        //table click
        TableAnggota.addEventHandler(MouseEvent.MOUSE_CLICKED, e -> {
            tampilfield();
        });
    }

    //fungsi membatasi karakter
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
        txt_tgl_masuk.setConverter(new StringConverter<LocalDate>() {
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
    
    private void tampilfield()
    {
        txt_nama.setText(TableAnggota.getSelectionModel().getSelectedItem().getNama());
        int tahun = Integer.valueOf(TableAnggota.getSelectionModel().getSelectedItem().getTglMasuk().substring(0, 4));
        int hari = Integer.valueOf(TableAnggota.getSelectionModel().getSelectedItem().getTglMasuk().substring(8, 10));
        int bulan = Integer.valueOf(TableAnggota.getSelectionModel().getSelectedItem().getTglMasuk().substring(5, 7));
        txt_tgl_masuk.setValue(LocalDate.of(tahun, Month.of(bulan), hari));
        txt_rt.setText(TableAnggota.getSelectionModel().getSelectedItem().getRT());
        txt_hp.setText(TableAnggota.getSelectionModel().getSelectedItem().getHP());
    }
    
    private void loadData()
    {
        try
        {
            Class.forName(driver);
            Connection kon = DriverManager.getConnection(database, user, pass);
            Statement stt = kon.createStatement();
            String SQL  = "SELECT no_anggota, nama, tgl_masuk, handphone, rt, saldo_pokok, saldo_wajib, saldo_sukarela "
                        + "FROM anggota "
                        + "WHERE aktif = '1'";
            ResultSet res = stt.executeQuery(SQL);
            while(res.next())
            {
                String no_anggota1 = res.getString(1);
                String nama1 = res.getString(2);
                String tgl_masuk1 = res.getString(3);
                String hp1 = res.getString(4);
                String rt1 = res.getString(5);
                String sp1 = res.getString(6);
                String sw1 = res.getString(7);
                String ss1 = res.getString(8);
                
                list.add(new Anggota(no_anggota1, nama1, tgl_masuk1, hp1, rt1, sp1, sw1, ss1));
            }
            TableAnggota.getItems().setAll(list);
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
        no_anggota.setCellValueFactory(new PropertyValueFactory<>("NoAnggota"));
        nama.setCellValueFactory(new PropertyValueFactory<>("Nama"));
        tgl_masuk.setCellValueFactory(new PropertyValueFactory<>("TglMasuk"));
        hp.setCellValueFactory(new PropertyValueFactory<>("HP"));
        rt.setCellValueFactory(new PropertyValueFactory<>("RT"));
        sp.setCellValueFactory(new PropertyValueFactory<>("SP"));
        sw.setCellValueFactory(new PropertyValueFactory<>("SW"));
        ss.setCellValueFactory(new PropertyValueFactory<>("SS"));
    }

    private void cleantext()
    {
        txt_nama.setText("");
        txt_tgl_masuk.setValue(null);
        txt_rt.setText("");
        txt_hp.setText("");
    }
    
    @FXML
    private void TambahData(ActionEvent event) {
        if(txt_nama.getText().isEmpty()
                ||txt_hp.getText().isEmpty()
                ||txt_rt.getText().isEmpty()
                ||txt_tgl_masuk.getValue()==null)
        {
            Notifications.create()
                         .title("Information")
                         .text("Data tidak boleh kosong, silahkan di perbaiki.")
                         .position(Pos.BOTTOM_RIGHT)
                         .showInformation();
            txt_nama.requestFocus();
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
                    String SQLID = "SELECT COUNT(no_anggota)+1 "
                                 + "FROM anggota;";
                    ResultSet res = stt.executeQuery(SQLID);
                    String ID = null;
                    while(res.next())
                    {
                        ID = res.getString(1);
                    }
                    String SQL = "INSERT INTO anggota " 
                               + "VALUES "+"( '"+ID+"',"
                                          + " '"+txt_nama.getText().toUpperCase()+"',"
                                          + " '"+txt_tgl_masuk.getValue()+"',"
                                          + " '"+txt_hp.getText()+"',"
                                          + " '"+txt_rt.getText()+"',"
                                          + " '35000',"
                                          + " '0',"
                                          + " '0',"
                                          + " '1');";
                    DateTimeFormatter kode_simpanan = DateTimeFormatter.ofPattern("ddMMyyyy");
                    String SQL1 = "INSERT INTO simpanan "
                                + "VALUES " + "( 'SP"+ID+kode_simpanan.format(txt_tgl_masuk.getValue())+"',"
                                            + " '"+txt_tgl_masuk.getValue()+"',"
                                            + " 'POKOK',"
                                            + " '35000',"
                                            + " '"+ID+"',"
                                            + " '1');";
                    String SQL2 = "INSERT INTO login "
                                + "VALUES " + "( '"+txt_nama.getText().toUpperCase()+ID+"',"
                                            + " '"+txt_hp.getText()+"',"
                                            + " '"+ID+"');";
                    String SQLcommit = "COMMIT";
                    stt.executeUpdate(SQL);
                    stt.executeUpdate(SQL1);
                    stt.executeUpdate(SQL2);
                    TableAnggota.getItems().add(new Anggota(ID, 
                                         txt_nama.getText().toUpperCase(), 
                                         txt_tgl_masuk.getValue().toString(), 
                                         txt_hp.getText(), 
                                         txt_rt.getText(), 
                                         "35000", 
                                         "0", 
                                         "0"));
                    stt.executeQuery(SQLcommit);
                    res.close();
                    stt.close();
                    kon.close();
                    Notifications.create()
                                 .title("Information")
                                 .text("Data Berhasil Di Tambahkan.")
                                 .position(Pos.BOTTOM_RIGHT)
                                 .showInformation();
                    cleantext();
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
        int index = TableAnggota.getSelectionModel().getSelectedIndex();
        if (index >= 0)
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
                String i = TableAnggota.getSelectionModel().getSelectedItem().getNoAnggota();
                String n = TableAnggota.getSelectionModel().getSelectedItem().getNama();
                String t = TableAnggota.getSelectionModel().getSelectedItem().getTglMasuk();
                String h = TableAnggota.getSelectionModel().getSelectedItem().getHP();
                String r = TableAnggota.getSelectionModel().getSelectedItem().getRT();
                String p = TableAnggota.getSelectionModel().getSelectedItem().getSP();
                String w = TableAnggota.getSelectionModel().getSelectedItem().getSW();
                String s = TableAnggota.getSelectionModel().getSelectedItem().getSS();

                //data baru
                String nami = txt_nama.getText().toUpperCase();
                String t_masuk = txt_tgl_masuk.getValue().toString();
                String hape = txt_hp.getText();
                String erte = txt_rt.getText();
                
                try
                {
                    Class.forName(driver);
                    Connection kon = DriverManager.getConnection(database, user, pass);
                    Statement stt = kon.createStatement();
                    String SQLstrat = "START TRANSACTION;";
                    stt.executeQuery(SQLstrat);
                    String SQL = "UPDATE anggota " 
                               + "SET "
                               + "nama=REPLACE(nama,'"+n+"','"+nami+"'),"
                               + "tgl_masuk=REPLACE(tgl_masuk,'"+t+"','"+t_masuk+"'),"
                               + "handphone=REPLACE(handphone,'"+h+"','"+hape+"'),"
                               + "rt=REPLACE(rt,'"+r+"','"+erte+"') "
                               + "WHERE "
                               + "no_anggota='"+i+"';";
                    stt.executeUpdate(SQL);
                    TableAnggota.getItems().remove(index);
                    TableAnggota.getItems().add(new Anggota(i, 
                                         nami, 
                                         t_masuk, 
                                         hape, 
                                         erte, 
                                         p, 
                                         w, 
                                         s));
                    String SQLcommit = "COMMIT;";
                    stt.executeQuery(SQLcommit);
                    stt.close();
                    kon.close();
                    Notifications.create()
                                 .title("Information")
                                 .text("Data Berhasil Diubah.")
                                 .position(Pos.BOTTOM_RIGHT)
                                 .showInformation();
                    cleantext();
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
            if (TableAnggota.getItems().size()==0)
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
        int index = TableAnggota.getSelectionModel().getSelectedIndex();
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
                int count=0;
                String noAnggota = TableAnggota.getSelectionModel().getSelectedItem().getNoAnggota();
                try
                {
                    Class.forName(driver);
                    Connection kon = DriverManager.getConnection(database, user, pass);
                    Statement stt = kon.createStatement();
                    String SQL  = "SELECT COUNT(status_pinjaman) "
                                + "FROM pinjaman "
                                + "WHERE status_pinjaman = 'BELUM LUNAS' "
                                    + "AND no_anggota = '"+noAnggota+"';";
                    ResultSet res = stt.executeQuery(SQL);
                    while(res.next())
                    {
                        count = Integer.parseInt(res.getString(1));
                    }
                    if(count!=0)
                    {
                        Notifications.create()
                                 .title("Information")
                                 .text("Maaf data yang anda pilih tidak dapat dihapus dikarenakan masih memiliki pinjaman BELUM LUNAS.")
                                 .position(Pos.BOTTOM_RIGHT)
                                 .showInformation();
                    }
                    else
                    {
                        String SQLstart = "START TRANSACTION;";
                        stt.executeQuery(SQLstart);
                        String SQL1 = "UPDATE anggota "
                                    + "SET "
                                    + "aktif = REPLACE(aktif,'1','0') "
                                    + "WHERE no_anggota='"+noAnggota+"';";
                        String SQL2 = "UPDATE simpanan "
                                    + "SET "
                                    + "aktif = REPLACE(aktif,'1','0') "
                                    + "WHERE no_anggota='"+noAnggota+"';";
                        String SQL3 = "UPDATE pinjaman "
                                    + "SET "
                                    + "aktif = REPLACE(aktif,'1','0') "
                                    + "WHERE no_anggota='"+noAnggota+"';";
                        stt.executeUpdate(SQL1);
                        stt.executeUpdate(SQL2);
                        stt.executeUpdate(SQL3);
                        String query = "SELECT no_pinjaman "
                                     + "FROM pinjaman "
                                     + "WHERE no_anggota = '"+noAnggota+"';";
                        ResultSet rees = stt.executeQuery(query);
                        while(rees.next())
                        {
                            Statement st = kon.createStatement();
                            String SQL4 = "UPDATE angsuran "
                                        + "SET "
                                        + "aktif = REPLACE(aktif,'1','0') "
                                        + "WHERE no_pinjaman ='"+res.getString(1)+"';";
                            st.executeUpdate(SQL4);
                            st.close();
                        }
                        TableAnggota.getItems().remove(index);
                        String SQLcommit = "COMMIT";
                        stt.executeQuery(SQLcommit);
                        Notifications.create()
                                 .title("Information")
                                 .text("Data Berhasil Dihapus.")
                                 .position(Pos.BOTTOM_RIGHT)
                                 .showInformation();
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
        }
        else
        {
            if (TableAnggota.getItems().size()==0)
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
    
    public static class Anggota {
        private final SimpleStringProperty NoAnggota;
        private final SimpleStringProperty Nama;
        private final SimpleStringProperty TglMasuk;
        private final SimpleStringProperty HP;
        private final SimpleStringProperty RT;
        private final SimpleStringProperty SP;
        private final SimpleStringProperty SW;
        private final SimpleStringProperty SS;
        
        public Anggota(String NoAnggota, String Nama, String TglMasuk, String HP, String RT, String SP, String SW, String SS)
        {
            this.NoAnggota = new SimpleStringProperty(NoAnggota);
            this.Nama = new SimpleStringProperty(Nama);
            this.TglMasuk = new SimpleStringProperty(TglMasuk);
            this.HP = new SimpleStringProperty(HP);
            this.RT = new SimpleStringProperty(RT);
            this.SP = new SimpleStringProperty(SP);
            this.SW = new SimpleStringProperty(SW);
            this.SS = new SimpleStringProperty(SS);
        }

        public String getNoAnggota() {
            return NoAnggota.get();
        }

        public String getNama() {
            return Nama.get();
        }

        public String getTglMasuk() {
            return TglMasuk.get();
        }

        public String getHP() {
            return HP.get();
        }

        public String getRT() {
            return RT.get();
        }

        public String getSP() {
            return SP.get();
        }

        public String getSW() {
            return SW.get();
        }

        public String getSS() {
            return SS.get();
        }
    }
    
}
