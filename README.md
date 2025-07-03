# **Barbero Dormilón - Simulación de Barbería**

**Barbero Dormilón** es una aplicación interactiva desarrollada en **JavaFX** que simula la gestión de una **barbería** con múltiples **barberos**, **clientes normales**, **VIP** y **problemáticos**, colas de espera, caja registradora y eventos aleatorios. El proyecto está pensado como una herramienta didáctica para comprender la **concurrencia**, la **gestión de recursos** y la **interacción gráfica** en **Java**.

## **Características principales**

- **Simulación visual** de una barbería con 3 barberos, sillas, sofá y sala de espera.
- **Clientes normales**, **VIP** y **problemáticos**:
  - Los VIP pagan el **doble** y tienen **prioridad** en la atención.
  - Los problemáticos **retrasan** la cola de pago.
- **Eventos aleatorios**: Cada cliente puede ser normal o problemático al azar.
- **Notificaciones automáticas** para eventos importantes (VIP, problemáticos, logros).
- **Gestión de caja**: Animación de cobro, suma de dinero y ranking de barberos.
- **Persistencia**: Guarda el récord de dinero ganado por cada barbero.
- **Interfaz amigable** con botones para crear clientes, clientes prioritarios y finalizar el día.
- **Resumen del día**: Muestra el barbero ganador y el dinero acumulado.
- **Tabla de barberos**: Muestra en tiempo real el dinero ganado por cada barbero.
- **Animaciones**: Transiciones suaves al sentarse, pagar y moverse por la barbería.

## **Estructura del código**

- **BarberiaApp.java**: Clase principal, contiene la lógica de la interfaz y la simulación.
- **BarberoInfo.java**: Clase auxiliar para la tabla de barberos.
- **(Opcional)** **Cliente.java** / **Barbero.java**: Puedes dividir la lógica en clases para mayor claridad.
- **Recursos**: Imágenes de clientes, barberos, sofá, caja, etc.

## **¿Cómo funciona?**

1. **Crear Cliente**: Agrega un cliente normal o problemático a la barbería.
2. **Crear Cliente Prioridad**: Agrega un cliente VIP que paga el doble y se atiende primero.
3. **Fin del Día**: Muestra un resumen con el barbero que más dinero ganó.
4. **Eventos automáticos**: Los clientes pasan por las sillas, pagan en caja y salen; los eventos VIP y problemáticos se notifican automáticamente.
5. **Persistencia**: El récord de cada barbero se guarda y carga automáticamente.

## **Tecnologías usadas**

- **Java 17+**
- **JavaFX**
- **Programación orientada a objetos**
- **Animaciones** y **notificaciones personalizadas**




## **¿Cómo ejecutar el proyecto?**

1. **Clonar el repositorio:**

   ```bash
   git clone https://github.com/Dip1224/BARBERO-DORMILON-JIMENEZ--ARCANI-.git
   ```

2. **Abrir el proyecto en tu IDE favorito** (IntelliJ IDEA, Eclipse, VS Code, etc.)

3. **Compilar y ejecutar:**
   - Si usas Maven:
     ```bash
     mvn clean javafx:run
     ```
   - O ejecuta la clase `BarberiaApp` desde tu IDE.

4. **¡Listo!** Disfruta de la simulación y experimenta con los diferentes tipos de clientes y eventos.

## **Capturas de pantalla**

![image](https://github.com/user-attachments/assets/cc9fd5d4-c41f-492b-9af5-bba2c3df1407)
