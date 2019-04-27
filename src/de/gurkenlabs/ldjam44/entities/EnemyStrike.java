package de.gurkenlabs.ldjam44.entities;

import de.gurkenlabs.litiengine.annotation.AbilityInfo;
import de.gurkenlabs.litiengine.entities.Creature;

@AbilityInfo(name = "Strike", cooldown = 1500, range = 0, impact = 10, impactAngle = 360, value = 1, duration = 400, multiTarget = true)
public class EnemyStrike extends Strike {

  protected EnemyStrike(Creature executor) {
    super(executor);
  }

}
