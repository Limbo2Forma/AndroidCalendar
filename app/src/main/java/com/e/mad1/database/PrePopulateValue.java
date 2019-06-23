package com.e.mad1.database;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Log;

import com.e.mad1.database.Dao.EventDao;
import com.e.mad1.database.Entity.EventEntity;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;

class PrePopulateValue {
    private Context context;

    PrePopulateValue(Context context){
        this.context = context;
    }

    private EventEntity fillEvent(String string){
        StringBuilder stringBuilder = new StringBuilder(string);
        stringBuilder.deleteCharAt(string.length()-1);
        stringBuilder.deleteCharAt(0);
        String[] elements = stringBuilder.toString().split("\",\"");
        for (String s:elements){
            Log.i("database","Array " + s);
        }
        @SuppressLint("SimpleDateFormat")
        SimpleDateFormat dateFormat = new SimpleDateFormat("d/MM/yyyy h:mm:ss a");
        EventEntity e = new EventEntity(elements[1], dateFormat.parse(elements[2],new ParsePosition(0)),
                dateFormat.parse(elements[3],new ParsePosition(0)), elements[4],elements[5]);
        e.setId(Long.parseLong(elements[0]));

        return e;
    }

    void populateEvents(EventDao eventDao){
        BufferedReader reader;
        try{
            final InputStream file = context.getAssets().open("events.txt");
            reader = new BufferedReader(new InputStreamReader(file));
            String line = reader.readLine();
            while(line != null){
                Log.d("populate event", line);
                line = reader.readLine();
                if (line == null) { break; }
                if (line.charAt(0) == '"') {
                    Log.i("database","add event " + line);
                    eventDao.addNewEvent(fillEvent(line));
                }
            }
        } catch(IOException ioe){
            ioe.printStackTrace();
        }
    }
}
