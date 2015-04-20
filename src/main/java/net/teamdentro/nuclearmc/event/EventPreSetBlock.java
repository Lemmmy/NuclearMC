package net.teamdentro.nuclearmc.event;

import net.teamdentro.nuclearmc.Blocks;
import net.teamdentro.nuclearmc.User;
import net.teamdentro.nuclearmc.plugin.PluginGlobals;
import org.luaj.vm2.LuaError;
import org.luaj.vm2.LuaFunction;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.lib.jse.CoerceJavaToLua;

public class EventPreSetBlock extends Event {
    private User user;
    private short posx,posy,posz;
    private byte mode;
    private Blocks block;
    private Blocks oldBlock;

    @Override
    public String getName() {
        return "PreSetBlock";
    }

    public void setUser(User user) {
        this.user = user;
    }

    public void setPosx(short posx) {
        this.posx = posx;
    }

    public void setPosy(short posy) {
        this.posy = posy;
    }

    public void setPosz(short posz) {
        this.posz = posz;
    }

    public void setMode(byte mode) {
        this.mode = mode;
    }

    public void setBlock(Blocks block) {
        this.block = block;
    }

    public Blocks getOldBlock() {
        return oldBlock;
    }

    public void setOldBlock(Blocks oldBlock) {
        this.oldBlock = oldBlock;
    }

    @Override
    public void invoke() {
        try {
            for (LuaFunction listener : getListeners()) {
                listener.invoke(LuaValue.varargsOf(new LuaValue[]{
                        CoerceJavaToLua.coerce(this),
                        CoerceJavaToLua.coerce(user),
                        LuaValue.valueOf(posx),
                        LuaValue.valueOf(posy),
                        LuaValue.valueOf(posz),
                        LuaValue.valueOf(mode),
                        PluginGlobals.getLuaBlock(block),
                        PluginGlobals.getLuaBlock(oldBlock)
                }));
            }
        } catch (LuaError e) {
            e.printStackTrace();
        }
    }
}
