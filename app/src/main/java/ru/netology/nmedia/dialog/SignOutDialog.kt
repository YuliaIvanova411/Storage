package ru.netology.nmedia.dialog

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import ru.netology.nmedia.R

class SignOutDialog : DialogFragment() {
    interface ConfirmationListener {
        fun confirmButtonClicked()
    }
    private lateinit var listener: ConfirmationListener

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        listener = activity as ConfirmationListener

        return AlertDialog.Builder(requireContext())
            .setMessage(R.string.confirm_signOut)
            .setNegativeButton(R.string.cancel, null)
            .setPositiveButton(R.string.signOut) { _, _ ->
                listener.confirmButtonClicked()
            }

            .create()
    }
    companion object {
        const val TAG = "SignOutDialog"
    }
}