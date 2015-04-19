package net.teamdentro.nuclearmc.plugin;

import net.teamdentro.nuclearmc.NuclearMC;
import net.teamdentro.nuclearmc.User;
import org.luaj.vm2.LuaString;
import org.luaj.vm2.LuaTable;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.lib.VarArgFunction;
import org.luaj.vm2.lib.jse.CoerceJavaToLua;

import java.util.HashMap;
import java.util.Map;

public class Commands {
    private static Map<String, VarArgFunction> commands = new HashMap<>();

    public static void registerCommand(String cmd, VarArgFunction function) {
        if (!cmd.isEmpty() && !commands.containsKey(cmd)) {
            commands.put(cmd, function);
        }
    }

    public static void invokeCommand(String cmd, User user, String[] args) {
        if (!cmd.isEmpty()) {
            if (commands.containsKey(cmd)) {
                VarArgFunction function = commands.get(cmd);
                LuaValue[] arguments = new LuaValue[args.length-1];

                for (int i = 1; i > args.length; i++) {
                    arguments[i-1] = LuaValue.valueOf(args[i]);
                }

                function.invoke(LuaValue.varargsOf(new LuaValue[] {
                        LuaString.valueOf(cmd),
                        CoerceJavaToLua.coerce(user),
                        LuaValue.tableOf(arguments)
                }));
            } else {
                if (user == null) {
                    NuclearMC.getLogger().info("Command \"" + cmd + "\" not found");
                } else {
                    user.sendMessage("&cCommand &f\"" + cmd + "\"&c not found");
                }
            }
        }
    }
}
