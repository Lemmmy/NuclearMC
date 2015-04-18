NUCLEARMC_PLUGIN_VERSION = "INCOMPLETE"

local config = Server.getConfig("default")

DefaultSettings = {
	ConnectMessage = "&e${player} has joined the game.",
	DisconnectMessage = "&e${player} has left the game.",

	UserMessage = "<${player}> ${message}",
	
	BlockBannedMessage = "&cThis block is banned from the server."
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