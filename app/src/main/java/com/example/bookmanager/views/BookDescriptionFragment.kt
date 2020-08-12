package com.example.bookmanager.views

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.example.bookmanager.R
import com.example.bookmanager.databinding.FragmentBookDescriptionBinding
import com.example.bookmanager.utils.C

class BookDescriptionFragment : Fragment() {

    private lateinit var bookDescription: String

    private lateinit var binding: FragmentBookDescriptionBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        arguments?.let {
            if (it.getString(C.BOOK_DESCRIPTION) != null) {
                bookDescription = it.getString(C.BOOK_DESCRIPTION) as String
            } else {
                // TODO: パラメーターが null だった場合の処理。
                activity?.finish()
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(
            layoutInflater,
            R.layout.fragment_book_description,
            container,
            false
        )

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.bookDescSummary.text = bookDescription
    }

    companion object {
        @JvmStatic
        fun newInstance(bookDescription: String) = BookDescriptionFragment().apply {
            arguments = Bundle().apply {
                putString(C.BOOK_DESCRIPTION, bookDescription)
            }
        }
    }
}
