local core, _ = ...

core.onChatMessage = function(evt)
    local msg  = evt.getMessage()
    if string.find(msg, "disappear") then
        dprint("Removed message: ", msg)
        evt.cancel()
    end
end