package de.gurkenlabs.ldjam44.ui;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;

import de.gurkenlabs.litiengine.Game;
import de.gurkenlabs.litiengine.IUpdateable;
import de.gurkenlabs.litiengine.graphics.ImageRenderer;
import de.gurkenlabs.litiengine.graphics.TextRenderer;
import de.gurkenlabs.litiengine.gui.Menu;
import de.gurkenlabs.litiengine.gui.screens.Screen;
import de.gurkenlabs.litiengine.resources.Resources;
import de.gurkenlabs.litiengine.util.Imaging;
import de.gurkenlabs.litiengine.util.MathUtilities;

public class MenuScreen extends Screen implements IUpdateable {
  public static final Font MENU_FONT = Resources.fonts().get("Roman.ttf").deriveFont(40f);
  public static final Color ROMAN_RED = new Color(140, 16, 16);

  private static final BufferedImage LOGO_COIN = Resources.images().get("logo_pass2.png");
  private static final BufferedImage LOGO_TEXT = Resources.images().get("text_transparent.png");
  private static final BufferedImage LOGO_LAURELS = Resources.images().get("laurels_transparent.png");
  private static final BufferedImage BG = Imaging.scale(Resources.images().get("menu_bg.png"), Game.window().getWidth(), Game.window().getHeight());
  private static final BufferedImage CLOUD1 = Imaging.scale(Resources.images().get("cloud1.png"), 6f);
  int cloud1Offset = MathUtilities.randomInRange(-40, 30) * 6;
  int cloud2Offset = MathUtilities.randomInRange(-40, 30) * 6;
  int cloud3Offset = MathUtilities.randomInRange(-40, 30) * 6;
  int cloud4Offset = MathUtilities.randomInRange(-40, 30) * 6;
  private static final BufferedImage CLOUD2 = Imaging.scale(Resources.images().get("cloud2.png"), 6f);
  private static final BufferedImage CLOUD3 = Imaging.scale(Resources.images().get("cloud3.png"), 6f);
  private static final BufferedImage CLOUD4 = Imaging.scale(Resources.images().get("cloud4.png"), 6f);
  private static final BufferedImage STATUE = Imaging.scale(Resources.images().get("statue.png"), 6f);
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
    final double centerY = Game.window().getResolution().getHeight() * 1 / 2;
    final double buttonWidth = 600;

    this.mainMenu = new Menu(Game.window().getResolution().getWidth() * 5 / 8, Game.window().getResolution().getHeight() * 1 / 12 + LOGO_COIN.getHeight() / 2 - centerY / 4, buttonWidth, centerY / 2, "Play", "Instructions",
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
    this.getAppearance().setBackgroundColor1(Color.BLUE);
  }

  @Override
  public void prepare() {
    super.prepare();
    Game.loop().attach(this);
    Game.window().getRenderComponent().setBackground(Color.BLACK);
    Game.graphics().setBaseRenderScale(6f * Game.window().getResolutionScale());

    this.mainMenu.getCellComponents().forEach(comp -> {
      comp.setFont(MENU_FONT);
      comp.getAppearance().setForeColor(ROMAN_RED);
      comp.getAppearance().setTextAntialiasing(RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
    });
  }

  @Override
  public void render(final Graphics2D g) {
    ImageRenderer.render(g, BG, 0, 0);
    this.renderScrollingStuff(g);
    final double centerX = Game.window().getResolution().getWidth() / 2.0;
    final double logoX = centerX - LOGO_COIN.getWidth() / 2;
    final double logoY = Game.window().getResolution().getHeight() * 1 / 12;
    ImageRenderer.render(g, LOGO_COIN, logoX, logoY);
    // ImageRenderer.render(g, LOGO_TEXT, logoX, logoY);

    final double stringWidth = g.getFontMetrics().stringWidth(COPYRIGHT);
    g.setColor(Color.WHITE);
    g.setFont(g.getFont().deriveFont(20f));
    TextRenderer.renderWithOutline(g, COPYRIGHT, Game.window().getResolution().getWidth() - stringWidth * 1.5,
        Game.window().getResolution().getHeight() - LOGO_TEXT.getHeight() / 2.0, Color.BLACK);
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

  private void renderScrollingStuff(Graphics2D g) {
    ImageRenderer.render(g, CLOUD1, -CLOUD1.getWidth() + Game.time().now() * 0.1 % (CLOUD1.getWidth() + Game.window().getResolution().getWidth()) + cloud1Offset, cloud1Offset);
    ImageRenderer.render(g, CLOUD2, -CLOUD2.getWidth() + Game.time().now() * 0.2 % (CLOUD2.getWidth() + Game.window().getResolution().getWidth()) + cloud2Offset, cloud2Offset);
    ImageRenderer.render(g, CLOUD3, -CLOUD3.getWidth() + Game.time().now() * 0.3 % (CLOUD3.getWidth() + Game.window().getResolution().getWidth()) + cloud3Offset, cloud3Offset);
    ImageRenderer.render(g, CLOUD4, -CLOUD4.getWidth() + Game.time().now() * 0.4 % (CLOUD4.getWidth() + Game.window().getResolution().getWidth()) + cloud4Offset, cloud4Offset);
    ImageRenderer.render(g, Imaging.horizontalFlip(CLOUD1), -CLOUD1.getWidth() + Game.time().now() * 0.5 % (CLOUD1.getWidth() + Game.window().getResolution().getWidth()) + cloud1Offset, cloud1Offset);
    ImageRenderer.render(g, Imaging.horizontalFlip(CLOUD2), -CLOUD2.getWidth() + Game.time().now() * 0.6 % (CLOUD2.getWidth() + Game.window().getResolution().getWidth()) + cloud2Offset, cloud1Offset);
    ImageRenderer.render(g, Imaging.horizontalFlip(CLOUD3), -CLOUD3.getWidth() + Game.time().now() * 0.7 % (CLOUD3.getWidth() + Game.window().getResolution().getWidth()) + cloud3Offset, cloud2Offset);
    ImageRenderer.render(g, Imaging.horizontalFlip(CLOUD4), -CLOUD4.getWidth() + Game.time().now() * 0.8 % (CLOUD4.getWidth() + Game.window().getResolution().getWidth()) + cloud4Offset, cloud4Offset);
    ImageRenderer.render(g, STATUE, -STATUE.getWidth() + Game.time().now() % (STATUE.getWidth() + Game.window().getResolution().getWidth()), 0);

  }
}
