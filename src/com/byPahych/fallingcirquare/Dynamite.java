package com.byPahych.fallingcirquare;

import org.andengine.entity.IEntity;
import org.andengine.entity.modifier.MoveModifier;
import org.andengine.entity.scene.Scene;
import org.andengine.entity.sprite.AnimatedSprite;
import org.andengine.entity.sprite.AnimatedSprite.IAnimationListener;
import org.andengine.extension.physics.box2d.PhysicsConnector;
import org.andengine.extension.physics.box2d.PhysicsFactory;
import org.andengine.opengl.texture.region.ITiledTextureRegion;
import org.andengine.opengl.vbo.VertexBufferObjectManager;

import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;

public class Dynamite extends GameTileObject {

	public Dynamite(float pX, float pY,
			ITiledTextureRegion pTiledTextureRegion,
			VertexBufferObjectManager pVertexBufferObjectManager, SceneGame scene) {
		super(pX, pY, pTiledTextureRegion, pVertexBufferObjectManager);
		this.setHeight(GameOptions.DEFAULT_WIDTH_DYNAMITE);
		this.setWidth(GameOptions.DEFAULT_WIDTH_DYNAMITE);
		this.scene = scene;
		CreateBody();
		scene.attachChild(this);
	}

	@Override
	protected void CreateBody()
	{
		body = PhysicsFactory.createBoxBody(SceneGame.mPhysicsWorld, this, BodyType.DynamicBody, GameOptions.MONET_FIXTURE_DEF);
		connector = new PhysicsConnector(this, body, true, true);
		SceneGame.mPhysicsWorld.registerPhysicsConnector(connector);
	}
	
	public void StartDestroyAnimation(float x, float y)
	{
		StartAnimationBoom(scene, x, y);
		Remove();
	}
	
	public static void StartAnimationBoom(final Scene scene, float x, float y)
	{
		final AnimatedSprite BOOM = new AnimatedSprite(x, y, MainGameActivity.mRegionBOOM, MainGameActivity._main.getVertexBufferObjectManager());
		BOOM.setScale(3.2f);
		BOOM.setX(BOOM.getX() - BOOM.getWidth()/2.0f);
		BOOM.setY(BOOM.getY() - BOOM.getHeight()/2.0f);
		scene.attachChild(BOOM);
		
		final AnimatedSprite BOOM2 = new AnimatedSprite(x, y, MainGameActivity.mRegionBOOM2, MainGameActivity._main.getVertexBufferObjectManager());
		BOOM2.setScale(3.2f);
		BOOM2.setX(BOOM2.getX() - BOOM2.getWidth()/2.0f);
		BOOM2.setY(BOOM2.getY() - BOOM2.getHeight()/2.0f);
		//scene.attachChild(BOOM);
		
		
		final IAnimationListener listener_end = new IAnimationListener() {
			
			@Override
			public void onAnimationStarted(AnimatedSprite pAnimatedSprite,
					int pInitialLoopCount) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void onAnimationLoopFinished(AnimatedSprite pAnimatedSprite,
					int pRemainingLoopCount, int pInitialLoopCount) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void onAnimationFrameChanged(AnimatedSprite pAnimatedSprite,
					int pOldFrameIndex, int pNewFrameIndex) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void onAnimationFinished(AnimatedSprite pAnimatedSprite) {
				GameMainFunctions.RemoveObject(BOOM, null, null);
				GameMainFunctions.RemoveObject(BOOM2, null, null);
			}
		};
		
		BOOM.animate(50, false, new IAnimationListener() {

			@Override
			public void onAnimationStarted(AnimatedSprite pAnimatedSprite,
					int pInitialLoopCount) {				
			}

			@Override
			public void onAnimationFrameChanged(AnimatedSprite pAnimatedSprite,
					int pOldFrameIndex, int pNewFrameIndex) {				
			}

			@Override
			public void onAnimationLoopFinished(AnimatedSprite pAnimatedSprite,
					int pRemainingLoopCount, int pInitialLoopCount) {	
			}

			@Override
			public void onAnimationFinished(AnimatedSprite pAnimatedSprite) {
				BOOM.setVisible(false);
				scene.attachChild(BOOM2);
				BOOM2.animate(50, false, listener_end);
			}
	        
	    });
	}
	
	public void StartDestroyAnimationWithoutBoom(float x, float y)
	{
		final Dynamite dynamite = this;
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
	    	        	scene.detachChild(dynamite);
	    	        	dynamite.clearUpdateHandlers();	
	    	        	dynamite.clearEntityModifiers();
	    	        	dynamite.dispose();	    	        	
	    	        }
	      	    });
	        }
		};
		this.registerEntityModifier(mod);
	}
	
}
