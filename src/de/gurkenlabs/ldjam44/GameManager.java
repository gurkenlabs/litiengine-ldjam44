package de.gurkenlabs.ldjam44;

import java.awt.Color;
import java.awt.Font;

import de.gurkenlabs.ldjam44.entities.CustomCreatureMapObjectLoader;
import de.gurkenlabs.ldjam44.entities.DecorMob;
import de.gurkenlabs.ldjam44.entities.Enemy;
import de.gurkenlabs.ldjam44.entities.Gatekeeper;
import de.gurkenlabs.ldjam44.entities.HealthPot;
import de.gurkenlabs.ldjam44.entities.Player;
import de.gurkenlabs.ldjam44.entities.Slave;
import de.gurkenlabs.litiengine.Game;
import de.gurkenlabs.litiengine.entities.Spawnpoint;
import de.gurkenlabs.litiengine.environment.CreatureMapObjectLoader;
import de.gurkenlabs.litiengine.environment.Environment;
import de.gurkenlabs.litiengine.environment.PropMapObjectLoader;
import de.gurkenlabs.litiengine.graphics.Camera;
import de.gurkenlabs.litiengine.graphics.PositionLockCamera;
import de.gurkenlabs.litiengine.gui.GuiProperties;
import de.gurkenlabs.litiengine.gui.SpeechBubbleAppearance;
import de.gurkenlabs.litiengine.resources.Resources;

public final class GameManager {
  public static final Font GUI_FONT = Resources.fonts().get("fsex300.ttf").deriveFont(32f);
  public static final Font SPEECH_BUBBLE_FONT = GUI_FONT.deriveFont(4f);
  public static final SpeechBubbleAppearance SPEECH_BUBBLE_APPEARANCE = new SpeechBubbleAppearance(new Color(16, 20, 19), new Color(255, 255, 255, 150), new Color(16, 20, 19), 5);

  static {
    SPEECH_BUBBLE_APPEARANCE.setBackgroundColor2(new Color(255, 255, 255, 220));
  }

  private GameManager() {
  }

  public static void init() {
    GuiProperties.setDefaultFont(GUI_FONT);

    Environment.registerMapObjectLoader(new CustomCreatureMapObjectLoader());

    CreatureMapObjectLoader.registerCustomCreatureType(DecorMob.class);

    CreatureMapObjectLoader.registerCustomCreatureType(Slave.class);
    CreatureMapObjectLoader.registerCustomCreatureType(Enemy.class);
    CreatureMapObjectLoader.registerCustomCreatureType(Gatekeeper.class);

    PropMapObjectLoader.registerCustomPropType(HealthPot.class);

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

  public static int getOwnSlaveCount() {
    return (int) Game.world().environment().getByType(Slave.class).stream().filter(x -> !x.isDead() && x.getOwner() == null).count();
  }

  public static int getAliveSlaveCount() {
    return (int) Game.world().environment().getByType(Slave.class).stream().filter(x -> !x.isDead()).count();
  }

  public static Gatekeeper getGateKeeper() {
    return Game.world().environment().get(Gatekeeper.class, "keeper");
  }

  public static int getRequiredSlaveCount() {
    Gatekeeper keeper = getGateKeeper();
    if (keeper == null) {
      return 0;
    }

    return keeper.getRequiredSlaves();
  }
}
