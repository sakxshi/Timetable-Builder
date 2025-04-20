package view;

import model.Instructor;
import service.TimetableService;
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;
import static view.UIConstants.*;
import util.IconFactory;

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
        setBackground(SECTION_BG_COLOR);

        JLabel headerLabel = new JLabel("Instructor Management");
        headerLabel.setFont(HEADING_FONT);

        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(WHITE_COLOR);
        formPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(new Color(220, 220, 220)),
                "Add New Instructor",
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
        idLabel.setFont(SMALL_FONT);
        idField = new JTextField(10);
        styleTextField(idField);
        idFieldPanel.add(idLabel, BorderLayout.NORTH);
        idFieldPanel.add(idField, BorderLayout.CENTER);

        JPanel firstNameFieldPanel = new JPanel(new BorderLayout());
        firstNameFieldPanel.setOpaque(false);
        JLabel firstNameLabel = new JLabel("First Name");
        firstNameLabel.setFont(SMALL_FONT);
        firstNameField = new JTextField(10);
        styleTextField(firstNameField);
        firstNameFieldPanel.add(firstNameLabel, BorderLayout.NORTH);
        firstNameFieldPanel.add(firstNameField, BorderLayout.CENTER);

        JPanel lastNameFieldPanel = new JPanel(new BorderLayout());
        lastNameFieldPanel.setOpaque(false);
        JLabel lastNameLabel = new JLabel("Last Name");
        lastNameLabel.setFont(SMALL_FONT);
        lastNameField = new JTextField(10);
        styleTextField(lastNameField);
        lastNameFieldPanel.add(lastNameLabel, BorderLayout.NORTH);
        lastNameFieldPanel.add(lastNameField, BorderLayout.CENTER);

        JPanel departmentPanel = new JPanel(new BorderLayout());
        departmentPanel.setOpaque(false);
        JLabel departmentLabel = new JLabel("Department");
        departmentLabel.setFont(SMALL_FONT);
        departmentCombo = new JComboBox<>(new String[]{"CSE", "Mathematics", "Electrical", "Mechanical", "Physics", "Chemistry"});
        departmentCombo.setFont(NORMAL_FONT);
        departmentPanel.add(departmentLabel, BorderLayout.NORTH);
        departmentPanel.add(departmentCombo, BorderLayout.CENTER);

        fieldsPanel.add(idFieldPanel);
        fieldsPanel.add(firstNameFieldPanel);
        fieldsPanel.add(lastNameFieldPanel);
        fieldsPanel.add(departmentPanel);

        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        formPanel.add(fieldsPanel, gbc);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        buttonPanel.setOpaque(false);
        
        addButton = createStyledButton("Add Instructor", PRIMARY_COLOR);
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

        String[] columnNames = {"ID", "First Name", "Last Name", "Department"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        instructorTable = new JTable(tableModel);
        instructorTable.setFont(NORMAL_FONT);
        instructorTable.setRowHeight(40);
        instructorTable.setShowGrid(true);
        instructorTable.setGridColor(new Color(230, 230, 230));

        JTableHeader header = instructorTable.getTableHeader();
        header.setFont(SMALL_FONT);
        header.setBackground(new Color(240, 240, 240));
        header.setForeground(HEADING_COLOR);

        JScrollPane scrollPane = new JScrollPane(instructorTable);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(220, 220, 220)));

        JPanel mainContentPanel = new JPanel(new BorderLayout(0, 20));
        mainContentPanel.setOpaque(false);

        JPanel tablePanel = new JPanel(new BorderLayout());
        tablePanel.setBackground(WHITE_COLOR);
        tablePanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(220, 220, 220)),
            BorderFactory.createEmptyBorder(0, 0, 0, 0)
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
        tableModel.setRowCount(0);
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
