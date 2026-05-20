package Workers;

import java.util.Date;

public class Mechanic extends Worker {
    public Mechanic(String name, String lastName, String secondLastName, String DNI, String address, double salary, Date entryDate) {
        super(name, lastName, secondLastName, DNI, address, salary, entryDate);
    }
}
