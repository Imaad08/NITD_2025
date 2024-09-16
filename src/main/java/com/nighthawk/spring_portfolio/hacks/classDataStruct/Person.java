package com.nighthawk.spring_portfolio.hacks.classDataStruct;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.Period;
import java.time.ZoneId;
import java.util.Date;


/*
Adapted from Person POJO, Plain Old Java Object.
 */
public class Person extends Generics{
    // Class data
    private static String classType = "Person";
    public static KeyTypes key = KeyType.title;  // static initializer
	public static void setOrder(KeyTypes key) {Person.key = key;}
	public enum KeyType implements KeyTypes {title, uid, name, dob, age}

    // Instance data
    private String uid;  // user / person id
    private String password;
    private String name;
    private Date dob;
    

    // Constructor with zero arguments
    public Person() {
        super.setType(classType);
    }

    // Constructor used when building object from an API
    public Person(String uid, String password, String name, Date dob) {
        this();  // runs zero argument constructor
        this.uid = uid;
        this.password = password;
        this.name = name;
        this.dob = dob;
    }

    /* 'Generics' requires getKey to help enforce KeyTypes usage */
	@Override
	protected KeyTypes getKey() { return Person.key; }

    public String getUserID() {
        return uid;
    }

    /* 'Generics' requires toString override
	 * toString provides data based off of Static Key setting
	 */
	@Override
	public String toString() {		
		String output="";
		if (KeyType.uid.equals(this.getKey())) {
			output += this.uid;
		} else if (KeyType.name.equals(this.getKey())) {
			output += this.name;
		} else if (KeyType.age.equals(this.getKey())) {
			output += "0000" + this.getAge();  // pads integer 1,100,11,2 to 0001,0100,0011,0002
			output = output.substring(output.length() - 4);
		} else {
			output = super.getType() + ": " + this.uid + ", " + this.name + ", " + this.getAge();
		}
		return output;
	}

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Date getDob() {
        return dob;
    }

    public void setDob(Date dob) {
        this.dob = dob;
    }

    // A custom getter to return age from dob attribute
    public int getAge() {
        if (this.dob != null) {
            LocalDate birthDay = this.dob.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
            return Period.between(birthDay, LocalDate.now()).getYears(); }
        return -1;
    }

    // Initialize static test data 
    public static Person[] init() {

        // basics of class construction
        Person p1 = new Person();
        p1.setName("Thomas Edison");
        p1.setUid("toby@gmail.com");
        p1.setPassword("123Toby!");
        // adding Note to notes collection
        try {  // All data that converts formats could fail
            Date d = new SimpleDateFormat("MM-dd-yyyy").parse("01-01-1840");
            p1.setDob(d);
        } catch (Exception e) {
            // no actions as dob default is good enough
        }

        Person p2 = new Person();
        p2.setName("Alexander Graham Bell");
        p2.setUid("lexb@gmail.com");
        p2.setPassword("123LexB!");
        try {
            Date d = new SimpleDateFormat("MM-dd-yyyy").parse("01-01-1845");
            p2.setDob(d);
        } catch (Exception e) {
        }

        Person p3 = new Person();
        p3.setName("Nikola Tesla");
        p3.setUid("niko@gmail.com");
        p3.setPassword("123Niko!");
        try {
            Date d = new SimpleDateFormat("MM-dd-yyyy").parse("01-01-1850");
            p3.setDob(d);
        } catch (Exception e) {
        }

        Person p4 = null;
        Person p5 = null;
        try {
            p4 = new Person(
                "madam@gmail.com",
                "123Madam!",
                "Madam Currie", 
                new SimpleDateFormat("MM-dd-yyyy").parse("01-01-2023")
            );
    
            p5 = new Person(
                "jm1021@gmail.com", 
                "123Qwerty!",
                "John Mortensen",
                new SimpleDateFormat("MM-dd-yyyy").parse("10-21-1959")
            );
        } catch (Exception e) {
        }

        // Array definition and data initialization
        Person persons[] = {p1, p2, p3, p4, p5};
        return(persons);
    }

    public static void main(String[] args) {
        // obtain Person from initializer
        Person persons[] = init();
        Person.setOrder(Person.KeyType.title);

        // iterate using "enhanced for loop"
        for( Person person : persons ) {
            System.out.println(person);  // print object
        }
    }

}