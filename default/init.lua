NUCLEARMC_PLUGIN_VERSION = "1.0"
print("Running Default NuclearMC Plugin v" .. NUCLEARMC_PLUGIN_VERSION)

local config = Server.getConfig("default")

DefaultSettings = {
	ConnectMessage = "${player} has joined the game.",
	DisconnectMessage = "${player} has left the game."
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
print(DefaultSettings.ConnectMessage)

Event.addListener("PreUserConnect", function (args)
		print("PreUserConnect " .. args["protVersion"] .. " " .. args["username"] .. " " .. args["userdata"])
	end)

Event.addListener("PostUserConnect", function (args)
		print("PostUserConnect " .. args["user"])
	end)