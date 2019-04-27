package de.gurkenlabs.ldjam44;

import java.awt.event.KeyEvent;

import de.gurkenlabs.ldjam44.entities.Player;
import de.gurkenlabs.litiengine.input.Input;

public final class PlayerInput {
  public static void init() {
    Input.keyboard().onKeyPressed(KeyEvent.VK_SPACE, e -> {
      Player.instance().getStrike().cast();
    });
  }
}
