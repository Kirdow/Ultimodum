To create an addon, create a folder in here and give it the name of your addon,
and within have a lua file of the same name, let's refer to this file as "addon.lua".
You should now have a structure similar to this:
-----
addon\
| addon.lua
-----
Inside addon.lua will be your addon setup code. Load other files using include("file.lua").
Loaded files are run in the order they were loaded, although after addon.lua finishes execution.
If you want to wait for some files to load for code to execute in addon.lua, create a function onPostLoad(files).
The files parameter will contain a list of the files that were loaded. For reference, any variable not defined as local will be in the global scope,
accessible by other addons.
Additionally, if you have dependencies like other addons, all addons are initially loaded when onPostLoad executes,
if you require further execution before yours you can also use a function onLoadComplete() which runs after all onPostLoad calls.
You could also create a file called "dep.txt" containing an addon name on each line, and your addon will load after all those have been loaded, which would leave your structure like this:
-----
addon\
| addon.lua
| dep.txt
-----
