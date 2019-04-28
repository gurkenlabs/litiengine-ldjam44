package de.gurkenlabs.ldjam44.ui;

import java.awt.Color;
import java.awt.Graphics2D;

import de.gurkenlabs.ldjam44.entities.Enemy;
import de.gurkenlabs.ldjam44.entities.Player;
import de.gurkenlabs.ldjam44.entities.Slave;
import de.gurkenlabs.litiengine.Game;
import de.gurkenlabs.litiengine.graphics.RenderEngine;
import de.gurkenlabs.litiengine.graphics.TextRenderer;
import de.gurkenlabs.litiengine.gui.GuiComponent;

public class Hud extends GuiComponent {

  protected Hud() {
    super(0, 0, Game.window().getResolution().getWidth(), Game.window().getResolution().getHeight());
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

    long slavesAlive = Game.world().environment().getByType(Slave.class).stream().filter(x -> !x.isDead()).count();
    TextRenderer.render(g, "Slaves: " + slavesAlive, 250, 150);

    long mySlaves = Game.world().environment().getByType(Slave.class).stream().filter(x -> !x.isDead() && x.getOwner() == null).count();
    TextRenderer.render(g, "My slaves: " + mySlaves, 250, 180);

    if (Player.instance().isDead()) {
      g.setFont(g.getFont().deriveFont(20f));
      TextRenderer.render(g, "YOU ARE DEAD", Game.window().getCenter());
    }
  }
}
