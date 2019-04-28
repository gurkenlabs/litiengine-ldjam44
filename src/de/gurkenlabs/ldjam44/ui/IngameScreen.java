package de.gurkenlabs.ldjam44.ui;

import de.gurkenlabs.litiengine.gui.screens.GameScreen;

public class IngameScreen extends GameScreen {
  public static final String NAME = "INGAME-SCREEN";

  private Hud hud;

  public IngameScreen() {
    super(NAME);
  }

  @Override
  protected void initializeComponents() {
    this.hud = new Hud();

    this.getComponents().add(this.hud);
  }
}
