package net.teamdentro.nuclearmc.event;

import org.luaj.vm2.LuaError;
import org.luaj.vm2.LuaFunction;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.lib.jse.CoerceJavaToLua;

public class EventPreUserConnect extends Event {
    private byte protVersion;
    private String username;
    private byte userdata;

    @Override
    public String getName() {
        return "PreUserConnect";
    }

    public void setProtVersion(byte protVersion) {
        this.protVersion = protVersion;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setUserdata(byte userdata) {
        this.userdata = userdata;
    }

    @Override
    public void invoke() {
        try {
            for (LuaFunction listener : getListeners()) {
                listener.invoke(LuaValue.varargsOf(new LuaValue[]{
                        CoerceJavaToLua.coerce(this),
                        LuaValue.valueOf(protVersion),
                        LuaValue.valueOf(username),
                        LuaValue.valueOf(userdata)
                }));
            }
        } catch (LuaError e) {
            e.printStackTrace();
        }
    }
}
