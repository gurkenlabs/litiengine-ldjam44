package de.gurkenlabs.ldjam44.entities;

import de.gurkenlabs.ldjam44.entities.Enemy.EnemyType;
import de.gurkenlabs.litiengine.entities.Creature;
import de.gurkenlabs.litiengine.environment.CreatureMapObjectLoader;
import de.gurkenlabs.litiengine.environment.tilemap.IMapObject;

public class EnemyMapObjectLoader extends CreatureMapObjectLoader {

  @Override
  protected Creature createNewCreature(IMapObject mapObject, String spriteSheet, String spawnType) {
    Creature creature = super.createNewCreature(mapObject, spriteSheet, spawnType);
    if (creature instanceof Enemy) {
      Enemy enemy = (Enemy) creature;
      String type = spriteSheet.split("_")[1];
      enemy.setType(Enum.valueOf(EnemyType.class, type));
    }
    return creature;
  }
}
