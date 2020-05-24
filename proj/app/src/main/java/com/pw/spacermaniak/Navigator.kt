package com.pw.spacermaniak

import android.content.Context
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import com.pw.spacermaniak.presentation.MapFragment

class Navigator {
    companion object {
        fun navigateToMap(
            activity: FragmentActivity?
        ){
            replaceFragment(
                MapFragment(),
                activity
            )
        }

        private fun replaceFragment(
            fragment: Fragment,
            activity: FragmentActivity?

        ) {
            activity?.supportFragmentManager?.beginTransaction()?.run {
                setCustomAnimations(R.anim.fade_in, 0, 0, R.anim.fade_out)
                replace(R.id.root_container, fragment)
                commit()
            }
        }

        fun hideKeyboard(view : View) {
            val imm = view.context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(view.windowToken, 0)
        }
    }
}