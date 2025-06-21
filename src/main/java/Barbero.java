public class Barbero extends Thread {
    private boolean ocupado;

    public Barbero() {
        this.ocupado = false;
    }

    @Override
    public void run() {
        while (true) {
            synchronized (this) {
                if (!ocupado) {
                    System.out.println("El barbero está esperando clientes...");
                    try {
                        wait(); // El barbero espera hasta que haya un cliente
                    } catch (InterruptedException e) {
                        System.err.println("Interrupted while atendiendo al cliente: " + e.getMessage());
                    }
                } else {
                    System.out.println("El barbero está atendiendo.");
                }
            }
        }
    }

    public void atenderCliente(Cliente cliente) {
        synchronized (this) {
            ocupado = true;
            System.out.println("Atendiendo a: " + cliente.getNombre());
        }
        try {
            Thread.sleep(5000); // El tiempo de atención (5 segundos)
        } catch (InterruptedException e) {
            System.err.println("Interrupted while atendiendo al cliente: " + e.getMessage());
        }
        synchronized (this) {
            ocupado = false;
            System.out.println("Cliente " + cliente.getNombre() + " atendido.");
            notify(); // Notifica al hilo principal que el barbero está disponible nuevamente
        }
    }
}
