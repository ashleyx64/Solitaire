/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package solitaire;

/**
 *
 * @author Ashley
 */
public class Card {
    
    private final String suit, value, colour;
    private final char suitChar, valueChar;
    private boolean hidden;
    private static final String ANSI_RESET = "\u001B[0m",
                                ANSI_BLACK = "\u001B[30m",
                                ANSI_RED = "\u001B[31m",
                                hiddenChar = "##";
    
    public Card(String consSuit, String consValue, boolean consHidden) {
        suit = consSuit;
        value = consValue;
        hidden = consHidden;
        
        suitChar = genSuitChar();
        valueChar = genValueChar();
        colour = genColour();
    }
    
    private String genColour() {
        if (suit.equals("spades") || suit.equals("clubs")) {
            return "black";
        } else {
            return "red";
        }
    }

    private char genSuitChar() {
        switch (suit) {
            case "spades":
                return 'S';
            case "hearts":
                return 'H';
            case "diamonds":
                return 'D';
            case "clubs":
                return 'C';
            default:
                System.out.println("ERROR: Suit not recognised");                
                return '?';
        }
    }

    private char genValueChar() {
        switch (value) {
            case "ace":
                return 'A';
            case "jack":
                return 'J';
            case "queen":
                return 'Q';
            case "king":
                return 'K';
            case "2":
            case "3":
            case "4":
            case "5":
            case "6":
            case "7":
            case "8":
            case "9":
            case "10":
                return value.charAt(0);
            default:
                System.out.println("ERROR: Value not recognised");                  
                return '?';            
        }
    }
    
    public String getSuit() {
        return suit;
    }
    
    public String getValue() {
        return value;
    }
    
    public String getColour() {
        return colour;
    }
    
    public String getSymbol() {
        if(hidden) {
            return hiddenChar;
        } else {
            if (colour.equals("black")) {
                return ANSI_BLACK + valueChar + suitChar + ANSI_RESET;
            } else {
                return ANSI_RED + valueChar + suitChar + ANSI_RESET;
            }
        }
    }
    
    public void setHidden(boolean cond) {
        hidden = cond;
    }
    
    public boolean getHidden() {
        return hidden;
    }
}
