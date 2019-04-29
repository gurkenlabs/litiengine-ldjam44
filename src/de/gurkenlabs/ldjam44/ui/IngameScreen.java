package de.gurkenlabs.ldjam44.ui;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;

import de.gurkenlabs.ldjam44.GameManager;
import de.gurkenlabs.ldjam44.GameManager.GameState;
import de.gurkenlabs.ldjam44.entities.Player;
import de.gurkenlabs.ldjam44.entities.Player.PlayerState;
import de.gurkenlabs.litiengine.Game;
import de.gurkenlabs.litiengine.environment.Environment;
import de.gurkenlabs.litiengine.graphics.ImageRenderer;
import de.gurkenlabs.litiengine.gui.screens.Screen;

public class IngameScreen extends Screen {
  public static final String NAME = "INGAME-SCREEN";
  private static final int CINEMATIC_BORDER = 100;

  private Hud hud;
  public static KeyboardMenu ingameMenu;
  public static KeyboardMenu deathMenu;

  public IngameScreen() {
    super(NAME);
  }

  @Override
  protected void initializeComponents() {
    this.hud = new Hud();

    final double centerX = Game.window().getResolution().getWidth() / 2.0;
    final double centerY = Game.window().getResolution().getHeight() * 1 / 2;
    final double buttonWidth = 450;

    ingameMenu = new KeyboardMenu(centerX - buttonWidth / 2, centerY * 1.3, buttonWidth, centerY / 2, "Continue", "Exit");
    ingameMenu.onConfirm(c -> {
      switch (c.intValue()) {
      case 0:
        GameManager.setState(GameState.INGAME);
        break;
      case 1:
        System.exit(0);
        break;
      default:
        break;
      }
    });

    deathMenu = new KeyboardMenu(centerX - buttonWidth / 2, centerY * 1.3, buttonWidth, centerY / 2, "Retry", "Exit");
    deathMenu.onConfirm(c -> {
      switch (c.intValue()) {
      case 0:
        Game.window().getRenderComponent().fadeOut(1000);

        Game.loop().perform(1500, () -> {
          // remove player before unloading the environment or the instance's animation controller will be disposed
          Environment current = Game.world().environment();
          Player.instance().setState(PlayerState.LOCKED);
          current.remove(Player.instance());
          Game.world().unloadEnvironment();
          Game.world().loadEnvironment(new Environment(current.getMap().getName()));
        });
        break;
      case 1:
        System.exit(0);
        break;
      default:
        break;
      }
    });

    this.getComponents().add(this.hud);
    this.getComponents().add(ingameMenu);
    this.getComponents().add(deathMenu);
  }

  @Override
  public void render(Graphics2D g) {
    if (Player.instance().getState() == PlayerState.LOCKED) {
      g.setClip(new Rectangle2D.Double(0, CINEMATIC_BORDER, Game.window().getResolution().getWidth(), Game.window().getResolution().getHeight() - CINEMATIC_BORDER * 2));
      Game.world().camera().setZoom(1.25f, 600);
    } else {
      Game.world().camera().setZoom(1, 600);
    }

    if (Game.world().environment() != null) {
      Game.world().environment().render(g);
    }

    deathMenu.setVisible(GameManager.getState() == GameState.INGAME && Player.instance().isDead() || GameManager.getState() == GameState.SLAVES_DEAD);

    if (GameManager.getState() == GameState.INGAME_MENU) {
      g.setColor(new Color(0, 0, 0, 100));
      g.fillRect(0, 0, (int) Game.window().getResolution().getWidth(), (int) Game.window().getResolution().getHeight());

      final double logoX = Game.window().getCenter().getX() - MenuScreen.LOGO_COIN.getWidth() / 2;
      final double logoY = Game.window().getResolution().getHeight() * 1 / 12;
      ImageRenderer.render(g, MenuScreen.LOGO_COIN, logoX, logoY);
    }
    
    // TODO RENDER MESSAGE FOR GameManager.getState() == GameState.SLAVES_DEAD
    // TODO RENDER MESSAGE FOR GameManager.getState() == GameState.INGAME && Player.instance().isDead()

    super.render(g);
  }
}
