Event.addListener("UserMessage", function (ev, a, b)
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