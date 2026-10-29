package model.dao;

import java.util.List;

import infra.ConexaoDB;
import model.vo.Endereco;

public class EnderecoDAO implements GenericoDAO<Endereco, Long> {

    @Override
    public void salvar(Endereco objecto) {
        String sql = "INSERT INTO endereco (rua, cidade, estado, cep) VALUES (?, ?, ?, ?)";
        try(var conn = ConexaoDB.getConnection();
            var stmt = conn.prepareStatement(sql)){
            stmt.setString(1, objecto.getRua());
            stmt.setString(2, objecto.getCidade());
            stmt.setString(3, objecto.getEstado());
            stmt.setString(4, objecto.getCep());
            stmt.executeUpdate();

            try(var rs = stmt.getGeneratedKeys()){
                if(rs.next()){
                    objecto.setIdEndereco(rs.getLong(1));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void eliminar(Long id) {
        
    }

    @Override
    public void atualizar(Endereco objecto) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'atualizar'");
    }

    @Override
    public List<Endereco> listarTodos() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'listarTodos'");
    }

    @Override
    public Endereco buscarPorId(Long id) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'buscarPorId'");
    }

    
}