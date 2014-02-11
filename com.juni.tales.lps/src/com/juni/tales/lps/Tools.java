package com.juni.tales.lps;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;

import android.content.Context;

public class Tools {

	public Tools() {
		// TODO Auto-generated constructor stub
	}

	 public HashMap<Integer,Integer> readRawTextFile(Context ctx, int resId, HashMap<Integer,Integer> num)
     {
          InputStream inputStream = ctx.getResources().openRawResource(resId);

             InputStreamReader inputreader = new InputStreamReader(inputStream);
             BufferedReader buffreader = new BufferedReader(inputreader);
             String line;

             try {
                while (( line = buffreader.readLine()) != null) {
                	String[] lines = line.split(";");
                    int position = Integer.valueOf(lines[0]);
                    int scene = Integer.valueOf(lines[1]);
                    num.put(position, scene);
                  }
                return num;
             } catch (IOException e) {
            	e.printStackTrace();
            }
            return num;
     }
	
}
