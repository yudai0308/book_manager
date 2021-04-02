package com.example.bookmanager.views

import android.app.DatePickerDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.bookmanager.R
import com.example.bookmanager.databinding.FragmentBookMemoBinding
import com.example.bookmanager.rooms.entities.Book
import com.example.bookmanager.utils.C
import com.example.bookmanager.viewmodels.BookInfoViewModel
import java.text.DateFormat
import java.util.*

/**
 * 本詳細ページ内、メモタブのフラグメント。
 */
class BookMemoFragment : Fragment() {

    private lateinit var bookId: String

    private lateinit var viewModel: BookInfoViewModel

    private lateinit var binding: FragmentBookMemoBinding

    private var selectedStatusButtonId: Int? = null

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
            layoutInflater, R.layout.fragment_book_memo, container, false
        )
        binding.also {
            it.viewModel = viewModel
            it.lifecycleOwner = activity
        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        selectedStatusButtonId = when (viewModel.statusCode.value) {
            Book.Status.WANT_TO_READ.code -> binding.bookStatusButtons.statusButtonWantToRead.id
            Book.Status.READING.code -> binding.bookStatusButtons.statusButtonReading.id
            Book.Status.FINISHED.code -> binding.bookStatusButtons.statusButtonFinished.id
            else -> null
        }

        setOnStatusButtonsClickListener()
        setDatePicker()
        setOnClearButtonClickListener()
    }

    override fun onResume() {
        super.onResume()

        binding.root.requestLayout()
    }

    private fun setOnStatusButtonsClickListener() {
        val buttons = binding.bookStatusButtons.run {
            listOf(
                statusButtonWantToRead,
                statusButtonReading,
                statusButtonFinished
            )
        }
        buttons.forEach { button ->
            button.setOnClickListener {
                selectedStatusButtonId ?: return@setOnClickListener
                if (it.id != selectedStatusButtonId) {
                    val status = getBookStatusByButtonId(it.id)
                    viewModel.updateStatus(status)
                }
            }
        }
    }

    private fun getBookStatusByButtonId(buttonId: Int): Book.Status {
        return when (buttonId) {
            binding.bookStatusButtons.statusButtonWantToRead.id -> Book.Status.WANT_TO_READ
            binding.bookStatusButtons.statusButtonReading.id -> Book.Status.READING
            binding.bookStatusButtons.statusButtonFinished.id -> Book.Status.FINISHED
            else -> Book.Status.WANT_TO_READ
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
        fun getInstance(bookId: String) = BookMemoFragment().apply {
            arguments = Bundle().apply {
                putString(C.BOOK_ID, bookId)
            }
        }
    }
}
