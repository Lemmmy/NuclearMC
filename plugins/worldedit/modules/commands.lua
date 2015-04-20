Commands.addCommand("set", {
	usage = "<block>",
	category = "worldedit",
	aliases = {"z"},
	
	execute = function(sender, args)
		if #args == 1 then
			local block = Blocks.getBlock(args[1])
			WorldEditSelection(2, sender, function(user, points) 
				local changecount = 0

				for y=points[1].y, points[2].y, points[2].y<points[1].y and -1 or 1 do
					for x=points[1].x, points[2].x, points[2].x<points[1].x and -1 or 1 do
						for z=points[1].z, points[2].z, points[2].z<points[1].z and -1 or 1 do
							changecount = changecount + 1
							user:getLevel():setBlockNotify(x, y, z, Blocks.getJBlock(block))
						end
					end
				end

				for k,v in ipairs(points) do
					user:getLevel():setBlockNotify(v.x, v.y, v.z, Blocks.getJBlock(block))
				end

				user:sendMessage(Utils.substitute(DefaultSettings.BlockChangeMessage,
			    	{
			    		{ "blocks", changecount }
			    	}))
			end)

			return true
		end
		return false
	end
})