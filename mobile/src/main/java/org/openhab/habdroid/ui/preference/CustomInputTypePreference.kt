/*
 * Copyright (c) 2010-2019 Contributors to the openHAB project
 *
 * See the NOTICE file(s) distributed with this work for additional
 * information.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 */

package org.openhab.habdroid.ui.preference

import android.content.Context
import android.os.Build
import android.util.AttributeSet
import android.view.View
import android.widget.EditText
import androidx.core.os.bundleOf
import androidx.fragment.app.DialogFragment
import androidx.preference.EditTextPreference
import androidx.preference.EditTextPreferenceDialogFragmentCompat

class CustomInputTypePreference constructor(context: Context, attrs: AttributeSet) :
    EditTextPreference(context, attrs) {
    private val inputType: Int
    private var autofillHints: Array<String>? = null

    init {
        val attrArray = intArrayOf(android.R.attr.inputType, android.R.attr.autofillHints)
        context.obtainStyledAttributes(attrs, attrArray).apply {
            inputType = getInt(0, 0)
            autofillHints = getString(1)?.split(',')?.toTypedArray()
            recycle()
        }
    }

    fun createDialog(): DialogFragment {
        return PrefFragment.newInstance(key, inputType, autofillHints)
    }

    class PrefFragment : EditTextPreferenceDialogFragmentCompat() {
        override fun onBindDialogView(view: View?) {
            val editor = view?.findViewById<EditText>(android.R.id.edit)
            arguments?.getInt(KEY_INPUT_TYPE)?.let { type ->
                editor?.inputType = type
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val hints = arguments?.getStringArray(KEY_AUTOFILL_HINTS)
                if (hints == null) {
                    editor?.importantForAutofill = View.IMPORTANT_FOR_AUTOFILL_NO
                } else {
                    editor?.importantForAutofill = View.IMPORTANT_FOR_AUTOFILL_YES
                    editor?.setAutofillHints(*hints)
                }
            }
            super.onBindDialogView(view)
        }

        companion object {
            private const val KEY_INPUT_TYPE = "inputType"
            private const val KEY_AUTOFILL_HINTS = "autofillHint"

            fun newInstance(key: String, inputType: Int, autofillHints: Array<String>?): PrefFragment {
                val f = PrefFragment()
                f.arguments = bundleOf(ARG_KEY to key, KEY_INPUT_TYPE to inputType,
                    KEY_AUTOFILL_HINTS to autofillHints)
                return f
            }
        }
    }
}

