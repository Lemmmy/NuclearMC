package net.teamdentro.nuclearmc.event;

import org.luaj.vm2.LuaFunction;
import org.luaj.vm2.LuaTable;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.lib.LibFunction;
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
        for (LuaFunction listener : getListeners()) {
            LuaTable args = new LuaTable();
            args.set("event", CoerceJavaToLua.coerce(this));
            args.set("protVersion", LuaValue.valueOf(protVersion));
            args.set("username", LuaValue.valueOf(username));
            args.set("userdata", LuaValue.valueOf(userdata));

            listener.call(args);
        }
    }
}
