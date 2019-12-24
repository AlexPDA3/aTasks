package com.alextarasik.tasks;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.graphics.Paint;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

public class CustomListAdapter extends BaseAdapter {

    private LayoutInflater inflater_;
    private List<ToDoItem> list = new ArrayList<ToDoItem>();
    private Context context;

    public CustomListAdapter(List<ToDoItem> list, Context context)
    {
        this.list = list;
        inflater_ = LayoutInflater.from(context);
        this.context = context;
    }
    @Override
    public int getCount() {
        return this.list.size();
    }

    @Override
    public Object getItem(int position) {
        return this.list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public void setList(List<ToDoItem> list)
    {
        this.list = list;
    }

    @Override
    public View getView(final int position, View view, ViewGroup parent) {
        View v = view;
        if (view == null)
            v = inflater_.inflate(R.layout.listitem,
                    parent, false);

        v.setVisibility(View.VISIBLE);

        // Получение айтема из листа
        final ToDoItem item = list.get(position);

        // Установка названия дела
        TextView listitem_name = (TextView)v.findViewById(R.id.listitem_name);
        listitem_name.setText(item.getIndex() + ". " + item.getName());


        if (item.isCheck())
            listitem_name.setPaintFlags(Paint.ANTI_ALIAS_FLAG | Paint.STRIKE_THRU_TEXT_FLAG);
        else
            listitem_name.setPaintFlags(Paint.ANTI_ALIAS_FLAG);


        CheckBox listitem_check = (CheckBox)v.findViewById(R.id.listitem_check);
        listitem_check.setChecked(item.isCheck());
        listitem_check.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                Message msg = new Message();
                msg.arg1 = position;
                msg.what = MainActivity.MSG_CHANGE_ITEM;
                ((MainActivity)context).getHandler().sendMessage(msg);

            }
        });

        return v;
    }
}
