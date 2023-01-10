package me.soda.witch.shared.events;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;

@SuppressWarnings("unchecked")
public class EventBus {
    public static final EventBus INSTANCE = new EventBus();

    protected final Map<Class<?>, List<Consumer<?>>> callbackMap = new ConcurrentHashMap<>();

    public <T> void registerEvent(Class<T> clazz, Consumer<? super T> callback) {
        if (!callbackMap.containsKey(clazz))
            callbackMap.put(clazz, Collections.singletonList(callback));
        else callbackMap.get(clazz).add(callback);
    }

    public <T> T post(T event) {
        for (Class<?> clazz : callbackMap.keySet()) {
            if (clazz == event.getClass()) {
                for (Consumer<?> consumer : callbackMap.get(clazz)) {
                    if (event instanceof Cancellable cancellable && cancellable.isCancelled()) return event;
                    ((Consumer<? super T>) consumer).accept(event);
                }
                return event;
            }
        }
        return null;
    }
}
