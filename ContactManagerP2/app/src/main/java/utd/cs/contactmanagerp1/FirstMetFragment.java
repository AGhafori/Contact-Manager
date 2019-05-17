/*
Contact Manager App (Phase 1)
This is the date of first contact field fragment
 */

package utd.cs.contactmanagerp1;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.Calendar;

public class FirstMetFragment extends Fragment {
    OnFragmentSelectedListener mListener;  // interface to pass data from fragment to container activity


    // Gets current date for default date and sets the textview with it.
    static Calendar c = Calendar.getInstance();
    static int default_year = c.get(Calendar.YEAR);
    static int default_month = c.get(Calendar.MONTH);
    static int default_day = c.get(Calendar.DAY_OF_MONTH);
    public static String date = (default_month + 1) + "/" + default_day + "/" + default_year;

    TextView textView;

    // Container Activity must implement this interface | Written by: Fabliha Hassan
    public interface OnFragmentSelectedListener{
        void returnFirstMetDate(String date);
    }
    public FirstMetFragment() {
        // Required empty public constructor
    }

    /* This method sets the text of the date textviews with existing values when a list item
    is clicked
     */
    public void changeText(String text){
        View v = getView();
        textView = (TextView) v.findViewById(R.id.second_date_textview);
        textView.setText(text);
    }

    // Makes sure container activity implements the interface
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        if(context instanceof OnFragmentSelectedListener){
            mListener = (OnFragmentSelectedListener) context;
        }
        else{
            throw new ClassCastException(context.toString() + "Must implement OnFragmentSelectedListener");

        }
    }

    // Just inflates the layout onCreate
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_first_met, container, false);
        return view;
    }

    /* sets a click listener to the textview and calls method to open date picker fragment
    */
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        textView = (TextView) view.findViewById(R.id.second_date_textview);
        textView.setText(date);

        mListener.returnFirstMetDate(date);
        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDatePicker();
            }
        });
    }

    /* opens the date picker fragment showing current date on start. Calls method that passes back the
     new selected date.*/
    private void showDatePicker(){
        DatePicker date = new DatePicker();

        /* Shows current date when begins */
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
       the SecondActivity.
    */
    DatePickerDialog.OnDateSetListener ondate = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(android.widget.DatePicker view, int year, int month, int dayOfMonth) {
            View v = getView();
            textView = v.findViewById(R.id.second_date_textview);
            textView.setText((month + 1) + "/" + dayOfMonth + "/" + year);

            date = (month + 1) + "/" + dayOfMonth + "/" + year;
            mListener.returnFirstMetDate(date);
        }
    };
}
