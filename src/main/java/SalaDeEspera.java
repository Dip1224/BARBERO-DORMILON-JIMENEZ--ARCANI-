import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.Semaphore;

public class SalaDeEspera {
    private final Queue<Cliente> colaDeEspera;
    private final Semaphore sillas;  // Control de sillas disponibles

    public SalaDeEspera(int capacidad) {
        colaDeEspera = new LinkedList<>();
        sillas = new Semaphore(capacidad, true); // Las sillas disponibles
    }

    // Método para agregar un cliente a la sala de espera
    public void agregarCliente(Cliente cliente) {
        try {
            if (sillas.availablePermits() > 0) {
                sillas.acquire(); // Intentar adquirir una silla
                colaDeEspera.add(cliente);
                System.out.println(cliente.getNombre() + " se sentó.");
            } else {
                System.out.println(cliente.getNombre() + " no pudo sentarse, no hay sillas disponibles.");
            }
        } catch (InterruptedException e) {
            System.err.println("Error al agregar cliente a la sala de espera: " + e.getMessage());
        }
    }

    // Método para obtener un cliente de la sala de espera
    public Cliente obtenerCliente() {
        if (!colaDeEspera.isEmpty()) {
            Cliente cliente = colaDeEspera.poll();
            sillas.release(); // Liberar la silla después de que el cliente es atendido
            System.out.println(cliente.getNombre() + " ha sido atendido y ha dejado su silla.");
            return cliente;
        }
        return null;
    }
}
