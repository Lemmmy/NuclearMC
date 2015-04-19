package net.teamdentro.nuclearmc.event;

import net.teamdentro.nuclearmc.User;
import org.luaj.vm2.LuaError;
import org.luaj.vm2.LuaFunction;
import org.luaj.vm2.LuaString;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.lib.jse.CoerceJavaToLua;

public class EventUserMessage extends Event {
    private User user;
    private String message;

    @Override
    public String getName() {
        return "UserMessage";
    }

    public void setUser(User user) {
        this.user = user;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    @Override
    public void invoke() {
        try {
            for (int i = 0; i < getListeners().size(); ++i) {
                LuaFunction listener = getListeners().get(i);
                listener.invoke(LuaValue.varargsOf(new LuaValue[]{
                        CoerceJavaToLua.coerce(this),
                        CoerceJavaToLua.coerce(user),
                        LuaString.valueOf(message)
                }));
            }
        } catch (LuaError e) {
            e.printStackTrace();
        }
    }
}
