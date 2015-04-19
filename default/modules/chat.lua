Commands.addCommand("help", {
	usage = "",
	execute = function(sender)
		sender:sendMessage("&eNYI")
		return true
	end
})

Event.addListener("UserMessage", function (ev, a, b)
	if b:sub(1, 1) == "/" then
		local command = b:sub(2, #b)
		print(a:getUsername() .. " executed command " .. command)
		
		local cmd = Commands.getCommand(command)
		if cmd ~= nil then
			local success = cmd:execute(a)
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
	
	local newMessage = Utils.substitute(DefaultSettings.UserMessage,
	{
		{ "player", a:getUsername() },
		{ "id", a:getPlayerID() },
		{ "level", a:getLevel():getName() },
		{ "message", b }
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