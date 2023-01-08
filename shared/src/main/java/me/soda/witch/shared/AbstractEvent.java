package me.soda.witch.shared;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class AbstractEvent<T> {
    protected final Map<Integer, T> callbackMap = new HashMap<>();
    private final List<Integer> pendingUnregisters = new ArrayList<>();
    private int id = 0;

    public synchronized void registerEvent(T callback) {
        id++;
        callbackMap.put(id, callback);
    }

    public synchronized void unregisterEvent(int id) {
        pendingUnregisters.add(id);
    }

    public synchronized void post(Object... args) {
        callbackMap.keySet().forEach(id -> run(id, callbackMap.get(id), args));
        pendingUnregisters.forEach(callbackMap::remove);
    }

    public abstract void run(int id, T callback, Object... args);
}
