local config = Server.getConfig("worldedit")

WorldEditSettings = {
	SelectionMessage = "&ePlace or break a block for point &c${point}",
	BlockChangeMessage = "&eChanged &c${blocks} &eblocks",
	InvalidBlockMessage = "&cUnknown block &4${block}",
}

if config:isEmpty() then
	for k,v in pairs(WorldEditSettings) do
		config:set(k, v)
	end

	config:save()
else
	local anythingChanged = false
	for k,v in pairs(WorldEditSettings) do
		if not config:hasKey(k) then
			anythingChanged = true
			config:set(k, v)
		end
	end

	if anythingChanged then
		config:save()
	end
end

config:copyToTable(WorldEditSettings)