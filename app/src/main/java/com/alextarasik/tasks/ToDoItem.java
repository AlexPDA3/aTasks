package com.alextarasik.tasks;

import java.io.Serializable;
import java.util.List;

public class ToDoItem implements Serializable{

    private static final long serialVersionUID = 2008719019880549886L;


    private String name;
    private boolean check;
    private int index; // Позиция в списке
    private List<ToDoItem> list;



    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isCheck() {
        return check;
    }

    public void setCheck(boolean check) {
        this.check = check;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public ToDoItem(String name, int index) {
        setName(name);
        setIndex(index);
        setCheck(false);
    }

    public ToDoItem() {
        setName("");
        setCheck(false);
    }
}