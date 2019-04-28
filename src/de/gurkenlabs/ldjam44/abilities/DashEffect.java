package de.gurkenlabs.ldjam44.abilities;

import de.gurkenlabs.ldjam44.entities.Player;
import de.gurkenlabs.ldjam44.graphics.LandEmitter;
import de.gurkenlabs.ldjam44.graphics.SpawnEmitter;
import de.gurkenlabs.litiengine.Game;
import de.gurkenlabs.litiengine.abilities.Ability;
import de.gurkenlabs.litiengine.abilities.effects.Effect;
import de.gurkenlabs.litiengine.abilities.effects.EffectApplication;
import de.gurkenlabs.litiengine.abilities.effects.EffectTarget;
import de.gurkenlabs.litiengine.entities.ICombatEntity;

public class DashEffect extends Effect {
  private double angle;
  private double factor;

  public DashEffect(final Ability ability) {
    super(ability, EffectTarget.EXECUTINGENTITY);
  }

  @Override
  public void update() {
    final long deltaTime = Game.loop().getDeltaTime();
    final double maxPixelsPerTick = this.getAbility().getAttributes().getValue().getCurrentValue() / 1000.0 * Math.min(deltaTime, 50);
    Game.physics().move(Player.instance(), this.angle, maxPixelsPerTick);

    if (this.factor != 0) {
      Player.instance().setWidth(Player.instance().getWidth() * this.factor);
      Player.instance().setHeight(Player.instance().getHeight() * this.factor);
    }
    super.update();
  }

  @Override
  protected void apply(final ICombatEntity entity) {
    this.angle = Player.instance().getAngle();
    Player.instance().setWidth(11);
    Player.instance().setHeight(20);
    this.factor = 1.025;
    Game.loop().perform(this.getAbility().getAttributes().getDuration().getCurrentValue() / 2, () -> {
      this.factor = 0.975;
    });
    Player.instance().setScaling(true);

    super.apply(entity);
  }

  @Override
  protected void cease(EffectApplication appliance) {
    Player.instance().setWidth(11);
    Player.instance().setHeight(20);
    Player.instance().setScaling(false);

    Game.world().environment().add(new LandEmitter(Player.instance()));
    super.cease(appliance);
  }
}
