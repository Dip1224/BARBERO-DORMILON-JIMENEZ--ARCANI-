public class Barbero extends Thread {
    private boolean ocupado;

    public Barbero() {
        this.ocupado = false;
    }

    @Override
    public void run() {
        while (true) {
            if (!ocupado) {
                System.out.println("El barbero está esperando clientes...");
                try {
                    Thread.sleep(2000); // El barbero espera por 2 segundos antes de ir a atender
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            } else {
                System.out.println("El barbero está atendiendo.");
            }
        }
    }

    public void atenderCliente(Cliente cliente) {
        ocupado = true;
        System.out.println("Atendiendo a: " + cliente.getNombre());
        try {
            Thread.sleep(5000); // El tiempo de atención (5 segundos)
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        ocupado = false;
        System.out.println("Cliente " + cliente.getNombre() + " atendido.");
    }
}
