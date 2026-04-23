package view;

import database.*;
import model.*;
import utils.CurrencyFormatter;
import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;
import java.time.*;
import java.time.format.TextStyle;
import java.util.*;
import java.util.List;

public class CalendarPanel extends JPanel {

    private User currentUser;
    private YearMonth currentYearMonth;
    private JLabel monthYearLabel;
    private JPanel calendarGridPanel;
    private JPanel dayDetailPanel;
    private JLabel selectedDateLabel;
    private JLabel dayIncomeLabel, dayExpenseLabel;
    private JPanel transactionListPanel;

    private TransactionDAO transactionDAO = new TransactionDAO();
    private ScheduledDAO scheduledDAO = new ScheduledDAO();

    private Map<LocalDate, List<CalendarEvent>> monthEvents = new HashMap<>();
    private LocalDate selectedDate = null;

    // Modern color palette
    private static final Color INCOME_GREEN = new Color(46, 160, 67);
    private static final Color EXPENSE_RED = new Color(211, 47, 47);
    private static final Color SCHEDULED_ORANGE = new Color(237, 108, 0);
    private static final Color TODAY_BG = new Color(227, 242, 253);
    private static final Color SELECTED_BORDER = new Color(25, 118, 210);
    private static final Color HOVER_BG = new Color(250, 250, 250);
    private static final Color CELL_BORDER = new Color(230, 230, 230);

    public CalendarPanel(User user) {
        this.currentUser = user;
        this.currentYearMonth = YearMonth.now();
        this.selectedDate = LocalDate.now();

        setLayout(new BorderLayout(0, 12));
        setBackground(UIUtils.BG_PAGE);
        setBorder(BorderFactory.createEmptyBorder(20, 24, 20, 24));

        // Top navigation
        add(buildNavigationPanel(), BorderLayout.NORTH);

        // Calendar grid (center) with square cell wrapper
        JPanel calendarWrapper = new JPanel(new GridBagLayout());
        calendarWrapper.setOpaque(false);
        calendarGridPanel = new JPanel(new GridLayout(0, 7, 8, 8));
        calendarGridPanel.setBackground(UIUtils.BG_PAGE);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        calendarWrapper.add(calendarGridPanel, gbc);
        add(calendarWrapper, BorderLayout.CENTER);

        // Day detail panel (south)
        dayDetailPanel = buildDayDetailPanel();
        add(dayDetailPanel, BorderLayout.SOUTH);

        loadMonthEvents();
        refreshCalendar();
    }

    private JPanel buildNavigationPanel() {
        JPanel nav = new JPanel(new GridBagLayout());
        nav.setOpaque(false);
        nav.setBorder(BorderFactory.createEmptyBorder(0, 0, 16, 0));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(0, 8, 0, 8);
        gbc.fill = GridBagConstraints.NONE;
        gbc.weightx = 0;
        gbc.gridy = 0;

        // Previous button
        JButton prev = createNavButton("←");
        prev.addActionListener(e -> {
            currentYearMonth = currentYearMonth.minusMonths(1);
            loadMonthEvents();
            refreshCalendar();
        });
        gbc.gridx = 0;
        gbc.anchor = GridBagConstraints.WEST;
        nav.add(prev, gbc);

        // Month/Year label (center, expands)
        monthYearLabel = new JLabel("", SwingConstants.CENTER);
        monthYearLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        monthYearLabel.setForeground(UIUtils.TEXT_DARK);
        gbc.gridx = 1;
        gbc.weightx = 1.0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.CENTER;
        nav.add(monthYearLabel, gbc);

        // Next button
        JButton next = createNavButton("→");
        next.addActionListener(e -> {
            currentYearMonth = currentYearMonth.plusMonths(1);
            loadMonthEvents();
            refreshCalendar();
        });
        gbc.gridx = 2;
        gbc.weightx = 0;
        gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.EAST;
        nav.add(next, gbc);

        // Today button
        JButton today = createPillButton("Today");
        today.addActionListener(e -> {
            currentYearMonth = YearMonth.now();
            selectedDate = LocalDate.now();
            loadMonthEvents();
            refreshCalendar();
        });
        gbc.gridx = 3;
        gbc.anchor = GridBagConstraints.EAST;
        nav.add(today, gbc);

        return nav;
    }

    private JButton createNavButton(String text) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 20));
        btn.setForeground(UIUtils.TEXT_DARK);
        btn.setContentAreaFilled(false);
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return btn;
    }

    private JButton createPillButton(String text) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        btn.setForeground(UIUtils.BLUE);
        btn.setBackground(UIUtils.WHITE);
        btn.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(UIUtils.BORDER_COLOR, 1, true),
                BorderFactory.createEmptyBorder(6, 16, 6, 16)));
        btn.setFocusPainted(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setOpaque(true);
        btn.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) { btn.setBackground(new Color(245, 245, 245)); }
            public void mouseExited(MouseEvent e) { btn.setBackground(UIUtils.WHITE); }
        });
        return btn;
    }

    private JPanel buildDayDetailPanel() {
        JPanel panel = new RoundedPanel(16, UIUtils.WHITE);
        panel.setLayout(new BorderLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(16, 20, 16, 20));

        // Header with date and totals
        JPanel header = new JPanel(new GridBagLayout());
        header.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(4, 4, 4, 16);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;

        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 3;
        selectedDateLabel = new JLabel();
        selectedDateLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        selectedDateLabel.setForeground(UIUtils.TEXT_DARK);
        header.add(selectedDateLabel, gbc);

        gbc.gridwidth = 1;
        gbc.gridy = 1; gbc.gridx = 0;
        header.add(new JLabel("Income"), gbc);
        gbc.gridx = 1;
        dayIncomeLabel = new JLabel();
        dayIncomeLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
        dayIncomeLabel.setForeground(INCOME_GREEN);
        header.add(dayIncomeLabel, gbc);

        gbc.gridy = 2; gbc.gridx = 0;
        header.add(new JLabel("Expenses"), gbc);
        gbc.gridx = 1;
        dayExpenseLabel = new JLabel();
        dayExpenseLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
        dayExpenseLabel.setForeground(EXPENSE_RED);
        header.add(dayExpenseLabel, gbc);

        // Add a spacer to push the transaction list down
        gbc.gridy = 3; gbc.gridx = 0; gbc.weighty = 1.0;
        header.add(Box.createVerticalStrut(8), gbc);

        panel.add(header, BorderLayout.NORTH);

        // Separator
        JSeparator sep = new JSeparator();
        sep.setForeground(UIUtils.BORDER_COLOR);
        panel.add(sep, BorderLayout.CENTER);

        // Transaction list
        transactionListPanel = new JPanel();
        transactionListPanel.setLayout(new BoxLayout(transactionListPanel, BoxLayout.Y_AXIS));
        transactionListPanel.setOpaque(false);
        JScrollPane scroll = new JScrollPane(transactionListPanel);
        scroll.setBorder(BorderFactory.createEmptyBorder(8, 0, 0, 0));
        scroll.getViewport().setOpaque(false);
        scroll.setOpaque(false);
        scroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scroll.getVerticalScrollBar().setUnitIncrement(16);
        panel.add(scroll, BorderLayout.SOUTH);

        return panel;
    }

    private void loadMonthEvents() {
        monthEvents.clear();
        LocalDate start = currentYearMonth.atDay(1);
        LocalDate end = currentYearMonth.atEndOfMonth();

        // Transactions
        List<Transaction> txns = transactionDAO.getByUserAndMonth(currentUser.getId(), currentYearMonth);
        for (Transaction t : txns) {
            LocalDate date = t.getTransactionDate().toLocalDate();
            monthEvents.computeIfAbsent(date, k -> new ArrayList<>())
                    .add(new CalendarEvent(
                            t.getDescription(),
                            t.getAmount(),
                            t.getType().equals("INCOME") ? "I" : "E",
                            date,
                            t
                    ));
        }

        // Scheduled
        List<Scheduled> scheduled = scheduledDAO.getAllByUser(currentUser.getId());
        for (Scheduled s : scheduled) {
            LocalDate next = s.getNextDate().toLocalDate();
            if (!next.isBefore(start) && !next.isAfter(end)) {
                monthEvents.computeIfAbsent(next, k -> new ArrayList<>())
                        .add(new CalendarEvent(
                                s.getDescription(),
                                s.getAmount(),
                                "S",
                                next,
                                s
                        ));
            }
        }
    }

    private void refreshCalendar() {
        monthYearLabel.setText(currentYearMonth.getMonth().getDisplayName(TextStyle.FULL, Locale.US) +
                " " + currentYearMonth.getYear());

        calendarGridPanel.removeAll();
        buildCalendarGrid();
        updateDayDetailPanel();

        revalidate();
        repaint();
    }

    private void buildCalendarGrid() {
        LocalDate firstOfMonth = currentYearMonth.atDay(1);
        int daysInMonth = currentYearMonth.lengthOfMonth();
        int firstDayValue = firstOfMonth.getDayOfWeek().getValue(); // 1=Mon, 7=Sun
        int startOffset = (firstDayValue % 7); // 0=Sun, 1=Mon, ... 6=Sat

        // Day headers
        String[] dayNames = {"Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat"};
        for (String day : dayNames) {
            JLabel header = new JLabel(day, SwingConstants.CENTER);
            header.setFont(new Font("Segoe UI", Font.BOLD, 13));
            header.setForeground(day.equals("Sun") ? EXPENSE_RED : UIUtils.TEXT_LIGHT);
            header.setOpaque(false);
            calendarGridPanel.add(header);
        }

        // Empty cells before first day
        for (int i = 0; i < startOffset; i++) {
            calendarGridPanel.add(new JPanel() {{ setOpaque(false); }});
        }

        LocalDate today = LocalDate.now();

        for (int day = 1; day <= daysInMonth; day++) {
            LocalDate date = currentYearMonth.atDay(day);
            JPanel cell = createDayCell(date, day);
            if (date.equals(today)) {
                cell.setBackground(TODAY_BG);
            }
            if (date.equals(selectedDate)) {
                cell.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(SELECTED_BORDER, 2),
                        BorderFactory.createEmptyBorder(4, 4, 4, 4)));
            }
            calendarGridPanel.add(cell);
        }

        // Fill remaining cells to complete 6 rows (42 total)
        int totalCells = 7 * 6;
        int cellsAdded = 7 + startOffset + daysInMonth;
        for (int i = cellsAdded; i < totalCells; i++) {
            calendarGridPanel.add(new JPanel() {{ setOpaque(false); }});
        }
    }

    private JPanel createDayCell(LocalDate date, int day) {
        JPanel cell = new JPanel(new BorderLayout()) {
            private boolean hovered = false;
            {
                addMouseListener(new MouseAdapter() {
                    public void mouseEntered(MouseEvent e) { hovered = true; repaint(); }
                    public void mouseExited(MouseEvent e) { hovered = false; repaint(); }
                });
            }
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(getBackground());
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 12, 12);
                if (hovered && !date.equals(selectedDate)) {
                    g2.setColor(HOVER_BG);
                    g2.fillRoundRect(0, 0, getWidth(), getHeight(), 12, 12);
                }
                g2.setColor(CELL_BORDER);
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 12, 12);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        cell.setOpaque(false);
        cell.setBackground(UIUtils.WHITE);
        cell.setBorder(BorderFactory.createEmptyBorder(8, 6, 6, 6));
        cell.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        cell.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent evt) {
                selectedDate = date;
                refreshCalendar();
            }
        });

        JLabel dayLabel = new JLabel(String.valueOf(day), SwingConstants.CENTER);
        dayLabel.setFont(new Font("Segoe UI", Font.BOLD, 15));
        dayLabel.setForeground(UIUtils.TEXT_DARK);
        cell.add(dayLabel, BorderLayout.NORTH);

        JPanel eventPanel = new JPanel();
        eventPanel.setLayout(new BoxLayout(eventPanel, BoxLayout.Y_AXIS));
        eventPanel.setOpaque(false);

        List<CalendarEvent> events = monthEvents.get(date);
        if (events != null && !events.isEmpty()) {
            // Show up to 3 events as small colored pills
            int count = 0;
            for (CalendarEvent ev : events) {
                if (count++ >= 3) break;
                JPanel pill = new JPanel(new FlowLayout(FlowLayout.LEFT, 4, 0));
                pill.setOpaque(false);
                JLabel dot = new JLabel("●");
                dot.setFont(new Font("Segoe UI", Font.PLAIN, 12));
                dot.setForeground(getEventColor(ev.type));
                pill.add(dot);

                JLabel desc = new JLabel(ev.description.length() > 8 ? ev.description.substring(0, 6) + "…" : ev.description);
                desc.setFont(new Font("Segoe UI", Font.PLAIN, 10));
                desc.setForeground(UIUtils.TEXT_DARK);
                pill.add(desc);

                eventPanel.add(pill);
            }

            // Net amount with dynamic currency
            double net = events.stream()
                    .mapToDouble(e -> e.type.equals("I") ? e.amount : (e.type.equals("E") ? -e.amount : 0))
                    .sum();
            if (net != 0) {
                JLabel netLabel = new JLabel(CurrencyFormatter.format(net), SwingConstants.CENTER);
                netLabel.setFont(new Font("Segoe UI", Font.BOLD, 11));
                netLabel.setForeground(net > 0 ? INCOME_GREEN : EXPENSE_RED);
                netLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
                eventPanel.add(Box.createVerticalStrut(4));
                eventPanel.add(netLabel);
            }
        }
        cell.add(eventPanel, BorderLayout.CENTER);
        return cell;
    }

    private void updateDayDetailPanel() {
        selectedDateLabel.setText(selectedDate.format(java.time.format.DateTimeFormatter.ofPattern("EEEE, d MMMM yyyy")));

        List<CalendarEvent> events = monthEvents.getOrDefault(selectedDate, new ArrayList<>());
        double income = events.stream().filter(e -> e.type.equals("I")).mapToDouble(e -> e.amount).sum();
        double expense = events.stream().filter(e -> e.type.equals("E")).mapToDouble(e -> e.amount).sum();

        dayIncomeLabel.setText(CurrencyFormatter.format(income));
        dayExpenseLabel.setText("-" + CurrencyFormatter.format(expense));

        transactionListPanel.removeAll();
        if (events.isEmpty()) {
            transactionListPanel.add(Box.createVerticalStrut(20));
            JLabel empty = new JLabel("No transactions this day");
            empty.setFont(UIUtils.F_SMALL);
            empty.setForeground(UIUtils.TEXT_LIGHT);
            empty.setAlignmentX(Component.CENTER_ALIGNMENT);
            transactionListPanel.add(empty);
            transactionListPanel.add(Box.createVerticalGlue());
        } else {
            for (CalendarEvent ev : events) {
                transactionListPanel.add(createTransactionRow(ev));
                transactionListPanel.add(Box.createRigidArea(new Dimension(0, 6)));
            }
        }
        transactionListPanel.revalidate();
        transactionListPanel.repaint();
    }

    private JPanel createTransactionRow(CalendarEvent ev) {
        JPanel row = new JPanel(new BorderLayout(10, 0));
        row.setOpaque(false);
        row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 32));

        JLabel desc = new JLabel(ev.description);
        desc.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        desc.setForeground(UIUtils.TEXT_DARK);

        String amtStr = (ev.type.equals("I") ? "+" : (ev.type.equals("E") ? "-" : "")) + CurrencyFormatter.format(ev.amount);
        JLabel amt = new JLabel(amtStr);
        amt.setFont(new Font("Segoe UI", Font.BOLD, 14));
        amt.setForeground(getEventColor(ev.type));

        row.add(desc, BorderLayout.WEST);
        row.add(amt, BorderLayout.EAST);
        return row;
    }

    private Color getEventColor(String type) {
        switch (type) {
            case "I": return INCOME_GREEN;
            case "E": return EXPENSE_RED;
            case "S": return SCHEDULED_ORANGE;
            default: return UIUtils.TEXT_DARK;
        }
    }

    private static class RoundedPanel extends JPanel {
        private final int radius;
        private final Color bg;
        RoundedPanel(int radius, Color bg) {
            this.radius = radius;
            this.bg = bg;
            setOpaque(false);
        }
        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(new Color(0, 0, 0, 15));
            g2.fillRoundRect(2, 4, getWidth() - 4, getHeight() - 4, radius, radius);
            g2.setColor(bg);
            g2.fillRoundRect(0, 0, getWidth() - 2, getHeight() - 2, radius, radius);
            g2.dispose();
            super.paintComponent(g);
        }
    }

    private static class CalendarEvent {
        String description;
        double amount;
        String type;
        LocalDate date;
        Object source;

        CalendarEvent(String desc, double amt, String type, LocalDate date, Object source) {
            this.description = desc;
            this.amount = amt;
            this.type = type;
            this.date = date;
            this.source = source;
        }
    }

    public void refreshData() {
        loadMonthEvents();
        refreshCalendar();
    }
}