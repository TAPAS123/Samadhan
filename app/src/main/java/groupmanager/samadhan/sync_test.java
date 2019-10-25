package groupmanager.samadhan;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.StrictMode;
import android.text.Html;
import android.view.KeyEvent;
import android.view.WindowManager;
import android.widget.TextView;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Created by intel on 02-01-2018.
 */

public class sync_test extends Activity {
    TextView txtProgg;
    Thread networkThread, background;
    Connection conn;
    String ClientId, Log, ClubName, logid, Tab2Name1,Tab2Name2, UID = "", ChkSync = "";
    ProgressDialog dialog;
    ResultSet rs, RS1;
    SQLiteDatabase dbase;
    final Context context = this;
    Cursor cursorT;
    String TablesInfo;
    int Tab2Count,  Tab2Max_Mid,   Tab2Min_Sync,   Tab2Min_SyncDT  ;
    String CurrentDT_Diff, SyncDT;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.clubsync);

        Intent menuIntent = getIntent();
        Log = menuIntent.getStringExtra("Clt_Log");
        logid = menuIntent.getStringExtra("Clt_LogID");
        ClubName = menuIntent.getStringExtra("Clt_ClubName");
        ClientId = menuIntent.getStringExtra("UserClubName");
        TablesInfo = menuIntent.getStringExtra("StrTT");

        Get_SharedPref_Values();

        String[] TabArr = TablesInfo.split("#");
        // 2nd Table Info
        Tab2Name1 = "C_" + ClientId + "_2"; // 2nd Table Name Local Table name
        Tab2Name2 = "C_mukesh_2"; // 2nd Table Name Server Table name
        Tab2Max_Mid = Integer.parseInt(TabArr[0]); // 2nd Table Max M_id
        Tab2Count = Integer.parseInt(TabArr[1]); // 2nd Table Total Records
        Tab2Min_Sync = Integer.parseInt(TabArr[2]); // 2nd Table Min Sync Id
        Tab2Min_SyncDT = Integer.parseInt(TabArr[3]); // 2nd Table Min Sync DateTimeDiff

        txtProgg = (TextView) findViewById(R.id.txtUpdateRecd);

        dialog = new ProgressDialog(this);
        dialog.setCancelable(false);
        dialog.setMessage(Html.fromHtml("<font color='#0892D0'>Loading...</font>"));
        dialog.setIcon(R.drawable.syncy);
        dialog.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        dialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        dialog.setTitle(Html.fromHtml("<font color='#008000'>Synchronization ( 1/1 )</font>"));

        dialog.setButton(DialogInterface.BUTTON_NEGATIVE, Html.fromHtml("<font color='#FF2400'>Stop now!</font>"), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialg, int which) {
                backs();
            }
        });

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        RInsert();
    }


    private void Get_SharedPref_Values() {
        SharedPreferences ShPref = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
        if (ShPref.contains("userid")) {
            UID = ShPref.getString("userid", "");
        }
        if (ShPref.contains("ChkSync")) {
            ChkSync = ShPref.getString("ChkSync", "");
        }
    }

    public void RInsert() {
        networkThread = new Thread() {
            @Override
            public void run() {
                try {
                    Looper.prepare();
                    String ConnectionString = "jdbc:jtds:sqlserver://103.21.58.192:1433/mda_clubs";
                    Class.forName("net.sourceforge.jtds.jdbc.Driver").newInstance();
                    conn = DriverManager.getConnection(ConnectionString, "mda_club", "MDA.1234_");
                    WebServiceCall webcall = new WebServiceCall();//Call a Webservice
                    CurrentDT_Diff = webcall.SyncDT_GetJullian();

                    runOnUiThread(new Runnable() {
                        public void run() {
                            if (CurrentDT_Diff != "CatchError") {
                                dialog.show();
                                String SArr[] = CurrentDT_Diff.split("#");
                                SyncDT = SArr[0].trim();
                                //String t=SyncDT;
                                //RecordInsertion(Tab2Name,Tab2Count,Tab2Max_Mid,Tab2Min_Sync,Tab2Min_SyncDT); // Insert For Table 2
                                //RecordInsertion(Tab2Name,Tab2Count,4434,Tab2Min_Sync,Tab2Min_SyncDT);
                                RecordInsertion(Tab2Count, Tab2Max_Mid, Tab2Min_Sync, Tab2Min_SyncDT); // Insert For Table 4 First
                            } else {
                                DisplayMsg("Connection Problem !", "No Internet Connection");
                            }
                        }
                    });
                } catch (SQLException se) {
                    DisplayMsg("Connection Problem !", "No Internet Connection");
                    se.printStackTrace();
                } catch (ClassNotFoundException e) {
                    DisplayMsg("Connection Problem !", "No Internet Connection");
                    e.printStackTrace();
                } catch (Exception e) {
                    DisplayMsg("Connection Problem !", "No Internet Connection");
                    e.printStackTrace();
                }
            }
        };
        networkThread.start();
    }


    private void RecordInsertion(int TabCount, int Max_M_id, int Min_Sync, int Min_SyncDT) {
        String StrQry = "", StrQryCount = "";
        int RCount = 0;

        try {
            Statement statement = conn.createStatement();
            if (TabCount == 0) {
                StrQryCount = "select count(M_id) from " + Tab2Name2; // For Count
                StrQry = "select * from " + Tab2Name2 + " order by M_id";
            } else {
                StrQryCount = "select count(M_id) from " + Tab2Name2 + " where M_id>" + Max_M_id; // For Count
                StrQry = "select * from " + Tab2Name2 + " where M_id>" + Max_M_id + " order by M_id";
            }
            RS1 = statement.executeQuery(StrQryCount);
            while (RS1.next()) {
                RCount = RS1.getInt(1);
                break;
            }
            RS1.close(); // Close Result Set(RS1) Of Count
            rs = statement.executeQuery(StrQry);
            ProgressBar(RCount);// Display Progress bar for Insertion
            Prog_Insert(TabCount, Min_Sync, Min_SyncDT);
        } catch (SQLException se) {
            DisplayMsg("Connection Problem !", "No Internet Connection");
            se.printStackTrace();
        } catch (Exception e) {
            DisplayMsg("Connection Problem !", "No Internet Connection");
            e.printStackTrace();
        }
    }


    private void RecordUpdation(int Min_Sync, int Min_SyncDT) {
        String StrQry = "", StrQryCount = "";
        int RCount = 0;
        try {
            Statement statement = conn.createStatement();
            StrQryCount = "select count(M_id) from " + Tab2Name2 + " where Syncid>" + Min_Sync + " AND SyncDT>" + Min_SyncDT; // For Count
            StrQry = "select * from " + Tab2Name2 + " where Syncid>" + Min_Sync + " AND SyncDT>" + Min_SyncDT + " order by M_id";
            RS1 = statement.executeQuery(StrQryCount);
            while (RS1.next()) {
                RCount = RS1.getInt(1);
                break;
            }
            RS1.close(); // Close Result Set(RS1) Of Count
            rs = statement.executeQuery(StrQry);
            ProgressBar(RCount); // Display Progress bar for Updation
            Prog_Update();
        } catch (SQLException se) {
            DisplayMsg("Connection Problem !", "No Internet Connection");
            se.printStackTrace();
        } catch (Exception e) {
            DisplayMsg("Connection Problem !", "No Internet Connection");
            e.printStackTrace();
        }
    }





    Handler progressHandler = new Handler() {
        public void handleMessage(Message msg) {
            dialog.incrementProgressBy(1);
        }
    };

    private void ProgressBar(int RecordCount) {
        //Table 5 is User for Delete Records from Local Db
        dialog.setTitle(Html.fromHtml("<font color='#008000'>Synchronization ( 1/1 )</font>"));
        dialog.setProgress(0);
        dialog.setMax(RecordCount);
    }


    private void RecordUpdate_DT() {
        dbase = openOrCreateDatabase("MDA_Club_Saved", SQLiteDatabase.CREATE_IF_NECESSARY, null);
        String StrQry = "Update " + Tab2Name1 + " Set SyncDT=" + SyncDT;
        dbase.execSQL(StrQry);
        dbase.close();
    }


    private void Prog_Update() {
        networkThread = new Thread() {
            @Override
            public void run() {
                try {
                    Looper.prepare();
                    String StrQry = "";
                    int j  = 96; // SyncId Position


                    while (rs.next()) {
                        int M_Id = rs.getInt(1);
                        int SYNCID = rs.getInt(j);

                        dbase = openOrCreateDatabase("MDA_Club_Saved", SQLiteDatabase.CREATE_IF_NECESSARY, null);
                        String sqlSearch = "select Syncid from " + Tab2Name1 + " where M_id=" + M_Id;
                        cursorT = dbase.rawQuery(sqlSearch, null);
                        while (cursorT.moveToFirst()) {
                            if (SYNCID != cursorT.getInt(0)) {
                                StrQry = "Delete from " + Tab2Name1 + " where M_id=" + M_Id;
                                dbase.execSQL(StrQry);
                                dbase.close();

                                Dbhandler_Test db = new Dbhandler_Test(context, Tab2Name1);

                                    // Insert Updated Record
                                    String M_Name = rs.getString(2);
                                    String M_FName = rs.getString(3);
                                    String M_MName = rs.getString(4);
                                    String M_LName = rs.getString(5);
                                    String M_Add1 = rs.getString(6);
                                    String M_Add2 = rs.getString(7);
                                    String M_Add3 = rs.getString(8);
                                    String M_City = rs.getString(9);
                                    String M_Email = rs.getString(10);
                                    String M_Gender = rs.getString(11);
                                    String M_MrgAnn_D = rs.getString(12);
                                    String M_MrgAnn_M = rs.getString(13);
                                    String M_MrgAnn_Y = rs.getString(14);
                                    String M_DOB_D = rs.getString(15);
                                    String M_DOB_M = rs.getString(16);
                                    String M_DOB_Y = rs.getString(17);
                                    String M_Mob = rs.getString(18);
                                    String M_Pin = rs.getString(19);
                                    String M_BG = rs.getString(20);
                                    String M_BussNm = rs.getString(21);
                                    String M_BussCate = rs.getString(22);
                                    String M_MemSince_D = rs.getString(23);
                                    String M_MemSince_M = rs.getString(24);
                                    String M_MemSince_Y = rs.getString(25);
                                    byte[] M_Pic = rs.getBytes(26);
                                    String S_Name = rs.getString(27);
                                    String S_FName = rs.getString(28);
                                    String S_Mname = rs.getString(29);
                                    String S_LName = rs.getString(30);
                                    String S_Mob = rs.getString(31);
                                    String S_Email = rs.getString(32);
                                    String S_DOB_D = rs.getString(33);
                                    String S_DOB_M = rs.getString(34);
                                    String S_DOB_Y = rs.getString(35);
                                    String S_BG = rs.getString(36);
                                    byte[] S_Pic = rs.getBytes(37);
                                    String C1_Name = rs.getString(38);
                                    String C1_FName = rs.getString(39);
                                    String C1_Mname = rs.getString(40);
                                    String C1_LName = rs.getString(41);
                                    String C1_Mob = rs.getString(42);
                                    String C1_Email = rs.getString(43);
                                    String C1_Gender = rs.getString(44);
                                    String C1_DOB_D = rs.getString(45);
                                    String C1_DOB_M = rs.getString(46);
                                    String C1_DOB_Y = rs.getString(47);
                                    String C1_BG = rs.getString(48);
                                    byte[] C1_Pic = rs.getBytes(49);
                                    String C2_Name = rs.getString(50);
                                    String C2_FName = rs.getString(51);
                                    String C2_Mname = rs.getString(52);
                                    String C2_LName = rs.getString(53);
                                    String C2_Mob = rs.getString(54);
                                    String C2_Email = rs.getString(55);
                                    String C2_Gender = rs.getString(56);
                                    String C2_DOB_D = rs.getString(57);
                                    String C2_DOB_M = rs.getString(58);
                                    String C2_DOB_Y = rs.getString(59);
                                    String C2_BG = rs.getString(60);
                                    byte[] C2_Pic = rs.getBytes(61);
                                    String C3_Name = rs.getString(62);
                                    String C3_FName = rs.getString(63);
                                    String C3_Mname = rs.getString(64);
                                    String C3_LName = rs.getString(65);
                                    String C3_Mob = rs.getString(66);
                                    String C3_Email = rs.getString(67);
                                    String C3_Gender = rs.getString(68);
                                    String C3_DOB_D = rs.getString(69);
                                    String C3_DOB_M = rs.getString(70);
                                    String C3_DOB_Y = rs.getString(71);
                                    String C3_BG = rs.getString(72);
                                    byte[] C3_Pic = rs.getBytes(73);
                                    String C4_Name = rs.getString(74);
                                    String C4_FName = rs.getString(75);
                                    String C4_Mname = rs.getString(76);
                                    String C4_LName = rs.getString(77);
                                    String C4_Mob = rs.getString(78);
                                    String C4_Email = rs.getString(79);
                                    String C4_Gender = rs.getString(80);
                                    String C4_DOB_D = rs.getString(81);
                                    String C4_DOB_M = rs.getString(82);
                                    String C4_DOB_Y = rs.getString(83);
                                    String C4_BG = rs.getString(84);
                                    byte[] C4_Pic = rs.getBytes(85);
                                    String Pass = rs.getString(86);
                                    byte[] Photo1 = rs.getBytes(87);
                                    byte[] Photo2 = rs.getBytes(88);
                                    byte[] Photo3 = rs.getBytes(89);
                                    byte[] Photo4 = rs.getBytes(90);
                                    byte[] Photo5 = rs.getBytes(91);
                                    byte[] Photo6 = rs.getBytes(92);
                                    int Admin = rs.getInt(93);
                                    String MemNo = rs.getString(94);
                                    String Oth = rs.getString(95);

                                    //Added Some Extra Fields On 16-03-2016
                                    String[] ArrExtra = new String[14];
                                    ArrExtra[0] = rs.getString(98);// M_SndAdd1
                                    ArrExtra[1] = rs.getString(99);// M_SndAdd2
                                    ArrExtra[2] = rs.getString(100);// M_SndAdd3
                                    ArrExtra[3] = rs.getString(101);// M_SndAdd4
                                    ArrExtra[4] = rs.getString(102);// M_SndPin
                                    ArrExtra[5] = rs.getString(103);// M_SndMob
                                    ArrExtra[6] = rs.getString(104);// M_SndMob1
                                    ArrExtra[7] = rs.getString(105);// M_SndStd
                                    ArrExtra[8] = rs.getString(106);// Oth1
                                    ArrExtra[9] = rs.getString(107);// Oth2
                                    ArrExtra[10] = rs.getString(108);// Oth3
                                    ArrExtra[11] = rs.getString(109);// M_Land1
                                    ArrExtra[12] = rs.getString(110);// M_Land2
                                    ArrExtra[13] = rs.getString(111);// M_Email1
                                    /////////////////////////////////////////

                                    db.addContact(M_Id, M_Name, M_FName, M_MName, M_LName, M_Add1,
                                            M_Add2, M_Add3, M_City, M_Email, M_Gender, M_MrgAnn_D,
                                            M_MrgAnn_M, M_MrgAnn_Y, M_DOB_D, M_DOB_M, M_DOB_Y, M_Mob, M_Pin,
                                            M_BG, M_BussNm, M_BussCate, M_MemSince_D, M_MemSince_M, M_MemSince_Y, M_Pic,
                                            S_Name, S_FName, S_Mname, S_LName, S_Mob, S_Email, S_DOB_D, S_DOB_M,
                                            S_DOB_Y, S_BG, S_Pic, C1_Name, C1_FName, C1_Mname,
                                            C1_LName, C1_Mob, C1_Email, C1_Gender, C1_DOB_D, C1_DOB_M, C1_DOB_Y, C1_BG,
                                            C1_Pic, C2_Name, C2_FName, C2_Mname, C2_LName, C2_Mob, C2_Email, C2_Gender,
                                            C2_DOB_D, C2_DOB_M, C2_DOB_Y, C2_BG, C2_Pic, C3_Name, C3_FName, C3_Mname, C3_LName,
                                            C3_Mob, C3_Email, C3_Gender, C3_DOB_D, C3_DOB_M, C3_DOB_Y, C3_BG, C3_Pic, C4_Name,
                                            C4_FName, C4_Mname, C4_LName, C4_Mob, C4_Email, C4_Gender, C4_DOB_D, C4_DOB_M,
                                            C4_DOB_Y, C4_BG, C4_Pic, Pass, Photo1, Photo2, Photo3, Photo4,
                                            Photo5, Photo6, Admin, MemNo, Oth, SYNCID, SyncDT, ArrExtra);

                                db.close();
                            } else {
                                StrQry = "Update " + Tab2Name1 + " Set SyncDT=" + SyncDT + " where M_id=" + M_Id;
                                dbase.execSQL(StrQry);
                                dbase.close();
                            }
                            break;
                        }
                        cursorT.close();
                        dbase.close(); // Close Local DataBase
                        progressHandler.sendMessage(progressHandler.obtainMessage());
                    }
                    rs.close();


                    RecordUpdate_DT(); //Update DatetimeDiff To All Data
                    runOnUiThread(new Runnable() {
                        public void run() {

                               backs(); // Delete Records After Updation
                        }
                    });
                } catch (SQLException se) {
                    DisplayMsg("Connection Problem !", "No Internet Connection");
                    se.printStackTrace();
                } catch (Exception e) {
                    DisplayMsg("Connection Problem !", "No Internet Connection");
                    e.printStackTrace();
                }
            }
        };
        networkThread.start();
    }


    public void Prog_Insert(final int TabCount, final int Min_Sync, final int Min_SyncDT) {
        background = new Thread(new Runnable() {
            public void run() {
                try {
                    Looper.prepare();
                    Dbhandler_Test db = new Dbhandler_Test(context, Tab2Name1);
                    while (rs.next()) {
                            int M_Id = rs.getInt(1);
                            //System.out.println("Mid:"+M_Id);
                            String M_Name = rs.getString(2);
                            String M_FName = rs.getString(3);
                            String M_MName = rs.getString(4);
                            String M_LName = rs.getString(5);
                            String M_Add1 = rs.getString(6);
                            String M_Add2 = rs.getString(7);
                            String M_Add3 = rs.getString(8);
                            String M_City = rs.getString(9);
                            String M_Email = rs.getString(10);
                            String M_Gender = rs.getString(11);
                            String M_MrgAnn_D = rs.getString(12);
                            String M_MrgAnn_M = rs.getString(13);
                            String M_MrgAnn_Y = rs.getString(14);
                            String M_DOB_D = rs.getString(15);
                            String M_DOB_M = rs.getString(16);
                            String M_DOB_Y = rs.getString(17);
                            String M_Mob = rs.getString(18);
                            String M_Pin = rs.getString(19);
                            String M_BG = rs.getString(20);
                            String M_BussNm = rs.getString(21);
                            String M_BussCate = rs.getString(22);
                            String M_MemSince_D = rs.getString(23);
                            String M_MemSince_M = rs.getString(24);
                            String M_MemSince_Y = rs.getString(25);
                            byte[] M_Pic = rs.getBytes(26);
                            String S_Name = rs.getString(27);
                            String S_FName = rs.getString(28);
                            String S_Mname = rs.getString(29);
                            String S_LName = rs.getString(30);
                            String S_Mob = rs.getString(31);
                            String S_Email = rs.getString(32);
                            String S_DOB_D = rs.getString(33);
                            String S_DOB_M = rs.getString(34);
                            String S_DOB_Y = rs.getString(35);
                            String S_BG = rs.getString(36);
                            byte[] S_Pic = rs.getBytes(37);
                            String C1_Name = rs.getString(38);
                            String C1_FName = rs.getString(39);
                            String C1_Mname = rs.getString(40);
                            String C1_LName = rs.getString(41);
                            String C1_Mob = rs.getString(42);
                            String C1_Email = rs.getString(43);
                            String C1_Gender = rs.getString(44);
                            String C1_DOB_D = rs.getString(45);
                            String C1_DOB_M = rs.getString(46);
                            String C1_DOB_Y = rs.getString(47);
                            String C1_BG = rs.getString(48);
                            byte[] C1_Pic = rs.getBytes(49);
                            String C2_Name = rs.getString(50);
                            String C2_FName = rs.getString(51);
                            String C2_Mname = rs.getString(52);
                            String C2_LName = rs.getString(53);
                            String C2_Mob = rs.getString(54);
                            String C2_Email = rs.getString(55);
                            String C2_Gender = rs.getString(56);
                            String C2_DOB_D = rs.getString(57);
                            String C2_DOB_M = rs.getString(58);
                            String C2_DOB_Y = rs.getString(59);
                            String C2_BG = rs.getString(60);
                            byte[] C2_Pic = rs.getBytes(61);
                            String C3_Name = rs.getString(62);
                            String C3_FName = rs.getString(63);
                            String C3_Mname = rs.getString(64);
                            String C3_LName = rs.getString(65);
                            String C3_Mob = rs.getString(66);
                            String C3_Email = rs.getString(67);
                            String C3_Gender = rs.getString(68);
                            String C3_DOB_D = rs.getString(69);
                            String C3_DOB_M = rs.getString(70);
                            String C3_DOB_Y = rs.getString(71);
                            String C3_BG = rs.getString(72);
                            byte[] C3_Pic = rs.getBytes(73);
                            String C4_Name = rs.getString(74);
                            String C4_FName = rs.getString(75);
                            String C4_Mname = rs.getString(76);
                            String C4_LName = rs.getString(77);
                            String C4_Mob = rs.getString(78);
                            String C4_Email = rs.getString(79);
                            String C4_Gender = rs.getString(80);
                            String C4_DOB_D = rs.getString(81);
                            String C4_DOB_M = rs.getString(82);
                            String C4_DOB_Y = rs.getString(83);
                            String C4_BG = rs.getString(84);
                            byte[] C4_Pic = rs.getBytes(85);
                            String Pass = rs.getString(86);
                            byte[] Photo1 = rs.getBytes(87);
                            byte[] Photo2 = rs.getBytes(88);
                            byte[] Photo3 = rs.getBytes(89);
                            byte[] Photo4 = rs.getBytes(90);
                            byte[] Photo5 = rs.getBytes(91);
                            byte[] Photo6 = rs.getBytes(92);
                            int Admin = rs.getInt(93);
                            String MemNo = rs.getString(94);
                            String Oth = rs.getString(95);
                            int SYNCID = rs.getInt(96);

                            //Added Some Extra Fields On 16-03-2016
                            String[] ArrExtra = new String[14];
                            ArrExtra[0] = rs.getString(98);// M_SndAdd1
                            ArrExtra[1] = rs.getString(99);// M_SndAdd2
                            ArrExtra[2] = rs.getString(100);// M_SndAdd3
                            ArrExtra[3] = rs.getString(101);// M_SndAdd4
                            ArrExtra[4] = rs.getString(102);// M_SndPin
                            ArrExtra[5] = rs.getString(103);// M_SndMob
                            ArrExtra[6] = rs.getString(104);// M_SndMob1
                            ArrExtra[7] = rs.getString(105);// M_SndStd
                            ArrExtra[8] = rs.getString(106);// Oth1
                            ArrExtra[9] = rs.getString(107);// Oth2
                            ArrExtra[10] = rs.getString(108);// Oth3
                            ArrExtra[11] = rs.getString(109);// M_Land1
                            ArrExtra[12] = rs.getString(110);// M_Land2
                            ArrExtra[13] = rs.getString(111);// M_Email1
                            /////////////////////////////////////////

                            db.addContact(M_Id, M_Name, M_FName, M_MName, M_LName, M_Add1,
                                    M_Add2, M_Add3, M_City, M_Email, M_Gender, M_MrgAnn_D,
                                    M_MrgAnn_M, M_MrgAnn_Y, M_DOB_D, M_DOB_M, M_DOB_Y, M_Mob, M_Pin,
                                    M_BG, M_BussNm, M_BussCate, M_MemSince_D, M_MemSince_M, M_MemSince_Y, M_Pic,
                                    S_Name, S_FName, S_Mname, S_LName, S_Mob, S_Email, S_DOB_D, S_DOB_M,
                                    S_DOB_Y, S_BG, S_Pic, C1_Name, C1_FName, C1_Mname,
                                    C1_LName, C1_Mob, C1_Email, C1_Gender, C1_DOB_D, C1_DOB_M, C1_DOB_Y, C1_BG,
                                    C1_Pic, C2_Name, C2_FName, C2_Mname, C2_LName, C2_Mob, C2_Email, C2_Gender,
                                    C2_DOB_D, C2_DOB_M, C2_DOB_Y, C2_BG, C2_Pic, C3_Name, C3_FName, C3_Mname, C3_LName,
                                    C3_Mob, C3_Email, C3_Gender, C3_DOB_D, C3_DOB_M, C3_DOB_Y, C3_BG, C3_Pic, C4_Name,
                                    C4_FName, C4_Mname, C4_LName, C4_Mob, C4_Email, C4_Gender, C4_DOB_D, C4_DOB_M,
                                    C4_DOB_Y, C4_BG, C4_Pic, Pass, Photo1, Photo2, Photo3, Photo4,
                                    Photo5, Photo6, Admin, MemNo, Oth, SYNCID, SyncDT, ArrExtra);

                        progressHandler.sendMessage(progressHandler.obtainMessage());
                    }
                    rs.close(); // Close Result Set
                    db.close(); // Close Local DataBase



                    runOnUiThread(new Runnable() {
                        public void run() {

                                if (TabCount != 0) {
                                    RecordUpdation( Min_Sync, Min_SyncDT); // Records Updation
                                } else {

                                    backs();
                                }
                        }
                    });
                } catch (SQLException se) {
                    DisplayMsg("Connection Problem !", "No Internet Connection");
                    se.printStackTrace();
                } catch (Exception e) {
                    DisplayMsg("Connection Problem !", "No Internet Connection");
                    e.printStackTrace();
                }
            }
        });
        // start the background thread
        background.start();
    }




    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            //backs();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }


    @SuppressWarnings("deprecation")
    private void DisplayMsg(String head, String body) {
        AlertDialog ad = new AlertDialog.Builder(this).create();
        ad.setTitle(Html.fromHtml("<font color='#E3256B'>" + head + "</font>"));
        ad.setMessage(Html.fromHtml("<font color='#1C1CF0'>" + body + "</font>"));
        ad.setButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                backs();
            }
        });
        ad.show();
    }


    public void backs() {
        //background.stop();
        try {
            conn.close(); // Close the Server Connection
        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (Exception ex) {
            // TODO Auto-generated catch block
            ex.printStackTrace();
        }
        dialog.dismiss(); // Dismiss Progress Bar
        finish();
    }

}
