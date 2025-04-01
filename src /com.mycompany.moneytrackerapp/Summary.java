/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package com.mycompany.moneytrackerapp;

import Config.Koneksi;
import java.sql.SQLException;
import java.sql.PreparedStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.text.NumberFormat;
import java.util.Locale;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.data.general.DefaultPieDataset;
import javax.swing.BorderFactory;
import org.jfree.chart.plot.PiePlot3D;
import org.jfree.chart.ChartMouseEvent;
import org.jfree.chart.ChartMouseListener;
import org.jfree.chart.entity.ChartEntity;
import org.jfree.chart.entity.PieSectionEntity;
import javax.swing.JDialog;
import javax.swing.JProgressBar;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import java.awt.BorderLayout;
import javax.swing.BoxLayout;
import java.util.LinkedHashMap;
import java.util.Map;
import javax.swing.JButton;
import java.awt.Font;
import javax.swing.SwingConstants; 
import java.sql.Statement;
import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;


public class Summary extends javax.swing.JFrame {
    
    private Connection conn;
    
    private javax.swing.Timer notificationTimer;
    private boolean notificationShown = false; 
    private boolean isCheckingThreshold = false;

    
    private static int threshold = 70; // Default threshold

    public static int getThreshold() {
        return threshold;
    }

    public static void setThreshold(int newThreshold) {
        threshold = newThreshold;
    }
    
    private int totalIncome = 0;
    private int totalExpense = 0;
    
    public Summary() {
        initComponents();
        conn = Koneksi.getConnection(); 
        updateIncomeTotal();
        updateExpenseTotal();
        updateBalance();
        createIncomePieChart(); 
        createExpensePieChart();
        updateReminder();
        
        
        String selectedTransaction = (String) chooseTransaction.getSelectedItem();
        updateChartPanel(selectedTransaction);
        
        reminder = new JLabel("Reminder will be shown here.");
        
        notificationTimer = new javax.swing.Timer(10000, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                updateTotals(); 
                checkThreshold(totalExpense, totalIncome); che
            }
        });
        notificationTimer.start(); 
        
    }
  
    public void updateReminder() {
        updateReminderLabel(reminder); 
    }
    
    
    private void updateIncomeTotal() {
        int totalIncome = 0;

        try {
            if (conn == null || conn.isClosed()) {
                conn = Koneksi.getConnection(); 
            }

            String sql = "SELECT SUM(amount) AS total_income FROM transaction WHERE type = 'Income'";
            try (PreparedStatement ps = conn.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    totalIncome = rs.getInt("total_income"); // Ambil hasil total income
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        NumberFormat currencyFormatter = NumberFormat.getCurrencyInstance(new Locale("id", "ID"));
        incomeTotal.setText(currencyFormatter.format(totalIncome));
    }

    private void updateExpenseTotal() {
        int totalExpense = 0;

        try {
            if (conn == null || conn.isClosed()) {
                conn = Koneksi.getConnection(); 
            }

            String sql = "SELECT SUM(amount) AS total_expense FROM transaction WHERE type = 'Expense'";
            try (PreparedStatement ps = conn.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    totalExpense = rs.getInt("total_Expense"); 
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        NumberFormat currencyFormatter = NumberFormat.getCurrencyInstance(new Locale("id", "ID"));
        expenseTotal.setText(currencyFormatter.format(totalExpense));
    }

    private void updateBalance() {
        int totalIncome = 0; 
        int totalExpense = 0;
        int balance = 0; 

        try {
            if (conn == null || conn.isClosed()) {
                conn = Koneksi.getConnection();
            }
            String sqlIncome = "SELECT SUM(amount) AS total FROM transaction WHERE type = 'Income'";
            try (PreparedStatement psIncome = conn.prepareStatement(sqlIncome);
                 ResultSet rsIncome = psIncome.executeQuery()) {
                if (rsIncome.next()) {
                    totalIncome = rsIncome.getInt("total");
                }
            }

            String sqlExpense = "SELECT SUM(amount) AS total FROM transaction WHERE type = 'Expense'";
            try (PreparedStatement psExpense = conn.prepareStatement(sqlExpense);
                 ResultSet rsExpense = psExpense.executeQuery()) {
                if (rsExpense.next()) {
                    totalExpense = rsExpense.getInt("total");
                }
            }

            balance = totalIncome - totalExpense;
            NumberFormat currencyFormatter = NumberFormat.getCurrencyInstance(new Locale("id", "ID"));
            balanceLabel.setText(currencyFormatter.format(balance));

        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

        private JFreeChart createIncomePieChart() {
        DefaultPieDataset dataset = new DefaultPieDataset();

        String sql = "SELECT category, SUM(amount) AS total " +
                    "FROM transaction " +
                    "WHERE type = 'Income' " +
                    "GROUP BY category";


        try (PreparedStatement st = conn.prepareStatement(sql); ResultSet rs = st.executeQuery()) {
            while (rs.next()) {
                String category = rs.getString("category");
                double total = rs.getDouble("total");
                dataset.setValue(category, total);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return ChartFactory.createPieChart3D(
                "Income Overview",   
                dataset,             
                true,       
                true,       
                false             
        );
    }
    

        private JFreeChart createExpensePieChart() {
        DefaultPieDataset dataset = new DefaultPieDataset();

   
        String sql = "SELECT category, SUM(amount) AS total " +
                    "FROM transaction " +
                    "WHERE type = 'Expense' " +
                    "GROUP BY category";


        try (PreparedStatement st = conn.prepareStatement(sql); ResultSet rs = st.executeQuery()) {
            while (rs.next()) {
                String category = rs.getString("category");
                double total = rs.getDouble("total");
                dataset.setValue(category, total);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return ChartFactory.createPieChart3D(
                "Expense Overview",  
                dataset,             
                true,       
                true,       
                false             
        );
    }


    private void updateChartPanel(String transactionType) {
        chartPanel.removeAll(); 

        JFreeChart pieChart;
        if (transactionType.equals("EXPENSE OVERVIEW")) {
            pieChart = createExpensePieChart(); 
        } else if (transactionType.equals("INCOME OVERVIEW")) {
            pieChart = createIncomePieChart();
        } else {
            return; 
        }

       
        pieChart.setBackgroundPaint(null); 

        PiePlot3D plot = (PiePlot3D) pieChart.getPlot();
        plot.setBackgroundPaint(null);      
        plot.setOutlineVisible(false);     
        plot.setShadowPaint(null);         

        ChartPanel chartPanelComponent = new ChartPanel(pieChart);
        chartPanelComponent.setMouseWheelEnabled(true); 
        chartPanelComponent.setPreferredSize(new java.awt.Dimension(400, 300));
        chartPanelComponent.setBorder(BorderFactory.createEmptyBorder()); 

        
        Summary summaryFrame = this; 

        chartPanelComponent.addChartMouseListener(new ChartMouseListener() {
            @Override
            public void chartMouseClicked(ChartMouseEvent event) {
                ChartEntity entity = event.getEntity();
                if (entity instanceof PieSectionEntity) {
                    PieSectionEntity pieEntity = (PieSectionEntity) entity;
                    String category = pieEntity.getSectionKey().toString(); 

                    int totalAmount = 0;
                    Map<String, Integer> items = new LinkedHashMap<>();
                    try {
                        String sql = "SELECT item, SUM(amount) AS total FROM transaction WHERE category = ? GROUP BY item";
                        PreparedStatement st = conn.prepareStatement(sql);
                        st.setString(1, category);
                        ResultSet rs = st.executeQuery();

                        while (rs.next()) {
                            String item = rs.getString("item");
                            int amount = rs.getInt("total");
                            items.put(item, amount);
                            totalAmount += amount;
                        }
                        rs.close();
                        st.close();
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }

                    
                    items = items.entrySet().stream()
                        .sorted((e1, e2) -> e2.getValue().compareTo(e1.getValue()))
                        .collect(LinkedHashMap::new, (map, entry) -> map.put(entry.getKey(), entry.getValue()), Map::putAll);

                    summaryFrame.setVisible(false);

                    JDialog detailCategoryDialog = new JDialog(summaryFrame, "Detail Category", true);
                    detailCategoryDialog.setSize(400, 400);
                    detailCategoryDialog.setLayout(new BorderLayout());

                    JPanel headerPanel = new JPanel();
                    headerPanel.setLayout(new BorderLayout());
                    JLabel headerLabel = new JLabel("Category: " + category + " | Total: " + totalAmount, SwingConstants.CENTER);
                    headerLabel.setFont(new Font("Arial", Font.BOLD, 16));
                    headerPanel.add(headerLabel, BorderLayout.CENTER);

                    JPanel progressPanel = new JPanel();
                    progressPanel.setLayout(new BoxLayout(progressPanel, BoxLayout.Y_AXIS));
                    for (Map.Entry<String, Integer> entry : items.entrySet()) {
                        String itemName = entry.getKey();
                        int amount = entry.getValue();
                        int progressValue = (int) ((amount / (double) totalAmount) * 100);

                        JLabel itemLabel = new JLabel(itemName + " (" + amount + ")");
                        JProgressBar progressBar = new JProgressBar(0, 100);
                        progressBar.setValue(progressValue);
                        progressBar.setStringPainted(true);

                        progressPanel.add(itemLabel);
                        progressPanel.add(progressBar);
                    }

                    JButton backButton = new JButton("Back");
                    backButton.addActionListener(e -> {
                        detailCategoryDialog.dispose(); 
                        summaryFrame.setVisible(true); 
                    });

                    detailCategoryDialog.add(headerPanel, BorderLayout.NORTH);
                    detailCategoryDialog.add(new JScrollPane(progressPanel), BorderLayout.CENTER);
                    detailCategoryDialog.add(backButton, BorderLayout.SOUTH);

                    detailCategoryDialog.setLocationRelativeTo(null); 
                    detailCategoryDialog.setVisible(true);
                }
            }

            @Override
            public void chartMouseMoved(ChartMouseEvent event) {
            }
        });


        chartPanel.setLayout(new BorderLayout());
        chartPanel.add(chartPanelComponent, BorderLayout.CENTER);
        chartPanel.validate();
    }
    

    private void updateReminderLabel(JLabel reminder) {
        int totalIncome = 0;
        int totalExpense = 0;

        try {
            String incomeQuery = "SELECT SUM(amount) AS total FROM transaction WHERE type = 'Income'";
            Statement incomeStmt = conn.createStatement();
            ResultSet incomeRs = incomeStmt.executeQuery(incomeQuery);
            if (incomeRs.next()) {
                totalIncome = incomeRs.getInt("total");
            }
            incomeRs.close();
            incomeStmt.close();

            String expenseQuery = "SELECT SUM(amount) AS total FROM transaction WHERE type = 'Expense'";
            Statement expenseStmt = conn.createStatement();
            ResultSet expenseRs = expenseStmt.executeQuery(expenseQuery);
            if (expenseRs.next()) {
                totalExpense = expenseRs.getInt("total");
            }
            expenseRs.close();
            expenseStmt.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        String reminderMessage;
        if (totalIncome > 0) {
            int percentage = (int) ((totalExpense / (double) totalIncome) * 100);

            if (totalExpense > totalIncome) {
                reminderMessage = "You've spent more than your total income!";
            } else if (percentage >= 70) {
                reminderMessage = "Caution! You've spent more than " + percentage + "% of your income.";
            } else {
                reminderMessage = "You've spent more than " + percentage + "% of your income.";
            }
            
            checkThreshold(totalExpense, totalIncome);
            
        } else {
            reminderMessage = "No income recorded. Please add your income to track expenses.";
        }

     
        reminder.setText(reminderMessage);
    }


private void checkThreshold(int totalExpense, int totalIncome) {
    if (isCheckingThreshold) {
        return;
    }
    isCheckingThreshold = true;

    try {
        int percentage = 0;
        if (totalIncome > 0) {
            percentage = (int) ((totalExpense / (double) totalIncome) * 100);
        }

        if (percentage >= Summary.getThreshold() && !notificationShown) {
            this.textNotif.setText("Warning: You have spent " + percentage + " % of your income!");
            Notification.setSize(370, 230); // Atur ukuran dialog
            Notification.setLocationRelativeTo(this);
            Notification.setVisible(true);

            // Set flag 
            notificationShown = true;
        }

        // Reset flag
        if (percentage < Summary.getThreshold()) {
            notificationShown = false;
        }
    } finally {
        // Resets flag
        isCheckingThreshold = false;
    }
}   
  

    
private void updateTotals() {
    try {
      
        String incomeQuery = "SELECT SUM(amount) AS total FROM transaction WHERE type = 'Income'";
        Statement incomeStmt = conn.createStatement();
        ResultSet incomeRs = incomeStmt.executeQuery(incomeQuery);
        if (incomeRs.next()) {
            totalIncome = incomeRs.getInt("total");
        }
        incomeRs.close();
        incomeStmt.close();

        String expenseQuery = "SELECT SUM(amount) AS total FROM transaction WHERE type = 'Expense'";
        Statement expenseStmt = conn.createStatement();
        ResultSet expenseRs = expenseStmt.executeQuery(expenseQuery);
        if (expenseRs.next()) {
            totalExpense = expenseRs.getInt("total");
        }
        expenseRs.close();
        expenseStmt.close();
        
        System.out.println("Total Income: " + totalIncome);
        System.out.println("Total Expense: " + totalExpense);
    } catch (SQLException e) {
        e.printStackTrace();
    }
}


    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">                          
    private void initComponents() {

        Notification = new javax.swing.JDialog();
        jPanel6 = new javax.swing.JPanel();
        textNotif = new javax.swing.JTextArea();
        jPanel4 = new javax.swing.JPanel();
        jLabel14 = new javax.swing.JLabel();
        jButton8 = new javax.swing.JButton();
        detailCategory = new javax.swing.JDialog();
        jButton9 = new javax.swing.JButton();
        jPanel5 = new javax.swing.JPanel();
        jLabel16 = new javax.swing.JLabel();
        jProgressBar1 = new javax.swing.JProgressBar();
        jPanel1 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jPanel2 = new javax.swing.JPanel();
        jButton3 = new javax.swing.JButton();
        jButton2 = new javax.swing.JButton();
        jButton5 = new javax.swing.JButton();
        jButton7 = new javax.swing.JButton();
        jPanel3 = new javax.swing.JPanel();
        jButton1 = new javax.swing.JButton();
        chartPanel = new javax.swing.JPanel();
        jLabel5 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        expenseTotal = new javax.swing.JLabel();
        incomeTotal = new javax.swing.JLabel();
        balanceLabel = new javax.swing.JLabel();
        chooseTransaction = new javax.swing.JComboBox<>();
        reminder = new javax.swing.JLabel();

        Notification.setBackground(new java.awt.Color(255, 255, 255));

        textNotif.setEditable(false);
        textNotif.setBackground(new java.awt.Color(255, 255, 255));
        textNotif.setColumns(20);
        textNotif.setFont(new java.awt.Font("Century Gothic", 0, 13)); // NOI18N
        textNotif.setRows(4);
        textNotif.setText("\nYou've spent 70% more than your income!\nstop lmao u gon go broke");

        javax.swing.GroupLayout jPanel6Layout = new javax.swing.GroupLayout(jPanel6);
        jPanel6.setLayout(jPanel6Layout);
        jPanel6Layout.setHorizontalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
            .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(jPanel6Layout.createSequentialGroup()
                    .addGap(7, 7, 7)
                    .addComponent(textNotif, javax.swing.GroupLayout.DEFAULT_SIZE, 284, Short.MAX_VALUE)
                    .addContainerGap()))
        );
        jPanel6Layout.setVerticalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 84, Short.MAX_VALUE)
            .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(jPanel6Layout.createSequentialGroup()
                    .addContainerGap()
                    .addComponent(textNotif, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
        );

        jPanel4.setBackground(new java.awt.Color(204, 255, 204));
        jPanel4.setForeground(new java.awt.Color(204, 255, 255));

        jLabel14.setFont(new java.awt.Font("Blackadder ITC", 1, 24)); // NOI18N
        jLabel14.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel14.setText("Money Tracker");

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jLabel14, javax.swing.GroupLayout.DEFAULT_SIZE, 297, Short.MAX_VALUE)
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jLabel14, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 37, Short.MAX_VALUE)
        );

        jButton8.setFont(new java.awt.Font("Century Gothic", 1, 14)); // NOI18N
        jButton8.setText("Ok");
        jButton8.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton8ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout NotificationLayout = new javax.swing.GroupLayout(Notification.getContentPane());
        Notification.getContentPane().setLayout(NotificationLayout);
        NotificationLayout.setHorizontalGroup(
            NotificationLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(jPanel6, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(NotificationLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jButton8, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );
        NotificationLayout.setVerticalGroup(
            NotificationLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(NotificationLayout.createSequentialGroup()
                .addComponent(jPanel4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel6, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButton8)
                .addContainerGap())
        );

        jButton9.setFont(new java.awt.Font("Century Gothic", 1, 14)); // NOI18N
        jButton9.setText("Ok");
        jButton9.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton9ActionPerformed(evt);
            }
        });

        jPanel5.setBackground(new java.awt.Color(204, 255, 204));
        jPanel5.setForeground(new java.awt.Color(204, 255, 255));

        jLabel16.setFont(new java.awt.Font("Blackadder ITC", 1, 24)); // NOI18N
        jLabel16.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel16.setText("Money Tracker");

        javax.swing.GroupLayout jPanel5Layout = new javax.swing.GroupLayout(jPanel5);
        jPanel5.setLayout(jPanel5Layout);
        jPanel5Layout.setHorizontalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel16, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGap(20, 20, 20))
        );
        jPanel5Layout.setVerticalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel16, javax.swing.GroupLayout.DEFAULT_SIZE, 37, Short.MAX_VALUE)
                .addContainerGap())
        );

        javax.swing.GroupLayout detailCategoryLayout = new javax.swing.GroupLayout(detailCategory.getContentPane());
        detailCategory.getContentPane().setLayout(detailCategoryLayout);
        detailCategoryLayout.setHorizontalGroup(
            detailCategoryLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(detailCategoryLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jButton9, javax.swing.GroupLayout.DEFAULT_SIZE, 378, Short.MAX_VALUE)
                .addGap(16, 16, 16))
            .addComponent(jPanel5, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(detailCategoryLayout.createSequentialGroup()
                .addComponent(jProgressBar1, javax.swing.GroupLayout.PREFERRED_SIZE, 262, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, Short.MAX_VALUE))
        );
        detailCategoryLayout.setVerticalGroup(
            detailCategoryLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(detailCategoryLayout.createSequentialGroup()
                .addComponent(jPanel5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(47, 47, 47)
                .addComponent(jProgressBar1, javax.swing.GroupLayout.PREFERRED_SIZE, 36, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 136, Short.MAX_VALUE)
                .addComponent(jButton9)
                .addContainerGap())
        );

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setBackground(new java.awt.Color(255, 255, 255));

        jPanel1.setBackground(new java.awt.Color(204, 255, 204));
        jPanel1.setForeground(new java.awt.Color(204, 255, 255));

        jLabel1.setFont(new java.awt.Font("Blackadder ITC", 1, 24)); // NOI18N
        jLabel1.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel1.setText("Money Tracker");

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jLabel1, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel1)
                .addGap(0, 0, Short.MAX_VALUE))
        );

        jPanel2.setBackground(new java.awt.Color(255, 255, 255));

        jButton3.setFont(new java.awt.Font("Century Gothic", 1, 13)); // NOI18N
        jButton3.setText("Records");
        jButton3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton3ActionPerformed(evt);
            }
        });

        jButton2.setFont(new java.awt.Font("Century Gothic", 1, 13)); // NOI18N
        jButton2.setText("Home");
        jButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton2ActionPerformed(evt);
            }
        });

        jButton5.setFont(new java.awt.Font("Century Gothic", 1, 13)); // NOI18N
        jButton5.setText("Summary");
        jButton5.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton5ActionPerformed(evt);
            }
        });

        jButton7.setFont(new java.awt.Font("Century Gothic", 1, 13)); // NOI18N
        jButton7.setText("Report");
        jButton7.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton7ActionPerformed(evt);
            }
        });

        jPanel3.setPreferredSize(new java.awt.Dimension(398, 480));

        jButton1.setFont(new java.awt.Font("Century Gothic", 0, 13)); // NOI18N
        jButton1.setText("Test Notif");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        chartPanel.setBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.RAISED));

        javax.swing.GroupLayout chartPanelLayout = new javax.swing.GroupLayout(chartPanel);
        chartPanel.setLayout(chartPanelLayout);
        chartPanelLayout.setHorizontalGroup(
            chartPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );
        chartPanelLayout.setVerticalGroup(
            chartPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 190, Short.MAX_VALUE)
        );

        jLabel5.setFont(new java.awt.Font("Century Gothic", 1, 14)); // NOI18N
        jLabel5.setText("Income");

        jLabel6.setFont(new java.awt.Font("Century Gothic", 1, 14)); // NOI18N
        jLabel6.setText("Expenses");

        jLabel7.setFont(new java.awt.Font("Century Gothic", 1, 14)); // NOI18N
        jLabel7.setText("Difference");

        expenseTotal.setFont(new java.awt.Font("Century Gothic", 0, 14)); // NOI18N
        expenseTotal.setForeground(new java.awt.Color(255, 51, 51));
        expenseTotal.setText("Expenses");

        incomeTotal.setFont(new java.awt.Font("Century Gothic", 0, 14)); // NOI18N
        incomeTotal.setText("Income");

        balanceLabel.setFont(new java.awt.Font("Century Gothic", 0, 14)); // NOI18N
        balanceLabel.setText("Total");

        chooseTransaction.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "EXPENSE OVERVIEW", "INCOME OVERVIEW" }));
        chooseTransaction.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                chooseTransactionActionPerformed(evt);
            }
        });

        reminder.setFont(new java.awt.Font("Century Gothic", 1, 13)); // NOI18N
        reminder.setText("reminder");
        reminder.addPropertyChangeListener(new java.beans.PropertyChangeListener() {
            public void propertyChange(java.beans.PropertyChangeEvent evt) {
                reminderPropertyChange(evt);
            }
        });

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel3Layout.createSequentialGroup()
                                .addContainerGap()
                                .addComponent(expenseTotal))
                            .addGroup(jPanel3Layout.createSequentialGroup()
                                .addGap(25, 25, 25)
                                .addComponent(jLabel6)))
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addGroup(jPanel3Layout.createSequentialGroup()
                                .addGap(43, 43, 43)
                                .addComponent(jLabel5)
                                .addGap(76, 76, 76)
                                .addComponent(jLabel7))
                            .addGroup(jPanel3Layout.createSequentialGroup()
                                .addGap(24, 24, 24)
                                .addComponent(incomeTotal)
                                .addGap(24, 24, 24)
                                .addComponent(balanceLabel))))
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addGap(17, 17, 17)
                        .addComponent(jButton1, javax.swing.GroupLayout.PREFERRED_SIZE, 88, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(0, 121, Short.MAX_VALUE))
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(chartPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(chooseTransaction, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(reminder, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                .addContainerGap())
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(chooseTransaction, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(reminder, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel6)
                    .addComponent(jLabel5)
                    .addComponent(jLabel7))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(expenseTotal)
                    .addComponent(incomeTotal)
                    .addComponent(balanceLabel))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(chartPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(33, 33, 33)
                .addComponent(jButton1, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(55, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGap(20, 20, 20)
                .addComponent(jButton3, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButton2, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButton5, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jButton7, javax.swing.GroupLayout.PREFERRED_SIZE, 29, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(20, 20, 20))
            .addComponent(jPanel3, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 460, Short.MAX_VALUE)
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, 407, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(20, 20, 20)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jButton3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jButton2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jButton5)
                    .addComponent(jButton7))
                .addContainerGap(77, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        setSize(new java.awt.Dimension(474, 577));
        setLocationRelativeTo(null);
    }// </editor-fold>                        

    private void jButton3ActionPerformed(java.awt.event.ActionEvent evt) {                                         
       Records records = new Records();
        records.setVisible(true);
        this.dispose();        // TODO add your handling code here:
    }                                        

    private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {                                         
        Home home = new Home();
        home.setVisible(true);
        this.dispose();        // TODO add your handling code here:
    }                                        

    private void jButton5ActionPerformed(java.awt.event.ActionEvent evt) {                                         
        
        Summary summary = new Summary();
        summary.setVisible(true);
        this.dispose();        // TODO add your handling code here:
    }                                        

    private void jButton7ActionPerformed(java.awt.event.ActionEvent evt) {                                         
        Settings settings = new Settings();
        settings.setVisible(true);
        this.dispose();         // TODO add your handling code here:
    }                                        

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {                                         
        Notification.setSize(320, 220); 
        Notification.setLocationRelativeTo(this); 
        Notification.setVisible(true); 
        this.dispose();        // TODO add your handling code here:
    }                                        

    private void jButton8ActionPerformed(java.awt.event.ActionEvent evt) {                                         
        Notification.setVisible(false);
    }                                        

    private void chooseTransactionActionPerformed(java.awt.event.ActionEvent evt) {                                                  
        String selectedTransaction = (String) chooseTransaction.getSelectedItem();
        updateChartPanel(selectedTransaction);
    }                                                 

    private void jButton9ActionPerformed(java.awt.event.ActionEvent evt) {                                         
        Summary summary = new Summary();
        summary.setVisible(true);
        detailCategory.setVisible(false);
    }                                        

    private void reminderPropertyChange(java.beans.PropertyChangeEvent evt) {                                        
        // TODO add your handling code here:
    }                                       

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(Summary.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(Summary.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(Summary.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(Summary.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new Summary().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify                     
    private javax.swing.JDialog Notification;
    private javax.swing.JLabel balanceLabel;
    private javax.swing.JPanel chartPanel;
    private javax.swing.JComboBox<String> chooseTransaction;
    private javax.swing.JDialog detailCategory;
    private javax.swing.JLabel expenseTotal;
    private javax.swing.JLabel incomeTotal;
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton2;
    private javax.swing.JButton jButton3;
    private javax.swing.JButton jButton5;
    private javax.swing.JButton jButton7;
    private javax.swing.JButton jButton8;
    private javax.swing.JButton jButton9;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel16;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JProgressBar jProgressBar1;
    private javax.swing.JLabel reminder;
    private javax.swing.JTextArea textNotif;
    // End of variables declaration                   
}
