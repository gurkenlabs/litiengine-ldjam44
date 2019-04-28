package de.gurkenlabs.ldjam44.abilities;

import de.gurkenlabs.litiengine.abilities.Ability;
import de.gurkenlabs.litiengine.annotation.AbilityInfo;
import de.gurkenlabs.litiengine.entities.Creature;

@AbilityInfo(name = "Dash", cooldown = 1500, value = 80, duration = 500)
public class JumpAbility extends Ability {

  public JumpAbility(final Creature executor) {
    super(executor);
    this.getEffects().add(new JumpEffect(this));
  }
}
