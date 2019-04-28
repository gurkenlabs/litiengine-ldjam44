package de.gurkenlabs.ldjam44.ui;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

import de.gurkenlabs.litiengine.Game;
import de.gurkenlabs.litiengine.IUpdateable;
import de.gurkenlabs.litiengine.graphics.ImageRenderer;
import de.gurkenlabs.litiengine.graphics.TextRenderer;
import de.gurkenlabs.litiengine.gui.Menu;
import de.gurkenlabs.litiengine.gui.screens.Screen;
import de.gurkenlabs.litiengine.resources.Resources;
import de.gurkenlabs.litiengine.util.Imaging;

public class MenuScreen extends Screen implements IUpdateable {
  private static final BufferedImage BG = Imaging.scale(Resources.images().get("logo.png"),
      Game.graphics().getBaseRenderScale());
  private static final String COPYRIGHT = "© 2019 GURKENLABS.DE";

  public long lastPlayed;

  private Menu mainMenu;

  public MenuScreen() {
    super("MENU");
  }

  private void exit() {
    System.exit(0);
  }

  @Override
  protected void initializeComponents() {
    final double centerX = Game.window().getResolution().getWidth() / 2.0;
    final double centerY = Game.window().getResolution().getHeight() / 3.0 + 10;
    final double buttonWidth = 345;

    this.mainMenu = new Menu(centerX - buttonWidth / 2, centerY, buttonWidth, centerY / 2, "Play", "Instructions",
        "Exit");

    this.getComponents().add(this.mainMenu);
    this.mainMenu.onChange(c -> {
      switch (c.intValue()) {
      case 0:
        this.startGame();
        // load the first level (resources for the map were implicitly loaded
        // from
        // the
        // game file)
        Game.world().loadEnvironment("SlaveMarket_Nubia");
        break;
      case 1:
        this.showInstructions();
        break;
      case 2:
        this.exit();
        break;
      default:
        break;
      }

    });
  }

  @Override
  public void prepare() {
    super.prepare();
    Game.loop().attach(this);
    Game.graphics().setBaseRenderScale(7f * Game.window().getResolutionScale());
  }

  @Override
  public void render(final Graphics2D g) {

    final double centerX = Game.window().getResolution().getWidth() / 2.0;
    final double x = centerX - BG.getWidth() * 0.5 / 2.0;
    final double y = 2;
    ImageRenderer.renderScaled(g, BG, x, y, 0.5);

    final double stringWidth = g.getFontMetrics().stringWidth(COPYRIGHT);
    g.setColor(Color.WHITE);
    g.setFont(g.getFont().deriveFont(20f));
    TextRenderer.renderWithOutline(g, COPYRIGHT, Game.window().getResolution().getWidth() - stringWidth * 1.5,
        Game.window().getResolution().getHeight() - BG.getHeight() / 2.0, Color.BLACK);
    super.render(g);
  }

  private void showInstructions() {
    System.out.println("settings");
  }

  private void startGame() {
    this.mainMenu.setEnabled(false);
    // Game.window().getRenderComponent().fadeOut(2500);
    Game.screens().display("INGAME-SCREEN");
  }

  @Override
  public void suspend() {
    super.suspend();
    Game.loop().detach(this);
    Game.audio().stopMusic();
  }

  @Override
  public void update() {
    if (this.lastPlayed == 0) {
      Game.audio().playMusic(Resources.sounds().get("menumusic.ogg"));
      this.lastPlayed = Game.loop().getTicks();
    }

  }
}
