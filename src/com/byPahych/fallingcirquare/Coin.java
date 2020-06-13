package com.byPahych.fallingcirquare;

import org.andengine.entity.IEntity;
import org.andengine.entity.modifier.MoveModifier;
import org.andengine.entity.scene.Scene;
import org.andengine.entity.sprite.AnimatedSprite;
import org.andengine.extension.physics.box2d.PhysicsConnector;
import org.andengine.extension.physics.box2d.PhysicsFactory;
import org.andengine.opengl.texture.region.ITiledTextureRegion;
import org.andengine.opengl.vbo.VertexBufferObjectManager;

import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;

public class Coin extends GameTileObject {

	public Coin(float pX, float pY, ITiledTextureRegion pTiledTextureRegion,
			VertexBufferObjectManager pVertexBufferObjectManager, SceneGame scene) {
		super(pX, pY, pTiledTextureRegion, pVertexBufferObjectManager);
		this.setHeight(GameOptions.DEFAULT_WIDTH_COIN);
		this.setWidth(GameOptions.DEFAULT_WIDTH_COIN);
		this.scene = scene;
		CreateBody();
		scene.attachChild(this);
	}
	
	@Override
	protected void CreateBody()
	{
		body = PhysicsFactory.createCircleBody(SceneGame.mPhysicsWorld, this, BodyType.DynamicBody, GameOptions.MONET_FIXTURE_DEF);
		body.setUserData(this);
		connector = new PhysicsConnector(this, body, true, true);
		SceneGame.mPhysicsWorld.registerPhysicsConnector(connector);
	}
	
	public void StartDestroyAnimation(float x, float y)
	{
		StartAnimation(x, y);
	}
	private void StartAnimation(float x, float y)
	{
		final Coin coin = this;
		MoveModifier mod = new MoveModifier(0.8f, this.getX(), x, this.getY(), y)
		{
	        @Override
	        protected void onModifierFinished(IEntity pItem)
	        {
    			RemoveBody();
	        	MainGameActivity._main.getEngine().runOnUpdateThread(new Runnable()
	    	    {
	    	        public void run()
	    	        {
	    	        	scene.detachChild(coin);
	    	        	coin.clearUpdateHandlers();	
	    	        	coin.clearEntityModifiers();
	    	        	coin.dispose();
	    	        	scene.UpdateTextCoin();
	    	        }
	      	    });
	        }
		};
		this.registerEntityModifier(mod);
	}
}
