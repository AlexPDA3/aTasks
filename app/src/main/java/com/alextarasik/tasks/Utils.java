package com.alextarasik.tasks;

import java.io.File;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.os.Environment;
import android.widget.ListView;

public class Utils {


    /**
     * Убирает полосу разделения у элементов списка.
     */
    public static void setList(ListView list, Context context) {
        list.setSelector(android.R.color.transparent);
        ColorDrawable sage = new ColorDrawable(context.getResources().getColor(
                android.R.color.transparent));
        list.setDivider(sage);
        list.setDividerHeight(0);
    }

    /**
     * @param type 0 - сортировка по галочке
     * @param type 1 - сортировка по индексу (от меньшего к большему)
     */
    public static void sorting(List<ToDoItem> list, final int type)
    {
        Collections.sort(list, new Comparator<ToDoItem>() {

            @Override
            public int compare(ToDoItem item1, ToDoItem item2) {
                int compare = 0;
                switch (type)
                {
                    case 0:
                        Boolean bool_value1 = Boolean.valueOf(item1.isCheck());
                        Boolean bool_value2 = Boolean.valueOf(item2.isCheck());
                        compare = bool_value1.compareTo(bool_value2);
                        if (compare == 0)
                            compare = (item1.getIndex() > item2.getIndex()) ? 1 : -1;
                        break;
                    case 1:
                        Integer int_value1 = Integer.valueOf(item1.getIndex());
                        Integer int_value2 = Integer.valueOf(item2.getIndex());
                        compare = int_value1.compareTo(int_value2);
                        break;
                }
                return compare;
            }
        });
    }
}
