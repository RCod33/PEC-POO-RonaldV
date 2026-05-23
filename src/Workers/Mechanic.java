package Workers;

import java.util.Date;
import java.util.Random;

public class Mechanic extends Worker {

    private static final int REPAIRS_FOR_EFFICIENT = 20;

    private String perfil;
    private int numOfRepairs;

    public Mechanic(String name, String lastName, String secondLastName, String DNI,
                    String address, String numeroSS, double salary, Date entryDate,
                    int numOfRepairs) {
        super(name, lastName, secondLastName, DNI, address, numeroSS, salary, entryDate);
        setNumOfRepairs(numOfRepairs);
    }

    @Override
    public String getRole() {
        return "Mecánico de Cinta";
    }

    public void setNumOfRepairs(int numOfRepairs) {
        if (numOfRepairs < 0) {
            throw new IllegalArgumentException("El número de reparaciones no puede ser negativo");
        }
        this.numOfRepairs = numOfRepairs;
        updatePerfil();
    }

    public int getNumOfRepairs() {
        return numOfRepairs;
    }

    public String getPerfil() {
        return perfil;
    }

    public int getRepairTime() {
        if (numOfRepairs > REPAIRS_FOR_EFFICIENT) {
            return 1;
        } else {
            return 2 + new Random().nextInt(4); // [2, 5]
        }
    }

    private void updatePerfil() {
        perfil = (numOfRepairs > REPAIRS_FOR_EFFICIENT) ? "EFICIENTE" : "ESTANDAR";
    }

    public void updateMechanic() {
        ++numOfRepairs;
        updatePerfil();
    }
}