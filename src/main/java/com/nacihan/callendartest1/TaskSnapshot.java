package com.nacihan.callendartest1;

import java.time.LocalDate;

public class TaskSnapshot {
    int taskId;
    String title;
    String note;
    LocalDate date;

    public TaskSnapshot(int taskId, String title, String note, LocalDate date) {
        this.taskId = taskId;
        this.title = title;
        this.note = note;
        this.date = date;
    }
}
