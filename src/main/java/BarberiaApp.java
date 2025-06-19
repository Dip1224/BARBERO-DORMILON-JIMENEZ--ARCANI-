import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class BarberiaApp extends Application {

    @Override
    public void start(Stage primaryStage) {
        // Crear la interfaz
        VBox root = new VBox();
        Label mensaje = new Label("La barbería está abierta.");

        root.getChildren().add(mensaje);

        Scene scene = new Scene(root, 400, 300);
        primaryStage.setTitle("Simulador de Barbería");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
