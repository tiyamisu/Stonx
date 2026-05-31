package com.stonx.ui;

import com.stonx.service.ChatbotService;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.text.BadLocationException;
import javax.swing.text.html.HTMLDocument;
import javax.swing.text.html.HTMLEditorKit;
import java.awt.*;
import java.io.IOException;

/**
 * View Panel for StonBot AI Chat assistant.
 * Features rounded HTML bubbles, FAQ grid chips, and a simulated typing latency delay.
 */
public class StonBotPanel extends JPanel {
    private final MainFrame mainFrame;
    private final ChatbotService chatbotService;

    private JTextPane chatPane;
    private JTextField txtInput;
    private JButton btnSend;
    private HTMLDocument doc;
    private HTMLEditorKit kit;

    public StonBotPanel(MainFrame mainFrame) {
        this.mainFrame = mainFrame;
        this.chatbotService = ChatbotService.getInstance();

        setLayout(new BorderLayout(10, 10));
        setBorder(new EmptyBorder(15, 15, 15, 15));
        setBackground(new Color(20, 20, 20));

        initComponents();

        appendBotMessage("Hello! I am <b>StonBot</b> 🤖, your virtual investing assistant. " +
                "How can I help you navigate the stock market today?<br><br>" +
                "<i>Tip: Type a custom question or click the quick chips below!</i>");
    }

    private void initComponents() {
        // --- CHAT LOG PANEL (CENTER) ---
        JPanel chatPanel = new JPanel(new BorderLayout());
        chatPanel.setBackground(new Color(25, 25, 25));
        chatPanel.setBorder(BorderFactory.createLineBorder(new Color(45, 45, 45)));

        chatPane = new JTextPane();
        chatPane.setEditable(false);
        chatPane.setContentType("text/html");
        chatPane.setBackground(new Color(25, 25, 25));
        chatPane.setBorder(new EmptyBorder(10, 10, 10, 10));

        kit = new HTMLEditorKit();
        doc = new HTMLDocument();
        chatPane.setEditorKit(kit);
        chatPane.setDocument(doc);

        // Customize chat bubble styles
        String styleSheet = "body { font-family: 'Segoe UI', sans-serif; font-size: 12px; margin: 8px; color: #E0E0E0; }" +
                ".msg-container { margin-bottom: 12px; display: block; clear: both; width: 100%; }" +
                ".user-msg { float: right; background-color: #34495e; color: white; padding: 10px 14px; border-radius: 12px; max-width: 70%; text-align: left; }" +
                ".bot-msg { float: left; background-color: #1e272e; color: #ecf0f1; padding: 10px 14px; border-radius: 12px; border-left: 4px solid #2ecc71; max-width: 80%; text-align: left; }" +
                ".sender { font-size: 9px; font-weight: bold; margin-bottom: 4px; color: #95a5a6; text-transform: uppercase; }";
        doc.getStyleSheet().addRule(styleSheet);

        JScrollPane scrollChat = new JScrollPane(chatPane);
        scrollChat.setBorder(BorderFactory.createEmptyBorder());
        scrollChat.getVerticalScrollBar().setUnitIncrement(14);
        chatPanel.add(scrollChat, BorderLayout.CENTER);

        add(chatPanel, BorderLayout.CENTER);

        // --- BOTTOM ACTION PANEL (SOUTH) ---
        JPanel southPanel = new JPanel(new BorderLayout(5, 5));
        southPanel.setBackground(new Color(20, 20, 20));

        // 1. FAQ Grid Chips (2 columns grid to fit nicely)
        JPanel chipsPanel = new JPanel(new GridLayout(2, 5, 6, 6));
        chipsPanel.setBackground(new Color(20, 20, 20));
        chipsPanel.setBorder(new EmptyBorder(0, 0, 10, 0));

        String[] chips = {
                "What is a stock?",
                "What is a portfolio?",
                "What is profit/loss?",
                "What is a bull market?",
                "What is a bear market?",
                "How do I buy shares?",
                "How do I sell shares?",
                "Investing tips",
                "Risk management",
                "How does StonX work?"
        };

        for (String label : chips) {
            JButton btnChip = new JButton(label);
            btnChip.setFont(new Font("Segoe UI", Font.PLAIN, 11));
            btnChip.setBackground(new Color(45, 52, 54));
            btnChip.setForeground(new Color(200, 200, 200));
            btnChip.setCursor(new Cursor(Cursor.HAND_CURSOR));
            btnChip.putClientProperty("JButton.buttonType", "roundRect");
            btnChip.addActionListener(e -> handleQuestion(label));
            chipsPanel.add(btnChip);
        }
        
        southPanel.add(chipsPanel, BorderLayout.NORTH);

        // 2. Text Input row
        JPanel inputRow = new JPanel(new BorderLayout(8, 0));
        inputRow.setBackground(new Color(20, 20, 20));

        txtInput = new JTextField();
        txtInput.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        txtInput.putClientProperty("JTextField.placeholderText", "Ask StonBot a question about investing...");
        txtInput.addActionListener(e -> sendMessage());
        inputRow.add(txtInput, BorderLayout.CENTER);

        btnSend = new JButton("Send");
        btnSend.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btnSend.setBackground(new Color(46, 204, 113));
        btnSend.setForeground(Color.WHITE);
        btnSend.putClientProperty("JButton.buttonType", "roundRect");
        btnSend.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnSend.addActionListener(e -> sendMessage());
        inputRow.add(btnSend, BorderLayout.EAST);

        southPanel.add(inputRow, BorderLayout.CENTER);
        add(southPanel, BorderLayout.SOUTH);
    }

    private void handleQuestion(String question) {
        appendUserMessage(question);
        triggerTypingEffect(question);
    }

    private void sendMessage() {
        String text = txtInput.getText().trim();
        if (text.isEmpty()) return;

        txtInput.setText("");
        appendUserMessage(text);
        triggerTypingEffect(text);
    }

    /**
     * Creates a Swing Timer delay to simulate artificial typing.
     * Prevents EDT lockup and enhances presentation fidelity.
     */
    private void triggerTypingEffect(String query) {
        // Disable UI inputs
        txtInput.setEnabled(false);
        btnSend.setEnabled(false);
        btnSend.setText("Typing...");

        // Start 600ms latency timer
        Timer latencyTimer = new Timer(600, e -> {
            String answer = chatbotService.answerQuery(query);
            appendBotMessage(answer);

            // Re-enable inputs
            txtInput.setEnabled(true);
            btnSend.setEnabled(true);
            btnSend.setText("Send");
            txtInput.requestFocusInWindow();
        });
        latencyTimer.setRepeats(false);
        latencyTimer.start();
    }

    private void appendUserMessage(String text) {
        try {
            String html = "<div class='msg-container'>" +
                    "<div class='user-msg'>" +
                    "<div class='sender'>You</div>" +
                    text +
                    "</div>" +
                    "</div><br style='clear:both;'/>";
            kit.insertHTML(doc, doc.getLength(), html, 0, 0, null);
            scrollToBottom();
        } catch (BadLocationException | IOException e) {
            System.err.println("Chat rendering error: " + e.getMessage());
        }
    }

    private void appendBotMessage(String htmlText) {
        try {
            String html = "<div class='msg-container'>" +
                    "<div class='bot-msg'>" +
                    "<div class='sender'>StonBot AI 🤖</div>" +
                    htmlText +
                    "</div>" +
                    "</div><br style='clear:both;'/>";
            kit.insertHTML(doc, doc.getLength(), html, 0, 0, null);
            scrollToBottom();
        } catch (BadLocationException | IOException e) {
            System.err.println("Chat rendering error: " + e.getMessage());
        }
    }

    private void scrollToBottom() {
        SwingUtilities.invokeLater(() -> chatPane.setCaretPosition(doc.getLength()));
    }
}
