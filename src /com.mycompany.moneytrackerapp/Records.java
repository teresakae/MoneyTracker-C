/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package com.mycompany.moneytrackerapp;

import Config.Koneksi;
import java.awt.Component;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.UIManager;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import java.text.SimpleDateFormat;
import javax.swing.JOptionPane;
import java.text.ParseException;
import javax.swing.JComboBox;
import java.util.ArrayList;
import com.toedter.calendar.JDateChooser;

/**
 *
 * @author ADMIN
 */
public class Records extends javax.swing.JFrame {

    private Connection conn;

    public Records() {
        initComponents();
        conn = Koneksi.getConnection();
        getDataExpense();
        getDataIncome();
        updateComb();
        getDataCategory();

        try {
            Font centuryGothic = new Font("Century Gothic", Font.BOLD, 13);
            if (centuryGothic.getFamily().equals("Dialog")) {
                System.out.println("Century Gothic font not found. Using default font.");
            } else {
                UIManager.put("TableHeader.font", centuryGothic); // Here!
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        tbl_income.getTableHeader().setDefaultRenderer(new HeaderRenderer(tbl_income));
        tbl_income.getColumnModel().getColumn(1).setCellRenderer(new DateRenderer());
        ActionListener comboBoxActionListener = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                getDataCategory();
            }
        };
        categoryCombo.addActionListener(comboBoxActionListener);
        typeCombo.addActionListener(comboBoxActionListener);
    }

    private void updateComb() {
        String sql = "SELECT category FROM category";
        try (PreparedStatement ps = conn.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {

            categoryCombo.removeAllItems(); // Clear existing items

            while (rs.next()) {
                String category = rs.getString("category");
                categoryCombo.addItem(category);
            }
        } catch (SQLException ex) {
            System.out.println("Fail: " + ex.getMessage());
        }
    }

    private void getDataExpense() {
        DefaultTableModel model = (DefaultTableModel) tbl_expense.getModel();
        model.setRowCount(0);
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");

        try {
            String sql = "Select * From transaction WHERE type = 'Expense'";
            PreparedStatement st = conn.prepareStatement(sql);
            ResultSet rs = st.executeQuery();

            while (rs.next()) {
                int id = rs.getInt("id");
                Date date = rs.getDate("date");
                String formattedDate = dateFormat.format(date);
                String category = rs.getString("category");
                String item = rs.getString("item");
                int amount = rs.getInt("amount");

                Object[] rowData = {id, formattedDate, category, item, amount};
                model.addRow(rowData);
            }
            model.fireTableDataChanged();

            rs.close();
            st.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        tbl_expense.getColumnModel().getColumn(0).setMinWidth(0); // Set min width to 0
        tbl_expense.getColumnModel().getColumn(0).setMaxWidth(0); // Set max width to 0
        tbl_expense.getColumnModel().getColumn(0).setWidth(0); // Set width to 0
    }

    private void getDataIncome() {
        DefaultTableModel model = (DefaultTableModel) tbl_income.getModel();
        model.setRowCount(0);
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");

        try {
            String sql = "Select * From transaction WHERE type = 'Income'";
            PreparedStatement st = conn.prepareStatement(sql);
            ResultSet rs = st.executeQuery();

            while (rs.next()) {
                int id = rs.getInt("id");
                Date date = rs.getDate("date");
                String formattedDate = dateFormat.format(date);
                String category = rs.getString("category");
                String item = rs.getString("item");
                int amount = rs.getInt("amount");

                Object[] rowData = {id, formattedDate, category, item, amount};
                model.addRow(rowData);
            }
            model.fireTableDataChanged();

            rs.close();
            st.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        tbl_income.getColumnModel().getColumn(0).setMinWidth(0); // Set min width to 0
        tbl_income.getColumnModel().getColumn(0).setMaxWidth(0); // Set max width to 0
        tbl_income.getColumnModel().getColumn(0).setWidth(0); // Set width to 0
    }

    private void getDataCategory() {
        DefaultTableModel model = (DefaultTableModel) tbl_categorybased.getModel();
        model.setRowCount(0);
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");

        try {
            String sql = "SELECT * FROM transaction WHERE type = ? AND category = ?";
            PreparedStatement st = conn.prepareStatement(sql);
            st.setString(1, (String) typeCombo.getSelectedItem());
            st.setString(2, (String) categoryCombo.getSelectedItem());
            ResultSet rs = st.executeQuery();

            while (rs.next()) {
                int id = rs.getInt("id");
                Date date = rs.getDate("date");
                String formattedDate = dateFormat.format(date);
                String item = rs.getString("item");
                int amount = rs.getInt("amount");

                Object[] rowData = {id, formattedDate, item, amount};
                model.addRow(rowData);
            }
            model.fireTableDataChanged();

            rs.close();
            st.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        tbl_categorybased.getColumnModel().getColumn(0).setMinWidth(0);
        tbl_categorybased.getColumnModel().getColumn(0).setMaxWidth(0);
        tbl_categorybased.getColumnModel().getColumn(0).setWidth(0);
    }

    private static class HeaderRenderer implements TableCellRenderer {

        DefaultTableCellRenderer renderer;

        public HeaderRenderer(JTable table) {
            renderer = (DefaultTableCellRenderer) table.getTableHeader().getDefaultRenderer();
            renderer.setHorizontalAlignment(JLabel.CENTER);
        }

        @Override
        public Component getTableCellRendererComponent(
                JTable table, Object value, boolean isSelected,
                boolean hasFocus, int row, int col) {
            return renderer.getTableCellRendererComponent(
                    table, value, isSelected, hasFocus, row, col);
        }
    }

    private static class DateRenderer extends DefaultTableCellRenderer {

        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            if (value instanceof Date) {
                value = dateFormat.format((Date) value);
            }
            return super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
        }
    }

    private void deleteSelectedRow(javax.swing.JTable table) {
        int selectedRow = table.getSelectedRow();

        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Click on a row to delete it!", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        DefaultTableModel model = (DefaultTableModel) table.getModel();
        Object idObject = model.getValueAt(selectedRow, 0); 
        int id = Integer.parseInt(idObject.toString()); 

        int confirm = JOptionPane.showConfirmDialog(this, "Are you sure you want to delete this item?", "Confirm", JOptionPane.YES_NO_OPTION);
        if (confirm != JOptionPane.YES_OPTION) {
            return;
        }

        try {
            if (conn == null || conn.isClosed()) {
                conn = Koneksi.getConnection(); 
            }

            String deleteSQL = "DELETE FROM transaction WHERE id = ?"; 
            try (PreparedStatement ps = conn.prepareStatement(deleteSQL)) {
                ps.setInt(1, id); 
                int rowsAffected = ps.executeUpdate();

                if (rowsAffected > 0) {
                    model.removeRow(selectedRow);
                    JOptionPane.showMessageDialog(this, "Data successfully deleted!", "Success", JOptionPane.INFORMATION_MESSAGE);
                } else {
                    JOptionPane.showMessageDialog(this, "Failed to delete data!", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public void updateSelectedRow(javax.swing.JTable table) {
        int selectedRow = table.getSelectedRow();

        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Select the row you want to change!", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        DefaultTableModel model = (DefaultTableModel) table.getModel();
        Object idObject = model.getValueAt(selectedRow, 0);
        int id = Integer.parseInt(idObject.toString());

        JDateChooser dateChooser = new JDateChooser();
        dateChooser.setDateFormatString("dd/MM/yyyy");
        Date currentDate = parseDate(model.getValueAt(selectedRow, 1).toString(), "dd/MM/yyyy");
        dateChooser.setDate(currentDate);
        int dateResult = JOptionPane.showConfirmDialog(this, dateChooser, "Select a new date:", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        Date updatedDate = (dateResult == JOptionPane.OK_OPTION) ? dateChooser.getDate() : null;

        JComboBox<String> categoryComboBox = new JComboBox<>(getCategoryList());
        categoryComboBox.setSelectedItem(model.getValueAt(selectedRow, 2)); // Set current category
        int categoryResult = JOptionPane.showConfirmDialog(this, categoryComboBox, "Select a new category:", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        String updatedCategory = (categoryResult == JOptionPane.OK_OPTION) ? (String) categoryComboBox.getSelectedItem() : null;

        String updatedItem = JOptionPane.showInputDialog(this, "Enter a new value for the item column:", model.getValueAt(selectedRow, 3));
        String updatedAmount = JOptionPane.showInputDialog(this, "Enter a new value for the amount column:", model.getValueAt(selectedRow, 4));

        if (updatedDate != null && updatedCategory != null && !updatedCategory.trim().isEmpty()
                && updatedItem != null && !updatedItem.trim().isEmpty()
                && updatedAmount != null && !updatedAmount.trim().isEmpty()) {
            try {
                SimpleDateFormat dbFormat = new SimpleDateFormat("yyyy-MM-dd");
                String formattedDateForDb = dbFormat.format(updatedDate);  // Database format

                if (conn == null || conn.isClosed()) {
                    conn = Koneksi.getConnection();
                }

                String updateSQL = "UPDATE transaction SET date = ?, category = ?, item = ?, amount = ? WHERE id = ?";
                try (PreparedStatement ps = conn.prepareStatement(updateSQL)) {
                    ps.setString(1, formattedDateForDb);
                    ps.setString(2, updatedCategory);
                    ps.setString(3, updatedItem);
                    ps.setString(4, updatedAmount);
                    ps.setInt(5, id);
                    int rowsAffected = ps.executeUpdate();

                    if (rowsAffected > 0) {
                        SimpleDateFormat displayFormat = new SimpleDateFormat("dd/MM/yyyy");
                        model.setValueAt(displayFormat.format(updatedDate), selectedRow, 1);
                        model.setValueAt(updatedCategory, selectedRow, 2);
                        model.setValueAt(updatedItem, selectedRow, 3);
                        model.setValueAt(updatedAmount, selectedRow, 4);
                        JOptionPane.showMessageDialog(this, "Data updated successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                    } else {
                        JOptionPane.showMessageDialog(this, "Failed to update data!", "Error", JOptionPane.ERROR_MESSAGE);
                    }
                }
            } catch (SQLException ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private Date parseDate(String dateStr, String format) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat(format);
            return sdf.parse(dateStr);
        } catch (ParseException e) {
            return null;
        }
    }

    private String[] getCategoryList() {
        ArrayList<String> categories = new ArrayList<>();
        try {
            if (conn == null || conn.isClosed()) {
                conn = Koneksi.getConnection();
            }
            String query = "SELECT category FROM category";  // Adjust column name if necessary
            try (PreparedStatement ps = conn.prepareStatement(query); ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    categories.add(rs.getString("category"));
                }
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error fetching category list: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
        return categories.toArray(new String[0]);
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">                          
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jPanel2 = new javax.swing.JPanel();
        jLabel7 = new javax.swing.JLabel();
        jButton3 = new javax.swing.JButton();
        jButton2 = new javax.swing.JButton();
        jButton5 = new javax.swing.JButton();
        jButton7 = new javax.swing.JButton();
        jTabbedPane1 = new javax.swing.JTabbedPane();
        ExpenseRecords = new javax.swing.JScrollPane();
        jPanel3 = new javax.swing.JPanel();
        jScrollPane2 = new javax.swing.JScrollPane();
        tbl_expense = new javax.swing.JTable();
        IncomeRecords = new javax.swing.JScrollPane();
        jPanel4 = new javax.swing.JPanel();
        jScrollPane4 = new javax.swing.JScrollPane();
        tbl_income = new javax.swing.JTable();
        CategoryRecords = new javax.swing.JScrollPane();
        jPanel5 = new javax.swing.JPanel();
        jScrollPane3 = new javax.swing.JScrollPane();
        tbl_categorybased = new javax.swing.JTable();
        categoryCombo = new javax.swing.JComboBox<>();
        typeCombo = new javax.swing.JComboBox<>();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        btn_update = new javax.swing.JButton();
        btn_delete = new javax.swing.JButton();

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

        jLabel7.setFont(new java.awt.Font("Century Gothic", 1, 13)); // NOI18N
        jLabel7.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel7.setText("Click on a row to make changes to it!");

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

        jTabbedPane1.setBackground(new java.awt.Color(255, 255, 255));
        jTabbedPane1.setFont(new java.awt.Font("Century Gothic", 1, 13)); // NOI18N

        tbl_expense.setFont(new java.awt.Font("Century Gothic", 0, 13)); // NOI18N
        tbl_expense.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null}
            },
            new String [] {
                "id", "Date", "Category", "Item", "Amount"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.Integer.class, java.lang.Object.class, java.lang.Object.class, java.lang.Object.class, java.lang.Object.class
            };
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jScrollPane2.setViewportView(tbl_expense);

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel3Layout.createSequentialGroup()
                .addGap(0, 0, 0)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 413, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 478, Short.MAX_VALUE)
        );

        ExpenseRecords.setViewportView(jPanel3);

        jTabbedPane1.addTab("Expense", ExpenseRecords);

        tbl_income.setFont(new java.awt.Font("Century Gothic", 0, 13)); // NOI18N
        tbl_income.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null}
            },
            new String [] {
                "id", "Date", "Category", "Item", "Amount"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.Integer.class, java.lang.Object.class, java.lang.Object.class, java.lang.Object.class, java.lang.Object.class
            };
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jScrollPane4.setViewportView(tbl_income);

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane4, javax.swing.GroupLayout.DEFAULT_SIZE, 401, Short.MAX_VALUE)
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane4, javax.swing.GroupLayout.PREFERRED_SIZE, 466, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        IncomeRecords.setViewportView(jPanel4);

        jTabbedPane1.addTab("Income", IncomeRecords);

        tbl_categorybased.setFont(new java.awt.Font("Century Gothic", 0, 13)); // NOI18N
        tbl_categorybased.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "id", "Date", "Item", "Amount"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.Object.class, java.lang.Integer.class, java.lang.Object.class, java.lang.Object.class
            };
            boolean[] canEdit = new boolean [] {
                true, false, false, false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jScrollPane3.setViewportView(tbl_categorybased);

        categoryCombo.setFont(new java.awt.Font("Century Gothic", 0, 13)); // NOI18N
        categoryCombo.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        categoryCombo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                categoryComboActionPerformed(evt);
            }
        });

        typeCombo.setFont(new java.awt.Font("Century Gothic", 0, 13)); // NOI18N
        typeCombo.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Income", "Expense" }));
        typeCombo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                typeComboActionPerformed(evt);
            }
        });

        jLabel2.setFont(new java.awt.Font("Century Gothic", 0, 13)); // NOI18N
        jLabel2.setText("Category");

        jLabel3.setFont(new java.awt.Font("Century Gothic", 0, 13)); // NOI18N
        jLabel3.setText("View your transactions based on their:");

        jLabel4.setFont(new java.awt.Font("Century Gothic", 0, 13)); // NOI18N
        jLabel4.setText("Type");

        jLabel5.setFont(new java.awt.Font("Century Gothic", 0, 13)); // NOI18N
        jLabel5.setText(":");

        jLabel6.setFont(new java.awt.Font("Century Gothic", 0, 13)); // NOI18N
        jLabel6.setText(":");

        javax.swing.GroupLayout jPanel5Layout = new javax.swing.GroupLayout(jPanel5);
        jPanel5.setLayout(jPanel5Layout);
        jPanel5Layout.setHorizontalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel5Layout.createSequentialGroup()
                .addGap(25, 25, 25)
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(jPanel5Layout.createSequentialGroup()
                        .addComponent(jLabel4, javax.swing.GroupLayout.PREFERRED_SIZE, 104, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 8, Short.MAX_VALUE)
                        .addComponent(jLabel6, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(typeCombo, javax.swing.GroupLayout.PREFERRED_SIZE, 178, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel5Layout.createSequentialGroup()
                        .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 104, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jLabel5, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(categoryCombo, javax.swing.GroupLayout.PREFERRED_SIZE, 178, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(120, 120, 120))
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 395, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, Short.MAX_VALUE))
            .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(jPanel5Layout.createSequentialGroup()
                    .addGap(26, 26, 26)
                    .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 248, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addContainerGap(199, Short.MAX_VALUE)))
        );
        jPanel5Layout.setVerticalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel5Layout.createSequentialGroup()
                .addGap(35, 35, 35)
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(categoryCombo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel2)
                    .addComponent(jLabel5))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(typeCombo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel4)
                    .addComponent(jLabel6))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 20, Short.MAX_VALUE)
                .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 466, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
            .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(jPanel5Layout.createSequentialGroup()
                    .addGap(16, 16, 16)
                    .addComponent(jLabel3)
                    .addContainerGap(546, Short.MAX_VALUE)))
        );

        CategoryRecords.setViewportView(jPanel5);

        jTabbedPane1.addTab("Category", CategoryRecords);

        btn_update.setFont(new java.awt.Font("Century Gothic", 1, 13)); // NOI18N
        btn_update.setText("Update");
        btn_update.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_updateActionPerformed(evt);
            }
        });

        btn_delete.setFont(new java.awt.Font("Century Gothic", 1, 13)); // NOI18N
        btn_delete.setText("Delete");
        btn_delete.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_deleteActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jTabbedPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                .addGap(0, 0, Short.MAX_VALUE)
                .addComponent(btn_update, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(btn_delete, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(103, 103, 103))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                .addContainerGap(30, Short.MAX_VALUE)
                .addComponent(jButton3, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButton2, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButton5, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jButton7, javax.swing.GroupLayout.PREFERRED_SIZE, 29, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(30, 30, 30))
            .addComponent(jLabel7, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addComponent(jTabbedPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 268, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(19, 19, 19)
                .addComponent(jLabel7)
                .addGap(18, 18, 18)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btn_delete, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(btn_update, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(71, 71, 71)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jButton3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jButton2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jButton5)
                    .addComponent(jButton7))
                .addContainerGap(533, Short.MAX_VALUE))
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
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        setSize(new java.awt.Dimension(427, 567));
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
        this.dispose();        // TODO add your handling code here:  
    }                                        

    private void btn_updateActionPerformed(java.awt.event.ActionEvent evt) {                                           
        if (tbl_income.getSelectedRow() != -1) {
            updateSelectedRow(tbl_income);
        } else if (tbl_expense.getSelectedRow() != -1) {
            updateSelectedRow(tbl_expense);
        } else if (tbl_categorybased.getSelectedRow() != -1) {
            updateSelectedRow(tbl_categorybased);
        } else {
            JOptionPane.showMessageDialog(this, "Click on a row to delete it!", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }                                          

    private void btn_deleteActionPerformed(java.awt.event.ActionEvent evt) {                                           
        if (tbl_categorybased.getSelectedRow() != -1) {
            deleteSelectedRow(tbl_categorybased);
        } else if (tbl_expense.getSelectedRow() != -1) {
            deleteSelectedRow(tbl_expense);
        } else if (tbl_income.getSelectedRow() != -1) {
            deleteSelectedRow(tbl_income);
        } else {
            JOptionPane.showMessageDialog(this, "Click on a row to delete it!", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }                                          

    private void typeComboActionPerformed(java.awt.event.ActionEvent evt) {                                          
        // TODO add your handling code here:
    }                                         

    private void categoryComboActionPerformed(java.awt.event.ActionEvent evt) {                                              
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
            java.util.logging.Logger.getLogger(Records.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(Records.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(Records.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(Records.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new Records().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify                     
    private javax.swing.JScrollPane CategoryRecords;
    private javax.swing.JScrollPane ExpenseRecords;
    private javax.swing.JScrollPane IncomeRecords;
    private javax.swing.JButton btn_delete;
    private javax.swing.JButton btn_update;
    private javax.swing.JComboBox<String> categoryCombo;
    private javax.swing.JButton jButton2;
    private javax.swing.JButton jButton3;
    private javax.swing.JButton jButton5;
    private javax.swing.JButton jButton7;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JScrollPane jScrollPane4;
    private javax.swing.JTabbedPane jTabbedPane1;
    private javax.swing.JTable tbl_categorybased;
    private javax.swing.JTable tbl_expense;
    private javax.swing.JTable tbl_income;
    private javax.swing.JComboBox<String> typeCombo;
    // End of variables declaration                   
}
