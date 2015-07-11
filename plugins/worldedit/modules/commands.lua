local function getBlockFromArgs(sender, argn, args, points)
	local block

	if #args >= argn then
		block = Blocks.getBlock(args[argn])

		if not block then
			sender:sendMessage(Utils.substitute(WorldEditSettings.InvalidBlockMessage,
		    	{
		    		{ "block", args[argn] }
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
			local block = getBlockFromArgs(sender, 1, args, points)
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
			local block = getBlockFromArgs(sender, 1, args, points)
			local changecount = WorldEdit.Shapes.Ellipsoid(user, true, points, block)

			user:sendMessage(Utils.substitute(WorldEditSettings.BlockChangeMessage,
		    	{
		    		{ "blocks", changecount }
		    	}))

			pts = points
		end)

		return true
	end
})

Commands.addCommand("line", {
	usage = "[block]",
	category = "worldedit",

	execute = function(sender, args)
		local pts
		WorldEditSelection(2, sender, function(user, points)
			local block = getBlockFromArgs(sender, 1, args, points)
			local changecount = WorldEdit.Shapes.Line(user, points, block)

			user:sendMessage(Utils.substitute(WorldEditSettings.BlockChangeMessage,
		    	{
		    		{ "blocks", changecount }
		    	}))

			pts = points
		end)

		return true
	end
})