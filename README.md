<p align="center">
    <a href="https://github.com/Kirdow/Ultimodum/releases"><img alt="Ultimodum" src="https://raw.githubusercontent.com/Kirdow/Ultimodum/master/assoc-files/images/logo256.png"></a>
    <br>
    <a href="https://github.com/Kirdow/Ultimodum/issues"><img alt="GitHub issues" src="https://img.shields.io/github/issues/Kirdow/Ultimodum"></a>
    <a href="https://github.com/Kirdow/Ultimodum/stargazers"><img alt="GitHub stars" src="https://img.shields.io/github/stars/Kirdow/Ultimodum"></a>
    <a href="https://github.com/Kirdow/Ultimodum/blob/master/LICENSE"><img alt="GitHub license" src="https://img.shields.io/github/license/Kirdow/Ultimodum"></a>
</p>

About Ultimodum
========
Ever felt closing the game just to try a new mod was annoying? Well Ultimodum is here to resolve that.
<br> By the use of Lua, Ultimodum allows you to make your mods within the game itself, without the need to restart the game. It does this by using Lua scripts for the programming, while still giving you the same power Java would to a strong degree.
<br><br>
If you've played games like World of Warcraft, you should be fairly familiar with the addon system used there. Ultimodum is a recreation of WoW AddOns within Minecraft.

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

Issues
========
If you find anything about the mod that should be changed or fixed, feel free to create an issue, and I'd look into it whenever I can.

License
========

Ultimodum is licensed under the [GNU Affero General Public License v3.0](https://github.com/Kirdow/Ultimodum/blob/master/LICENSE)