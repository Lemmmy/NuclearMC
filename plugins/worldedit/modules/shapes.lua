WorldEdit.Shapes = {}

WorldEdit.Shapes.Cuboid = function (user, points, block)
	local changecount = 0

	for y=points[1].y, points[2].y, points[2].y<points[1].y and -1 or 1 do
		for x=points[1].x, points[2].x, points[2].x<points[1].x and -1 or 1 do
			for z=points[1].z, points[2].z, points[2].z<points[1].z and -1 or 1 do
				changecount = changecount + 1
				user:getLevel():setBlockNotify(x, y, z, Blocks.getJBlock(block))
			end
		end
	end

	return changecount
end

WorldEdit.Shapes.Ellipse = function (user, points, block)
	local function quickSetBlock(x, y, z) 
		user:getLevel():setBlockNotify(x, y, z, Blocks.getJBlock(block))
	end

	local changecount = 0

	local x1 = points[1].x;
	local y1 = points[1].y;
	local z1 = points[1].z;
	local x2 = points[2].x;

	local cx = x1 < x2 and x1 + ((x2 - x1) / 2) or x2 + ((x1 - x2) / 2)
	local cz = z1 < z2 and z1 + ((z2 - z1) / 2) or z2 + ((z1 - z2) / 2) 
	local w = x1 < x2 and x2 - x1 or x1 - x2
	local d = z1 < z2 and z2 - z1 or z1 - z2

	return changecount
end

WorldEdit.Shapes.Ellipsoid = function (user, points, block)
	local changecount = 0

	local x1 = points[1].x;
	local y1 = points[1].y;
	local z1 = points[1].z;
	local x2 = points[2].x;
	local y2 = points[2].y;
	local z2 = points[2].z;

	local cx = x1 < x2 and x1 + ((x2 - x1) / 2) or x2 + ((x1 - x2) / 2) 
	local cy = y1 < y2 and y1 + ((y2 - y1) / 2) or y2 + ((y1 - y2) / 2) 
	local cz = z1 < z2 and z1 + ((z2 - z1) / 2) or z2 + ((z1 - z2) / 2) 

	return changecount
end