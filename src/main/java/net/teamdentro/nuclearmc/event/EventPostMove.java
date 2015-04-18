package net.teamdentro.nuclearmc.event;

import net.teamdentro.nuclearmc.User;
import net.teamdentro.nuclearmc.util.Position;
import org.luaj.vm2.LuaError;
import org.luaj.vm2.LuaFunction;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.lib.jse.CoerceJavaToLua;

public class EventPostMove extends Event {
    private User user;
    private Position position;

    @Override
    public String getName() {
        return "PostMove";
    }

    public void setUser(User user) {
        this.user = user;
    }

    public void setPosition(Position position) {
        this.position = position;
    }

    @Override
    public void invoke() {
        try {
            for (LuaFunction listener : getListeners()) {
                listener.invoke(LuaValue.varargsOf(new LuaValue[]{
                        CoerceJavaToLua.coerce(this),
                        CoerceJavaToLua.coerce(user),
                        CoerceJavaToLua.coerce(position)
                }));
            }
        } catch (LuaError e) {
            e.printStackTrace();
        }
    }
}
