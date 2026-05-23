package Workers;

import java.util.Date;

public class Operator extends Worker implements LineWorker{

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

    @Override
    public int getWorkTime() {
        return (numOfAssemblies > ASSEMBLIES_FOR_EFFICIENT) ? 1 : 3;
    }

    @Override
    public void updateWorker() {
        ++numOfAssemblies;
        updatePerfil();
    }

    // --- Operario-specific ---

    public void setNumOfAssemblies(int numOfAssemblies) {
        if (numOfAssemblies < 0)
            throw new IllegalArgumentException("El número de montajes no puede ser negativo");
        this.numOfAssemblies = numOfAssemblies;
        updatePerfil();
    }

    public int getNumOfAssemblies() { return numOfAssemblies; }

    @Override
    public String getPerfil() { return perfil; }

    /** @deprecated Usar updateWorker() para seguir el contrato LineWorker */
    @Deprecated
    public void updateOperator() { updateWorker(); }

    /** @deprecated Usar getWorkTime() para seguir el contrato LineWorker */
    @Deprecated
    public int getTimePerAssemblies() { return getWorkTime(); }

    private void updatePerfil() {
        perfil = (numOfAssemblies > ASSEMBLIES_FOR_EFFICIENT) ? "EFICIENTE" : "ESTANDAR";
    }
}
