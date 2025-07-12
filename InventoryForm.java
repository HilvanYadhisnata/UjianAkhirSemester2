import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.scene.effect.DropShadow;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class InventoryForm {
    private Stage dialog;
    private InventoryItem result;
    private boolean isEditMode = false;
    
    public InventoryForm(Stage parent, InventoryItem item) {
        this.isEditMode = (item != null);
        createDialog(parent, item);
    }
    
    private void createDialog(Stage parent, InventoryItem item) {
        dialog = new Stage();
        dialog.initModality(Modality.WINDOW_MODAL);
        dialog.initOwner(parent);
        dialog.setTitle(isEditMode ? "Edit Inventaris" : "Tambah Inventaris");
        dialog.setResizable(false);
        
        VBox mainContainer = new VBox(20);
        mainContainer.setPadding(new Insets(30));
        mainContainer.setStyle("-fx-background-color: #ECF0F1;");
        
        // Header
        Label headerLabel = new Label(isEditMode ? "Edit Data Inventaris" : "Tambah Data Inventaris");
        headerLabel.setFont(Font.font("Arial", FontWeight.BOLD, 18));
        headerLabel.setTextFill(Color.web("#2C3E50"));
        
        // Form Container
        VBox formContainer = new VBox(15);
        formContainer.setPadding(new Insets(25));
        formContainer.setStyle("-fx-background-color: white; -fx-background-radius: 10;");
        formContainer.setEffect(new DropShadow(5, Color.rgb(0, 0, 0, 0.1)));
        
        // Form Fields
        TextField idField = createTextField("ID Inventaris", item != null ? item.getId() : "");
        idField.setDisable(isEditMode); // ID tidak bisa diubah saat edit
        
        TextField namaField = createTextField("Nama Item", item != null ? item.getNama() : "");
        
        ComboBox<String> kategoriCombo = createComboBox("Kategori", 
            new String[]{"Hardware", "Software", "Aksesoris", "Networking", "Furniture"});
        if (item != null) kategoriCombo.setValue(item.getKategori());
        
        TextField merkField = createTextField("Merk", item != null ? item.getMerk() : "");
        
        ComboBox<String> kondisiCombo = createComboBox("Kondisi", 
            new String[]{"Baik", "Rusak", "Perlu Perbaikan"});
        if (item != null) kondisiCombo.setValue(item.getKondisi());
        
        ComboBox<String> lokasiCombo = createComboBox("Lokasi", 
            new String[]{"Lab 1", "Lab 2", "Lab 3", "Server Room", "Gudang"});
        if (item != null) lokasiCombo.setValue(item.getLokasi());
        
        ComboBox<String> statusCombo = createComboBox("Status", 
            new String[]{"Tersedia", "Dipinjam", "Maintenance"});
        if (item != null) statusCombo.setValue(item.getStatus());
        
        DatePicker tanggalPicker = new DatePicker();
        tanggalPicker.setPromptText("Tanggal Masuk");
        if (item != null) {
            try {
                tanggalPicker.setValue(LocalDate.parse(item.getTanggalMasuk()));
            } catch (Exception e) {
                tanggalPicker.setValue(LocalDate.now());
            }
        } else {
            tanggalPicker.setValue(LocalDate.now());
        }
        
        // Buttons
        HBox buttonContainer = new HBox(15);
        buttonContainer.setStyle("-fx-alignment: center;");
        
        Button saveButton = new Button(isEditMode ? "Update" : "Simpan");
        saveButton.setPrefWidth(100);
        saveButton.setStyle("-fx-background-color: #27AE60; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 5;");
        
        Button cancelButton = new Button("Batal");
        cancelButton.setPrefWidth(100);
        cancelButton.setStyle("-fx-background-color: #E74C3C; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 5;");
        
        // Button actions
        saveButton.setOnAction(e -> {
            if (validateForm(idField, namaField, kategoriCombo, merkField, kondisiCombo, lokasiCombo, statusCombo, tanggalPicker)) {
                result = new InventoryItem(
                    idField.getText().trim(),
                    namaField.getText().trim(),
                    kategoriCombo.getValue(),
                    merkField.getText().trim(),
                    kondisiCombo.getValue(),
                    lokasiCombo.getValue(),
                    statusCombo.getValue(),
                    tanggalPicker.getValue().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))
                );
                dialog.close();
            }
        });
        
        cancelButton.setOnAction(e -> dialog.close());
        
        buttonContainer.getChildren().addAll(saveButton, cancelButton);
        
        // Add all components to form
        formContainer.getChildren().addAll(
            createFieldContainer("ID:", idField),
            createFieldContainer("Nama:", namaField),
            createFieldContainer("Kategori:", kategoriCombo),
            createFieldContainer("Merk:", merkField),
            createFieldContainer("Kondisi:", kondisiCombo),
            createFieldContainer("Lokasi:", lokasiCombo),
            createFieldContainer("Status:", statusCombo),
            createFieldContainer("Tanggal Masuk:", tanggalPicker),
            buttonContainer
        );
        
        mainContainer.getChildren().addAll(headerLabel, formContainer);
        
        Scene scene = new Scene(mainContainer, 400, 650);
        dialog.setScene(scene);
    }
    
    private TextField createTextField(String prompt, String value) {
        TextField field = new TextField(value);
        field.setPromptText(prompt);
        field.setPrefHeight(35);
        field.setStyle("-fx-background-radius: 5; -fx-border-radius: 5; -fx-border-color: #BDC3C7;");
        return field;
    }
    
    private ComboBox<String> createComboBox(String prompt, String[] items) {
        ComboBox<String> combo = new ComboBox<>();
        combo.getItems().addAll(items);
        combo.setPromptText(prompt);
        combo.setPrefHeight(35);
        combo.setStyle("-fx-background-radius: 5; -fx-border-radius: 5; -fx-border-color: #BDC3C7;");
        return combo;
    }
    
    private VBox createFieldContainer(String label, javafx.scene.Node field) {
        VBox container = new VBox(5);
        Label fieldLabel = new Label(label);
        fieldLabel.setFont(Font.font("Arial", FontWeight.BOLD, 12));
        fieldLabel.setTextFill(Color.web("#2C3E50"));
        container.getChildren().addAll(fieldLabel, field);
        return container;
    }
    
    private boolean validateForm(TextField idField, TextField namaField, ComboBox<String> kategoriCombo,
                                TextField merkField, ComboBox<String> kondisiCombo, ComboBox<String> lokasiCombo,
                                ComboBox<String> statusCombo, DatePicker tanggalPicker) {
        
        if (idField.getText().trim().isEmpty()) {
            showAlert("Error", "ID tidak boleh kosong!", Alert.AlertType.ERROR);
            return false;
        }
        
        if (namaField.getText().trim().isEmpty()) {
            showAlert("Error", "Nama tidak boleh kosong!", Alert.AlertType.ERROR);
            return false;
        }
        
        if (kategoriCombo.getValue() == null) {
            showAlert("Error", "Kategori harus dipilih!", Alert.AlertType.ERROR);
            return false;
        }
        
        if (merkField.getText().trim().isEmpty()) {
            showAlert("Error", "Merk tidak boleh kosong!", Alert.AlertType.ERROR);
            return false;
        }
        
        if (kondisiCombo.getValue() == null) {
            showAlert("Error", "Kondisi harus dipilih!", Alert.AlertType.ERROR);
            return false;
        }
        
        if (lokasiCombo.getValue() == null) {
            showAlert("Error", "Lokasi harus dipilih!", Alert.AlertType.ERROR);
            return false;
        }
        
        if (statusCombo.getValue() == null) {
            showAlert("Error", "Status harus dipilih!", Alert.AlertType.ERROR);
            return false;
        }
        
        if (tanggalPicker.getValue() == null) {
            showAlert("Error", "Tanggal masuk harus dipilih!", Alert.AlertType.ERROR);
            return false;
        }
        
        return true;
    }
    
    private void showAlert(String title, String message, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
    
    public InventoryItem showAndWait() {
        dialog.showAndWait();
        return result;
    }
}