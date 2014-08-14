/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2014 The Catrobat Team
 * (<http://developer.catrobat.org/credits>)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * An additional term exception under section 7 of the GNU Affero
 * General Public License, version 3, is available at
 * http://developer.catrobat.org/license_additional_term
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.catrobat.catroid.test.physics;

import android.test.AndroidTestCase;
import android.util.Log;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.GdxNativesLoader;

import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.physics.PhysicsLook;
import org.catrobat.catroid.physics.PhysicsObject;
import org.catrobat.catroid.physics.PhysicsObject.Type;
import org.catrobat.catroid.physics.PhysicsWorld;
import org.catrobat.catroid.test.utils.Reflection;
import org.catrobat.catroid.test.utils.Reflection.ParameterList;

import java.util.Map;

public class PhysicsWorldTest extends AndroidTestCase {
	static {
		GdxNativesLoader.load();
	}

	private static final String TAG = PhysicsObjectTest.class.getSimpleName();
	private PhysicsWorld physicsWorld;
	private World world;
	private Map<Sprite, PhysicsObject> physicsObjects;

	@SuppressWarnings("unchecked")
	@Override
	public void setUp() {
		//TODO[physics] take a meaningful width and height of PhysicsWorld
		physicsWorld = new PhysicsWorld(1920, 1600);
		world = (World) Reflection.getPrivateField(physicsWorld, "world");
		physicsObjects = (Map<Sprite, PhysicsObject>) Reflection.getPrivateField(physicsWorld, "physicsObjects");
	}

	@Override
	public void tearDown() {
		physicsWorld = null;
		world = null;
		physicsObjects = null;
	}

	public void testDefaultSettings() {
		assertEquals("Wrong configuration", 40.0f, PhysicsWorld.RATIO);
		assertEquals("Wrong configuration", 8, PhysicsWorld.VELOCITY_ITERATIONS);
		assertEquals("Wrong configuration", 3, PhysicsWorld.POSITION_ITERATIONS);

		assertEquals("Wrong configuration", new Vector2(0, -10), PhysicsWorld.DEFAULT_GRAVITY);
		assertFalse("Wrong configuration", PhysicsWorld.IGNORE_SLEEPING_OBJECTS);

		assertEquals("Wrong configuration", 6, PhysicsWorld.STABILIZING_STEPS);

		short expectedCategoryBoundaryBox= 0x0002;
		short expectedCategoryPhysicsObject = 0x0004;
		assertEquals("Wrong configuration", 0x0000, PhysicsWorld.NOCOLLISION_MASK);
		assertEquals("Wrong configuration", expectedCategoryBoundaryBox, PhysicsWorld.CATEGORY_BOUNDARYBOX);
		assertEquals("Wrong configuration", expectedCategoryPhysicsObject, PhysicsWorld.CATEGORY_PHYSICSOBJECT);

		assertEquals("Wrong configuration", expectedCategoryPhysicsObject, PhysicsWorld.MASK_BOUNDARYBOX);
		assertEquals("Wrong configuration", ~expectedCategoryBoundaryBox, PhysicsWorld.MASK_PHYSICSOBJECT);
		assertEquals("Wrong configuration", -1, PhysicsWorld.MASK_TOBOUNCE);
		assertEquals("Wrong configuration", 0, PhysicsWorld.MASK_NOCOLLISION);
	}

	public void testWrapper() {
		assertNotNull("Didn't load box2d wrapper", world);
	}

	public void testGravity() {
		assertEquals("Wrong initialization", PhysicsWorld.DEFAULT_GRAVITY, world.getGravity());

		Vector2 newGravity = new Vector2(-1.2f, 3.4f);
		physicsWorld.setGravity(newGravity.x, newGravity.y);

		assertEquals("Did not update gravity", newGravity, world.getGravity());
	}

	public void testGetNullPhysicsObject() {
		try {
			physicsWorld.getPhysicsObject(null);
			fail("Get physics object of a null sprite didn't cause a null pointer exception");
		} catch (NullPointerException exception) {
			Log.d(TAG,"");
		}
	}

	public void testGetPhysicsObject() {
		Sprite sprite = new Sprite("TestSprite");
		PhysicsObject physicsObject = physicsWorld.getPhysicsObject(sprite);

		assertNotNull("No physics object was created", physicsObject);
		assertEquals("Wrong number of physics objects were stored", 1, physicsObjects.size());
		assertTrue("Sprite wasn't saved into physics object map", physicsObjects.containsKey(sprite));
		assertEquals("Wrong map relation for sprite", physicsObject, physicsObjects.get(sprite));
	}

	public void testCreatePhysicsObject() {
		Object[] values = { new Sprite("testsprite") };
		ParameterList paramList = new ParameterList(values);
		PhysicsObject physicsObject = (PhysicsObject) Reflection.invokeMethod(physicsWorld, "createPhysicsObject",
				paramList);
		//Body body = (Body) Reflection.getPrivateField(physicsObject, "body");

		assertEquals("Type is not the expected", Type.NONE, physicsObject.getType());
		// TODO[Physics] extend test
		//assertTrue("Created body isn't a bullet", body.isBullet());
	}

	public void testGetSamePhysicsObject() {
		Sprite sprite = new Sprite("TestSprite");
		PhysicsObject physicsObject = physicsWorld.getPhysicsObject(sprite);
		PhysicsObject samePhysicsObject = physicsWorld.getPhysicsObject(sprite);

		assertEquals("Wrong number of physics objects stored", 1, physicsObjects.size());
		assertEquals("Physics objects are different", physicsObject, samePhysicsObject);
	}

	public void testStabilizingSteps() {
		int stepPasses = PhysicsWorld.STABILIZING_STEPS + 10;

		int stabilizingStep;
		for (int pass = 0; pass < stepPasses; pass++) {
			physicsWorld.step(100.0f);
			stabilizingStep = (Integer) Reflection.getPrivateField(physicsWorld, "stabilizingSteCounter");
			assertTrue("Stabilizing the project didn't work",
					((stabilizingStep == (pass + 1)) && (stabilizingStep < PhysicsWorld.STABILIZING_STEPS))
							|| (stabilizingStep == PhysicsWorld.STABILIZING_STEPS));
		}
	}

	public void testSteps() throws SecurityException, IllegalArgumentException, NoSuchFieldException,
			IllegalAccessException {
		Sprite sprite = new Sprite("TestSprite");
		sprite.look = new PhysicsLook(sprite, physicsWorld);

		PhysicsObject physicsObject = physicsWorld.getPhysicsObject(sprite);

		Vector2 velocity = new Vector2(2.3f, 4.5f);
		float rotationSpeed = 45.0f;
		physicsWorld.setGravity(0.0f, 0.0f);
		Reflection.setPrivateField(PhysicsWorld.class, physicsWorld, "stabilizingSteCounter",
				PhysicsWorld.STABILIZING_STEPS);

		assertEquals("Physics object has a wrong start position", new Vector2(), physicsObject.getPosition());

		physicsObject.setVelocity(velocity.x, velocity.y);
		physicsObject.setRotationSpeed(rotationSpeed);

		physicsWorld.step(1.0f);
		assertEquals("Wrong x position", velocity.x, physicsObject.getX(), 1e-8);
		assertEquals("Wrong y position", velocity.y, physicsObject.getY(), 1e-8);
		assertEquals("Wrong angle", rotationSpeed, physicsObject.getDirection(), 1e-8);

		// TODO[Physics] angle problem
		physicsWorld.step(1.0f);
		assertEquals("Wrong x position", 2 * velocity.x, physicsObject.getX(), 1e-8);
		assertEquals("Wrong y position", 2 * velocity.y, physicsObject.getY(), 1e-8);
		assertEquals("Wrong angle", 2 * rotationSpeed, physicsObject.getDirection(), 1e-8);
	}
}