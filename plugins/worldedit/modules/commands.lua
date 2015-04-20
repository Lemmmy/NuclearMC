Commands.addCommand("cuboid", {
	usage = "[block]",
	category = "worldedit",
	aliases = {"z", "set"},
	
	execute = function(sender, args)
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

		local pts
		WorldEditSelection(2, sender, function(user, points) 
			if #args == 0 then
				block = points[2].block
			end

			local changecount = 0

			for y=points[1].y, points[2].y, points[2].y<points[1].y and -1 or 1 do
				for x=points[1].x, points[2].x, points[2].x<points[1].x and -1 or 1 do
					for z=points[1].z, points[2].z, points[2].z<points[1].z and -1 or 1 do
						changecount = changecount + 1
						user:getLevel():setBlockNotify(x, y, z, Blocks.getJBlock(block))
					end
				end
			end

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

		local pts
		WorldEditSelection(2, sender, function(user, points) 
			if #args == 0 then
				block = points[2].block
			end

			local changecount = 0

			local x1 = points[1].x;
			local y1 = points[1].y;
			local z1 = points[1].z;
			local x2 = points[2].x;
			local y2 = points[2].y;
			local z2 = points[2].z;

			for y=y1, y2, y2<y1 and -1 or 1 do
				for x=x1, x2, x2<x1 and -1 or 1 do
					for z=z1, z2, z2<z1 and -1 or 1 do
						if math.pow(x1/x2, 3) + math.pow(y1/y2, 3) + math.pow(z1/z2, 3) == 1 then
							changecount = changecount + 1
							user:getLevel():setBlockNotify(x, y, z, Blocks.getJBlock(block))
						end
					end
				end
			end

			user:sendMessage(Utils.substitute(WorldEditSettings.BlockChangeMessage,
		    	{
		    		{ "blocks", changecount }
		    	}))

			pts = points
		end)

		return true
	end
})