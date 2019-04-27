package de.gurkenlabs.ldjam44.entities;

import de.gurkenlabs.litiengine.abilities.Ability;
import de.gurkenlabs.litiengine.annotation.AbilityInfo;
import de.gurkenlabs.litiengine.entities.Creature;

@AbilityInfo(name = "Dash", cooldown = 1500, value = 80, duration = 500)
public class DashAbility extends Ability {

  public DashAbility(final Creature executor) {
    super(executor);
    this.getEffects().add(new DashEffect(this));
  }
}
