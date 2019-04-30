package de.gurkenlabs.ldjam44;

import de.gurkenlabs.ldjam44.ui.IngameScreen;
import de.gurkenlabs.ldjam44.ui.MenuScreen;
import de.gurkenlabs.litiengine.Game;
import de.gurkenlabs.litiengine.input.Input;
import de.gurkenlabs.litiengine.resources.Resources;

public class Program {

  /**
   * The main entry point for the Game.
   * 
   * @param args
   *          The command line arguments.
   */
  public static void main(String[] args) {
    // set meta information about the game
    Game.info().setName("SERVUS BONUS");
    Game.info().setSubTitle("");
    Game.info().setVersion("v1.0.0");
    Game.info().setWebsite("https://github.com/gurkenlabs/litiengine-ldjam44");

    // init the game infrastructure
    Game.init(args);
    Input.mouse().setGrabMouse(false);
    // set the icon for the game (this has to be done after initialization
    // because
    // the ScreenManager will not be present otherwise)
    Game.window().setIconImage(Resources.images().get("iconx32_pass2.png"));
    Game.graphics().setBaseRenderScale(6f);

    // load data from the utiLITI game file
    Resources.load("game.litidata");

    // add the screens that will help you organize the different states of your
    // game
    Game.screens().add(new IngameScreen());
    Game.screens().add(new MenuScreen());

    GameManager.init();
    PlayerInput.init();

    if (args.length > 0) {
      if (Game.isDebug()) {
        GameManager.START_LEVEL = args[0];
      }
    }
    Game.screens().display("MENU");

    Game.start();
  }
}