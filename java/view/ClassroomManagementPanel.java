package view;

import model.Classroom;
import service.TimetableService;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class ClassroomManagementPanel extends JPanel {
    private TimetableService service;
    
    private JTextField idField, roomNameField, capacityField, computersField;
    private JComboBox<String> avEquipmentCombo, roomTypeCombo;
    private JTable classroomTable;
    private DefaultTableModel tableModel;
    private JButton addButton, updateButton, deleteButton, clearButton;
    
    public ClassroomManagementPanel() {
        service = new TimetableService();
        initializeUI();
        loadClassroomData();
    }
    
    private void initializeUI() {
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        // Header
        JLabel headerLabel = new JLabel("Classroom Management");
        headerLabel.setFont(new Font("Arial", Font.BOLD, 24));
        
        // Form Panel
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createTitledBorder("Add/Edit Classroom"),
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
        
        // Room Name field
        gbc.gridx = 0;
        gbc.gridy = 1;
        formPanel.add(new JLabel("Room Name:"), gbc);
        
        gbc.gridx = 1;
        roomNameField = new JTextField(10);
        formPanel.add(roomNameField, gbc);
        
        // Capacity field
        gbc.gridx = 0;
        gbc.gridy = 2;
        formPanel.add(new JLabel("Capacity:"), gbc);
        
        gbc.gridx = 1;
        capacityField = new JTextField(10);
        formPanel.add(capacityField, gbc);
        
        // A/V Equipment combo
        gbc.gridx = 0;
        gbc.gridy = 3;
        formPanel.add(new JLabel("A/V Equipment:"), gbc);
        
        gbc.gridx = 1;
        avEquipmentCombo = new JComboBox<>(new String[]{"Yes", "No"});
        formPanel.add(avEquipmentCombo, gbc);
        
        // Computers field
        gbc.gridx = 0;
        gbc.gridy = 4;
        formPanel.add(new JLabel("Computers:"), gbc);
        
        gbc.gridx = 1;
        computersField = new JTextField(10);
        formPanel.add(computersField, gbc);
        
        // Room Type combo
        gbc.gridx = 0;
        gbc.gridy = 5;
        formPanel.add(new JLabel("Room Type:"), gbc);
        
        gbc.gridx = 1;
        roomTypeCombo = new JComboBox<>(new String[]{"Lecture", "Lab"});
        formPanel.add(roomTypeCombo, gbc);
        
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
        gbc.gridy = 6;
        gbc.gridwidth = 2;
        formPanel.add(buttonPanel, gbc);
        
        // Table
        String[] columnNames = {"ID", "Room Name", "Capacity", "A/V Equipment", "Computers", "Room Type"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        classroomTable = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(classroomTable);
        
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
                addClassroom();
            }
        });
        
        updateButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                updateClassroom();
            }
        });
        
        deleteButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                deleteClassroom();
            }
        });
        
        clearButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                clearForm();
            }
        });
        
        classroomTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && classroomTable.getSelectedRow() != -1) {
                populateForm();
            }
        });
    }
    
    private void loadClassroomData() {
        // Clear existing data
        tableModel.setRowCount(0);
        
        // Load from service
        for (Classroom classroom : service.getAllClassrooms()) {
            Object[] row = {
                classroom.getId(),
                "Room " + classroom.getId(),
                classroom.getCapacity(),
                classroom.hasAV() ? "Yes" : "No",
                classroom.getComputers(),
                classroom.getRoomType()
            };
            tableModel.addRow(row);
        }
    }
    
    private void populateForm() {
        int selectedRow = classroomTable.getSelectedRow();
        if (selectedRow != -1) {
            idField.setText(tableModel.getValueAt(selectedRow, 0).toString());
            roomNameField.setText(tableModel.getValueAt(selectedRow, 1).toString());
            capacityField.setText(tableModel.getValueAt(selectedRow, 2).toString());
            avEquipmentCombo.setSelectedItem(tableModel.getValueAt(selectedRow, 3));
            computersField.setText(tableModel.getValueAt(selectedRow, 4).toString());
            roomTypeCombo.setSelectedItem(tableModel.getValueAt(selectedRow, 5));
        }
    }
    
    private void clearForm() {
        idField.setText("");
        roomNameField.setText("");
        capacityField.setText("");
        avEquipmentCombo.setSelectedIndex(0);
        computersField.setText("");
        roomTypeCombo.setSelectedIndex(0);
        classroomTable.clearSelection();
    }
    
    private void addClassroom() {
        try {
            int id = Integer.parseInt(idField.getText());
            int capacity = Integer.parseInt(capacityField.getText());
            boolean avEquipment = avEquipmentCombo.getSelectedItem().equals("Yes");
            int computers = Integer.parseInt(computersField.getText());
            String roomType = (String) roomTypeCombo.getSelectedItem();
            
            Classroom classroom = new Classroom(id, capacity, avEquipment, computers, roomType);
            service.addClassroom(classroom);
            loadClassroomData();
            clearForm();
            JOptionPane.showMessageDialog(this, "Classroom added successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Please enter valid numbers for ID, Capacity, and Computers", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void updateClassroom() {
        int selectedRow = classroomTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a classroom to update", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        try {
            int id = Integer.parseInt(idField.getText());
            int capacity = Integer.parseInt(capacityField.getText());
            boolean avEquipment = avEquipmentCombo.getSelectedItem().equals("Yes");
            int computers = Integer.parseInt(computersField.getText());
            String roomType = (String) roomTypeCombo.getSelectedItem();
            
            Classroom classroom = new Classroom(id, capacity, avEquipment, computers, roomType);
            service.updateClassroom(classroom);
            loadClassroomData();
            clearForm();
            JOptionPane.showMessageDialog(this, "Classroom updated successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Please enter valid numbers for ID, Capacity, and Computers", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void deleteClassroom() {
        int selectedRow = classroomTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a classroom to delete", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        int confirm = JOptionPane.showConfirmDialog(this, "Are you sure you want to delete this classroom?", "Confirm Delete", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            int id = Integer.parseInt(tableModel.getValueAt(selectedRow, 0).toString());
            service.deleteClassroom(id);
            loadClassroomData();
            clearForm();
            JOptionPane.showMessageDialog(this, "Classroom deleted successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
        }
    }
}