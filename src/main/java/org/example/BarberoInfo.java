package org.example;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class BarberoInfo {
    private final StringProperty nombre;
    private final DoubleProperty dinero;

    public BarberoInfo(String nombre, double dinero) {
        this.nombre = new SimpleStringProperty(nombre);
        this.dinero = new SimpleDoubleProperty(dinero);
    }

    public String getNombre() { return nombre.get(); }
    public void setNombre(String nombre) { this.nombre.set(nombre); }
    public StringProperty nombreProperty() { return nombre; }

    public double getDinero() { return dinero.get(); }
    public void setDinero(double dinero) { this.dinero.set(dinero); }
    public DoubleProperty dineroProperty() { return dinero; }
}