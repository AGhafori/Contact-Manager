/*
Contact Manager App (Phase 1)
Written by: Fabliha Hassan & Ahmad Ghafori
This is the birthday date field implemented as a fragment.
 */

package utd.cs.contactmanagerp1;

import android.app.DatePickerDialog;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.Calendar;


public class BirthdayFragment extends Fragment{
    OnFragmentInteractionListener mListener;    // interface to pass data from fragment to container activity
    public static String date;
    TextView tv;

    // Container Activity must implement this interface | Written by: Fabliha Hassan
    public interface OnFragmentInteractionListener{
        void returnDateSelected(String date);
    }

    // Makes sure container activity implements the interface | Written by: Fabliha Hassan
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        if(context instanceof  OnFragmentInteractionListener){
            mListener = (OnFragmentInteractionListener) context;
        }
        else{
            throw new ClassCastException(context.toString() + "Must implement OnFragmentInteractionListener");

        }
    }

    public BirthdayFragment(){
        // empty constructor
    }

    // Just inflates the layout onCreate | Written by: Fabliha Hassan
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_birthday, container, false);
        return view;
    }

    /* This method sets the text of the date textviews with existing values when a list item is clicked.
    written by: Fabliha Hassan
    */
    public void changeText(String text){
        View v = getView();
        tv = v.findViewById(R.id.first_date_textview);
        tv.setText(text);
    }

    /* sets a click listener to the textview and calls method to open date picker fragment
    written by: Fabliha Hassan
    */
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        tv = (TextView) view.findViewById(R.id.first_date_textview);
        mListener.returnDateSelected(date);  // pass data to activity
        tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDatePicker();
            }
        });


    }

    /* opens the date picker fragment showing current date on start. Calls method that passes back the
     new selected date. Written by: Fabliha Hassan */
    private void showDatePicker(){
        DatePicker date = new DatePicker();

        Calendar calendar = Calendar.getInstance();
        Bundle args = new Bundle();
        args.putInt("year", calendar.get(Calendar.YEAR));
        args.putInt("month", calendar.get(Calendar.MONTH));
        args.putInt("day", calendar.get(Calendar.DAY_OF_MONTH));
        date.setArguments(args);

        date.setCallBack(ondate);
        date.show(getFragmentManager(), "Date Picker");
    }

    /* Gets the selected date back here and sets the textview with new date. Passes the date back to
        the SecondActivity. Written by: Fabliha Hassan
     */
    DatePickerDialog.OnDateSetListener ondate = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(android.widget.DatePicker view, int year, int month, int dayOfMonth) {
            View v = getView();
            tv = v.findViewById(R.id.first_date_textview);
            tv.setText((month + 1) + "/" + dayOfMonth + "/" + year);
            date = (month + 1) + "/" + dayOfMonth + "/" + year;
            mListener.returnDateSelected(date);  // pass data to activity
        }
    };
}
