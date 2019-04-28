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
import de.gurkenlabs.litiengine.graphics.TextRenderer;
import de.gurkenlabs.litiengine.graphics.animation.AnimationController;
import de.gurkenlabs.litiengine.gui.GuiComponent;
import de.gurkenlabs.litiengine.resources.Resources;

public class Hud extends GuiComponent {
  public final AnimationController useButtonAnimationController = new AnimationController(Resources.spritesheets().get("hud-use-button"));

  protected Hud() {
    super(0, 0, Game.window().getResolution().getWidth(), Game.window().getResolution().getHeight());
    Game.loop().attach(this.useButtonAnimationController);
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
    }

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
      ImageRenderer.render(g, useButton, (loc.getX() * Game.graphics().getBaseRenderScale() - useButton.getWidth() / 2.0), loc.getY() * Game.graphics().getBaseRenderScale() - (useButton.getHeight() * 2.5));
    }
  }
}
