package me.soda.witch.shared;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;

@SuppressWarnings("unchecked")
public class EventBus {
    public static final EventBus INSTANCE = new EventBus();

    protected final Map<Class<?>, List<Consumer<Object>>> callbackMap = new ConcurrentHashMap<>();

    public <T> void registerEvent(Class<T> clazz, Consumer<T> callback) {
        if (!callbackMap.containsKey(clazz))
            callbackMap.put(clazz, Collections.singletonList((Consumer<Object>) callback));
        else callbackMap.get(clazz).add((Consumer<Object>) callback);
    }

    public <T> T post(T event) {
        for (Class<?> clazz : callbackMap.keySet()) {
            if (clazz == event.getClass())
                for (Consumer<Object> consumer : callbackMap.get(clazz)) {
                    consumer.accept(event);
                    return event;
                }
        }
        return null;
    }
}