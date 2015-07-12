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

WorldEdit.Shapes.Ellipsoid = function (user, hollow, points, block)
	local changecount = 0

	local x1 = points[1].x;
	local y1 = points[1].y;
	local z1 = points[1].z;
	local x2 = points[2].x;
	local y2 = points[2].y;
	local z2 = points[2].z;

	local rx = (math.abs(x2 - x1) + 1) / 2
	local ry = (math.abs(y2 - y1) + 1) / 2
	local rz = (math.abs(z2 - z1) + 1) / 2
	for y=y1, y2, y2<y1 and -1 or 1 do
		for x=x1, x2, x2<x1 and -1 or 1 do
			for z=z1, z2 , z2<z1 and -1 or 1 do
				local lx = x1 < x2 and x2 - x or x1 - x
				local ly = y1 < y2 and y2 - y or y1 - y
				local lz = z1 < z2 and z2 - z or z1 - z
				user:sendMessage("W: " .. (math.abs(x2 - x1) + 1) .. " X: " .. lx .. " R: " .. rx)
				if math.round(math.sqrt(rx * rx + ry * ry + rz * rz)) ==
					math.round(math.sqrt(lx * lx + ly * ly + lz * lz)) then
					changecount = changecount + 1
					user:getLevel():setBlockNotify(x, y, z, Blocks.getJBlock(block))
				end
			end
		end
	end

	return changecount
end

WorldEdit.Shapes.Line = function (user, points, block)
	local changecount = 0
	local notdrawn = true

	local x1 = points[1].x;
	local y1 = points[1].y;
	local z1 = points[1].z;
	local x2 = points[2].x;
	local y2 = points[2].y;
	local z2 = points[2].z;

	local tipx = x1
	local tipy = y1
	local tipz = z1

	local dx = math.abs(x2 - x1)
	local dy = math.abs(y2 - y1)
	local dz = math.abs(z2 - z1)

	if dx + dy + dz == 0 then
		changecount = changecount + 1
		user:getLevel():setBlockNotify(tipx, tipy, tipz, Blocks.getJBlock(block))
		notdrawn = false
	end

	if math.max(math.max(dx, dy), dz) == dx and notdrawn then
		for domstep = 0, dx, 1 do
			tipx = x1 + domstep * (x2 - x1 > 0 and 1 or -1)
			tipy = math.round(y1 + domstep * dy / dx * (y2 - y1 > 0 and 1 or -1))
			tipz = math.round(z1 + domstep * dz / dx * (z2 - z1 > 0 and 1 or -1))

			changecount = changecount + 1
			user:getLevel():setBlockNotify(tipx, tipy, tipz, Blocks.getJBlock(block))
		end
		notdrawn = false
	end

	if math.max(math.max(dx, dy), dz) == dy and notdrawn then
		for domstep = 0, dy, 1 do
			tipy = y1 + domstep * (y2 - y1 > 0 and 1 or -1)
			tipx = math.round(x1 + domstep * dx / dy * (x2 - x1 > 0 and 1 or -1))
			tipz = math.round(z1 + domstep * dz / dy * (z2 - z1 > 0 and 1 or -1))

			changecount = changecount + 1
			user:getLevel():setBlockNotify(tipx, tipy, tipz, Blocks.getJBlock(block))
		end
		notdrawn = false
	end

	if math.max(math.max(dx, dy), dz) == dz and notdrawn then
		for domstep = 0, dz, 1 do
			tipz = z1 + domstep * (z2 - z1 > 0 and 1 or -1)
			tipy = math.round(y1 + domstep * dy / dz * (y2 - y1 > 0 and 1 or -1))
			tipx = math.round(x1 + domstep * dx / dz * (x2 - x1 > 0 and 1 or -1))

			changecount = changecount + 1
			user:getLevel():setBlockNotify(tipx, tipy, tipz, Blocks.getJBlock(block))
		end
		notdrawn = false
	end

	return changecount
end

WorldEdit.Shapes.Rainbow = function (user, points)
	local changecount = 0

	local wx = math.abs(points[2].x - points[1].x)
	local wy = math.abs(points[2].y - points[1].y)
	local wz = math.abs(points[2].z - points[1].z)

	local blockid = Blocks.getBlock("red").id
	local blockidcurrent = blockid
	local blockidmax = Blocks.getBlock("pink").id

	if wx >= wy and wx >= wz then
		for x=points[1].x, points[2].x, points[2].x<points[1].x and -1 or 1 do
			for y=points[1].y, points[2].y, points[2].y<points[1].y and -1 or 1 do
				for z=points[1].z, points[2].z, points[2].z<points[1].z and -1 or 1 do
						changecount = changecount + 1
						user:getLevel():setBlockNotify(x, y, z, Blocks.getJBlock(Blocks.getBlockFromId(blockidcurrent)))
				end
			end
			blockidcurrent = blockidcurrent + 1
			if blockidcurrent > blockidmax then blockidcurrent = blockid end
		end
	elseif wy > wx and wy > wz then
		for y=points[1].y, points[2].y, points[2].y<points[1].y and -1 or 1 do
			for x=points[1].x, points[2].x, points[2].x<points[1].x and -1 or 1 do
				for z=points[1].z, points[2].z, points[2].z<points[1].z and -1 or 1 do
						changecount = changecount + 1
						user:getLevel():setBlockNotify(x, y, z, Blocks.getJBlock(Blocks.getBlockFromId(blockidcurrent)))
				end
			end
			blockidcurrent = blockidcurrent + 1
			if blockidcurrent > blockidmax then blockidcurrent = blockid end
		end
	elseif wz > wy and wz > wx then
		for z=points[1].z, points[2].z, points[2].z<points[1].z and -1 or 1 do
			for y=points[1].y, points[2].y, points[2].y<points[1].y and -1 or 1 do
				for x=points[1].x, points[2].x, points[2].x<points[1].x and -1 or 1 do
						changecount = changecount + 1
						user:getLevel():setBlockNotify(x, y, z, Blocks.getJBlock(Blocks.getBlockFromId(blockidcurrent)))
				end
			end
			blockidcurrent = blockidcurrent + 1
			if blockidcurrent > blockidmax then blockidcurrent = blockid end
		end
	end

	return changecount
end

WorldEdit.Shapes.Tree = function (user, points)
	local changecount = 0

	local x1 = points[1].x
	local y1 = points[1].y
	local z1 = points[1].z

	local height = math.random(3, 4)
	local top = 2

	for x = -top, top, 1 do
		for y = 0, top, 1 do
			for z = -top, top, 1 do
				local dist = math.sqrt(x * x + y * y + z * z)
				if dist < top + 1 then
					local nx = x1 + x
					local ny = y1 + y + height - top + 1
					local nz = z1 + z

					changecount = changecount + 1
					user:getLevel():setBlockNotify(nx, ny, nz, Blocks.getJBlock(Blocks.getBlock("leaves")))
				end
			end
		end
	end

	for y = y1, y1 + top + height - 1, 1 do
			changecount = changecount + 1
			user:getLevel():setBlockNotify(x1, y, z1, Blocks.getJBlock(Blocks.getBlock("log")))
	end

	user:getLevel():setBlockNotify(x1, y1 + height + 1, z1, Blocks.getJBlock(Blocks.getBlock("leaves")))

	return changecount
end