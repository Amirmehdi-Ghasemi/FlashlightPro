package com.example.flashlightpro;

import android.content.Context;
import android.util.Log;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;

public class Language {
    private final String FILE_NAME = "language.txt";
    Context context;
    private String languageType = "";

    public boolean checkUp(){
        load();
        if (languageType.equals("")) setLanguageType("fa");
        return language(languageType);
    }
    public Boolean language(String language) {
        Log.i("inside language method", language);
        if (language.equals("fa")){
            return true;
        } else {
            return false;
        }
    }
    public void change(Boolean checked){
        if (checked) {
            languageType = "fa";
        } else {
            languageType = "en";
        }
        //language(languageType);
        save(languageType);
    }

    public void save(String language){
        FileOutputStream fos = null;
        try {
            fos = context.openFileOutput(FILE_NAME, context.MODE_PRIVATE);
            fos.write(language.getBytes());

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (fos != null) {
                try {
                    fos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
    public void load(){
        FileInputStream fis = null;
        try {
            fis = context.openFileInput(FILE_NAME);
            InputStreamReader isr = new InputStreamReader(fis);
            BufferedReader br = new BufferedReader(isr);
            languageType = br.readLine();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (fis != null) {
                try {
                    fis.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }



    public void setLanguageType(String languageType) {
        this.languageType = languageType;
    }

    public void setContext(Context context) {
        this.context = context;
    }
}
