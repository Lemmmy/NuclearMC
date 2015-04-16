package net.teamdentro.nuclearmc.plugin;

import net.teamdentro.nuclearmc.Server;
import net.teamdentro.nuclearmc.User;
import org.luaj.vm2.LuaString;
import org.luaj.vm2.LuaTable;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.lib.OneArgFunction;
import org.luaj.vm2.lib.TwoArgFunction;
import org.luaj.vm2.lib.ZeroArgFunction;
import org.luaj.vm2.lib.jse.CoerceJavaToLua;

public class ServerLib extends OneArgFunction {
    @Override
    public LuaValue call(LuaValue env) {
        LuaValue lib = tableOf();

        lib.set("getConfig", new ConfigLib.GetConfigFunc());
        lib.set("broadcast", new BroadcastFunc());
        lib.set("isRunning", new DoingARunnerFunc());
        lib.set("getServerName", new WhatAmIFeckingCalled());
        lib.set("getMotd", new GetInspirationalQuoteOfToday());
        lib.set("getOnlineUsers", new WhosFeckinHereImBlindLikeBusLadySoICantSeeWhosActuallyHereForFecksSake());
        lib.set("kickPlayer", new KickTheFeckinPlayer());
        lib.set("disconnectPlayer", new TellThemToFeckOff());
        lib.set("closeServer", new CloseServerFunc());

        return lib;
    }

    private class BroadcastFunc extends OneArgFunction {
        @Override
        public LuaValue call(LuaValue msg) {
            Server.instance.broadcastMessage(msg.tojstring());

            return null;
        }
    }

    private class DoingARunnerFunc extends ZeroArgFunction {
        @Override
        public LuaValue call() {
            return LuaString.valueOf(Server.instance.isRunning());
        }
    }

    private class WhatAmIFeckingCalled extends ZeroArgFunction {
        @Override
        public LuaValue call() {
            return LuaString.valueOf(Server.instance.getServerName());
        }
    }

    private class GetInspirationalQuoteOfToday extends ZeroArgFunction {
        @Override
        public LuaValue call() {
            return LuaString.valueOf(Server.instance.getMotd());
        }
    }

    private class WhosFeckinHereImBlindLikeBusLadySoICantSeeWhosActuallyHereForFecksSake extends ZeroArgFunction {
        @Override
        public LuaValue call() {
            LuaTable ushers = new LuaTable();

            for (User user : Server.instance.getOnlineUsers()) {
                ushers.add(CoerceJavaToLua.coerce(user));
            }

            return ushers;
        }
    }

    private class TellThemToFeckOff extends OneArgFunction {
        @Override
        public LuaValue call(LuaValue player) {
            Server.instance.disconnectPlayer(player.tojstring());

            return null;
        }
    }

    private class KickTheFeckinPlayer extends TwoArgFunction {
        @Override
        public LuaValue call(LuaValue player, LuaValue reason) {
            // GET THE FECKIN PLAYER YOU POTATO STAINED GEESES
            Server.instance.kickPlayer(player.tojstring(), reason.tojstring());

            return null;
        }
    }

    private class CloseServerFunc extends ZeroArgFunction {
        @Override
        public LuaValue call() {
            Server.instance.closeServer();

            return null;
        }
    }
}
