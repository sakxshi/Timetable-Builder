package view;

import model.Classroom;
import service.TimetableService;
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;
import static view.UIConstants.*;
import util.IconFactory;

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
        setBackground(SECTION_BG_COLOR);

        JLabel headerLabel = new JLabel("Classroom Management");
        headerLabel.setFont(new Font("Arial", Font.BOLD, 24));

        JPanel formPanel = new JPanel(new GridBagLayout());
    formPanel.setBackground(WHITE_COLOR);
    formPanel.setBorder(BorderFactory.createCompoundBorder(
        BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(new Color(220, 220, 220)),
            "Add New Classroom",
            TitledBorder.DEFAULT_JUSTIFICATION,
            TitledBorder.DEFAULT_POSITION,
            SUBHEADING_FONT
        ),
        BorderFactory.createEmptyBorder(15, 15, 15, 15)
    ));


        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.weightx = 1.0;

        JPanel fieldsPanel = new JPanel(new GridLayout(0, 2, 10, 10));
        fieldsPanel.setOpaque(false);

        JPanel idFieldPanel = new JPanel(new BorderLayout());
        idFieldPanel.setOpaque(false);
        JLabel idLabel = new JLabel("ID");
        idLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        idField = new JTextField(10);
        styleTextField(idField);
        idFieldPanel.add(idLabel, BorderLayout.NORTH);
        idFieldPanel.add(idField, BorderLayout.CENTER);

        JPanel roomNameFieldPanel = new JPanel(new BorderLayout());
        roomNameFieldPanel.setOpaque(false);
        JLabel roomNameLabel = new JLabel("Room Name");
        roomNameLabel.setFont(SMALL_FONT);
        roomNameField = new JTextField(10);
        styleTextField(roomNameField);
        roomNameFieldPanel.add(roomNameLabel, BorderLayout.NORTH);
        roomNameFieldPanel.add(roomNameField, BorderLayout.CENTER);

        JPanel capacityFieldPanel = new JPanel(new BorderLayout());
        capacityFieldPanel.setOpaque(false);
        JLabel capacityLabel = new JLabel("Capacity");
        capacityLabel.setFont(SMALL_FONT);
        capacityField = new JTextField(10);
        styleTextField(capacityField);
        capacityFieldPanel.add(capacityLabel, BorderLayout.NORTH);
        capacityFieldPanel.add(capacityField, BorderLayout.CENTER);

        JPanel avEquipmentPanel = new JPanel(new BorderLayout());
        avEquipmentPanel.setOpaque(false);
        JLabel avEquipmentLabel = new JLabel("AV Equipment");
        avEquipmentLabel.setFont(SMALL_FONT);
        avEquipmentCombo = new JComboBox<>(new String[]{"Yes", "No"});
        avEquipmentCombo.setFont(NORMAL_FONT);
        avEquipmentPanel.add(avEquipmentLabel, BorderLayout.NORTH);
        avEquipmentPanel.add(avEquipmentCombo, BorderLayout.CENTER);

        JPanel computersFieldPanel = new JPanel(new BorderLayout());
        computersFieldPanel.setOpaque(false);
        JLabel computersLabel = new JLabel("Number of Computers");
        computersLabel.setFont(SMALL_FONT);
        computersField = new JTextField(10);
        styleTextField(computersField);
        computersFieldPanel.add(computersLabel, BorderLayout.NORTH);
        computersFieldPanel.add(computersField, BorderLayout.CENTER);

        JPanel roomTypePanel = new JPanel(new BorderLayout());
        roomTypePanel.setOpaque(false);
        JLabel roomTypeLabel = new JLabel("Room Type");
        roomTypeLabel.setFont(SMALL_FONT);
        roomTypeCombo = new JComboBox<>(new String[]{"Lecture", "Lab"});
        roomTypeCombo.setFont(NORMAL_FONT);
        roomTypePanel.add(roomTypeLabel, BorderLayout.NORTH);
        roomTypePanel.add(roomTypeCombo, BorderLayout.CENTER);

        fieldsPanel.add(idFieldPanel);
        fieldsPanel.add(roomNameFieldPanel);
        fieldsPanel.add(capacityFieldPanel);
        fieldsPanel.add(avEquipmentPanel);
        fieldsPanel.add(computersFieldPanel);
        fieldsPanel.add(roomTypePanel);

        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        formPanel.add(fieldsPanel, gbc);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        buttonPanel.setOpaque(false);
        buttonPanel.setLayout(new FlowLayout(FlowLayout.LEFT, 15, 10)); 
        
        addButton = createStyledButton("Add Classroom", PRIMARY_COLOR);
addButton.setIcon(IconFactory.createPlusIcon());
        addButton.setIcon(IconFactory.createPlusIcon());
        updateButton = createStyledButton("Update", SECONDARY_COLOR);
        deleteButton = createStyledButton("Delete", new Color(192, 57, 43));
        clearButton = createStyledButton("Clear", new Color(108, 117, 125));

        buttonPanel.add(addButton);
        buttonPanel.add(updateButton);
        buttonPanel.add(deleteButton);
        buttonPanel.add(clearButton);

        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridwidth = 2;
        formPanel.add(buttonPanel, gbc);

        String[] columnNames = {"ID", "Room Name", "Capacity", "AV Equipment", "Computers", "Room Type"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        classroomTable = new JTable(tableModel);
        classroomTable.setFont(new Font("Arial", Font.PLAIN, 14));
        classroomTable.setRowHeight(30);
        classroomTable.setShowGrid(true);
        classroomTable.setGridColor(new Color(230, 230, 230));
        classroomTable.setBackground(new Color(255, 255, 255));
        classroomTable.setSelectionBackground(new Color(200, 220, 255));

        JTableHeader header = classroomTable.getTableHeader();
        header.setFont(new Font("Arial", Font.BOLD, 16));
        header.setBackground(new Color(230, 230, 250)); 
        header.setForeground(new Color(50, 50, 50));  

        JScrollPane scrollPane = new JScrollPane(classroomTable);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(220, 220, 220)));

        JPanel mainContentPanel = new JPanel(new BorderLayout(0, 20));
        mainContentPanel.setOpaque(false);

        JPanel tablePanel = new JPanel(new BorderLayout());
        tablePanel.setBackground(WHITE_COLOR);
        tablePanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(200, 200, 200), 1),
            BorderFactory.createLineBorder(new Color(200, 200, 200), 1),
        ));
        tablePanel.add(scrollPane, BorderLayout.CENTER);

        mainContentPanel.add(formPanel, BorderLayout.NORTH);
        mainContentPanel.add(tablePanel, BorderLayout.CENTER);

        add(headerLabel, BorderLayout.NORTH);
        add(mainContentPanel, BorderLayout.CENTER);

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
        tableModel.setRowCount(0);
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
