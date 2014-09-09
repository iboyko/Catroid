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
package org.catrobat.catroid.test.physics.actions;


import com.badlogic.gdx.math.Vector2;

import org.catrobat.catroid.formulaeditor.Formula;
import org.catrobat.catroid.physics.PhysicsLook;
import org.catrobat.catroid.physics.PhysicsObject;
import org.catrobat.catroid.test.physics.PhysicsBaseTest;
import org.catrobat.catroid.test.utils.Reflection;


public class SetSizeToActionTest extends PhysicsBaseTest {


	private PhysicsLook physicsLook;
	private PhysicsObject physicsObject;
	private float floatThreshold = 0.001f;


	@Override
	protected void setUp() throws Exception {
		super.setUp();
		this.physicsLook = (PhysicsLook) sprite.look;
		this.physicsObject = (PhysicsObject) Reflection.getPrivateField(physicsLook, "physicsObject");
	}


	public void testSizeLarger() {
		Vector2 oldAABBDimensions = getAABBDimensions();
		float oldCircumference = physicsObject.getCircumference();
		float scaleFactor = 500.0f;
		performSetSizeToAction(scaleFactor);
		Vector2 newAABBDimensions = getAABBDimensions();
		float newCircumference = physicsObject.getCircumference();
		assertTrue("Size is not being set to correct scale", Math.abs(oldAABBDimensions.x * (scaleFactor / 100.0f) - newAABBDimensions.x) < floatThreshold);
		assertTrue("Size is not being set to correct scale", Math.abs(oldAABBDimensions.y * (scaleFactor / 100.0f) - newAABBDimensions.y) < floatThreshold);
		assertTrue("Circumference is not being updated", Math.abs(oldCircumference * (scaleFactor / 100.0f) - newCircumference) < floatThreshold);
	}

	public void testSizeSmaller() {
		Vector2 oldAABBDimensions = getAABBDimensions();
		float oldCircumference = physicsObject.getCircumference();
		float scaleFactor = 25.0f;
		performSetSizeToAction(scaleFactor);
		Vector2 newAABBDimensions = getAABBDimensions();
		float newCircumference = physicsObject.getCircumference();
		assertTrue("Size is not being set to correct scale", Math.abs(oldAABBDimensions.x * (scaleFactor / 100.0f) - newAABBDimensions.x) < floatThreshold);
		assertTrue("Size is not being set to correct scale", Math.abs(oldAABBDimensions.y * (scaleFactor / 100.0f) - newAABBDimensions.y) < floatThreshold);
		assertTrue("Circumference is not being updated", Math.abs(oldCircumference * (scaleFactor / 100.0f) - newCircumference) < floatThreshold);
	}

	public void testSizeSame() {
		Vector2 oldAABBDimensions = getAABBDimensions();
		float oldCircumference = physicsObject.getCircumference();
		float scaleFactor = 100.0f;
		performSetSizeToAction(scaleFactor);
		Vector2 newAABBDimensions = getAABBDimensions();
		float newCircumference = physicsObject.getCircumference();
		assertTrue("Size is not being set to correct scale", Math.abs(oldAABBDimensions.x - newAABBDimensions.x) < floatThreshold);
		assertTrue("Size is not being set to correct scale", Math.abs(oldAABBDimensions.y - newAABBDimensions.y) < floatThreshold);
		assertTrue("Circumference is not being updated", Math.abs(oldCircumference * (scaleFactor / 100.0f) - newCircumference) < floatThreshold);
	}

	public void testSizeZero() {
		Vector2 oldAABBDimensions = getAABBDimensions();
		float oldCircumference = physicsObject.getCircumference();
		float scaleFactor = 0.0f;
		performSetSizeToAction(scaleFactor);
		Vector2 newAABBDimensions = getAABBDimensions();
		float newCircumference = physicsObject.getCircumference();
		assertTrue("Size is not being set to correct scale", Math.abs(oldAABBDimensions.x * (scaleFactor / 100.0f) - newAABBDimensions.x) < floatThreshold);
		assertTrue("Size is not being set to correct scale", Math.abs(oldAABBDimensions.y * (scaleFactor / 100.0f) - newAABBDimensions.y) < floatThreshold);
		assertTrue("Circumference is not being updated", Math.abs(oldCircumference * (scaleFactor / 100.0f) - newCircumference) < floatThreshold);
	}


	private Vector2 getAABBDimensions() {
		Vector2 lowerLeft = new Vector2();
		Vector2 upperRight = new Vector2();
		physicsObject.getBoundaryBox(lowerLeft, upperRight);
		float aabbWidth = Math.abs(Math.round(upperRight.x - lowerLeft.x));
		float aabbHeight = Math.abs(Math.round(upperRight.y - lowerLeft.y));
		return new Vector2(aabbWidth, aabbHeight);
	}

	private void performSetSizeToAction(float scaleFactor) {
		sprite.getActionFactory().createSetSizeToAction(sprite, new Formula(scaleFactor)).act(1.0f);
	}

}
