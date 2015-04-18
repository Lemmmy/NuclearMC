print("Working directory is " .. Plugin.getWorkingDirectory())

Plugin.run("setup.lua")
Plugin.run("modules/bans.lua")
Plugin.run("modules/chat.lua")