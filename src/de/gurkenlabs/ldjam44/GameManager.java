package de.gurkenlabs.ldjam44;

import java.awt.Color;
import java.awt.Font;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import de.gurkenlabs.ldjam44.entities.CustomCreatureMapObjectLoader;
import de.gurkenlabs.ldjam44.entities.DecorMob;
import de.gurkenlabs.ldjam44.entities.Enemy;
import de.gurkenlabs.ldjam44.entities.Gatekeeper;
import de.gurkenlabs.ldjam44.entities.HealthPot;
import de.gurkenlabs.ldjam44.entities.Player;
import de.gurkenlabs.ldjam44.entities.Player.PlayerState;
import de.gurkenlabs.ldjam44.graphics.SpawnEmitter;
import de.gurkenlabs.ldjam44.entities.Slave;
import de.gurkenlabs.ldjam44.ui.IngameScreen;
import de.gurkenlabs.litiengine.Game;
import de.gurkenlabs.litiengine.entities.Creature;
import de.gurkenlabs.litiengine.entities.Spawnpoint;
import de.gurkenlabs.litiengine.environment.CreatureMapObjectLoader;
import de.gurkenlabs.litiengine.environment.Environment;
import de.gurkenlabs.litiengine.environment.PropMapObjectLoader;
import de.gurkenlabs.litiengine.environment.tilemap.MapProperty;
import de.gurkenlabs.litiengine.graphics.Camera;
import de.gurkenlabs.litiengine.graphics.PositionLockCamera;
import de.gurkenlabs.litiengine.gui.GuiProperties;
import de.gurkenlabs.litiengine.gui.SpeechBubble;
import de.gurkenlabs.litiengine.gui.SpeechBubbleAppearance;
import de.gurkenlabs.litiengine.gui.SpeechBubbleListener;
import de.gurkenlabs.litiengine.resources.Resources;

public final class GameManager {
  public enum GameState {
    INGAME,
    MENU,
    INGAME_MENU,
    SLAVES_DEAD
  }

  public static final Font GUI_FONT = Resources.fonts().get("fsex300.ttf").deriveFont(32f);
  public static final Font SPEECH_BUBBLE_FONT = GUI_FONT.deriveFont(4f);
  public static final Font MENU_FONT = Resources.fonts().get("caesar.ttf").deriveFont(40f);
  public static final Font GUI_FONT_ALT = Resources.fonts().get("roman.ttf").deriveFont(40f);
  public static String START_LEVEL = "level0";

  public static final SpeechBubbleAppearance SPEECH_BUBBLE_APPEARANCE = new SpeechBubbleAppearance(new Color(16, 20, 19), new Color(255, 255, 255, 150), new Color(16, 20, 19), 5);

  private static final Map<String, Runnable> startups = new ConcurrentHashMap<>();
  static {
    SPEECH_BUBBLE_APPEARANCE.setBackgroundColor2(new Color(255, 255, 255, 220));

    startups.put("level0", () -> {
      Camera cam = new Camera();
      cam.setFocus(Game.world().environment().getCenter());
      Game.world().setCamera(cam);
    });

    startups.put("level1", () -> {
      Camera camera = new PositionLockCamera(Player.instance());
      camera.setClampToMap(true);
      Game.world().setCamera(camera);
    });

    startups.put("end", () -> {
      Camera cam = new Camera();

      Spawnpoint water = Game.world().environment().getSpawnpoint("water-spawn");
      cam.setFocus(water.getCenter());
      Game.world().setCamera(cam);
      Game.loop().perform(1000, () -> {
        Game.world().environment().add(new SpawnEmitter(water));
        Game.audio().playSound("water-splash.ogg");
        Creature waterMonger = new Creature("monger_water");
        waterMonger.setWidth(11);
        waterMonger.setHeight(15);
        water.spawn(waterMonger);
        Game.loop().perform(2500, () -> {
          SpeechBubble bubble = SpeechBubble.create(waterMonger, ".... ", GameManager.SPEECH_BUBBLE_APPEARANCE, GameManager.SPEECH_BUBBLE_FONT);
          bubble.addListener(new SpeechBubbleListener() {
            @Override
            public void hidden() {
              Game.audio().playSound("success.ogg");
              SpeechBubble bubble2 = SpeechBubble.create(waterMonger, ":) ", GameManager.SPEECH_BUBBLE_APPEARANCE, GameManager.SPEECH_BUBBLE_FONT);
              bubble2.setTextDisplayTime(5000);
              Game.loop().perform(5000, () -> {
                Game.window().getRenderComponent().fadeOut(5000);
                Game.loop().perform(5000, () -> {
                  Game.screens().display("MENU");
                  Game.window().getRenderComponent().fadeIn(1000);
                });
              });
            }
          });
        });
      });
    });
  }

  private static GameState state = GameState.MENU;

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
      if (e.getMap().getName().equals("title")) {
        return;
      }

      Game.loop().perform(500, () -> Game.window().getRenderComponent().fadeIn(500));

      if (startups.containsKey(e.getMap().getName())) {
        startups.get(e.getMap().getName()).run();
      }

      if (e.getMap().getName().equals("end")) {
        return;
      }

      setState(GameState.INGAME);
      Player.instance().getHitPoints().setToMaxValue();
      Player.instance().setIndestructible(false);
      Player.instance().setCollision(true);
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

  public static String getCity(String levelName) {
    return Game.world().getEnvironment(levelName).getMap().getStringValue(MapProperty.MAP_TITLE);
  }

  public static String getCurrentCity() {
    return getCity(Game.world().environment().getMap().getName());
  }

  public static GameState getState() {
    return state;
  }

  public static void setState(GameState state) {
    GameManager.state = state;

    if (getState() == GameState.INGAME_MENU) {
      Game.loop().setTimeScale(0);
      IngameScreen.ingameMenu.setVisible(true);
    } else {
      Game.loop().setTimeScale(1);
      IngameScreen.ingameMenu.setVisible(false);
    }

    if (getState() == GameState.SLAVES_DEAD) {
      Game.audio().playSound("fail.ogg");
      Player.instance().setState(PlayerState.LOCKED);
      Player.instance().setIndestructible(true);
    }
  }
}
