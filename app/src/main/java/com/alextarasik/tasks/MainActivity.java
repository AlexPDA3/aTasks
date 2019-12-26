package com.alextarasik.tasks;


import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ContextMenu.ContextMenuInfo;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.TextView;


import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.android.material.bottomappbar.BottomAppBar;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import static com.alextarasik.tasks.ParceCourses.course;

public class MainActivity extends AppCompatActivity {

    // Сообщения для Handler'а
    public static final int MSG_UPDATE_ADAPTER 		= 0;
    public static final int MSG_CHANGE_ITEM 		= 1;

    // Сообщения для контекстного меню
    public static final int MSG_REMOVE_ITEM 		= 10;
    public static final int MSG_RENAME_ITEM 		= 11;

    private List<ToDoItem> list = new ArrayList<ToDoItem>();
    private CustomListAdapter adapter;
    private ListView listview;
    private Toolbar toolbar;
    private BottomAppBar bottomAppBar;
    private FloatingActionButton fabButton;
    public static TextView USDCourseTV;

    private View footer;

    private Handler handler = new Handler() {
        public void handleMessage(Message msg) {

            switch (msg.what)
            {
                case MSG_UPDATE_ADAPTER: // Обновление ListView
                    adapter.notifyDataSetChanged();
                    setCountPurchaseProduct();
                    break;
                case MSG_CHANGE_ITEM: // Сделано / Не сделано дело
                    ToDoItem item = list.get(msg.arg1);
                    item.setCheck(!item.isCheck());
                    Utils.sorting(list, 0);
                    saveList();
                    adapter.notifyDataSetChanged();
                    setCountPurchaseProduct();
                    break;
            }
        }
    };

    public Handler getHandler()
    {
        return handler;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        /*toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);*/

        bottomAppBar = findViewById(R.id.bottom_app_bar);
        setSupportActionBar(bottomAppBar);

        //initList();
        loadList(); // Загрузка списка из кеша

        listview = (ListView)findViewById(R.id.listview);

        fabButton = (FloatingActionButton) findViewById(R.id.fab);
        fabButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                showAddToDoItemDialog();
            }
        });

        ParceCourses parceCourses = new ParceCourses();// Вывод курса доллара

        USDCourseTV =(TextView)findViewById(R.id.USDCourse);

		/**
		 Добавление header'a и footer'а к ListView
		 */

        View header = LayoutInflater.from(this).inflate(R.layout.list_header, null, false);
        ((TextView)header.findViewById(R.id.header_title)).setText(R.string.txt_header_title);
        listview.addHeaderView(header);

        footer = LayoutInflater.from(this).inflate(R.layout.list_footer, null, false);
        listview.addFooterView(footer);

        adapter = new CustomListAdapter(list, this);
        setCountPurchaseProduct(); // Расчет сделанных дел
        listview.setAdapter(adapter);

        registerForContextMenu(listview);

        listview.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position,
                                    long id) {
                if (position == 0 || position == list.size() + 1)
                    return;
                Message msg = new Message();
                msg.arg1 = position - 1;
                msg.what = MSG_CHANGE_ITEM;
                handler.sendMessage(msg);
            }
        });

        Utils.setList(listview, this);
        Utils.sorting(list, 0);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    /**
     * Создание основного меню
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    /**
     * Обработка нажатия на айтем основного меню
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id)
        {
            case R.id.menu_item_add:
                showAddToDoItemDialog();
                return true;
            case R.id.menu_item_remove_all:
                removeAll();
                return true;
            case R.id.menu_item_check_all:
                setCheckAll(true);
                return true;
            case R.id.menu_item_uncheck_all:
                setCheckAll(false);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    /**
     * Создание контекстного меню для ListView
     */
    @Override
    public void onCreateContextMenu(ContextMenu menu, View v,
                                    ContextMenuInfo menuInfo) {
        int index = ((AdapterView.AdapterContextMenuInfo)menuInfo).position;
        if (index == list.size() + 1 || index == 0)
            return;
        super.onCreateContextMenu(menu, v, menuInfo);
        menu.add(0, MSG_REMOVE_ITEM, Menu.NONE, R.string.menu_item_remove);
        menu.add(0, MSG_RENAME_ITEM, Menu.NONE, R.string.menu_item_rename);
    }

    /**
     * Обработка нажатия на айтем контекстного меню
     */
    @Override
    public boolean onContextItemSelected(MenuItem item) {

        super.onContextItemSelected(item);
        AdapterView.AdapterContextMenuInfo menuInfo = (AdapterView.AdapterContextMenuInfo)item.getMenuInfo();
        int index = menuInfo.position - 1;

        switch (item.getItemId())
        {
            case MSG_REMOVE_ITEM:  // Удалить айтем
                removeItem(index);
                return true;
            case MSG_RENAME_ITEM:	// Переименовать айтем
                showRenameDialog(index);
                return true;
        }
        return false;
    }

    /**
     * Открыть диалоговое окно для переименования айтема
     */
    private void showRenameDialog(final int index)
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(getString(R.string.menu_item_rename));
        final Context context = this;
        final EditText input = new EditText(this);
        input.setText(list.get(index).getName());
        builder.setView(input);

        builder.setPositiveButton(getString(R.string.btn_yes), new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int whichButton) {
                String value = input.getText().toString();
                if (value.equals(""))
                    Toast.makeText(context, getString(R.string.msg_empty), Toast.LENGTH_SHORT).show();
                else
                {
                    list.get(index).setName(value);
                    saveList();
                    getHandler().sendEmptyMessage(MainActivity.MSG_UPDATE_ADAPTER);
                }

            }
        });

        builder.setNegativeButton(getString(R.string.btn_no), new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                return;
            }
        });
        AlertDialog alert = builder.create();
        alert.show();
    }

    /**
     * Открыть диалоговое окно для добавления айтема
     */
    private void showAddToDoItemDialog()
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(getString(R.string.menu_item_add));
        final Context context = this;
        final EditText input = new EditText(this);
        builder.setView(input);

        builder.setPositiveButton(getString(R.string.btn_yes), new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int whichButton) {
                String value = input.getText().toString();
                if (value.equals(""))
                    Toast.makeText(context, getString(R.string.msg_empty), Toast.LENGTH_SHORT).show();
                else
                {
                    list.add(new ToDoItem(value, getIndexFromList() + 1));
                    Utils.sorting(list, 0);
                    //saveList();
                    getHandler().sendEmptyMessage(MainActivity.MSG_UPDATE_ADAPTER);
                    saveList();
                }

            }
        });

        builder.setNegativeButton(getString(R.string.btn_no), new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                return;
            }
        });
        AlertDialog alert = builder.create();
        alert.show();
    }

    /**
     * Удаление айтема из списка
     */
    private void removeItem(int index) {
        list.remove(index);
        reindexList();
        saveList();
        getHandler().sendEmptyMessage(MainActivity.MSG_UPDATE_ADAPTER);
    }

    /**
     * Удаление всех айтемов из списка
     */
    private void removeAll() {
        list.clear();
        saveList();
        getHandler().sendEmptyMessage(MainActivity.MSG_UPDATE_ADAPTER);
    }

    /**
     * Сделать/Не сделать все дела
     */
    private void setCheckAll(boolean check)
    {
        Iterator<ToDoItem> it = list.iterator();
        while (it.hasNext())
        {
            ToDoItem item = it.next();
            item.setCheck(check);
        }
        Utils.sorting(list, 0);
        saveList();
        getHandler().sendEmptyMessage(MainActivity.MSG_UPDATE_ADAPTER);
    }

    /**
     * Получение последнего индекса в списке
     */
    private int getIndexFromList()
    {
        int index = 0;
        Iterator<ToDoItem> it = list.iterator();
        while (it.hasNext())
        {
            ToDoItem item = it.next();
            if (item.getIndex() > index)
                index = item.getIndex();
        }
        return index;
    }

    /**
     * Перераспределить индексы для айтемов в списке (от меньшего к большему)
     */
    private void reindexList()
    {
        int index = 1;
        Utils.sorting(list, 1);
        Iterator<ToDoItem> it = list.iterator();
        while (it.hasNext())
        {
            ToDoItem item = it.next();
            item.setIndex(index);
            index++;
        }
        Utils.sorting(list, 0);
    }

    /**
     * Инициализация списка для тестирования
     */
    @SuppressWarnings("unused")
   /* private void initList()
    {
        for (int i = 1; i <= 10; i++)
            list.add(new ToDoItem("ToDo Item " + i, i));
    }*/

    /**
     * Сохранить список
     */
    public synchronized void saveList() {
        try {
            FileOutputStream fos = openFileOutput("aTaskData.txt",MODE_PRIVATE);
            System.out.println("Файл создан!");
            ObjectOutputStream out = new ObjectOutputStream(fos);
            out.writeObject(list);
            out.close();
            System.out.println("SaveList сработал");
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("SaveList не сработал");
        }
    }

    /**
     * Загрузить список
     */
    @SuppressWarnings("unchecked")
    public boolean loadList() {
        try {
            FileInputStream fos = openFileInput("aTaskData.txt");
            ObjectInputStream in = new ObjectInputStream(fos);
            list = (List<ToDoItem>) in.readObject();
            in.close();
            System.out.println("LoadList сработал");
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("LoadList не сработал");
            return false;
        }
    }

    /**
     * Рассчитать количество сделанных дел
     */
    private void setCountPurchaseProduct()
    {
        int count = 0;
        Iterator<ToDoItem> it = list.iterator();
        while (it.hasNext())
        {
            ToDoItem item = it.next();
            if (item.isCheck())
                count++;
        }

        ((TextView)footer.findViewById(R.id.footer_title)).setText(String.format(getString(R.string.txt_footer_title), count));
    }

}

