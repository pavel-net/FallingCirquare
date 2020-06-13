package com.byPahych.fallingcirquare;

import java.util.LinkedList;

import org.andengine.engine.Engine.EngineLock;
import org.andengine.engine.handler.timer.ITimerCallback;
import org.andengine.engine.handler.timer.TimerHandler;
import org.andengine.entity.IEntityFactory;
import org.andengine.entity.particle.ParticleSystem;
import org.andengine.entity.particle.SpriteParticleSystem;
import org.andengine.entity.particle.emitter.CircleOutlineParticleEmitter;
import org.andengine.entity.particle.emitter.RectangleParticleEmitter;
import org.andengine.entity.particle.initializer.AlphaParticleInitializer;
import org.andengine.entity.particle.initializer.BlendFunctionParticleInitializer;
import org.andengine.entity.particle.initializer.ColorParticleInitializer;
import org.andengine.entity.particle.initializer.RotationParticleInitializer;
import org.andengine.entity.particle.initializer.VelocityParticleInitializer;
import org.andengine.entity.particle.modifier.AlphaParticleModifier;
import org.andengine.entity.particle.modifier.ColorParticleModifier;
import org.andengine.entity.particle.modifier.ExpireParticleInitializer;
import org.andengine.entity.particle.modifier.OffCameraExpireParticleModifier;
import org.andengine.entity.particle.modifier.ScaleParticleModifier;
import org.andengine.entity.scene.Scene;
import org.andengine.entity.sprite.Sprite;
import org.andengine.extension.physics.box2d.PhysicsConnector;
import org.andengine.extension.physics.box2d.PhysicsFactory;
import org.andengine.extension.physics.box2d.util.Vector2Pool;
import org.andengine.opengl.texture.region.ITextureRegion;
import org.andengine.opengl.vbo.VertexBufferObjectManager;
import org.andengine.util.math.MathUtils;

import android.opengl.GLES20;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;

public class Board extends Sprite {

	Body body;
	PhysicsConnector connector;
	SceneGame scene;
	RectangleParticleEmitter particleEmitter;
	BoardManager manager;
	public Board(float pX, float pY, float width, float height, ITextureRegion pTextureRegion,
			VertexBufferObjectManager pVertexBufferObjectManager, SceneGame pScene, BoardManager manager) {
		super(pX, pY, pTextureRegion, pVertexBufferObjectManager);
		this.setWidth(width);
		this.setHeight(height);
		this.scene = pScene;	
		this.manager = manager;
	}
	
	public void Start(float speed)
	{	
		CreateBody();
		scene.attachChild(this);
		body.setLinearVelocity(0, speed);
	}
	
	public void SetSpeed(float speed)
	{
		body.setLinearVelocity(0, speed);
	}
	
	private void CreateBody()
	{
		body = PhysicsFactory.createBoxBody(SceneGame.mPhysicsWorld, this, BodyType.KinematicBody, GameOptions.BOARD_FIXTURE_DEF);
		body.setUserData(this);
		connector = new PhysicsConnector(this, body, true, true);
		SceneGame.mPhysicsWorld.registerPhysicsConnector(connector);
	}	
	
	public void Remove()
	{		
		final Board board = this;
    	GameMainFunctions.RemoveObject(board, body, connector);
	}

	public float GetTailPosition()
	{
		return this.getY();
	}
		
	
	public void StartDestroyAnimation(float x, float y)
	{
		//RemoveBody();
		StartAnimation(x, y);	
		Remove();	
	}
	
	private void StartAnimation(float x, float y)
	{						
		particleEmitter = new RectangleParticleEmitter(0.5f*(2.0f*this.getX() + this.getWidth()), 0.5f*(2.0f*this.getY() + this.getHeight()),
				this.getWidth(), this.getHeight());		
		
		final SpriteParticleSystem particleSystem = new SpriteParticleSystem(particleEmitter, 20, 20, 10, 
				MainGameActivity.mParticleTextureRegion, MainGameActivity._main.getVertexBufferObjectManager());
		particleSystem.addParticleInitializer(new ColorParticleInitializer<Sprite>(0, 0, 0));
		particleSystem.addParticleInitializer(new AlphaParticleInitializer<Sprite>(1));
		particleSystem.addParticleInitializer(new VelocityParticleInitializer<Sprite>(-100, 100, 0, 100));	
		particleSystem.addParticleModifier(new AlphaParticleModifier<Sprite>(0, 1.8f, 1, 0));
		particleSystem.addParticleInitializer(new ExpireParticleInitializer<Sprite>(2.5f));
		scene.registerUpdateHandler(new TimerHandler(2f, new ITimerCallback() {
	        @Override
	        public void onTimePassed(final TimerHandler pTimerHandler){
	                particleSystem.setParticlesSpawnEnabled(false);
	                scene.detachChild(particleSystem);
	                scene.unregisterUpdateHandler(pTimerHandler);
	        }	               
		}));		
		scene.attachChild(particleSystem);
		
		CircleOutlineParticleEmitter particleEmitterBoom = new CircleOutlineParticleEmitter(x, y, 20);
		final SpriteParticleSystem particleSystemBoom = new SpriteParticleSystem(particleEmitterBoom, 20, 20, 10, 
				MainGameActivity.mParticleTextureRegionBoom, MainGameActivity._main.getVertexBufferObjectManager());				
		particleSystemBoom.addParticleInitializer(new ColorParticleInitializer<Sprite>(1, 0, 0));
		particleSystemBoom.addParticleInitializer(new AlphaParticleInitializer<Sprite>(1));
		particleSystemBoom.addParticleInitializer(new BlendFunctionParticleInitializer<Sprite>(GLES20.GL_SRC_ALPHA, GLES20.GL_ONE_MINUS_SRC_ALPHA));
		particleSystemBoom.addParticleInitializer(new VelocityParticleInitializer<Sprite>(-40, 40, -60, -40));

		particleSystemBoom.addParticleModifier(new ScaleParticleModifier<Sprite>(0, 1, 2.0f, 3.0f));
		particleSystemBoom.addParticleModifier(new ColorParticleModifier<Sprite>(0, 1.0f, 1, 1, 0, 0.5f, 0, 0));
		particleSystemBoom.addParticleModifier(new AlphaParticleModifier<Sprite>(0, 1, 1, 0));
		particleSystem.addParticleInitializer(new ExpireParticleInitializer<Sprite>(1.5f));
		scene.registerUpdateHandler(new TimerHandler(1.2f, new ITimerCallback() {
	        @Override
	        public void onTimePassed(final TimerHandler pTimerHandler){
	        		particleSystemBoom.setParticlesSpawnEnabled(false);
	                scene.detachChild(particleSystemBoom);
	                scene.unregisterUpdateHandler(pTimerHandler);
	        }	               
		}));		
		scene.attachChild(particleSystemBoom);
	}	
	
	public Vector2 GetCenter()
	{
		return new Vector2(this.getX() + this.getWidth()/2.0f, this.getY());
	}
}
