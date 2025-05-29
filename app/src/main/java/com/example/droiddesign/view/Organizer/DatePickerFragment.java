package com.example.droiddesign.view.Organizer;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.widget.DatePicker;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;

import java.util.Calendar;
import java.util.Objects;


/**
 * The DatePickerFragment class provides a date picker dialog allowing users to select a date.
 * It communicates the selected date back to the host activity using the DatePickerListener interface.
 */
public class DatePickerFragment extends DialogFragment
                    implements DatePickerDialog.OnDateSetListener {

    /**
     * The DatePickerFragment class provides a date picker dialog allowing users to select a date.
     * It communicates the selected date back to the host activity using the DatePickerListener interface.
     */
    public interface DatePickerListener {

        /**
         * Called when a date is set in the picker.
         *
         * @param year  The year that was set.
         * @param month The month that was set (0-11 for compatibility with {@link Calendar}).
         * @param day   The day of the month that was set.
         */
        void onDateSet(int year, int month, int day);
    }


    /**
     * Listener to communicate with the host activity.
     */
    DatePickerListener listener;

    /**
     * Called when the fragment is first attached to its context.
     * Confirms that the host context implements the required DatePickerListener interface.
     *
     * @param context The context attaching the fragment.
     */

    /**
     * Called when the fragment is first attached to its context.
     * Confirms that the host context implements the required DatePickerListener interface.
     *
     * @param context The context attaching the fragment.
     */
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            listener = (DatePickerListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() + " must implement DatePickerListener");
        }
    }


    /**
     * Creates a new instance of DatePickerDialog with the current date selected by default.
     *
     * @param savedInstanceState If the fragment is being re-created from a previous saved state, this is the state.
     * @return A new instance of DatePickerDialog.
     */
    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        return new DatePickerDialog(requireActivity(), this, year, month, day);
    }

    /**
     * Passes the date selected in the picker to the host activity through the DatePickerListener interface.
     *
     * @param view  The view associated with this listener.
     * @param year  The year that was chosen.
     * @param month The month that was chosen.
     * @param day   The day of the month that was chosen.
     */
    @Override
    public void onDateSet(DatePicker view, int year, int month, int day) {
        listener.onDateSet(year, month, day);
    }
}
