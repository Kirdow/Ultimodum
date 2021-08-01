<!--<p align="center">
    <h1 style="font: sans-serif;font-size: 32px;">Ultimodum</h1>
</p>-->

About Ultimodum
========
Ever felt closing the game just to try a new mod was annoying? Well Ultimodum is here to resolve that.
<br> By the use of Lua, Ultimodum allows you to make your mods within the game itself, without the need to restart the game. It does this by using Lua scripts for the programming, while still giving you the same power Java would to a strong degree.

Setting up the environment
========
To set up the workspace, you can import the project in your IDE as a gradle project.
<br> To then build the mod you run the ``buildDependents`` task and the artifact should be generated in `build/libs`

You can also test/run the mod within your IDE by running one of the following tasks

* IntelliJ - `genIntellijRuns`
* Eclipse - `genEclipseRuns`
* VSCode - `genVSCodeRuns`

When you launch a run configuration you should also use the one specific for your IDE

* IntelliJ - `runIntellijClient`
* Eclipse - `runEclipseClient`

Then you should log in to your mojang account in the dialog that shows up.

Pull Request
========
All pull requests are welcome. While I might not accept all pull requests, don't hesitate to make one if you feel a change or addition is needed. I will look it through and determine if it's worth a merge.

License
========

Ultimodum is licensed under the [GNU Affero General Public License v3.0](https://github.com/Kirdow/Ultimodum/blob/master/LICENSE)