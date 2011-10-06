/**
 *  Catroid: An on-device graphical programming language for Android devices
 *  Copyright (C) 2010  Catroid development team 
 *  (<http://code.google.com/p/catroid/wiki/Credits>)
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package at.tugraz.ist.catroid.tutorial;

import java.util.HashMap;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import at.tugraz.ist.catroid.R;

/**
 * @author User
 * 
 */
public class StateJump implements State {
	private StateController controller;
	private static HashMap<Tutor.TutorType, StateJump> instances;
	private Resources resources;
	Bitmap bitmaps_portal[];

	int currentFrame;
	int frameCount;
	boolean portAway;
	boolean notificated;

	private StateJump(StateController controller, Resources resources, Tutor.TutorType tutorType) {
		this.controller = controller;
		this.resources = resources;
		bitmaps_portal = new Bitmap[5];
		if (tutorType.compareTo(Tutor.TutorType.CAT_TUTOR) == 0) {
			bitmaps_portal[0] = BitmapFactory.decodeResource(resources, R.drawable.simons_cat_portal_1);
			bitmaps_portal[1] = BitmapFactory.decodeResource(resources, R.drawable.simons_cat_portal_2);
			bitmaps_portal[2] = BitmapFactory.decodeResource(resources, R.drawable.simons_cat_portal_3);
			bitmaps_portal[3] = BitmapFactory.decodeResource(resources, R.drawable.simons_cat_portal_4);
			bitmaps_portal[4] = BitmapFactory.decodeResource(resources, R.drawable.simons_cat_portal_5);
		} else {
			bitmaps_portal[0] = BitmapFactory.decodeResource(resources, R.drawable.tutor_dog_portal_5);
			bitmaps_portal[1] = BitmapFactory.decodeResource(resources, R.drawable.tutor_dog_portal_4);
			bitmaps_portal[2] = BitmapFactory.decodeResource(resources, R.drawable.tutor_dog_portal_3);
			bitmaps_portal[3] = BitmapFactory.decodeResource(resources, R.drawable.tutor_dog_portal_2);
			bitmaps_portal[4] = BitmapFactory.decodeResource(resources, R.drawable.tutor_dog_portal_1);
		}
		resetState();
	}

	public void resetState() {
		portAway = true;
		currentFrame = 0;
		frameCount = 5;
		notificated = false;
	}

	public static State enter(StateController controller, Resources resources, Tutor.TutorType tutorType) {
		Log.i("catroid", "State Appear");
		if (instances == null) {
			instances = new HashMap<Tutor.TutorType, StateJump>();
		}
		if (!instances.containsKey(tutorType)) {
			instances.put(tutorType, new StateJump(controller, resources, tutorType));
		}
		controller.setDisappeared(false);
		return (instances.get(tutorType));
	}

	public Bitmap updateAnimation(Tutor.TutorType tutorType) {
		if (portAway) {
			if (currentFrame < (frameCount - 1)) {
				currentFrame++;
			} else {
				controller.tutor.setNewPositionAfterPort();
				portAway = false;
			}
		} else {
			if (currentFrame > 0) {
				currentFrame--;
			} else {
				controller.changeState(StateIdle.enter(controller, resources, tutorType));
				Tutorial tut = Tutorial.getInstance(null);
				tut.setNotification("JumpDone");
				//resetState();
			}
		}
		return (bitmaps_portal[currentFrame]);

	}
}
