package model.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import infra.ConexaoDB;
import model.vo.Cliente;

public class ClienteDAO implements GenericoDAO<Cliente,Long>{

    @Override
    public void salvar(Cliente objecto) {
        String sql = "INSERT INTO cliente (nomeCliente, telefoneCliente, emailCliente) VALUES (?, ?, ?)";
        try(Connection conn = ConexaoDB.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql)){
            stmt.setString(1, objecto.getNome());
            stmt.setString(2, objecto.getTelefone());
            stmt.setString(3, objecto.getEmail());
            stmt.executeUpdate();

            try(ResultSet rs = stmt.getGeneratedKeys()){
                if(rs.next()){
                    objecto.setIdCliente(rs.getLong(1));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void eliminar(Long id) {
        String sql = "DELETE FROM cliente WHERE idCliente = ?";
        try(Connection conn = ConexaoDB.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql)){
            stmt.setLong(1, id);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void atualizar(Cliente objecto) {
        String sql = "UPDATE cliente SET nomeCliente = ?, telefoneCliente = ?, emailCliente = ? WHERE idCliente = ?";
        try(Connection conn = ConexaoDB.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql)){
            stmt.setString(1, objecto.getNome());
            stmt.setString(2, objecto.getTelefone());
            stmt.setString(3, objecto.getEmail());
            stmt.setLong(4, objecto.getIdCliente());
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public List<Cliente> listarTodos() {
        String sql = "SELECT * FROM cliente";
        List<Cliente> clientes = new ArrayList<>();
        try(Connection conn = ConexaoDB.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql);
            ResultSet rs = stmt.executeQuery()){
            while(rs.next()){
                Cliente cliente = new Cliente();
                cliente.setIdCliente(rs.getLong("idCliente"));
                cliente.setNome(rs.getString("nomeCliente"));
                cliente.setTelefone(rs.getString("telefoneCliente"));
                cliente.setEmail(rs.getString("emailCliente"));
                clientes.add(cliente);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return clientes;
    }

    @Override
    public Cliente buscarPorId(Long id) {
        String sql = "SELECT * FROM cliente WHERE idCliente = ?";
        Cliente cliente = null;
        try(Connection conn = ConexaoDB.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql)){
            stmt.setLong(1, id);
            try(ResultSet rs = stmt.executeQuery()){
                if(rs.next()){
                    cliente = new Cliente();
                    cliente.setIdCliente(rs.getLong("idCliente"));
                    cliente.setNome(rs.getString("nomeCliente"));
                    cliente.setTelefone(rs.getString("telefoneCliente"));
                    cliente.setEmail(rs.getString("emailCliente"));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return cliente;
    }
     
}
