
# **Barbero Dormilón - Simulación de Barbería**

**Barbero Dormilón** es una aplicación interactiva desarrollada en **JavaFX** que simula la gestión de una **barbería** con múltiples **barberos**, **clientes normales**, **VIP** y **problemáticos**, colas de espera, caja registradora y eventos aleatorios. El proyecto está pensado como una herramienta didáctica para comprender la **concurrencia**, la **gestión de recursos**, la **interacción gráfica** en **Java** y el uso de **bases de datos** para persistencia real.


## **Características principales**

- **Simulación visual** de una barbería con 3 barberos, sillas, sofá y sala de espera.
- **Clientes normales**, **VIP** y **problemáticos**:
  - Los VIP pagan el **doble** y tienen **prioridad** en la atención.
  - Los problemáticos **retrasan** la cola de pago.
- **Eventos aleatorios**: Cada cliente puede ser normal o problemático al azar.
- **Notificaciones automáticas** para eventos importantes (VIP, problemáticos, logros).
- **Gestión de caja**: Animación de cobro, suma de dinero y ranking de barberos.
- **Persistencia avanzada con base de datos**: Se utiliza una base de datos (SQLite) para guardar el dinero ganado por cada barbero y el historial de transacciones de cada día. Esto permite consultar el historial, ver récords y mantener la información aunque cierres la aplicación.
- **Interfaz amigable** con botones para crear clientes, clientes prioritarios y finalizar el día.
- **Resumen del día**: Muestra el barbero ganador y el dinero acumulado.
- **Tabla de barberos**: Muestra en tiempo real el dinero ganado por cada barbero.
- **Animaciones**: Transiciones suaves al sentarse, pagar y moverse por la barbería.


## **Estructura del código**

- **BarberiaApp.java**: Clase principal, contiene la lógica de la interfaz, la simulación y la conexión con la base de datos.
- **BarberoInfo.java**: Clase auxiliar para la tabla de barberos.
- **DBService.java**: Clase encargada de la gestión de la base de datos (guardar barberos, transacciones, días, etc.).
- **DBUtil.java**: Utilidades para la conexión y manejo de la base de datos SQLite.
- **(Opcional)** **Cliente.java** / **Barbero.java**: Puedes dividir la lógica en clases para mayor claridad.
- **Recursos**: Imágenes de clientes, barberos, sofá, caja, etc.


## **¿Cómo funciona?**

1. **Crear Cliente**: Agrega un cliente normal o problemático a la barbería.
2. **Crear Cliente Prioridad**: Agrega un cliente VIP que paga el doble y se atiende primero.
3. **Fin del Día**: Muestra un resumen con el barbero que más dinero ganó y guarda los resultados en la base de datos.
4. **Eventos automáticos**: Los clientes pasan por las sillas, pagan en caja y salen; los eventos VIP y problemáticos se notifican automáticamente.
5. **Persistencia**: El récord de cada barbero y el historial de transacciones se guardan y cargan automáticamente desde la base de datos SQLite.


## **Tecnologías usadas**

- **Java 17+**
- **JavaFX**
- **SQLite** (base de datos local)
- **Programación orientada a objetos**
- **Animaciones** y **notificaciones personalizadas**





## **¿Cómo ejecutar el proyecto?**

1. **Clonar el repositorio:**

   ```bash
   git clone https://github.com/Dip1224/BARBERO-DORMILON-JIMENEZ--ARCANI-.git
   ```

2. **Abrir el proyecto en tu IDE favorito** (IntelliJ IDEA, Eclipse, VS Code, etc.)

3. **Configura la base de datos:**
   - El proyecto crea automáticamente la base de datos SQLite (`barberia.db`) en la primera ejecución.
   - Si quieres limpiar el historial, puedes borrar el archivo `barberia.db` y se regenerará.

4. **Compilar y ejecutar:**
   - Si usas Maven:
     ```bash
     mvn clean javafx:run
     ```
   - O ejecuta la clase `BarberiaApp` desde tu IDE.

5. **¡Listo!** Disfruta de la simulación y experimenta con los diferentes tipos de clientes y eventos. Consulta el historial y récords gracias a la base de datos.

## **Capturas de pantalla**

![image](https://github.com/user-attachments/assets/cc9fd5d4-c41f-492b-9af5-bba2c3df1407)
