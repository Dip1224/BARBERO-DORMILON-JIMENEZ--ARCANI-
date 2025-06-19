public class Main {
    public static void main(String[] args) {
        SalaDeEspera salaDeEspera = new SalaDeEspera(4); // 4 sillas disponibles
        Barbero barbero = new Barbero();
        barbero.start();

        // Crear clientes
        Cliente cliente1 = new Cliente("Cliente 1", false);
        Cliente cliente2 = new Cliente("Cliente 2", true); // VIP
        Cliente cliente3 = new Cliente("Cliente 3", false);
        Cliente cliente4 = new Cliente("Cliente 4", true); // VIP
        Cliente cliente5 = new Cliente("Cliente 5", false);

        // Agregar clientes a la sala
        salaDeEspera.agregarCliente(cliente1);
        salaDeEspera.agregarCliente(cliente2);
        salaDeEspera.agregarCliente(cliente3);
        salaDeEspera.agregarCliente(cliente4);
        salaDeEspera.agregarCliente(cliente5);

        // Atender a los clientes
        while (true) {
            Cliente cliente = salaDeEspera.obtenerCliente();
            if (cliente != null) {
                barbero.atenderCliente(cliente);
            }
        }
    }
}
