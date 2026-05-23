package Workers;

public interface LineWorker {

    /**
     * Devuelve el tiempo (en segundos/ticks) que este trabajador
     * necesita para completar su tarea en la cinta.
     */
    int getWorkTime();

    /**
     * Devuelve el perfil actual del trabajador: "EFICIENTE" o "ESTANDAR".
     */
    String getPerfil();

    /**
     * Actualiza el estado interno del trabajador tras completar una tarea
     * (incrementa contador de experiencia y recalcula perfil).
     */
    void updateWorker();

}