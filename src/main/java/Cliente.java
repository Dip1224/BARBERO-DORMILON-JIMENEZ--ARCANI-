public class Cliente extends Thread {
    private String nombre;
    private boolean esVIP;
    private int tiempoEspera; // en segundos

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
            e.printStackTrace();
        }
    }

    public String getNombre() {
        return nombre;
    }

    public boolean isVIP() {
        return esVIP;
    }
}
