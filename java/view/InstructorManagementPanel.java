package view;

import model.Instructor;
import service.TimetableService;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class InstructorManagementPanel extends JPanel {
    private TimetableService service;
    
    private JTextField idField, firstNameField, lastNameField;
    private JComboBox<String> departmentCombo;
    private JTable instructorTable;
    private DefaultTableModel tableModel;
    private JButton addButton, updateButton, deleteButton, clearButton;
    
    public InstructorManagementPanel() {
        service = new TimetableService();
        initializeUI();
        loadInstructorData();
    }
    
    private void initializeUI() {
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        // Header
        JLabel headerLabel = new JLabel("Instructor Management");
        headerLabel.setFont(new Font("Arial", Font.BOLD, 24));
        
        // Form Panel
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createTitledBorder("Add/Edit Instructor"),
            BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));
        
        // Grid constraints
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 5, 5, 5);
        
        // ID field
        gbc.gridx = 0;
        gbc.gridy = 0;
        formPanel.add(new JLabel("ID:"), gbc);
        
        gbc.gridx = 1;
        idField = new JTextField(10);
        formPanel.add(idField, gbc);
        
        // First Name field
        gbc.gridx = 0;
        gbc.gridy = 1;
        formPanel.add(new JLabel("First Name:"), gbc);
        
        gbc.gridx = 1;
        firstNameField = new JTextField(10);
        formPanel.add(firstNameField, gbc);
        
        // Last Name field
        gbc.gridx = 0;
        gbc.gridy = 2;
        formPanel.add(new JLabel("Last Name:"), gbc);
        
        gbc.gridx = 1;
        lastNameField = new JTextField(10);
        formPanel.add(lastNameField, gbc);
        
        // Department combo
        gbc.gridx = 0;
        gbc.gridy = 3;
        formPanel.add(new JLabel("Department:"), gbc);
        
        gbc.gridx = 1;
        departmentCombo = new JComboBox<>(new String[]{"CSE", "Mathematics", "Electrical", "Mechanical", "Physics", "Chemistry"});
        formPanel.add(departmentCombo, gbc);
        
        // Buttons panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        
        addButton = new JButton("Add");
        updateButton = new JButton("Update");
        deleteButton = new JButton("Delete");
        clearButton = new JButton("Clear");
        
        buttonPanel.add(addButton);
        buttonPanel.add(updateButton);
        buttonPanel.add(deleteButton);
        buttonPanel.add(clearButton);
        
        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.gridwidth = 2;
        formPanel.add(buttonPanel, gbc);
        
        // Table
        String[] columnNames = {"ID", "First Name", "Last Name", "Department"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        instructorTable = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(instructorTable);
        
        // Add components to main panel
        add(headerLabel, BorderLayout.NORTH);
        add(formPanel, BorderLayout.WEST);
        add(scrollPane, BorderLayout.CENTER);
        
        // Add action listeners to buttons
        setupActionListeners();
    }
    
    private void setupActionListeners() {
        addButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                addInstructor();
            }
        });
        
        updateButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                updateInstructor();
            }
        });
        
        deleteButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                deleteInstructor();
            }
        });
        
        clearButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                clearForm();
            }
        });
        
        instructorTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && instructorTable.getSelectedRow() != -1) {
                populateForm();
            }
        });
    }
    
    private void loadInstructorData() {
        // Clear existing data
        tableModel.setRowCount(0);
        
        // Load from service
        for (Instructor instructor : service.getAllInstructors()) {
            Object[] row = {
                instructor.getId(),
                instructor.getFirstName(),
                instructor.getLastName(),
                instructor.getDepartment()
            };
            tableModel.addRow(row);
        }
    }
    
    private void populateForm() {
        int selectedRow = instructorTable.getSelectedRow();
        if (selectedRow != -1) {
            idField.setText(tableModel.getValueAt(selectedRow, 0).toString());
            firstNameField.setText(tableModel.getValueAt(selectedRow, 1).toString());
            lastNameField.setText(tableModel.getValueAt(selectedRow, 2).toString());
            departmentCombo.setSelectedItem(tableModel.getValueAt(selectedRow, 3));
        }
    }
    
    private void clearForm() {
        idField.setText("");
        firstNameField.setText("");
        lastNameField.setText("");
        departmentCombo.setSelectedIndex(0);
        instructorTable.clearSelection();
    }
    
    private void addInstructor() {
        try {
            int id = Integer.parseInt(idField.getText());
            String firstName = firstNameField.getText();
            String lastName = lastNameField.getText();
            String department = (String) departmentCombo.getSelectedItem();
            
            Instructor instructor = new Instructor(id, firstName, lastName, department);
            service.addInstructor(instructor);
            loadInstructorData();
            clearForm();
            JOptionPane.showMessageDialog(this, "Instructor added successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Please enter a valid ID", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void updateInstructor() {
        int selectedRow = instructorTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select an instructor to update", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        try {
            int id = Integer.parseInt(idField.getText());
            String firstName = firstNameField.getText();
            String lastName = lastNameField.getText();
            String department = (String) departmentCombo.getSelectedItem();
            
            Instructor instructor = new Instructor(id, firstName, lastName, department);
            service.updateInstructor(instructor);
            loadInstructorData();
            clearForm();
            JOptionPane.showMessageDialog(this, "Instructor updated successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Please enter a valid ID", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void deleteInstructor() {
        int selectedRow = instructorTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select an instructor to delete", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        int confirm = JOptionPane.showConfirmDialog(this, "Are you sure you want to delete this instructor?", "Confirm Delete", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            int id = Integer.parseInt(tableModel.getValueAt(selectedRow, 0).toString());
            service.deleteInstructor(id);
            loadInstructorData();
            clearForm();
            JOptionPane.showMessageDialog(this, "Instructor deleted successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
        }
    }
}