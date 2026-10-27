package model.vo;

public class Contacto {
    private Long idContacto;
    private String contacto;

    public Contacto() {
    }

    public Contacto(String contacto) {
        this.contacto = contacto;
    }

    public Contacto(Long idContacto, String contacto) {
        this.idContacto = idContacto;
        this.contacto = contacto;
    }

    public Long getIdContacto() {
        return idContacto;
    }

    public void setIdContacto(Long idContacto) {
        this.idContacto = idContacto;
    }

    public String getContacto() {
        return contacto;
    }

    public void setContacto(String contacto) {
        this.contacto = contacto;
    }

    @Override
    public String toString() {
        return "Contacto [contacto=" + contacto + "]";
    }

    

}
