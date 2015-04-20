local config = Server.getConfig("worldedit")

DefaultSettings = {
	SelectionMessage = "&ePlace or break a block for point &c${point}",
	BlockChangeMessage = "&eChanged &c${blocks} &eblocks",
}

if config:isEmpty() then
	for k,v in pairs(DefaultSettings) do
		config:set(k, v)
	end

	config:save()
else
	local anythingChanged = false
	for k,v in pairs(DefaultSettings) do
		if not config:hasKey(k) then
			anythingChanged = true
			config:set(k, v)
		end
	end

	if anythingChanged then
		config:save()
	end
end

config:copyToTable(DefaultSettings)