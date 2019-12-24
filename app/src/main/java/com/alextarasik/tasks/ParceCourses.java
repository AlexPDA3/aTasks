package com.alextarasik.tasks;

import android.os.AsyncTask;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.IOException;

class ParceCourses extends AsyncTask<String, Void, String> {

    public static String course; // Курс доллара
    Document doc;

    @Override
    protected String doInBackground(String... arg) {
        try{
            doc = Jsoup.connect("http://www.nbrb.by/api/exrates/rates/145").get();
        } catch (IOException e){
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected void onPostExecute (String result){
        course = doc.text();
    }
}
