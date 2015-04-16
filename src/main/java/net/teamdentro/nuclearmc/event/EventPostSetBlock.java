package net.teamdentro.nuclearmc.event;

import net.teamdentro.nuclearmc.Blocks;
import net.teamdentro.nuclearmc.User;
import org.luaj.vm2.LuaError;
import org.luaj.vm2.LuaFunction;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.lib.jse.CoerceJavaToLua;

public class EventPostSetBlock extends Event {
    private User user;
    private short posx,posy,posz;
    private byte mode;
    private Blocks block;

    static {
        registerEvent(EventPostSetBlock.class);
    }

    @Override
    public String getName() {
        return "PostSetBlock";
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
                        CoerceJavaToLua.coerce(block)
                }));
            }
        } catch (LuaError e) {
            e.printStackTrace();
        }
    }
}
