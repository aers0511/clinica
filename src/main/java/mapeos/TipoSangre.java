package mapeos;

public enum TipoSangre {
    A_POSITIVO("A+"), A_NEGATIVO("A-"),
    B_POSITIVO("B+"), B_NEGATIVO("B-"),
    AB_POSITIVO("AB+"), AB_NEGATIVO("AB-"),
    O_POSITIVO("O+"), O_NEGATIVO("O-");

    private final String etiqueta;

    TipoSangre(String etiqueta) {
        this.etiqueta = etiqueta;
    }

    @Override
    public String toString() {
        return etiqueta;
    }
}