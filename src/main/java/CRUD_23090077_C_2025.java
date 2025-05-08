import com.mongodb.client.*;
import org.bson.Document;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;
import java.util.Vector;

public class CRUD_23090077_C_2025 extends JFrame {
    private MongoCollection<Document> collection;
    private DefaultTableModel tableModel;
    private JTable table;
    private JTextField tfNama, tfNim, tfKelas;

    public CRUD_23090077_C_2025() {
        MongoClient mongoClient = MongoClients.create("mongodb://localhost:27017");
        MongoDatabase database = mongoClient.getDatabase("uts_23090077_C_2025");
        collection = database.getCollection("coll_23090077_C_2025");

        setTitle("CRUD MongoDB GUI - 23090026");
        setSize(700, 500);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        JPanel formPanel = new JPanel(new GridLayout(2, 4, 10, 10));
        tfNama = new JTextField();
        tfNim = new JTextField();
        tfKelas = new JTextField();
        JButton btnAdd = new JButton("Add");

        formPanel.add(new JLabel("Nama:"));
        formPanel.add(tfNama);
        formPanel.add(new JLabel("NIM:"));
        formPanel.add(tfNim);
        formPanel.add(new JLabel("Kelas:"));
        formPanel.add(tfKelas);
        formPanel.add(new JLabel(""));
        formPanel.add(btnAdd);

        add(formPanel, BorderLayout.NORTH);

        tableModel = new DefaultTableModel(new Object[]{"Nama", "NIM", "Kelas"}, 0);
        table = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(table);
        add(scrollPane, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel();
        JButton btnUpdate = new JButton("Update");
        JButton btnDelete = new JButton("Delete");
        JButton btnSearch = new JButton("Search");
        JButton btnRefresh = new JButton("Refresh");

        buttonPanel.add(btnUpdate);
        buttonPanel.add(btnDelete);
        buttonPanel.add(btnSearch);
        buttonPanel.add(btnRefresh);
        add(buttonPanel, BorderLayout.SOUTH);

        btnAdd.addActionListener(e -> addData());
        btnRefresh.addActionListener(e -> loadData());
        btnDelete.addActionListener(e -> deleteData());
        btnUpdate.addActionListener(e -> updateData());
        btnSearch.addActionListener(e -> searchData());

        loadData();
        setVisible(true);
    }

    private void addData() {
        String nama = tfNama.getText();
        String nim = tfNim.getText();
        String kelas = tfKelas.getText();

        if (!nama.isEmpty() && !nim.isEmpty() && !kelas.isEmpty()) {
            try {
                Document doc = new Document("_id", nim)
                        .append("nama", nama)
                        .append("kelas", kelas);
                collection.insertOne(doc);
                loadData();
                clearFields();
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "NIM sudah digunakan!", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void loadData() {
        tableModel.setRowCount(0);
        List<Document> docs = collection.find().into(new Vector<>());
        for (Document doc : docs) {
            tableModel.addRow(new Object[]{
                    doc.getString("nama"),
                    doc.getString("_id"),
                    doc.getString("kelas")
            });
        }
    }

    private void deleteData() {
        int row = table.getSelectedRow();
        if (row >= 0) {
            String nim = tableModel.getValueAt(row, 1).toString();
            collection.deleteOne(new Document("_id", nim));
            loadData();
        }
    }

    private void updateData() {
        int row = table.getSelectedRow();
        if (row >= 0) {
            String nim = tableModel.getValueAt(row, 1).toString();
            String namaBaru = tfNama.getText();
            String kelasBaru = tfKelas.getText();

            collection.updateOne(new Document("_id", nim),
                    new Document("$set", new Document("nama", namaBaru)
                            .append("kelas", kelasBaru)));
            loadData();
            clearFields();
        }
    }

    private void searchData() {
        String nim = JOptionPane.showInputDialog(this, "Masukkan NIM yang dicari:");
        if (nim != null && !nim.isEmpty()) {
            Document doc = collection.find(new Document("_id", nim)).first();
            if (doc != null) {
                tfNama.setText(doc.getString("nama"));
                tfNim.setText(doc.getString("_id"));
                tfKelas.setText(doc.getString("kelas"));
            } else {
                JOptionPane.showMessageDialog(this, "Data tidak ditemukan.");
            }
        }
    }

    private void clearFields() {
        tfNama.setText("");
        tfNim.setText("");
        tfKelas.setText("");
    }

    public static void main(String[] args) {
        new CRUD_23090077_C_2025();
    }
}
