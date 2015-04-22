local function getBlockFromArgs(sender, args, points)
	local block

	if #args == 1 then
		block = Blocks.getBlock(args[1])

		if not block then
			sender:sendMessage(Utils.substitute(WorldEditSettings.InvalidBlockMessage,
		    	{
		    		{ "block", args[1] }
		    	}))

			return
		end
	end

	if #args == 0 or not block then
		block = points[2].block
	end

	return block
end

Commands.addCommand("cuboid", {
	usage = "[block]",
	category = "worldedit",
	aliases = {"z", "set"},
	
	execute = function(sender, args)
		local pts
		WorldEditSelection(2, sender, function(user, points) 
			local block = getBlockFromArgs(sender, args, points)
			local changecount = WorldEdit.Shapes.Cuboid(user, points, block)			

			user:sendMessage(Utils.substitute(WorldEditSettings.BlockChangeMessage,
		    	{
		    		{ "blocks", changecount }
		    	}))

			pts = points
		end)

		return true
	end
})

Commands.addCommand("ellipsoid", {
	usage = "[block]",
	category = "worldedit",
	
	execute = function(sender, args)
		local pts
		WorldEditSelection(2, sender, function(user, points) 
			local block = getBlockFromArgs(sender, args, points)
			local changecount = WorldEdit.Shapes.Ellipsoid(user, points, block)			

			user:sendMessage(Utils.substitute(WorldEditSettings.BlockChangeMessage,
		    	{
		    		{ "blocks", changecount }
		    	}))

			pts = points
		end)

		return true
	end
})