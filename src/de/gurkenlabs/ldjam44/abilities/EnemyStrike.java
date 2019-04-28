package de.gurkenlabs.ldjam44.abilities;

import de.gurkenlabs.litiengine.abilities.AbilityOrigin;
import de.gurkenlabs.litiengine.annotation.AbilityInfo;
import de.gurkenlabs.litiengine.entities.Creature;

@AbilityInfo(name = "Strike", cooldown = 1500, range = 0, impact = 10, impactAngle = 360, value = 1, duration = 400, multiTarget = true, origin = AbilityOrigin.DIMENSION_CENTER)
public class EnemyStrike extends Strike {

  public EnemyStrike(Creature executor) {
    super(executor);
  }

}
