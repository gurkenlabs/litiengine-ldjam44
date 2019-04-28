package de.gurkenlabs.ldjam44.ui;

import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;

import de.gurkenlabs.ldjam44.entities.Player;
import de.gurkenlabs.ldjam44.entities.Player.PlayerState;
import de.gurkenlabs.litiengine.Game;
import de.gurkenlabs.litiengine.gui.screens.GameScreen;

public class IngameScreen extends GameScreen {
  public static final String NAME = "INGAME-SCREEN";
  private static final int CINEMATIC_BORDER = 100;

  private Hud hud;

  public IngameScreen() {
    super(NAME);
  }

  @Override
  protected void initializeComponents() {
    this.hud = new Hud();

    this.getComponents().add(this.hud);
  }

  @Override
  public void render(Graphics2D g) {
    if (Player.instance().getState() == PlayerState.LOCKED) {
      g.setClip(new Rectangle2D.Double(0, CINEMATIC_BORDER, Game.window().getResolution().getWidth(), Game.window().getResolution().getHeight() - CINEMATIC_BORDER * 2));
      Game.world().camera().setZoom(1.25f, 600);
    } else {
      Game.world().camera().setZoom(1, 600);
    }
    super.render(g);
  }
}
