package net.teamdentro.nuclearmc.event;

import org.luaj.vm2.LuaError;
import org.luaj.vm2.LuaFunction;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public abstract class Event {
    private static Map<String, Class<? extends Event>> events = new HashMap<>();
    private static Map<String, ArrayList<LuaFunction>> listeners = new HashMap<>();
    private boolean cancelled;

    static {
        registerEvent(EventPostMove.class);
        registerEvent(EventSetBlock.class);
        registerEvent(EventPostUserConnect.class);
        registerEvent(EventPreMove.class);
        registerEvent(EventSetBlock.class);
        registerEvent(EventPreUserConnect.class);
        registerEvent(EventUserMessage.class);
    }

    public static void registerEvent(Class<? extends Event> event) {
        Event e;
        try {
            e = event.newInstance();
            events.put(e.getName(), event);
            listeners.put(e.getName(), new ArrayList<LuaFunction>());
        } catch (Exception er) {
            er.printStackTrace();
        }
    }

    public static void addListener(String event, LuaFunction listener) {
        if (!listeners.containsKey(event)) {
            throw new LuaError("no such event type");
        }

        listeners.get(event).add(listener);
    }

    public abstract String getName();
    public abstract void invoke();

    public boolean isCancelled() {
        return cancelled;
    }

    public void setCancelled(boolean cancelled) {
        this.cancelled = cancelled;
    }

    public ArrayList<LuaFunction> getListeners() {
        return listeners.get(getName());
    }
}
