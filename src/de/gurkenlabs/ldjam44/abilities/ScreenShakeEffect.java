package de.gurkenlabs.ldjam44.abilities;

import java.awt.Shape;

import de.gurkenlabs.litiengine.Game;
import de.gurkenlabs.litiengine.abilities.Ability;
import de.gurkenlabs.litiengine.abilities.effects.Effect;
import de.gurkenlabs.litiengine.abilities.effects.EffectApplication;
import de.gurkenlabs.litiengine.entities.ICombatEntity;

public class ScreenShakeEffect extends Effect {

  private final int duration;

  private final double intensity;

  public ScreenShakeEffect(final Ability ability, final double intensity, final int duration) {
    super(ability);
    this.duration = duration;
    this.intensity = intensity;
  }

  @Override
  public void apply(final Shape impactArea) {
    super.apply(impactArea);
    Game.world().camera().shake(this.intensity, 30, this.duration);
  }
  
  @Override
  protected void cease(EffectApplication appliance) {
    if (Game.world().environment().getMap().getName().equals("level0")) {
      Game.world().camera().setFocus(Game.world().environment().getCenter());
    }
    
    super.cease(appliance);
  }
}
