package org.example;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.example.model.BarberoInfo;

import javafx.animation.Interpolator;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.animation.TranslateTransition;
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.Tooltip;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
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

    // --- Constantes ---
    private static final double OBJETIVO_GANANCIA = 500;

    // --- Variables de instancia ---
    private boolean cajaOcupada = false;
    private ImageView clientesEnCaja;
    private double totalAcumulado = 0;
    private int clienteContador = 1;
    private final Map<ImageView, Integer> clienteNumeros = new HashMap<>();
    private final double[] dineroBarberos = {0, 0, 0};
    private Label labelCobrandoCliente;
    private Label labelElBarbero;
    private Label labelTotalAcumulado;
    private final List<ProgressBar> barrasProgreso = new ArrayList<>();
    private final List<Timeline> timelines = new ArrayList<>();
    private final List<ImageView> clientesEnSilla = new ArrayList<>();
    private final List<ImageView> clientes = new ArrayList<>();
    private final List<ImageView> sillasOcupadas = new ArrayList<>();
    private final List<ImageView> colaPago = new ArrayList<>();
    private ProgressBar barraCaja;
    private Runnable actualizarSofaYEspera;
    private final boolean[] logroMostrado = {false, false, false};
    private HBox sofaClientes;
    private VBox salaEspera;
    private final ObservableList<BarberoInfo> barberosData = FXCollections.observableArrayList(
        new BarberoInfo("Barbero 1", 0),
        new BarberoInfo("Barbero 2", 0),
        new BarberoInfo("Barbero 3", 0)
    );
    private TableView<BarberoInfo> tablaBarberos;
    private StackPane rootPane;
    private int idDiaActual;
    private int numeroDiaActual;
    private Label labelDiaActual;

    // --- Enums y mapas auxiliares ---
    private enum EventoCliente { NINGUNO, VIP, PROBLEMATICO }
    private final Map<ImageView, EventoCliente> eventosCliente = new HashMap<>();

    // --- Métodos de utilidad ---
    private void mostrarNotificacion(String mensaje, String color) {
        Label notificacion = new Label(mensaje);
        notificacion.setStyle(
            "-fx-background-color: " + color + ";"
            + "-fx-text-fill: white;"
            + "-fx-padding: 16px 32px;"
            + "-fx-background-radius: 20px;"
            + "-fx-font-size: 18px;"
            + "-fx-font-weight: bold;"
            + "-fx-effect: dropshadow(gaussian, #333, 8, 0.3, 0, 2);"
        );
        StackPane.setAlignment(notificacion, Pos.TOP_RIGHT);
        StackPane.setMargin(notificacion, new Insets(40, 40, 0, 0));
        rootPane.getChildren().add(notificacion);

        notificacion.setOpacity(0);
        Timeline fadeIn = new Timeline(
            new KeyFrame(Duration.seconds(0), new KeyValue(notificacion.opacityProperty(), 0)),
            new KeyFrame(Duration.seconds(0.3), new KeyValue(notificacion.opacityProperty(), 1))
        );
        Timeline fadeOut = new Timeline(
            new KeyFrame(Duration.seconds(0), new KeyValue(notificacion.opacityProperty(), 1)),
            new KeyFrame(Duration.seconds(0.3), new KeyValue(notificacion.opacityProperty(), 0))
        );
        fadeIn.setOnFinished(_ -> {
            new Timeline(new KeyFrame(Duration.seconds(2), _ -> fadeOut.play())).play();
        });
        fadeOut.setOnFinished(_ -> rootPane.getChildren().remove(notificacion));
        fadeIn.play();
    }

    // --- Métodos de lógica ---
    private void actualizarBarberosInfo() {
        double max = Math.max(dineroBarberos[0], Math.max(dineroBarberos[1], dineroBarberos[2]));
        for (int i = 0; i < 3; i++) {
            barberosData.get(i).setDinero(dineroBarberos[i]);
            if (!logroMostrado[i] && dineroBarberos[i] >= OBJETIVO_GANANCIA) {
                logroMostrado[i] = true;
                mostrarNotificacion(barberosData.get(i).getNombre() + " ha superado el objetivo de $" + (int)OBJETIVO_GANANCIA + "!", "#2980b9");
            }
        }
        tablaBarberos.setRowFactory(_ -> new TableRow<BarberoInfo>() {
            @Override
            protected void updateItem(BarberoInfo item, boolean empty) {
                super.updateItem(item, empty);
                if (item == null || empty) {
                    this.setStyle("");
                } else if (item.getDinero() == max && max > 0) {
                    this.setStyle("-fx-background-color: gold; -fx-font-weight: bold;");
                } else {
                    this.setStyle("");
                }
            }
        });
        tablaBarberos.refresh();
    }

    void iniciarPagoEnCaja() {
        if (!colaPago.isEmpty() && !cajaOcupada) {
            cajaOcupada = true;
            ImageView clienteQueSale = colaPago.remove(0);

            int idxSillaTmp = -1;
            for (int i = 0; i < sillasOcupadas.size(); i++) {
                if (sillasOcupadas.get(i) == clienteQueSale) {
                    idxSillaTmp = i;
                    break;
                }
            }
            final int idxSilla = idxSillaTmp;

            clientesEnCaja.setImage(clienteQueSale.getImage());
            clientesEnCaja.setVisible(true);
            clientesEnCaja.setTranslateX(-100);
            TranslateTransition animCaja = new TranslateTransition(Duration.seconds(0.5), clientesEnCaja);
            animCaja.setToX(0);
            animCaja.play();

            barraCaja.setProgress(0);
            Timeline pagoTimeline = new Timeline(
                new KeyFrame(Duration.ZERO, _ -> barraCaja.setProgress(0)),
                new KeyFrame(Duration.seconds(3), _ -> barraCaja.setProgress(1))
            );
            pagoTimeline.currentTimeProperty().addListener((_, _, newTime) -> {
                double progress = newTime.toSeconds() / 3.0;
                barraCaja.setProgress(progress);
            });
            pagoTimeline.setCycleCount(1);

            int numeroCliente = clienteNumeros.getOrDefault(clienteQueSale, 0);
            labelCobrandoCliente.setText("Cobrando a Cliente: " + numeroCliente);
            labelElBarbero.setText("El Barbero: " + (idxSilla + 1));

            pagoTimeline.setOnFinished(_ -> {
                TranslateTransition salida = new TranslateTransition(Duration.seconds(0.5), clientesEnCaja);
                salida.setToX(150);
                salida.setOnFinished(_ -> {
                    clientesEnCaja.setVisible(false);
                    clientesEnCaja.setTranslateX(0);
                    barraCaja.setProgress(0);

                    clientes.remove(clienteQueSale);
                    sofaClientes.getChildren().remove(clienteQueSale);
                    salaEspera.getChildren().remove(clienteQueSale);

                    if (idxSilla != -1) {
                        sillasOcupadas.set(idxSilla, null);
                        clientesEnSilla.get(idxSilla).setImage(null);
                        clientesEnSilla.get(idxSilla).setVisible(false);
                    }

                    Random random = new Random();
                    EventoCliente evento = eventosCliente.getOrDefault(clienteQueSale, EventoCliente.NINGUNO);
                    int monto = 30 + random.nextInt(51);

                    switch (evento) {
                        case VIP -> {
                            monto *= 2;
                            mostrarNotificacion("¡Cliente VIP! El barbero recibe el doble: $" + monto, "#e67e22");
                        }
                        case PROBLEMATICO -> {
                            try { Thread.sleep(2000); } catch (InterruptedException ex) {}
                            mostrarNotificacion("¡Cliente problemático! El pago se retrasó. Monto: $" + monto, "#c0392b");
                        }
                        default -> {
                            mostrarNotificacion("Pago exitoso. Monto: $" + monto, "#27ae60");
                        }
                    }

                    // Después de calcular el monto y actualizar el dinero del barbero:
                    dineroBarberos[idxSilla] += monto;
                    totalAcumulado += monto;
                    labelTotalAcumulado.setText("Total Acumulado: $" + totalAcumulado);
                    actualizarBarberosInfo();

                    // GUARDAR EN BASE DE DATOS:
                    // Guarda el barbero (nombre y dinero ganado)
                    DBService.guardarBarbero(barberosData.get(idxSilla).getNombre(), dineroBarberos[idxSilla]);
                    // Guarda la transacción (nombre del cliente, id del barbero, monto)
                    // Si no tienes el id del barbero, puedes usar (idxSilla+1) si los ids son 1,2,3
                    DBService.guardarTransaccion(
                        // nombre del cliente
                        clienteQueSale.getProperties().getOrDefault("nombre", "Cliente").toString(),
                        idxSilla + 1,
                        monto,
                        idDiaActual // <-- Este debe ser el id del día actual, NO -1
                    );

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
                                clientesEnSilla.get(i).setFitWidth(60);
                                clientesEnSilla.get(i).setFitHeight(90);
                                clientesEnSilla.get(i).setVisible(true);
                                barrasProgreso.get(i).setProgress(0);
                                timelines.get(i).playFromStart();

                                clientesEnSilla.get(i).setTranslateY(-50);
                                TranslateTransition transition = new TranslateTransition(Duration.seconds(0.5), clientesEnSilla.get(i));
                                transition.setToY(0);
                                transition.play();

                                actualizarSofaYEspera.run();
                            } else {
                                Image durmiendoImg = new Image(getClass().getResourceAsStream("/img/durmiendo.png"));
                                clientesEnSilla.get(i).setImage(durmiendoImg);
                                clientesEnSilla.get(i).setFitWidth(140);
                                clientesEnSilla.get(i).setFitHeight(140);
                                clientesEnSilla.get(i).setVisible(true);
                            }
                        }
                    }
                    cajaOcupada = false;
                    iniciarPagoEnCaja();
                });
                salida.play();
            });
            pagoTimeline.playFromStart();
        }
        actualizarSofaYEspera.run();
    }

    // --- Método principal de la interfaz ---
    @Override
    public void start(Stage primaryStage) {
        rootPane = new StackPane();
        VBox root = new VBox(20);
        root.setStyle("-fx-background-color: #b0b0b0;");
        rootPane.getChildren().add(root);

        // Imagen de barbero grande
        Image barberoImg = new Image(getClass().getResourceAsStream("/img/barbero.png"));
        ImageView barberoView = new ImageView(barberoImg);
        barberoView.setFitWidth(100);
        barberoView.setFitHeight(100);

        // Título principal
        Label titulo = new Label("LA MAFIA");
        titulo.setStyle("-fx-font-size: 44px; -fx-font-weight: bold; -fx-text-fill: #222;");

        // HBox solo para la imagen a la izquierda
        HBox barraImagen = new HBox(barberoView);
        barraImagen.setAlignment(Pos.TOP_LEFT);


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
            Label labelBarbero = new Label();
            labelBarbero.textProperty().bind(barberosData.get(i).nombreProperty());
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
                new KeyFrame(Duration.ZERO, _ -> barraTimeline.setProgress(0)),
                new KeyFrame(Duration.seconds(5), _ -> barraTimeline.setProgress(1))
            );
            timeline.getKeyFrames().setAll(
                new KeyFrame(Duration.ZERO, new KeyValue(barraTimeline.progressProperty(), 0)),
                new KeyFrame(Duration.seconds(5), new KeyValue(barraTimeline.progressProperty(), 1, Interpolator.EASE_BOTH))
            );
            timeline.currentTimeProperty().addListener((_, _, newTime) -> {
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
        Button btnCrearClientePrioridad = new Button("Crear Cliente Prioridad");
        Button btnFinDia = new Button("Fin del Día");
        btnCrearCliente.setStyle("-fx-font-size: 16px; -fx-background-color: #e67e22; -fx-text-fill: white; -fx-pref-width: 180px; -fx-pref-height: 40px;");
        btnCrearClientePrioridad.setStyle("-fx-font-size: 16px; -fx-background-color: #e67e22; -fx-text-fill: white; -fx-pref-width: 180px; -fx-pref-height: 40px;");
        btnFinDia.setStyle("-fx-font-size: 16px; -fx-background-color: #27ae60; -fx-text-fill: white; -fx-pref-width: 180px; -fx-pref-height: 40px;");

        // Acción del botón "Fin del Día"
        btnFinDia.setOnAction(_ -> {
            StringBuilder resumen = new StringBuilder("Resumen del día:\n\n");
            for (int i = 0; i < 3; i++) {
                resumen.append(barberosData.get(i).getNombre())
                    .append(": $").append((int)dineroBarberos[i]).append("\n");
            }
            double max = Math.max(dineroBarberos[0], Math.max(dineroBarberos[1], dineroBarberos[2]));
            for (int i = 0; i < 3; i++) {
                if (dineroBarberos[i] == max && max > 0) {
                    resumen.append("\n¡Ganador del día: ").append(barberosData.get(i).getNombre()).append("!\n");
                }
            }
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Fin del Día");
            alert.setHeaderText(null);
            alert.setContentText(resumen.toString());
            alert.show();
        });

        // HBox para los botones de crear cliente
        HBox botonesClientes = new HBox(10, btnCrearCliente, btnCrearClientePrioridad, btnFinDia);
        botonesClientes.setAlignment(Pos.CENTER);

        // VBox para mostrar la información de los barberos
        // Tabla para mostrar la información de los barberos
        tablaBarberos = new TableView<>(barberosData);
        tablaBarberos.setPrefHeight(120);
        tablaBarberos.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_FLEX_LAST_COLUMN);

        TableColumn<BarberoInfo, String> colNombre = new TableColumn<>("Barbero");
        colNombre.setCellValueFactory(cellData -> cellData.getValue().nombreProperty());
        // Hacer la columna editable
        colNombre.setCellFactory(TextFieldTableCell.forTableColumn());
        tablaBarberos.setEditable(true);
        colNombre.setOnEditCommit(event -> {
            event.getRowValue().setNombre(event.getNewValue());
        });

        TableColumn<BarberoInfo, Number> colDinero = new TableColumn<>("Dinero Ganado");
        colDinero.setCellValueFactory(cellData -> cellData.getValue().dineroProperty());

        tablaBarberos.getColumns().add(colNombre);
        tablaBarberos.getColumns().add(colDinero);

        // VBox para poner la tabla y los labels de los barberos
        VBox vboxBarberos = new VBox(10, tablaBarberos);
        vboxBarberos.setAlignment(Pos.CENTER);

        // VBox para sofá (siempre visible), botón y barberosInfo juntos, centrados
        VBox sofaYBoton = new VBox(10, sofaStack, botonesClientes, vboxBarberos);
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
        labelCobrandoCliente = new Label("Cobrando a Cliente: ");
        labelCobrandoCliente.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: #333;");

        // Crear un Label para "El Barbero"
        labelElBarbero = new Label("El Barbero: "); // Esto se actualizará dinámicamente
        labelElBarbero.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: #333;");

        // Crear un Label para el total acumulado
        labelTotalAcumulado = new Label("Total Acumulado: $0");
        labelTotalAcumulado.setStyle("-fx-font-size: 22px; -fx-font-weight: 900; -fx-text-fill: #111; -fx-font-family: 'Arial Black', Arial, sans-serif;");



        // Cuando el cliente paga
        labelCobrandoCliente.setText("Cobrando a Cliente: " + clienteContador);
        labelElBarbero.setText("El Barbero: " + (1));  // Aquí puedes asociar el número del barbero o la silla




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
        barraSuperior.setAlignment(Pos.CENTER); // <-- Centrado
        barraSuperior.setPadding(new Insets(0, 0, 0, 0)); // Sin margen izquierdo extra

        // Título para la tabla VIP
        Label tituloVIP = new Label("Clientes VIP");
        tituloVIP.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: #333;");

        ObservableList<String> listaVIP = FXCollections.observableArrayList(
            "Rivera", "Peluchín", "Darwin Menacho"
        );
        ListView<String> tablaVIP = new ListView<>(listaVIP);
        tablaVIP.setPrefSize(150, 90);
        tablaVIP.setStyle("-fx-background-color: rgba(255,255,255,0.7); -fx-border-color: #e67e22;");

        VBox vboxVIP = new VBox(5, tituloVIP, tablaVIP);
        vboxVIP.setAlignment(Pos.TOP_CENTER);
        vboxVIP.setPadding(new Insets(10));
        vboxVIP.setStyle("-fx-background-color: transparent;");

        // Agrega vboxVIP a la barra superior
        Region espaciadorVIP = new Region();
        HBox.setHgrow(espaciadorVIP, Priority.ALWAYS);

        HBox filaSuperior = new HBox(20, barraSuperior, espaciadorVIP, vboxVIP);
        filaSuperior.setAlignment(Pos.TOP_LEFT);
        filaSuperior.setPadding(new Insets(0, 40, 0, 40));

        // AGREGA LOS COMPONENTES AL ROOT
        root.getChildren().addAll(
            filaSuperior,         // Ahora la barra superior y la tabla VIP
            sillonesBarbero,
            espaciadorVertical,
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
            if (clienteActual[0] == 3) clienteActual[0] = 4;
            String imgPath = "/img/persona" + clienteActual[0] + ".png";
            ImageView nuevoCliente = new ImageView(new Image(getClass().getResourceAsStream(imgPath)));
            nuevoCliente.setFitWidth(60);
            nuevoCliente.setFitHeight(90);


            EventoCliente evento = EventoCliente.NINGUNO;
            int aleatorio = new Random().nextInt(10); // 10% problemático, 90% ninguno
            if (aleatorio == 0) evento = EventoCliente.PROBLEMATICO;
            eventosCliente.put(nuevoCliente, evento);

            // Tooltip con el nombre del cliente
            // Marca visual si es problemático
            if (evento == EventoCliente.PROBLEMATICO) {
                nuevoCliente.setStyle("-fx-effect: dropshadow(gaussian, red, 15, 0.5, 0, 0);");
            } else {
                nuevoCliente.setStyle("");
            }

            // Generar nombre aleatorio
            String nombre = nombresClientes[new Random().nextInt(nombresClientes.length)] + " #" + clienteContador;
            nuevoCliente.setPickOnBounds(true);
            Tooltip tooltip = new Tooltip(nombre);
            Tooltip.install(nuevoCliente, tooltip);
            // Mostrar notificación con el nombre del cliente creado
            mostrarNotificacion("Nuevo cliente: " + nombre, "#34495e");


            // Verifica si hay espacio total (sillas + sofá + de pie)
            int totalClientes = clientes.size();

            if (totalClientes >= 11) { // 3 sillas + 4 sofá + 4 de pie
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Sin espacio");
                alert.setHeaderText(null);
                alert.setContentText("No hay espacio disponible para el cliente.");
                alert.showAndWait();
                return;
            }

            clientes.add(nuevoCliente);
            clienteNumeros.put(nuevoCliente, clienteContador);
            clienteContador++;

            clienteActual[0]++;
            if (clienteActual[0] == 3) clienteActual[0] = 4;

            // Intentar sentar al cliente en una silla libre
            for (int i = 0; i < 3; i++) {
                if (sillasOcupadas.get(i) == null) {
                    sillasOcupadas.set(i, nuevoCliente);
                    barrasProgreso.get(i).setProgress(0);
                    timelines.get(i).playFromStart();
                    clientesEnSilla.get(i).setImage(nuevoCliente.getImage());
                    clientesEnSilla.get(i).setFitWidth(60);
                    clientesEnSilla.get(i).setFitHeight(90);
                    clientesEnSilla.get(i).setVisible(true);

                    // Animación suave al sentarse
                    TranslateTransition transition = new TranslateTransition(Duration.seconds(0.5), clientesEnSilla.get(i));
                    transition.setFromY(-50);
                    transition.setToY(0);
                    transition.play();
                    break;
                }
            }
            // ...después de crear el cliente y antes de agregarlo a la interfaz...


            actualizarSofaYEspera.run();
        });

        // Acción del botón de cliente prioritario
        btnCrearClientePrioridad.setOnAction(_ -> {
            String[] opciones = {
                "/img/persona3.png", 
                "/img/rivera.png", 
                "/img/peluchin.png", 
                "/img/darwin Menacho.png"
            };
            Random random = new Random();
            int idx = random.nextInt(opciones.length);
            String imgPath = opciones[idx];
            ImageView clientePrioridad = new ImageView(new Image(getClass().getResourceAsStream(imgPath)));
            clientePrioridad.setFitWidth(60);
            clientePrioridad.setFitHeight(90);
            clienteNumeros.put(clientePrioridad, clienteContador);
            clienteContador++;

            String nombre;
            if (imgPath.contains("rivera.png")) {
                nombre = "Rivera";
            } else if (imgPath.contains("peluchin.png")) {
                nombre = "Peluchín";
            } else if (imgPath.contains("darwin Menacho.png")) {
                nombre = "Darwin Menacho";
            } else {
                nombre = nombresClientes[new Random().nextInt(nombresClientes.length)] + " VIP #" + clienteContador;
            }

            EventoCliente evento = EventoCliente.VIP;
            eventosCliente.put(clientePrioridad, evento);

            clientePrioridad.setPickOnBounds(true);
            Tooltip tooltip = new Tooltip(nombre);
            Tooltip.install(clientePrioridad, tooltip);
            clientePrioridad.setStyle("-fx-effect: dropshadow(gaussian, gold, 15, 0.5, 0, 0);");
            // Mostrar notificación con el nombre del cliente VIP creado
            mostrarNotificacion("Nuevo cliente VIP: " + nombre, "#e67e22");


            // Busca la primera silla libre y lo sienta de inmediato
            boolean sentado = false;
            for (int i = 0; i < 3; i++) {
                if (sillasOcupadas.get(i) == null) {
                    sillasOcupadas.set(i, clientePrioridad);
                    barrasProgreso.get(i).setProgress(0);
                    timelines.get(i).playFromStart();
                    clientesEnSilla.get(i).setImage(clientePrioridad.getImage());
                    clientesEnSilla.get(i).setFitWidth(60);
                    clientesEnSilla.get(i).setFitHeight(90);
                    clientesEnSilla.get(i).setVisible(true);

                    // AGREGA ESTO:
                    TranslateTransition transition = new TranslateTransition(Duration.seconds(0.5), clientesEnSilla.get(i));
                    transition.setFromY(-50);
                    transition.setToY(0);
                    transition.play();

                    sentado = true;
                    break;
                }
            }
            // Si no hay silla libre, lo inserta en una posición aleatoria entre los primeros 4 del sofá
            if (!sentado) {
                int pos = random.nextInt(Math.min(sofaClientes.getChildren().size() + 1, 4)); // 0 a 4
                clientes.add(pos, clientePrioridad);
            } else {
                clientes.add(clientePrioridad);
            }
            actualizarSofaYEspera.run();
        });

        // Configura el evento de fin de cada Timeline (cuando termina el corte)
        for (int i = 0; i < 3; i++) {
            final int idx = i;
            timelines.get(i).setOnFinished(_ -> {
                ImageView clienteQuePagara = sillasOcupadas.get(idx);

                // Mostrar "pagando.png"
                Image pagandoImg = new Image(getClass().getResourceAsStream("/img/pagando.png"));
                clientesEnSilla.get(idx).setImage(pagandoImg);
                clientesEnSilla.get(idx).setFitWidth(140);   // Aumenta el ancho
                clientesEnSilla.get(idx).setFitHeight(140); // Aumenta el alto
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

        Scene scene = new Scene(rootPane, 1250, 900); // 1100 x 850
        primaryStage.setScene(scene);
        primaryStage.show();

        idDiaActual = DBService.crearNuevoDia();
        numeroDiaActual = obtenerNumeroDiaActual();
        labelDiaActual = new Label("Día: " + numeroDiaActual);
        labelDiaActual.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: #2980b9;");

        // Botón para ver historial de días
        Button btnHistorialDias = new Button("Ver Historial de Días");
        btnHistorialDias.setOnAction(_ -> mostrarHistorialDias());

        // Agrega el label y el botón a la barra superior existente
        barraSuperior.getChildren().addAll(labelDiaActual, btnHistorialDias);
    }

    // Método para obtener el número de día (conteo de registros en la tabla dias)
    private int obtenerNumeroDiaActual() {
        int count = 1;
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement("SELECT COUNT(*) FROM dias");
             ResultSet rs = ps.executeQuery()) {
            if (rs.next()) {
                count = rs.getInt(1);
            }
        } catch (Exception e) {
            System.err.println("Error al obtener el número de día actual: " + e.getMessage());
        }
        return count;
    }

    // Método para mostrar el historial de días en una ventana nueva
    private void mostrarHistorialDias() {
        Stage stage = new Stage();
        VBox root = new VBox(10);
        root.setPadding(new Insets(15));

        Label titulo = new Label("Historial de Días");
        titulo.setStyle("-fx-font-size: 20px; -fx-font-weight: bold;");

        TableView<DiaResumen> tablaDias = new TableView<>();
        tablaDias.setPlaceholder(new Label("Tabla sin contenido"));

        TableColumn<DiaResumen, Integer> colNumeroDia = new TableColumn<>("Día");
        colNumeroDia.setCellValueFactory(new PropertyValueFactory<>("numeroDia"));
        TableColumn<DiaResumen, String> colFecha = new TableColumn<>("Fecha");
        colFecha.setCellValueFactory(new PropertyValueFactory<>("fecha"));

        tablaDias.getColumns().clear();
        tablaDias.getColumns().add(colNumeroDia);
        tablaDias.getColumns().add(colFecha);

        // Método para cargar los días
        Runnable cargarDias = () -> {
            ObservableList<DiaResumen> dias = FXCollections.observableArrayList();
            try (Connection conn = DBUtil.getConnection();
                 PreparedStatement ps = conn.prepareStatement("SELECT id, fecha FROM dias ORDER BY id ASC");
                 ResultSet rs = ps.executeQuery()) {
                int contador = 1;
                while (rs.next()) {
                    dias.add(new DiaResumen(contador, rs.getInt("id"), rs.getString("fecha")));
                    contador++;
                }
            } catch (Exception e) {
                System.err.println("Error al cargar los días: " + e.getMessage());
            }
            tablaDias.setItems(dias);
        };

        // Botón para refrescar
        Button btnRefrescar = new Button("Refrescar");
        btnRefrescar.setOnAction(_ -> cargarDias.run());

        // Cargar los días al abrir la ventana
        cargarDias.run();

        // Después de cargarDias.run();
        Timeline autoRefresh = new Timeline(
            new KeyFrame(Duration.seconds(2), _ -> cargarDias.run())
        );
        autoRefresh.setCycleCount(Timeline.INDEFINITE);
        autoRefresh.play();

        // Cuando cierres la ventana, detén el Timeline:
        stage.setOnCloseRequest(_ -> autoRefresh.stop());

        // Al seleccionar un día, muestra las transacciones de ese día usando el id real
        tablaDias.setOnMouseClicked(event -> {
            if (event.getClickCount() == 2 && tablaDias.getSelectionModel().getSelectedItem() != null) {
                DiaResumen dia = tablaDias.getSelectionModel().getSelectedItem();
                mostrarTransaccionesDeDia(dia.getIdReal(), dia.getNumeroDia());
            }
        });

        root.getChildren().addAll(titulo, btnRefrescar, tablaDias);
        stage.setScene(new Scene(root, 400, 400));
        stage.setTitle("Historial de Días");
        stage.show();
    }

    // Clase auxiliar para mostrar días en la tabla
    public static class DiaResumen {
        private final int numeroDia; // número consecutivo
        private final int idReal;    // id real de la base de datos
        private final String fecha;
        public DiaResumen(int numeroDia, int idReal, String fecha) {
            this.numeroDia = numeroDia;
            this.idReal = idReal;
            this.fecha = fecha;
        }
        public int getNumeroDia() { return numeroDia; }
        public int getIdReal() { return idReal; }
        public String getFecha() { return fecha; }
    }

    // Método para mostrar las transacciones de un día específico
    private void mostrarTransaccionesDeDia(int idDia, int numeroDia) {
        Stage stage = new Stage();
        VBox root = new VBox(10);
        root.setPadding(new Insets(15));

        Label titulo = new Label("Transacciones Día " + numeroDia);
        titulo.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");

        TableView<DBService.TransaccionResumen> tabla = new TableView<>();
        tabla.setPlaceholder(new Label("Tabla sin contenido"));

        TableColumn<DBService.TransaccionResumen, String> colCliente = new TableColumn<>("Cliente");
        colCliente.setCellValueFactory(new PropertyValueFactory<>("nombreCliente"));
        TableColumn<DBService.TransaccionResumen, Integer> colBarbero = new TableColumn<>("ID Barbero");
        colBarbero.setCellValueFactory(new PropertyValueFactory<>("idBarbero"));
        TableColumn<DBService.TransaccionResumen, Double> colMonto = new TableColumn<>("Monto");
        colMonto.setCellValueFactory(new PropertyValueFactory<>("montoPago"));

        tabla.getColumns().add(colCliente);
        tabla.getColumns().add(colBarbero);
        tabla.getColumns().add(colMonto);

        // Método para recargar la tabla
        Runnable cargarTransacciones = () -> {
            tabla.setItems(
                javafx.collections.FXCollections.observableArrayList(
                    DBService.obtenerTransaccionesPorDia(idDia)
                )
            );
        };

        // Carga inicial
        cargarTransacciones.run();

        // Timeline para refrescar automáticamente cada 2 segundos
        Timeline autoRefresh = new Timeline(
            new KeyFrame(Duration.seconds(2), _ -> cargarTransacciones.run())
        );
        autoRefresh.setCycleCount(Timeline.INDEFINITE);
        autoRefresh.play();

        // Detener el refresco automático al cerrar la ventana
        stage.setOnCloseRequest(_ -> autoRefresh.stop());

        root.getChildren().addAll(titulo, tabla);
        stage.setScene(new Scene(root, 400, 400));
        stage.setTitle("Transacciones Día " + numeroDia);
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }

    // Lista de nombres masculinos
    private final String[] nombresClientes = {
        "Carlos", "Luis", "Pedro", "Jorge", "Miguel", "David", "Juan", "Andrés", "Sergio", "Fernando", "Manuel", "Diego"
    };
}