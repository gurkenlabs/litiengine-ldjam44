package de.gurkenlabs.ldjam44.entities;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import de.gurkenlabs.ldjam44.GameManager;
import de.gurkenlabs.ldjam44.abilities.Charge;
import de.gurkenlabs.ldjam44.abilities.EnemyStrike;
import de.gurkenlabs.ldjam44.abilities.Strike;
import de.gurkenlabs.ldjam44.graphics.SpawnEmitter;
import de.gurkenlabs.litiengine.Game;
import de.gurkenlabs.litiengine.Valign;
import de.gurkenlabs.litiengine.annotation.AnimationInfo;
import de.gurkenlabs.litiengine.annotation.CollisionInfo;
import de.gurkenlabs.litiengine.annotation.CombatInfo;
import de.gurkenlabs.litiengine.annotation.EntityInfo;
import de.gurkenlabs.litiengine.annotation.MovementInfo;
import de.gurkenlabs.litiengine.entities.Creature;
import de.gurkenlabs.litiengine.graphics.IRenderable;
import de.gurkenlabs.litiengine.graphics.RenderEngine;
import de.gurkenlabs.litiengine.graphics.ShapeRenderer;
import de.gurkenlabs.litiengine.graphics.Spritesheet;
import de.gurkenlabs.litiengine.graphics.animation.Animation;
import de.gurkenlabs.litiengine.graphics.animation.CreatureAnimationController;
import de.gurkenlabs.litiengine.graphics.animation.IAnimationController;
import de.gurkenlabs.litiengine.gui.SpeechBubble;
import de.gurkenlabs.litiengine.gui.SpeechBubbleAppearance;
import de.gurkenlabs.litiengine.resources.Resources;
import de.gurkenlabs.litiengine.util.Imaging;
import de.gurkenlabs.litiengine.util.MathUtilities;
import de.gurkenlabs.litiengine.util.geom.GeometricUtilities;

@AnimationInfo(spritePrefix = { "enemy_gold", "enemy_silver", "enemy_leather" })
@CombatInfo(hitpoints = 5, team = 2, isIndestructible = true)
@MovementInfo(velocity = 32)
@CollisionInfo(collisionBoxWidth = 5f, collisionBoxHeight = 8f, collision = true)
@EntityInfo(width = 17, height = 21)
public class Enemy extends Mob implements IRenderable {

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
  private final Charge charge;
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
    this.charge = new Charge(this);

    this.addController(new EnemyController(this));
    this.initAnimationController();
    this.addMessageListener(l -> {
      if (l.getMessage() == null) {
        return;
      }

      if (l.getMessage().equals(SLAVE_TRIGGER)) {
        SpeechBubbleAppearance appearance = new SpeechBubbleAppearance(new Color(16, 20, 19), new Color(255, 255, 255, 150), new Color(16, 20, 19), 5);
        appearance.setBackgroundColor2(new Color(255, 255, 255, 220));
        SpeechBubble.create(this, "FEEEEELL MY WRATH!!!!!", appearance, GameManager.SPEECH_BUBBLE_FONT);

        Game.loop().perform(2000, () -> {
          this.setEngaged(true);
          this.setIndestructible(false);
        });
      }
    });

    this.addDeathListener(l -> {
      if (this.getType() == EnemyType.silver) {
        this.setCollisionBoxValign(Valign.MIDDLE);
      } else {
        this.setCollisionBoxValign(Valign.TOP);
      }

      // despawn owned slaves
      List<Slave> ownedSlaves = Game.world().environment().getByType(Slave.class).stream().filter(sla -> sla.getOwner() != null && sla.getOwner().getMapId() == this.getMapId()).collect(Collectors.toList());
      for (Slave s : ownedSlaves) {
        Game.world().environment().add(new SpawnEmitter(s));

        Game.loop().perform(250, () -> {
          Game.world().environment().remove(s);
        });
      }

      // spawn player slaves
      int spawns = !ownedSlaves.isEmpty() ? ownedSlaves.size() : slaves.get(this.getType());
      for (int i = 0; i < spawns; i++) {
        Point2D spawn = new Point2D.Double(this.getCenter().getX() + MathUtilities.randomInRange(-10, 10), this.getCenter().getY() + MathUtilities.randomInRange(-10, 10));

        Game.loop().perform(500 * (i + 1), () -> {
          Slave newSlave = new Slave(spawn);
          newSlave.setSpritePrefix("slave-monger");
          Game.world().environment().add(new SpawnEmitter(newSlave));
          Game.world().environment().add(newSlave);
        });
      }
    });
  }

  @Override
  public void render(Graphics2D g) {
    double prep = this.getController(EnemyController.class).getPreparation();
    if (prep != 0) {
      double angle = GeometricUtilities.calcRotationAngleInDegrees(this.getCollisionBoxCenter(), Player.instance().getCenter());
      int dots = (int) (prep / 0.2);

      double delta = 15;
      g.setColor(new Color(0, 100, 100, 255));
      for (int i = 0; i < dots; i++) {
        Point2D target = GeometricUtilities.project(this.getCollisionBoxCenter(), angle, delta + delta * i);
        RenderEngine.renderShape(g, new Rectangle2D.Double(target.getX() - 0.5, target.getY() - 0.5, 1, 1));
      }
    }

    if (Game.config().debug().isDebug()) {
      this.strike.render(g);
    }
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

  public Charge getCharge() {
    return charge;
  }

  private void initAnimationController() {
    IAnimationController controller = this.getAnimationController();

    if (this.getSpritePrefix().equals("enemy_silver")) {
      Spritesheet prepare = Resources.spritesheets().get("enemy_silver-prepare");

      final BufferedImage leftPrepare = Imaging.flipSpritesHorizontally(prepare);
      Spritesheet prepareLeft = Resources.spritesheets().load(leftPrepare, "enemy_silver-prepare-left", prepare.getSpriteWidth(), prepare.getSpriteHeight());

      controller.add(new Animation(prepare, false, 500, 500, 1000));
      controller.add(new Animation(prepareLeft, false, 500, 500, 1000));
    } else if (this.getSpritePrefix().equals("enemy_gold")) {
      Spritesheet prepare = Resources.spritesheets().get("enemy_gold-prepare");

      final BufferedImage leftPrepare = Imaging.flipSpritesHorizontally(prepare);
      Spritesheet prepareLeft = Resources.spritesheets().load(leftPrepare, "enemy_gold-prepare-left", prepare.getSpriteWidth(), prepare.getSpriteHeight());

      controller.add(new Animation(prepare, false, 500, 500, 1000));
      controller.add(new Animation(prepareLeft, false, 500, 500, 1000));

    }
  }

  @Override
  protected void updateAnimationController() {
    super.updateAnimationController();
    this.initAnimationController();
  }
}
