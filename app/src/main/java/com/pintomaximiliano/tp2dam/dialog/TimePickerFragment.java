package com.pintomaximiliano.tp2dam.dialog;

import android.app.Dialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;

import java.util.Calendar;

public class TimePickerFragment extends DialogFragment {

    private TimePickerDialog.OnTimeSetListener listener;

    public static TimePickerFragment newInstance(TimePickerDialog.OnTimeSetListener listener){
        TimePickerFragment f = new TimePickerFragment();
        f.setListener(listener);
        return f;
    }

    private void setListener(TimePickerDialog.OnTimeSetListener listener){
        this.listener = listener;
    }

    @Override
    @NonNull
    public Dialog onCreateDialog(Bundle savedInstanceState){
        final Calendar c = Calendar.getInstance();
        int hours = c.get(Calendar.HOUR_OF_DAY);
        int minutes = c.get(Calendar.MINUTE);

        return new TimePickerDialog(getActivity(), listener, hours, minutes, true);
    }
}
