function WorldEditSelection(points, user, callback)
    local pts = {}

    for i = 1, points do
        table.insert(pts, {done = false})
    end

	user:setProperty("worldedit_selection_selecting", true)
	user:setProperty("worldedit_selection_points", pts)
	user:setProperty("worldedit_selection_allpointscallback", callback)

	user:sendMessage(Utils.substitute(WorldEditSettings.SelectionMessage,
    	{
    		{ "point", 1 },
        { "max", points }
    	}))
end

Event.addListener("PostSetBlock", function(ev, user, x, y, z, mode, block, oldBlock)
	local selecting = user:getProperty("worldedit_selection_selecting")

	if selecting then
		local points = user:getProperty("worldedit_selection_points")

		local oldBlock = oldBlock
		user:getLevel():setBlockNotify(x, y, z, Blocks.getJBlock(oldBlock))

        if points ~= null and #points > 0 then
        	for i,v in ipairs(points) do
        		if not v.done then
        			v.x = x
			    	v.y = y
			    	v.z = z
			    	v.done = true
			    	v.block = block
			    	v.oldBlock = oldBlock
			    	user:setProperty("worldedit_selection_points", points)
        			if i < #points then
						user:sendMessage(Utils.substitute(WorldEditSettings.SelectionMessage,
					    	{
					    		{ "point", i + 1 },
  					    	{ "max", #points }
					    	}))
        			else
        				local callback = user:getProperty("worldedit_selection_allpointscallback")
        				if callback ~= null then
        					callback(user, points)
        				end
						user:setProperty("worldedit_selection_selecting", false)
        			end
        			return
        		end
        	end
	    end
	end
end)