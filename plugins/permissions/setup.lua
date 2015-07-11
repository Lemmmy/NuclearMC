local config = Server.getConfig("permissions")

PermissionsSettings = {}

if config:isEmpty() then
	for k,v in pairs(PermissionsSettings) do
		config:set(k, v)
	end

	config:save()
else
	local anythingChanged = false
	for k,v in pairs(PermissionsSettings) do
		if not config:hasKey(k) then
			anythingChanged = true
			config:set(k, v)
		end
	end

	if anythingChanged then
		config:save()
	end
end

config:copyToTable(PermissionsSettings)