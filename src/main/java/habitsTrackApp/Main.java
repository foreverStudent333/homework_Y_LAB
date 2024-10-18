package habitsTrackApp;

import habitsTrackApp.controller.ControllerConsole;

public class Main {
    public static void main(String[] args) {
        ControllerConsole controllerConsole = new ControllerConsole();
        controllerConsole.printMenuAndDoCommands();
    }
}