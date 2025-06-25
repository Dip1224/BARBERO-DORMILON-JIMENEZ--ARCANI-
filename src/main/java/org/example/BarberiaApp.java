package org.example;

import java.util.ArrayList;
import java.util.List;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Duration;

public class BarberiaApp extends Application {

    // Variables de instancia para acceso entre métodos
    private boolean cajaOcupada = false;
    private ImageView clientesEnCaja; // Cliente en la caja (inicialmente invisible)
   
    private double totalAcumulado = 0; // Dinero total ganado
 // Variables para el contador de clientes, dinero acumulado y dinero de los barberos
private int clienteContador = 1; // Comienza con 1
private double[] dineroBarberos = {0, 0, 0}; // Dinero ganado por cada barbero




  
    //private int[] dineroBarberos = new int[3]; // Cantidad de dinero que cada barbero ha ganado

    private Label labelCobrandoCliente; // Label para mostrar el cliente que está pagando
    private Label labelElBarbero; // Label para mostrar el número del barbero que está atendiendo
    private Label labelTotalAcumulado; // Label para mostrar el total acumulado
    
    // Variables de instancia movidas desde start()
    private final List<ProgressBar> barrasProgreso = new ArrayList<>();
    private final List<Timeline> timelines = new ArrayList<>();
    // private final List<StackPane> sillasGraficas = new ArrayList<>(); // Removed: unused collection
    private final List<ImageView> clientesEnSilla = new ArrayList<>();
    private final List<ImageView> clientes = new ArrayList<>();
    private List<ImageView> sillasOcupadas = new ArrayList<>();
    private List<ImageView> colaPago = new ArrayList<>();
    private ProgressBar barraCaja;
    private Runnable actualizarSofaYEspera;

    // HBox para clientes sentados en el sofá
    private HBox sofaClientes;
    // VBox para la sala de espera (de pie, a la izquierda)
    private VBox salaEspera;

    
    

    @Override
    public void start(Stage primaryStage) {
// Método de pago y cobro


        VBox root = new VBox(20);
        root.setStyle("-fx-background-color: #b0b0b0;"); // Fondo plomo

        // Imagen de barbero grande
        Image barberoImg = new Image(getClass().getResourceAsStream("/img/barbero.png"));
        ImageView barberoView = new ImageView(barberoImg);
        barberoView.setFitWidth(100);
        barberoView.setFitHeight(100);

        // Título principal
        Label titulo = new Label("LA MAFIA");
        titulo.setStyle("-fx-font-size: 32px; -fx-font-weight: bold; -fx-text-fill: #222;");

        // HBox solo para la imagen a la izquierda
        HBox barraImagen = new HBox(barberoView);
        barraImagen.setAlignment(Pos.TOP_LEFT);

        // HBox para las sillas (centradas arriba)
        // Listas para las barras, timelines y clientes en silla
        // (Ya declaradas como variables de instancia)

        HBox sillonesBarbero = new HBox(60);
        sillonesBarbero.setAlignment(Pos.CENTER);
        for (int i = 0; i < 3; i++) {
    // Crear la imagen de la silla
    Image sillaImg = new Image(getClass().getResourceAsStream("/img/sillon.png"));
    ImageView sillaView = new ImageView(sillaImg);
    sillaView.setFitWidth(140);
    sillaView.setFitHeight(140);

    // Crear la imagen del cliente (inicialmente invisible)
    ImageView clienteEnSilla = new ImageView();
    clienteEnSilla.setFitWidth(60);
    clienteEnSilla.setFitHeight(90);
    clienteEnSilla.setVisible(false);  // Esto controla si el cliente está en la silla
    clientesEnSilla.add(clienteEnSilla);

    // Crear el StackPane que contendrá la silla y el cliente
    StackPane stackSilla = new StackPane();
    stackSilla.getChildren().addAll(sillaView, clienteEnSilla);
    StackPane.setAlignment(clienteEnSilla, Pos.CENTER);  // Alinea el cliente en el centro de la silla

    // Crear la barra de progreso
    ProgressBar barra = new ProgressBar(0);
    barra.setPrefWidth(120);
    barra.setPrefHeight(15);
    barra.setStyle("-fx-accent: #2e8b57; -fx-control-inner-background: #cccccc;");
    barrasProgreso.add(barra);

    // Crear el texto (Label) debajo de cada barra
    Label labelBarbero = new Label("Barbero " + (i + 1)); // Este texto estará debajo de la barra
    labelBarbero.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: #333;");

    // Crear un VBox para la barra y el texto debajo
    VBox barraYTexto = new VBox(5, barra, labelBarbero);
    barraYTexto.setAlignment(Pos.CENTER);  // Centra los elementos dentro del VBox

    // Crear un contenedor (VBox) que agrupe la silla y la barra + texto
    VBox sillaConBarra = new VBox(5, stackSilla, barraYTexto);
    sillaConBarra.setAlignment(Pos.CENTER);  // Centra todos los elementos (silla, barra, texto)

    // Agregar al contenedor principal
    sillonesBarbero.getChildren().add(sillaConBarra);

    // Timeline para la barra de progreso de cada silla
    ProgressBar barraTimeline = barrasProgreso.get(i);
    Timeline timeline = new Timeline(
        new KeyFrame(Duration.ZERO, e -> barraTimeline.setProgress(0)),
        new KeyFrame(Duration.seconds(5), e -> barraTimeline.setProgress(1))
    );
    timeline.currentTimeProperty().addListener((obs, oldTime, newTime) -> {
        double progress = newTime.toSeconds() / 5.0;
        barraTimeline.setProgress(progress);
    });
    timeline.setCycleCount(1);
    timelines.add(timeline);
}


        // Imagen del sofá (siempre visible)
        Image sofaImg = new Image(getClass().getResourceAsStream("/img/sofa.png"));
        ImageView sofaView = new ImageView(sofaImg);
        sofaView.setFitWidth(320);
        sofaView.setFitHeight(100);

        // HBox para clientes sentados en el sofá
        sofaClientes = new HBox(10);
        sofaClientes.setAlignment(Pos.CENTER);

        // StackPane para superponer el sofá y los clientes sentados
        StackPane sofaStack = new StackPane();
        sofaStack.setPrefSize(320, 100);
        sofaStack.getChildren().addAll(sofaView, sofaClientes);

        // Botón para crear cliente
        Button btnCrearCliente = new Button("Crear Cliente");

        // VBox para sofá (siempre visible) y botón juntos, centrados
        VBox sofaYBoton = new VBox(10, sofaStack, btnCrearCliente);
        sofaYBoton.setAlignment(Pos.CENTER);
        HBox.setHgrow(sofaYBoton, Priority.ALWAYS); // Esto permite que el sofá se expanda y quede centrado

        // VBox para la sala de espera (de pie, a la izquierda)
        salaEspera = new VBox(10);
        salaEspera.setAlignment(Pos.BOTTOM_CENTER);
        salaEspera.setPrefWidth(80);
        salaEspera.setMaxWidth(80);
        salaEspera.setMinWidth(80);
        salaEspera.setPrefHeight(400); // <-- AJUSTA AQUÍ
        salaEspera.setMaxHeight(400);
        salaEspera.setMinHeight(400);
        salaEspera.setStyle("-fx-background-color: #e0e0e0; -fx-border-color: #888; -fx-border-width: 2px; -fx-border-radius: 10px; -fx-background-radius: 10px;");

        // Caja grande
        Image cajaImg = new Image(getClass().getResourceAsStream("/img/caja.png"));
        ImageView cajaView = new ImageView(cajaImg);
        cajaView.setFitWidth(180);
        cajaView.setFitHeight(180);

        // Imagen del cliente en la caja (inicialmente invisible)
        clientesEnCaja = new ImageView();
        clientesEnCaja.setFitWidth(60);
        clientesEnCaja.setFitHeight(90);
        clientesEnCaja.setVisible(false);

        // Barrita de progreso en la caja
        barraCaja = new ProgressBar(0);
        barraCaja.setPrefWidth(120);
        barraCaja.setPrefHeight(15);
        barraCaja.setVisible(true);

        // Crear un Label para el texto "CAJA REGISTRADORA"
Label labelCaja = new Label("CAJA REGISTRADORA");
labelCaja.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: #333;");

 // Crear un Label para "Cobrando a Cliente"
        labelCobrandoCliente = new Label("Cobrando a Cliente: " + clienteContador);
        labelCobrandoCliente.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: #333;");

        // Crear un Label para "El Barbero"
        labelElBarbero = new Label("El Barbero: " + 1); // Esto se actualizará dinámicamente
        labelElBarbero.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: #333;");

        // Crear un Label para el total acumulado
        labelTotalAcumulado = new Label("Total Acumulado: $" + totalAcumulado);
        labelTotalAcumulado.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: #333;");



        // Cuando el cliente paga
labelCobrandoCliente.setText("Cobrando a Cliente: " + clienteContador);
labelElBarbero.setText("El Barbero: " + (1));  // Aquí puedes asociar el número del barbero o la silla
totalAcumulado += 50;  // Actualiza el total acumulado de dinero (suponiendo que el precio por corte es 50)
labelTotalAcumulado.setText("Total Acumulado: $" + totalAcumulado);
    



// Mostrar el dinero ganado por cada barbero
Label barbero1 = new Label("Barbero 1: $" + dineroBarberos[0]);
Label barbero2 = new Label("Barbero 2: $" + dineroBarberos[1]);
Label barbero3 = new Label("Barbero 3: $" + dineroBarberos[2]);

VBox barberosInfo = new VBox(5, barbero1, barbero2, barbero3);
barberosInfo.setAlignment(Pos.CENTER);


        // StackPane solo para la caja y el cliente
        StackPane cajaStack = new StackPane();
        cajaStack.getChildren().addAll(cajaView, clientesEnCaja);
        StackPane.setAlignment(cajaView, Pos.CENTER);
        StackPane.setAlignment(clientesEnCaja, Pos.CENTER);

        // VBox para poner la barra ARRIBA de la caja
        VBox cajaVBox = new VBox(8, labelCaja, barraCaja, cajaStack,labelCobrandoCliente, labelElBarbero, labelTotalAcumulado); // Primero el texto, luego la barra, luego la caja
        cajaVBox.setAlignment(Pos.CENTER);

        // HBox para la fila inferior (más espacio)
        HBox filaInferior = new HBox(60);
        filaInferior.setAlignment(Pos.BOTTOM_LEFT);

        // Espaciador horizontal para empujar la caja a la derecha
        Region espaciadorHorizontal = new Region();
        HBox.setHgrow(espaciadorHorizontal, Priority.ALWAYS);

        filaInferior.getChildren().addAll(
            salaEspera, 
            sofaYBoton, 
            espaciadorHorizontal, // <-- agrega el espaciador aquí
            cajaVBox
        );

        HBox.setMargin(salaEspera, new Insets(0, 0, 40, 40));
        HBox.setMargin(cajaVBox, new Insets(0, 40, 40, 0));

        // Espaciador vertical para empujar la fila inferior hacia abajo
        Region espaciadorVertical = new Region();
        VBox.setVgrow(espaciadorVertical, Priority.ALWAYS);

        // VBox principal
        root.setStyle("-fx-background-color: #b0b0b0;");
        root.setPadding(new Insets(30, 0, 0, 0));
        // Agregar barraImagen y título en una HBox para la barra superior
        HBox barraSuperior = new HBox(20, barraImagen, titulo);
        barraSuperior.setAlignment(Pos.CENTER_LEFT);
        barraSuperior.setPadding(new Insets(0, 0, 0, 40));

        // AGREGA LOS COMPONENTES AL ROOT
        root.getChildren().addAll(
            barraSuperior,      // Barra superior con imagen y título
            sillonesBarbero,    // Sillas de barbero con barras de progreso
            espaciadorVertical, // Espaciador para empujar la fila inferior hacia abajo
            filaInferior        // Fila inferior: sala de espera, sofá, caja
        );

        // Lista para los clientes creados
        // (clientes ya es variable de instancia)
        int[] clienteActual = {1}; // Para alternar entre persona1.png y persona6.png

        // Control de ocupación de sillas (null si está libre, si no, el ImageView del cliente)
        // (sillasOcupadas ya es variable de instancia)
        sillasOcupadas.clear();
        for (int i = 0; i < 3; i++) sillasOcupadas.add(null);
        // boolean cajaOcupada = false; // Ahora es variable de instancia
        // Cola de pago
        // (colaPago ya es variable de instancia)
        colaPago.clear(); // Guarda el índice de la silla que está pagando
        // Función para actualizar sofá y sala de espera visualmente
        actualizarSofaYEspera = () -> {
            sofaClientes.getChildren().clear();
            salaEspera.getChildren().clear();
            List<ImageView> sofaList = new ArrayList<>();
            List<ImageView> esperaList = new ArrayList<>();
            for (ImageView cliente : clientes) {
                // NO mostrar el cliente que está en la caja
                if (sillasOcupadas.contains(cliente)) continue; // Ya está en silla
                if (clientesEnCaja.isVisible() && cliente.getImage() == clientesEnCaja.getImage()) continue; // Está en caja
                if (sofaList.size() < 4) sofaList.add(cliente);
                else if (esperaList.size() < 4) esperaList.add(cliente);
            }
            sofaClientes.getChildren().addAll(sofaList);
            salaEspera.getChildren().addAll(esperaList);
        };

        // Acción del botón
        btnCrearCliente.setOnAction(_ -> {
            if (clienteActual[0] > 6) clienteActual[0] = 1;
            String imgPath = "/img/persona" + clienteActual[0] + ".png";
            ImageView nuevoCliente = new ImageView(new Image(getClass().getResourceAsStream(imgPath)));
            nuevoCliente.setFitWidth(60);
            nuevoCliente.setFitHeight(90);
            clientes.add(nuevoCliente);
            clienteActual[0]++;

            // Intentar sentar al cliente en una silla libre
            boolean sentado = false;
            for (int i = 0; i < 3; i++) {
                if (sillasOcupadas.get(i) == null) {
                    sillasOcupadas.set(i, nuevoCliente);
                    barrasProgreso.get(i).setProgress(0);
                    timelines.get(i).playFromStart();
                    // Mostrar cliente en la silla correspondiente
                    clientesEnSilla.get(i).setImage(nuevoCliente.getImage());
                    clientesEnSilla.get(i).setVisible(true);
                    sentado = true;
                    break;
                }
            }

            // Actualiza sofá y sala de espera
            actualizarSofaYEspera.run();
        });

        // Configura el evento de fin de cada Timeline (cuando termina el corte)
        for (int i = 0; i < 3; i++) {
            final int idx = i;
            timelines.get(i).setOnFinished(e -> {
                ImageView clienteQuePagara = sillasOcupadas.get(idx);

                // Mostrar "pagando.png"
                Image pagandoImg = new Image(getClass().getResourceAsStream("/img/pagando.png"));
                clientesEnSilla.get(idx).setImage(pagandoImg);
                clientesEnSilla.get(idx).setVisible(true);
                barrasProgreso.get(idx).setProgress(0);

                // Agrega a la cola de pago
                colaPago.add(clienteQuePagara);
                iniciarPagoEnCaja();
                // NO liberes la silla ni actualices sofá aquí
            });
        }

        // Al inicio, no inicies ninguna barra
        // timelines.get(0).playFromStart(); // ¡Elimina o comenta esta línea!

        Scene scene = new Scene(root, 1100, 850);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }

    void iniciarPagoEnCaja() {
        if (!colaPago.isEmpty() && !cajaOcupada) {
            cajaOcupada = true;
            ImageView clienteQueSale = colaPago.remove(0);

            // Busca en qué silla estaba este cliente
            int idxSillaTmp = -1;
            for (int i = 0; i < sillasOcupadas.size(); i++) {
                if (sillasOcupadas.get(i) == clienteQueSale) {
                    idxSillaTmp = i;
                    break;
                }
            }
            final int idxSilla = idxSillaTmp; // <-- así es final

            // Muestra el cliente en la caja
            clientesEnCaja.setImage(clienteQueSale.getImage());
            clientesEnCaja.setVisible(true);

            // Barrita de pago
            barraCaja.setProgress(0);
            Timeline pagoTimeline = new Timeline(
                new KeyFrame(Duration.ZERO, e -> barraCaja.setProgress(0)),
                new KeyFrame(Duration.seconds(3), e -> barraCaja.setProgress(1))
            );
            pagoTimeline.currentTimeProperty().addListener((obs, _, newTime) -> {
                double progress = newTime.toSeconds() / 3.0;
                barraCaja.setProgress(progress);
            });
            pagoTimeline.setCycleCount(1);

            pagoTimeline.setOnFinished(e -> {
                clientesEnCaja.setVisible(false);
                barraCaja.setProgress(0); // <-- REINICIA LA BARRA DE LA CAJA

                // Elimina al cliente de la lista para que no vuelva al sofá
                clientes.remove(clienteQueSale);
                sofaClientes.getChildren().remove(clienteQueSale);
                salaEspera.getChildren().remove(clienteQueSale);

                // Libera la silla
                if (idxSilla != -1) {
                    sillasOcupadas.set(idxSilla, null);
                    clientesEnSilla.get(idxSilla).setImage(null);
                    clientesEnSilla.get(idxSilla).setVisible(false);
                }

                // Intenta llenar todas las sillas libres con clientes esperando
                for (int i = 0; i < sillasOcupadas.size(); i++) {
                    if (sillasOcupadas.get(i) == null) {
                        ImageView siguiente = null;
                        for (ImageView cliente : clientes) {
                            if (!sillasOcupadas.contains(cliente) &&
                                !(clientesEnCaja.isVisible() && cliente.getImage() == clientesEnCaja.getImage())) {
                                siguiente = cliente;
                                break;
                            }
                        }
                        if (siguiente != null) {
                            sillasOcupadas.set(i, siguiente);
                            clientesEnSilla.get(i).setImage(siguiente.getImage());
                            clientesEnSilla.get(i).setVisible(true);
                            barrasProgreso.get(i).setProgress(0);
                            timelines.get(i).playFromStart();
                            actualizarSofaYEspera.run();
                        } else {
                            // No hay clientes esperando, muestra "durmiendo.png"
                            Image durmiendoImg = new Image(getClass().getResourceAsStream("/img/durmiendo.png"));
                            clientesEnSilla.get(i).setImage(durmiendoImg);
                            clientesEnSilla.get(i).setVisible(true);
                        }
                    }
                }
                cajaOcupada = false;

                actualizarSofaYEspera.run();
                iniciarPagoEnCaja();
            });
            pagoTimeline.playFromStart();
        }
    }
    Label barbero1 = new Label("Barbero 1: $0");
Label barbero2 = new Label("Barbero 2: $0");
Label barbero3 = new Label("Barbero 3: $0");


    // Método para actualizar la información de los barberos (dinero ganado)
private void actualizarBarberosInfo() {
    // Actualizamos el texto con el dinero ganado por cada barbero
    barbero1.setText("Barbero 1: $" + dineroBarberos[0]);
    barbero2.setText("Barbero 2: $" + dineroBarberos[1]);
    barbero3.setText("Barbero 3: $" + dineroBarberos[2]);
}

}