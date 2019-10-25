package groupmanager.samadhan;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.MediaController;
import android.widget.TextView;
import android.widget.VideoView;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

import static groupmanager.samadhan.R.id.txt1;

/**
 * Created by intel on 27-12-2017.
 */

public class Educational_Content2 extends AppCompatActivity {
    ArrayList<RowEnvt> ListObj;
    ListView LV1;
    TextView txthead;
    String Str_user,AppLogin;
    String MTitle, Data;
    ProgressDialog Progsdial;
    private Context context=this;


    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.educational_content1);
        LV1 = (ListView) findViewById(R.id.Lv1);
        txthead = (TextView) findViewById(R.id.txthead);

        Intent menuIntent = getIntent();
        Data = menuIntent.getStringExtra("Data");
        Str_user =  menuIntent.getStringExtra("UserClubName");
        MTitle = menuIntent.getStringExtra("MTitle");

        txthead.setText(MTitle);
        Typeface face = Typeface.createFromAsset(getAssets(), "calibri.ttf");
        txthead.setTypeface(face);

        Get_SharedPref_Values();

        FillList();//get data from database and show in listview.

        LV1.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String fileType = ListObj.get(position).getEvtName();
                String fileName = ListObj.get(position).getEvtDesc();

                String filePath="http://mybackup.co.in/samadhan/"+fileName.trim();

                if(fileName.toLowerCase().contains("youtube")){
                    //filePath=fileName;

                    String VCode=fileName.replace("https://www.youtube.com/watch?v=","");

                    Intent menuIntent = new Intent(context, YouTube_Video.class);
                    menuIntent.putExtra("VCode", VCode);
                    startActivity(menuIntent);
                }
                else {

                    if (fileType.contains("PPS"))
                        downloadAndOpenPPS(filePath);//Download and open PPS or PPT File
                    else
                        openFile(filePath);

                   /*if(fileType.contains("Video")) {
                    Disp_Video(filePath);
                   }else if(fileType.contains("Document")) {
                    Disp_Pdf(filePath);
                   }else if(fileType.contains("PPS")) {
                    Disp_PPS(filePath);
                   }*/
                }
            }
        });
    }


    private void Get_SharedPref_Values()
    {
        SharedPreferences ShPref = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
        if (ShPref.contains("Login"))
        {
            AppLogin=ShPref.getString("Login", "");
        }
    }


    private void FillList() {
        RowEnvt item;
        ListObj = new ArrayList<RowEnvt>();

        String[] Arr1=Data.split("##");

        for(int i=0;i<Arr1.length;i++)
        {
            String fileName = ChkVal(Arr1[i]);//Video,Pdf,PPS(Powerpoint)
            String Type="";
            if(fileName.length()>1)
            {
                if(i==0 && !AppLogin.equals("Guest"))
                    Type="View Video";
                else if(i==1)
                    Type="View Document";
                else if(i==2)
                    Type="View PPS";
                else if(i==3 && AppLogin.equals("Guest"))
                    Type="View Video";

                if(Type.length()>1) {
                    item = new RowEnvt(Type, fileName);
                    ListObj.add(item);// Adding contact to list
                }
            }
        }


        if(ListObj.size()>0)
        {
            Adapter_Educational_Content adp = new Adapter_Educational_Content(this, R.layout.educational_content_cell, ListObj,2);
            LV1.setAdapter(adp);

        } else {
            AlertDialog ad = new AlertDialog.Builder(this).create();
            ad.setTitle(Html.fromHtml("<font color='#FF7F27'>Result</font>"));
            ad.setMessage("No record found.");
            ad.setCancelable(false);
            ad.setButton("OK", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                         backs();
                }
            });
            ad.show();
        }

    }


    private String ChkVal(String Val) {
        if (Val == null)
            Val = "";
        return Val.trim();
    }



    ///To View Pdf File
    private void Disp_Pdf(String Pdf_File)
    {
        try {

            Intent i;
            i = new Intent(Intent.ACTION_VIEW);
            i.setDataAndType(Uri.parse(Pdf_File), "application/pdf");
            startActivity(i);

        } catch (ActivityNotFoundException e) {
            AlertDisplay("PDF Reader issue","No Application Available to view this file type !");
        }
    }


    ///To View Video
    private void Disp_Video(String Video_File)
    {
        // Display Popup Screen of ListView
        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);// For Hide the title of the dialog box
        dialog.setContentView(R.layout.view_video);
        dialog.setCancelable(false);
        dialog.show();

        final VideoView VidVw=(VideoView)dialog.findViewById(R.id.videoView);
        Button btnClose=(Button)dialog.findViewById(R.id.btnClose);

        MediaController mediaControls=new MediaController(this);
        mediaControls.setAnchorView(VidVw);

        // set the media controller for video view

        Uri uri = Uri.parse(Video_File);
        VidVw.setVideoURI(uri);
        VidVw.setMediaController(mediaControls);
        VidVw.requestFocus();
        //setContentView(VidVw);
        VidVw.start();


        btnClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (VidVw.isPlaying()) {
                    VidVw.stopPlayback();
                }
                dialog.dismiss();
            }
        });
    }



    ///Open file with format
    private void openFile(String url) {

        try {

            Uri uri = Uri.parse(url);

            Intent intent = new Intent(Intent.ACTION_VIEW);
            if (url.toString().contains(".doc") || url.toString().contains(".docx")) {
                // Word document
                intent.setDataAndType(uri, "application/msword");
            } else if (url.toString().contains(".pdf")) {
                // PDF file
                intent.setDataAndType(uri, "application/pdf");
            } else if (url.toString().contains(".ppt") || url.toString().contains(".pptx") || url.toString().contains(".pps")) {
                // Powerpoint file
                intent.setDataAndType(uri, "application/vnd.ms-powerpoint");
                //intent.setDataAndType(uri, "application/powerpoint");
            } else if (url.toString().contains(".xls") || url.toString().contains(".xlsx")) {
                // Excel file
                intent.setDataAndType(uri, "application/vnd.ms-excel");
            } else if (url.toString().contains(".zip") || url.toString().contains(".rar")) {
                // WAV audio file
                intent.setDataAndType(uri, "application/x-wav");
            } else if (url.toString().contains(".rtf")) {
                // RTF file
                intent.setDataAndType(uri, "application/rtf");
            } else if (url.toString().contains(".wav") || url.toString().contains(".mp3")) {
                // WAV audio file
                intent.setDataAndType(uri, "audio/x-wav");
            } else if (url.toString().contains(".gif")) {
                // GIF file
                intent.setDataAndType(uri, "image/gif");
            } else if (url.toString().contains(".jpg") || url.toString().contains(".jpeg") || url.toString().contains(".png")) {
                // JPG file
                intent.setDataAndType(uri, "image/jpeg");
            } else if (url.toString().contains(".txt")) {
                // Text file
                intent.setDataAndType(uri, "text/plain");
            } else if (url.toString().contains(".3gp") || url.toString().contains(".mpg") ||
                    url.toString().contains(".mpeg") || url.toString().contains(".mpe") || url.toString().contains(".mp4") || url.toString().contains(".avi")) {
                // Video files
                intent.setDataAndType(uri, "video/*");
            } else {
                intent.setDataAndType(uri, "*/*");
            }

            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        } catch (ActivityNotFoundException e) {
            AlertDisplay("", "No application found which can open the file");
        }
    }



    ///To View PPS
    private void Disp_PPS(String PPS_File)
    {
        try {

            Uri uri=Uri.parse(PPS_File);
            Intent i= new Intent(Intent.ACTION_VIEW);


            //String backupDBPath = Environment.getExternalStorageDirectory()+"/Download/PPS_054e51f3_Loan-Against-Property.ppt";

            //File PPtah=new File(backupDBPath);
            // i.setDataAndType(Uri.fromFile(PPtah), "application/vnd.ms-powerpoint");

            i.setDataAndType(uri, "application/vnd.ms-powerpoint");
            //i.setType("application/vnd.ms-powerpoint");


            startActivity(i);

        } catch (Exception e) {
            AlertDisplay("Powerpoint issue","No Application Available to view this file type !");
        }
    }



    ///To Download and View PPS or PPT file
    void downloadAndOpenPPS(final String FilePath) {
        progressdial();
        new Thread(new Runnable() {
            public void run() {
                File f1=downloadFile(FilePath);
                if(f1!=null)
                {
                 Uri path = Uri.fromFile(f1);

                    try {
                        Intent intent = new Intent(Intent.ACTION_VIEW);
                        intent.setDataAndType(path, "application/vnd.ms-powerpoint");
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);
                        //finish();
                    } catch (ActivityNotFoundException e) {
                        setTextError("",1);
                    }
                }

            }
        }).start();
    }




    File downloadFile(String dwnload_file_path) {
        int downloadedSize = 0, totalsize;
        float per = 0;

        File file = null;
        try {

            // set the path where we want to save the file
            File SDCardRoot = Environment.getExternalStorageDirectory();
            // create a new file, to save the downloaded file
            String PPSFileName=dwnload_file_path.replace("http://mybackup.co.in/samadhan/", "");
            String dest_file_path = "/Samadhan/PowerPoint/"+PPSFileName;
            file = new File(SDCardRoot, dest_file_path);

            if(!file.exists())
            {
                File folder = new File(SDCardRoot, "/Samadhan/PowerPoint/");

                if (!folder.isDirectory()) {
                    folder.mkdirs();
                }

                setText("Please Wait....");

                URL url = new URL(dwnload_file_path);
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();

                urlConnection.setRequestMethod("GET");
                //urlConnection.setDoOutput(true);

                // connect
                urlConnection.connect();

                FileOutputStream fileOutput = new FileOutputStream(file);

                int Flength = urlConnection.getContentLength();

                if (Flength > 0) {

                    // Stream used for reading the data from the internet
                    InputStream inputStream = urlConnection.getInputStream();

                    // this is the total size of the file which we are
                    // downloading
                    totalsize = urlConnection.getContentLength();
                    setText("Starting download...");

                    // create a buffer...
                    byte[] buffer = new byte[1024 * 1024];
                    int bufferLength = 0;

                    while ((bufferLength = inputStream.read(buffer)) > 0) {
                        fileOutput.write(buffer, 0, bufferLength);
                        downloadedSize += bufferLength;
                        per = ((float) downloadedSize / totalsize) * 100;
                        setText("Total File size  : "
                                + (totalsize / 1024)
                                + " KB\n\nDownloading " + (int) per
                                + "% complete");
                    }
                    // close the output stream when complete //
                    fileOutput.close();
                    setText("Download Complete..");
                }
            }

        } catch (final MalformedURLException e) {
            file=null;
            setTextError("Some error occured. Press back and try again.",0);
        } catch (final IOException e) {
            file=null;
            setTextError("Some error occured. Press back and try again.",0);
        } catch (final Exception e) {
            file=null;
            setTextError("Failed to download file. Please check your internet connection.",0);
        }

        Progsdial.dismiss();

        return file;
    }

    void setTextError(final String message,final int i) {
        runOnUiThread(new Runnable() {
            public void run() {
                if(i==0)
                    AlertDisplay("",message);
                else
                    ConfirmAlert_Installpowerpoint();
            }
        });

    }

    void setText(final String txt) {
        runOnUiThread(new Runnable() {
            public void run() {
                Progsdial.setMessage(txt);
            }
        });

    }





    public boolean onKeyDown(int keyCode, KeyEvent event)
    {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            backs();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    public void backs(){
        finish();
    }


    private void AlertDisplay(String head,String body)
    {
        AlertDialog ad=new AlertDialog.Builder(this).create();
        ad.setTitle( Html.fromHtml("<font color='#E3256B'>"+head+"</font>"));
        ad.setMessage(Html.fromHtml("<font color='#1C1CF0'>"+body+"</font>"));
        ad.setButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        ad.show();
    }



    ///Display Confirmation dialog for Install Microsoft powerpoint
    private void ConfirmAlert_Installpowerpoint()
    {
        AlertDialog.Builder AdBuilder = new AlertDialog.Builder(this);
        AdBuilder.setMessage(Html.fromHtml("<font color='#E3256B'>Please install Microsoft Powerpoint to view this file type <br/>Do you want to download it now ?</font>"));
        AdBuilder
                .setPositiveButton("YES",new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog,int id) {
                        dialog.dismiss();
                        Intent intent = new Intent(android.content.Intent.ACTION_VIEW);

                        //Copy App URL from Google Play Store.
                        intent.setData(Uri.parse("https://play.google.com/store/apps/details?id=com.microsoft.office.powerpoint&hl=en"));

                        startActivity(intent);
                    }
                })
                .setNegativeButton("NO",new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog,int id) {
                        dialog.dismiss();
                    }
                });

        AdBuilder.show();
    }



    protected void progressdial()
    {
        Progsdial = new ProgressDialog(this, R.style.MyTheme);
        //Progsdial.setTitle("App Loading");
        // Progsdial.setMessage("Please Wait....");
        Progsdial.setIndeterminate(true);
        Progsdial.setCancelable(false);
        Progsdial.getWindow().setGravity(Gravity.DISPLAY_CLIP_VERTICAL);
        Progsdial.show();
    }


}
