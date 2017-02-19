package com.honeywell.editingplugin;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.BatteryManager;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.util.Log;
import android.widget.Toast;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by e43884 on 04/01/2017.
 */
public class DataEditing extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {

        String ScanResult = intent.getStringExtra("data");//Read the scan result from the Intent
        String codeId = intent.getStringExtra("codeId");//Read the scan result from the Intent
        String DataFormated;
        float batLevel;

        //---------------------------------------------
        //Modify the scan result as needed.
        //---------------------------------------------

        //Toast.makeText(context,"ScanResult: "+ ScanResult,Toast.LENGTH_SHORT).show();
        //Toast.makeText(context, "CodeID: " + codeId, Toast.LENGTH_SHORT).show();

        DataFormated = ScanResult + '\n';

//        if(codeId.contains("e")) {
//            // ITF25; prefix M
//            DataFormated="M" + ScanResult;
//        }
//        else if (codeId.contains("I")) {
//            // GS1-128; Replace 0x1D with *
//            DataFormated=ScanResult.replace((char)0x1D,'*');
//        }

//        if(codeId.contains("j") && ScanResult.length()>12) {
//            // Code128, Only the first 13 characters
//            DataFormated= ScanResult.substring(0,13);
//
//            // Keep Enter Suffix
//            if (ScanResult.charAt(ScanResult.length()-1)=='\n')
//            {
//                DataFormated=DataFormated+'\n';
//            }
//        }
//        else {
//            DataFormated = ScanResult;
//        }

        batLevel= getBatteryLevel(context);
        appendLog(DataFormated, String.format("%.00f", batLevel));

        //Return the Modified scan result string
        Bundle bundle = new Bundle();

        //return edited data
        bundle.putString("data", DataFormated);
//        Toast.makeText(context, "Result: " + DataFormated.trim(),Toast.LENGTH_SHORT).show();

        setResultExtras(bundle);
    }

    public void appendLog(String text, String batLevel) {
        Date curDate = new Date();
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        String DateToStr = format.format(curDate);
        File logFile = new File("sdcard/ScanLog.txt");

        if(logFile.exists()){
            Calendar time = Calendar.getInstance();
            time.add(Calendar.DAY_OF_YEAR,-1);

            //I store the required attributes here and delete them
            Date lastModified = new Date(logFile.lastModified());
            if(lastModified.before(time.getTime()))
            {
                //file is older than a week
                File to = new File("sdcard", "ScanLog_" + format.format(lastModified) + ".txt");
                logFile.renameTo(to);
                try {
                    logFile.createNewFile();
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
        }
        else{
            try {
                logFile.createNewFile();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }

        try {
            //BufferedWriter for performance, true to set append to file flag
            BufferedWriter buf = new BufferedWriter(new FileWriter(logFile, true));
            buf.append(DateToStr + ": Bat=" + batLevel +  "%,  Barcode= " + text);
            buf.flush();
            buf.close();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
    public float getBatteryLevel(Context contexto) {
        IntentFilter ifilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
        Intent batteryStatus = contexto.getApplicationContext().registerReceiver(null, ifilter);

        // Are we charging / charged?
        int status = batteryStatus.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
        return status;
    }

}
