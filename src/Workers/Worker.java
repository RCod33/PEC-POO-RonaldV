package Workers;

import java.util.Date;
import java.util.Objects;

abstract public class Worker {
    private String name;
    private String lastName;
    private String secondLastName;
    private String DNI;
    private String address;
    private double salary;
    private final Date entryDate;
    private String numeroSS;

    public Worker(String name, String lastName, String secondLastName, String DNI,
                  String address, String numeroSS, double salary, Date entryDate) {
        this.name = name;
        this.lastName = lastName;
        this.secondLastName = secondLastName;
        if (isValidDNI(DNI)) {
            this.DNI = DNI;
        } else {
            throw new IllegalArgumentException("El formato de DNI es incorrecto");
        }
        this.address = address;
        if (!isValidNumeroSS(numeroSS)) {
            throw new IllegalArgumentException("El formato del número de SS es incorrecto");
        }
        this.numeroSS = numeroSS;
        if (salary <= 0) throw new IllegalArgumentException("El salario debe de ser positivo");
        this.salary = salary;
        this.entryDate = (entryDate != null) ? entryDate : new Date();
    }

    public abstract String getRole();

    public String getNumeroSS() {
        return numeroSS;
    }

    public void setNumeroSS(String numeroSS) {
        if (!isValidNumeroSS(numeroSS)) {
            throw new IllegalArgumentException("El formato del número de SS es incorrecto");
        }
        this.numeroSS = numeroSS;
    }

    private boolean isValidNumeroSS(String ss) {
        return ss != null && ss.matches("\\d{2}/\\d{8}/\\d{2}");
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getSecondLastName() {
        return secondLastName;
    }

    public void setSecondLastName(String secondLastName) {
        this.secondLastName = secondLastName;
    }

    public String getDNI() {
        return DNI;
    }

    public void setDNI(String DNI) {
        if (isValidDNI(DNI)) {
            this.DNI = DNI;
        } else {
            throw new IllegalArgumentException("El formato de DNI es incorrecto");
        }
    }

    public Date getEntryDate() {
        return entryDate;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public double getSalary() {
        return salary;
    }

    public void setSalary(double salary) {
        if(salary <= 0) throw new IllegalArgumentException("El salario debe de ser positivo");
        this.salary = salary;
    }

    private boolean isValidDNI(String dni) {
        String letras = "TRWAGMYFPDXBNJZSQVHLCKE";

        if (dni == null || !dni.matches("\\d{8}[A-Z]")) {
            return false;
        }

        int number = Integer.parseInt(dni.substring(0, 8));
        char letter = dni.charAt(8);

        return letras.charAt(number % 23) == letter;
    }

    @Override
    public String toString() {
        return getRole() + " | " + name + " " + lastName + " - " + DNI;
    }

    @Override
    public boolean equals(Object obj) {

        if (!(obj instanceof Worker)) {
            return false;
        }

        Worker worker = (Worker) obj;

        return this.DNI.equals(worker.DNI);
    }

    @Override
    public int hashCode() {
        return Objects.hash(DNI);
    }

}
