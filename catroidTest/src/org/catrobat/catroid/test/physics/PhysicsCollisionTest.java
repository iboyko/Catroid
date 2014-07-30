/**
 *  Catroid: An on-device visual programming system for Android devices
 *  Copyright (C) 2010-2013 The Catrobat Team
 *  (<http://developer.catrobat.org/credits>)
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU Affero General Public License as
 *  published by the Free Software Foundation, either version 3 of the
 *  License, or (at your option) any later version.
 *
 *  An additional term exception under section 7 of the GNU Affero
 *  General Public License, version 3, is available at
 *  http://developer.catrobat.org/license_additional_term
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 *  GNU Affero General Public License for more details.
 *
 *  You should have received a copy of the GNU Affero General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.catrobat.catroid.test.physics;

import android.util.Log;

import com.badlogic.gdx.math.Vector2;

import org.catrobat.catroid.ProjectManager;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.physics.PhysicsLook;
import org.catrobat.catroid.physics.PhysicsObject;
import org.catrobat.catroid.test.physics.actions.PhysicsActionTestCase;

public class PhysicsCollisionTest extends PhysicsActionTestCase {

	private static final String TAG = PhysicsCollisionTest.class.getSimpleName();
	private Sprite sprite2;

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		ProjectManager.getInstance().getCurrentProject().addSprite(sprite);
		sprite2 = sprite.clone();
		sprite2.look = new PhysicsLook(sprite2, physicsWorld);
		sprite2.look.setLookData(sprite.look.getLookData());
		physicsWorld.setGravity(0f, 0f);
	}

	public void testCollisionDynamicOnly() {
		assertTrue("cloning sprite failed", sprite2 != sprite);
		PhysicsObject physicsObject1 = physicsWorld.getPhysicsObject(sprite);
		PhysicsObject physicsObject2 = physicsWorld.getPhysicsObject(sprite2);
		assertTrue("cloning sprite's physicsObject failed", physicsObject2 != physicsObject1);
		physicsObject1.setType(PhysicsObject.Type.DYNAMIC);
		physicsObject2.setType(PhysicsObject.Type.DYNAMIC);

		Vector2 spriteAABBPoint1 = new Vector2();
		Vector2 spriteAABBPoint2 = new Vector2();
		physicsObject1.getBoundaryBox(spriteAABBPoint1, spriteAABBPoint2);

		float spriteDistance = 256f;
		// set positions of sprites so that distance between their AxisAlignedBoundingBox-Edges is spriteDistance
		sprite.look.setPosition(spriteAABBPoint1.x - spriteDistance/2, 0f);
		sprite2.look.setPosition(spriteAABBPoint2.x + spriteDistance/2, 0f);

		float velocityX = 32f;
		physicsObject1.setVelocity(velocityX, 0f);
		physicsObject2.setVelocity(-velocityX, 0f);

		for (int i = 0; i < 12; i++) {
			physicsWorld.step(0.5f);
		}

		assertTrue("physicsObjects do not seem to have collided ", physicsObject1.getVelocity().x < 0
				&& physicsObject2.getVelocity().x > 0);
	}

	public void testCollisionDynamicStatic() {
		assertTrue("cloning sprite failed", sprite2 != sprite);
		PhysicsObject physicsObject1 = physicsWorld.getPhysicsObject(sprite);
		PhysicsObject physicsObject2 = physicsWorld.getPhysicsObject(sprite2);
		assertTrue("cloning sprite's physicsObject failed", physicsObject2 != physicsObject1);
		physicsObject1.setType(PhysicsObject.Type.DYNAMIC);
		physicsObject2.setType(PhysicsObject.Type.FIXED);

		Vector2 spriteAABBPoint1 = new Vector2();
		Vector2 spriteAABBPoint2 = new Vector2();
		physicsObject1.getBoundaryBox(spriteAABBPoint1, spriteAABBPoint2);

		float spriteDistance = 256;
		sprite.look.setPosition(spriteAABBPoint1.x - spriteDistance/2, 0f);
		sprite2.look.setPosition(spriteAABBPoint2.x + spriteDistance/2, 0f);

		float velocityX = 64f;
		physicsObject1.setVelocity(velocityX, 0f);

		for (int i = 0; i < 12; i++) {
			physicsWorld.step(0.5f);
		}

		assertTrue("physicsObjects do not seem to have collided ", physicsObject1.getVelocity().x < 0);
	}

}
