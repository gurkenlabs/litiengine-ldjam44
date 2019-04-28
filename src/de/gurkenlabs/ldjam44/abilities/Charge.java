package de.gurkenlabs.ldjam44.abilities;

import de.gurkenlabs.litiengine.abilities.Ability;
import de.gurkenlabs.litiengine.annotation.AbilityInfo;
import de.gurkenlabs.litiengine.entities.Creature;

@AbilityInfo(name = "Charge", cooldown = 5000, value = 200, duration = 300)
public class Charge extends Ability {

  public Charge(Creature executor) {
    super(executor);

    this.addEffect(new ChargeEffect(this));
  }
}