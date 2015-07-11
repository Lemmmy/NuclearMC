WorldEdit.Utils = {}

math.sign = function(n)
    return n < 0 and -1 or (n == 0 and 0 or 1)
end

math.round = function(n)
  return n + 0.5 - (n + 0.5) % 1
end