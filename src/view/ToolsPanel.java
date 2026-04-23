package view;

import database.*;
import model.*;
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.sql.Date;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.*;
import java.util.List;

public class ToolsPanel extends JPanel {

    private User currentUser;
    private TransactionDAO transactionDAO;
    private AccountDAO accountDAO;
    private CategoryDAO categoryDAO;
    private CardDAO cardDAO;
    private JLabel statusLabel;
    private JProgressBar progressBar;

    private Runnable onDataChanged;

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    public ToolsPanel(User user) {
        this.currentUser = user;
        this.transactionDAO = new TransactionDAO();
        this.accountDAO = new AccountDAO();
        this.categoryDAO = new CategoryDAO();
        this.cardDAO = new CardDAO();

        setLayout(new BorderLayout(20, 20));
        setBackground(UIUtils.BG_PAGE);
        setBorder(BorderFactory.createEmptyBorder(30, 40, 30, 40));

        JLabel header = new JLabel("Import & Export Tools", SwingConstants.CENTER);
        header.setFont(new Font("Segoe UI", Font.BOLD, 28));
        header.setForeground(UIUtils.TEXT_DARK);
        header.setBorder(BorderFactory.createEmptyBorder(0, 0, 20, 0));
        add(header, BorderLayout.NORTH);

        JPanel centerPanel = new JPanel(new GridLayout(1, 2, 30, 0));
        centerPanel.setOpaque(false);
        centerPanel.add(buildExportCard());
        centerPanel.add(buildImportCard());
        add(centerPanel, BorderLayout.CENTER);

        JPanel bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.setOpaque(false);
        bottomPanel.setBorder(BorderFactory.createEmptyBorder(20, 0, 0, 0));

        statusLabel = new JLabel("Ready");
        statusLabel.setFont(UIUtils.F_SMALL);
        statusLabel.setForeground(UIUtils.TEXT_LIGHT);

        progressBar = new JProgressBar();
        progressBar.setVisible(false);
        progressBar.setPreferredSize(new Dimension(200, 20));

        bottomPanel.add(statusLabel, BorderLayout.WEST);
        bottomPanel.add(progressBar, BorderLayout.EAST);
        add(bottomPanel, BorderLayout.SOUTH);
    }

    public void setOnDataChanged(Runnable callback) {
        this.onDataChanged = callback;
    }

    private JPanel buildExportCard() {
        JPanel card = new JPanel();
        card.setBackground(UIUtils.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(UIUtils.BORDER_COLOR, 1, true),
                BorderFactory.createEmptyBorder(30, 25, 30, 25)
        ));
        card.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 0, 5, 0);

        JLabel title = new JLabel("Export Data", SwingConstants.CENTER);
        title.setFont(new Font("Segoe UI", Font.BOLD, 20));
        title.setForeground(UIUtils.TEXT_DARK);
        card.add(title, gbc);

        gbc.gridy++;
        JLabel desc = new JLabel("<html><center>Export your transactions to a CSV file.<br>You can open it in Excel or use it as a backup.</center></html>", SwingConstants.CENTER);
        desc.setFont(UIUtils.F_SMALL);
        desc.setForeground(UIUtils.TEXT_LIGHT);
        desc.setBorder(BorderFactory.createEmptyBorder(15, 0, 25, 0));
        card.add(desc, gbc);

        gbc.gridy++;
        gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.CENTER;
        JButton exportBtn = UIUtils.accentButton("Export Transactions (CSV)", UIUtils.BLUE);
        exportBtn.addActionListener(e -> exportTransactions());
        card.add(exportBtn, gbc);

        return card;
    }

    private JPanel buildImportCard() {
        JPanel card = new JPanel();
        card.setBackground(UIUtils.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(UIUtils.BORDER_COLOR, 1, true),
                BorderFactory.createEmptyBorder(30, 25, 30, 25)
        ));
        card.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 0, 5, 0);

        JLabel title = new JLabel("Import Data", SwingConstants.CENTER);
        title.setFont(new Font("Segoe UI", Font.BOLD, 20));
        title.setForeground(UIUtils.TEXT_DARK);
        card.add(title, gbc);

        gbc.gridy++;
        JLabel desc = new JLabel("<html><center>Import transactions from a CSV file.<br>Preview and confirm before importing.</center></html>", SwingConstants.CENTER);
        desc.setFont(UIUtils.F_SMALL);
        desc.setForeground(UIUtils.TEXT_LIGHT);
        desc.setBorder(BorderFactory.createEmptyBorder(15, 0, 25, 0));
        card.add(desc, gbc);

        gbc.gridy++;
        gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.CENTER;
        JButton importBtn = UIUtils.accentButton("Import Transactions (CSV)", UIUtils.GREEN);
        importBtn.addActionListener(e -> importTransactions());
        card.add(importBtn, gbc);

        return card;
    }

    private void exportTransactions() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Save Transactions CSV");
        fileChooser.setSelectedFile(new File("transactions_export.csv"));
        FileNameExtensionFilter filter = new FileNameExtensionFilter("CSV Files", "csv");
        fileChooser.setFileFilter(filter);

        if (fileChooser.showSaveDialog(this) != JFileChooser.APPROVE_OPTION) {
            return;
        }

        File selectedFile = fileChooser.getSelectedFile();
        if (!selectedFile.getName().toLowerCase().endsWith(".csv")) {
            selectedFile = new File(selectedFile.getAbsolutePath() + ".csv");
        }
        final File file = selectedFile;

        setStatus("Exporting transactions...", true);
        progressBar.setIndeterminate(true);

        SwingWorker<Boolean, Void> worker = new SwingWorker<>() {
            @Override
            protected Boolean doInBackground() {
                try {
                    List<Transaction> transactions = getAllTransactionsForUser(currentUser.getId());
                    try (PrintWriter writer = new PrintWriter(new OutputStreamWriter(
                            new FileOutputStream(file), StandardCharsets.UTF_8))) {
                        writer.println("Date,Type,Category,Amount,Description,Account,Card");
                        for (Transaction t : transactions) {
                            writer.printf("%s,%s,%s,%.2f,\"%s\",%s,%s%n",
                                    t.getTransactionDate().toLocalDate().format(DATE_FORMATTER),
                                    t.getType(),
                                    t.getCategoryName() != null ? escapeCsv(t.getCategoryName()) : "",
                                    t.getAmount(),
                                    t.getDescription() != null ? escapeCsv(t.getDescription()) : "",
                                    t.getAccountName() != null ? escapeCsv(t.getAccountName()) : "",
                                    t.getCardName() != null ? escapeCsv(t.getCardName()) : "");
                        }
                    }
                    return true;
                } catch (Exception ex) {
                    ex.printStackTrace();
                    return false;
                }
            }

            @Override
            protected void done() {
                progressBar.setIndeterminate(false);
                progressBar.setVisible(false);
                try {
                    if (get()) {
                        setStatus("Export completed: " + file.getName(), false);
                        JOptionPane.showMessageDialog(ToolsPanel.this,
                                "Export successful.", "Export", JOptionPane.INFORMATION_MESSAGE);
                    } else {
                        setStatus("Export failed.", false);
                        JOptionPane.showMessageDialog(ToolsPanel.this,
                                "Failed to export transactions.", "Export Error", JOptionPane.ERROR_MESSAGE);
                    }
                } catch (Exception ex) {
                    setStatus("Export error.", false);
                }
            }
        };
        worker.execute();
    }

    private String escapeCsv(String s) {
        if (s.contains(",") || s.contains("\"")) {
            return "\"" + s.replace("\"", "\"\"") + "\"";
        }
        return s;
    }

    private List<Transaction> getAllTransactionsForUser(int userId) {
        List<Transaction> all = new ArrayList<>();
        int currentYear = YearMonth.now().getYear();
        for (int year = currentYear - 10; year <= currentYear; year++) {
            for (int month = 1; month <= 12; month++) {
                YearMonth ym = YearMonth.of(year, month);
                all.addAll(transactionDAO.getByUserAndMonth(userId, ym));
            }
        }
        return all;
    }

    private void importTransactions() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Select Transactions CSV");
        FileNameExtensionFilter filter = new FileNameExtensionFilter("CSV Files", "csv");
        fileChooser.setFileFilter(filter);

        if (fileChooser.showOpenDialog(this) != JFileChooser.APPROVE_OPTION) {
            return;
        }

        final File file = fileChooser.getSelectedFile();

        ImportPreview preview = parseCSVPreview(file);
        if (preview == null) {
            JOptionPane.showMessageDialog(this, "Unable to read CSV file.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        PreviewDialog previewDialog = new PreviewDialog((JFrame) SwingUtilities.getWindowAncestor(this), preview);
        previewDialog.setVisible(true);
        if (!previewDialog.isConfirmed()) {
            return;
        }

        setStatus("Importing transactions...", true);
        progressBar.setIndeterminate(true);

        SwingWorker<ImportResult, Void> worker = new SwingWorker<>() {
            @Override
            protected ImportResult doInBackground() {
                ImportResult result = new ImportResult();
                Map<String, Integer> categoryCache = new HashMap<>();
                Map<String, Integer> accountCache = new HashMap<>();
                Map<String, Integer> cardCache = new HashMap<>();
                Set<String> duplicates = new HashSet<>();

                List<Transaction> existing = getAllTransactionsForUser(currentUser.getId());
                for (Transaction t : existing) {
                    duplicates.add(generateDuplicateKey(t));
                }

                for (String[] row : preview.rows) {
                    try {
                        String dateStr = getColumn(row, preview.columnIndex, "date");
                        String typeStr = getColumn(row, preview.columnIndex, "type");
                        String categoryName = getColumn(row, preview.columnIndex, "category");
                        String amountStr = getColumn(row, preview.columnIndex, "amount");
                        String description = getColumn(row, preview.columnIndex, "description");
                        String accountName = getColumn(row, preview.columnIndex, "account");
                        String cardName = getColumn(row, preview.columnIndex, "card");

                        if (dateStr == null || typeStr == null || amountStr == null) {
                            result.skippedInvalidFormat++;
                            continue;
                        }

                        LocalDate date;
                        try {
                            date = LocalDate.parse(dateStr.trim(), DATE_FORMATTER);
                        } catch (DateTimeParseException e) {
                            result.skippedInvalidFormat++;
                            continue;
                        }

                        String type = typeStr.trim().toUpperCase();
                        if (!type.equals("INCOME") && !type.equals("EXPENSE")) {
                            result.skippedInvalidFormat++;
                            continue;
                        }

                        double amount;
                        try {
                            amount = Double.parseDouble(amountStr.trim());
                            if (amount <= 0) throw new NumberFormatException();
                        } catch (NumberFormatException e) {
                            result.skippedInvalidFormat++;
                            continue;
                        }

                        int categoryId = -1;
                        if (categoryName != null && !categoryName.isEmpty()) {
                            categoryId = categoryCache.computeIfAbsent(categoryName, name -> {
                                Category cat = categoryDAO.findByNameAndType(name, "EXPENSE", currentUser.getId());
                                if (cat == null && type.equals("INCOME")) {
                                    cat = categoryDAO.findByNameAndType(name, "INCOME", currentUser.getId());
                                }
                                if (cat == null) {
                                    Category newCat = new Category();
                                    newCat.setUserId(null);
                                    newCat.setName(name);
                                    newCat.setType(type.equals("INCOME") ? "INCOME" : "EXPENSE");
                                    if (categoryDAO.addCategory(newCat)) {
                                        cat = categoryDAO.findByNameAndType(name, newCat.getType(), currentUser.getId());
                                    }
                                }
                                return cat != null ? cat.getId() : -1;
                            });
                        }

                        Integer accountId = null;
                        if (accountName != null && !accountName.isEmpty()) {
                            accountId = accountCache.computeIfAbsent(accountName, name -> {
                                List<Account> accounts = accountDAO.getAccountsByUser(currentUser.getId());
                                Optional<Account> match = accounts.stream()
                                        .filter(a -> a.getAccountName().equalsIgnoreCase(name))
                                        .findFirst();
                                return match.map(Account::getId).orElse(null);
                            });
                        }

                        Integer cardId = null;
                        if (cardName != null && !cardName.isEmpty()) {
                            cardId = cardCache.computeIfAbsent(cardName, name -> {
                                List<Card> cards = cardDAO.getCardsByUser(currentUser.getId());
                                Optional<Card> match = cards.stream()
                                        .filter(c -> c.getCardName().equalsIgnoreCase(name))
                                        .findFirst();
                                return match.map(Card::getId).orElse(null);
                            });
                        }

                        Transaction t = new Transaction();
                        t.setUserId(currentUser.getId());
                        t.setTransactionDate(Date.valueOf(date));
                        t.setType(type);
                        t.setAmount(amount);
                        t.setDescription(description != null ? description : "");
                        if (categoryId != -1) t.setCategoryId(categoryId);
                        t.setAccountId(accountId);
                        t.setCardId(cardId);

                        String dupKey = generateDuplicateKey(t);
                        if (duplicates.contains(dupKey)) {
                            result.skippedDuplicates++;
                            continue;
                        }

                        if (transactionDAO.add(t)) {
                            result.imported++;
                            duplicates.add(dupKey);
                        } else {
                            result.skippedErrors++;
                        }
                    } catch (Exception e) {
                        result.skippedErrors++;
                    }
                }
                return result;
            }

            @Override
            protected void done() {
                progressBar.setIndeterminate(false);
                progressBar.setVisible(false);
                try {
                    ImportResult result = get();
                    setStatus("Import completed.", false);
                    String message = String.format(
                            "Imported: %d\nSkipped (duplicates): %d\nSkipped (invalid format): %d\nErrors: %d",
                            result.imported, result.skippedDuplicates, result.skippedInvalidFormat, result.skippedErrors);
                    JOptionPane.showMessageDialog(ToolsPanel.this, message, "Import Summary", JOptionPane.INFORMATION_MESSAGE);

                    if (onDataChanged != null) {
                        onDataChanged.run();
                    }
                } catch (Exception ex) {
                    setStatus("Import error.", false);
                }
            }
        };
        worker.execute();
    }

    private String generateDuplicateKey(Transaction t) {
        return t.getTransactionDate().toString() + "|" + t.getAmount() + "|" + t.getDescription() + "|" + t.getAccountId();
    }

    private ImportPreview parseCSVPreview(File file) {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(file), StandardCharsets.UTF_8))) {
            String headerLine = reader.readLine();
            if (headerLine == null) return null;
            String[] headers = headerLine.split(",");
            Map<String, Integer> colIndex = new HashMap<>();
            for (int i = 0; i < headers.length; i++) {
                colIndex.put(headers[i].trim().toLowerCase(), i);
            }

            List<String[]> rows = new ArrayList<>();
            String line;
            int count = 0;
            while ((line = reader.readLine()) != null && count < 10) {
                if (line.trim().isEmpty()) continue;
                String[] parts = parseCSVLine(line);
                rows.add(parts);
                count++;
            }
            return new ImportPreview(headers, colIndex, rows);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private String getColumn(String[] row, Map<String, Integer> colIndex, String colName) {
        Integer idx = colIndex.get(colName.toLowerCase());
        if (idx != null && idx < row.length) {
            return row[idx].trim();
        }
        return null;
    }

    private String[] parseCSVLine(String line) {
        List<String> tokens = new ArrayList<>();
        boolean inQuotes = false;
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < line.length(); i++) {
            char c = line.charAt(i);
            if (c == '"') {
                inQuotes = !inQuotes;
            } else if (c == ',' && !inQuotes) {
                tokens.add(sb.toString());
                sb.setLength(0);
            } else {
                sb.append(c);
            }
        }
        tokens.add(sb.toString());
        return tokens.toArray(new String[0]);
    }

    private void setStatus(String message, boolean showProgress) {
        statusLabel.setText(message);
        progressBar.setVisible(showProgress);
    }

    private static class ImportPreview {
        String[] headers;
        Map<String, Integer> columnIndex;
        List<String[]> rows;

        ImportPreview(String[] headers, Map<String, Integer> colIndex, List<String[]> rows) {
            this.headers = headers;
            this.columnIndex = colIndex;
            this.rows = rows;
        }
    }

    private static class ImportResult {
        int imported = 0;
        int skippedDuplicates = 0;
        int skippedInvalidFormat = 0;
        int skippedErrors = 0;
    }

    private class PreviewDialog extends JDialog {
        private boolean confirmed = false;
        private JTable previewTable;

        public PreviewDialog(JFrame parent, ImportPreview preview) {
            super(parent, "Preview Import Data", true);
            setSize(900, 400);
            setLocationRelativeTo(parent);

            JPanel content = new JPanel(new BorderLayout(10, 10));
            content.setBackground(UIUtils.WHITE);
            content.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

            JLabel info = new JLabel("First " + preview.rows.size() + " rows from CSV:");
            info.setFont(UIUtils.F_BODY);
            content.add(info, BorderLayout.NORTH);

            DefaultTableModel model = new DefaultTableModel(preview.headers, 0);
            for (String[] row : preview.rows) {
                model.addRow(row);
            }
            previewTable = new JTable(model);
            previewTable.setFont(UIUtils.F_SMALL);
            previewTable.setRowHeight(25);
            JScrollPane scroll = new JScrollPane(previewTable);
            content.add(scroll, BorderLayout.CENTER);

            JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
            JButton cancelBtn = new JButton("Cancel");
            cancelBtn.addActionListener(e -> dispose());
            JButton confirmBtn = UIUtils.accentButton("Confirm Import", UIUtils.BLUE);
            confirmBtn.addActionListener(e -> {
                confirmed = true;
                dispose();
            });
            buttonPanel.add(cancelBtn);
            buttonPanel.add(confirmBtn);
            content.add(buttonPanel, BorderLayout.SOUTH);

            setContentPane(content);
        }

        public boolean isConfirmed() {
            return confirmed;
        }
    }
}