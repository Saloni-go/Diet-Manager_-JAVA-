// model/UndoManager.java
package model;

import java.util.Stack;

public class UndoManager {
    private Stack<Command> history = new Stack<>();

    public void executeCommand(Command command) {
        command.execute();
        history.push(command);
    }

    public void undo() {
        if (!history.isEmpty()) {
            Command command = history.pop();
            command.undo();
        }
    }

    public boolean canUndo() {
        return !history.isEmpty();
    }
}
