/*
    Contact Manager App (Phase 1)
    Written by:  Ahmad Ghafori
    This Activity shows contact detail when a contact is clicked in the first screen.
    Shows the first name, last name, phone number, birthday, date of first contact.
    names and phone are EditTexts and dates are fragments ( all are modifiable).
    Two buttons Save and Deletes lets you Save, Edit and Delete a contact.
 */
package utd.cs.contactmanagerp1;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;


public class SecondActivity extends AppCompatActivity implements BirthdayFragment.OnFragmentInteractionListener, FirstMetFragment.OnFragmentSelectedListener {
    String id, fn, ln, phn, add_1, add_2, ct, zp, st, distString, latlngString;
    EditText firstname, lastname, phone_number, address_lineOne, address_lineTwo, cityName, zipCode, State;
    TextView result, latlngView;
    String birthdate = "";
    String firstMet = "";
    String address = "";
    Button button;
    Contacts map;

    int position = 0;

    /*
    This method checks if the intent was from Edit Contact. If so, it populates the views with the
    contact's information and handles a situation where the delete button is clicked.

     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_second);
        Toolbar toolbar = (android.support.v7.widget.Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        id = String.valueOf(position);
        firstname = (EditText)findViewById(R.id.first_name);
        lastname = (EditText) findViewById(R.id.last_name);
        phone_number = (EditText)findViewById(R.id.phone_number);
        address_lineOne = (EditText) findViewById(R.id.address_firstLine);
        address_lineTwo = (EditText) findViewById(R.id.address_secondLine);
        zipCode = (EditText) findViewById(R.id.zip);
        cityName = (EditText) findViewById(R.id.city);
        State = (EditText) findViewById(R.id.state);
        result = (TextView) findViewById(R.id.resultView);
        latlngView = (TextView) findViewById(R.id.latlngView);




        final Intent intent = getIntent();
        position = intent.getIntExtra("position", 1);  // get the index
        int request_code = intent.getIntExtra("requestCode", 0); // get the request code

        // If request code is from "Edit Contact"
        if(request_code == 2) {

            FirstMetFragment fm_fragment = (FirstMetFragment) getSupportFragmentManager().findFragmentById(R.id.first_date_fragment);
            BirthdayFragment bd_fragment = (BirthdayFragment) getSupportFragmentManager().findFragmentById(R.id.dob_fragment);

            //get the Contact object
            final Contacts edit = (Contacts) intent.getSerializableExtra("edit_contact");

            // get Contact's fields and set them back to EditText and fragment fields.
            String fname = edit.getFirstname();
            String lname = edit.getLastname();
            String phone = edit.getPhone();
            String dob = edit.getBirthdate();
            String date = edit.getFirstmeeting();
            String addyOne = edit.getAddyOne();
            String addyTwo = edit.getAddyTwo();
            String editedCity = edit.getCity();
            String zipcode = edit.getZip();
            String state = edit.getState();

            //update new values
            firstname.setText(fname);
            lastname.setText(lname);
            phone_number.setText(phone);
            fm_fragment.changeText(date);
            bd_fragment.changeText(dob);
            address_lineTwo.setText(addyOne);
            address_lineOne.setText(addyTwo);
            cityName.setText(editedCity);
            zipCode.setText(zipcode);
            State.setText(state);
            mapActivity map = new mapActivity();
            String dist = map.result;

            result.setText(dist);

            // onClickListener for delete button. Passes the contact to be deleted and its position to the Main screen
            button = findViewById(R.id.delete); //instantiate delete button
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    intent.putExtra("position", position);
                    intent.putExtra("delete_contact", edit);
                    setResult(RESULT_FIRST_USER, intent);
                    finish();
                }
            });
        }
    }

    ///////////////////////////////Menu Options///////////////////////////
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_second, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        //map address
        if (id == R.id.action_mapAddy) {
            //get contact values
            //save EditText fields to string
            fn = firstname.getText().toString();
            ln = lastname.getText().toString();
            phn = phone_number.getText().toString();
            add_1 = address_lineOne.getText().toString();
            add_2 = address_lineTwo.getText().toString();
            ct = cityName.getText().toString();
            zp = zipCode.getText().toString();
            st = State.getText().toString();

            address = add_1 + "," + add_2 + "," + ct + "," + st + "," + zp;

            //start new intent to send address and receive Lat and Lng
            Intent mapIntent = new Intent(this, mapActivity.class);

            mapIntent.putExtra("address", address);

            startActivityForResult(mapIntent, 5);
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void startActivityForResult(Intent intent, int requestCode){
        intent.putExtra("requestCode", requestCode);
        super.startActivityForResult(intent, requestCode);
    }

    //receive distance in miles
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        double distance = 0.0;
        double lat = 0.0, lng = 0.0;
        if (requestCode == 5 && resultCode == RESULT_OK) {
            distance = (double) data.getDoubleExtra("dist", 0);
            lat = data.getDoubleExtra("lat", 0);
            lng = data.getDoubleExtra("lng", 0);
        }
        String latlng = "Latitude: " + String.valueOf(lat) + ", Longitude: " + String.valueOf(lng);
        String dist = String.valueOf(distance);
        result.setText(" " + dist + " miles");
        latlngView.setText(" " + latlng);
    }

    //gets the birthdate from the fragment.
    @Override
    public void returnDateSelected(String date) {
        birthdate = date;
    }

    /*
    gets the date of first contact from the fragment.
     */
    @Override
    public void returnFirstMetDate(String date) {
        firstMet = date;
    }


    /*
    When save button is clicked, gets the contact details from the fields, creates a Contact
    object using the values and passes the object back to the MainActivity.
     */
    public void saveClick(View view) {
        Intent intent = new Intent();

        fn = firstname.getText().toString();
        ln = lastname.getText().toString();
        phn = phone_number.getText().toString();
        add_1 = address_lineOne.getText().toString();
        add_2 = address_lineTwo.getText().toString();
        ct = cityName.getText().toString();
        zp = zipCode.getText().toString();
        st = State.getText().toString();



        //add to contacts
        Contacts item = new Contacts(fn, ln, phn, birthdate, firstMet, add_1, add_2, ct, zp, st);

        //send back the intent to the main activity
        intent.putExtra("position", position);
        intent.putExtra("contact", item);
        setResult(RESULT_OK, intent);

        finish();
    }
}
































