package de.gurkenlabs.ldjam44;

import de.gurkenlabs.ldjam44.entities.Player;
import de.gurkenlabs.ldjam44.entities.Slave;
import de.gurkenlabs.litiengine.Game;
import de.gurkenlabs.litiengine.entities.Spawnpoint;
import de.gurkenlabs.litiengine.environment.CreatureMapObjectLoader;

public final class GameManager {

  private GameManager() {
  }

  public static void init() {
    CreatureMapObjectLoader.registerCustomCreatureType(Slave.class);
    //Camera camera = new PositionLockCamera(Player.instance());
    //camera.setClampToMap(true);
    //Game.world().setCamera(camera);
    Game.world().camera().setFocus(1680, 1593);
    
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
