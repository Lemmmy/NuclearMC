package net.teamdentro.nuclearmc.plugin;

import net.teamdentro.nuclearmc.command.Command;
import net.teamdentro.nuclearmc.command.CommandSender;
import org.luaj.vm2.LuaTable;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.lib.OneArgFunction;
import org.luaj.vm2.lib.TwoArgFunction;
import org.luaj.vm2.lib.jse.CoerceJavaToLua;

/**
 * Created by Lignum on 19/04/2015.
 */
public class CommandsLib extends OneArgFunction {
    @Override
    public LuaValue call(LuaValue arg) {
        LuaTable lib = tableOf();

        lib.set("getCommand", new GetCommand());
        lib.set("addCommand", new AddCommand());

        return lib;
    }

    private class AddCommand extends TwoArgFunction {
        @Override
        public LuaValue call(LuaValue arg1, LuaValue arg2) {
            final String name = arg1.checkjstring();
            final LuaTable tbl = arg2.checktable();

            Command cmd = new Command() {
                @Override
                public String[] getAliases() {
                    LuaTable aliasTable = tbl.get("aliases").checktable();
                    String[] aliases = new String[aliasTable.length()];

                    for (int i = 0; i < aliasTable.length(); ++i) {
                        aliases[i] = String.valueOf(aliasTable.get(i));
                    }

                    return aliases;
                }

                @Override
                public String getName() {
                    return name;
                }

                @Override
                public String getUsage() {
                    return tbl.get("usage").checkjstring();
                }

                @Override
                public String getCategory() {
                    return tbl.get("category").checkjstring();
                }

                @Override
                public boolean execute(CommandSender sender, String[] args) {
                    LuaTable argTable = new LuaTable();

                    int i = 1;
                    for (String arg : args) {
                        argTable.set(i, arg);
                        i++;
                    }

                    boolean result = tbl.get("execute").call(CoerceJavaToLua.coerce(sender), argTable).checkboolean();
                    return result;
                }
            };

            Command.registerCommand(cmd);

            return LuaValue.NIL;
        }
    }

    private class GetCommand extends OneArgFunction {
        @Override
        public LuaValue call(LuaValue arg) {
            String cmdName = arg.checkjstring();
            Command cmd = Command.getCommand(cmdName);

            if (cmd == null) {
                return LuaValue.NIL;
            }

            return CoerceJavaToLua.coerce(cmd);
        }
    }
}
