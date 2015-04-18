package net.teamdentro.nuclearmc.plugin;

import net.teamdentro.nuclearmc.event.Event;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.lib.OneArgFunction;
import org.luaj.vm2.lib.TwoArgFunction;

public class EventLib extends OneArgFunction {
    @Override
    public LuaValue call(LuaValue env) {
        LuaValue lib = tableOf();

        lib.set("addListener", new AddListenerFunc());

        return lib;
    }

    private class AddListenerFunc extends TwoArgFunction {
        @Override
        public LuaValue call(LuaValue eventType, LuaValue func) {
            if (eventType.isstring() && func.isfunction()) {
                Event.addListener(eventType.tojstring(), func.checkfunction());
            }

            return null;
        }
    }
}
