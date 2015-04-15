package net.teamdentro.nuclearmc;

public enum Blocks {
    AIR(0, "NONE", "EMPTY"),
    STONE(1),
    GRASS(2),
    DIRT(3),
    COBBLESTONE(4),
    WOOD(5, "PLANK", "PLANKS", "WOODENPLANKS", "WOODPLANKS"),
    SAPLING(6, "PLANT"),
    BEDROCK(7, "ADMINIUM"),
    WATER(8),
    STATIONARYWATER(9, "STILLWATER"), // this is no different to other water
    LAVA(10),
    STATIONARYLAVA(11, "STILLLAVA"), // this is no different to other lava
    SAND(12),
    GRAVEL(13),
    GOLDORE(14),
    IRONORE(15),
    COALORE(16),
    LOG(17, "TREE", "WOODBLOCK"),
    LEAVES(18, "LEAF"),
    SPONGE(19),
    GLASS(20),
    RED(21, "REDWOOL", "REDCLOTH"),
    ORANGE(22, "ORANGEWOOL", "ORANGECLOTH"),
    YELLOW(23, "YELLOWWOOL", "YELLOWCLOTH"),
    LIME(24, "LIMEWOOL", "LIMECLOTH"),
    GREEN(25, "GREENWOOL", "GREENCLOTH"),
    AQUAGREEN(26, "AQUAGREENWOOL", "AQUAGREENCLOTH", "TURQOUISE", "TURQOISEWOOL", "TURQOISECLOTH"),
    CYAN(27, "CYANWOOL", "CYANCLOTH", "AQUA", "AQUAWOOL", "AQUACLOTH"),
    BLUE(28, "BLUEWOOL", "BLUECLOTH"),
    PURPLE(29, "PURPLEWOOL", "PURPLECLOTH"),
    INDIGO(30, "INDIGOWOOL", "INDIGOCLOTH"),
    VIOLET(31, "VIOLETWOOL", "VIOLETCLOTH"),
    MAGENTA(32, "MAGENTAWOOL", "MAGENTACLOTH"),
    PINK(33, "PINKWOOL", "PINKCLOTH"),
    BLACK(34, "BLACKWOOL", "BLACKCLOTH"),
    GRAY(35, "GRAYWOOL", "GRAYCLOTH"),
    WHITE(36, "WHITEWOOL", "WHITECLOTH"),
    YELLOWFLOWER(37, "DANDELION", "FLOWER"),
    REDFLOWER(38, "ROSE"),
    BROWNMUSHRROM(39, "MUSHROOM"),
    REDMUSHROOM(40),
    GOLD(41, "BLOCKOFGOLD", "GOLDBLOCK"),
    IRON(42, "BLOCKOFIRON", "IRONBLOCK"),
    DOUBLESLAB(43, "SLABBLOCK", "DOUBLESTEP", "DOUBLESTAIR"), // we support legacy here ;)
    SLAB(44, "HALFBLOCK", "HALFSTONE", "STEP", "STAIR"),
    BRICKS(45, "BRICK", "BRICKBLOCK"),
    TNT(46, "EXPLOSIVES"),
    BOOKSHELF(47, "BOOK", "BOOKS", "SHELF", "CASE", "BOOKCASE"),
    MOSSYCOBBLESTONE(48, "MOSSSTONE", "MOSSCOBBLESTONE", "MOSS", "MOSSY", "MOSSYSTONE"),
    OBSIDIAN(49);

    private byte id;
    private String[] aliases;

    Blocks(int id, String... aliases) {
        this.id = (byte) id;
        this.aliases = aliases;
    }

    /**
     * Get all aliases for this block type.
     * <p/>
     * Accept all of these for String input.
     *
     * @return A list of aliases for this block
     * @see #fromString(String)
     */
    public String[] getAliases() {
        return aliases;
    }

    public byte getId() {
        return id;
    }

    /**
     * Gets a block from a String, alias inclusive
     *
     * @param input The block as a string
     * @return The Blocks block, null if the string is invalid
     */
    public static Blocks fromString(String input) {
        for (Blocks block : values()) {
            if (block.toString().equalsIgnoreCase(input))
                return block;
            else
                for (String s : block.getAliases())
                    if (s.equalsIgnoreCase(input))
                        return block;
        }

        return null;
    }
}
