package me.nrubin29.codechallenges;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;

public abstract class Challenge extends JFrame {

    private Console console;
    private JToolBar consolePane;

    JTextPane text;

    public Challenge() {
        setTitle(getName());

        text = new JTextPane();
        text.setText(getDefaultText());
        add(text);

        console = new Console();

        JScrollPane consoleScroll = new JScrollPane(console);
        consoleScroll.setBorder(null);
        consoleScroll.getVerticalScrollBar().setPreferredSize(new Dimension(0, 0));

        consolePane = new JToolBar();
        consolePane.setFloatable(false);
        consolePane.setVisible(false);
        consolePane.add(consoleScroll);

        add(consolePane);

        JMenuBar menuBar = new JMenuBar();

        final JMenu
                fileMenu = new JMenu("File"),
                consoleMenu = new JMenu("Console"),
                orientationMenu = new JMenu("Orientation")
        ;

        final JMenuItem
                runItem = new JMenuItem("Run"),
                closeConsoleItem = new JMenuItem("Close Console"),
                horizontalItem = new JRadioButtonMenuItem("Horizontal"),
                verticalItem = new JRadioButtonMenuItem("Vertical")
                        ;

        menuBar.add(fileMenu);
        menuBar.add(consoleMenu);

        fileMenu.add(runItem);

        consoleMenu.add(closeConsoleItem);
        consoleMenu.add(orientationMenu);

        ButtonGroup orientationGroup = new ButtonGroup();
        orientationGroup.add(horizontalItem);
        orientationGroup.add(verticalItem);

        verticalItem.setSelected(true);

        orientationMenu.add(verticalItem);
        orientationMenu.add(horizontalItem);

        setJMenuBar(menuBar);

        int meta = System.getProperty("os.name").startsWith("Mac") ? KeyEvent.META_DOWN_MASK : KeyEvent.CTRL_DOWN_MASK;

        runItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_R, meta));
        runItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (!consolePane.isVisible()) consolePane.setVisible(true);
                console.run(Challenge.this);
            }
        });

        closeConsoleItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_X, meta + KeyEvent.SHIFT_DOWN_MASK));
        closeConsoleItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                consolePane.setVisible(false);
            }
        });

        verticalItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                boolean visible = consolePane.isVisible();
                if (visible) consolePane.setVisible(false);
                setLayout(new BoxLayout(getContentPane(), BoxLayout.Y_AXIS));
                if (visible) consolePane.setVisible(true);
            }
        });

        horizontalItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                boolean visible = consolePane.isVisible();
                if (visible) consolePane.setVisible(false);
                setLayout(new BoxLayout(getContentPane(), BoxLayout.X_AXIS));
                if (visible) consolePane.setVisible(true);
            }
        });

        setLayout(new BoxLayout(getContentPane(), BoxLayout.Y_AXIS));
        setBackground(Color.WHITE);
        setSize(640, 480);
        setResizable(false);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setVisible(true);
    }

    public abstract String getName();
    public abstract String getTagline();
    public abstract String getInstructions();
    public abstract String getDefaultText();
    public abstract String getMainMethod();
}