Commands.addCommand("set", {
	usage = "<block>",
	category = "worldedit",
	aliases = {"z"},
	
	execute = function(sender, args)
		if #args == 1 then
			WorldEditSelection(2, sender, function(user, points) 
				local block = Blocks.getBlock(args[1])
				local changecount = 0

				for y=points[1].y,points[2].y do
					for x=points[1].x,points[2].x do
						for z=points[1].z,points[2].z do
							changecount = changecount + 1
							user:getLevel():setBlockNotify(x, y, z, Blocks.getJBlock(block))
						end
					end
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