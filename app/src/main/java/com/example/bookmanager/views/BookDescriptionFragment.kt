package com.example.bookmanager.views

import android.app.DatePickerDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.EditText
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.bookmanager.R
import com.example.bookmanager.databinding.FragmentBookDescriptionBinding
import com.example.bookmanager.utils.C
import com.example.bookmanager.viewmodels.BookInfoViewModel
import java.text.DateFormat
import java.util.*

/**
 * 本詳細ページ内、詳細タブのフラグメント。
 */
class BookDescriptionFragment : Fragment() {

    private lateinit var bookId: String

    private lateinit var viewModel: BookInfoViewModel

    private lateinit var binding: FragmentBookDescriptionBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        bookId = arguments?.getString(C.BOOK_ID) ?: return
        val activity = activity ?: return
        viewModel = ViewModelProvider(
            this, BookInfoViewModel.Factory(activity.application, bookId)
        ).get(BookInfoViewModel::class.java)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(
            layoutInflater, R.layout.fragment_book_description, container, false
        )
        binding.also {
            it.viewModel = viewModel
            it.lifecycleOwner = activity
        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initSpinner()
        setDatePicker()
        setOnClearButtonClickListener()
    }

    private fun initSpinner() {
        val adapter = context?.let {
            val items = listOf(
                getString(R.string.book_status_planning),
                getString(R.string.book_status_reading),
                getString(R.string.book_status_finished)
            )
            ArrayAdapter(it, android.R.layout.simple_spinner_item, items).apply {
                setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            }
        }
        binding.bookStatusValue.also {
            it.adapter = adapter
            it.setSelection(viewModel.fetchCurrentStatus())
            it.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>?, view: View?, position: Int, id: Long
                ) {
                    viewModel.updateStatus(position)
                }

                override fun onNothingSelected(parent: AdapterView<*>?) {
                    TODO("Not yet implemented")
                }

            }
        }
    }

    private fun setDatePicker() {
        binding.startReadingBookDate.setOnClickListener(
            OnDateClickListener(DatePickerDialog.OnDateSetListener { _, year, month, dayOfMonth ->
                viewModel.updateStartDate("$year/${month + 1}/$dayOfMonth")
            })
        )
        binding.finishReadingBookDate.setOnClickListener(
            OnDateClickListener(DatePickerDialog.OnDateSetListener { _, year, month, dayOfMonth ->
                viewModel.updateFinishDate("$year/${month + 1}/$dayOfMonth")
            })
        )
    }

    inner class OnDateClickListener(private val onDateSetListener: DatePickerDialog.OnDateSetListener) :
        View.OnClickListener {
        override fun onClick(v: View?) {
            val context = context ?: return
            val c = Calendar.getInstance()
            val editText = v as EditText
            // 日付が入力されている場合は入力値をデフォルト値とする。
            if (editText.text.isNotBlank()) {
                val dateStr = editText.text.toString()
                val date =
                    DateFormat.getDateInstance(DateFormat.MEDIUM, Locale.JAPAN).parse(dateStr)
                date?.let { d -> c.time = d }
            }
            val dialog = DatePickerDialog(
                context,
                onDateSetListener,
                c.get(Calendar.YEAR),
                c.get(Calendar.MONTH),
                c.get(Calendar.DAY_OF_MONTH)
            )
            // 読書開始日と完了日によって日にちを制御する。
            restrictDate(editText, dialog).show()
        }
    }

    private fun setOnClearButtonClickListener() {
        binding.clearStartDateButton.setOnClickListener {
            viewModel.clearStartDate()
        }
        binding.clearFinishDateButton.setOnClickListener {
            viewModel.clearFinishDate()
        }
    }

    private fun restrictDate(view: View, dialog: DatePickerDialog): DatePickerDialog {
        when (view.id) {
            R.id.start_reading_book_date -> {
                val finishDateStr = binding.finishReadingBookDate.text.toString()
                if (finishDateStr.isNotBlank()) {
                    val dateFormat = DateFormat.getDateInstance(DateFormat.MEDIUM, Locale.JAPAN)
                    val finishDate = dateFormat.parse(finishDateStr) ?: return dialog
                    dialog.datePicker.maxDate = finishDate.time
                }
            }
            R.id.finish_reading_book_date -> {
                val startDateStr = binding.startReadingBookDate.text.toString()
                if (startDateStr.isNotBlank()) {
                    val dateFormat = DateFormat.getDateInstance(DateFormat.MEDIUM, Locale.JAPAN)
                    val startDate = dateFormat.parse(startDateStr) ?: return dialog
                    dialog.datePicker.minDate = startDate.time
                }
            }
        }
        return dialog
    }

    companion object {
        @JvmStatic
        fun getInstance(bookId: String) = BookDescriptionFragment().apply {
            arguments = Bundle().apply {
                putString(C.BOOK_ID, bookId)
            }
        }
    }
}
