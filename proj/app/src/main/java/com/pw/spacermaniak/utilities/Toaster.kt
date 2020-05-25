package com.pw.spacermaniak.utilities

import android.content.Context
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import com.pw.spacermaniak.presentation.MapFragment
import com.pw.spacermaniak.presentation.SearchFragment

class Toaster {
    companion object {
        fun toast(context: Context, msg: String){
            Toast.makeText(
                context,
                "Error:route results returned is not valid",
                Toast.LENGTH_SHORT
            ).show()
        }
    }
}