local groups = {}

local Group = {}
Group.__index = Group

setmetatable(Group, {__call = function(cls, ...)
    local self = setmetatable({}, Group)
    self.Users = {}
    self.Permissions = {}
    return self
end})

function Group:AddUser(name)
    table.insert(self.Users, name)
end

function Group:GetUserIndex(name)
    for k,v in pairs(self.Users) do
        if v == name then
            return k
        end
    end

    return nil
end

function Group:RemoveUser(name)
    local idx = self:GetUserIndex(name)
    table.remove(self.Users, idx)
end

function Group:AddPermission(permission)
    table.insert(self.Permissions, permission)
    self:UpdateAllUserPermissions()
end

function Group:GetPermissionIndex(permission)
    for k,v in pairs(self.Permissions) do
        if v == permission then
            return k
        end
    end

    return nil
end

function Group:RemovePermission(permission)
    local idx = self:GetPermissionIndex(permission)
    table.remove(self.Permissions, idx)
    self:UpdateAllUserPermissions()
end

function Group:UpdateUserPermissions(username)
    local ply = Server.getPlayer(username)

    if ply ~= nil then
        ply:clearPermissions()

        for _,v in pairs(self.Permissions) do
            ply:permit(v)
        end
    end
end

function Group:UpdateAllUserPermissions()
    for _,v in pairs(self.Users) do
        self:UpdateUserPermissions(v)
    end
end

function Permissions.NewGroup(name)
    if type(name) ~= "string" then
        error("string expected for arg #1", 2)
    end

    local group = Group()
    groups[name] = group
    return group
end

function Permissions.GetGroup(name)
    return groups[name]
end

function Permissions.RemoveGroup(name)
    groups[name] = nil
end

function Permissions.SaveGroups()
    local filename = Plugin.resolve("groups.txt")
    local firstSave = not Plugin.fileExists("groups.txt")

    local file = io.open(filename, "w+")

    if firstSave then
        local default = Permissions.NewGroup("default")
        default:AddPermission("nmc.info")
        default:AddUser("Lignum")
        default:AddUser("Lemmmy")
    end

    for k,v in pairs(groups) do
        file:write(k .. ":" .. table.concat(v.Permissions, ",") .. ":" .. table.concat(v.Users, ",") .. "\n")
    end

    file:close()
end

function Permissions.LoadGroups()
    local filename = Plugin.resolve("groups.txt")
    local file = io.open(filename, "r")

    if file == nil then
        return
    end

    local lineCount = 0
    for line in file:lines() do
        lineCount = lineCount + 1

        local whole, group, perms, users = line:match("(([A-Za-z1-9]+)%:(.-)%:(.*))")
        if whole == nil then
            print("Permissions config is invalid on line " .. lineCount)
        end

        local newGroup = Permissions.NewGroup(group)
        local it = perms:gmatch("[^,]+")
        if it == nil then
            print("Invalid permissions enumeration in group on line " .. lineCount)
            file:close()
            return
        end

        for perm in it do
            newGroup:AddPermission(perm)
        end

        it = users:gmatch("[^,]+")
        if it == nil then
            print("Invalid user enumeration in group on line " .. lineCount)
            file:close()
            return
        end

        for user in it do
            newGroup:AddUser(user)
        end
    end

    file:close()
end

function Permissions.GetUserGroup(username)
    for k,v1 in pairs(groups) do
        for _,v2 in pairs(v1.Users) do
            if v2 == username then
                return k, v1
            end
        end
    end

    return nil
end