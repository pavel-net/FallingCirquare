package com.byPahych.fallingcirquare;

import org.andengine.extension.physics.box2d.PhysicsFactory;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.FixtureDef;

public class GameOptions {
	
	private static final short CATEGORYBIT_WALL = 1;
	private static final short CATEGORYBIT_BALL = 2;
	private static final short CATEGORYBIT_BOARD = 4;

	private static final short MASKBITS_WALL = CATEGORYBIT_WALL + CATEGORYBIT_BALL;
	private static final short MASKBITS_BALL = CATEGORYBIT_WALL + CATEGORYBIT_BOARD;
	private static final short MASKBITS_BOARD = CATEGORYBIT_BOARD + CATEGORYBIT_BALL;
	private static final short MASKBITS_MONET = CATEGORYBIT_BOARD;
	
	//																		плотность, эластичность, трение
	public static final FixtureDef WALL_FIXTURE_DEF = PhysicsFactory.createFixtureDef(1, 0.4f, 0.0f, false, CATEGORYBIT_WALL, MASKBITS_WALL, (short)0);
	public static final FixtureDef BALL_FIXTURE_DEF = PhysicsFactory.createFixtureDef(1, 0.0f, 0.8f, false, CATEGORYBIT_BALL, MASKBITS_BALL, (short)0);
	public static final FixtureDef BOARD_FIXTURE_DEF = PhysicsFactory.createFixtureDef(1, 0.0f, 1.8f, false, CATEGORYBIT_BOARD, MASKBITS_BOARD, (short)0);
	public static final FixtureDef MONET_FIXTURE_DEF = PhysicsFactory.createFixtureDef(0, 0, 1.0f, false, CATEGORYBIT_BOARD, MASKBITS_MONET, (short)0);
	
	public static float DEFAULT_START_GRAVITY = 25.0f;		// начальная гравитация
	public static float gravity_delta = 2.0f;
	public static float DEFAULT_MIN_SIZE_CLIFF = 70.0f; 	// минимальное расстояние, допустимое сбоку
	public static float DISTANCE_CREATE_BOARD = 60.0f;		// расстояние между платформами
	public static float MAX_SPEED_BALL = 30.0f;				// начальное ограничение скорости шарика
	public static float start_acceleration_ball = 0.8f;
	public static float ball_delta_acceleration = 0.05f;
	public static int DEFAULT_START_DELTA_LENGTH = 20;		// начальное расстояние обновления скорости
	public static float DEFAULT_START_BOARD_SPEED = -3.0f;	// начальная скорость платформ
	public static float board_delta_acceleration = -0.2f;
	
	public static float DEFAULT_WIDTH_COIN = 40.0f;			// ширина монеты
	public static float DEFAULT_WIDTH_SPEED_BALL = 60.0f;	// ширина ширика ускорения
	public static float DEFAULT_WIDTH_DYNAMITE = 60.0f;		// ширина и высота динамита
	public static float DEFAULT_BOOM_RADIUS = 230.0f;	
	public static float probability_coin = 0.5f;	
	public static float probability_ball_speed = 0.3f;
	public static float probability_boom = 0.3f;
	public static float[] array_speed_point_bonus = new float[]{10.0f, 14.0f, 18.0f, 22.0f};		
	public static boolean IsAccelerometer = false;
	public static boolean IsVibrations = true;
	public static boolean IsAcceptAddSpeedBall = true;
	public static int MIN_COUNT_TRANSFORM_COINS = 10;
}
