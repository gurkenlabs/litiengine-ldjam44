package de.gurkenlabs.ldjam44.entities;

import java.awt.Color;
import java.awt.geom.Point2D;
import java.util.EnumMap;
import java.util.Map;
import java.util.stream.Collectors;

import de.gurkenlabs.ldjam44.GameManager;
import de.gurkenlabs.ldjam44.graphics.SpawnEmitter;
import de.gurkenlabs.litiengine.Game;
import de.gurkenlabs.litiengine.Valign;
import de.gurkenlabs.litiengine.annotation.AnimationInfo;
import de.gurkenlabs.litiengine.annotation.CollisionInfo;
import de.gurkenlabs.litiengine.annotation.CombatInfo;
import de.gurkenlabs.litiengine.annotation.EntityInfo;
import de.gurkenlabs.litiengine.annotation.MovementInfo;
import de.gurkenlabs.litiengine.environment.Environment;
import de.gurkenlabs.litiengine.graphics.CreatureShadowImageEffect;
import de.gurkenlabs.litiengine.gui.SpeechBubble;
import de.gurkenlabs.litiengine.gui.SpeechBubbleAppearance;
import de.gurkenlabs.litiengine.util.MathUtilities;

@AnimationInfo(spritePrefix = { "enemy_gold", "enemy_silver", "enemy_leather" })
@CombatInfo(hitpoints = 5, team = 2, isIndestructible = true)
@MovementInfo(velocity = 32)
@CollisionInfo(collisionBoxWidth = 5f, collisionBoxHeight = 8f, collision = true)
@EntityInfo(width = 17, height = 21)
public class Enemy extends Mob {

  public enum EnemyType {
    leather,
    silver,
    gold
  }

  public static final String SLAVE_TRIGGER = "GIVE ME SLAVES";

  private static final Map<EnemyType, Integer> slaves = new EnumMap<>(EnemyType.class);
  private static final Map<EnemyType, Integer> hp = new EnumMap<>(EnemyType.class);

  private EnemyType type = EnemyType.leather;

  private final EnemyStrike strike;
  private boolean engaged;

  static {
    hp.put(EnemyType.leather, 5);
    hp.put(EnemyType.silver, 7);
    hp.put(EnemyType.gold, 10);

    slaves.put(EnemyType.leather, 1);
    slaves.put(EnemyType.silver, 2);
    slaves.put(EnemyType.gold, 3);
  }

  public Enemy() {
    this.setIndestructible(true);
    this.strike = new EnemyStrike(this);

    this.addController(new EnemyController(this));

    this.addMessageListener(l -> {
      if (l.getMessage() == null) {
        return;
      }

      if (l.getMessage().equals(SLAVE_TRIGGER)) {
        SpeechBubbleAppearance appearance = new SpeechBubbleAppearance(new Color(16, 20, 19), new Color(255, 255, 255, 150), new Color(16, 20, 19), 5);
        appearance.setBackgroundColor2(new Color(255, 255, 255, 220));
        SpeechBubble.create(this, "FEEEEELL MY WRATH!!!!!" + this.getType(), appearance, GameManager.SPEECH_BUBBLE_FONT);
        this.setEngaged(true);
        this.setIndestructible(false);
      }
    });

    this.addDeathListener(l -> {
      if (this.getType() == EnemyType.silver) {
        this.setCollisionBoxValign(Valign.MIDDLE);
      } else {
        this.setCollisionBoxValign(Valign.TOP);
      }
      for (int i = 0; i < slaves.get(this.getType()); i++) {
        Point2D spawn = new Point2D.Double(this.getCenter().getX() + MathUtilities.randomInRange(-10, 10), this.getCenter().getY() + MathUtilities.randomInRange(-10, 10));

        Game.loop().perform(500 * (i + 1), () -> {
          Slave newSlave = new Slave(spawn);
          Game.world().environment().add(new SpawnEmitter(newSlave));
          Game.world().environment().add(newSlave);
        });
      }
      for (Slave s : Game.world().environment().getByType(Slave.class).stream().filter(sla -> sla.getOwner() != null && sla.getOwner().getMapId() == this.getMapId()).collect(Collectors.toList())) {
        Game.world().environment().add(new SpawnEmitter(s));

        Game.loop().perform(250, () -> {
          Game.world().environment().remove(s);
        });
      }
    });
  }

  @Override
  public void loaded(Environment environment) {
    super.loaded(environment);

    this.getAnimationController().add(new CreatureShadowImageEffect(this, new Color(0, 0, 0, 150)));
  }

  public EnemyType getType() {
    return type;
  }

  public void setType(EnemyType type) {
    this.type = type;
    this.initByType();
  }

  private void initByType() {
    this.getHitPoints().setMaxValue(hp.get(this.getType()));
    this.getHitPoints().setToMaxValue();
  }

  public boolean isEngaged() {
    return engaged;
  }

  public void setEngaged(boolean engaged) {
    this.engaged = engaged;
  }

  public Strike getStrike() {
    return strike;
  }
}
