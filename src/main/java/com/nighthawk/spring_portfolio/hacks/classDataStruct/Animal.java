package com.nighthawk.spring_portfolio.hacks.classDataStruct;

/*
 * Animal class extends Generics and defines abstract methods
 */
public class Animal extends Generics {
	// Class data
	public static KeyTypes key = KeyType.title;  // static initializer
	public static void setOrder(KeyTypes key) { Animal.key = key; }
	public enum KeyType implements KeyTypes {title, name, age, color}

	// Instance data
	private final String name;
	private final int age;
	private final String color;

	/* constructor
	 *
	 */
	public Animal(String name, int age, String color)
	{
		super.setType("Animal");
		this.name = name;
		this.age = age;
		this.color = color;
	}

	/* 'Generics' requires getKey to help enforce KeyTypes usage */
	@Override
	protected KeyTypes getKey() { return Animal.key; }
	
	/* 'Generics' requires toString override
	 * toString provides data based off of Static Key setting
	 */
	@Override
	public String toString()
	{
		String output="";
		if (KeyType.name.equals(this.getKey())) {
			output += this.name;
		} else if (KeyType.age.equals(this.getKey())) {
			output += "00" + this.age;
			output = output.substring(output.length() - 2);
		} else if (KeyType.color.equals(this.getKey())) {
			output += this.color;
		} else {
			output += super.getType() + ": " + this.name + ", " + this.color + ", " + this.age;
		}
		return output;
		
	}

	// Test data initializer
	public static Animal[] animalData() {
		return new Animal[]{
				new Animal("Lion", 8, "Gold"),
				new Animal("Pig", 3, "Pink"),
				new Animal("Robin", 7, "Red"),
				new Animal("Cat", 10, "Black"),
				new Animal("Kitty", 1, "Calico"),
				new Animal("Dog", 14, "Brown")
		};
	}
	
	/* main to test Animal class
	 * 
	 */
	public static void main(String[] args)
	{
		// Inheritance Hierarchy
		Animal[] objs = animalData();

		// print with title
		Animal.setOrder(KeyType.title);
		Animal.print(objs);

		// print name only
		Animal.setOrder(KeyType.name);
		Animal.print(objs);
	}

}
