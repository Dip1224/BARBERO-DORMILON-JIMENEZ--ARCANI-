import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.Semaphore;

public class SalaDeEspera {
    private Queue<Cliente> colaDeEspera;
    private Semaphore sillas;

    public SalaDeEspera(int capacidad) {
        colaDeEspera = new LinkedList<>();
        sillas = new Semaphore(capacidad, true); // Las sillas disponibles
    }

    public synchronized void agregarCliente(Cliente cliente) {
        if (sillas.availablePermits() > 0) {
            colaDeEspera.add(cliente);
            System.out.println(cliente.getNombre() + " se sentó.");
            sillas.release(); // Un cliente ocupa una silla
        } else {
            System.out.println(cliente.getNombre() + " se fue porque no hay lugar.");
        }
    }

    public synchronized Cliente obtenerCliente() {
        if (!colaDeEspera.isEmpty()) {
            Cliente cliente = colaDeEspera.poll();
            sillas.acquireUninterruptibly(); // Barbero está atendiendo
            return cliente;
        }
        return null;
    }
}
