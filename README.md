# LITIengine LDJAM Project template
This project contains the source code of our game for the Ludum Dare Game Jam 44.
It was created in 72 hours and serves as an open-source **example project** for the LITIenine.

You can use this repository as a blueprint for LITIengine games and derive your own game structure from it.

:warning: **It's recommended to use the [`v0.4.17-alpha` branch](https://github.com/gurkenlabs/litiengine-ldjam44/tree/v0.4.17-alpha) of this repository if you want to use it as a starting point for your own jam game.** The master branch requires you to also build the engine upon compilation because it references it as a submodule.

The matching utiLITI editor can be downloaded here:

* [utiLITI for Windows](https://github.com/gurkenlabs/litiengine/releases/download/v0.4.17-alpha/utiliti-v0.4.17-alpha-win.zip)
* [utiLITI for Linux / MacOS](https://github.com/gurkenlabs/litiengine/releases/download/v0.4.17-alpha/utiliti-v0.4.17-alpha-linux-mac.zip)

## Build & Distribute
For the jam, we recommend to bundle the JRE with your game because not everyone has the matching Java version installed on
their machine. This will be done automatically if you use this project template. 

> Beware that there is no installer for Linux or Mac. The bundled JRE will only be used when the game is distributed via launch4j.

In order to build and distribute your game, you need to execute the following tasks:
1. `gradle build` will produce an executable **.jar** and copy the required distribution files to **build/libs**
2. `gradle createAllExecutables` will produce a **.exe** file as configured in the `launch4j` task in the **build.gradle** file
3. All the required files to execute you game will now be located in the **build/libs** folder. You can prepare the game for upload by
  creating a **.zip** archive with your favorite archiving tool (make sure to include the jre folder when distributing for Windows).
