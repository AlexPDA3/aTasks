package com.alextarasik.tasks;

import android.app.ProgressDialog;
import android.os.AsyncTask;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;

import static com.alextarasik.tasks.MainActivity.USDCourseTV;

class ParceCourses extends AsyncTask<Void, Void, Void> {

    public static String course; // Курс доллара

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    public Void doInBackground(Void... arg) {

        Document document = null;
        try{
            document= Jsoup.connect("http://www.nbrb.by/api/exrates/rates/145").get();
            Elements content = document.select("div.line-content");
            course = content.toString();
        } catch (IOException e){
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public void onPostExecute (Void result){
        USDCourseTV.setText(R.string.USD_Course+course);
    }
}
