package de.gurkenlabs.ldjam44.graphics;

import java.awt.Color;
import java.awt.geom.Point2D;
import java.util.Random;

import de.gurkenlabs.litiengine.Align;
import de.gurkenlabs.litiengine.Valign;
import de.gurkenlabs.litiengine.annotation.EmitterInfo;
import de.gurkenlabs.litiengine.annotation.EntityInfo;
import de.gurkenlabs.litiengine.entities.ICollisionEntity;
import de.gurkenlabs.litiengine.graphics.RenderType;
import de.gurkenlabs.litiengine.graphics.emitters.EntityEmitter;
import de.gurkenlabs.litiengine.graphics.emitters.particles.Particle;
import de.gurkenlabs.litiengine.graphics.emitters.particles.RectangleFillParticle;
import de.gurkenlabs.litiengine.util.geom.GeometricUtilities;

@EmitterInfo(maxParticles = 50, spawnAmount = 50, emitterTTL = 250, particleMinTTL = 200, particleMaxTTL = 250, originAlign = Align.CENTER, originVAlign = Valign.MIDDLE)
@EntityInfo(renderType = RenderType.GROUND)
public class LandEmitter extends EntityEmitter {

  public LandEmitter(final ICollisionEntity entity) {
    super(entity);
    this.setColors(Color.GRAY, Color.LIGHT_GRAY);
  }

  @Override
  protected Particle createNewParticle() {
    final Point2D randomLocation = GeometricUtilities.getPointOnCircle(new Point2D.Double(0, 0),
        16 + Math.random() * 16, new Random().nextInt(360));

    final float xCoord = (float) randomLocation.getX();
    final float yCoord = (float) randomLocation.getY();

    final float dx = -xCoord / (this.getParticleUpdateRate() / 2f);
    final float dy = -yCoord / (this.getParticleUpdateRate() / 2f);

    final float gravityX = 0;
    final float gravityY = 0;
    final float size = (float) (3 + Math.random() * 2);
    final int life = this.getRandomParticleTTL();

    return new RectangleFillParticle(size, size, new Color(255, 255, 255, 100), life)
        .setDeltaX(dx)
        .setDeltaY(dy)
        .setDeltaIncX(gravityX)
        .setDeltaIncY(gravityY);
  }
}
