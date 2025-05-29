package com.example.droiddesign.view.Organizer;

import android.app.Dialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.widget.TimePicker;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;

import com.example.droiddesign.view.Organizer.AddEventActivity;
import java.util.Calendar;

/**
 * Fragment class that presents a time picker dialog to the user.
 * It allows users to select a specific time and communicates that selection back to the hosting activity.
 */
public class TimePickerFragment extends DialogFragment
                    implements TimePickerDialog.OnTimeSetListener {

    /**
     * Creates and returns an instance of TimePickerDialog with the current time set as default.
     * @param savedInstanceState If the fragment is being re-created from a previous saved state, this is the state.
     * @return A new instance of TimePickerDialog.
     */
    @NonNull
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final Calendar calendar = Calendar.getInstance();
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);

        return new TimePickerDialog(getActivity(), this, hour, minute, true);
    }

    /**
     * Callback for when the user sets a time in the TimePicker.
     * Delegates the action to the activity, passing the hour and minute set by the user.
     * @param view The view associated with this listener.
     * @param hourOfDay The hour that was set.
     * @param minute The minute that was set.
     */
    @Override
    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
        ((AddEventActivity) requireActivity()).onTimeSet(getTag(), hourOfDay, minute);
    }
}
