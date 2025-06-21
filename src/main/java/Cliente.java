public class Cliente implements Runnable {
    private final String nombre;
    private final boolean esVIP;
    private final int tiempoEspera; // en segundos

    public Cliente(String nombre, boolean esVIP) {
        this.nombre = nombre;
        this.esVIP = esVIP;
        this.tiempoEspera = esVIP ? 5 : 10; // VIP espera menos
    }

    @Override
    public void run() {
        try {
            System.out.println(nombre + " está esperando.");
            Thread.sleep(tiempoEspera * 1000); // Simula el tiempo de espera
            System.out.println(nombre + " está siendo atendido.");
        } catch (InterruptedException e) {
            System.err.println("El hilo del cliente fue interrumpido: " + e.getMessage());
        }
    }

    public String getNombre() {
        return nombre;
    }

    public boolean isVIP() {
        return esVIP;
    }

    // Método para simular que el cliente llega a la sala
    public synchronized void agregarACola(SalaDeEspera sala) {
        sala.agregarCliente(this);
    }
}
