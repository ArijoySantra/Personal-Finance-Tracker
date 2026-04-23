package view;

import database.ScheduledDAO;
import model.Scheduled;
import model.User;
import utils.CurrencyFormatter;
import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;
import java.time.format.DateTimeFormatter;
import java.util.List;
import database.TransactionDAO;
import model.Transaction;
import java.sql.Date;
import java.time.LocalDate;

public class ScheduledPanel extends JPanel {

    private User currentUser;
    private ScheduledDAO scheduledDAO;
    private JTable table;
    private DefaultTableModel tableModel;
    private List<Scheduled> scheduledList;
    private JLabel totalAmountLabel;

    private static final DateTimeFormatter DATE_FMT = DateTimeFormatter.ofPattern("dd MMM yyyy");

    public ScheduledPanel(User user) {
        this.currentUser = user;
        this.scheduledDAO = new ScheduledDAO();

        setLayout(new BorderLayout(0, 16));
        setBackground(UIUtils.BG_PAGE);
        setBorder(BorderFactory.createEmptyBorder(20, 24, 20, 24));

        add(buildHeader(), BorderLayout.NORTH);
        add(buildTablePanel(), BorderLayout.CENTER);

        refreshData();
    }

    private void processDueSchedules() {
        List<Scheduled> dueSchedules = scheduledDAO.getDueSchedules(currentUser.getId());
        if (dueSchedules.isEmpty()) {
            JOptionPane.showMessageDialog(this, "No schedules due today.");
            return;
        }

        int created = 0;
        TransactionDAO transactionDAO = new TransactionDAO();

        for (Scheduled s : dueSchedules) {
            Transaction t = new Transaction();
            t.setUserId(s.getUserId());
            t.setAccountId(s.getAccountId());
            t.setCardId(s.getCardId());
            t.setCategoryId(s.getCategoryId());
            t.setAmount(s.getAmount());
            t.setType(s.getType());
            t.setDescription(s.getDescription());
            t.setTransactionDate(new Date(System.currentTimeMillis())); // today

            if (transactionDAO.add(t)) {
                created++;
                // Calculate next date based on frequency
                LocalDate next = s.getNextDate().toLocalDate();
                switch (s.getFrequency()) {
                    case "DAILY":   next = next.plusDays(1); break;
                    case "WEEKLY":  next = next.plusWeeks(1); break;
                    case "MONTHLY": next = next.plusMonths(1); break;
                    case "YEARLY":  next = next.plusYears(1); break;
                }
                scheduledDAO.updateNextDate(s.getId(), Date.valueOf(next));
            }
        }

        JOptionPane.showMessageDialog(this,
                String.format("Processed %d due schedule(s). Created %d transaction(s).",
                        dueSchedules.size(), created));
        refreshData();

        Window window = SwingUtilities.getWindowAncestor(this);
        if (window instanceof MainFrame) {
            ((MainFrame) window).refreshBudgetsPanel();
        }
    }

    private JPanel buildHeader() {
        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);

        JLabel title = new JLabel("Scheduled Transactions");
        title.setFont(new Font("Segoe UI", Font.BOLD, 24));
        title.setForeground(UIUtils.TEXT_DARK);

        JPanel actions = new JPanel(new FlowLayout(FlowLayout.RIGHT, 12, 0));
        actions.setOpaque(false);

        JButton addBtn = createModernButton("+ New Schedule", UIUtils.BLUE);
        addBtn.addActionListener(e -> {
            AddEditScheduledDialog dialog = new AddEditScheduledDialog(
                    (JFrame) SwingUtilities.getWindowAncestor(this), currentUser, null);
            dialog.setVisible(true);
            if (dialog.isSaved()) refreshData();
        });

        JButton processDueBtn = createModernButton("Process Due", UIUtils.GREEN);
        processDueBtn.addActionListener(e -> processDueSchedules());
        actions.add(processDueBtn);
        actions.add(addBtn);

        header.add(title, BorderLayout.WEST);
        header.add(actions, BorderLayout.EAST);
        return header;
    }

    private JButton createModernButton(String text, Color bgColor) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btn.setForeground(Color.WHITE);
        btn.setBackground(bgColor);
        btn.setBorder(BorderFactory.createEmptyBorder(10, 18, 10, 18));
        btn.setFocusPainted(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setBorderPainted(false);
        btn.setOpaque(true);

        btn.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(bgColor.darker(), 1, true),
                BorderFactory.createEmptyBorder(9, 17, 9, 17)
        ));

        btn.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                btn.setBackground(bgColor.brighter());
            }
            @Override
            public void mouseExited(MouseEvent e) {
                btn.setBackground(bgColor);
            }
        });

        return btn;
    }

    private JPanel buildTablePanel() {
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(UIUtils.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(UIUtils.BORDER_COLOR, 1),
                BorderFactory.createEmptyBorder(4, 4, 4, 4)
        ));

        JPanel summaryBar = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 8));
        summaryBar.setBackground(UIUtils.WHITE);
        summaryBar.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 1, 0, UIUtils.BORDER_COLOR),
                BorderFactory.createEmptyBorder(8, 18, 8, 18)));
        JLabel lbl = new JLabel("Total Scheduled: ");
        lbl.setFont(UIUtils.F_BODY);
        lbl.setForeground(UIUtils.TEXT_MID);
        totalAmountLabel = new JLabel();
        totalAmountLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        totalAmountLabel.setForeground(UIUtils.BLUE);
        summaryBar.add(lbl);
        summaryBar.add(totalAmountLabel);
        card.add(summaryBar, BorderLayout.NORTH);

        String[] cols = {"Name", "Category", "Frequency", "Next Date", "Amount", "Status", "Actions"};

        tableModel = new DefaultTableModel(cols, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 6;
            }
        };

        table = new JTable(tableModel);
        styleTable();

        table.getColumnModel().getColumn(0).setPreferredWidth(140);
        table.getColumnModel().getColumn(1).setPreferredWidth(130);
        table.getColumnModel().getColumn(2).setPreferredWidth(110);
        table.getColumnModel().getColumn(3).setPreferredWidth(120);
        table.getColumnModel().getColumn(4).setPreferredWidth(100);
        table.getColumnModel().getColumn(5).setPreferredWidth(90);
        table.getColumnModel().getColumn(6).setPreferredWidth(200);

        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(SwingConstants.CENTER);
        for (int i = 0; i < 6; i++) {
            table.getColumnModel().getColumn(i).setCellRenderer(centerRenderer);
        }

        table.getColumnModel().getColumn(4).setCellRenderer(new AmountRenderer());

        table.getColumnModel().getColumn(5).setCellRenderer(new StatusRenderer());

        table.getColumnModel().getColumn(6).setCellRenderer(new ActionsRenderer());
        table.getColumnModel().getColumn(6).setCellEditor(new ActionsEditor());

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.getViewport().setBackground(UIUtils.WHITE);
        card.add(scrollPane, BorderLayout.CENTER);

        return card;
    }

    private void styleTable() {
        table.setFont(UIUtils.F_BODY);
        table.setRowHeight(52);
        table.setBackground(UIUtils.WHITE);
        table.setForeground(UIUtils.TEXT_DARK);
        table.setGridColor(new Color(240, 240, 240));
        table.setShowVerticalLines(false);
        table.setIntercellSpacing(new Dimension(8, 8));
        table.setSelectionBackground(new Color(245, 245, 250));
        table.setSelectionForeground(UIUtils.TEXT_DARK);
        table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 13));
        table.getTableHeader().setBackground(new Color(250, 250, 250));
        table.getTableHeader().setForeground(UIUtils.TEXT_DARK);
        table.getTableHeader().setBorder(BorderFactory.createMatteBorder(0, 0, 2, 0, UIUtils.BORDER_COLOR));
        table.getTableHeader().setPreferredSize(new Dimension(0, 40));
    }

    public void refreshData() {
        scheduledList = scheduledDAO.getAllByUser(currentUser.getId());
        tableModel.setRowCount(0);
        double totalAmount = 0.0;
        for (Scheduled st : scheduledList) {
            totalAmount += st.getAmount();
            String status = "Active"; // default; extend with DB column later
            tableModel.addRow(new Object[]{
                    st.getDescription(),
                    st.getCategoryName() != null ? st.getCategoryName() : "—",
                    st.getFrequency(),
                    st.getNextDate().toLocalDate().format(DATE_FMT),
                    st.getAmount(), // store raw double for AmountRenderer
                    status,
                    "ACTIONS"
            });
        }
        totalAmountLabel.setText(CurrencyFormatter.format(totalAmount));
    }

    private void editItem(int row) {
        Scheduled st = scheduledList.get(row);
        AddEditScheduledDialog dialog = new AddEditScheduledDialog(
                (JFrame) SwingUtilities.getWindowAncestor(this), currentUser, st);
        dialog.setVisible(true);
        if (dialog.isSaved()) refreshData();
    }

    private void togglePause(int row) {
        Scheduled st = scheduledList.get(row);
        String currentStatus = (String) tableModel.getValueAt(row, 5);
        String newStatus = currentStatus.equals("Active") ? "Paused" : "Active";
        tableModel.setValueAt(newStatus, row, 5);
    }

    private void deleteItem(int row) {
        Scheduled st = scheduledList.get(row);
        int confirm = JOptionPane.showConfirmDialog(this,
                "Delete scheduled transaction '" + st.getDescription() + "'?",
                "Confirm Delete", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            if (scheduledDAO.delete(st.getId())) {
                refreshData();
            } else {
                JOptionPane.showMessageDialog(this, "Delete failed.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private static class AmountRenderer extends DefaultTableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value,
                                                       boolean isSelected, boolean hasFocus, int row, int column) {
            JLabel label = (JLabel) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            label.setHorizontalAlignment(SwingConstants.CENTER);
            if (value instanceof Number) {
                double val = ((Number) value).doubleValue();
                label.setText(CurrencyFormatter.format(val));
                label.setForeground(val >= 0 ? UIUtils.TEXT_DARK : UIUtils.RED);
            } else {
                label.setText(value != null ? value.toString() : "");
            }
            return label;
        }
    }

    private static class StatusRenderer extends DefaultTableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value,
                                                       boolean isSelected, boolean hasFocus, int row, int column) {
            JLabel label = (JLabel) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            label.setHorizontalAlignment(SwingConstants.CENTER);
            String status = value.toString();
            if (status.equals("Active")) {
                label.setBackground(new Color(230, 245, 230));
                label.setForeground(new Color(30, 150, 30));
                label.setText("● Active");
            } else {
                label.setBackground(new Color(255, 245, 230));
                label.setForeground(new Color(200, 120, 20));
                label.setText("○ Paused");
            }
            label.setFont(new Font("Segoe UI", Font.BOLD, 12));
            label.setOpaque(true);
            label.setBorder(BorderFactory.createEmptyBorder(4, 12, 4, 12));
            return label;
        }
    }

    private class ActionsRenderer extends JPanel implements TableCellRenderer {
        private final JButton editBtn = new JButton("Edit");
        private final JButton pauseBtn = new JButton();
        private final JButton deleteBtn = new JButton("Delete");

        public ActionsRenderer() {
            setLayout(new FlowLayout(FlowLayout.CENTER, 6, 8));
            setOpaque(true);
            styleActionButton(editBtn, UIUtils.TEXT_DARK);
            styleActionButton(pauseBtn);
            styleActionButton(deleteBtn, UIUtils.RED);
            add(editBtn);
            add(pauseBtn);
            add(deleteBtn);
        }

        private void styleActionButton(JButton btn, Color color) {
            btn.setFont(new Font("Segoe UI", Font.PLAIN, 12));
            btn.setForeground(color);
            btn.setFocusPainted(false);
            btn.setBorderPainted(false);
            btn.setContentAreaFilled(false);
            btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        }

        private void styleActionButton(JButton btn) {
            btn.setFont(new Font("Segoe UI", Font.PLAIN, 12));
            btn.setFocusPainted(false);
            btn.setBorderPainted(false);
            btn.setContentAreaFilled(false);
            btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        }

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value,
                                                       boolean isSelected, boolean hasFocus, int row, int column) {
            String status = (String) table.getValueAt(row, 5);
            pauseBtn.setText(status.equals("Active") ? "Pause" : "Resume");
            pauseBtn.setForeground(status.equals("Active") ? UIUtils.ORANGE : UIUtils.BLUE);
            setBackground(isSelected ? new Color(245, 245, 250) : UIUtils.WHITE);
            return this;
        }
    }

    private class ActionsEditor extends AbstractCellEditor implements TableCellEditor {
        private final JPanel panel;
        private final JButton editBtn;
        private final JButton pauseBtn;
        private final JButton deleteBtn;
        private int currentRow;

        public ActionsEditor() {
            panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 6, 8));
            panel.setOpaque(true);
            editBtn = new JButton("Edit");
            pauseBtn = new JButton();
            deleteBtn = new JButton("Delete");
            styleActionButton(editBtn, UIUtils.TEXT_DARK);
            styleActionButton(pauseBtn);
            styleActionButton(deleteBtn, UIUtils.RED);

            editBtn.addActionListener(e -> {
                editItem(currentRow);
                fireEditingStopped();
            });

            pauseBtn.addActionListener(e -> {
                togglePause(currentRow);
                fireEditingStopped();
            });

            deleteBtn.addActionListener(e -> {
                deleteItem(currentRow);
                fireEditingStopped();
            });

            panel.add(editBtn);
            panel.add(pauseBtn);
            panel.add(deleteBtn);
        }

        private void styleActionButton(JButton btn, Color color) {
            btn.setFont(new Font("Segoe UI", Font.PLAIN, 12));
            btn.setForeground(color);
            btn.setFocusPainted(false);
            btn.setBorderPainted(false);
            btn.setContentAreaFilled(false);
            btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        }

        private void styleActionButton(JButton btn) {
            btn.setFont(new Font("Segoe UI", Font.PLAIN, 12));
            btn.setFocusPainted(false);
            btn.setBorderPainted(false);
            btn.setContentAreaFilled(false);
            btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        }

        @Override
        public Component getTableCellEditorComponent(JTable table, Object value,
                                                     boolean isSelected, int row, int column) {
            currentRow = row;
            String status = (String) table.getValueAt(row, 5);
            pauseBtn.setText(status.equals("Active") ? "Pause" : "Resume");
            pauseBtn.setForeground(status.equals("Active") ? UIUtils.ORANGE : UIUtils.BLUE);
            panel.setBackground(isSelected ? new Color(245, 245, 250) : UIUtils.WHITE);
            return panel;
        }

        @Override
        public Object getCellEditorValue() {
            return "ACTIONS";
        }
    }
}