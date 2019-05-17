/*
    Contact Manager App (Phase 1)
    Written by: Ahmad Ghafori
    This is the Contacts object class
 */

package utd.cs.contactmanagerp1;


import java.io.Serializable;
import java.util.Comparator;

public class Contacts implements Serializable, Comparable<Contacts> {
    public String _id, firstname, lastname, phone, birthdate, firstmeeting,
            address_one, address_two, city, zip, state;
    Contacts other;

    // Constructor | Written by: Ahmad Ghafori
    public Contacts ( String first, String last, String number, String dob, String date, String addyOne, String addyTwo, String CITY, String ZIP, String STATE){
        //_id = id;
        firstname = first;
        lastname = last;
        phone = number;
        birthdate = dob;
        firstmeeting = date;
        address_one = addyOne;
        address_two = addyTwo;
        city = CITY;
        zip = ZIP;
        state = STATE;

    }
    //Sorting method: alphabetically
    public static Comparator<Contacts> COMPARE_BY_NAME = new Comparator<Contacts>() {
        public int compare(Contacts one, Contacts other) {
            return one.lastname.compareTo(other.lastname);
        }
    };

    /* overrode toString() method. Each contact consists of a line
    with fields separated by tab characters | Written by: Ahmad Ghafori
     */
    public String toString(){
        return firstname + "\t" + lastname + "\t" + phone + "\t" + birthdate + "\t" + firstmeeting + "\t"
                + address_one + "\t" + address_two + "\t" + city + "\t" + zip + "\t" + state + "\n";
    }

    // getters & setters for each field | Written by: Fabliha Hassan & Ahmad Ghafori
    //public String getId() { return _id; }

    //public void setId(String id) { this._id = id;}
    // First Name
    public String getFirstname() {
        return firstname;
    }

    public void setFirstname(String firstname) {
        this.firstname = firstname;
    }

    //Last Name
    public String getLastname() {
        return lastname;
    }

    public void setLastname(String lastname) {
        this.lastname = lastname;
    }

    // Phone Number
    public String getPhone() { return phone; }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    //Date of Birth
    public String getBirthdate() {
        return birthdate;
    }

    public void setBirthdate(String birthdate) {
        this.birthdate = birthdate;
    }

    //Date of meeting
    public String getFirstmeeting() {
        return firstmeeting;
    }

    public void setFirstmeeting(String firstmeeting) {
        this.firstmeeting = firstmeeting;
    }

    //Address line one
    public String getAddyOne() {
        return address_one;
    }

    public void setAddyOne(String address_one) {
        this.address_one = address_one;
    }

    //address line two
    public String getAddyTwo() {
        return address_two;
    }

    public void setAddyTwo(String address_two) {
        this.address_two = address_two;
    }

    //City
    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    //zip code
    public String getZip() {
        return zip;
    }

    public void setZip(String zip) {
        this.zip = zip;
    }

    //state
    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    @Override
    public int compareTo(Contacts o) {
        return lastname.compareTo(other.lastname);
    }
}
