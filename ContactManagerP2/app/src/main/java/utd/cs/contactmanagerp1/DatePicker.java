/*
    Contact Manager App (Phase 1)
    Written by: Fabliha Hassan & Ahmad Ghafori
    This is the DatePicker Fragment that brings up a calendar from which a date can be chosen.
 */

package utd.cs.contactmanagerp1;

import android.app.DatePickerDialog;
import android.app.DatePickerDialog.OnDateSetListener;
import android.app.Dialog;
import android.app.FragmentManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.app.Activity;

import java.util.Calendar;

public class DatePicker extends DialogFragment{
    OnDateSetListener onDateSetListener;       // listener for when a date is chosen
    int year, month, day;

    public DatePicker(){
        // required empty constructor
    }

    // Returns the date back to the textview date fragments | Written by: Fabliha Hassan
    public void setCallBack(OnDateSetListener ondate){
        onDateSetListener = ondate;
    }

    /*
    Sets the date fields | Written by: Fabliha Hassan
     */
    @Override
    public void setArguments(@Nullable Bundle args) {
        super.setArguments(args);
        year = args.getInt("year");
        month = args.getInt("month");
        day = args.getInt("day");
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        return new DatePickerDialog(getActivity(), onDateSetListener, year, month, day);
    }

}
