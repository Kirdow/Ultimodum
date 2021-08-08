include("rendering.lua")
include("lib.lua")
include("chat.lua")

local core, _ = ...
local events = getEventTable()
core.events = events

events.onPostLoad = function(files)
    events.registerEvent("Overlay", core.onDrawOverlay)
    events.registerEvent("ChatReceive", core.onChatMessage)
    events.registerEvent("ClientTick", core.onTick)
end