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
package org.catrobat.catroid.uitest.content.brick;

import android.graphics.Rect;
import android.support.test.espresso.DataInteraction;
import android.support.test.espresso.UiController;
import android.support.test.espresso.ViewAction;
import android.support.test.espresso.matcher.ViewMatchers;
import android.util.Log;
import android.view.View;
import android.widget.AbsListView;
import android.widget.ListView;

import org.catrobat.catroid.ProjectManager;
import org.catrobat.catroid.R;
import org.catrobat.catroid.content.Project;
import org.catrobat.catroid.content.Script;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.content.StartScript;
import org.catrobat.catroid.content.bricks.Brick;
import org.catrobat.catroid.content.bricks.ChangeYByNBrick;
import org.catrobat.catroid.content.bricks.IfLogicBeginBrick;
import org.catrobat.catroid.content.bricks.IfLogicElseBrick;
import org.catrobat.catroid.content.bricks.IfLogicEndBrick;
import org.catrobat.catroid.content.bricks.SetLookBrick;
import org.catrobat.catroid.ui.MainMenuActivity;
import org.catrobat.catroid.ui.adapter.BrickAdapter;
import org.catrobat.catroid.uitest.util.BaseActivityInstrumentationTestCase;
import org.catrobat.catroid.uitest.util.EspressoUtils;
import org.catrobat.catroid.uitest.util.UiTestUtils;
import org.hamcrest.Matcher;

import java.util.ArrayList;
import java.util.List;

import static android.support.test.espresso.Espresso.onData;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isChecked;
import static android.support.test.espresso.matcher.ViewMatchers.isNotChecked;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.is;

public class IfBrickTest extends BaseActivityInstrumentationTestCase<MainMenuActivity> {
	private static final String TAG = IfBrickTest.class.getSimpleName();
	private Project project;
	private IfLogicBeginBrick ifBrick;

	public IfBrickTest() {
		super(MainMenuActivity.class);
	}

	@Override
	public void setUp() throws Exception {
		super.setUp();
		createProject();
		UiTestUtils.getIntoScriptActivityFromMainMenu(solo);
	}

	public void testIfBrick() {
		ListView view = UiTestUtils.getScriptListView(solo);
		ListView dragDropListView = UiTestUtils.getScriptListView(solo);
		BrickAdapter adapter = (BrickAdapter) view.getAdapter();

		int childrenCount = adapter.getChildCountFromLastGroup();

		UiTestUtils.testBrickWithFormulaEditor(solo, ProjectManager.getInstance().getCurrentSprite(),
				R.id.brick_if_begin_edit_text, 5, Brick.BrickField.IF_CONDITION, ifBrick);

		assertEquals("Incorrect number of bricks.", 6, dragDropListView.getChildCount()); // don't forget the footer
		assertEquals("Incorrect number of bricks.", 0, childrenCount);

		ArrayList<Brick> projectBrickList = project.getSpriteList().get(0).getScript(0).getBrickList();
		assertEquals("Incorrect number of bricks.", 4, projectBrickList.size());

		assertTrue("Wrong Brick instance.", projectBrickList.get(0) instanceof IfLogicBeginBrick);
		assertNotNull("TextView does not exist", solo.getText(getActivity().getString(R.string.brick_if_begin)));
	}

	public void testStrings() {
		DataInteraction ifLogicBeginBrickDi = onData(isBrick(IfLogicBeginBrick.class));
		ifLogicBeginBrickDi.perform(click());

		onView(withId(R.id.if_label)).check(matches(withText(R.string.brick_if_begin)));
		onView(withId(R.id.if_label_second_part)).check(matches(withText(R.string.brick_if_begin_second_part)));
	}

	public void testIfBrickParts() {
		int dragAndDropSteps = 100;

		ArrayList<Brick> projectBrickList = project.getSpriteList().get(0).getScript(0).getBrickList();
		Log.d(TAG, "Before drag item 1 to item 4 + 20");

		logBrickListForJenkins(projectBrickList);

//		CollectYPosOfListItemsAction collectYPosOfListItemsAction = new CollectYPosOfListItemsAction();
//		onView(withId(android.R.id.list)).perform(collectYPosOfListItemsAction);
		List<Integer> yPosition = UiTestUtils.getListItemYPositions(solo, 0);

//		final List<Integer> yPosition = collectYPosOfListItemsAction.getYPositions();

		int oldYto = yPosition.get(4) + 20;

//		onData(isBrick(IfLogicBeginBrick.class)).perform(ViewActions.longClick());
		UiTestUtils.longClickAndDrag(solo, 10, yPosition.get(1), 10, oldYto, dragAndDropSteps);

		boolean result = solo.waitForLogMessage("longClickAndDrag finished: " + oldYto, 1000);
		assertTrue("Timeout during longClickAndDrag on item 1! y-Coordinate: " + oldYto, result);

		assertEquals("Incorrect number of bricks.", 4, projectBrickList.size());
		assertTrue("Wrong Brick instance.", (projectBrickList.get(1) instanceof IfLogicBeginBrick));

		Log.d(TAG, "Before drag item 2 to item 0");
		logBrickListForJenkins(projectBrickList);

		yPosition = UiTestUtils.getListItemYPositions(solo, 0);
		oldYto = yPosition.get(0);
		UiTestUtils.longClickAndDrag(solo, 10, yPosition.get(2), 10, oldYto, dragAndDropSteps);

		result = solo.waitForLogMessage("longClickAndDrag finished: " + oldYto, 1000);
		assertTrue("Timeout during longClickAndDrag on item 2! y-Coordinate: " + oldYto, result);

		assertEquals("Incorrect number of bricks.", 4, projectBrickList.size());
		assertTrue("Wrong Brick instance.", (projectBrickList.get(0) instanceof IfLogicBeginBrick));

		Log.d(TAG, "Before drag item 3 to item 0");
		logBrickListForJenkins(projectBrickList);

		yPosition = UiTestUtils.getListItemYPositions(solo, 0);
		oldYto = yPosition.get(0);
		UiTestUtils.longClickAndDrag(solo, 10, yPosition.get(3), 10, oldYto, dragAndDropSteps);

		result = solo.waitForLogMessage("longClickAndDrag finished: " + oldYto, 1000);
		assertTrue("Timeout during longClickAndDrag on item 3! y-Coordinate: " + oldYto, result);

		assertEquals("Incorrect number of bricks.", 4, projectBrickList.size());
		assertTrue("Wrong Brick instance - expected IfElseBrick but was "
						+ projectBrickList.get(1).getClass().getSimpleName(),
				projectBrickList.get(1) instanceof IfLogicElseBrick);

		assertTrue("Wrong Brick instance - expected ChangeYByNBrick but was "
						+ projectBrickList.get(2).getClass().getSimpleName(),
				projectBrickList.get(2) instanceof ChangeYByNBrick
		);

		Log.d(TAG, "Before drag item 4 to item 0");
		logBrickListForJenkins(projectBrickList);

		yPosition = UiTestUtils.getListItemYPositions(solo, 0);
		oldYto = yPosition.get(0);
		UiTestUtils.longClickAndDrag(solo, 10, yPosition.get(4) - 10, 10, oldYto, dragAndDropSteps);
		assertEquals("Incorrect number of bricks.", 4, projectBrickList.size());

		result = solo.waitForLogMessage("longClickAndDrag finished: " + oldYto, 1000);
		assertTrue("Timeout during longClickAndDrag on item 4! y-Coordinate: " + oldYto, result);

		Log.d(TAG, "After drag item 4 to item 0");
		logBrickListForJenkins(projectBrickList);

		assertTrue("Wrong Brick instance, expected IfLogicEndBrick but was "
						+ projectBrickList.get(2).getClass().getSimpleName(),
				projectBrickList.get(2) instanceof IfLogicEndBrick);

		UiTestUtils.addNewBrick(solo, R.string.brick_broadcast_receive);
		yPosition = UiTestUtils.getListItemYPositions(solo, 0);

		Sprite sprite = ProjectManager.getInstance().getCurrentSprite();
		assertEquals("Incorrect number of Scripts.", 2, sprite.getNumberOfScripts());

		solo.goBack();

		yPosition = UiTestUtils.getListItemYPositions(solo, 0);
		solo.clickOnScreen(20, yPosition.get(3));
		clickOnDeleteInDialog();

		assertEquals("Incorrect number of bricks.", 1, projectBrickList.size());
		assertTrue("Wrong Brick instance.", projectBrickList.get(0) instanceof ChangeYByNBrick);

		yPosition = UiTestUtils.getListItemYPositions(solo, 0);
		oldYto = yPosition.get(2) + 20;
		UiTestUtils.longClickAndDrag(solo, 10, yPosition.get(1), 10, oldYto, dragAndDropSteps);

		result = solo.waitForLogMessage("longClickAndDrag finished: " + oldYto, 1000);
		assertTrue("Timeout during longClickAndDrag! y-Coordinate: " + oldYto, result);

		assertEquals("Incorrect number of bricks.", 0, projectBrickList.size());
		projectBrickList = project.getSpriteList().get(0).getScript(1).getBrickList();

		assertEquals("Incorrect number of bricks.", 1, projectBrickList.size());
		assertTrue("Wrong Brick instance.", projectBrickList.get(0) instanceof ChangeYByNBrick);

		UiTestUtils.addNewBrick(solo, R.string.brick_if_begin);
		yPosition = UiTestUtils.getListItemYPositions(solo, 0);
		int addedYPosition = UiTestUtils.getAddedListItemYPosition(solo);
		solo.drag(20, 20, addedYPosition, yPosition.get(3) + 20, dragAndDropSteps);

		UiTestUtils.addNewBrick(solo, R.string.brick_set_look);
		yPosition = UiTestUtils.getListItemYPositions(solo, 0);
		addedYPosition = UiTestUtils.getAddedListItemYPosition(solo);
		solo.drag(20, 20, addedYPosition, yPosition.get(5) + 20, dragAndDropSteps);

		yPosition = UiTestUtils.getListItemYPositions(solo, 0);
		oldYto = yPosition.get(5) + 20;
		UiTestUtils.longClickAndDrag(solo, 10, yPosition.get(4), 10, oldYto, dragAndDropSteps);
		projectBrickList = project.getSpriteList().get(0).getScript(1).getBrickList();

		result = solo.waitForLogMessage("longClickAndDrag finished: " + oldYto, 1000);
		assertTrue("Timeout during longClickAndDrag! y-Coordinate: " + oldYto, result);

		Log.d(TAG, "Final order of bricks");
		logBrickListForJenkins(projectBrickList);

		assertTrue("Wrong Brick instance.", projectBrickList.get(0) instanceof ChangeYByNBrick);
		assertTrue("Wrong Brick instance.", projectBrickList.get(1) instanceof IfLogicBeginBrick);
		assertTrue("Wrong Brick instance.", projectBrickList.get(2) instanceof SetLookBrick);
		assertTrue("Wrong Brick instance.", projectBrickList.get(3) instanceof IfLogicElseBrick);
		assertTrue("Wrong Brick instance.", projectBrickList.get(4) instanceof IfLogicEndBrick);
	}

	public void testCopyIfLogicBeginBrickActionMode() {
		//Open Copy ActionMode
		EspressoUtils.openCopyActionMode(this);

		onData(isBrick(IfLogicBeginBrick.class)).perform(click());

		//Close ActionMode: Will Copy Selected Bricks.
		EspressoUtils.acceptAndCloseActionMode();

		ArrayList<Brick> projectBrickList = project.getSpriteList().get(0).getScript(0).getBrickList();
		assertEquals("Incorrect number of bricks.", 7, projectBrickList.size());
		assertTrue("Wrong Brick instance.", projectBrickList.get(0) instanceof IfLogicBeginBrick);
		assertTrue("Wrong Brick instance.", projectBrickList.get(1) instanceof ChangeYByNBrick);
		assertTrue("Wrong Brick instance.", projectBrickList.get(2) instanceof IfLogicElseBrick);
		assertTrue("Wrong Brick instance.", projectBrickList.get(3) instanceof IfLogicEndBrick);
		assertTrue("Wrong Brick instance.", projectBrickList.get(4) instanceof IfLogicBeginBrick);
		assertTrue("Wrong Brick instance.", projectBrickList.get(5) instanceof IfLogicElseBrick);
		assertTrue("Wrong Brick instance.", projectBrickList.get(6) instanceof IfLogicEndBrick);
	}

	public void testCopyIfLogicElseBrickActionMode() {

		EspressoUtils.openCopyActionMode(this);

		onData(isBrick(IfLogicBeginBrick.class)).perform(click());

		//Close ActionMode: Will Copy Selected Bricks.
		EspressoUtils.acceptAndCloseActionMode();


		ArrayList<Brick> projectBrickList = project.getSpriteList().get(0).getScript(0).getBrickList();
		assertEquals("Incorrect number of bricks.", 7, projectBrickList.size());
		assertTrue("Wrong Brick instance.", projectBrickList.get(0) instanceof IfLogicBeginBrick);
		assertTrue("Wrong Brick instance.", projectBrickList.get(1) instanceof ChangeYByNBrick);
		assertTrue("Wrong Brick instance.", projectBrickList.get(2) instanceof IfLogicElseBrick);
		assertTrue("Wrong Brick instance.", projectBrickList.get(3) instanceof IfLogicEndBrick);
		assertTrue("Wrong Brick instance.", projectBrickList.get(4) instanceof IfLogicBeginBrick);
		assertTrue("Wrong Brick instance.", projectBrickList.get(5) instanceof IfLogicElseBrick);
		assertTrue("Wrong Brick instance.", projectBrickList.get(6) instanceof IfLogicEndBrick);
	}

	private Matcher<Object> isBrick(Class<? extends Brick> clazz) {
		return is(instanceOf(clazz));
	}

	public void testCopyIfLogicEndBrickActionMode() {
		//Open Copy ActionMode
		EspressoUtils.openCopyActionMode(this);

		onData(isBrick(IfLogicEndBrick.class)).perform(click());

		//Close ActionMode: Will Copy Selected Bricks.
		EspressoUtils.acceptAndCloseActionMode();

		ArrayList<Brick> projectBrickList = project.getSpriteList().get(0).getScript(0).getBrickList();
		assertEquals("Incorrect number of bricks.", 7, projectBrickList.size());
		assertTrue("Wrong Brick instance.", projectBrickList.get(0) instanceof IfLogicBeginBrick);
		assertTrue("Wrong Brick instance.", projectBrickList.get(1) instanceof ChangeYByNBrick);
		assertTrue("Wrong Brick instance.", projectBrickList.get(2) instanceof IfLogicElseBrick);
		assertTrue("Wrong Brick instance.", projectBrickList.get(3) instanceof IfLogicEndBrick);
		assertTrue("Wrong Brick instance.", projectBrickList.get(4) instanceof IfLogicBeginBrick);
		assertTrue("Wrong Brick instance.", projectBrickList.get(5) instanceof IfLogicElseBrick);
		assertTrue("Wrong Brick instance.", projectBrickList.get(6) instanceof IfLogicEndBrick);
	}

	public void testSelectionAfterCopyActionMode() {

		onData(is(instanceOf(IfLogicBeginBrick.class))).check(matches(isNotChecked()));
		onData(isBrick(IfLogicBeginBrick.class)).check(matches(isNotChecked()));
		onData(is(instanceOf(IfLogicEndBrick.class))).check(matches(isNotChecked()));

		//Do Test
		//Open Copy ActionMode
		EspressoUtils.openCopyActionMode(this);

		onData(isBrick(IfLogicBeginBrick.class)).perform(click());

		//Close ActionMode: Will Copy Selected Bricks.
		EspressoUtils.acceptAndCloseActionMode();

		onData(isBrick(IfLogicBeginBrick.class)).atPosition(1).check(matches(isNotChecked()));
		onData(isBrick(IfLogicElseBrick.class)).atPosition(1).check(matches(isNotChecked()));
		onData(isBrick(IfLogicEndBrick.class)).atPosition(1).check(matches(isNotChecked()));

		//Open Delete ActionMode (It is not in Overflow menu)
		onView(withId(R.id.delete)).perform(click());

		//Click to checked Second If Bricks
		onData(is(instanceOf(IfLogicBeginBrick.class))).atPosition(1).perform(click());

		// Should be not checked!
		onData(is(instanceOf(IfLogicBeginBrick.class))).atPosition(0).check(matches(isNotChecked()));
		onData(isBrick(IfLogicBeginBrick.class)).atPosition(0).check(matches(isNotChecked()));
		onData(is(instanceOf(IfLogicEndBrick.class))).atPosition(0).check(matches(isNotChecked()));

		// Should be checked!
		onData(is(instanceOf(IfLogicBeginBrick.class))).atPosition(1).check(matches(isChecked()));
		onData(isBrick(IfLogicBeginBrick.class)).atPosition(1).check(matches(isChecked()));
		onData(is(instanceOf(IfLogicEndBrick.class))).atPosition(1).check(matches(isChecked()));

	}

	public void testSelectionActionMode() {

		onData(isBrick(IfLogicBeginBrick.class)).check(matches(isNotChecked()));
		onData(isBrick(IfLogicBeginBrick.class)).check(matches(isNotChecked()));
		onData(isBrick(IfLogicEndBrick.class)).check(matches(isNotChecked()));

		onData(is(instanceOf(ChangeYByNBrick.class))).check(matches(isNotChecked()));

		// Do Test 1
		//Open Copy ActionMode
		EspressoUtils.openCopyActionMode(this);

		//Click to select All If Bricks
		onData(isBrick(IfLogicBeginBrick.class)).perform(click());

		onData(isBrick(IfLogicBeginBrick.class)).check(matches(isChecked()));
		onData(isBrick(IfLogicBeginBrick.class)).check(matches(isChecked()));
		onData(isBrick(IfLogicEndBrick.class)).check(matches(isChecked()));
		onData(isBrick(ChangeYByNBrick.class)).check(matches(isNotChecked()));

		//Close ActionMode: Will Copy Selected Bricks.
		EspressoUtils.acceptAndCloseActionMode();

		// Do Test 2
		//Open Delete ActionMode (It is not in Overflow menu)

		//Open Copy ActionMode
		EspressoUtils.openDeleteActionMode();

		//Click to checked First If Bricks
		onData(is(instanceOf(IfLogicBeginBrick.class))).atPosition(0).perform(click());

		//Assert First If Bricks are checked
		onData(is(instanceOf(IfLogicBeginBrick.class))).atPosition(0).check(matches(isChecked()));
		onData(isBrick(IfLogicBeginBrick.class)).atPosition(0).check(matches(isChecked()));
		onData(is(instanceOf(IfLogicEndBrick.class))).atPosition(0/*2 IfLogicBeginBrick are present now in List View*/).check(matches(isChecked()));

		//Assert ChangeYByNBrick is not are checked
		onData(is(instanceOf(ChangeYByNBrick.class))).check(matches(isNotChecked()));

	}


	private void logBrickListForJenkins(ArrayList<Brick> projectBrickList) {
		for (Brick brick : projectBrickList) {
			Log.d(TAG, "Brick at Position " + projectBrickList.indexOf(brick) + ": " + brick.getClass().getSimpleName());
		}
	}

	private void createProject() {
		project = new Project(null, UiTestUtils.DEFAULT_TEST_PROJECT_NAME);
		Sprite sprite = new Sprite("cat");
		Script script = new StartScript();
		ifBrick = new IfLogicBeginBrick(0);
		IfLogicElseBrick ifElseBrick = new IfLogicElseBrick(ifBrick);
		IfLogicEndBrick ifEndBrick = new IfLogicEndBrick(ifElseBrick, ifBrick);
		ifBrick.setIfElseBrick(ifElseBrick);
		ifBrick.setIfEndBrick(ifEndBrick);

		script.addBrick(ifBrick);
		script.addBrick(new ChangeYByNBrick(-10));
		script.addBrick(ifElseBrick);
		script.addBrick(ifEndBrick);

		sprite.addScript(script);
		sprite.addScript(new StartScript());
		project.addSprite(sprite);

		ProjectManager.getInstance().setProject(project);
		ProjectManager.getInstance().setCurrentSprite(sprite);
		ProjectManager.getInstance().setCurrentScript(script);
	}

	private void clickOnDeleteInDialog() {
		if (!solo.waitForText(solo.getString(R.string.brick_context_dialog_delete_brick), 0, 5000)) {
			fail("Text not shown in 5 secs!");
		}

		solo.clickOnText(solo.getString(R.string.brick_context_dialog_delete_brick));
		solo.clickOnText(solo.getString(R.string.yes));
		if (!solo.waitForView(ListView.class, 0, 5000)) {
			fail("Dialog does not close in 5 sec!");
		}
	}

	private static class CollectYPosOfListItemsAction implements ViewAction {
		private final List<Integer> yPositionList = new ArrayList<Integer>();

		public CollectYPosOfListItemsAction() {
		}

		@Override
		public Matcher<View> getConstraints() {
			return allOf(new Matcher[]{ViewMatchers.isAssignableFrom(AbsListView.class), ViewMatchers.isDisplayed()});
		}

		@Override
		public String getDescription() {
			return "get y-Pos of List Items";
		}

		@Override
		public void perform(UiController uiController, View view) {

			if (view instanceof AbsListView) {
				AbsListView lv = (AbsListView) view;
				for (int i = 0; i < lv.getChildCount(); ++i) {
					View currentViewInList = lv.getChildAt(i);
					Rect globalVisibleRectangle = new Rect();
					currentViewInList.getGlobalVisibleRect(globalVisibleRectangle);
					int middleYPosition = globalVisibleRectangle.top + globalVisibleRectangle.height() / 2;
					yPositionList.add(middleYPosition);
				}

			}

		}

		public List<Integer> getYPositions() {
			return yPositionList;
		}
	}
}
