package jaakko.jaaska.apptycoon.console;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.Stack;


/**
 * Purpose of the console is to provide an interface for manual testing of the engine through
 * commandline on the PC. This will not be accessible from the Android game.
 *
 * Created by jaakko on 27.3.2017.
 */

public class Console {

    private List<MenuPage> mPages;
    private Stack<MenuPage> mNavStack;

    private void startConsole() {
        System.out.println("SoftwareTycoon Dev Console\n");
        initPages();
        printCurrentMenuPage();
    }

    private void quitConsole() {
        // Gracefully stop and exit the console.
    }


    private void initPages() {

        mNavStack = new Stack<>();
        mPages = new ArrayList<>();

        //
        //
        // Random Generators
        final MenuPage randomGens = new MenuPage("Random Generators");
        randomGens.addAction(new Action("person", "Generate a random Employee.") {
            @Override
            public void doAction(Object... params) {
                //System.out.println(Employee.generateRandomPerson(500));
            }
        });



        //
        //
        // Main Menu
        MenuPage mainMenu = new MenuPage("Main Menu");
        mainMenu.addAction(new Action("r", "Run random generators.") {
            @Override
            public void doAction(Object[] params) {
                navigateToMenuPage(randomGens);
            }
        });


        // Set mainMenu as the current page.
        mNavStack.push(mainMenu);

    }


    private void printCurrentMenuPage() {
        System.out.println(mNavStack.peek());
        readCommand();
    }

    private void readCommand() {
        String cmd = "";
        Scanner sc = new Scanner(System.in);
        MenuPage currentPage = mNavStack.peek();

        while (currentPage.getAction(cmd) == null) {
            System.out.print("> ");
            cmd = sc.nextLine();

            // "back" is a universal command to navigate back one level in the menu.
            if (cmd.equals("back")) {
                goBack();
                return;
            }
        }

        currentPage.getAction(cmd).doAction();
        printCurrentMenuPage();
    }

    private void goBack() {

        mNavStack.pop();

        if (!mNavStack.empty()) {
            printCurrentMenuPage();
        } else {
            quitConsole();
        }

    }

    private void navigateToMenuPage(MenuPage newPage) {
        mNavStack.push(newPage);
        printCurrentMenuPage();
    }

    public static void main(String[] args) {
        Console console = new Console();
        console.startConsole();
    }

    public static void printError(String error) {
        System.out.println("ERROR: " + error);
    }
}
