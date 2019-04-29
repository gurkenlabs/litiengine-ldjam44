package de.gurkenlabs.ldjam44;

import java.awt.event.KeyEvent;

import de.gurkenlabs.ldjam44.GameManager.GameState;
import de.gurkenlabs.ldjam44.entities.Enemy;
import de.gurkenlabs.ldjam44.entities.Gatekeeper;
import de.gurkenlabs.ldjam44.entities.Player;
import de.gurkenlabs.ldjam44.entities.Player.PlayerState;
import de.gurkenlabs.litiengine.Game;
import de.gurkenlabs.litiengine.entities.ICombatEntity;
import de.gurkenlabs.litiengine.input.Input;
import de.gurkenlabs.litiengine.util.geom.GeometricUtilities;

public final class PlayerInput {
  public static void init() {

    Input.keyboard().onKeyReleased(KeyEvent.VK_ESCAPE, e -> {
      if (Player.instance().getState() == PlayerState.LOCKED || Player.instance().isDead()) {
        return;
      }

      if (GameManager.getState() == GameState.INGAME_MENU) {
        GameManager.setState(GameState.INGAME);
      } else if (GameManager.getState() == GameState.INGAME) {
        GameManager.setState(GameState.INGAME_MENU);
      }
    });

    Input.keyboard().onKeyPressed(KeyEvent.VK_SPACE, e -> {
      if (Player.instance().getState() != PlayerState.CONTROLLABLE) {
        return;
      }

      Player.instance().getStrike().cast();
    });

    Input.keyboard().onKeyPressed(KeyEvent.VK_SHIFT, e -> {
      if (Player.instance().getState() != PlayerState.CONTROLLABLE) {
        return;
      }
      Player.instance().getDash().cast();
    });

    Input.keyboard().onKeyPressed(KeyEvent.VK_E, e -> {
      if (Player.instance().getState() != PlayerState.CONTROLLABLE) {
        return;
      }

      boolean triggered = false;
      for (ICombatEntity entity : Game.world().environment().findCombatEntities(GeometricUtilities.extrude(Player.instance().getBoundingBox(), 2))) {
        if (entity instanceof Enemy) {
          Enemy enemy = (Enemy) entity;
          enemy.sendMessage(Player.instance(), Enemy.SLAVE_TRIGGER);
          triggered = true;
        }
      }

      if (triggered) {
        return;
      }

      if (!Player.instance().canTalkToGateKeeper()) {
        return;
      }

      Gatekeeper keeper = GameManager.getGateKeeper();
      if (keeper == null) {
        return;
      }

      keeper.sendMessage(Player.instance(), Gatekeeper.MESSAGE_FINISH);
    });
  }
}
