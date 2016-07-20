/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package solitaire;

import java.util.ArrayList;
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
    private static Card[] pack;
    private static Stack<Card> hand, waste;
    private static Stack<Stack<Card>>  tab, found;
    private static ArrayList<int[]> posMoves;
    private static boolean  gameWon, gameLost;
    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        boolean restart;
        do {
            //Initliase the variables
            pack = new Card[52];
            hand = new Stack<>();
            waste = new Stack<>();
            tab = new Stack<>();
            found = new Stack<>();
            posMoves = new ArrayList<>();
            gameWon = false;
            gameLost = false;
            restart = false;
            
            //Fill the pack with cards
            fillPack();
//            printPack();

            //Shuffle the pack
            shufflePack();
//            printPack();

            //Place the cards onto the table
            layTable();

            int posMovesC;

            //Run main game loop        
            while (true) {

                posMovesC = 0;
                checkPossibleMoves();

                printTable();

                //Query the player
                System.out.println("Options:");
                System.out.println("- Type 'turn' to turn over the hand");
                System.out.println("- Type 'move #1 #2 #3' where #1 refers to the column/area you wish to move cards from, #2 refers to the number of cards you wish to move and #3 refers to the column/area you want to move them to");
                System.out.println("(Use 8 to refer to the waste and 9 through 12 to refer to the foundations)");
                System.out.println("- Type 'hint' to recieve a hint");
                System.out.println("- Type 'exit' to exit");
                System.out.println("- Type 'restart' to start a new game");

                boolean repeat, exitGameLoop;
                do {
                    exitGameLoop = false;
                    repeat = false;
                    String[] input = sc.nextLine().split(" ");
                    switch (input[0]) {
                        case "turn":
                            turnHand();
                            break;
                        case "move":
                            if (input.length != 4) {
                                System.out.println("You haven't entered the correct number of arguments");
                                repeat = true;
                            } else {
                                int[] inputNum = new int[3];
                                for (int i = 0; i < 3; i++) {
                                    inputNum[i] = Integer.parseInt(input[i + 1]);
                                }
//                                System.out.println(Arrays.toString(inputNum));
                                repeat = moveCardStack(inputNum[0], inputNum[1], inputNum[2]);
                            }
                            break;
                        case "hint":
                            if (posMoves.isEmpty()) {
                                System.out.println("turn");
                            } else {
                                int[] move = posMoves.get(posMovesC);
                                System.out.println("move " + move[0] + " " + move[1] + " " + move[2]);
                                posMovesC++;
                                if (posMovesC == posMoves.size()) {
                                    posMovesC = 0;
                                }
                            }
                            repeat = true;
                        case "exit":
                            exitGameLoop = true;
                            break;
                        case "restart":
                            restart = true;
                            exitGameLoop = true;
                            break;
                        default:
                            System.out.println("I don't recognise what you've entered");
                            repeat = true;
                    }
                } while (repeat);

                checkGameWon();
                checkGameLost();

                if (exitGameLoop) {
                    break;
                }
            }

            if (gameWon) {
                System.out.println("You win, congratulations!");
            } else if (gameLost) {
                System.out.println("There are no more available moves.");
            } else if (restart) {
                System.out.println("Restarting game");
            } else {
                System.out.println("Exiting game");
            }
        } while (restart);
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
            tab.push(new Stack<Card>());
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
            found.push(new Stack<Card>());
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
            System.out.println("You can't move that many cards at a time");
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
                if (n == 1) {
                    return readCardStackFromWaste();
                } else {
                    System.out.println("You can only move one card at a time from the waste");
                    return null;
                }
            case 9:
            case 10:
            case 11:
            case 12:
                if (n == 1) {
                    return readCardStackFromFoundations(loc);
                } else {
                    System.out.println("You can only move one card at a time from the foundations");
                }
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
            if (tab.get(index).get(tab.get(index).size() - 1 - i).getHidden()) {
                System.out.println("There aren't enough visible cards in that column");
                return null;             
            }
            cardStack.push(tab.get(index).get(tab.get(index).size() - 1 - i));
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
                if (cardStack.size() == 1) {
                    return writeCardStackToFoundation(cardStack, loc);
                } else {
                    System.out.println("You can only move one card at a time to the foundations");
                }
            default:
                System.out.println("Destination column/area not recognised");
                return true;
        }
    }
    
    private static boolean writeCardStackToTableau(Stack<Card> cardStack, int loc) {
        int index = loc - 1;
        if (isValidForTableau(cardStack.peek(), index, false)) {
            while (!cardStack.empty()) {
                tab.get(index).push(cardStack.pop());
            }
            return false;
        }
        return true;
    }
    
    private static boolean writeCardStackToFoundation(Stack<Card> cardStack, int loc) {
        int index = loc - 9;
        if (isValidForFoundations(cardStack.peek(), index, false)) {
            while (!cardStack.empty()) {
                found.get(index).push(cardStack.pop());
            }
            return false;
        }
        return true;
    }
    
    private static boolean isValidForTableau(Card placee, int index, boolean silent) {
        if (tab.get(index).empty()) {
            if (placee.getValue().equals("king")) {
                return true;
            }
            if (!silent) {
                System.out.println("Only kings can be placed in empty columns");
            }
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
                if (!silent) {
                    System.out.println("Cards can only be stacked sequentially");
                }
                return false;
            }
            if (!silent) {
                System.out.println("Only cards of differing colour may be stacked on the tableau");
            }
            return false;
        }
        if (!silent) {
            System.out.println("That is not a valid movement");
        }
        return false;
    }
    
    private static boolean isValidForFoundations(Card placee, int index, boolean silent) {
        if (found.get(index).empty()) {
            if (placee.getValue().equals("ace")) {
                return true;
            }
            if (!silent) {
                System.out.println("Only aces can be placed in empty foundations");
            }
            return false;
        }
        Card placed = found.get(index).peek();
        int valIndex = Arrays.asList(values).indexOf(placed.getValue()) + 1;
        if (valIndex >= 0 && valIndex < 13) {
            if (placed.getSuit().equals(placee.getSuit())) {
                if (values[valIndex].equals(placee.getValue())) {
                    return true;
                }
                if (!silent) {
                    System.out.println("Cards can only be stacked sequentially");
                }
                return false;
            }
            if (!silent) {
                System.out.println("Only cards of the same suit may be stacked on the foundations");
            }
            return false;
        }
        if (!silent) {
            System.out.println("That is not a valid movement");
        }
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
    
    private static void checkGameWon() {
        for (int i = 0; i < 4; i++) {
            if (found.get(i).size() != 13) {
                return;
            }
        }
        gameWon = true;
    }

    private static void checkGameLost() {
        //TODO: Code game loss condition
    }
    
    private static void checkPossibleMoves() {
        posMoves.clear();
        //Check the card a the top of the waste
        if (!waste.empty()) {
            considerCard(waste.peek(), 7, 1);
        }
        //Check the cards in the tableau
        for (Stack<Card> col : tab) {
            for (Card card : col) {
                if (!card.getHidden()) {
                    considerCard(card, tab.indexOf(col), col.size() - col.indexOf(card));
                }
            }
        }
        //Check the cards at the tops of the foundations
        for (Stack<Card> col : found) {
            if (!col.empty()) {
                considerCard(col.peek(), found.indexOf(col) + 8, 1);
            }
        }
    }
    
    private static void considerCard(Card card, int source, int n) {
        if (!(source > 7) && n == 1) {
            for (int i = 0; i < 4; i++) {
                if (isValidForFoundations(card, i, true)) {
                    int[] move = {source + 1, n, i + 9};
                    posMoves.add(move);
                }
            }
        }
        for (int i = 0; i < 7; i++) {
            if (isValidForTableau(card, i, true)) {
                int[] move = {source + 1, n, i + 1};
                posMoves.add(move);
            }
        }
    }
}