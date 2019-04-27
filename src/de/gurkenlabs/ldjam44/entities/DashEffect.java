package de.gurkenlabs.ldjam44.entities;

import de.gurkenlabs.litiengine.Game;
import de.gurkenlabs.litiengine.abilities.Ability;
import de.gurkenlabs.litiengine.abilities.effects.Effect;
import de.gurkenlabs.litiengine.abilities.effects.EffectTarget;
import de.gurkenlabs.litiengine.entities.ICombatEntity;
import de.gurkenlabs.litiengine.input.Input;
import de.gurkenlabs.litiengine.util.geom.GeometricUtilities;

public class DashEffect extends Effect {
  private double angle;

  public DashEffect(final Ability ability) {
    super(ability, EffectTarget.EXECUTINGENTITY);
  }

  @Override
  public void update() {
    final long deltaTime = Game.loop().getDeltaTime();
    final double maxPixelsPerTick = this.getAbility().getAttributes().getValue().getCurrentValue() / 1000.0 * Math.min(deltaTime, 50);
    Game.physics().move(Player.instance(), this.angle, maxPixelsPerTick);

    super.update();
  }

  @Override
  protected void apply(final ICombatEntity entity) {
    this.angle = Player.instance().getAngle();
    String dash = "lepus-dash";
    if (this.angle > 180) {
      dash = "lepus-dash-left";
    }
    // Player.instance().getAnimationController().playAnimation(dash);
    super.apply(entity);
  }
}
