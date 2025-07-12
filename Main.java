import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;
import javafx.scene.effect.DropShadow;
import javafx.animation.FadeTransition;
import javafx.util.Duration;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;

import java.util.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Main extends Application {
    private Stage primaryStage;
    private String currentUser;
    private String currentRole;
    private VBox currentMainContent;
    private ObservableList<InventoryItem> inventoryData;
    private TableView<InventoryItem> inventoryTable;
    private FilteredList<InventoryItem> filteredData;
    
    // Data pengguna (username:password:role)
    private Map<String, String[]> users = new HashMap<>();
    
    @Override
    public void start(Stage primaryStage) {
        this.primaryStage = primaryStage;
        initializeUsers();
        loadInventoryData();
        showLoginPage();
    }
    
    private void initializeUsers() {
        users.put("admin", new String[]{"admin123", "Admin"});
        users.put("petugas", new String[]{"petugas123", "Petugas Lab"});
    }
    
    private void loadInventoryData() {
        inventoryData = CSVManager.loadInventoryData();
        filteredData = new FilteredList<>(inventoryData, p -> true);
    }
    
    private void showLoginPage() {
        VBox loginContainer = new VBox(20);
        loginContainer.setAlignment(Pos.CENTER);
        loginContainer.setPadding(new Insets(40));
        
        // Header
        Label titleLabel = new Label("Lab Inventory System");
        titleLabel.setFont(Font.font("Arial", FontWeight.BOLD, 32));
        titleLabel.setTextFill(Color.web("#2C3E50"));
        
        Label subtitleLabel = new Label("Sistem Pengelolaan Inventaris Laboratorium");
        subtitleLabel.setFont(Font.font("Arial", FontWeight.NORMAL, 14));
        subtitleLabel.setTextFill(Color.web("#7F8C8D"));
        
        // Login Form
        VBox formBox = new VBox(15);
        formBox.setAlignment(Pos.CENTER);
        formBox.setPadding(new Insets(30));
        formBox.setStyle("-fx-background-color: white; -fx-background-radius: 10;");
        formBox.setEffect(new DropShadow(10, Color.rgb(0, 0, 0, 0.1)));
        
        Label loginLabel = new Label("Login");
        loginLabel.setFont(Font.font("Arial", FontWeight.BOLD, 20));
        loginLabel.setTextFill(Color.web("#2C3E50"));
        
        TextField usernameField = new TextField();
        usernameField.setPromptText("Username");
        usernameField.setPrefWidth(250);
        usernameField.setPrefHeight(35);
        usernameField.setStyle("-fx-background-radius: 5; -fx-border-radius: 5; -fx-border-color: #BDC3C7;");
        
        PasswordField passwordField = new PasswordField();
        passwordField.setPromptText("Password");
        passwordField.setPrefWidth(250);
        passwordField.setPrefHeight(35);
        passwordField.setStyle("-fx-background-radius: 5; -fx-border-radius: 5; -fx-border-color: #BDC3C7;");
        
        Button loginButton = new Button("Login");
        loginButton.setPrefWidth(250);
        loginButton.setPrefHeight(40);
        loginButton.setStyle("-fx-background-color: #3498DB; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 5;");
        
        loginButton.setOnMouseEntered(e -> loginButton.setStyle("-fx-background-color: #2980B9; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 5;"));
        loginButton.setOnMouseExited(e -> loginButton.setStyle("-fx-background-color: #3498DB; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 5;"));       
        // Login button action
        loginButton.setOnAction(e -> {
            String username = usernameField.getText().trim();
            String password = passwordField.getText().trim();
            
            if (authenticateUser(username, password)) {
                showDashboard();
            } else {
                showAlert("Login Gagal", "Username atau password salah!", Alert.AlertType.ERROR);
            }
        });
        
        // Enter key support
        passwordField.setOnAction(e -> loginButton.fire());
        
        formBox.getChildren().addAll(loginLabel, usernameField, passwordField, loginButton);
        loginContainer.getChildren().addAll(titleLabel, subtitleLabel, formBox);
        
        // Background gradient
        loginContainer.setStyle("-fx-background-color: linear-gradient(to bottom, #ECF0F1, #BDC3C7);");
        
        Scene loginScene = new Scene(loginContainer, 600, 500);
        primaryStage.setTitle("Lab Inventory System - Login");
        primaryStage.setScene(loginScene);
        primaryStage.setResizable(false);
        primaryStage.show();
    }
    
    private boolean authenticateUser(String username, String password) {
        if (users.containsKey(username)) {
            String[] userData = users.get(username);
            if (userData[0].equals(password)) {
                currentUser = username;
                currentRole = userData[1];
                return true;
            }
        }
        return false;
    }
    
    private void showDashboard() {
        BorderPane dashboardLayout = new BorderPane();
        
        // Header
        HBox header = createHeader();
        dashboardLayout.setTop(header);
        
        // Sidebar
        VBox sidebar = createSidebar();
        dashboardLayout.setLeft(sidebar);
        
        // Main content
        currentMainContent = createMainContent();
        dashboardLayout.setCenter(currentMainContent);
        
        Scene dashboardScene = new Scene(dashboardLayout, 1200, 800);
        primaryStage.setTitle("Lab Inventory System - Dashboard");
        primaryStage.setScene(dashboardScene);
        primaryStage.setMaximized(true);
        
        // Fade transition
        FadeTransition fade = new FadeTransition(Duration.millis(500), dashboardLayout);
        fade.setFromValue(0.0);
        fade.setToValue(1.0);
        fade.play();
    }
    
    private HBox createHeader() {
        HBox header = new HBox();
        header.setPadding(new Insets(15, 20, 15, 20));
        header.setStyle("-fx-background-color: #2C3E50;");
        header.setAlignment(Pos.CENTER_LEFT);
        
        Label titleLabel = new Label("Lab Inventory System");
        titleLabel.setFont(Font.font("Arial", FontWeight.BOLD, 18));
        titleLabel.setTextFill(Color.WHITE);
        
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        
        Label userLabel = new Label("Welcome, " + currentRole + " (" + currentUser + ")");
        userLabel.setFont(Font.font("Arial", FontWeight.NORMAL, 12));
        userLabel.setTextFill(Color.web("#ECF0F1"));
        
        Button logoutButton = new Button("Logout");
        logoutButton.setStyle("-fx-background-color: #E74C3C; -fx-text-fill: white; -fx-background-radius: 5;");
        logoutButton.setOnAction(e -> showLoginPage());
        
        header.getChildren().addAll(titleLabel, spacer, userLabel, logoutButton);
        return header;
    }
    
    private VBox createSidebar() {
        VBox sidebar = new VBox(10);
        sidebar.setPadding(new Insets(20));
        sidebar.setPrefWidth(250);
        sidebar.setStyle("-fx-background-color: #34495E;");
        
        Label menuLabel = new Label("MENU");
        menuLabel.setFont(Font.font("Arial", FontWeight.BOLD, 14));
        menuLabel.setTextFill(Color.web("#BDC3C7"));
        
        Button dashboardBtn = createMenuButton("📊 Dashboard", true);
        Button inventoryBtn = createMenuButton("📦 Data Inventaris");
        Button checkBtn = createMenuButton("🔍 Cek Ketersediaan");
        Button borrowBtn = createMenuButton("📋 Peminjaman");
        Button returnBtn = createMenuButton("↩️ Pengembalian");
        Button maintenanceBtn = createMenuButton("🔧 Maintenance");
        Button reportBtn = createMenuButton("📄 Laporan");
        
        // Menu actions
        dashboardBtn.setOnAction(e -> switchToContent(createMainContent(), dashboardBtn));
        inventoryBtn.setOnAction(e -> switchToContent(createInventoryContent(), inventoryBtn));
        
        sidebar.getChildren().addAll(menuLabel, dashboardBtn, inventoryBtn, checkBtn, borrowBtn, returnBtn, maintenanceBtn, reportBtn);
        return sidebar;
    }
    
    private void switchToContent(VBox newContent, Button activeButton) {
        // Update active button style
        VBox sidebar = (VBox) ((BorderPane) primaryStage.getScene().getRoot()).getLeft();
        for (int i = 1; i < sidebar.getChildren().size(); i++) {
            Button btn = (Button) sidebar.getChildren().get(i);
            btn.setStyle("-fx-background-color: transparent; -fx-text-fill: #ECF0F1; -fx-background-radius: 5;");
        }
        activeButton.setStyle("-fx-background-color: #3498DB; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 5;");
        
        // Switch content
        ((BorderPane) primaryStage.getScene().getRoot()).setCenter(newContent);
        currentMainContent = newContent;
    }
    
    private Button createMenuButton(String text, boolean active) {
        Button button = new Button(text);
        button.setPrefWidth(210);
        button.setPrefHeight(40);
        button.setAlignment(Pos.CENTER_LEFT);
        
        if (active) {
            button.setStyle("-fx-background-color: #3498DB; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 5;");
        } else {
            button.setStyle("-fx-background-color: transparent; -fx-text-fill: #ECF0F1; -fx-background-radius: 5;");
            button.setOnMouseEntered(e -> button.setStyle("-fx-background-color: #2C3E50; -fx-text-fill: white; -fx-background-radius: 5;"));
            button.setOnMouseExited(e -> button.setStyle("-fx-background-color: transparent; -fx-text-fill: #ECF0F1; -fx-background-radius: 5;"));
        }
        
        return button;
    }
    
    private Button createMenuButton(String text) {
        return createMenuButton(text, false);
    }
    
    private VBox createMainContent() {
        VBox mainContent = new VBox(20);
        mainContent.setPadding(new Insets(30));
        mainContent.setStyle("-fx-background-color: #ECF0F1;");
        
        // Welcome section
        Label welcomeLabel = new Label("Dashboard");
        welcomeLabel.setFont(Font.font("Arial", FontWeight.BOLD, 24));
        welcomeLabel.setTextFill(Color.web("#2C3E50"));
        
        Label dateLabel = new Label("Tanggal: " + LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd MMMM yyyy, HH:mm")));
        dateLabel.setFont(Font.font("Arial", FontWeight.NORMAL, 14));
        dateLabel.setTextFill(Color.web("#7F8C8D"));
        
        // Statistics cards
        HBox statsContainer = new HBox(20);
        statsContainer.setAlignment(Pos.CENTER);
        
        long totalItems = inventoryData.size();
        long borrowedItems = inventoryData.stream().filter(item -> "Dipinjam".equals(item.getStatus())).count();
        long maintenanceItems = inventoryData.stream().filter(item -> "Maintenance".equals(item.getStatus())).count();
        long availableItems = inventoryData.stream().filter(item -> "Tersedia".equals(item.getStatus())).count();
        
        VBox totalCard = createStatCard("Total Inventaris", String.valueOf(totalItems), "#3498DB");
        VBox borrowedCard = createStatCard("Sedang Dipinjam", String.valueOf(borrowedItems), "#E67E22");
        VBox maintenanceCard = createStatCard("Maintenance", String.valueOf(maintenanceItems), "#E74C3C");
        VBox availableCard = createStatCard("Tersedia", String.valueOf(availableItems), "#27AE60");
        
        statsContainer.getChildren().addAll(totalCard, borrowedCard, maintenanceCard, availableCard);
        
        // Quick actions
        Label quickActionsLabel = new Label("Aksi Cepat");
        quickActionsLabel.setFont(Font.font("Arial", FontWeight.BOLD, 18));
        quickActionsLabel.setTextFill(Color.web("#2C3E50"));
        
        HBox actionsContainer = new HBox(15);
        actionsContainer.setAlignment(Pos.CENTER_LEFT);
        
        Button checkButton = createActionButton("Cek Ketersediaan", "#3498DB");
        Button borrowButton = createActionButton("Peminjaman Baru", "#27AE60");
        Button returnButton = createActionButton("Pengembalian", "#E67E22");
        Button reportButton = createActionButton("Buat Laporan", "#9B59B6");
        
        actionsContainer.getChildren().addAll(checkButton, borrowButton, returnButton, reportButton);
        
        mainContent.getChildren().addAll(welcomeLabel, dateLabel, new Separator(), statsContainer, new Separator(), quickActionsLabel, actionsContainer);
        
        return mainContent;
    }
    
    private VBox createInventoryContent() {
        VBox inventoryContent = new VBox(20);
        inventoryContent.setPadding(new Insets(30));
        inventoryContent.setStyle("-fx-background-color: #ECF0F1;");
        
        // Header
        HBox headerBox = new HBox(20);
        headerBox.setAlignment(Pos.CENTER_LEFT);
        
        Label titleLabel = new Label("Data Inventaris");
        titleLabel.setFont(Font.font("Arial", FontWeight.BOLD, 24));
        titleLabel.setTextFill(Color.web("#2C3E50"));
        
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        
        Button addButton = new Button("+ Tambah Item");
        addButton.setStyle("-fx-background-color: #27AE60; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 5;");
        addButton.setOnAction(e -> showAddItemDialog());
        
        Button refreshButton = new Button("🔄 Refresh");
        refreshButton.setStyle("-fx-background-color: #3498DB; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 5;");
        refreshButton.setOnAction(e -> refreshInventoryData());
        
        headerBox.getChildren().addAll(titleLabel, spacer, addButton, refreshButton);
        
        // Search and Filter
        HBox searchBox = new HBox(15);
        searchBox.setAlignment(Pos.CENTER_LEFT);
        
        Label searchLabel = new Label("Cari:");
        searchLabel.setFont(Font.font("Arial", FontWeight.BOLD, 12));
        
        TextField searchField = new TextField();
        searchField.setPromptText("Cari berdasarkan nama, kategori, atau merk...");
        searchField.setPrefWidth(300);
        searchField.setStyle("-fx-background-radius: 5; -fx-border-radius: 5; -fx-border-color: #BDC3C7;");
        
        ComboBox<String> statusFilter = new ComboBox<>();
        statusFilter.getItems().addAll("Semua Status", "Tersedia", "Dipinjam", "Maintenance");
        statusFilter.setValue("Semua Status");
        statusFilter.setStyle("-fx-background-radius: 5;");
        
        ComboBox<String> categoryFilter = new ComboBox<>();
        categoryFilter.getItems().addAll("Semua Kategori", "Hardware", "Software", "Aksesoris", "Networking", "Furniture");
        categoryFilter.setValue("Semua Kategori");
        categoryFilter.setStyle("-fx-background-radius: 5;");
        
        // Search functionality
        searchField.textProperty().addListener((observable, oldValue, newValue) -> {
            updateFilter(newValue, statusFilter.getValue(), categoryFilter.getValue());
        });
        
        statusFilter.setOnAction(e -> {
            updateFilter(searchField.getText(), statusFilter.getValue(), categoryFilter.getValue());
        });
        
        categoryFilter.setOnAction(e -> {
            updateFilter(searchField.getText(), statusFilter.getValue(), categoryFilter.getValue());
        });
        
        searchBox.getChildren().addAll(searchLabel, searchField, statusFilter, categoryFilter);
        
        // Table
        inventoryTable = createInventoryTable();
        
        // Table container with styling
        VBox tableContainer = new VBox(10);
        tableContainer.setStyle("-fx-background-color: white; -fx-background-radius: 10; -fx-padding: 15;");
        tableContainer.setEffect(new DropShadow(5, Color.rgb(0, 0, 0, 0.1)));
        
        Label tableLabel = new Label("Daftar Inventaris");
        tableLabel.setFont(Font.font("Arial", FontWeight.BOLD, 16));
        tableLabel.setTextFill(Color.web("#2C3E50"));
        
        tableContainer.getChildren().addAll(tableLabel, inventoryTable);
        
        inventoryContent.getChildren().addAll(headerBox, searchBox, tableContainer);
        
        return inventoryContent;
    }
    
    private void updateFilter(String searchText, String statusFilter, String categoryFilter) {
        filteredData.setPredicate(item -> {
            // Search text filter
            if (searchText != null && !searchText.isEmpty()) {
                String lowerCaseFilter = searchText.toLowerCase();
                if (!item.getNama().toLowerCase().contains(lowerCaseFilter) &&
                    !item.getKategori().toLowerCase().contains(lowerCaseFilter) &&
                    !item.getMerk().toLowerCase().contains(lowerCaseFilter)) {
                    return false;
                }
            }
            
            // Status filter
            if (statusFilter != null && !statusFilter.equals("Semua Status")) {
                if (!item.getStatus().equals(statusFilter)) {
                    return false;
                }
            }
            
            // Category filter
            if (categoryFilter != null && !categoryFilter.equals("Semua Kategori")) {
                if (!item.getKategori().equals(categoryFilter)) {
                    return false;
                }
            }
            
            return true;
        });
    }
    
    private TableView<InventoryItem> createInventoryTable() {
        TableView<InventoryItem> table = new TableView<>();
        table.setItems(filteredData);
        table.setPrefHeight(400);
        
        // Columns
        TableColumn<InventoryItem, String> idCol = new TableColumn<>("ID");
        idCol.setCellValueFactory(new PropertyValueFactory<>("id"));
        idCol.setPrefWidth(80);
        
        TableColumn<InventoryItem, String> namaCol = new TableColumn<>("Nama");
        namaCol.setCellValueFactory(new PropertyValueFactory<>("nama"));
        namaCol.setPrefWidth(200);
        
        TableColumn<InventoryItem, String> kategoriCol = new TableColumn<>("Kategori");
        kategoriCol.setCellValueFactory(new PropertyValueFactory<>("kategori"));
        kategoriCol.setPrefWidth(100);
        
        TableColumn<InventoryItem, String> merkCol = new TableColumn<>("Merk");
        merkCol.setCellValueFactory(new PropertyValueFactory<>("merk"));
        merkCol.setPrefWidth(100);
        
        TableColumn<InventoryItem, String> kondisiCol = new TableColumn<>("Kondisi");
        kondisiCol.setCellValueFactory(new PropertyValueFactory<>("kondisi"));
        kondisiCol.setPrefWidth(100);
        
        TableColumn<InventoryItem, String> lokasiCol = new TableColumn<>("Lokasi");
        lokasiCol.setCellValueFactory(new PropertyValueFactory<>("lokasi"));
        lokasiCol.setPrefWidth(120);
        
        TableColumn<InventoryItem, String> statusCol = new TableColumn<>("Status");
        statusCol.setCellValueFactory(new PropertyValueFactory<>("status"));
        statusCol.setPrefWidth(100);
        
        TableColumn<InventoryItem, String> tanggalCol = new TableColumn<>("Tanggal Masuk");
        tanggalCol.setCellValueFactory(new PropertyValueFactory<>("tanggalMasuk"));
        tanggalCol.setPrefWidth(120);
        
        // Action column
        TableColumn<InventoryItem, Void> actionCol = new TableColumn<>("Aksi");
        actionCol.setPrefWidth(120);
        actionCol.setCellFactory(col -> {
            return new TableCell<InventoryItem, Void>() {
                private final Button editBtn = new Button("Edit");
                private final Button deleteBtn = new Button("Hapus");
                private final HBox actionBox = new HBox(5);
                
                {
                    editBtn.setStyle("-fx-background-color: #3498DB; -fx-text-fill: white; -fx-font-size: 10px; -fx-background-radius: 3;");
                    deleteBtn.setStyle("-fx-background-color: #E74C3C; -fx-text-fill: white; -fx-font-size: 10px; -fx-background-radius: 3;");
                    
                    editBtn.setOnAction(e -> {
                        InventoryItem item = getTableView().getItems().get(getIndex());
                        showEditItemDialog(item);
                    });
                    
                    deleteBtn.setOnAction(e -> {
                        InventoryItem item = getTableView().getItems().get(getIndex());
                        showDeleteConfirmation(item);
                    });
                    
                    actionBox.getChildren().addAll(editBtn, deleteBtn);
                }
                
                @Override
                protected void updateItem(Void item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty) {
                        setGraphic(null);
                    } else {
                        setGraphic(actionBox);
                    }
                }
            };
        });
        
        table.getColumns().addAll(idCol, namaCol, kategoriCol, merkCol, kondisiCol, lokasiCol, statusCol, tanggalCol, actionCol);
        
        return table;
    }
    
    private void showAddItemDialog() {
        InventoryForm form = new InventoryForm(primaryStage, null);
        InventoryItem newItem = form.showAndWait();
        
        if (newItem != null) {
            // Generate new ID if empty
            if (newItem.getId().isEmpty()) {
                newItem.setId(CSVManager.generateNewId(inventoryData));
            }
            
            // Check if ID already exists
            boolean idExists = inventoryData.stream().anyMatch(item -> item.getId().equals(newItem.getId()));
            if (idExists) {
                showAlert("Error", "ID sudah digunakan! Silakan gunakan ID yang berbeda.", Alert.AlertType.ERROR);
                return;
            }
            
            inventoryData.add(newItem);
            CSVManager.saveInventoryData(inventoryData);
            showAlert("Sukses", "Data inventaris berhasil ditambahkan!", Alert.AlertType.INFORMATION);
            
            // Refresh dashboard if currently showing
            if (currentMainContent == createMainContent()) {
                switchToContent(createMainContent(), null);
            }
        }
    }
    
    private void showEditItemDialog(InventoryItem item) {
        InventoryForm form = new InventoryForm(primaryStage, item);
        InventoryItem updatedItem = form.showAndWait();
        
        if (updatedItem != null) {
            // Update item properties
            item.setNama(updatedItem.getNama());
            item.setKategori(updatedItem.getKategori());
            item.setMerk(updatedItem.getMerk());
            item.setKondisi(updatedItem.getKondisi());
            item.setLokasi(updatedItem.getLokasi());
            item.setStatus(updatedItem.getStatus());
            item.setTanggalMasuk(updatedItem.getTanggalMasuk());
            
            CSVManager.saveInventoryData(inventoryData);
            inventoryTable.refresh();
            showAlert("Sukses", "Data inventaris berhasil diperbarui!", Alert.AlertType.INFORMATION);
        }
    }
    
    private void showDeleteConfirmation(InventoryItem item) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Konfirmasi Hapus");
        alert.setHeaderText("Hapus Data Inventaris");
        alert.setContentText("Apakah Anda yakin ingin menghapus item: " + item.getNama() + "?");
        
        Optional<ButtonType> result = alert.showAndWait();
        if (result.get() == ButtonType.OK) {
            inventoryData.remove(item);
            CSVManager.saveInventoryData(inventoryData);
            showAlert("Sukses", "Data inventaris berhasil dihapus!", Alert.AlertType.INFORMATION);
        }
    }
    
    private void refreshInventoryData() {
        inventoryData.clear();
        inventoryData.addAll(CSVManager.loadInventoryData());
        showAlert("Sukses", "Data inventaris berhasil direfresh!", Alert.AlertType.INFORMATION);
    }
    
    private VBox createStatCard(String title, String value, String color) {
        VBox card = new VBox(10);
        card.setAlignment(Pos.CENTER);
        card.setPadding(new Insets(20));
        card.setPrefWidth(150);
        card.setStyle("-fx-background-color: white; -fx-background-radius: 10;");
        card.setEffect(new DropShadow(5, Color.rgb(0, 0, 0, 0.1)));
        
        Label valueLabel = new Label(value);
        valueLabel.setFont(Font.font("Arial", FontWeight.BOLD, 28));
        valueLabel.setTextFill(Color.web(color));
        
        Label titleLabel = new Label(title);
        titleLabel.setFont(Font.font("Arial", FontWeight.NORMAL, 12));
        titleLabel.setTextFill(Color.web("#7F8C8D"));
        titleLabel.setWrapText(true);
        titleLabel.setAlignment(Pos.CENTER);
        
        card.getChildren().addAll(valueLabel, titleLabel);
        return card;
    }
    
    private Button createActionButton(String text, String color) {
        Button button = new Button(text);
        button.setPrefWidth(150);
        button.setPrefHeight(50);
        button.setStyle("-fx-background-color: " + color + "; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 8;");
        button.setOnMouseEntered(e -> button.setStyle("-fx-background-color: derive(" + color + ", -10%); -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 8;"));
        button.setOnMouseExited(e -> button.setStyle("-fx-background-color: " + color + "; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 8;"));
        
        return button;
    }
    
    private void showAlert(String title, String message, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
    
    public static void main(String[] args) {
        launch(args);
    }
}