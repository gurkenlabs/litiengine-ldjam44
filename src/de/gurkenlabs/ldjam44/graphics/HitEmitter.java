package de.gurkenlabs.ldjam44.graphics;

import java.awt.Color;
import java.util.Random;

import de.gurkenlabs.litiengine.Align;
import de.gurkenlabs.litiengine.Valign;
import de.gurkenlabs.litiengine.annotation.EmitterInfo;
import de.gurkenlabs.litiengine.annotation.EntityInfo;
import de.gurkenlabs.litiengine.entities.ICombatEntity;
import de.gurkenlabs.litiengine.graphics.RenderType;
import de.gurkenlabs.litiengine.graphics.emitters.Emitter;
import de.gurkenlabs.litiengine.graphics.emitters.particles.Particle;
import de.gurkenlabs.litiengine.graphics.emitters.particles.RectangleFillParticle;
import de.gurkenlabs.litiengine.physics.Collision;
import de.gurkenlabs.litiengine.util.MathUtilities;

@EmitterInfo(maxParticles = 50, spawnAmount = 50, emitterTTL = 0, particleMinTTL = 0, particleMaxTTL = 0, particleUpdateRate = 10)
@EntityInfo(renderType = RenderType.GROUND, width = 16, height = 16)
public class HitEmitter extends Emitter {
  private static final int MAX_MOVE_TIME = 250;
  private static final Color[] DEFAULT_HIT_COLORS = new Color[] { new Color(104, 23, 40), new Color(146, 25, 42), new Color(153, 39, 34) };

  private boolean hasStopped;

  public HitEmitter(final ICombatEntity entity) {
    this(entity, 50);
  }

  public HitEmitter(final ICombatEntity entity, final Color... colors) {
    this(entity, 50);
    this.setColors(colors);
  }

  public HitEmitter(final ICombatEntity entity, final int spawnAmount) {
    super(entity.getLocation());
    this.setWidth(entity.getWidth());
    this.setHeight(entity.getHeight());
    this.setOriginAlign(Align.CENTER);
    this.setOriginValign(Valign.MIDDLE);
    this.setSpawnAmount(spawnAmount);
    this.setMaxParticles(spawnAmount);
    this.setColors(DEFAULT_HIT_COLORS);
  }

  @Override
  public Particle createNewParticle() {
    final float dx = (float) (this.getWidth() / 3 * Math.random() * MathUtilities.randomSign());
    final float dy = (float) (this.getHeight() / 3 * Math.random() * MathUtilities.randomSign());
    final float gravityX = 0.1f * MathUtilities.randomSign() * 2;
    final float gravityY = 0.1f * MathUtilities.randomSign() * 2;
    final float size = (float) MathUtilities.randomInRange(0.75, 2.0);
    final int life = this.getRandomParticleTTL();

    final Color color = this.getColors().get(new Random().nextInt(this.getColors().size()));
    return new RectangleFillParticle(size, size, color, life)
        .setCollisionType(Collision.ANY)
        .setDeltaX(dx)
        .setDeltaY(dy)
        .setDeltaIncX(gravityX)
        .setDeltaIncY(gravityY);
  }

  @Override
  public void update() {
    super.update();
    if (this.getAliveTime() >= MAX_MOVE_TIME) {
      this.getParticles().forEach(particle -> particle.setDeltaX(0));
      this.getParticles().forEach(particle -> particle.setDeltaY(0));
      this.hasStopped = true;
    }
  }

  @Override
  protected boolean canTakeNewParticles() {
    return !this.hasStopped && super.canTakeNewParticles();
  }
}
