package de.gurkenlabs.ldjam44.entities;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.geom.Ellipse2D;
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
import de.gurkenlabs.ldjam44.abilities.Stomp;
import de.gurkenlabs.ldjam44.graphics.SpawnEmitter;
import de.gurkenlabs.litiengine.Game;
import de.gurkenlabs.litiengine.Valign;
import de.gurkenlabs.litiengine.annotation.AnimationInfo;
import de.gurkenlabs.litiengine.annotation.CollisionInfo;
import de.gurkenlabs.litiengine.annotation.CombatInfo;
import de.gurkenlabs.litiengine.annotation.EntityInfo;
import de.gurkenlabs.litiengine.annotation.MovementInfo;
import de.gurkenlabs.litiengine.environment.Environment;
import de.gurkenlabs.litiengine.graphics.IRenderable;
import de.gurkenlabs.litiengine.graphics.RenderEngine;
import de.gurkenlabs.litiengine.graphics.RenderType;
import de.gurkenlabs.litiengine.graphics.Spritesheet;
import de.gurkenlabs.litiengine.graphics.animation.Animation;
import de.gurkenlabs.litiengine.graphics.animation.IAnimationController;
import de.gurkenlabs.litiengine.gui.SpeechBubble;
import de.gurkenlabs.litiengine.resources.Resources;
import de.gurkenlabs.litiengine.util.Imaging;
import de.gurkenlabs.litiengine.util.MathUtilities;
import de.gurkenlabs.litiengine.util.geom.GeometricUtilities;

@AnimationInfo(spritePrefix = { "enemy_gold", "enemy_silver", "enemy_leather" })
@CombatInfo(hitpoints = 5, team = 2, isIndestructible = true)
@MovementInfo(velocity = 30)
@CollisionInfo(collisionBoxWidth = 5f, collisionBoxHeight = 8f, collision = true)
@EntityInfo(width = 17, height = 21)
public class Enemy extends Mob implements IRenderable {
  public static int GCD = 2000;

  private long lastCast;

  public enum EnemyType {
    leather,
    silver,
    gold
  }

  public static final String SLAVE_TRIGGER = "GIVE ME SLAVES";

  private static final Map<EnemyType, Integer> slaves = new EnumMap<>(EnemyType.class);
  private static final Map<EnemyType, Integer> hp = new EnumMap<>(EnemyType.class);
  private static final Map<EnemyType, Integer> velocity = new EnumMap<>(EnemyType.class);

  private EnemyType type = EnemyType.leather;

  private final EnemyStrike strike;
  private final Charge charge;
  private final Stomp stomp;
  private boolean engaged;
  private boolean engaging;

  static {
    hp.put(EnemyType.leather, 5);
    hp.put(EnemyType.silver, 7);
    hp.put(EnemyType.gold, 10);

    slaves.put(EnemyType.leather, 1);
    slaves.put(EnemyType.silver, 2);
    slaves.put(EnemyType.gold, 3);

    velocity.put(EnemyType.leather, 30);
    velocity.put(EnemyType.silver, 30);
    velocity.put(EnemyType.gold, 33);
  }

  public Enemy() {
    this.setIndestructible(true);
    this.strike = new EnemyStrike(this);
    this.charge = new Charge(this);
    this.stomp = new Stomp(this);

    this.strike.onCast(e -> this.lastCast = Game.time().now());
    this.charge.onCast(e -> this.lastCast = Game.time().now());
    this.stomp.onCast(e -> this.lastCast = Game.time().now());

    this.addController(new EnemyController(this));
    this.initAnimationController();
    this.addMessageListener(l -> {
      if (l.getMessage() == null) {
        return;
      }

      if (!this.engaged && l.getMessage().equals(SLAVE_TRIGGER)) {
        SpeechBubble.create(this, "FEEEEELL MY WRATH!!!!!", GameManager.SPEECH_BUBBLE_APPEARANCE, GameManager.SPEECH_BUBBLE_FONT);
        this.setEngaging(true);
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
    if (prep != 0 && !this.isDead()) {
      double angle = GeometricUtilities.calcRotationAngleInDegrees(this.getCollisionBoxCenter(), Player.instance().getCenter());
      int dots = (int) (prep / 0.125);
      double delta = 10;
      g.setColor(new Color(0, 100, 100, 255));

      for (int i = 0; i < 7; i++) {
        Point2D target = GeometricUtilities.project(this.getCollisionBoxCenter(), angle, delta + delta * i);
        RenderEngine.renderOutline(g, new Rectangle2D.Double(target.getX() - 0.5, target.getY() - 0.5, 1, 1));
      }

      for (int i = 0; i < dots; i++) {
        Point2D target = GeometricUtilities.project(this.getCollisionBoxCenter(), angle, delta + delta * i);
        RenderEngine.renderShape(g, new Rectangle2D.Double(target.getX() - 0.5, target.getY() - 0.5, 1, 1));
      }
    }

    if (Game.config().debug().isDebugEnabled()) {
      this.strike.render(g);
    }
  }

  @Override
  public void loaded(Environment environment) {
    environment.addRenderListener(RenderType.GROUND, (g, l) -> {
      double stompPrep = this.getController(EnemyController.class).getStompPreparation();
      if (stompPrep != 0 && !this.isDead()) {
        g.setStroke(new BasicStroke(2f));
        g.setColor(new Color(255, 255, 0, 200));
        Shape s = this.stomp.calculateImpactArea();
        RenderEngine.renderOutline(g, s);
        double radius = this.stomp.getAttributes().getImpact().getCurrentValue() * stompPrep;
        double x = s.getBounds2D().getX() + (s.getBounds2D().getWidth() - radius) / 2.0;
        double y = s.getBounds2D().getY() + (s.getBounds2D().getHeight() - radius) / 2.0;
        Ellipse2D current = new Ellipse2D.Double(x, y, radius, radius);
        g.setColor(new Color(255, 255, 0, 50));
        RenderEngine.renderShape(g, current);
      }
    });
    super.loaded(environment);
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

    this.setVelocity(velocity.get(this.getType()));
  }

  public boolean isEngaged() {
    return engaged;
  }

  public void setEngaged(boolean engaged) {
    this.engaged = engaged;
  }

  public EnemyStrike getStrike() {
    return strike;
  }

  public Charge getCharge() {
    return charge;
  }

  public boolean canCast() {
    return Game.time().since(this.lastCast) > GCD;
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

  public Stomp getStomp() {
    return stomp;
  }

  public boolean isEngaging() {
    return engaging;
  }

  public void setEngaging(boolean engaging) {
    this.engaging = engaging;
  }
}
