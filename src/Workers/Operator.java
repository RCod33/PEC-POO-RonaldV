package Workers;

import java.util.Date;

public class Operator extends Worker{

    private String perfil;
    private int numOfAssemblies;

    public Operator(String name, String lastName, String secondLastName, String DNI, String address,
                double salary, Date entryDate, int numOfAssemblies) {

        super(name, lastName, secondLastName, DNI, address, salary, entryDate);

        setNumOfAssemblies(numOfAssemblies);
    }

    public void setNumOfAssemblies(int numOfAssemblies) {
        if (numOfAssemblies >= 0) {
            this.numOfAssemblies = numOfAssemblies;
            updatePerfil();
        } else {
            throw new IllegalArgumentException("El numero de montajes realizados no puede ser negativo");

        }
    }

    public int getNumOfAssemblies() {
        return numOfAssemblies;
    }

    public String getPerfil() {
        return perfil;
    }

    public int getTimePerAssemblies() {
        return (numOfAssemblies > 10) ? 1 : 3;
    }

    private void updatePerfil() {
        if (numOfAssemblies > 10) {
            perfil = "EICIENTE";
        } else {
            perfil = "ESTANDAR";
        }
    }

    public void updateOperator() {
        ++numOfAssemblies;
        updatePerfil();
    }
}
