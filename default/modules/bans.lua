local bannedBlocks = {}

do
	local function fileExists(name)
		local f = io.open(name, "r")
		if f ~= nil then io.close(f) return true end
		return false
	end
	
	local bannedBlocksPath = Plugin.resolve("banned_blocks.txt")

	if not fileExists(bannedBlocksPath) then
		local file = io.open(bannedBlocksPath, "w")
		io.close(file)
	end
	
	for line in io.lines(bannedBlocksPath) do
		table.insert(bannedBlocks, line)
	end
end

local function isBlockBanned(block)
	for _,v in pairs(bannedBlocks) do
		local b = Blocks.getBlock(v)
		if b == block then
			return true
		end
	end
	
	return false
end

Event.addListener("SetBlock", function(ev, user, x, y, z, mode, block, oldBlock)
	local banned = isBlockBanned(block)
	if banned then
		user:sendMessage(DefaultSettings.BlockBannedMessage)
		ev:setCancelled(true)
	end
end)