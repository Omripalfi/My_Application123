package com.example.myapplication;

public class HomeworkTask extends Task {
    private int exercises;

    public HomeworkTask(int id, String title, String subject, String priority, String dueDate, int exercises) {
        super(id, title, subject, priority, dueDate);
        this.exercises = exercises;
    }

    @Override
    public String getTypeName() {
        return "שיעורי בית";
    }

    @Override
    public int getPoints() {
        return (exercises * 2) + getPriorityBonus();
    }

    public int getExercises() {
        return exercises;
    }
}
