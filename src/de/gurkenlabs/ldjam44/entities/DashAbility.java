package de.gurkenlabs.ldjam44.entities;

import de.gurkenlabs.litiengine.abilities.Ability;
import de.gurkenlabs.litiengine.annotation.AbilityInfo;
import de.gurkenlabs.litiengine.entities.Creature;

@AbilityInfo(name = "Dash", cooldown = 1000, value = 100, duration = 300)
public class DashAbility extends Ability {

  public DashAbility(final Creature executor) {
    super(executor);
    this.getEffects().add(new DashEffect(this));
  }
}
