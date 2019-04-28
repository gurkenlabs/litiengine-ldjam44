package de.gurkenlabs.ldjam44;

import java.awt.event.KeyEvent;

import de.gurkenlabs.ldjam44.entities.Enemy;
import de.gurkenlabs.ldjam44.entities.Player;
import de.gurkenlabs.litiengine.Game;
import de.gurkenlabs.litiengine.entities.ICombatEntity;
import de.gurkenlabs.litiengine.input.Input;
import de.gurkenlabs.litiengine.util.geom.GeometricUtilities;

public final class PlayerInput {
  public static void init() {
    Input.keyboard().onKeyPressed(KeyEvent.VK_SPACE, e -> {
      Player.instance().getStrike().cast();
    });

    Input.keyboard().onKeyPressed(KeyEvent.VK_SHIFT, e -> {
      Player.instance().getDash().cast();
    });

    Input.keyboard().onKeyPressed(KeyEvent.VK_E, e -> {
      for (ICombatEntity entity : Game.world().environment().findCombatEntities(GeometricUtilities.extrude(Player.instance().getBoundingBox(), 2))) {
        if (entity instanceof Enemy) {
          Enemy enemy = (Enemy) entity;
          enemy.sendMessage(Player.instance(), Enemy.SLAVE_TRIGGER);
        }
      }
    });
  }
}
