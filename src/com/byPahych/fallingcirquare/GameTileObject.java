package com.byPahych.fallingcirquare;

import org.andengine.entity.sprite.AnimatedSprite;
import org.andengine.entity.sprite.TiledSprite;
import org.andengine.extension.physics.box2d.PhysicsConnector;
import org.andengine.extension.physics.box2d.PhysicsFactory;
import org.andengine.opengl.texture.region.ITiledTextureRegion;
import org.andengine.opengl.vbo.VertexBufferObjectManager;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;

public class GameTileObject extends AnimatedSprite {

	Body body;
	PhysicsConnector connector;
	SceneGame scene;
	
	public GameTileObject(float pX, float pY,
			ITiledTextureRegion pTiledTextureRegion,
			VertexBufferObjectManager pVertexBufferObjectManager) {
		super(pX, pY, pTiledTextureRegion, pVertexBufferObjectManager);
		// TODO Auto-generated constructor stub
	}
	
	protected void CreateBody()
	{
	}

	public void Remove()
	{
		GameMainFunctions.RemoveObject(this, body, connector);
	}
	
	public void RemoveBody()
	{
		GameMainFunctions.RemovePhysicsObject(body, connector);
	}
	
	public float GetX()
	{
		return this.mX;
	}
	
	public float GetY()
	{
		return this.mY;
	}
	
	public Vector2 GetCenter()
	{
		return new Vector2(this.mX + this.getWidth()/2.0f, this.mY + this.getHeight()/2.0f);
	}
}
