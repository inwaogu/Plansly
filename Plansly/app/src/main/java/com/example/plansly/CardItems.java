package com.example.plansly;

public class CardItems {
    private String time;
    private String task;
    private String note;

    public CardItems(String time, String task, String note)
    {
        this.time = time;
        this.task = task;
        this.note = note;
    }

    public String getTime()
    {
        return time;
    }

    public String getTask()
    {
        return task;
    }

    public String getNote()
    {
        return note;
    }
}
