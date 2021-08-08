local core, _ = ...

local function getX()
    if type(core.position) == "number" then
        return core.position
    end

    return 0
end

local function incX()
    core.position = getX() + 1
end

core.onTick = function(evt)
    incX()
end

core.onDrawOverlay = function(evt)
    gui.drawString("Hello, World!", getX(), 24, -1)

    if getX() >= evt.width() * 0.7 then
        ReloadUI()
    end
end