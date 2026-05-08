package diary;

import diary.ui.DiaryFrame;

import javax.swing.SwingUtilities;

public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(DiaryFrame::new);
    }
}
