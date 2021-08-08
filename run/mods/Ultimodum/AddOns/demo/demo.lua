include("load_a.lua")
include("load_b.lua")

local events = getEventTable()

events.onPostLoad = function(files)
    print("Files: " .. table.concat(files, ", "))

    onLoadA()
    onLoadB()
end

events.onLoadComplete = function(name)
    print("complete: " .. name)
end

local function onClientTick()

end

local function onGuiOpen(evt)
    print("Gui Opened: " .. evt.type())
end

events.registerEvent("GuiOpen", onGuiOpen)
