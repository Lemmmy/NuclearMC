package net.teamdentro.nuclearmc.event;

import net.teamdentro.nuclearmc.NuclearMC;
import net.teamdentro.nuclearmc.User;
import org.luaj.vm2.LuaError;
import org.luaj.vm2.LuaFunction;
import org.luaj.vm2.LuaTable;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.lib.LibFunction;
import org.luaj.vm2.lib.jse.CoerceJavaToLua;

public class EventPostUserConnect extends Event {
    private User user;

    @Override
    public String getName() {
        return "PostUserConnect";
    }

    public void setUser(User user) {
        this.user = user;
    }

    @Override
    public void invoke() {
        try {
            for (LuaFunction listener : getListeners()) {
                listener.invoke(LuaValue.varargsOf(new LuaValue[]{
                        CoerceJavaToLua.coerce(this),
                        CoerceJavaToLua.coerce(user)
                }));
            }
        } catch (LuaError e) {
            e.printStackTrace();
        }
    }
}
