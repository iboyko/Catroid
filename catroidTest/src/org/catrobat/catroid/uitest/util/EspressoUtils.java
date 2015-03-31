/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2015 The Catrobat Team
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

package org.catrobat.catroid.uitest.util;

import android.content.res.Resources;
import android.os.Build;
import android.support.test.espresso.matcher.ViewMatchers;
import android.test.InstrumentationTestCase;

import org.catrobat.catroid.R;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.Espresso.openActionBarOverflowOrOptionsMenu;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.matcher.ViewMatchers.withId;

/**
 * Espresso Utils.
 * Created by illya on 29/03/15.
 */
public class EspressoUtils {
	public static void openCopyActionMode(InstrumentationTestCase test) {
		openActionBarOverflowOrOptionsMenu(test.getInstrumentation().getTargetContext());
		onView(ViewMatchers.withText(R.string.copy)).perform(click());
	}

	public static void openDeleteActionMode() {
		onView(withId(R.id.delete)).perform(click());
	}

	public static void acceptAndCloseActionMode() {
		onView(withId(getActionModeCloseBtnId())).perform(click());
	}


	private static int getActionModeCloseBtnId() {
		int actionModeCloseBtnId;
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
			actionModeCloseBtnId = Resources.getSystem().getIdentifier("action_mode_close_button", "id", "android");
		} else {
			actionModeCloseBtnId = R.id.abs__action_mode_close_button;
		}
		return actionModeCloseBtnId;
	}


}
