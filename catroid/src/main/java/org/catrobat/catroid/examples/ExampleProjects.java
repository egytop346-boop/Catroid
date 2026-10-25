/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2025 The Catrobat Team
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

package org.catrobat.catroid.examples;

import android.content.Context;

import org.catrobat.catroid.R;
import org.catrobat.catroid.common.LookData;
import org.catrobat.catroid.content.Project;
import org.catrobat.catroid.content.Scene;
import org.catrobat.catroid.content.Script;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.content.StartScript;
import org.catrobat.catroid.content.WhenScript;
import org.catrobat.catroid.content.bricks.ChangeSizeByNBrick;
import org.catrobat.catroid.content.bricks.ChangeXByNBrick;
import org.catrobat.catroid.content.bricks.ForeverBrick;
import org.catrobat.catroid.content.bricks.IfOnEdgeBounceBrick;
import org.catrobat.catroid.content.bricks.MoveNStepsBrick;
import org.catrobat.catroid.content.bricks.NextLookBrick;
import org.catrobat.catroid.content.bricks.PlaceAtBrick;
import org.catrobat.catroid.content.bricks.PointInDirectionBrick;
import org.catrobat.catroid.content.bricks.RepeatBrick;
import org.catrobat.catroid.content.bricks.SayForBubbleBrick;
import org.catrobat.catroid.content.bricks.SetSizeToBrick;
import org.catrobat.catroid.content.bricks.TurnRightBrick;
import org.catrobat.catroid.content.bricks.WaitBrick;
import org.catrobat.catroid.formulaeditor.Formula;
import org.catrobat.catroid.io.ResourceImporter;
import org.catrobat.catroid.io.XstreamSerializer;
import org.catrobat.catroid.utils.FileMetaDataExtractor;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import static org.catrobat.catroid.common.Constants.DEFAULT_IMAGE_EXTENSION;
import static org.catrobat.catroid.common.Constants.IMAGE_DIRECTORY_NAME;
import static org.catrobat.catroid.common.Constants.SCREENSHOT_AUTOMATIC_FILE_NAME;
import static org.catrobat.catroid.common.FlavoredConstants.DEFAULT_ROOT_DIRECTORY;

/**
 * Ready-made example projects that ship inside the app. They are built from code with the
 * same classes the editor uses, so no external files or downloads are needed.
 */
public final class ExampleProjects {

	public static final int BOUNCING_BALL = 0;
	public static final int TAP_THE_STAR = 1;
	public static final int WALKING_ROBOT = 2;
	public static final int COUNT = 3;

	private static final String IMAGE_FILE_NAME = "img" + DEFAULT_IMAGE_EXTENSION;

	private ExampleProjects() {
		throw new AssertionError("No.");
	}

	public static Project create(Context context, int index) throws IOException {
		switch (index) {
			case BOUNCING_BALL:
				return createBouncingBall(context);
			case TAP_THE_STAR:
				return createTapTheStar(context);
			case WALKING_ROBOT:
				return createWalkingRobot(context);
			default:
				throw new IllegalArgumentException("Unknown example project: " + index);
		}
	}

	private static Project createBouncingBall(Context context) throws IOException {
		Project project = newProject(context, R.string.example_bounce_name);
		Scene scene = project.getDefaultScene();

		Sprite ball = new Sprite(context.getString(R.string.example_bounce_sprite));
		scene.addSprite(ball);
		addLook(context, scene, ball, R.drawable.ex_ball, R.string.example_bounce_sprite);

		Script start = new StartScript();
		start.addBrick(new PlaceAtBrick(new Formula(0), new Formula(0)));
		start.addBrick(new SetSizeToBrick(new Formula(60)));
		start.addBrick(new PointInDirectionBrick(new Formula(45)));
		ForeverBrick forever = new ForeverBrick();
		forever.addBrick(new MoveNStepsBrick(new Formula(10)));
		forever.addBrick(new IfOnEdgeBounceBrick());
		start.addBrick(forever);
		ball.addScript(start);

		Script tapped = new WhenScript();
		tapped.addBrick(new TurnRightBrick(new Formula(45)));
		ball.addScript(tapped);

		return finish(context, project, scene, R.drawable.ex_preview_bounce);
	}

	private static Project createTapTheStar(Context context) throws IOException {
		Project project = newProject(context, R.string.example_star_name);
		Scene scene = project.getDefaultScene();

		Sprite star = new Sprite(context.getString(R.string.example_star_sprite));
		scene.addSprite(star);
		addLook(context, scene, star, R.drawable.ex_star, R.string.example_star_sprite);

		Script start = new StartScript();
		start.addBrick(new PlaceAtBrick(new Formula(0), new Formula(0)));
		start.addBrick(new SetSizeToBrick(new Formula(50)));
		star.addScript(start);

		Script tapped = new WhenScript();
		tapped.addBrick(new ChangeSizeByNBrick(new Formula(10)));
		RepeatBrick repeat = new RepeatBrick(new Formula(10));
		repeat.addBrick(new TurnRightBrick(new Formula(36)));
		repeat.addBrick(new WaitBrick(new Formula(0.05)));
		tapped.addBrick(repeat);
		star.addScript(tapped);

		return finish(context, project, scene, R.drawable.ex_preview_star);
	}

	private static Project createWalkingRobot(Context context) throws IOException {
		Project project = newProject(context, R.string.example_robot_name);
		Scene scene = project.getDefaultScene();

		Sprite robot = new Sprite(context.getString(R.string.example_robot_sprite));
		scene.addSprite(robot);
		String robotName = context.getString(R.string.example_robot_sprite);
		addLook(context, scene, robot, R.drawable.ex_robot_a, robotName + " 1");
		addLook(context, scene, robot, R.drawable.ex_robot_b, robotName + " 2");

		Script start = new StartScript();
		start.addBrick(new SetSizeToBrick(new Formula(60)));
		ForeverBrick forever = new ForeverBrick();
		forever.addBrick(new PlaceAtBrick(new Formula(-300), new Formula(0)));
		RepeatBrick walk = new RepeatBrick(new Formula(40));
		walk.addBrick(new NextLookBrick());
		walk.addBrick(new ChangeXByNBrick(new Formula(15)));
		walk.addBrick(new WaitBrick(new Formula(0.15)));
		forever.addBrick(walk);
		start.addBrick(forever);
		robot.addScript(start);

		Script tapped = new WhenScript();
		tapped.addBrick(new SayForBubbleBrick(context.getString(R.string.example_robot_hello), 2f));
		robot.addScript(tapped);

		return finish(context, project, scene, R.drawable.ex_preview_robot);
	}

	private static Project newProject(Context context, int nameResId) throws IOException {
		String baseName = context.getString(nameResId);
		String name = baseName;
		int counter = 2;
		while (new File(DEFAULT_ROOT_DIRECTORY,
				FileMetaDataExtractor.encodeSpecialCharsForFileSystem(name)).exists()) {
			name = baseName + " " + counter;
			counter++;
		}

		Project project = new Project(context, name, false);
		if (project.getDirectory().exists()) {
			throw new IOException("Cannot create new project at "
					+ project.getDirectory().getAbsolutePath()
					+ ", directory already exists.");
		}

		XstreamSerializer.getInstance().saveProject(project);

		if (!project.getDirectory().isDirectory()) {
			throw new FileNotFoundException("Cannot create project at "
					+ project.getDirectory().getAbsolutePath());
		}
		return project;
	}

	private static void addLook(Context context, Scene scene, Sprite sprite, int drawableId,
			int lookNameResId) throws IOException {
		addLook(context, scene, sprite, drawableId, context.getString(lookNameResId));
	}

	private static void addLook(Context context, Scene scene, Sprite sprite, int drawableId,
			String lookName) throws IOException {
		File imageDir = new File(scene.getDirectory(), IMAGE_DIRECTORY_NAME);
		File imageFile = ResourceImporter.createImageFileFromResourcesInDirectory(
				context.getResources(), drawableId, imageDir, IMAGE_FILE_NAME, 1);
		sprite.getLookList().add(new LookData(lookName, imageFile));
	}

	private static Project finish(Context context, Project project, Scene scene, int previewDrawableId)
			throws IOException {
		ResourceImporter.createImageFileFromResourcesInDirectory(context.getResources(),
				previewDrawableId, scene.getDirectory(), SCREENSHOT_AUTOMATIC_FILE_NAME, 1);
		XstreamSerializer.getInstance().saveProject(project);
		return project;
	}
}
