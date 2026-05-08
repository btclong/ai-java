package test;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.Image;

public class SimpleCalculator extends JFrame {
    private static final String FONT_NAME = "Malgun Gothic";

    private final JTextField firstNumberField;
    private final JTextField secondNumberField;
    private final JComboBox<String> operatorBox;
    private final JLabel resultLabel;
    private final ImageIcon calculatorIcon;

    public SimpleCalculator() {
        setTitle("간단한 계산기");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(430, 360);
        setLocationRelativeTo(null);

        firstNumberField = new JTextField();
        secondNumberField = new JTextField();
        operatorBox = new JComboBox<>(new String[]{"+", "-", "*", "/"});
        resultLabel = new JLabel("결과: 대기 중");
        calculatorIcon = loadCalculatorIcon();

        JButton calculateButton = new JButton("계산하기");
        calculateButton.addActionListener(event -> calculate());
        calculateButton.setBackground(new Color(255, 119, 97));
        calculateButton.setForeground(Color.WHITE);
        calculateButton.setFocusPainted(false);
        calculateButton.setFont(new Font(FONT_NAME, Font.BOLD, 14));

        JPanel inputPanel = new JPanel(new GridLayout(3, 2, 8, 8));
        inputPanel.setBackground(new Color(255, 247, 214));
        inputPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(78, 205, 196), 3),
                BorderFactory.createEmptyBorder(12, 12, 12, 12)
        ));
        inputPanel.add(createLabel("첫 번째 숫자", new Color(34, 87, 122)));
        inputPanel.add(firstNumberField);
        inputPanel.add(createLabel("연산자", new Color(120, 80, 200)));
        inputPanel.add(operatorBox);
        inputPanel.add(createLabel("두 번째 숫자", new Color(255, 119, 97)));
        inputPanel.add(secondNumberField);

        firstNumberField.setBackground(new Color(232, 246, 255));
        secondNumberField.setBackground(new Color(242, 232, 255));
        operatorBox.setBackground(new Color(230, 255, 238));

        JPanel bottomPanel = new JPanel(new BorderLayout(8, 8));
        bottomPanel.setBackground(new Color(34, 87, 122));
        bottomPanel.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));
        resultLabel.setForeground(Color.WHITE);
        resultLabel.setFont(new Font(FONT_NAME, Font.BOLD, 14));
        bottomPanel.add(calculateButton, BorderLayout.WEST);
        bottomPanel.add(resultLabel, BorderLayout.CENTER);

        JPanel headerPanel = new JPanel(new BorderLayout(8, 8));
        headerPanel.setBackground(new Color(34, 87, 122));
        headerPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        JLabel imageLabel = new JLabel(calculatorIcon);
        JLabel titleLabel = new JLabel("컬러 계산기");
        titleLabel.setForeground(new Color(255, 218, 97));
        titleLabel.setFont(new Font(FONT_NAME, Font.BOLD, 24));
        headerPanel.add(imageLabel, BorderLayout.WEST);
        headerPanel.add(titleLabel, BorderLayout.CENTER);

        JPanel mainPanel = new JPanel(new BorderLayout(8, 8));
        mainPanel.setBackground(new Color(78, 205, 196));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));
        mainPanel.add(headerPanel, BorderLayout.NORTH);
        mainPanel.add(inputPanel, BorderLayout.CENTER);
        mainPanel.add(bottomPanel, BorderLayout.SOUTH);

        add(mainPanel);
    }

    private JLabel createLabel(String text, Color color) {
        JLabel label = new JLabel(text);
        label.setForeground(color);
        label.setFont(new Font(FONT_NAME, Font.BOLD, 14));
        return label;
    }

    private ImageIcon loadCalculatorIcon() {
        java.net.URL imageUrl = SimpleCalculator.class.getResource("calculator.png");
        if (imageUrl == null) {
            return new ImageIcon();
        }

        Image image = new ImageIcon(imageUrl).getImage();
        Image scaledImage = image.getScaledInstance(80, 80, Image.SCALE_SMOOTH);
        return new ImageIcon(scaledImage);
    }

    private void calculate() {
        try {
            double firstNumber = Double.parseDouble(firstNumberField.getText().trim());
            double secondNumber = Double.parseDouble(secondNumberField.getText().trim());
            String operator = (String) operatorBox.getSelectedItem();
            double result;

            switch (operator) {
                case "+":
                    result = firstNumber + secondNumber;
                    break;
                case "-":
                    result = firstNumber - secondNumber;
                    break;
                case "*":
                    result = firstNumber * secondNumber;
                    break;
                case "/":
                    if (secondNumber == 0) {
                        JOptionPane.showMessageDialog(this, "0으로 나눌 수 없습니다.");
                        return;
                    }
                    result = firstNumber / secondNumber;
                    break;
                default:
                    JOptionPane.showMessageDialog(this, "올바르지 않은 연산자입니다.");
                    return;
            }

            String resultText = "결과: " + result;
            resultLabel.setText(resultText);
            showResultWindow(resultText);
        } catch (NumberFormatException exception) {
            JOptionPane.showMessageDialog(this, "올바른 숫자를 입력해 주세요.");
        }
    }

    private void showResultWindow(String resultText) {
        JDialog resultDialog = new JDialog(this, "계산 결과", true);
        resultDialog.setSize(220, 130);
        resultDialog.setLocationRelativeTo(this);

        JLabel popupResultLabel = new JLabel(resultText, JLabel.CENTER);
        popupResultLabel.setOpaque(true);
        popupResultLabel.setBackground(new Color(255, 218, 97));
        popupResultLabel.setForeground(new Color(34, 87, 122));
        popupResultLabel.setFont(new Font(FONT_NAME, Font.BOLD, 20));

        JButton closeButton = new JButton("확인");
        closeButton.setBackground(new Color(78, 205, 196));
        closeButton.setForeground(Color.WHITE);
        closeButton.setFocusPainted(false);
        closeButton.addActionListener(event -> resultDialog.dispose());

        JPanel panel = new JPanel(new BorderLayout(8, 8));
        panel.setBackground(new Color(255, 247, 214));
        panel.setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));
        panel.add(popupResultLabel, BorderLayout.CENTER);
        panel.add(closeButton, BorderLayout.SOUTH);

        resultDialog.add(panel);
        resultDialog.setVisible(true);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            SimpleCalculator calculator = new SimpleCalculator();
            calculator.setVisible(true);
        });
    }
}
