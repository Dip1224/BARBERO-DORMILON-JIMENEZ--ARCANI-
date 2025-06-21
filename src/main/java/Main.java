import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class Main extends Application {

    public static void main(String[] args) {
        launch(args); // Invocamos launch() para iniciar la aplicación JavaFX
    }

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

        // Lógica del barbero y los clientes
        SalaDeEspera salaDeEspera = new SalaDeEspera(4); // 4 sillas disponibles
        Barbero barbero = new Barbero();
        barbero.start(); // El barbero comienza a trabajar

        // Crear clientes
        Cliente cliente1 = new Cliente("Cliente 1", false);
        Cliente cliente2 = new Cliente("Cliente 2", true); // VIP
        Cliente cliente3 = new Cliente("Cliente 3", false);
        Cliente cliente4 = new Cliente("Cliente 4", true); // VIP
        Cliente cliente5 = new Cliente("Cliente 5", false);

        // Agregar clientes a la sala de espera (simulando su llegada a la barbería)
        salaDeEspera.agregarCliente(cliente1);
        salaDeEspera.agregarCliente(cliente2);
        salaDeEspera.agregarCliente(cliente3);
        salaDeEspera.agregarCliente(cliente4);
        salaDeEspera.agregarCliente(cliente5);

        // Atender a los clientes de manera controlada
        int clientesAtendidos = 0;
        while (clientesAtendidos < 5) {
            Cliente cliente = salaDeEspera.obtenerCliente(); // El barbero obtiene el siguiente cliente
            if (cliente != null) {
                barbero.atenderCliente(cliente); // El barbero atiende al cliente
                clientesAtendidos++; // Contamos el cliente atendido
            }
        }
        System.out.println("Todos los clientes han sido atendidos.");
    }
}
