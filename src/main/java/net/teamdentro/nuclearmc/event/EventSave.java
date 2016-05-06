package net.teamdentro.nuclearmc.event;

import org.luaj.vm2.LuaFunction;

public class EventSave extends Event {
    @Override
    public String getName() {
        return "Save";
    }

    @Override
    public void invoke() {
        for (LuaFunction listener : getListeners()) {
            listener.invoke();
        }
    }
}
