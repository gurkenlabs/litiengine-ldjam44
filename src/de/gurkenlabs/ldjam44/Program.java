package de.gurkenlabs.ldjam44;

import java.awt.event.KeyEvent;

import de.gurkenlabs.litiengine.Game;
import de.gurkenlabs.litiengine.input.Input;
import de.gurkenlabs.litiengine.resources.Resources;

public class Program {

  /**
   * The main entry point for the Game.
   * 
   * @param args The command line arguments.
   */
  public static void main(String[] args) {
    // set meta information about the game
    Game.info().setName("LDJAM 44 GAME");
    Game.info().setSubTitle("");
    Game.info().setVersion("v0.0.1");
    Game.info().setWebsite("https://github.com/gurkenlabs/litiengine-ldjam44");
    Game.info().setDescription("An example 2D platformer with shooter elements made in the LITIengine");

    // init the game infrastructure
    Game.init(args);

    // set the icon for the game (this has to be done after initialization because
    // the ScreenManager will not be present otherwise)
    Game.window().setIconImage(Resources.images().get("icon.png"));
    Game.graphics().setBaseRenderScale(4.001f);

    // load data from the utiLITI game file
    Resources.load("game.litidata");

    // add the screens that will help you organize the different states of your game
    Game.screens().add(new IngameScreen());

    // load the first level (resources for the map were implicitly loaded from the
    // game file)
    // Game.world().loadEnvironment("level1");
    
    // make the game exit upon pressing ESCAPE (by default there is no such key binding and the window needs to be shutdown otherwise, e.g. ALT-F4 on Windows)
    Input.keyboard().onKeyPressed(KeyEvent.VK_ESCAPE, e -> System.exit(0));

    Game.start();
  }
}