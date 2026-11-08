package model.vo;

import java.io.Serializable;

public class Contacto implements Serializable {
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
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        Contacto outro = (Contacto) obj;
        if (this.idContacto == null) {
            if (outro.idContacto != null) {
                return false;
            }
        } else {
            if (!this.idContacto.equals(outro.idContacto)) {
                return false;
            }
        }
        return true;
    }

    @Override
    public int hashCode() {
        int primo = 31;
        int resultado = 1;
        if (this.idContacto != null) {
            resultado = primo * resultado + this.idContacto.hashCode();
        } else {
            resultado = primo * resultado + 0;
        }
        return resultado;
    }

    @Override
    public String toString() {
        return "Contacto [idContacto=" + idContacto + ", contacto=" + contacto + "]";
    }
}
