package net.teamdentro.nuclearmc.event;

import net.teamdentro.nuclearmc.User;
import org.luaj.vm2.LuaFunction;
import org.luaj.vm2.lib.jse.CoerceJavaToLua;

/**
 * Created by Lignum on 19/04/2015.
 */
public class EventUserDisconnect extends Event {
    private User user;

    @Override
    public String getName() {
        return "UserDisconnect";
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    @Override
    public void invoke() {
        for (LuaFunction listener : getListeners()) {
            listener.call(CoerceJavaToLua.coerce(this), CoerceJavaToLua.coerce(user));
        }
    }
}
