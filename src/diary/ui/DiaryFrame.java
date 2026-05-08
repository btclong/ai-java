package diary.ui;

import diary.dao.DiaryDao;
import diary.dto.DiaryDto;

import javax.swing.BorderFactory;
import javax.swing.DefaultListCellRenderer;
import javax.swing.DefaultListModel;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.Insets;
import java.awt.RenderingHints;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.sql.SQLException;
import java.time.format.DateTimeFormatter;

public class DiaryFrame extends JFrame {
    private static final Color BG_TOP = new Color(0xFFF7ED);
    private static final Color BG_BOTTOM = new Color(0xE0F2FE);
    private static final Color CARD_COLOR = new Color(255, 255, 255, 230);
    private static final Color CARD_BORDER = new Color(255, 255, 255, 170);
    private static final Color INPUT_COLOR = new Color(0xFFFFFF);
    private static final Color PRIMARY_COLOR = new Color(0xFB7185);
    private static final Color PRIMARY_DARK = new Color(0xE11D48);
    private static final Color SECONDARY_COLOR = new Color(0x60A5FA);
    private static final Color TEXT_COLOR = new Color(0x1F2937);
    private static final Color MUTED_COLOR = new Color(0x6B7280);
    private static final Font BASIC_FONT = new Font("Malgun Gothic", Font.PLAIN, 14);
    private static final Font BOLD_FONT = new Font("Malgun Gothic", Font.BOLD, 14);
    private static final Font TITLE_FONT = new Font("Malgun Gothic", Font.BOLD, 28);
    private static final DateTimeFormatter DETAIL_DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    private final DiaryDao diaryDao = new DiaryDao();
    private final DefaultListModel<DiaryDto> diaryListModel = new DefaultListModel<>();
    private final JList<DiaryDto> diaryList = new JList<>(diaryListModel);
    private final JTextField titleField = new JTextField();
    private final JComboBox<String> weatherComboBox = new JComboBox<>(new String[]{"맑음", "흐림", "비", "눈", "바람"});
    private final JTextArea contentArea = new JTextArea();
    private final JLabel dateLabel = new JLabel("새 일기");
    private final JLabel countLabel = new JLabel("0개의 기록");

    private Integer selectedId;

    public DiaryFrame() {
        setTitle("오늘의 일기");
        setSize(980, 680);
        setMinimumSize(new Dimension(860, 560));
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        GradientPanel root = new GradientPanel();
        root.setLayout(new BorderLayout(18, 18));
        root.setBorder(BorderFactory.createEmptyBorder(18, 22, 22, 22));
        setContentPane(root);

        root.add(createHeaderPanel(), BorderLayout.NORTH);
        root.add(createListPanel(), BorderLayout.WEST);
        root.add(createEditorPanel(), BorderLayout.CENTER);

        loadDiaryList();
        setVisible(true);
    }

    private JPanel createHeaderPanel() {
        RoundedPanel panel = new RoundedPanel(28, CARD_COLOR);
        panel.setLayout(new BorderLayout(16, 0));
        panel.setBorder(BorderFactory.createEmptyBorder(18, 20, 18, 20));

        JLabel imageLabel = new JLabel();
        ImageIcon icon = loadImageIcon();
        if (icon != null) {
            imageLabel.setIcon(icon);
        }

        JPanel textPanel = new JPanel(new GridLayout(2, 1, 0, 2));
        textPanel.setOpaque(false);

        JLabel titleLabel = new JLabel("오늘의 일기");
        titleLabel.setFont(TITLE_FONT);
        titleLabel.setForeground(TEXT_COLOR);

        JLabel subTitleLabel = new JLabel("하루의 기분, 날씨, 생각을 차분하게 기록하세요");
        subTitleLabel.setFont(BASIC_FONT);
        subTitleLabel.setForeground(MUTED_COLOR);

        textPanel.add(titleLabel);
        textPanel.add(subTitleLabel);

        countLabel.setFont(BOLD_FONT);
        countLabel.setForeground(PRIMARY_DARK);
        countLabel.setHorizontalAlignment(JLabel.RIGHT);

        panel.add(imageLabel, BorderLayout.WEST);
        panel.add(textPanel, BorderLayout.CENTER);
        panel.add(countLabel, BorderLayout.EAST);
        return panel;
    }

    private JPanel createListPanel() {
        RoundedPanel panel = new RoundedPanel(24, CARD_COLOR);
        panel.setPreferredSize(new Dimension(290, 0));
        panel.setLayout(new BorderLayout(10, 12));
        panel.setBorder(BorderFactory.createEmptyBorder(18, 18, 18, 18));

        JLabel listTitle = new JLabel("기록 목록");
        listTitle.setFont(new Font("Malgun Gothic", Font.BOLD, 18));
        listTitle.setForeground(TEXT_COLOR);

        diaryList.setFont(BASIC_FONT);
        diaryList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        diaryList.setFixedCellHeight(44);
        diaryList.setBackground(new Color(0, 0, 0, 0));
        diaryList.setForeground(TEXT_COLOR);
        diaryList.setSelectionBackground(new Color(0xFCE7F3));
        diaryList.setSelectionForeground(PRIMARY_DARK);
        diaryList.setCellRenderer(new DiaryCellRenderer());
        diaryList.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                showSelectedDiary();
            }
        });

        JScrollPane scrollPane = new JScrollPane(diaryList);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.getViewport().setOpaque(false);
        scrollPane.setOpaque(false);

        panel.add(listTitle, BorderLayout.NORTH);
        panel.add(scrollPane, BorderLayout.CENTER);
        return panel;
    }

    private JPanel createEditorPanel() {
        JPanel panel = new JPanel(new BorderLayout(14, 14));
        panel.setOpaque(false);

        panel.add(createInfoPanel(), BorderLayout.NORTH);
        panel.add(createContentPanel(), BorderLayout.CENTER);
        panel.add(createButtonPanel(), BorderLayout.SOUTH);
        return panel;
    }

    private JPanel createInfoPanel() {
        RoundedPanel panel = new RoundedPanel(24, CARD_COLOR);
        panel.setLayout(new GridBagLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(18, 18, 18, 18));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(6, 8, 6, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        styleTextField(titleField);
        styleComboBox(weatherComboBox);

        addFormRow(panel, gbc, 0, "제목", titleField);
        addFormRow(panel, gbc, 1, "날씨", weatherComboBox);

        dateLabel.setFont(BOLD_FONT);
        dateLabel.setForeground(SECONDARY_COLOR);
        gbc.gridx = 1;
        gbc.gridy = 2;
        gbc.weightx = 1;
        panel.add(dateLabel, gbc);

        return panel;
    }

    private JPanel createContentPanel() {
        RoundedPanel panel = new RoundedPanel(24, CARD_COLOR);
        panel.setLayout(new BorderLayout(0, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(18, 18, 18, 18));

        JLabel label = new JLabel("내용");
        label.setFont(new Font("Malgun Gothic", Font.BOLD, 18));
        label.setForeground(TEXT_COLOR);

        contentArea.setFont(new Font("Malgun Gothic", Font.PLAIN, 15));
        contentArea.setLineWrap(true);
        contentArea.setWrapStyleWord(true);
        contentArea.setForeground(TEXT_COLOR);
        contentArea.setBackground(INPUT_COLOR);
        contentArea.setCaretColor(PRIMARY_DARK);
        contentArea.setBorder(BorderFactory.createEmptyBorder(14, 14, 14, 14));

        JScrollPane contentScrollPane = new JScrollPane(contentArea);
        contentScrollPane.setBorder(BorderFactory.createLineBorder(new Color(0xF3F4F6), 1));

        panel.add(label, BorderLayout.NORTH);
        panel.add(contentScrollPane, BorderLayout.CENTER);
        return panel;
    }

    private JPanel createButtonPanel() {
        RoundedPanel panel = new RoundedPanel(24, CARD_COLOR);
        panel.setLayout(new GridLayout(1, 4, 10, 0));
        panel.setBorder(BorderFactory.createEmptyBorder(14, 14, 14, 14));

        panel.add(new ModernButton("새로쓰기", SECONDARY_COLOR, e -> clearForm()));
        panel.add(new ModernButton("저장", PRIMARY_COLOR, e -> saveDiary()));
        panel.add(new ModernButton("수정", new Color(0xA78BFA), e -> updateDiary()));
        panel.add(new ModernButton("삭제", new Color(0xF97316), e -> deleteDiary()));
        return panel;
    }

    private void addFormRow(JPanel panel, GridBagConstraints gbc, int row, String labelText, Component component) {
        JLabel label = new JLabel(labelText);
        label.setFont(BOLD_FONT);
        label.setForeground(TEXT_COLOR);

        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.weightx = 0;
        panel.add(label, gbc);

        component.setFont(BASIC_FONT);
        gbc.gridx = 1;
        gbc.gridy = row;
        gbc.weightx = 1;
        panel.add(component, gbc);
    }

    private void styleTextField(JTextField field) {
        field.setForeground(TEXT_COLOR);
        field.setCaretColor(PRIMARY_DARK);
        field.setBackground(INPUT_COLOR);
        field.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(0xF3F4F6), 1),
                BorderFactory.createEmptyBorder(10, 12, 10, 12)
        ));
    }

    private void styleComboBox(JComboBox<String> comboBox) {
        comboBox.setForeground(TEXT_COLOR);
        comboBox.setBackground(INPUT_COLOR);
        comboBox.setBorder(BorderFactory.createEmptyBorder(8, 10, 8, 10));
        comboBox.setFocusable(false);
    }

    private ImageIcon loadImageIcon() {
        String[] paths = {"resources/diary.png", "src/diary/diary.png", "src/test/calculator.png"};

        for (String path : paths) {
            File file = new File(path);
            if (file.exists()) {
                ImageIcon icon = new ImageIcon(path);
                Image image = icon.getImage().getScaledInstance(72, 72, Image.SCALE_SMOOTH);
                return new ImageIcon(image);
            }
        }

        return null;
    }

    private void loadDiaryList() {
        diaryListModel.clear();

        try {
            for (DiaryDto diary : diaryDao.findAll()) {
                diaryListModel.addElement(diary);
            }
            countLabel.setText(diaryListModel.size() + "개의 기록");
        } catch (SQLException e) {
            showError("일기 목록을 불러오지 못했습니다.", e);
        }
    }

    private void showSelectedDiary() {
        DiaryDto diary = diaryList.getSelectedValue();
        if (diary == null) {
            return;
        }

        selectedId = diary.getId();
        titleField.setText(diary.getTitle());
        weatherComboBox.setSelectedItem(diary.getWeather());
        contentArea.setText(diary.getContent());

        if (diary.getCreatedDate() == null) {
            dateLabel.setText("작성일 없음");
        } else {
            dateLabel.setText(diary.getCreatedDate().format(DETAIL_DATE_FORMATTER));
        }
    }

    private void saveDiary() {
        DiaryDto diary = getInputDiary();
        if (diary == null) {
            return;
        }

        try {
            if (diaryDao.insert(diary)) {
                loadDiaryList();
                clearForm();
                JOptionPane.showMessageDialog(this, "일기를 저장했습니다.");
            }
        } catch (SQLException e) {
            showError("일기 저장에 실패했습니다.", e);
        }
    }

    private void updateDiary() {
        if (selectedId == null) {
            JOptionPane.showMessageDialog(this, "수정할 일기를 선택하세요.");
            return;
        }

        DiaryDto diary = getInputDiary();
        if (diary == null) {
            return;
        }

        diary.setId(selectedId);

        try {
            if (diaryDao.update(diary)) {
                loadDiaryList();
                clearForm();
                JOptionPane.showMessageDialog(this, "일기를 수정했습니다.");
            }
        } catch (SQLException e) {
            showError("일기 수정에 실패했습니다.", e);
        }
    }

    private void deleteDiary() {
        if (selectedId == null) {
            JOptionPane.showMessageDialog(this, "삭제할 일기를 선택하세요.");
            return;
        }

        int result = JOptionPane.showConfirmDialog(this, "선택한 일기를 삭제할까요?", "삭제 확인", JOptionPane.YES_NO_OPTION);
        if (result != JOptionPane.YES_OPTION) {
            return;
        }

        try {
            if (diaryDao.delete(selectedId)) {
                loadDiaryList();
                clearForm();
                JOptionPane.showMessageDialog(this, "일기를 삭제했습니다.");
            }
        } catch (SQLException e) {
            showError("일기 삭제에 실패했습니다.", e);
        }
    }

    private DiaryDto getInputDiary() {
        String title = titleField.getText().trim();
        String content = contentArea.getText().trim();
        String weather = (String) weatherComboBox.getSelectedItem();

        if (title.isEmpty()) {
            JOptionPane.showMessageDialog(this, "제목을 입력하세요.");
            titleField.requestFocus();
            return null;
        }

        if (content.isEmpty()) {
            JOptionPane.showMessageDialog(this, "일기 내용을 입력하세요.");
            contentArea.requestFocus();
            return null;
        }

        return new DiaryDto(title, content, weather);
    }

    private void clearForm() {
        selectedId = null;
        diaryList.clearSelection();
        titleField.setText("");
        weatherComboBox.setSelectedIndex(0);
        contentArea.setText("");
        dateLabel.setText("새 일기");
        titleField.requestFocus();
    }

    private void showError(String message, Exception e) {
        JOptionPane.showMessageDialog(this, message + "\n" + e.getMessage(), "오류", JOptionPane.ERROR_MESSAGE);
    }

    private static class GradientPanel extends JPanel {
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setPaint(new java.awt.GradientPaint(0, 0, BG_TOP, getWidth(), getHeight(), BG_BOTTOM));
            g2.fillRect(0, 0, getWidth(), getHeight());
            g2.dispose();
        }
    }

    private static class RoundedPanel extends JPanel {
        private final int radius;
        private final Color color;

        RoundedPanel(int radius, Color color) {
            this.radius = radius;
            this.color = color;
            setOpaque(false);
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(color);
            g2.fillRoundRect(0, 0, getWidth(), getHeight(), radius, radius);
            g2.setColor(CARD_BORDER);
            g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, radius, radius);
            g2.dispose();
            super.paintComponent(g);
        }
    }

    private static class ModernButton extends JButton {
        private final Color normalColor;
        private final Color hoverColor;

        ModernButton(String text, Color color, java.awt.event.ActionListener listener) {
            super(text);
            this.normalColor = color;
            this.hoverColor = color.darker();

            setFont(new Font("Malgun Gothic", Font.BOLD, 14));
            setForeground(Color.WHITE);
            setBackground(normalColor);
            setFocusPainted(false);
            setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));
            setCursor(new Cursor(Cursor.HAND_CURSOR));
            addActionListener(listener);
            addMouseListener(new MouseAdapter() {
                @Override
                public void mouseEntered(MouseEvent e) {
                    setBackground(hoverColor);
                }

                @Override
                public void mouseExited(MouseEvent e) {
                    setBackground(normalColor);
                }
            });
        }
    }

    private static class DiaryCellRenderer extends DefaultListCellRenderer {
        @Override
        public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
            JLabel label = (JLabel) super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
            label.setBorder(BorderFactory.createEmptyBorder(8, 10, 8, 10));
            label.setFont(BASIC_FONT);
            label.setOpaque(true);

            if (isSelected) {
                label.setBackground(new Color(0xFCE7F3));
                label.setForeground(PRIMARY_DARK);
            } else {
                label.setBackground(new Color(255, 255, 255, 0));
                label.setForeground(TEXT_COLOR);
            }

            return label;
        }
    }
}
