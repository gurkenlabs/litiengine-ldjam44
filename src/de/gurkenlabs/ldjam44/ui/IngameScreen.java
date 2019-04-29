package de.gurkenlabs.ldjam44.ui;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;

import de.gurkenlabs.ldjam44.GameManager;
import de.gurkenlabs.ldjam44.GameManager.GameState;
import de.gurkenlabs.ldjam44.entities.Player;
import de.gurkenlabs.ldjam44.entities.Player.PlayerState;
import de.gurkenlabs.litiengine.Game;
import de.gurkenlabs.litiengine.environment.Environment;
import de.gurkenlabs.litiengine.environment.tilemap.MapProperty;
import de.gurkenlabs.litiengine.graphics.ImageRenderer;
import de.gurkenlabs.litiengine.graphics.TextRenderer;
import de.gurkenlabs.litiengine.gui.screens.Screen;
import de.gurkenlabs.litiengine.resources.Resources;
import de.gurkenlabs.litiengine.util.MathUtilities;

public class IngameScreen extends Screen {
  private static final int LEVELNAME_DURATION = 7000;
  private final BufferedImage NOTE_DEATH = Resources.images().get("died.png");
  private final BufferedImage NOTE_SLAVES = Resources.images().get("slaves-killed.png");

  public static final String NAME = "INGAME-SCREEN";
  private static final int CINEMATIC_BORDER = 100;
  public static KeyboardMenu ingameMenu;
  public static KeyboardMenu deathMenu;

  private Hud hud;

  public static long levelNameTick;

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

      final double controlsY = Game.window().getResolution().getHeight() - MenuScreen.CONTROLS.getHeight() - 20;
      ImageRenderer.render(g, MenuScreen.CONTROLS, 20, controlsY);
    }

    if (GameManager.getState() == GameState.SLAVES_DEAD) {
      double x = Game.window().getCenter().getX() - NOTE_SLAVES.getWidth() / 2.0;
      double y = Game.window().getCenter().getY() - NOTE_SLAVES.getHeight();
      ImageRenderer.render(g, NOTE_SLAVES, x, y);
    } else if (GameManager.getState() == GameState.INGAME && Player.instance().isDead()) {
      double x = Game.window().getCenter().getX() - NOTE_DEATH.getWidth() / 2.0;
      double y = Game.window().getCenter().getY() - NOTE_DEATH.getHeight();
      ImageRenderer.render(g, NOTE_DEATH, x, y);
    }

    super.render(g);

    // render level name
    if (Game.world().environment() != null && levelNameTick != 0 && GameManager.getState() == GameState.INGAME) {
      long deltaTime = Game.time().since(levelNameTick);

      if (deltaTime > 1000 && deltaTime < LEVELNAME_DURATION) {
        // fade out status color
        final double fadeOutTime = 0.75 * LEVELNAME_DURATION;

        int alpha = 255;
        if (deltaTime > fadeOutTime) {
          double fade = deltaTime - fadeOutTime;
          alpha = (int) (255 - (fade / (LEVELNAME_DURATION - fadeOutTime)) * 255);
          alpha = MathUtilities.clamp(alpha, 0, 255);
        }

        g.setColor(new Color(255, 255, 255, alpha));

        // TITLE
        Font old = g.getFont();
        g.setFont(GameManager.GUI_FONT.deriveFont(60f));
        FontMetrics fm = g.getFontMetrics();

        String cityName = GameManager.getCity(Game.world().environment().getMap().getName());
        double x = Game.window().getCenter().getX() - fm.stringWidth(cityName) / 2.0;
        TextRenderer.renderWithOutline(g, cityName, x, 150, new Color(0, 0, 0, alpha), RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

        // DESCRIPTION
        String description = Game.world().environment().getMap().getStringValue(MapProperty.MAP_DESCRIPTION);
        if (description != null && !description.isEmpty()) {
          g.setFont(GameManager.GUI_FONT.deriveFont(32f));
          FontMetrics fm2 = g.getFontMetrics();

          double x2 = Game.window().getCenter().getX() - fm2.stringWidth(description) / 2.0;
          TextRenderer.renderWithOutline(g, description, x2, 210, new Color(0, 0, 0, alpha), RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
          g.setFont(old);
        }
      }
    }
  }
}
