/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package com.mycompany.moneytrackerapp;

import Config.Koneksi;
import java.awt.Component;
import java.awt.Font;
import java.sql.Connection;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.UIManager;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellRenderer;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import javax.swing.SwingUtilities;
import javax.swing.table.DefaultTableModel;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;

public class Settings extends javax.swing.JFrame {
     
    private Connection conn;
    private static class HeaderRenderer implements TableCellRenderer {
    DefaultTableCellRenderer renderer;
      
    public HeaderRenderer(JTable table) {
        renderer = (DefaultTableCellRenderer)
            table.getTableHeader().getDefaultRenderer();
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
    /**
     * Creates new form Home
     */
    public Settings() {
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
        
        initComponents();
         // Ambil nilai threshold dari Summary dan perbarui label
        currentThreshold = Summary.getThreshold();
        updateThresholdLabel();

        
        conn = Koneksi.getConnection();
        getDataCategory();
    }

    private void getDataCategory() {
        DefaultTableModel model = (DefaultTableModel) categorytbl.getModel();
        model.setRowCount(0);

        try {
            String sql = "SELECT * FROM category";
            PreparedStatement st = conn.prepareStatement(sql);
            ResultSet rs = st.executeQuery();

            while (rs.next()) {
                int id = rs.getInt("id");
                String category = rs.getString("category");

                Object[] rowData = {id, category};
                model.addRow(rowData);
            }
            model.fireTableDataChanged();

            rs.close();
            st.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        categorytbl.getColumnModel().getColumn(0).setMinWidth(0);
        categorytbl.getColumnModel().getColumn(0).setMaxWidth(0);
        categorytbl.getColumnModel().getColumn(0).setWidth(0);
    }

private void saveCategory() {
    String newCategory = JOptionPane.showInputDialog(this, "Enter new category:", "New Category", JOptionPane.PLAIN_MESSAGE);
    
    if (newCategory != null && !newCategory.trim().isEmpty()) {
        newCategory = newCategory.trim();
        DefaultTableModel model = (DefaultTableModel) categorytbl.getModel();

        try {
            if (conn == null || conn.isClosed()) {
                conn = Koneksi.getConnection();
            }

            String sql = "INSERT INTO category (category) VALUES (?)";
            PreparedStatement st = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            st.setString(1, newCategory);

            int affectedRows = st.executeUpdate();

            if (affectedRows > 0) {
                ResultSet rs = st.getGeneratedKeys();
                if (rs.next()) {
                    int id = rs.getInt(1);

                    model.addRow(new Object[]{id, newCategory});
                    JOptionPane.showMessageDialog(this, "Category added successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                }
                rs.close();
            } else {
                JOptionPane.showMessageDialog(this, "Failed to add category! No rows affected.", "Error", JOptionPane.ERROR_MESSAGE);
            }

            st.close();
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "SQL Error: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Unexpected Error: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }

        getDataCategory();
    } else if (newCategory != null && newCategory.trim().isEmpty()) {
        JOptionPane.showMessageDialog(this, "Category cannot be empty!", "Error", JOptionPane.ERROR_MESSAGE);
    }
}

private void updateCategory() {
    int selectedRow = categorytbl.getSelectedRow();

    if (selectedRow != -1) {
        int id = (int) categorytbl.getValueAt(selectedRow, 0);

        JTextField textField = new JTextField(20);
        textField.setText((String) categorytbl.getValueAt(selectedRow, 1)); // Pre-fill with the current category name
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.add(new JLabel("Enter new category:"));
        panel.add(Box.createVerticalStrut(10)); 
        panel.add(textField);

        int result = JOptionPane.showConfirmDialog(this, panel, "Update Category", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        
        if (result == JOptionPane.OK_OPTION) {
            String updatedCategory = textField.getText().trim();

            if (!updatedCategory.isEmpty()) {
                try {
                    if (conn == null || conn.isClosed()) {
                        conn = Koneksi.getConnection();
                    }

                    String getOldCategorySql = "SELECT category FROM category WHERE id = ?";
                    PreparedStatement getOldCategorySt = conn.prepareStatement(getOldCategorySql);
                    getOldCategorySt.setInt(1, id);
                    ResultSet rs = getOldCategorySt.executeQuery();
                    String oldCategoryName = null;

                    if (rs.next()) {
                        oldCategoryName = rs.getString("category");
                    } else {
                        JOptionPane.showMessageDialog(this, "Category not found for the selected ID.", "Error", JOptionPane.ERROR_MESSAGE);
                        rs.close();
                        getOldCategorySt.close();
                        return;
                    }
                    rs.close();
                    getOldCategorySt.close();

                    System.out.println("Old Category Name: " + oldCategoryName);

                    String sql = "UPDATE category SET category = ? WHERE id = ?";
                    PreparedStatement st = conn.prepareStatement(sql);
                    st.setString(1, updatedCategory);
                    st.setInt(2, id);
                    int affectedRows = st.executeUpdate();

                    if (affectedRows > 0) {
                        categorytbl.setValueAt(updatedCategory, selectedRow, 1);
                        System.out.println("Row updated: ID=" + id + ", New Category=" + updatedCategory);
                        updateTransactionTable(oldCategoryName, updatedCategory);
                        JOptionPane.showMessageDialog(this, "Category updated successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                    } else {
                        JOptionPane.showMessageDialog(this, "Failed to update category! No rows affected.", "Error", JOptionPane.ERROR_MESSAGE);
                    }

                    st.close();
                    ;
                } catch (SQLException e) {
                    e.printStackTrace();
                    JOptionPane.showMessageDialog(this, "SQL Error: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                } catch (Exception e) {
                    e.printStackTrace();
                    JOptionPane.showMessageDialog(this, "Unexpected Error: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                }
                getDataCategory();
            } else {
                JOptionPane.showMessageDialog(this, "Category cannot be empty!", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    } else {
        JOptionPane.showMessageDialog(this, "Please select a category to update.", "Error", JOptionPane.ERROR_MESSAGE);
    }
}



private void updateTransactionTable(String oldCategoryName, String newCategoryName) {
    try {
        if (oldCategoryName != null) {
            String updateTransactionsSql = "UPDATE transaction SET category = ? WHERE category = ?";
            PreparedStatement updateTransactionsSt = conn.prepareStatement(updateTransactionsSql);
            updateTransactionsSt.setString(1, newCategoryName);
            updateTransactionsSt.setString(2, oldCategoryName);
            int transactionAffectedRows = updateTransactionsSt.executeUpdate();
            if (transactionAffectedRows > 0) {
                System.out.println("Transactions updated: " + transactionAffectedRows + " rows, New Category=" + newCategoryName);
            } else {
                System.out.println("No transactions found with the old category name: " + oldCategoryName);
            }
            updateTransactionsSt.close();
        } else {
            System.out.println("Old category name not found.");
        }
    } catch (SQLException e) {
        e.printStackTrace();
    }
}




private void deleteCategory() {
    int selectedRow = categorytbl.getSelectedRow();
    DefaultTableModel model = (DefaultTableModel) categorytbl.getModel();

    if (selectedRow != -1) {
        int id = (int) model.getValueAt(selectedRow, 0);
        String category = (String) model.getValueAt(selectedRow, 1);

        int confirm = JOptionPane.showConfirmDialog(this, "Are you sure you want to delete this category and all related transactions?", "Confirm Deletion", JOptionPane.YES_NO_OPTION);
        if (confirm != JOptionPane.YES_OPTION) {
            return;
        }

        try {
            String deleteTransactionsSql = "DELETE FROM transaction WHERE category = ?";
            PreparedStatement psTrans = conn.prepareStatement(deleteTransactionsSql);
            psTrans.setString(1, category);
            int transRowsAffected = psTrans.executeUpdate();
            psTrans.close();
            System.out.println("Deleted " + transRowsAffected + " transactions related to category: " + category);

            String deleteCategorySql = "DELETE FROM category WHERE id = ?";
            PreparedStatement psCat = conn.prepareStatement(deleteCategorySql);
            psCat.setInt(1, id);
            int catRowsAffected = psCat.executeUpdate();
            if (catRowsAffected > 0) {
                model.removeRow(selectedRow);  // Remove row from the table model
                System.out.println("Category deleted: ID=" + id + ", Name=" + category);
            }
            psCat.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        getDataCategory(); 
    }
}

private int currentThreshold = 70; // Default threshold 70%

private void updateThresholdLabel() {
    thresholdLabel.setText("Current Threshold: " + currentThreshold + "%");
}








 
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">                          
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jPanel2 = new javax.swing.JPanel();
        jButton3 = new javax.swing.JButton();
        jButton2 = new javax.swing.JButton();
        jButton5 = new javax.swing.JButton();
        jButton7 = new javax.swing.JButton();
        jTabbedPane1 = new javax.swing.JTabbedPane();
        jPanel5 = new javax.swing.JPanel();
        jButton4 = new javax.swing.JButton();
        jButton6 = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        categorytbl = new javax.swing.JTable();
        jButton8 = new javax.swing.JButton();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jPanel3 = new javax.swing.JPanel();
        jLabel4 = new javax.swing.JLabel();
        thresholdField = new javax.swing.JTextField();
        saveButton = new javax.swing.JButton();
        thresholdLabel = new javax.swing.JLabel();

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
            .addComponent(jLabel1, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 400, Short.MAX_VALUE)
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

        jPanel5.setBackground(new java.awt.Color(255, 255, 255));
        jPanel5.setFont(new java.awt.Font("Century Gothic", 0, 13)); // NOI18N
        jPanel5.setPreferredSize(new java.awt.Dimension(422, 300));

        jButton4.setFont(new java.awt.Font("Century Gothic", 1, 13)); // NOI18N
        jButton4.setText("Rename Category");
        jButton4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton4ActionPerformed(evt);
            }
        });

        jButton6.setFont(new java.awt.Font("Century Gothic", 1, 13)); // NOI18N
        jButton6.setText("Add New");
        jButton6.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton6ActionPerformed(evt);
            }
        });

        categorytbl.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null},
                {null, null},
                {null, null},
                {null, null}
            },
            new String [] {
                "id", "category"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.Integer.class, java.lang.Object.class
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }
        });
        jScrollPane1.setViewportView(categorytbl);

        jButton8.setFont(new java.awt.Font("Century Gothic", 1, 13)); // NOI18N
        jButton8.setText("Delete Category");
        jButton8.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton8ActionPerformed(evt);
            }
        });

        jLabel2.setFont(new java.awt.Font("Century Gothic", 1, 13)); // NOI18N
        jLabel2.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel2.setText("Click on a category to make changes to it!");

        jLabel3.setFont(new java.awt.Font("Century Gothic", 1, 13)); // NOI18N
        jLabel3.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel3.setText("What do you want to do with it?");

        javax.swing.GroupLayout jPanel5Layout = new javax.swing.GroupLayout(jPanel5);
        jPanel5.setLayout(jPanel5Layout);
        jPanel5Layout.setHorizontalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 373, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel5Layout.createSequentialGroup()
                        .addGap(103, 103, 103)
                        .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                            .addComponent(jButton4, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jButton8, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 176, Short.MAX_VALUE)
                            .addComponent(jButton6, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
                .addContainerGap(55, Short.MAX_VALUE))
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(jPanel5Layout.createSequentialGroup()
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 373, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, Short.MAX_VALUE))))
        );
        jPanel5Layout.setVerticalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel5Layout.createSequentialGroup()
                .addComponent(jLabel2)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 42, Short.MAX_VALUE)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 105, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(10, 10, 10)
                .addComponent(jLabel3)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jButton6)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButton4)
                .addGap(10, 10, 10)
                .addComponent(jButton8)
                .addGap(20, 20, 20))
        );

        jTabbedPane1.addTab("Category", jPanel5);

        jLabel4.setFont(new java.awt.Font("Century Gothic", 1, 13)); // NOI18N
        jLabel4.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel4.setText("Set Reminder Threshold (%):");

        saveButton.setText("SAVE");
        saveButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                saveButtonActionPerformed(evt);
            }
        });

        thresholdLabel.setFont(new java.awt.Font("Century Gothic", 1, 18)); // NOI18N
        thresholdLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        thresholdLabel.setText("..");

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addGap(44, 44, 44)
                        .addComponent(thresholdField, javax.swing.GroupLayout.PREFERRED_SIZE, 127, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(50, 50, 50)
                        .addComponent(saveButton, javax.swing.GroupLayout.PREFERRED_SIZE, 133, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(thresholdLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 385, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(37, Short.MAX_VALUE))
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addComponent(jLabel4, javax.swing.GroupLayout.PREFERRED_SIZE, 391, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, Short.MAX_VALUE))
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addGap(112, 112, 112)
                .addComponent(thresholdLabel)
                .addGap(27, 27, 27)
                .addComponent(jLabel4)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(thresholdField, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(saveButton, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(87, Short.MAX_VALUE))
        );

        jTabbedPane1.addTab("Notification", jPanel3);

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGap(25, 25, 25)
                .addComponent(jButton3, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButton2, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButton5, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jButton7, javax.swing.GroupLayout.PREFERRED_SIZE, 29, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(56, 56, 56))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                .addComponent(jTabbedPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 428, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addComponent(jTabbedPane1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jButton3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jButton2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jButton5)
                    .addComponent(jButton7))
                .addContainerGap())
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, 400, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        setSize(new java.awt.Dimension(414, 487));
        setLocationRelativeTo(null);
    }// </editor-fold>                        

    private void jButton3ActionPerformed(java.awt.event.ActionEvent evt) {                                         
        Records record = new Records();
        record.setVisible(true);
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

    private void saveButtonActionPerformed(java.awt.event.ActionEvent evt) {                                           

        try {
            int threshold = Integer.parseInt(thresholdField.getText());

            if (threshold < 0 || threshold > 100) {
                JOptionPane.showMessageDialog(null, "Please enter a valid percentage (0-100)!");
            } else {
                Summary.setThreshold(threshold);
                currentThreshold = threshold;
                updateThresholdLabel();
                JOptionPane.showMessageDialog(null, "Threshold updated successfully!");
            }
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(null, "Please enter a valid number!");
        }

    }                                          

    private void jButton8ActionPerformed(java.awt.event.ActionEvent evt) {                                         
        deleteCategory();
        // TODO add your handling code here:
    }                                        

    private void jButton6ActionPerformed(java.awt.event.ActionEvent evt) {                                         
        saveCategory();
        // TODO add your handling code here:
    }                                        

    private void jButton4ActionPerformed(java.awt.event.ActionEvent evt) {                                         
        updateCategory();
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
        java.util.logging.Logger.getLogger(Settings.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
    } catch (InstantiationException ex) {
        java.util.logging.Logger.getLogger(Settings.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
    } catch (IllegalAccessException ex) {
        java.util.logging.Logger.getLogger(Settings.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
    } catch (javax.swing.UnsupportedLookAndFeelException ex) {
        java.util.logging.Logger.getLogger(Settings.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
    }
    //</editor-fold>
    //</editor-fold>
    //</editor-fold>
    //</editor-fold>
    //</editor-fold>
    //</editor-fold>
    //</editor-fold>
    //</editor-fold>
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
            new Settings().setVisible(true);
        }
    });
}

    // Variables declaration - do not modify                     
    private javax.swing.JTable categorytbl;
    private javax.swing.JButton jButton2;
    private javax.swing.JButton jButton3;
    private javax.swing.JButton jButton4;
    private javax.swing.JButton jButton5;
    private javax.swing.JButton jButton6;
    private javax.swing.JButton jButton7;
    private javax.swing.JButton jButton8;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTabbedPane jTabbedPane1;
    private javax.swing.JButton saveButton;
    private javax.swing.JTextField thresholdField;
    private javax.swing.JLabel thresholdLabel;
    // End of variables declaration                   
}
