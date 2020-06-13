package com.byPahych.fallingcirquare;

import org.andengine.entity.primitive.Rectangle;
import org.andengine.entity.scene.Scene;
import org.andengine.extension.physics.box2d.PhysicsConnector;
import org.andengine.extension.physics.box2d.PhysicsFactory;
import org.andengine.opengl.vbo.VertexBufferObjectManager;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;

public class Border {
	
	private Rectangle ground;
	private Rectangle roof;
	private Rectangle left;
	private Rectangle right;	
	
	private Body body_ground;
	private Body body_roof;
	private Body body_left;
	private Body body_right;
	
	private PhysicsConnector connector_ground;
	private PhysicsConnector connector_roof;
	private PhysicsConnector connector_left;
	private PhysicsConnector connector_right;
	private Scene scene;
	private float default_width = 2.0f;
	
	public Border(VertexBufferObjectManager vertexBufferObjectManager, Scene scene,
			float roof_x, float width, float roof_y, float height)
	{		
		this.scene = scene;
		ground = new Rectangle(roof_x, roof_y + height - default_width, width, default_width, vertexBufferObjectManager);
		roof = new Rectangle(roof_x, roof_y, width, default_width, vertexBufferObjectManager);
		left = new Rectangle(roof_x, roof_y, default_width, height, vertexBufferObjectManager);
		right = new Rectangle(roof_x + width - default_width, roof_y, default_width, height, vertexBufferObjectManager);
		
		ground.setColor(0, 0, 0);
		roof.setColor(0, 0, 0);
		left.setColor(0, 0, 0);
		right.setColor(0, 0, 0);			
		
		CreatePhysicsBorder();
		
		scene.attachChild(ground);
		scene.attachChild(roof);
		scene.attachChild(left);
		scene.attachChild(right);
	}
	
	public boolean IsBallCollideRoof(Ball ball)
	{
		if (roof.collidesWith(ball))
			return true;
		return false;
	}
	
	private void CreatePhysicsBorder()
	{		
		body_ground = PhysicsFactory.createBoxBody(SceneGame.mPhysicsWorld, ground, BodyType.StaticBody, GameOptions.WALL_FIXTURE_DEF);
		connector_ground = new PhysicsConnector(ground, body_ground, true, true);
		SceneGame.mPhysicsWorld.registerPhysicsConnector(connector_ground);
		
		body_left = PhysicsFactory.createBoxBody(SceneGame.mPhysicsWorld, left, BodyType.StaticBody, GameOptions.WALL_FIXTURE_DEF);
		connector_left = new PhysicsConnector(left, body_left, true, true);
		SceneGame.mPhysicsWorld.registerPhysicsConnector(connector_left);
		
		body_right = PhysicsFactory.createBoxBody(SceneGame.mPhysicsWorld, right, BodyType.StaticBody, GameOptions.WALL_FIXTURE_DEF);
		connector_right = new PhysicsConnector(right, body_right, true, true);
		SceneGame.mPhysicsWorld.registerPhysicsConnector(connector_right);
		
		body_roof = PhysicsFactory.createBoxBody(SceneGame.mPhysicsWorld, roof, BodyType.StaticBody, GameOptions.WALL_FIXTURE_DEF);
		body_roof.setUserData("RECTANGLE");
		connector_roof = new PhysicsConnector(roof, body_roof, true, true);
		SceneGame.mPhysicsWorld.registerPhysicsConnector(connector_roof);
	}
	
	private void RemovePhysicsBorder()
	{
		GameMainFunctions.RemovePhysicsObject(body_ground, connector_ground);
		GameMainFunctions.RemovePhysicsObject(body_left, connector_left);
		GameMainFunctions.RemovePhysicsObject(body_right, connector_right);
		GameMainFunctions.RemovePhysicsObject(body_roof, connector_roof);
	}
	
	public void UpdateBorder(final float delta_y)
	{
		Vector2 pos = body_ground.getPosition();		
		body_ground.setTransform(pos.x, pos.y + delta_y, 0);
		
		pos = body_left.getPosition();
		body_left.setTransform(pos.x, pos.y + delta_y, 0);
		
		pos = body_right.getPosition();
		body_right.setTransform(pos.x, pos.y + delta_y, 0);
		
		pos = body_roof.getPosition();
		body_roof.setTransform(pos.x, pos.y + delta_y, 0);
	}
	
	public void Remove()
	{
		RemovePhysicsBorder();
		scene.detachChild(ground);
		scene.detachChild(roof);
		scene.detachChild(left);
		scene.detachChild(right);
	}
	
}
