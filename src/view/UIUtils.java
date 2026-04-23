package view;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;

public class UIUtils {

    // Colors
    public static final Color BG_PAGE       = new Color(245, 245, 245);
    public static final Color WHITE         = Color.WHITE;
    public static final Color BLUE          = new Color(33, 150, 243);
    public static final Color BLUE_LIGHT    = new Color(227, 240, 252);
    public static final Color GREEN         = new Color(76, 175, 80);
    public static final Color RED           = new Color(244, 67, 54);
    public static final Color ORANGE        = new Color(255, 152, 0);
    public static final Color PURPLE        = new Color(156, 39, 176);
    public static final Color BORDER_COLOR  = new Color(225, 225, 225);
    public static final Color TEXT_DARK     = new Color(45, 45, 45);
    public static final Color TEXT_MID      = new Color(70, 70, 70);
    public static final Color TEXT_LIGHT    = new Color(120, 120, 120);
    public static final Color SIDEBAR_BG    = new Color(250, 250, 250);

    // Fonts
    public static final Font F_TITLE   = new Font("Segoe UI", Font.BOLD, 22);
    public static final Font F_SECTION = new Font("Segoe UI", Font.BOLD, 20);
    public static final Font F_NAV     = new Font("Segoe UI", Font.PLAIN, 17);
    public static final Font F_NAV_SEL = new Font("Segoe UI", Font.BOLD, 17);
    public static final Font F_BODY    = new Font("Segoe UI", Font.PLAIN, 18);
    public static final Font F_SMALL   = new Font("Segoe UI", Font.PLAIN, 14);
    public static final Font F_VALUE   = new Font("Segoe UI", Font.BOLD, 22);


    public static JPanel card(int x, int y, int w, int h) {
        JPanel pn = new JPanel(null);
        pn.setBounds(x, y, w, h);
        pn.setBackground(WHITE);
        pn.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER_COLOR, 1),
                BorderFactory.createEmptyBorder(4, 4, 4, 4)
        ));
        return pn;
    }

    public static void label(JPanel parent, String text, Font font, Color color, int x, int y) {
        JLabel l = new JLabel(text);
        l.setFont(font);
        l.setForeground(color);
        l.setBounds(x, y, 500, 30);
        parent.add(l);
    }

    public static void separator(JPanel parent, int x, int y, int width) {
        JSeparator sep = new JSeparator();
        sep.setBounds(x, y, width, 1);
        sep.setForeground(BORDER_COLOR);
        parent.add(sep);
    }

    public static JPanel buildProgressBar(int x, int y, int w, int h, int pct, Color accent) {
        JPanel bar = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                // Background track
                g2.setColor(new Color(220, 222, 226));
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), getHeight(), getHeight());
                // Fill
                int fillW = (int) (getWidth() * pct / 100.0);
                if (fillW > 0) {
                    GradientPaint gp = new GradientPaint(0, 0, accent.brighter(), fillW, 0, accent);
                    g2.setPaint(gp);
                    g2.fillRoundRect(0, 0, fillW, getHeight(), getHeight(), getHeight());
                }
                g2.dispose();
            }
        };
        bar.setBounds(x, y, w, h);
        bar.setOpaque(false);
        return bar;
    }

    public static JButton accentButton(String text, Color color) {
        JButton btn = new JButton(text);
        btn.setFont(F_SMALL);
        btn.setBackground(color);
        btn.setForeground(WHITE);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setPreferredSize(new Dimension(btn.getPreferredSize().width + 10, 32));
        return btn;
    }

    public static JTextField styledTextField(String placeholder, int width) {
        JTextField tf = new JTextField(placeholder);
        tf.setFont(F_SMALL);
        tf.setForeground(TEXT_LIGHT);
        tf.setPreferredSize(new Dimension(width, 32));
        tf.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER_COLOR),
                BorderFactory.createEmptyBorder(2, 8, 2, 8)
        ));
        tf.addFocusListener(new FocusAdapter() {
            public void focusGained(FocusEvent e) {
                if (tf.getText().equals(placeholder)) {
                    tf.setText("");
                    tf.setForeground(TEXT_DARK);
                }
            }
            public void focusLost(FocusEvent e) {
                if (tf.getText().isEmpty()) {
                    tf.setText(placeholder);
                    tf.setForeground(TEXT_LIGHT);
                }
            }
        });
        return tf;
    }

    public static JComboBox<String> styledCombo(String[] items) {
        JComboBox<String> cb = new JComboBox<>(items);
        cb.setFont(F_SMALL);
        cb.setBackground(WHITE);
        cb.setPreferredSize(new Dimension(160, 32));
        return cb;
    }

    public static JScrollPane scrollWrap(JPanel content) {
        JScrollPane sp = new JScrollPane(content);
        sp.setBorder(null);
        sp.setOpaque(false);
        sp.getViewport().setOpaque(false);
        sp.getVerticalScrollBar().setUnitIncrement(16);
        return sp;
    }

    public static JScrollPane buildTable(int x, int y, int w, int h, String[] cols, Object[][] rows) {
        DefaultTableModel model = new DefaultTableModel(rows, cols) {
            public boolean isCellEditable(int r, int c) { return false; }
        };
        JTable table = new JTable(model);
        table.setFont(F_BODY);
        table.setRowHeight(36);
        table.setBackground(WHITE);
        table.setForeground(TEXT_DARK);
        table.setGridColor(BORDER_COLOR);
        table.setShowVerticalLines(false);
        table.setIntercellSpacing(new Dimension(0, 0));
        table.getTableHeader().setFont(F_SMALL);
        table.getTableHeader().setBackground(new Color(245,245,245));
        table.getTableHeader().setForeground(TEXT_LIGHT);
        table.getTableHeader().setBorder(BorderFactory.createMatteBorder(0,0,1,0,BORDER_COLOR));
        table.setSelectionBackground(BLUE_LIGHT);
        table.setSelectionForeground(TEXT_DARK);

        for (int i = 0; i < cols.length; i++) {
            if (cols[i].equals("Amount")) {
                final int col = i;
                table.getColumnModel().getColumn(col).setCellRenderer(new DefaultTableCellRenderer() {
                    public Component getTableCellRendererComponent(JTable t, Object v, boolean s, boolean f, int r, int c) {
                        super.getTableCellRendererComponent(t,v,s,f,r,c);
                        String txt = v == null ? "" : v.toString();
                        setForeground(txt.startsWith("+") ? GREEN : txt.startsWith("−")||txt.startsWith("-") ? RED : TEXT_DARK);
                        return this;
                    }
                });
            }
        }

        JScrollPane sp = new JScrollPane(table);
        sp.setBounds(x, y, w, h);
        sp.setBorder(null);
        sp.getViewport().setBackground(WHITE);
        sp.getVerticalScrollBar().setUnitIncrement(16);
        return sp;
    }

    // Additional chart methods can be added here as needed
}