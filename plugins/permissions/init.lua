Permissions = {}

Plugin.run("setup.lua")
Plugin.run("modules/manager.lua")
Plugin.run("modules/commands.lua")

Permissions.LoadGroups()
Permissions.SaveGroups() -- Make sure that there is a groups file

Event.addListener("Save", function(ev)
    Permissions.SaveGroups()
end)

Event.addListener("PostUserConnect", function(ev, user)
    local groupName, group = Permissions.GetUserGroup(user:getUsername())
    group:UpdateUserPermissions(user:getUsername())
end)