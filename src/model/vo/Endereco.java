package model.vo;

import java.util.List;

public class Endereco {
private Long idEndereco;
private  String quarteirao;
private String rua;
private Bairro bairro;
private String numeroCasa;

public Endereco() {
}

public Endereco(String quarteirao, String rua, Bairro bairro, String numeroCasa) {
    this.quarteirao = quarteirao;
    this.rua = rua;
    this.bairro = bairro;
    this.numeroCasa = numeroCasa;
}

public Endereco(String quarteirao, String rua, Bairro bairro) {
    this.quarteirao = quarteirao;
    this.rua = rua;
    this.bairro = bairro;
}

public Endereco(Long idEndereco, String quarteirao, String rua, Bairro bairro, String numeroCasa) {
    this.idEndereco = idEndereco;
    this.quarteirao = quarteirao;
    this.rua = rua;
    this.bairro = bairro;
    this.numeroCasa = numeroCasa;
}

public Long getIdEndereco() {
    return idEndereco;
}

public void setIdEndereco(Long idEndereco) {
    this.idEndereco = idEndereco;
}

public String getQuarteirao() {
    return quarteirao;
}

public void setQuarteirao(String quarteirao) {
    this.quarteirao = quarteirao;
}

public String getRua() {
    return rua;
}

public void setRua(String rua) {
    this.rua = rua;
}

public Bairro getBairro() {
    return bairro;
}

public void setBairro(Bairro bairro) {
    this.bairro = bairro;
}

public String getNumeroCasa() {
    return numeroCasa;
}

public void setNumeroCasa(String numeroCasa) {
    this.numeroCasa = numeroCasa;
}

@Override
public String toString() {
    return "Endereco [quarteirao=" + quarteirao + ", rua=" + rua + ", bairro=" + bairro + ", numeroCasa=" + numeroCasa
            + "]";
}




    
}
