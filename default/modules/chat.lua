local function parseCommand(cmd)
	local noSlash = cmd:sub(2, #cmd)
	local cmd, args = noSlash:match("^(%S+)%s?(.*)")
	
	local argTable = {}
	
	for arg in args:gmatch("%S+") do
		table.insert(argTable, arg)
	end
	
	return cmd, argTable
end

Event.addListener("UserMessage", function (ev, a, b)
	if b:sub(1, 1) == "/" then
		local command, arguments = parseCommand(b)
		print(a:getUsername() .. " executed command " .. command)
		
		local cmd = Commands.getCommand(command)
		if cmd ~= nil then
			local success = cmd:luaExecute(a, arguments)
			if not success then
				local message = Utils.substitute(DefaultSettings.WrongUsage,
				{
					{ "command", "/" .. command },
					{ "usage", cmd:getUsage() }
				})
				a:sendMessage(message)
			end
		else
			a:sendMessage(DefaultSettings.UnknownCommand)
		end
		
		ev:setCancelled(true)
		return
	end

	msg = string.gsub(b, "%%(%x)", "&%1")
	
	local newMessage = Utils.substitute(DefaultSettings.UserMessage,
	{
		{ "player", a:getUsername() },
		{ "id", a:getPlayerID() },
		{ "level", a:getLevel():getName() },
		{ "message", msg }
	})

	Server.broadcast(newMessage)
end)
	
Event.addListener("PostUserConnect", function(ev, user)
	local connectMessage = Utils.substitute(DefaultSettings.ConnectMessage,
	{
		{ "player", user:getUsername() },
		{ "id", user:getPlayerID() }
	})
	
	Server.broadcast(connectMessage)
end)

Event.addListener("UserDisconnect", function(ev, user)
    local disconnectMessage = Utils.substitute(DefaultSettings.DisconnectMessage,
    {
        { "player", user:getUsername() },
        { "id", user:getPlayerID() }
    })

    Server.broadcast(disconnectMessage)
end)