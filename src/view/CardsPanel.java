package view;

import database.CardDAO;
import model.Card;
import model.User;
import utils.CurrencyFormatter;
import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

public class CardsPanel extends JPanel {

    private User currentUser;
    private CardDAO cardDAO;
    private JPanel cardsContainer;
    private JLabel totalDueLabel;

    private static final DateTimeFormatter DATE_FMT = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    public CardsPanel(User user) {
        this.currentUser = user;
        this.cardDAO = new CardDAO();
        setLayout(new BorderLayout(0, 12));
        setBackground(UIUtils.BG_PAGE);
        setBorder(new EmptyBorder(20, 24, 20, 24));

        add(buildHeader(), BorderLayout.NORTH);

        JPanel body = new JPanel(new BorderLayout(0, 10));
        body.setOpaque(false);
        body.add(buildSummaryBar(), BorderLayout.NORTH);
        body.add(buildCardsArea(), BorderLayout.CENTER);

        add(body, BorderLayout.CENTER);
        refreshCards();
    }

    private JPanel buildHeader() {
        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);
        header.setBorder(new EmptyBorder(0, 0, 8, 0));

        JLabel title = new JLabel("Credit Cards");
        title.setFont(UIUtils.F_SECTION);
        title.setForeground(UIUtils.TEXT_DARK);

        JButton addBtn = UIUtils.accentButton("+ Add Credit Card", UIUtils.BLUE);
        addBtn.addActionListener(e -> {
            AddEditCardDialog dialog = new AddEditCardDialog(
                    (JFrame) SwingUtilities.getWindowAncestor(this), currentUser, null);
            dialog.setVisible(true);
            if (dialog.isSaved()) refreshCards();
        });

        header.add(title, BorderLayout.WEST);
        header.add(addBtn, BorderLayout.EAST);
        return header;
    }

    private JPanel buildSummaryBar() {
        JPanel bar = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
        bar.setBackground(UIUtils.WHITE);
        bar.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(UIUtils.BORDER_COLOR, 1),
                new EmptyBorder(10, 18, 10, 18)));

        JLabel lbl = new JLabel("Total amount due: ");
        lbl.setFont(UIUtils.F_BODY);
        lbl.setForeground(UIUtils.TEXT_MID);

        totalDueLabel = new JLabel();
        totalDueLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        totalDueLabel.setForeground(UIUtils.RED);

        bar.add(lbl);
        bar.add(totalDueLabel);
        return bar;
    }

    private JScrollPane buildCardsArea() {
        cardsContainer = new JPanel();
        cardsContainer.setLayout(new BoxLayout(cardsContainer, BoxLayout.Y_AXIS));
        cardsContainer.setBackground(UIUtils.BG_PAGE);

        JScrollPane scroll = new JScrollPane(cardsContainer);
        scroll.setBorder(BorderFactory.createEmptyBorder());
        scroll.getViewport().setBackground(UIUtils.BG_PAGE);
        scroll.getVerticalScrollBar().setUnitIncrement(16);
        return scroll;
    }

    public void refreshCards() {
        cardsContainer.removeAll();
        List<Card> allCards = cardDAO.getCardsByUser(currentUser.getId());

        List<Card> creditCards = allCards.stream()
                .filter(c -> "Credit".equalsIgnoreCase(c.getCardType()))
                .collect(Collectors.toList());

        double totalDue = 0;
        for (Card c : creditCards) {
            if (c.getCurrentBalance() > 0) totalDue += c.getCurrentBalance();
            cardsContainer.add(buildCardRow(c));
            cardsContainer.add(Box.createRigidArea(new Dimension(0, 10)));
        }

        if (creditCards.isEmpty()) {
            JLabel empty = new JLabel("No credit cards yet. Click '+ Add Credit Card' to get started.",
                    SwingConstants.CENTER);
            empty.setFont(UIUtils.F_BODY);
            empty.setForeground(UIUtils.TEXT_LIGHT);
            empty.setAlignmentX(Component.CENTER_ALIGNMENT);
            cardsContainer.add(Box.createVerticalGlue());
            cardsContainer.add(empty);
            cardsContainer.add(Box.createVerticalGlue());
        }

        totalDueLabel.setText(CurrencyFormatter.format(totalDue));
        totalDueLabel.setForeground(totalDue > 0 ? UIUtils.RED : UIUtils.GREEN);

        cardsContainer.revalidate();
        cardsContainer.repaint();
    }

    private JPanel buildCardRow(Card c) {
        JPanel card = new JPanel(new BorderLayout(0, 0));
        card.setBackground(UIUtils.WHITE);
        card.setMaximumSize(new Dimension(Integer.MAX_VALUE, 220));
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(UIUtils.BORDER_COLOR, 1),
                new EmptyBorder(14, 18, 12, 18)));

        JLabel nameLbl = new JLabel(c.getCardName());
        nameLbl.setFont(new Font("Segoe UI", Font.BOLD, 16));
        nameLbl.setForeground(UIUtils.TEXT_DARK);

        LocalDate today = LocalDate.now();
        LocalDate cycleStart = today.withDayOfMonth(1);
        LocalDate cycleEnd   = today.withDayOfMonth(today.lengthOfMonth());

        int usagePct = 0;
        if (c.getCreditLimit() > 0)
            usagePct = (int) Math.round((c.getCurrentBalance() / c.getCreditLimit()) * 100);
        usagePct = Math.max(0, Math.min(100, usagePct));

        JPanel dateRow = buildDateRow(cycleStart, cycleEnd, usagePct);
        JPanel progressBar = buildProgressBar(usagePct);

        double residual = c.getCreditLimit() - c.getCurrentBalance();
        double balance  = c.getCurrentBalance();

        JPanel infoRow = buildInfoRow(c, balance, residual);
        JPanel actionRow = buildActionRow(c);

        JPanel top = new JPanel();
        top.setLayout(new BoxLayout(top, BoxLayout.Y_AXIS));
        top.setOpaque(false);
        top.add(nameLbl);
        top.add(Box.createRigidArea(new Dimension(0, 8)));
        top.add(dateRow);
        top.add(Box.createRigidArea(new Dimension(0, 6)));
        top.add(progressBar);
        top.add(Box.createRigidArea(new Dimension(0, 10)));
        top.add(infoRow);
        top.add(Box.createRigidArea(new Dimension(0, 10)));
        top.add(actionRow);

        card.add(top, BorderLayout.CENTER);
        return card;
    }

    private JPanel buildDateRow(LocalDate start, LocalDate end, int pct) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setOpaque(false);
        panel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 20));

        JLabel startLbl = new JLabel(start.format(DATE_FMT));
        startLbl.setFont(UIUtils.F_SMALL);
        startLbl.setForeground(UIUtils.TEXT_LIGHT);

        JLabel pctLbl = new JLabel(pct + "%", SwingConstants.CENTER);
        pctLbl.setFont(UIUtils.F_SMALL);
        pctLbl.setForeground(UIUtils.TEXT_MID);

        JLabel endLbl = new JLabel(end.format(DATE_FMT), SwingConstants.RIGHT);
        endLbl.setFont(UIUtils.F_SMALL);
        endLbl.setForeground(UIUtils.TEXT_LIGHT);

        panel.add(startLbl, BorderLayout.WEST);
        panel.add(pctLbl,   BorderLayout.CENTER);
        panel.add(endLbl,   BorderLayout.EAST);
        return panel;
    }

    private JPanel buildProgressBar(int pct) {
        JPanel wrapper = new JPanel(new BorderLayout());
        wrapper.setOpaque(false);
        wrapper.setPreferredSize(new Dimension(0, 10));
        wrapper.setMinimumSize(new Dimension(0, 10));
        wrapper.setMaximumSize(new Dimension(Integer.MAX_VALUE, 10));

        JPanel track = new JPanel(null) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(220, 220, 220));
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), getHeight(), getHeight());
                int fillW = (int) (getWidth() * (pct / 100.0));
                if (fillW > 0) {
                    g2.setColor(pct > 80 ? UIUtils.RED : UIUtils.GREEN);
                    g2.fillRoundRect(0, 0, fillW, getHeight(), getHeight(), getHeight());
                }
                g2.dispose();
            }
        };
        track.setPreferredSize(new Dimension(0, 12));
        track.setMinimumSize(new Dimension(0, 12));
        wrapper.add(track, BorderLayout.CENTER);
        return wrapper;
    }

    private JPanel buildInfoRow(Card c, double balance, double residual) {
        JPanel panel = new JPanel(new BorderLayout(0, 0));
        panel.setOpaque(false);
        panel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 60));

        JPanel left = new JPanel(new GridLayout(3, 1, 0, 1));
        left.setOpaque(false);
        left.add(makeInfoLabel("Limit: " + CurrencyFormatter.format(c.getCreditLimit())));
        left.add(makeInfoLabel("Residual: " + CurrencyFormatter.format(residual)));
        if (balance > 0)
            left.add(makeInfoLabel("Amount due: " + CurrencyFormatter.format(balance)));
        else
            left.add(makeInfoLabel(""));

        JLabel balLbl = new JLabel(CurrencyFormatter.format(balance), SwingConstants.RIGHT);
        balLbl.setFont(new Font("Segoe UI", Font.BOLD, 18));
        balLbl.setForeground(balance > 0 ? UIUtils.RED : UIUtils.GREEN);
        balLbl.setBorder(new EmptyBorder(0, 12, 0, 0));

        panel.add(left,   BorderLayout.CENTER);
        panel.add(balLbl, BorderLayout.EAST);
        return panel;
    }

    private JLabel makeInfoLabel(String text) {
        JLabel lbl = new JLabel(text);
        lbl.setFont(UIUtils.F_SMALL);
        lbl.setForeground(UIUtils.TEXT_MID);
        return lbl;
    }

    private JPanel buildActionRow(Card c) {
        JPanel row = new JPanel(new FlowLayout(FlowLayout.CENTER, 12, 4));
        row.setOpaque(false);
        row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 48));

        JButton editBtn = UIUtils.accentButton("Edit", UIUtils.BLUE);
        editBtn.addActionListener(e -> {
            AddEditCardDialog dlg = new AddEditCardDialog(
                    (JFrame) SwingUtilities.getWindowAncestor(this), currentUser, c);
            dlg.setVisible(true);
            if (dlg.isSaved()) refreshCards();
        });

        JButton deleteBtn = UIUtils.accentButton("Delete", UIUtils.RED);
        deleteBtn.addActionListener(e -> {
            int confirm = JOptionPane.showConfirmDialog(this,
                    "Delete card '" + c.getCardName() + "'?",
                    "Confirm Delete", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION && cardDAO.deleteCard(c.getId()))
                refreshCards();
        });

        row.add(editBtn);
        row.add(deleteBtn);
        return row;
    }
}