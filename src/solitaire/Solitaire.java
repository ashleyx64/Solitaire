/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package solitaire;

import java.util.Arrays;
import java.util.Random;
import java.util.Scanner;
import java.util.Stack;

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
    private static final Stack<Card> hand = new Stack<>(),
                                    waste = new Stack<>();
    private static final Stack<Stack<Card>>  tab = new Stack<>(),
                                            found = new Stack<>();
    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        tab.setSize(7);
        found.setSize(4);
        
        fillPack();
        printPack();
        
        shufflePack();
        printPack();
        
        layTable();
        
        do {
            
            printTable();
            
            //Query the player
            System.out.println("Options:");
            System.out.println("- Enter 'turn' to turn over the hand");
            System.out.println("- Enter 'move #1 #2 #3' where #1 refers to the column/area you wish to move cards from, #2 refers to the number of cards you wish to move and #3 refers to the column/area you want to move them to");
            System.out.println("(Use 8 to refer to the waste and 9 through 12 to refer to the foundations)");
            
            boolean inputRecognised, moveFail = false;            
            do {
                inputRecognised = true;
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
//                            System.out.println(Arrays.toString(inputNum));
                            moveFail = moveCardStack(inputNum[0], inputNum[1], inputNum[2]);
                        }
                        break;
                    default:
                        System.out.println("I don't recognise what you've entered");
                        inputRecognised = false;
                }
            } while (!inputRecognised || moveFail);
            
            
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
            //Initialise the stack in each column
            tab.set(i, new Stack<Card>());
            for (int j = 0; j < i + 1; j++) {
                tab.get(i).push(pack[packCounter]);
                if (j != i) {
                    tab.get(i).peek().setHidden(true);
                }
                packCounter++;
            }
        }
        
        //Place the rest of the cards in the hand
        while (packCounter < 52) {
            hand.push(pack[packCounter]);
            hand.peek().setHidden(true);
            packCounter++;
        }
        
        //Initliase the foundations
        for (int i = 0; i < 4; i++) {
            found.set(i, new Stack<Card>());
        }
    }
    
    private static void printTable() {    
        //Print the hand and foundations indexes
        System.out.println("   08    09 10 11 12");
        
        //Print the hand
        if (hand.empty()) {
            System.out.print("[] ");
        } else {
            System.out.print(hand.peek().getSymbol() + " ");
        }
        
        //Print the waste
        if(waste.empty()) {
            System.out.print("[]    ");
        } else {
            System.out.print(waste.peek().getSymbol() + "    ");
        }
        
        //Print the foundations
        for (int i = 0; i < 4; i++) {
            if(found.get(i).empty()) {
                System.out.print("[] ");
            } else {
                System.out.print(found.get(i).peek().getSymbol() + " ");
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
        if (hand.empty()) {
            resetHand();
        } else {
            waste.push(hand.pop());
            waste.peek().setHidden(false);
        }
    }
    
    private static void resetHand() {
        while (!waste.empty()) {
            hand.push(waste.pop());
            hand.peek().setHidden(true);
        }
        waste.clear();
    }
    
    private static boolean gameWon() {
        //TODO: Code game win condition
        return false;
    }
    
    private static boolean moveCardStack(int source, int n, int dest) {
        Stack<Card> cardStack;
        //Read the card stack
        cardStack = readCardStack(source, n);
        
        //Was the card stack read successfully?
        if (cardStack != null) {
            //Attempt to write the card stack
            boolean writeFail = writeCardStack(cardStack, dest);
            //If writing failed write the card stack back to the source
            if (!writeFail) {
                removeCardStack(source, n);
                revealCard(source);
                return false;
            }
        }
        return true;
    }

    private static Stack<Card> readCardStack(int loc, int n) {
        //Check that n is valid
        if (n < 1 || n > 13) {
            System.out.println("That value for n is invalid");
            return null;
        }
        
        switch (loc) {
            case 1:
            case 2:
            case 3:
            case 4:
            case 5:
            case 6:
            case 7:
                return readCardStackFromTableau(loc, n);
            case 8:
                return readCardStackFromWaste();
            case 9:
            case 10:
            case 11:
            case 12:
                return readCardStackFromFoundations(loc);
            default:
                System.out.println("Source column/area not recognised");
                return null;
        }
    }

    private static Stack<Card> readCardStackFromTableau(int loc, int n) {
        int index = loc - 1;
        if (n > tab.get(index).size()) {
            System.out.println("There aren't enough cards in that column");
            return null;
        }
        Stack<Card> cardStack = new Stack<>();
        for (int i = 0; i < n; i++) {
            if (tab.get(index).peek().getHidden()) {
                System.out.println("There aren't enough visible cards in that column");
                return null;             
            }
            cardStack.push(tab.get(index).peek());
        }
        return cardStack;
    }
    
    private static Stack<Card> readCardStackFromWaste() {
        if (waste.empty()) {
            System.out.println("The waste is empty");
            return null;
        }
        Stack<Card> cardStack = new Stack<>();
        cardStack.push(waste.peek());
        return cardStack;
    }
    
    private static Stack<Card> readCardStackFromFoundations(int loc) {
        int index = loc - 9;
        if (found.get(index).empty()) {
            System.out.println("This foundation is empty");
            return null;
        }
        Stack<Card> cardStack = new Stack<>();
        cardStack.push(found.get(index).peek());
        return cardStack;
    }
    
    private static boolean writeCardStack(Stack<Card> cardStack, int loc) {
        switch (loc) {
            case 1:
            case 2:
            case 3:
            case 4:
            case 5:
            case 6:
            case 7:
                return writeCardStackToTableau(cardStack, loc);
            case 8:
                System.out.println("You cannot place a card in the waste"); 
                return true;
            case 9:
            case 10:
            case 11:
            case 12:
                return writeCardStackToFoundation(cardStack, loc);
            default:
                System.out.println("Destination column/area not recognised");
                return true;
        }
    }
    
    private static boolean writeCardStackToTableau(Stack<Card> cardStack, int loc) {
        int index = loc - 1;
        if (isValidForTableau(cardStack.peek(), index)) {
            while (!cardStack.empty()) {
                tab.get(index).push(cardStack.pop());
            }
            return false;
        }
        return true;
    }
    
    private static boolean writeCardStackToFoundation(Stack<Card> cardStack, int loc) {
        int index = loc - 9;
        if (isValidForFoundations(cardStack.peek(), index)) {
            while (!cardStack.empty()) {
                found.get(index).push(cardStack.pop());
            }
            return false;
        }
        return true;
    }
    
    private static boolean isValidForTableau(Card placee, int index) {
        if (tab.get(index).empty()) {
            if (placee.getValue().equals("king")) {
                return true;
            }
            System.out.println("Only kings can be placed in empty columns");
            return false;
        }
        Card placed = tab.get(index).peek();
//        System.out.println("placed.getValue() = " + placed.getValue());
        int valIndex = Arrays.asList(values).indexOf(placed.getValue()) - 1;
//        System.out.println("valIndex = " + valIndex);
        if (valIndex >= 0 && valIndex < 13) {
            if (!placed.getColour().equals(placee.getColour())) {
                if (values[valIndex].equals(placee.getValue())) {
                    return true;
                }
                System.out.println("Cards can only be stacked sequentially");
                return false;
            }
            System.out.println("Only cards of differing colour may be stacked on the tableau");
            return false;
        }
        System.out.println("That is not a valid movement");
        return false;
    }
    
    private static boolean isValidForFoundations(Card placee, int index) {
        if (found.get(index).empty()) {
            if (placee.getValue().equals("ace")) {
                return true;
            }
            System.out.println("Only aces can be placed in empty foundations");
            return false;
        }
        Card placed = found.get(index).peek();
        int valIndex = Arrays.asList(values).indexOf(placed.getValue()) + 1;
        if (valIndex >= 0 && valIndex < 13) {
            if (placed.getSuit().equals(placee.getSuit())) {
                if (values[valIndex].equals(placee.getValue())) {
                    return true;
                }
                System.out.println("Cards can only be stacked sequentially");
                return false;
            }
            System.out.println("Only cards of the same suit may be stacked on the foundations");
            return false;
        }
        System.out.println("That is not a valid movement");
        return false;
    }
    
    private static void revealCard(int loc) {
        if (loc >= 1 && loc <= 7) {
            int index = loc - 1;
            if (!tab.get(index).empty()) {
                tab.get(index).peek().setHidden(false);
            }
        }
    }
    
    private static void removeCardStack(int loc, int n) {
        switch (loc) {
            case 1:
            case 2:
            case 3:
            case 4:
            case 5:
            case 6:
            case 7:
                removeCardStackFromTableau(loc, n);
                break;
            case 8:
                removeCardFromWaste();
                break;
            case 9:
            case 10:
            case 11:
            case 12:
                removeCardFromFoundations(loc);
                break;
            default:
                System.out.println("ERROR: Location to remove card stack from not recognised");
        }
    }
    
    private static void removeCardStackFromTableau(int loc, int n) {
        int index = loc - 1;
        for (int i = 0; i < n; i++) {
            tab.get(index).pop();
        }
    }
    
    private static void removeCardFromWaste() {
        waste.pop();
    }
    
    private static void removeCardFromFoundations(int loc) {
        int index = loc - 9;
        found.get(index).pop();
    } 
}