Commands.addCommand("mygroup", {
    usage = "",
    category = "permissions",
    aliases = {"myrank"},

    execute = function(sender, args)
        if not sender:isPlayer() then
            sender:sendMessage("&4You can only run this command as a player!")
            return
        end

        local user = Server.toUser(sender)
        local groupName = Permissions.GetUserGroup(user:getUsername())
        user:sendMessage("&eYour group is &3" .. tostring(groupName) .. "&e.")
        return true
    end
})