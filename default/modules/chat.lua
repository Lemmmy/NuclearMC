Event.addListener("UserMessage", function (ev, a, b)
		local newMessage = TextUtils.substitute(DefaultSettings["UserMessage"],
		{
			{"player", a:getUsername()},
			{"id", a:getPlayerID()},
			{"level", a:getLevel():getName()},
			{"message", b}
		})

		Server.broadcast(newMessage)
	end)