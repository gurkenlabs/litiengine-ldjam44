package de.gurkenlabs.ldjam44.ui;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;

import de.gurkenlabs.ldjam44.GameManager;
import de.gurkenlabs.ldjam44.entities.Enemy;
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

public class Hud extends GuiComponent {
  public final AnimationController useButtonAnimationController = new AnimationController(Resources.spritesheets().get("hud-use-button"));
  public final AnimationController arrowAnimationController;

  protected Hud() {
    super(0, 0, Game.window().getResolution().getWidth(), Game.window().getResolution().getHeight());
    Game.loop().attach(this.useButtonAnimationController);

    Spritesheet arrow = Resources.spritesheets().load(Resources.images().get("arrow.png"), "arrow", 23, 28);
    this.arrowAnimationController = new AnimationController(arrow);
    Game.loop().attach(this.arrowAnimationController);
  }

  @Override
  public void render(Graphics2D g) {
    super.render(g);

    g.setColor(Color.RED);

    if (Game.world().environment() == null) {
      return;
    }

    for (Enemy enemy : Game.world().environment().getByType(Enemy.class)) {
      if (enemy.isEngaged()) {
        RenderEngine.renderText(g, enemy.getHitPoints().getCurrentValue().toString(), enemy.getCenter());
      }

      if (Player.instance().getState() == PlayerState.CONTROLLABLE && !enemy.isEngaged() && !enemy.isEngaging()) {
        BufferedImage arrow = this.arrowAnimationController.getCurrentSprite(46, 56);

        final Point2D loc = Game.world().camera().getViewportLocation(enemy.getCenter());
        ImageRenderer.render(g, arrow, (loc.getX() * Game.world().camera().getRenderScale() - arrow.getWidth() / 2.0), loc.getY() * Game.world().camera().getRenderScale() - (arrow.getHeight() * 2.5));
      }
    }

    TextRenderer.render(g, "HP:" + Player.instance().getHitPoints().getCurrentValue() + "/" + Player.instance().getHitPoints().getMaxValue(), 250, 120);
    TextRenderer.render(g, "Slaves: " + GameManager.getAliveSlaveCount(), 250, 150);
    TextRenderer.render(g, "My slaves: " + GameManager.getOwnSlaveCount(), 250, 180);
    TextRenderer.render(g, "Required: " + GameManager.getRequiredSlaveCount(), 250, 210);

    if (Player.instance().isDead()) {
      g.setFont(g.getFont().deriveFont(20f));
      TextRenderer.render(g, "YOU ARE DEAD", Game.window().getCenter());
    }

    this.renderUseButton(g);
  }

  private void renderUseButton(Graphics2D g) {
    if (Player.instance().getState() == PlayerState.CONTROLLABLE && (Player.instance().canTrigger() || Player.instance().canTalkToGateKeeper())) {
      BufferedImage useButton = this.useButtonAnimationController.getCurrentSprite(48, 48);

      final Point2D loc = Game.world().camera().getViewportLocation(Player.instance().getCenter());
      ImageRenderer.render(g, useButton, (loc.getX() * Game.world().camera().getRenderScale() - useButton.getWidth() / 2.0), loc.getY() * Game.world().camera().getRenderScale() - (useButton.getHeight() * 2.5));
    }
  }
}
