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

package org.catrobat.catroid.ui

import android.graphics.Color
import android.graphics.Typeface
import android.os.Bundle
import android.util.TypedValue
import android.view.MenuItem
import android.view.View
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import org.catrobat.catroid.ProjectManager
import org.catrobat.catroid.R
import org.catrobat.catroid.examples.ExampleLauncher
import org.catrobat.catroid.examples.ExampleProjects
import org.koin.android.ext.android.inject

class LearnActivity : BaseActivity() {

    private data class Lesson(val titleRes: Int, val bodyRes: Int, val exampleIndex: Int)

    private val projectManager: ProjectManager by inject()

    private val lessons = listOf(
        Lesson(R.string.learn_lesson1_title, R.string.learn_lesson1_body, -1),
        Lesson(R.string.learn_lesson2_title, R.string.learn_lesson2_body, -1),
        Lesson(R.string.learn_lesson3_title, R.string.learn_lesson3_body, ExampleProjects.BOUNCING_BALL),
        Lesson(R.string.learn_lesson4_title, R.string.learn_lesson4_body, ExampleProjects.TAP_THE_STAR),
        Lesson(R.string.learn_lesson5_title, R.string.learn_lesson5_body, ExampleProjects.WALKING_ROBOT)
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_learn)
        setSupportActionBar(findViewById(R.id.toolbar))
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setTitle(R.string.menu_learn)

        val container = findViewById<LinearLayout>(R.id.lessons_container)
        lessons.forEachIndexed { index, lesson -> container.addView(buildLesson(lesson, index == 0)) }
    }

    private fun buildLesson(lesson: Lesson, expanded: Boolean): View {
        val card = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply { bottomMargin = dp(10) }
            setBackgroundColor(ContextCompat.getColor(context, R.color.toolbar_background))
        }

        val body = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(dp(16), dp(4), dp(16), dp(16))
            visibility = if (expanded) View.VISIBLE else View.GONE
        }
        body.addView(TextView(this).apply {
            text = getString(lesson.bodyRes)
            setTextSize(TypedValue.COMPLEX_UNIT_SP, 16f)
            setTextColor(Color.WHITE)
            setLineSpacing(0f, 1.25f)
        })
        if (lesson.exampleIndex >= 0) {
            body.addView(Button(this).apply {
                text = getString(R.string.learn_try_example)
                setOnClickListener {
                    ExampleLauncher.createAndOpen(this@LearnActivity, projectManager, lesson.exampleIndex)
                }
            })
        }

        val title = TextView(this).apply {
            text = getString(lesson.titleRes)
            setTextSize(TypedValue.COMPLEX_UNIT_SP, 18f)
            setTypeface(typeface, Typeface.BOLD)
            setTextColor(ContextCompat.getColor(context, R.color.view_holder_item_title))
            setPadding(dp(16), dp(16), dp(16), dp(16))
            isClickable = true
            isFocusable = true
            setOnClickListener {
                body.visibility = if (body.visibility == View.VISIBLE) View.GONE else View.VISIBLE
            }
        }

        card.addView(title)
        card.addView(body)
        return card
    }

    private fun dp(value: Int): Int = (value * resources.displayMetrics.density).toInt()

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            finish()
            return true
        }
        return super.onOptionsItemSelected(item)
    }
}
