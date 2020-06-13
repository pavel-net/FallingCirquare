package com.byPahych.fallingcirquare;

import org.andengine.entity.IEntity;
import org.andengine.entity.modifier.EntityModifier;
import org.andengine.entity.modifier.MoveByModifier;
import org.andengine.entity.modifier.MoveModifier;
import org.andengine.entity.modifier.PathModifier;
import org.andengine.entity.modifier.PathModifier.Path;
import org.andengine.extension.physics.box2d.PhysicsConnector;
import org.andengine.extension.physics.box2d.PhysicsFactory;
import org.andengine.opengl.texture.region.ITiledTextureRegion;
import org.andengine.opengl.vbo.VertexBufferObjectManager;

import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;

public class BallSpeed extends GameTileObject {
	
	public BallSpeed(float pX, float pY,
			ITiledTextureRegion pTiledTextureRegion,
			VertexBufferObjectManager pVertexBufferObjectManager, SceneGame scene) {
		super(pX, pY, pTiledTextureRegion, pVertexBufferObjectManager);
		this.setHeight(GameOptions.DEFAULT_WIDTH_SPEED_BALL);
		this.setWidth(GameOptions.DEFAULT_WIDTH_SPEED_BALL);
		this.scene = scene;
		CreateBody();
		scene.attachChild(this);
	}
	
	@Override
	protected void CreateBody()
	{
		body = PhysicsFactory.createCircleBody(SceneGame.mPhysicsWorld, this, BodyType.DynamicBody, GameOptions.MONET_FIXTURE_DEF);
		connector = new PhysicsConnector(this, body, true, true);
		SceneGame.mPhysicsWorld.registerPhysicsConnector(connector);
	}
	
	public void StartDestroyAnimation(float x, float y)
	{
		
	}
	private void StartAnimation(float x, float y)
	{
		
	}

}
