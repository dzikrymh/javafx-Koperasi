/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.koperasi.control;

import com.database.koneksi;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXDatePicker;
import java.net.URL;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ResourceBundle;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.print.PrinterJob;
import javafx.scene.control.Alert;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.util.StringConverter;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.design.JRDesignQuery;
import net.sf.jasperreports.engine.design.JasperDesign;
import net.sf.jasperreports.engine.xml.JRXmlLoader;
import net.sf.jasperreports.view.JasperViewer;
import org.controlsfx.control.Notifications;

/**
 * FXML Controller class
 *
 * @author dzikry's
 */
public class LaporanController implements Initializable {

    //Deklarasi Variable
    koneksi dbsetting;
    String driver,database,user,pass;
    ObservableList<Anggota> listanggota = FXCollections.observableArrayList();
    ObservableList<Simpanan> listsimpanan = FXCollections.observableArrayList();
    ObservableList<Pinjaman> listpinjaman = FXCollections.observableArrayList();
    ObservableList<Angsuran> listangsuran = FXCollections.observableArrayList();
    Alert error = new Alert(Alert.AlertType.ERROR);
    Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION);
    
    @FXML
    private TableView<Anggota> TableAnggota;
    @FXML
    private TableColumn<Anggota, String> no_anggota1;
    @FXML
    private TableColumn<Anggota, String> nama1;
    @FXML
    private TableColumn<Anggota, String> tgl_masuk1;
    @FXML
    private TableColumn<Anggota, String> hp1;
    @FXML
    private TableColumn<Anggota, String> rt1;
    @FXML
    private TableColumn<Anggota, String> sp1;
    @FXML
    private TableColumn<Anggota, String> sw1;
    @FXML
    private TableColumn<Anggota, String> ss1;
    @FXML
    private TableColumn<Anggota, String> aktif1;
    @FXML
    private JFXButton cetakAnggota;
    @FXML
    private JFXDatePicker startDateAnggota;
    @FXML
    private JFXDatePicker endDateAnggota;
    @FXML
    private TableView<Simpanan> TableSimpanan;
    @FXML
    private TableColumn<Simpanan, String> no_simpanan2;
    @FXML
    private TableColumn<Simpanan, String> tgl_simpanan2;
    @FXML
    private TableColumn<Simpanan, String> jns_simpanan2;
    @FXML
    private TableColumn<Simpanan, String> jml_simpanan2;
    @FXML
    private TableColumn<Simpanan, String> no_anggota2;
    @FXML
    private TableColumn<Simpanan, String> aktif2;
    @FXML
    private JFXDatePicker startDateSimpanan;
    @FXML
    private JFXDatePicker endDateSimpanan;
    @FXML
    private JFXButton cetakSimpanan;
    @FXML
    private JFXButton cetakPinjaman;
    @FXML
    private JFXDatePicker startDatePinjaman;
    @FXML
    private JFXDatePicker endDatePinjaman;
    @FXML
    private TableView<Pinjaman> TablePinjaman;
    @FXML
    private TableColumn<Pinjaman, String> no_pinjaman3;
    @FXML
    private TableColumn<Pinjaman, String> tgl_pinjaman3;
    @FXML
    private TableColumn<Pinjaman, String> jml_pinjaman3;
    @FXML
    private TableColumn<Pinjaman, String> bunga3;
    @FXML
    private TableColumn<Pinjaman, String> total_pinjaman3;
    @FXML
    private TableColumn<Pinjaman, String> lama_pinjaman3;
    @FXML
    private TableColumn<Pinjaman, String> angsuran3;
    @FXML
    private TableColumn<Pinjaman, String> sisa_pinjaman3;
    @FXML
    private TableColumn<Pinjaman, String> no_anggota3;
    @FXML
    private TableColumn<Pinjaman, String> status_pinjaman3;
    @FXML
    private TableColumn<Pinjaman, String> aktif3;
    @FXML
    private JFXButton cetakAngsuran;
    @FXML
    private JFXDatePicker startDateAngsuran;
    @FXML
    private JFXDatePicker endDateAngsuran;
    @FXML
    private TableView<Angsuran> TableAngsuran;
    @FXML
    private TableColumn<Angsuran, String> no_angsuran4;
    @FXML
    private TableColumn<Angsuran, String> tgl_bayar4;
    @FXML
    private TableColumn<Angsuran, String> no_pinjaman4;
    @FXML
    private TableColumn<Angsuran, String> aktif4;
    @FXML
    private Tab tabAnggota;
    @FXML
    private Tab tabSimpanan;
    @FXML
    private Tab tabPinjaman;
    @FXML
    private Tab tabAngsuran;
    @FXML
    private TabPane TabPane;
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
        initColAnggota();
        loadDataAnggota();
        initColSimpanan();
        loadDataSimpanan();
        initColPinjaman();
        loadDataPinjaman();
        initColAngsuran();
        loadDataAngsuran();
        
        setTanggal(startDateAnggota);
        setTanggal(endDateAnggota);
        setTanggal(startDateSimpanan);
        setTanggal(endDateSimpanan);
        setTanggal(startDatePinjaman);
        setTanggal(endDatePinjaman);
        setTanggal(startDateAngsuran);
        setTanggal(endDateAngsuran);
        
        cetakAnggota.addEventHandler(MouseEvent.MOUSE_CLICKED, e -> {
            if(startDateAnggota.getValue()!=null&&endDateAnggota.getValue()!=null)
            {
                String lokasi = "src/com/koperasi/laporan/anggota.jrxml";
                String table = "anggota";
                String field = "tgl_masuk";
                cetak(lokasi, table, field, startDateAnggota.getValue().toString(), endDateAnggota.getValue().toString());
            }else if(startDateAnggota.getValue()==null&&endDateAnggota.getValue()==null)
            {
                String lokasi1 = "src/com/koperasi/laporan/anggota.jrxml";
                String table1 = "anggota";
                cetakfull(lokasi1, table1);
            }
            else
            {
                Notifications.create()
                             .title("Information")
                             .text("Filter tanggal tidak boleh di isi hanya satu tanggal. Silahkan diperbaiki.")
                             .position(Pos.BOTTOM_RIGHT)
                             .showInformation();
            }            
        });
        cetakSimpanan.addEventHandler(MouseEvent.MOUSE_CLICKED, e -> {
            if(startDateSimpanan.getValue()!=null&&endDateSimpanan.getValue()!=null)
            {
                String lokasi = "src/com/koperasi/laporan/simpanan.jrxml";
                String table = "simpanan";
                String field = "tgl_simpanan";
                cetak(lokasi, table, field, startDateSimpanan.getValue().toString(), endDateSimpanan.getValue().toString());
            }else if(startDateSimpanan.getValue()==null&&endDateSimpanan.getValue()==null)
            {
                String lokasi1 = "src/com/koperasi/laporan/simpanan.jrxml";
                String table1 = "simpanan";
                cetakfull(lokasi1, table1);
            }
            else
            {
                Notifications.create()
                             .title("Information")
                             .text("Filter tanggal tidak boleh di isi hanya satu tanggal. Silahkan diperbaiki.")
                             .position(Pos.BOTTOM_RIGHT)
                             .showInformation();
            }
        });
        cetakPinjaman.addEventHandler(MouseEvent.MOUSE_CLICKED, e -> {
            if(startDatePinjaman.getValue()!=null&&endDatePinjaman.getValue()!=null)
            {
                String lokasi = "src/com/koperasi/laporan/pinjaman.jrxml";
                String table = "pinjaman";
                String field = "tgl_pinjaman";
                cetak(lokasi, table, field, startDatePinjaman.getValue().toString(), endDatePinjaman.getValue().toString());
            }else if(startDatePinjaman.getValue()==null&&endDatePinjaman.getValue()==null)
            {
                String lokasi1 = "src/com/koperasi/laporan/pinjaman.jrxml";
                String table1 = "pinjaman";
                cetakfull(lokasi1, table1);
            }
            else
            {
                Notifications.create()
                             .title("Information")
                             .text("Filter tanggal tidak boleh di isi hanya satu tanggal. Silahkan diperbaiki.")
                             .position(Pos.BOTTOM_RIGHT)
                             .showInformation();
            }
        });
        cetakAngsuran.addEventHandler(MouseEvent.MOUSE_CLICKED, e -> {
            if(startDateAngsuran.getValue()!=null&&endDateAngsuran.getValue()!=null)
            {
                String lokasi = "src/com/koperasi/laporan/angsuran.jrxml";
                String table = "angsuran";
                String field = "tgl_bayar";
                cetak(lokasi, table, field, startDateAngsuran.getValue().toString(), endDateAngsuran.getValue().toString());
            }else if(startDateAngsuran.getValue()==null&&endDateAngsuran.getValue()==null)
            {
                String lokasi1 = "src/com/koperasi/laporan/angsuran.jrxml";
                String table1 = "angsuran";
                cetakfull(lokasi1, table1);
            }
            else
            {
                Notifications.create()
                             .title("Information")
                             .text("Filter tanggal tidak boleh di isi hanya satu tanggal. Silahkan diperbaiki.")
                             .position(Pos.BOTTOM_RIGHT)
                             .showInformation();
            }
        });
    }
    
    //cetak laporan full
    private void cetakfull(String url, String table)
    {
        try
        {
            JasperDesign jd = JRXmlLoader.load(url);
            String query = "SELECT * FROM "+table+";";
            JRDesignQuery runquery = new JRDesignQuery();
            runquery.setText(query);
            jd.setQuery(runquery);
            
            JasperReport jr = JasperCompileManager.compileReport(jd);
            JasperPrint jp = JasperFillManager.fillReport(jr, null, DriverManager.getConnection(database,user,pass));
            JasperViewer.viewReport(jp, false);
//            //eksport ke pdf secara otomatis dan lokasi simpan di documents user
//            JasperExportManager.exportReportToPdfFile(jp, System.getProperty("user.home")+"\\Documents\\data_anggota.pdf");
        }
        catch(Exception ex)
        {
            System.err.println(ex.getMessage());
            error.setTitle("Error");
            error.setHeaderText("Error");
            error.setResizable(false);
            error.setContentText(ex.getMessage());
            error.showAndWait();
        }
    }
    
    //cetak laporan dengan filter
    private void cetak(String url, String table, String field, String startDate, String endDate)
    {
        try
        {
            JasperDesign jd = JRXmlLoader.load(url);
            String query = "SELECT * FROM "+table+" WHERE "+field+" BETWEEN '"+startDate+"' AND '"+endDate+"';";
            JRDesignQuery runquery = new JRDesignQuery();
            runquery.setText(query);
            jd.setQuery(runquery);
            
            JasperReport jr = JasperCompileManager.compileReport(jd);
            JasperPrint jp = JasperFillManager.fillReport(jr, null, DriverManager.getConnection(database,user,pass));
            JasperViewer.viewReport(jp, false);
//            //eksport ke pdf secara otomatis dan lokasi simpan di documents user
//            JasperExportManager.exportReportToPdfFile(jp, System.getProperty("user.home")+"\\Documents\\data_anggota.pdf");
        }
        catch(Exception ex)
        {
            System.err.println(ex.getMessage());
            error.setTitle("Error");
            error.setHeaderText("Error");
            error.setResizable(false);
            error.setContentText(ex.getMessage());
            error.showAndWait();
        }
    }
    
    //set tanggal
    private void setTanggal(JFXDatePicker date)
    {
        String pattern = "yyyy-MM-dd";
        date.setConverter(new StringConverter<LocalDate>() {
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
    
    // <editor-fold defaultstate="collapsed" desc="Tabel Anggota">
    private void loadDataAnggota()
    {
        try
        {
            Class.forName(driver);
            Connection kon = DriverManager.getConnection(database, user, pass);
            Statement stt = kon.createStatement();
            String SQL  = "SELECT * "
                        + "FROM anggota;";
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
                String aktif1 = res.getString(9);
                listanggota.add(new Anggota(no_anggota1, nama1, tgl_masuk1, hp1, rt1, sp1, sw1, ss1, aktif1));
            }
            TableAnggota.getItems().setAll(listanggota);
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
    
    private void initColAnggota(){
        no_anggota1.setCellValueFactory(new PropertyValueFactory<>("NoAnggota1"));
        nama1.setCellValueFactory(new PropertyValueFactory<>("Nama1"));
        tgl_masuk1.setCellValueFactory(new PropertyValueFactory<>("TglMasuk1"));
        hp1.setCellValueFactory(new PropertyValueFactory<>("HP1"));
        rt1.setCellValueFactory(new PropertyValueFactory<>("RT1"));
        sp1.setCellValueFactory(new PropertyValueFactory<>("SP1"));
        sw1.setCellValueFactory(new PropertyValueFactory<>("SW1"));
        ss1.setCellValueFactory(new PropertyValueFactory<>("SS1"));
        aktif1.setCellValueFactory(new PropertyValueFactory<>("AKTIF1"));
    }
    public static class Anggota {
        private final SimpleStringProperty NoAnggota1;
        private final SimpleStringProperty Nama1;
        private final SimpleStringProperty TglMasuk1;
        private final SimpleStringProperty HP1;
        private final SimpleStringProperty RT1;
        private final SimpleStringProperty SP1;
        private final SimpleStringProperty SW1;
        private final SimpleStringProperty SS1;
        private final SimpleStringProperty AKTIF1;
        
        public Anggota(String NoAnggota1, String Nama1, String TglMasuk1, String HP1, String RT1, String SP1, String SW1, String SS1, String AKTIF1)
        {
            this.NoAnggota1 = new SimpleStringProperty(NoAnggota1);
            this.Nama1 = new SimpleStringProperty(Nama1);
            this.TglMasuk1 = new SimpleStringProperty(TglMasuk1);
            this.HP1 = new SimpleStringProperty(HP1);
            this.RT1 = new SimpleStringProperty(RT1);
            this.SP1 = new SimpleStringProperty(SP1);
            this.SW1 = new SimpleStringProperty(SW1);
            this.SS1 = new SimpleStringProperty(SS1);
            this.AKTIF1 = new SimpleStringProperty(AKTIF1);
        }

        public String getNoAnggota1() {
            return NoAnggota1.get();
        }

        public String getNama1() {
            return Nama1.get();
        }

        public String getTglMasuk1() {
            return TglMasuk1.get();
        }

        public String getHP1() {
            return HP1.get();
        }

        public String getRT1() {
            return RT1.get();
        }

        public String getSP1() {
            return SP1.get();
        }

        public String getSW1() {
            return SW1.get();
        }

        public String getSS1() {
            return SS1.get();
        }
        
        public String getAKTIF1() {
            return AKTIF1.get();
        }
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Tabel Simpanan">
    private void loadDataSimpanan()
    {
        try
        {
            Class.forName(driver);
            Connection kon = DriverManager.getConnection(database, user, pass);
            Statement stt = kon.createStatement();
            String SQL  = "SELECT * "
                        + "FROM simpanan;";
            ResultSet res = stt.executeQuery(SQL);
            while(res.next())
            {
                String no_simpanan2 = res.getString(1);
                String tgl_simpanan2 = res.getString(2);
                String jenis_simpanan2 = res.getString(3);
                String jumlah_simpanan2 = res.getString(4);
                String no_anggota2 = res.getString(5);
                String aktif2 = res.getString(6);
                
                listsimpanan.add(new Simpanan(no_simpanan2, tgl_simpanan2, jenis_simpanan2, jumlah_simpanan2, no_anggota2, aktif2));
            }
            TableSimpanan.getItems().setAll(listsimpanan);
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
    
    private void initColSimpanan(){
        no_simpanan2.setCellValueFactory(new PropertyValueFactory<>("NoSimpanan2"));
        tgl_simpanan2.setCellValueFactory(new PropertyValueFactory<>("TglSimpanan2"));
        jns_simpanan2.setCellValueFactory(new PropertyValueFactory<>("JnsSimpanan2"));
        jml_simpanan2.setCellValueFactory(new PropertyValueFactory<>("JmlSimpanan2"));
        no_anggota2.setCellValueFactory(new PropertyValueFactory<>("NoAnggota2"));
        aktif2.setCellValueFactory(new PropertyValueFactory<>("Aktif2"));
    }
    
    public static class Simpanan {
        private final SimpleStringProperty NoSimpanan2;
        private final SimpleStringProperty TglSimpanan2;
        private final SimpleStringProperty JnsSimpanan2;
        private final SimpleStringProperty JmlSimpanan2;
        private final SimpleStringProperty NoAnggota2;
        private final SimpleStringProperty Aktif2;
        
        public Simpanan(String NoSimpanan2, String TglSimpanan2, String JnsSimpanan2, String JmlSimpanan2, String NoAnggota2, String Aktif2)
        {
            this.NoSimpanan2 = new SimpleStringProperty(NoSimpanan2);
            this.TglSimpanan2 = new SimpleStringProperty(TglSimpanan2);
            this.JnsSimpanan2 = new SimpleStringProperty(JnsSimpanan2);
            this.JmlSimpanan2 = new SimpleStringProperty(JmlSimpanan2);
            this.NoAnggota2 = new SimpleStringProperty(NoAnggota2);
            this.Aktif2 = new SimpleStringProperty(Aktif2);
        }

        public String getNoSimpanan2() {
            return NoSimpanan2.get();
        }

        public String getTglSimpanan2() {
            return TglSimpanan2.get();
        }

        public String getJnsSimpanan2() {
            return JnsSimpanan2.get();
        }

        public String getJmlSimpanan2() {
            return JmlSimpanan2.get();
        }

        public String getNoAnggota2() {
            return NoAnggota2.get();
        }
        
        public String getAktif2() {
            return Aktif2.get();
        }
    }
    //</editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc="Tabel Pinjaman">
    private void loadDataPinjaman()
    {
        try
        {
            Class.forName(driver);
            Connection kon = DriverManager.getConnection(database, user, pass);
            Statement stt = kon.createStatement();
            String SQL  = "SELECT * "
                        + "FROM pinjaman;";
            ResultSet res = stt.executeQuery(SQL);
            while(res.next())
            {
                String no_pinjaman3 = res.getString(1);
                String tgl_pinjaman3 = res.getString(2);
                String jumlah_pinjaman3 = res.getString(3);
                String bunga3 = res.getString(4);
                String total_pinjaman3 = res.getString(5);
                String lama_pinjaman3 = res.getString(6);
                String angsuran3 = res.getString(7);
                String sisa_pinjaman3 = res.getString(8);
                String no_anggota3 = res.getString(9);
                String status_pinjaman3 = res.getString(10);
                String aktif3 = res.getString(11);
                
                listpinjaman.add(new Pinjaman(no_pinjaman3, tgl_pinjaman3, jumlah_pinjaman3, bunga3, total_pinjaman3, lama_pinjaman3, angsuran3, sisa_pinjaman3, no_anggota3, status_pinjaman3, aktif3));
            }
            TablePinjaman.getItems().setAll(listpinjaman);
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
    
    private void initColPinjaman(){
        no_pinjaman3.setCellValueFactory(new PropertyValueFactory<>("NoPinjaman3"));
        tgl_pinjaman3.setCellValueFactory(new PropertyValueFactory<>("TglPinjaman3"));
        jml_pinjaman3.setCellValueFactory(new PropertyValueFactory<>("JmlPinjaman3"));
        bunga3.setCellValueFactory(new PropertyValueFactory<>("Bunga3"));
        total_pinjaman3.setCellValueFactory(new PropertyValueFactory<>("TotalPinjaman3"));
        lama_pinjaman3.setCellValueFactory(new PropertyValueFactory<>("LamaPinjaman3"));
        angsuran3.setCellValueFactory(new PropertyValueFactory<>("Angsuran3"));
        sisa_pinjaman3.setCellValueFactory(new PropertyValueFactory<>("SisaPinjaman3"));
        no_anggota3.setCellValueFactory(new PropertyValueFactory<>("NoAnggota3"));
        status_pinjaman3.setCellValueFactory(new PropertyValueFactory<>("StatusPinjaman3"));
        aktif3.setCellValueFactory(new PropertyValueFactory<>("Aktif3"));
    }
    
    public static class Pinjaman {
        private final SimpleStringProperty NoPinjaman3;
        private final SimpleStringProperty TglPinjaman3;
        private final SimpleStringProperty JmlPinjaman3;
        private final SimpleStringProperty Bunga3;
        private final SimpleStringProperty TotalPinjaman3;
        private final SimpleStringProperty LamaPinjaman3;
        private final SimpleStringProperty Angsuran3;
        private final SimpleStringProperty SisaPinjaman3;
        private final SimpleStringProperty NoAnggota3;
        private final SimpleStringProperty StatusPinjaman3;
        private final SimpleStringProperty Aktif3;
        
        public Pinjaman(String NoPinjaman3, String TglPinjaman3, String JmlPinjaman3, String Bunga3, String TotalPinjaman3, 
                String LamaPinjaman3, String Angsuran3, String SisaPinjaman3, String NoAnggota3, String StatusPinjaman3, String Aktif3)
        {
            this.NoPinjaman3 = new SimpleStringProperty(NoPinjaman3);
            this.TglPinjaman3 = new SimpleStringProperty(TglPinjaman3);
            this.JmlPinjaman3 = new SimpleStringProperty(JmlPinjaman3);
            this.Bunga3 = new SimpleStringProperty(Bunga3);
            this.TotalPinjaman3 = new SimpleStringProperty(TotalPinjaman3);
            this.LamaPinjaman3 = new SimpleStringProperty(LamaPinjaman3);
            this.Angsuran3 = new SimpleStringProperty(Angsuran3);
            this.SisaPinjaman3 = new SimpleStringProperty(SisaPinjaman3);
            this.NoAnggota3 = new SimpleStringProperty(NoAnggota3);
            this.StatusPinjaman3 = new SimpleStringProperty(StatusPinjaman3);
            this.Aktif3 = new SimpleStringProperty(Aktif3);
        }

        public String getNoPinjaman3() {
            return NoPinjaman3.get();
        }

        public String getTglPinjaman3() {
            return TglPinjaman3.get();
        }

        public String getJmlPinjaman3() {
            return JmlPinjaman3.get();
        }

        public String getBunga3() {
            return Bunga3.get();
        }

        public String getTotalPinjaman3() {
            return TotalPinjaman3.get();
        }

        public String getLamaPinjaman3() {
            return LamaPinjaman3.get();
        }

        public String getAngsuran3() {
            return Angsuran3.get();
        }

        public String getSisaPinjaman3() {
            return SisaPinjaman3.get();
        }

        public String getNoAnggota3() {
            return NoAnggota3.get();
        }

        public String getStatusPinjaman3() {
            return StatusPinjaman3.get();
        }
        
        public String getAktif3() {
            return Aktif3.get();
        }
    }
    //</editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc="Tabel Angsuran">
    private void loadDataAngsuran()
    {
        try
        {
            Class.forName(driver);
            Connection kon = DriverManager.getConnection(database, user, pass);
            Statement stt = kon.createStatement();
            String SQL  = "SELECT * "
                        + "FROM angsuran;";
            ResultSet res = stt.executeQuery(SQL);
            while(res.next())
            {
                String no_angsuran4 = res.getString(1);
                String tgl_bayar4 = res.getString(2);
                String no_pinjaman4 = res.getString(3);
                String aktif4 = res.getString(4);
                listangsuran.add(new Angsuran(no_angsuran4, tgl_bayar4, no_pinjaman4, aktif4));
            }
            TableAngsuran.getItems().setAll(listangsuran);
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
    
    private void initColAngsuran(){
        no_angsuran4.setCellValueFactory(new PropertyValueFactory<>("NoAngsuran4"));
        tgl_bayar4.setCellValueFactory(new PropertyValueFactory<>("TglBayar4"));
        no_pinjaman4.setCellValueFactory(new PropertyValueFactory<>("NoPinjaman4"));
        aktif4.setCellValueFactory(new PropertyValueFactory<>("Aktif4"));
    }
    
    public static class Angsuran {
        private final SimpleStringProperty NoAngsuran4;
        private final SimpleStringProperty TglBayar4;
        private final SimpleStringProperty NoPinjaman4;
        private final SimpleStringProperty Aktif4;
        
        public Angsuran(String NoAngsuran4, String TglBayar4, String NoPinjaman4, String Aktif4)
        {
            this.NoAngsuran4 = new SimpleStringProperty(NoAngsuran4);
            this.TglBayar4 = new SimpleStringProperty(TglBayar4);
            this.NoPinjaman4 = new SimpleStringProperty(NoPinjaman4);
            this.Aktif4 = new SimpleStringProperty(Aktif4);
        }

        public String getNoAngsuran4() {
            return NoAngsuran4.get();
        }

        public String getTglBayar4() {
            return TglBayar4.get();
        }

        public String getNoPinjaman4() {
            return NoPinjaman4.get();
        }
        
        public String getAktif4() {
            return Aktif4.get();
        }
    }
    //</editor-fold>
}
