package me.nrubin29.codechallenges;

import javax.swing.*;

public class Main extends JFrame {

    public static void main(String[] args) {
        new Challenge() {

            @Override
            public String getName() {
                return "Test";
            }

            @Override
            public String getTagline() {
                return "A test challenge.";
            }

            @Override
            public String getInstructions() {
                return "Don't do anything.";
            }

            @Override
            public String getDefaultText() {
                return "public void text() {\n\n}";
            }

            @Override
            public String getMainMethod() {
                return "method(8);";
            }
        };
    }
}