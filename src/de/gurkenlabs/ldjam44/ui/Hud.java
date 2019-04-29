package de.gurkenlabs.ldjam44.ui;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;

import com.jcraft.jogg.Buffer;

import de.gurkenlabs.ldjam44.GameManager;
import de.gurkenlabs.ldjam44.entities.Enemy;
import de.gurkenlabs.ldjam44.entities.Gatekeeper;
import de.gurkenlabs.ldjam44.entities.Player;
import de.gurkenlabs.ldjam44.entities.Player.PlayerState;
import de.gurkenlabs.litiengine.Game;
import de.gurkenlabs.litiengine.graphics.ImageRenderer;
import de.gurkenlabs.litiengine.graphics.RenderEngine;
import de.gurkenlabs.litiengine.graphics.Spritesheet;
import de.gurkenlabs.litiengine.graphics.TextRenderer;
import de.gurkenlabs.litiengine.graphics.animation.AnimationController;
import de.gurkenlabs.litiengine.gui.GuiComponent;
import de.gurkenlabs.litiengine.resources.Resources;
import de.gurkenlabs.litiengine.util.Imaging;

public class Hud extends GuiComponent {
  private final BufferedImage HEART = Imaging.scale(Resources.images().get("heart.png"), 5.0);
  private final BufferedImage HEART_EMPTY = Imaging.scale(Resources.images().get("heart-empty.png"), 5.0);
  private final AnimationController useButtonAnimationController = new AnimationController(Resources.spritesheets().get("hud-use-button"));
  private final AnimationController arrowAnimationController;
  private final AnimationController questAnimationController;

  protected Hud() {
    super(0, 0, Game.window().getResolution().getWidth(), Game.window().getResolution().getHeight());
    Game.loop().attach(this.useButtonAnimationController);

    Spritesheet arrow = Resources.spritesheets().load(Resources.images().get("arrow.png"), "arrow", 23, 28);
    this.arrowAnimationController = new AnimationController(arrow);
    Game.loop().attach(this.arrowAnimationController);

    Spritesheet quest = Resources.spritesheets().load(Resources.images().get("quest.png"), "arrow", 23, 28);
    this.questAnimationController = new AnimationController(quest);
    Game.loop().attach(this.questAnimationController);
  }

  @Override
  public void render(Graphics2D g) {
    super.render(g);

    g.setColor(Color.RED);

    if (Game.world().environment() == null || Player.instance().getState() != PlayerState.CONTROLLABLE) {
      return;
    }

    this.renderEnemyUI(g);
    this.renderKeeperUI(g);
    this.renderHP(g);
    this.renderSlaves(g);
    TextRenderer.render(g, "Slaves: " + GameManager.getAliveSlaveCount(), 250, 150);
    TextRenderer.render(g, "My slaves: " + GameManager.getOwnSlaveCount(), 250, 180);
    TextRenderer.render(g, "Required: " + GameManager.getRequiredSlaveCount(), 250, 210);

    if (Player.instance().isDead()) {
      g.setFont(g.getFont().deriveFont(20f));
      TextRenderer.render(g, "YOU ARE DEAD", Game.window().getCenter());
    }

    this.renderUseButton(g);
  }

  private void renderKeeperUI(Graphics2D g) {
    final Gatekeeper keeper = GameManager.getGateKeeper();
    if (keeper == null) {
      return;
    }

    if (keeper.getRequiredSlaves() > GameManager.getOwnSlaveCount()) {
      return;
    }

    // display exclamation icon as soon as slave count is met
    BufferedImage arrow = this.questAnimationController.getCurrentSprite(46, 56);

    final Point2D loc = Game.world().camera().getViewportLocation(keeper.getCenter());
    ImageRenderer.render(g, arrow, (loc.getX() * Game.world().camera().getRenderScale() - arrow.getWidth() / 2.0), loc.getY() * Game.world().camera().getRenderScale() - (arrow.getHeight() * 2.5));

  }

  private void renderEnemyUI(Graphics2D g) {
    for (Enemy enemy : Game.world().environment().getByType(Enemy.class)) {
      if (enemy.isEngaged()) {
        RenderEngine.renderText(g, enemy.getHitPoints().getCurrentValue().toString(), enemy.getCenter());
      }

      if (!enemy.isEngaged() && !enemy.isEngaging()) {
        BufferedImage arrow = this.arrowAnimationController.getCurrentSprite(46, 56);

        final Point2D loc = Game.world().camera().getViewportLocation(enemy.getCenter());
        ImageRenderer.render(g, arrow, (loc.getX() * Game.world().camera().getRenderScale() - arrow.getWidth() / 2.0), loc.getY() * Game.world().camera().getRenderScale() - (arrow.getHeight() * 2.5));
      }
    }
  }

  private void renderSlaves(Graphics2D g) {

  }

  private void renderHP(Graphics2D g) {
    final int PADDING = 10;
    double y = Game.window().getResolution().getHeight() - PADDING * 2 - HEART.getHeight();
    double x = Game.window().getResolution().getWidth() / 2.0 - (Player.instance().getHitPoints().getMaxValue() * (HEART.getWidth() + PADDING) * 0.5);
    for (int i = 0; i < Player.instance().getHitPoints().getMaxValue(); i++) {
      BufferedImage img = i < Player.instance().getHitPoints().getCurrentValue() ? HEART : HEART_EMPTY;
      ImageRenderer.render(g, img, x + i * img.getWidth() + PADDING, y);
    }
  }

  private void renderUseButton(Graphics2D g) {
    if (Player.instance().getState() == PlayerState.CONTROLLABLE && (Player.instance().canTrigger() || Player.instance().canTalkToGateKeeper())) {
      BufferedImage useButton = this.useButtonAnimationController.getCurrentSprite(48, 48);

      final Point2D loc = Game.world().camera().getViewportLocation(Player.instance().getCenter());
      ImageRenderer.render(g, useButton, (loc.getX() * Game.world().camera().getRenderScale() - useButton.getWidth() / 2.0), loc.getY() * Game.world().camera().getRenderScale() - (useButton.getHeight() * 2.5));
    }
  }
}
