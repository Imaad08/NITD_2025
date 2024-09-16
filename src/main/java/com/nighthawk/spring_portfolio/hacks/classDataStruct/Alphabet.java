package com.nighthawk.spring_portfolio.hacks.classDataStruct;

public class Alphabet extends Generics {
	// Class data
	public static KeyTypes key = KeyType.title;  // static initializer
	public static void setOrder(KeyTypes key) {Alphabet.key = key;}
	public enum KeyType implements KeyTypes {title, letter}
	private static final int size = 26;  // constant used in data initialization

	// Instance data
	private final char letter;
	
	/*
	 * single letter object
	 */
	public Alphabet(char letter)
	{
		super.setType("Alphabet");
		this.letter = letter;
	}

	/* 'Generics' requires getKey to help enforce KeyTypes usage */
	@Override
	protected KeyTypes getKey() { return Alphabet.key; }

	/* 'Generics' requires toString override
	 * toString provides data based off of Static Key setting
	 */
	@Override
	public String toString()
	{
		String output="";
		if (KeyType.letter.equals(this.getKey())) {
			output += this.letter;
		} else {
			output += super.getType() + ": " + this.letter;
		}
		return output;
	}

	// Test data initializer for upper case Alphabet
	public static Alphabet[] alphabetData()
	{
		Alphabet[] alphabet = new Alphabet[Alphabet.size];
		for (int i = 0; i < Alphabet.size; i++)
		{
			alphabet[i] = new Alphabet( (char)('A' + i) );
		} 	
		return alphabet;
	}
	
	/* 
	 * main to test Animal class
	 */
	public static void main(String[] args)
	{
		// Inheritance Hierarchy
		Alphabet[] objs = alphabetData();

		// print with title
		Alphabet.setOrder(KeyType.title);
		Alphabet.print(objs);

		// print letter only
		Alphabet.setOrder(KeyType.letter);
		Alphabet.print(objs);
	}
	
}
		
	
	