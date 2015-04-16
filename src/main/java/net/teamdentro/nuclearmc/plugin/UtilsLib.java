package net.teamdentro.nuclearmc.plugin;

import org.apache.commons.lang3.text.StrSubstitutor;
import org.luaj.vm2.LuaString;
import org.luaj.vm2.LuaTable;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.lib.OneArgFunction;
import org.luaj.vm2.lib.TwoArgFunction;

import java.util.HashMap;
import java.util.Map;

public class UtilsLib extends OneArgFunction {
    @Override
    public LuaValue call(LuaValue env) {
        LuaValue lib = tableOf();

        lib.set("substitute", new SubstitutionFunc());

        return lib;
    }

    private class SubstitutionFunc extends TwoArgFunction {
        @Override
        public LuaValue call(LuaValue input, LuaValue table) {
            LuaTable map = table.checktable();
            Map<String, String> valuesMap = new HashMap();

            for (int i = 1; i < map.length() + 1; i++) {
                LuaTable t = map.get(i).checktable();
                valuesMap.put(t.get(1).tojstring(), t.get(2).tojstring());
            }

            StrSubstitutor substitutor = new StrSubstitutor(valuesMap);
            return LuaString.valueOf(substitutor.replace(input.tojstring()));
        }
    }
}
