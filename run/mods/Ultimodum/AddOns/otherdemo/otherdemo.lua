include("demofile.lua")

local events = getEventTable()

events.onPostLoad = function(files)
    print("Files: " .. table.concat(files, ", "))

    onOtherUtil()
end

events.onLoadComplete = function(name)
    print("complete: " .. name)
end