package de.gurkenlabs.ldjam44;

import java.awt.Font;

import de.gurkenlabs.ldjam44.entities.CustomCreatureMapObjectLoader;
import de.gurkenlabs.ldjam44.entities.Enemy;
import de.gurkenlabs.ldjam44.entities.Player;
import de.gurkenlabs.ldjam44.entities.Slave;
import de.gurkenlabs.litiengine.Game;
import de.gurkenlabs.litiengine.entities.Spawnpoint;
import de.gurkenlabs.litiengine.environment.CreatureMapObjectLoader;
import de.gurkenlabs.litiengine.environment.Environment;
import de.gurkenlabs.litiengine.graphics.Camera;
import de.gurkenlabs.litiengine.graphics.PositionLockCamera;
import de.gurkenlabs.litiengine.gui.GuiProperties;
import de.gurkenlabs.litiengine.resources.Resources;

public final class GameManager {
  public static final Font GUI_FONT = Resources.fonts().get("fsex300.ttf").deriveFont(32f);
  public static final Font SPEECH_BUBBLE_FONT = GUI_FONT.deriveFont(4f);

  private GameManager() {
  }

  public static void init() {
    GuiProperties.setDefaultFont(GUI_FONT);

    Environment.registerMapObjectLoader(new CustomCreatureMapObjectLoader());

    CreatureMapObjectLoader.registerCustomCreatureType(Slave.class);
    CreatureMapObjectLoader.registerCustomCreatureType(Enemy.class);

    Camera camera = new PositionLockCamera(Player.instance());
    camera.setClampToMap(true);
    Game.world().setCamera(camera);

    // add default game logic for when a level was loaded
    Game.world().addLoadedListener(e -> {

      // spawn the player instance on the spawn point with the name "enter"
      Spawnpoint enter = e.getSpawnpoint("enter");
      if (enter != null) {
        enter.spawn(Player.instance());
      }
    });
  }

}
