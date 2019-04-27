package de.gurkenlabs.ldjam44.entities;

import de.gurkenlabs.ldjam44.graphics.HitEmitter;
import de.gurkenlabs.litiengine.Game;
import de.gurkenlabs.litiengine.entities.CombatEntityHitEvent;
import de.gurkenlabs.litiengine.entities.Creature;
import de.gurkenlabs.litiengine.entities.ICombatEntity;
import de.gurkenlabs.litiengine.graphics.emitters.Emitter;

public class Mob extends Creature {

  public Mob() {
    this.addHitListener(e -> {
      spawnHitEmitter(e.getEntity(), e);
    });
  }

  private static void spawnHitEmitter(ICombatEntity entity, CombatEntityHitEvent args) {
    if (args.getDamage() <= 0) {
      return;
    }

    Emitter emitter = new HitEmitter(entity, 10);
    Game.world().environment().add(emitter);
  }
}
