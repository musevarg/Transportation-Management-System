package com.aquarian.drivers.util;

import android.content.Context;
import android.util.Log;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;

public class SaveData
{

    public SaveData(){}

    public static void write(String filename, String outputString, Context c)
    {

        try {
            FileOutputStream outputStream = c.openFileOutput(filename, Context.MODE_PRIVATE);
            outputStream.write(outputString.getBytes());
            outputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public static String content;

    public static void read(String filename, Context c)
    {
        try {
            FileInputStream inputStream = c.openFileInput(filename);
            BufferedReader r = new BufferedReader(new InputStreamReader(inputStream));
            StringBuilder total = new StringBuilder();
            String line;
            while ((line = r.readLine()) != null) {
                total.append(line);
            }
            r.close();
            inputStream.close();
            Log.d("File", "File contents: " + total);
            content = total.toString();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void delete(String filename, Context c)
    {
        try
        {
            c.deleteFile(filename);
            Log.d("FILE DELETED","File " + filename + " deleted.");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}
