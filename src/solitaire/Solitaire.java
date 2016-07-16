/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package solitaire;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.Scanner;

/**
 *
 * @author Ashley
 */
public class Solitaire {
    
    private static final Scanner sc = new Scanner(System.in);
    private static final Random rand = new Random();   
    private static final String[] suits = {"spades", "hearts", "diamonds", "clubs"};
    private static final String[] values = {"ace", "2", "3", "4", "5", "6", "7", "8", "9", "10", "jack", "queen", "king"};
    private static final Card[] pack = new Card[52];
    private static final List<Card> hand = new ArrayList<>(),
                                    waste = new ArrayList<>();
    private static final List<List<Card>>   tab = new ArrayList<>(),
                                            found = new ArrayList<>();
    

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        fillPack();
        printPack();
        
        shufflePack();
        printPack();
        
        layTable();
        
        do {
            
            printTable();
            
            //Query the player
            boolean inputRecognised;
            do {
                inputRecognised = true;
                System.out.println("Options:");
                System.out.println("- Enter 'turn' to turn over the hand");
                System.out.println("- Enter 'move #1 #2 #3' where #1 refers to the column/area you wish to move cards from, #2 refers to the number of cards you wish to move and #3 refers to the column/area you want to move them to");
                System.out.println("(Use 8 to refer to the waste and 9 through 12 to refer to the foundations)");
                String[] input = sc.nextLine().split(" ");
                switch (input[0]) {
                    case "turn":
                        turnHand();
                        break;
                    case "move":
                        if (input.length != 4) {
                            System.out.println("You haven't entered the correct number of arguments");
                            inputRecognised = false;
                        } else {
                            int[] inputNum = new int[3];
                            for (int i = 0; i < 3; i++) {
                                inputNum[i] = Integer.parseInt(input[i + 1]);
                            }
//                            moveCardStack(inputNum[0], inputNum[1], inputNum[2]);
                        }
                        break;
                    default:
                        System.out.println("I don't recognise what you've entered");
                        inputRecognised = false;
                }
            } while (!inputRecognised);
            
            
        } while (!gameWon());
    }

    private static void printPack() {
        for (int i = 0; i < 52; i++) {
//            System.out.println("Printing symbol of card at index " + i);
            System.out.println(pack[i].getSymbol());
        }
        System.out.println();
    }

    private static void fillPack() {
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 13; j++) {
//                System.out.println("Instantiating card with: suit = " + suits[i] + " value = " + values[j]);
                pack[i * 13 + j] = new Card(suits[i], values[j], false);
            }
        }
    }
    
    private static void shufflePack() {
        for (int i = 0; i < 52; i++) {
            int index = rand.nextInt(52 - i);
            Card temp = pack[index];
            pack[index] = pack[51 - i];
            pack[51 - i] = temp;
        }
    }
    
    private static void layTable() {
        int packCounter = 0;
        //Lay out the tableau
        for (int i = 0; i < 7; i++) {
            tab.add(new ArrayList<Card>());
            for (int j = 0; j < i + 1; j++) {
                tab.get(i).add(pack[packCounter]);
                if (j != i) {
                    tab.get(i).get(j).setHidden(true);
                }
                packCounter++;
            }
        }
        
        //Place the rest of the cards in the hand
        while (packCounter < 52) {
            hand.add(pack[packCounter]);
            hand.get(hand.size() - 1).setHidden(true);
            packCounter++;
        }
        
        //Initliase the foundations
        for (int i = 0; i < 4; i++) {
            found.add(new ArrayList<Card>());
        }
    }
    
    private static void printTable() {    
        //Print the hand and foundations indexes
        System.out.println("   08    09 10 11 12");
        
        //Print the hand
        if (hand.isEmpty()) {
            System.out.print("[] ");
        } else {
            System.out.print(hand.get(hand.size() - 1).getSymbol() + " ");
        }
        
        //Print the waste
        if(waste.isEmpty()) {
            System.out.print("[]    ");
        } else {
            System.out.print(waste.get(waste.size() - 1).getSymbol() + "    ");
        }
        
        //Print the foundations
        for (int i = 0; i < 4; i++) {
            if(found.get(i).isEmpty()) {
                System.out.print("[] ");
            } else {
                System.out.print(found.get(i).get(found.size() - 1).getSymbol() + " ");
            }
        }
        System.out.println();
        
        //Print the column indexes
        System.out.println("01 02 03 04 05 06 07");
                
        //Print the tableau
        for (int i = 0; i < 19; i++) {
            if (isRowEmpty(i)) {
                break;
            }
            for (int j = 0; j < 7; j++) {
                if(i >= tab.get(j).size()) {
                    System.out.print("   ");
                } else {
                    System.out.print(tab.get(j).get(i).getSymbol() + " ");
                }
            }
            System.out.println();
        }
    }
    
    private static boolean isRowEmpty(int index) {
        for (int i = 0; i < 7; i++) {
            if (index < tab.get(i).size()) {
                return false;
            }
        }
        return true;
    }
    
    private static void turnHand() {
        if (hand.isEmpty()) {
            resetHand();
        } else {
            waste.add(hand.remove(hand.size() - 1));
            waste.get(waste.size() - 1).setHidden(false);
        }
    }
    
    private static void resetHand() {
        Collections.reverse(waste);
        hand.addAll(waste);
        for (Card card : hand) {
            card.setHidden(true);
        }
        waste.clear();
    }
    
    private static boolean gameWon() {
        //Code game win condition
        return false;
    }
//    
//    private static void moveCardStack(int c1, int n, int c2) {
//        Card[] cardStack;
//        //Read the card stack
//        cardStack = readCardStack(c1, n);
//        
//        //Was the card stack read successfully?
//        if (cardStack != null) {
//            //Write the card stack
//            writeCardStack(cardStack, c2);
//        }
//    }
//
//    private static Card[] readCardStack(int c1, int n) {
//        Card[] cardStack;
//        switch (c1) {
//            case 1:
//            case 2:
//            case 3:
//            case 4:
//            case 5:
//            case 6:
//            case 7:
//                cardStack = readCardStackFromTableau(n, c1);
//                break;
//            case 8:
//                cardStack = new Card[1];
//                cardStack[0] = readCardFromWaste();
//                if (cardStack[0] == null) {
//                    cardStack = null;
//                }
//                break;
//            case 9:
//            case 10:
//            case 11:
//            case 12:
//                cardStack = new Card[1];
//                cardStack[0] = readCardFromFoundations(c1);
//                if (cardStack[0] == null) {
//                    cardStack = null;
//                }
//                break;
//            default:
//                System.out.println("Source column/area not recognised");
//                cardStack = null;
//        }
//        return cardStack;
//    }
//
//    private static Card[] readCardStackFromTableau(int n, int c1) {
//        int col = c1 - 1;
//        int row = tableau.length;
//        do {
//            row--;
//        } while (tableau[row][col] == null);
//        Card[] cardStack = new Card[n];
//        for (int i = 0; i < n; i--) {
//            if (row < 0) {
//                System.out.println("There aren't enough cards in that column");
//                return null;                
//            }
//            if (tableau[row][col] != null) {
//                Card card = tableau[row][col];
//                if (card.getHidden()) {
//                    System.out.println("There aren't enough visible cards in that column");
//                    return null;
//                }
//                cardStack[i] = card;
//                tableau[row][col] = null;
//                row--;
//            }
//        }
//        return cardStack;
//    }
//    
//    private static Card readCardFromWaste() {
//        if (wasteCounter == 0) {
//            System.out.println("The waste is empty");
//            return null;
//        }
//        wasteCounter--;
//        Card tempCard = waste[wasteCounter];
//        waste[wasteCounter] = null;
//        return tempCard;
//    }
//    
//    private static Card readCardFromFoundations(int c1) {
//        int index = c1 - 9;
//        if (foundationsCounter[index] == 0) {
//            System.out.println("This foundation is empty");
//            return null;
//        }
//        foundationsCounter[index]--;
//        Card tempCard = foundations[foundationsCounter[index]][index];
//        foundations[foundationsCounter[index]][index] = null;
//        return tempCard;
//    }
//    
//    private static void writeCardStack(Card[] cardStack, int c2) {
//        switch (c2) {
//            case 1:
//            case 2:
//            case 3:
//            case 4:
//            case 5:
//            case 6:
//            case 7:
//                writeCardStackToTableau(cardStack, c2);
//                break;
//            case 8:
//                System.out.println("You cannot place a card in the waste"); 
//                break;
//            case 9:
//            case 10:
//            case 11:
//            case 12:
//                writeCardToFoundation(cardStack[0], c2);
//                break;
//            default:
//                System.out.println("Destination column/area not recognised");
//        }       
//    }
//    
//    private static void writeCardStackToTableau(Card[] cardStack, int c2) {
//        int col = c2 - 1;
//        int row = tableau.length;
//        do {
//            row--;
//        } while (tableau[row][col] == null);
//        if (isValidT(tableau[row][col], cardStack[0])) {
//            for (int i = 0; i < cardStack.length; i++) {
//                tableau[row + 1][col] = cardStack[i];
//            }
//            return;
//        }
//        System.out.println("That is not a valid move");
//    }
//    
//    private static void writeCardToFoundation(Card card, int c2) {
//        int index = c2 -9;
//        for(int i = 0; i < 4; i++) {
//            if (isValidF(foundations[foundationsCounter[index] - 1][index], card)) {
//                foundations[foundationsCounter[index]][index] = card;
//                foundationsCounter[index]++;
//                return;
//            }
//        }
//        System.out.println("That is not a valid move");
//    }
//    
//    private static boolean isValidT(Card card1, Card card2) {
//        return !card1.getColour().equals(card1.getColour()) && card1.getValue().equals(values[getIndex(card2.getValue()) + 1]);
//    }
//
//    private static boolean isValidF(Card card1, Card card2) {
//        return card1.getSuit().equals(card1.getSuit()) && card1.getValue().equals(values[getIndex(card2.getValue()) - 1]);
//    }
//    
//    private static int getIndex(String value) {
//        for (int i = 0; i < 13; i++) {
//            if (values[i].equals(value)) {
//                return i;
//            }
//        }
//        System.out.println("ERROR: getIndex was unable to find the index of the specified String");
//        return -1;
//    }
//    
}