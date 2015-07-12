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

Commands.addCommand("empty", {
	usage = "",
	category = "worldedit",
	aliases = {"clear"},

	execute = function(sender, args)
		local pts
		WorldEditSelection(2, sender, function(user, points)
			local block = Blocks.getBlock("air")
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

Commands.addCommand("rainbow", {
	usage = "",
	category = "worldedit",
	aliases = {"rb"},

	execute = function(sender, args)
		local pts
		WorldEditSelection(2, sender, function(user, points)
			local changecount = WorldEdit.Shapes.Rainbow(user, points)

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

Event.addListener("PostSetBlock", function(ev, user, x, y, z, mode, block, oldBlock)
	local selecting = user:getProperty("worldedit_selection_selecting")
	local painting = user:getProperty("worldedit_paint_enabled")

	if not selecting and painting then
		local paintblock = user:getProperty("worldedit_paint_block")
		user:getLevel():setBlockNotify(x, y, z, Blocks.getJBlock(paintblock))
	end
end)

Commands.addCommand("paint", {
	usage = "<block>",
	category = "worldedit",

	execute = function(sender, args)
		if #args == 1 then
			if args[1]:lower() == "off" then
				sender:setProperty("worldedit_paint_enabled", false)
				sender:sendMessage(WorldEditSettings.PaintOffMessage)
			else
				local block = Blocks.getBlock(args[1])
				if block then
					sender:setProperty("worldedit_paint_enabled", true)
					sender:setProperty("worldedit_paint_block", block)
					sender:sendMessage(Utils.substitute(WorldEditSettings.PaintMessage,
						{
							{ "block", block.name}
						}))
				else
					sender:sendMessage(Utils.substitute(WorldEditSettings.InvalidBlockMessage,
			    	{
			    		{ "block", args[1] }
			    	}))
				end
			end
			return true
		else
			return false
		end
	end
})

Commands.addCommand("tree", {
	usage = "",
	category = "worldedit",

	execute = function(sender, args)
		local pts
		WorldEditSelection(1, sender, function(user, points)
			local changecount = WorldEdit.Shapes.Tree(user, points)

			user:sendMessage(Utils.substitute(WorldEditSettings.BlockChangeMessage,
	    	{
	    		{ "blocks", changecount }
	    	}))

			pts = points
		end)

		return true
	end
})