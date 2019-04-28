package de.gurkenlabs.ldjam44.abilities;

import de.gurkenlabs.litiengine.abilities.Ability;
import de.gurkenlabs.litiengine.annotation.AbilityInfo;
import de.gurkenlabs.litiengine.entities.Creature;

@AbilityInfo(name = "Charge", cooldown = 5000, value = 200, duration = 300)
public class Charge extends Ability {
  private ChargeEffect chargeEffect;

  public Charge(Creature executor) {
    super(executor);

    this.chargeEffect = new ChargeEffect(this);
    this.addEffect(chargeEffect);
  }

  public double getAngle() {
    return this.chargeEffect.getAngle();
  }
}