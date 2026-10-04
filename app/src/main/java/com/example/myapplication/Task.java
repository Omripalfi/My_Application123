package com.example.myapplication;

public abstract class Task implements Rewardable {
    private int id;
    private String title;
    private String subject;
    private String priority;
    private String dueDate;
    private boolean done;

    public Task(int id, String title, String subject, String priority, String dueDate) {
        this.id = id;
        this.title = title;
        this.subject = subject;
        this.priority = priority;
        this.dueDate = dueDate;
        this.done = false;
    }

    public abstract String getTypeName();

    protected int getPriorityBonus() {
        if (priority == null) return 0;
        switch (priority) {
            case "גבוהה": return 5;
            case "בינונית": return 3;
            case "נמוכה": return 1;
            default: return 0;
        }
    }

    public int getId() { return id; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getSubject() { return subject; }
    public String getPriority() { return priority; }
    public String getDueDate() { return dueDate; }
    public boolean isDone() { return done; }
    public void setDone(boolean done) { this.done = done; }

    @Override
    public String toString() {
        return title + " | " + subject + " | " + dueDate;
    }
}
