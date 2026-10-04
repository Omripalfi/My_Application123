package com.example.myapplication;

import android.content.Context;
import android.content.SharedPreferences;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;

public class TaskStorage {
    private static final String PREF_NAME = "TasksPrefs";
    private static final String KEY_TASKS = "tasks_list";
    private SharedPreferences prefs;
    private Gson gson;

    public TaskStorage(Context context) {
        prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);

        GsonBuilder builder = new GsonBuilder();
        builder.registerTypeAdapter(Task.class, new TaskAdapterGson());
        gson = builder.create();
    }

    private static class TaskAdapterGson implements JsonSerializer<Task>, JsonDeserializer<Task> {
        @Override
        public JsonElement serialize(Task src, Type typeOfSrc, JsonSerializationContext context) {
            JsonObject jsonObj = context.serialize(src, src.getClass()).getAsJsonObject();
            jsonObj.addProperty("task_type_class", src.getClass().getName());
            return jsonObj;
        }

        @Override
        public Task deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            JsonObject jsonObj = json.getAsJsonObject();
            if (jsonObj.has("task_type_class")) {
                String className = jsonObj.get("task_type_class").getAsString();
                try {
                    Class<?> clz = Class.forName(className);
                    return context.deserialize(json, clz);
                } catch (ClassNotFoundException ignored) {}
            }
            if (jsonObj.has("exercises")) {
                return context.deserialize(json, HomeworkTask.class);
            } else {
                return context.deserialize(json, ExamTask.class);
            }
        }
    }

    public ArrayList<Task> loadAll() {
        String json = prefs.getString(KEY_TASKS, null);
        if (json == null) {
            return new ArrayList<>();
        }
        Type type = new TypeToken<ArrayList<Task>>() {}.getType();
        ArrayList<Task> list = gson.fromJson(json, type);
        return list != null ? list : new ArrayList<>();
    }

    public void saveAll(ArrayList<Task> tasks) {
        String json = gson.toJson(tasks);
        prefs.edit().putString(KEY_TASKS, json).apply();
    }

    public void addTask(Task task) {
        ArrayList<Task> tasks = loadAll();
        tasks.add(task);
        saveAll(tasks);
    }

    public Task findById(int id) {
        for (Task t : loadAll()) {
            if (t.getId() == id) return t;
        }
        return null;
    }

    public void updateTask(Task task) {
        ArrayList<Task> tasks = loadAll();
        for (int i = 0; i < tasks.size(); i++) {
            if (tasks.get(i).getId() == task.getId()) {
                tasks.set(i, task);
                break;
            }
        }
        saveAll(tasks);
    }

    public void deleteById(int id) {
        ArrayList<Task> tasks = loadAll();
        for (int i = 0; i < tasks.size(); i++) {
            if (tasks.get(i).getId() == id) {
                tasks.remove(i);
                break;
            }
        }
        saveAll(tasks);
    }

    public int nextId() {
        ArrayList<Task> tasks = loadAll();
        int maxId = 0;
        for (Task t : tasks) {
            if (t.getId() > maxId) {
                maxId = t.getId();
            }
        }
        return maxId + 1;
    }

    public void clearAllData() {
        prefs.edit().clear().apply();
    }
}
