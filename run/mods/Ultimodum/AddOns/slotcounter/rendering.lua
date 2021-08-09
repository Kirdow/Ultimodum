local core, _ = ...

local function getFreeSlots()
    if not player.isActive() then
        return 0
    end

    local inv = player.inventory()
    local count = inv.size()
    local index = 1
    local freeSlots = 0
    while index <= count do
        local item = inv.getItem(index)
        index = index + 1

        if item.isAir() then
            freeSlots = freeSlots + 1
        end
    end

    return freeSlots
end

local function getPlayerName()
    if not player.isActive() then
        return ""
    end

    return player.name()
end

local function getX()
    if type(core.position) == "number" then
        return core.position
    end

    return 0
end

local direction = 1

local function incX()
    core.position = getX() + direction
end

core.onTick = function(evt)
    incX()
end

core.onDrawOverlay = function(evt)
    gui.drawString("Player Name: §6" .. getPlayerName() .. "§r", 10, 10, -1)
    gui.drawString("Free Slots: §5" .. getFreeSlots() .. "§r", 24, 24, -1)
    gui.drawString("Hello, World!", getX(), 38, -1)

    if getX() >= evt.width() * 0.7 then
        direction = -1
    elseif getX() <= -10 then
        direction = 1
    end
end