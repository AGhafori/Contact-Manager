/*
    Contact Manager App (Phase 1)
    Written by: Ahmad Ghafori
    This class handles the File I/O.
 */

package utd.cs.contactmanagerp1;

import android.content.Context;
import android.os.Environment;
import android.support.design.widget.Snackbar;
import android.util.Log;
import android.widget.ArrayAdapter;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Scanner;

public class SecondReadWriteFile {
    String line;
    public File file;
    ArrayList<Contacts> ContactList;

    /*
    Method to write to the file. Creates Contacts objects from lines read and returns
    the ArrayList of Contacts.
    Written by: Ahmad Ghafori
     */
    public void writeFile(Context context, String fileName, ArrayList<Contacts> list){

        String contact_str;
        file = new File(context.getFilesDir(),"mydir");     // opens the file
        if(!file.exists()){
            file.mkdir();
        }

        try{
            File gpxfile = new File(file, fileName);
            FileWriter writer = new FileWriter(gpxfile);

            /* Iterates through the ArrayList of Contacts and writes each string to the file */
            for(int i = 0; i < list.size(); i++){
                contact_str = list.get(i).toString();
                writer.write(contact_str);
            }

            writer.flush();
            writer.close();

        }catch (Exception e){
            e.printStackTrace();
            Log.d("mytag", "Could not create the file");
        }
    }


    /*
    Method to read the file.
    Written by: Ahmad Ghafori
     */
    public ArrayList<Contacts> readFile(Context context, String filename) throws IOException {
        try {
            String folder = context.getFilesDir().getAbsolutePath() + File.separator + "mydir" + File.separator + filename;
            File FileForIO = new File(folder);

            InputStream is = new FileInputStream(FileForIO);
            BufferedReader reader = new BufferedReader(new InputStreamReader(is));

           ContactList = new ArrayList<>();

           /*
           Reads each line of the file and creates a contact object from it. Adds Contacts to
           ArrayList<Contacts> and return it to MainActivity.
            */
           while ((line = reader.readLine()) != null){
               String[] values = line.split("\t"); // tab delimiter

               Contacts contact = new Contacts(values[0], values[1], values[2], values[3], values[4], values[5], values[6], values[7], values[8], values[9]);
               ContactList.add(contact);
            }


            return ContactList;

        } catch (FileNotFoundException e) {
            Log.e("ERROR", e.toString());
        } catch (IOException e) {
            e.printStackTrace();
            Log.e("mytag", "Could not read the file.");
        }
        return ContactList;
    }
}


