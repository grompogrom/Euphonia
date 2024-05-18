package com.euphoiniateam.euphonia.ui.settings

import android.app.Dialog
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import com.euphoiniateam.euphonia.R

class DialogLoadingFragment : DialogFragment() {
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return Dialog(requireContext(), R.style.TransparentDialog).apply {
            setContentView(R.layout.fragment_loading)
            this.setCanceledOnTouchOutside(false)
        }
    }

    companion object {
        const val TAG = "LoadingDialogFragment"
    }
}
