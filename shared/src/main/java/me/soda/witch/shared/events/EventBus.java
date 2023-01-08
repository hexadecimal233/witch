package me.soda.witch.shared.events;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;

public class EventBus {
    public static final EventBus INSTANCE = new EventBus();

    protected final Map<Class<?>, List<Consumer<Object>>> callbackMap = new ConcurrentHashMap<>();

    public <T> void registerEvent(Class<T> clazz, Consumer<T> callback) {
        if (!callbackMap.containsKey(clazz))
            callbackMap.put(clazz, Collections.singletonList((Consumer<Object>) callback));
        else callbackMap.get(clazz).add((Consumer<Object>) callback);
    }

    public void unregisterEvent(Consumer<Object> consumer) {
        for (Class<?> clazz : callbackMap.keySet()) {
            for (Consumer<Object> consumer1 : callbackMap.get(clazz)) {
                if (consumer1 == consumer) callbackMap.get(clazz).remove(consumer);
                break;
            }
        }
    }

    public <T> void post(T event) {
        for (Class<?> clazz : callbackMap.keySet()) {
            if (clazz == event.getClass())
                for (Consumer<Object> consumer : callbackMap.get(clazz)) {
                    consumer.accept(event);
                }
        }
    }
}
