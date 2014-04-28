package me.nrubin29.codechallenges;

import javax.swing.*;
import javax.swing.text.*;
import javax.tools.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.net.URI;
import java.util.Arrays;
import java.util.Locale;

public class Console extends JTextPane {

    private boolean waiting = false;
    private String result = null;

    public Console() {
        ((AbstractDocument) getDocument()).setDocumentFilter(new Filter());

        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_UP) {
                    e.consume();
                } else if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    if (waiting) result = getText().split("\n")[getText().split("\n").length - 1];
                }
            }
        });

        setEditable(false);
        setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 16));
    }

    public void run(Challenge challenge) {
        try {
            JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();

            StandardJavaFileManager stdFileManager = compiler.getStandardFileManager(null, Locale.getDefault(), null);
            stdFileManager.setLocation(StandardLocation.SOURCE_OUTPUT, Arrays.asList(new File(getClass().getProtectionDomain().getCodeSource().getLocation().toURI()).getParentFile()));

            ToolProvider.getSystemJavaCompiler().getTask(null, stdFileManager, null, null, null, Arrays.asList(new JavaSourceFromString("Main", challenge.text.getText()))).call();

            Process p = new ProcessBuilder().command("java", getClass().getProtectionDomain().getCodeSource().getLocation() + "/Main.class").start();

            BufferedReader reader = new BufferedReader(new InputStreamReader(p.getInputStream()));
            BufferedReader reader1 = new BufferedReader(new InputStreamReader(p.getErrorStream()));
            while (reader.ready() || reader1.ready()) {
                System.out.println(reader.readLine());
                System.out.println(reader1.readLine());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void write(final String txt, final MessageType messageType) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                try {
                    getDocument().insertString(getDocument().getLength(), txt + "\n", messageType.getAttributes());
                } catch (Exception ignored) {
                }

                setCaret();
            }
        });
    }

    private int getLineOfOffset(int offset) throws BadLocationException {
        Document doc = getDocument();
        if (offset < 0) {
            throw new BadLocationException("Can't translate offset to line", -1);
        } else if (offset > doc.getLength()) {
            throw new BadLocationException("Can't translate offset to line", doc.getLength() + 1);
        } else {
            Element map = doc.getDefaultRootElement();
            return map.getElementIndex(offset);
        }
    }

    private int getLineStartOffset(int line) throws BadLocationException {
        Element map = getDocument().getDefaultRootElement();
        if (line < 0) {
            throw new BadLocationException("Negative line", -1);
        } else if (line > map.getElementCount()) {
            throw new BadLocationException("Given line too big", getDocument().getLength() + 1);
        } else {
            Element lineElem = map.getElement(line);
            return lineElem.getStartOffset();
        }
    }

    private void setCaret() {
        try {
            setCaretPosition(getDocument().getLength());
        } catch (Exception ignored) {
        }
    }

    public enum MessageType {
        OUTPUT(Color.BLACK),
        ERROR(Color.RED);

        private final SimpleAttributeSet attributes;

        MessageType(Color color) {
            attributes = new SimpleAttributeSet();
            StyleConstants.setForeground(attributes, color);
        }

        public SimpleAttributeSet getAttributes() {
            return attributes;
        }
    }

    private class Filter extends DocumentFilter {
        @Override
        public void insertString(final FilterBypass fb, final int offset, final String string, final AttributeSet attr)
                throws BadLocationException {
            if (getLineStartOffset(getLineOfOffset(offset)) == getLineStartOffset(getLineOfOffset(getDocument().getLength()))) {
                super.insertString(fb, getDocument().getLength(), string, null);
            }
            setCaret();
        }

        @Override
        public void remove(final FilterBypass fb, final int offset, final int length) throws BadLocationException {
            if (getLineStartOffset(getLineOfOffset(offset)) == getLineStartOffset(getLineOfOffset(getDocument().getLength()))) {
                super.remove(fb, offset, length);
            }
            setCaret();
        }

        @Override
        public void replace(final FilterBypass fb, final int offset, final int length, final String string, final AttributeSet attrs)
                throws BadLocationException {
            if (getLineStartOffset(getLineOfOffset(offset)) == getLineStartOffset(getLineOfOffset(getDocument().getLength()))) {
                super.replace(fb, offset, length, string, null);
            }
            setCaret();
        }
    }
}

class MyDiagnosticListener implements DiagnosticListener<JavaFileObject> {
    public void report(Diagnostic diagnostic) {
        System.out.println("Code->" +  diagnostic.getCode());
        System.out.println("Column Number->" + diagnostic.getColumnNumber());
        System.out.println("End Position->" + diagnostic.getEndPosition());
        System.out.println("Kind->" + diagnostic.getKind());
        System.out.println("Line Number->" + diagnostic.getLineNumber());
        System.out.println("Message->"+ diagnostic.getMessage(Locale.ENGLISH));
        System.out.println("Position->" + diagnostic.getPosition());
        System.out.println("Source" + diagnostic.getSource());
        System.out.println("Start Position->" + diagnostic.getStartPosition());
        System.out.println("\n");
    }
}

class JavaSourceFromString extends SimpleJavaFileObject {
    /**
     * The source code of this "file".
     */
    final String code;

    /**
     * Constructs a new JavaSourceFromString.
     * @param name the name of the compilation unit represented by this file object
     * @param code the source code for the compilation unit represented by this file object
     */
    JavaSourceFromString(String name, String code) {
        super(URI.create("string:///" + name.replace('.', '/') + Kind.SOURCE.extension), Kind.SOURCE);
        this.code = code;
    }

    @Override
    public CharSequence getCharContent(boolean ignoreEncodingErrors) {
        return code;
    }
}