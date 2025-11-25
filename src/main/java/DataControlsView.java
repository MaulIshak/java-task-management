import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.ListView;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.VBox;

public class DataControlsView extends DocumentationView {

    public DataControlsView() {
        super("Data Controls", "Displaying data in Lists and Tables.");

        // ListView Demo
        VBox listBox = new VBox();
        ListView<String> listView = new ListView<>();
        ObservableList<String> items = FXCollections.observableArrayList(
                "Item 1", "Item 2", "Item 3", "Item 4");
        listView.setItems(items);
        listView.setPrefHeight(150);
        listBox.getChildren().add(listView);

        addSection("ListView", listBox);
        addCodeSnippet(
                "ListView<String> list = new ListView<>();\nlist.setItems(FXCollections.observableArrayList(\"A\", \"B\"));");

        // TableView Demo
        VBox tableBox = new VBox();
        TableView<Person> tableView = new TableView<>();

        TableColumn<Person, String> nameCol = new TableColumn<>("Name");
        nameCol.setCellValueFactory(new PropertyValueFactory<>("name"));

        TableColumn<Person, String> emailCol = new TableColumn<>("Email");
        emailCol.setCellValueFactory(new PropertyValueFactory<>("email"));

        tableView.getColumns().add(nameCol);
        tableView.getColumns().add(emailCol);

        ObservableList<Person> people = FXCollections.observableArrayList(
                new Person("John Doe", "john@example.com"),
                new Person("Jane Smith", "jane@example.com"));
        tableView.setItems(people);
        tableView.setPrefHeight(150);
        tableView.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        tableBox.getChildren().add(tableView);

        addSection("TableView", tableBox);
        addCodeSnippet(
                "TableView<Person> table = new TableView<>();\nTableColumn<Person, String> col = new TableColumn<>(\"Name\");\ncol.setCellValueFactory(new PropertyValueFactory<>(\"name\"));");
    }

    // Simple Data Model
    public static class Person {
        private String name;
        private String email;

        public Person(String name, String email) {
            this.name = name;
            this.email = email;
        }

        public String getName() {
            return name;
        }

        public String getEmail() {
            return email;
        }
    }
}
