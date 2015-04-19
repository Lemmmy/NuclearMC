package net.teamdentro.nuclearmc.event;

import org.luaj.vm2.LuaFunction;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.lib.jse.CoerceJavaToLua;

/**
 * Created by Lignum on 19/04/2015.
 */
public class EventTick extends Event {
    private float dt;

    @Override
    public String getName() {
        return "Tick";
    }

    public float getDt() {
        return dt;
    }

    public void setDt(float dt) {
        this.dt = dt;
    }

    @Override
    public void invoke() {
        for (LuaFunction listener : getListeners()) {
            listener.call(CoerceJavaToLua.coerce(this), LuaValue.valueOf(dt));
        }
    }
}
