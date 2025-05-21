package com.pool;

import com.pool.ui.StudentMenuHandler;

import java.util.Scanner;

public class MainApp {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        StudentMenuHandler handler = new StudentMenuHandler(scanner);

        boolean running = true;
        while (running) {
            handler.printMenu();
            try {
                int choice = Integer.parseInt(scanner.nextLine());
                running = handler.handleChoice(choice);
            } catch (NumberFormatException e) {
                System.out.println("Please enter a valid number!");
            }
        }

        System.out.println("Exiting Student Record System...");
        scanner.close();
    }
}
