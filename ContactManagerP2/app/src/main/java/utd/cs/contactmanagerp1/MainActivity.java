/*
    Contact Manager App (Phase 1)
    Written by: Ahmad Ghafori

    This activity implements multiple methods and contains a scrolling list
    with the names (Last name first) of each contact. The Action Bar contains a
    "New Contact" button that lets you create a new Contact. When a contact is selected
    from this screen, a new screen comes up.

    Contact Manager App (Phase 2)
    Using Comparable in contacts the objects can be sorted alphabetically.
    Then using sensors the order will change when the phone is shaking.

    Contact Manager App (Phase 3)
    Using database Handler add contacts to the database when import is clicked
    re-initialize the database when reset has been clicked.

    Contact Manager App (Phase 4)
    Using geocoder get lat and long of address entered by contact and display on map
    Get device location and calculate the distance between device and new address
 */

package utd.cs.contactmanagerp1;

import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;

public class MainActivity extends AppCompatActivity {
    //Variables
    ArrayList<Contacts> values = null;
    ArrayList<Contacts> contactList;    // contains a list of all contacts

    //Database initialization
    sqlDatabase dbHelper = new sqlDatabase(this);

    Contacts new_contact = new Contacts(null, null, null, null, null, null, null, null, null, null);
    Contacts edited_contact = new Contacts(null, null, null, null, null, null, null, null, null, null);
    Contacts deleted_contact = new Contacts(null, null, null, null, null, null, null, null, null, null);

    SecondReadWriteFile fileCall = new SecondReadWriteFile();
    SecondReadWriteFile fileEditCall = new SecondReadWriteFile();

    public static final int NEW_CONTACT = 4;
    public static final int EDIT_CONTACT = 2;

    String[] datas = null;

    //Variables for SensorEventListener
    private SensorManager mSensorManager;
    private Sensor mAccelerometer;
    private ShakeDetector mShakeDetector;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        contactList = new ArrayList<>();

        edit_Contact();

        // ShakeDetector initialization
        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        mAccelerometer = mSensorManager
                .getDefaultSensor(Sensor.TYPE_ACCELEROMETER);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    //Menu
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here.
        // under the three dots
        // New Contact
        // Import
        // ReInitialize
        // Settings
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        } else if(id == R.id.action_new_contact){  // if the "New Contact" button is clicked, starts the second activity
            Intent intent = new Intent(this, SecondActivity.class);
            startActivityForResult(intent, NEW_CONTACT);
        } else if(id == R.id.action_import) {

            //add contacts from contactList to database
            for (int i = 0; i < contactList.size(); i++)
            {
                dbHelper.addContacts(contactList.get(i));
            }

        Toast.makeText(this, "Contact List has been imported into the database", Toast.LENGTH_LONG).show();

        } else if(id == R.id.action_reset) {
            //reinitialize the database
            dbHelper.reInit();
            Toast.makeText(this, "Database has been Reset", Toast.LENGTH_LONG).show();
        }

        return super.onOptionsItemSelected(item);
    }


    /*
    Gets data back from the SecondActivity.
    Cases: 1. New Contact
               If new contact was clicked, add the new contact to the list. Write this list
                to the internal storage file. Read the list from the file and pass it to the adapter
                (which displays the list of contacts to the screen.

     */
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode, resultCode, data);

        // RequestCodes indicate how the SecondActivity was opened
        if(requestCode == NEW_CONTACT  && resultCode == RESULT_OK){

            // Get a contact object from SecondActivity using Serializable
            new_contact = (Contacts) data.getExtras().getSerializable("contact");
            contactList.add(new_contact);

            //Sort List alphabetically based on last name when new contacts are saved
            Collections.sort(contactList, Contacts.COMPARE_BY_NAME);
            ///////////////////////Shake Detector/////////////////////
            mShakeDetector = new ShakeDetector();
            mShakeDetector.setOnShakeListener(new ShakeDetector.OnShakeListener() {


                @Override
                public void onShake(int count) {
                    /*
                     * The following method, "handleShakeEvent(count):" is a stub //
                     * method you would use to setup whatever you want done once the
                     * device has been shook.
                     */
                    //Re-sort the list
                    Collections.reverse(contactList);

                    Toast.makeText(MainActivity.this, "List Re-Sorted", Toast.LENGTH_SHORT).show();
                    ListAdapter adapter = new ListAdapter(getBaseContext(), R.id.listAdapter, contactList);
                    ListView lv = findViewById(R.id.listView);
                    lv.setAdapter(adapter);
                }
            });

            fileCall.writeFile(this, "contacts.txt", contactList);
            try{
                values = fileCall.readFile(this, "contacts.txt");

            } catch (IOException e) {
                Log.d("mytag", "Could not begin read file function");
            }

            // Instantiate adapter and connect it with listView.
            ListAdapter adapter = new ListAdapter(this, R.id.listAdapter, values);
            ListView lv = findViewById(R.id.listView);
            lv.setAdapter(adapter);

        }
        else if (requestCode == EDIT_CONTACT && resultCode == RESULT_OK) {
            int position = data.getIntExtra("position", 1); // get index
            edited_contact = (Contacts) data.getExtras().getSerializable("contact"); // get contact object
            contactList.set(position, edited_contact); // .set replaces the old ArrayList item with new value

            fileEditCall.writeFile(this, "contacts.txt", contactList); // write file, pass entire ArrayList of Contacts

            ArrayList<Contacts> newRead = new ArrayList<>();
            try {
                newRead = fileEditCall.readFile(this, "contacts.txt"); // read file
            } catch (IOException e) {
                Log.d("mytag", "Could not begin read file function");
            }

            ListAdapter adapter = new ListAdapter(this, R.id.listAdapter, newRead);
            ListView lv = findViewById(R.id.listView);
            lv.setAdapter(adapter);
        }
        else if(requestCode == EDIT_CONTACT && resultCode == RESULT_FIRST_USER){
            int position = data.getIntExtra("position", 1); // index
            deleted_contact = (Contacts) data.getExtras().getSerializable("delete_contact"); // get contact to be deleted
            contactList.remove(position);   // .remove removes the ArrayList item as specified index

            fileEditCall.writeFile(this, "contacts.txt", contactList); // write file

            ArrayList<Contacts> newRead = new ArrayList<>();
            try {
                newRead = fileEditCall.readFile(this, "contacts.txt");  // read file
            } catch (IOException e) {
                Log.d("mytag", "Could not begin read file function");
            }

            ListAdapter adapter = new ListAdapter(this, R.id.listAdapter, newRead);
            ListView lv = findViewById(R.id.listView);
            lv.setAdapter(adapter);
        }
    }

    @Override
    public void startActivityForResult(Intent intent, int requestCode){
        intent.putExtra("requestCode", requestCode);
        super.startActivityForResult(intent, requestCode);
    }

    /*
    Method called when a listview item is clicked.
    Sets the onclick listeners for each item in list. Gets the index of the item that was clicked.
    Opens the SecondActivity passing the contact that may be modified and the position of it
     in the listview.
     */
    public void edit_Contact() {
        ListView list = (ListView) findViewById(R.id.listView);
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Contacts edit_contact = (Contacts) parent.getAdapter().getItem(position);
                Intent intent = new Intent(MainActivity.this, SecondActivity.class);
                intent.putExtra("edit_contact", edit_contact);
                intent.putExtra("position", position);
                startActivityForResult(intent, EDIT_CONTACT);

            }
        });

    }

    /////////////////////////Sensor Methods///////////////////////////////////////
    @Override
    public void onResume() {
        super.onResume();
        // Add the following line to register the Session Manager Listener onResume
        mSensorManager.registerListener(mShakeDetector, mAccelerometer,	SensorManager.SENSOR_DELAY_UI);
    }

    @Override
    public void onPause() {
        // Add the following line to unregister the Sensor Manager onPause
        mSensorManager.unregisterListener(mShakeDetector);
        super.onPause();
    }
    ///////////////////////////////////////////////////////////////////////////////
    // Import Contacts
    public void importContacts(Contacts contacts) {
        dbHelper.addContacts(contacts);

    }

    /*
    Adapter class for the ListView to display the array in the listView
    Taken from Professor John Cole's website.
     */
    public class ListAdapter extends ArrayAdapter<Contacts> {
        ArrayList<Contacts> values;
        Contacts contact = null;

        @Override
        public Contacts getItem(int position) {
            return this.values.get(position);
        }

        public ListAdapter(Context context, int textViewResourceId, ArrayList<Contacts> objects) {
            super(context, textViewResourceId, objects);
            values = objects;

        }

        // Creates the view for each list item.
        @Override
        public View getView(int position, View cvtView, ViewGroup parent) {
            int width = parent.getWidth();
            Context cx = this.getContext();
            LayoutInflater inflater = (LayoutInflater) cx.getSystemService(LAYOUT_INFLATER_SERVICE);  // inflate the layout
            View rowView = inflater.inflate(R.layout.listview_adapter, parent, false);
            TextView tvDetails = rowView.findViewById(R.id.Details);

            /* Set each Textview with appropriate row data and width */
            contact = values.get(position);
            datas = contact.toString().split("\t"); // tab as delimiter

            tvDetails.setWidth((width));
            tvDetails.setText(datas[1] + " " + datas[0]);

            return rowView;
        }
    }
}
