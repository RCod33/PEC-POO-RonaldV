package Workers;

import java.util.Date;

public class Operator extends Worker{

    private static final int ASSEMBLIES_FOR_EFFICIENT = 10;
    private String perfil;
    private int numOfAssemblies;

    public Operator(String name, String lastName, String secondLastName, String DNI, String address, String numeroSS,
                double salary, Date entryDate, int numOfAssemblies) {

        super(name, lastName, secondLastName, DNI, address, numeroSS, salary, entryDate);

        setNumOfAssemblies(numOfAssemblies);
    }

    @Override
    public String getRole() {
        return "Operario";
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
        if (numOfAssemblies > ASSEMBLIES_FOR_EFFICIENT) {
            perfil = "EFICIENTE";
        } else {
            perfil = "ESTANDAR";
        }
    }

    public void updateOperator() {
        ++numOfAssemblies;
        updatePerfil();
    }
}
