package model.dao;

import java.util.ArrayList;
import java.util.List;

import infra.ConexaoDB;
import model.vo.Contacto;

public class ContactoDAO implements GenericoDAO<Contacto, Long>{

    @Override
    public void salvar(Contacto objecto) {
        String sql = "INSERT INTO contacto (contacto) VALUES (?)";
        try(var conn = ConexaoDB.getConnection();   
            var stmt = conn.prepareStatement(sql)){
            stmt.setString(1, objecto.getContacto());
            stmt.executeUpdate();

            try(var rs = stmt.getGeneratedKeys()){
                if(rs.next()){
                    objecto.setIdContacto(rs.getLong(1));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void eliminar(Long id) {
        String sql = "DELETE FROM contacto WHERE idContacto = ?";
        try(var conn = ConexaoDB.getConnection();
            var stmt = conn.prepareStatement(sql)){
            stmt.setLong(1, id);
            stmt.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void atualizar(Contacto objecto) {
        String sql = "UPDATE contacto SET contacto = ? WHERE idContacto = ?";
        try(var conn = ConexaoDB.getConnection();
            var stmt = conn.prepareStatement(sql)){
            stmt.setString(1, objecto.getContacto());
            stmt.setLong(2, objecto.getIdContacto());
            stmt.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public List<Contacto> listarTodos() {
        String sql = "SELECT * FROM contacto";
        List<Contacto> contactos = new ArrayList<>();
        try(var conn = ConexaoDB.getConnection();
            var stmt = conn.prepareStatement(sql);
            var rs = stmt.executeQuery()){
            while(rs.next()){
                Contacto contacto = new Contacto();
                contacto.setIdContacto(rs.getLong("idContacto"));
                contacto.setContacto(rs.getString("contacto"));
                contactos.add(contacto);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return contactos;
    }

    @Override
    public Contacto buscarPorId(Long id) {
        String sql = "SELECT * FROM contacto WHERE idContacto = ?";
        Contacto contacto = null;
        try(var conn = ConexaoDB.getConnection();
            var stmt = conn.prepareStatement(sql)){
            stmt.setLong(1, id);
            try(var rs = stmt.executeQuery()){
                if(rs.next()){
                    contacto = new Contacto();
                    contacto.setIdContacto(rs.getLong("idContacto"));
                    contacto.setContacto(rs.getString("contacto"));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return contacto;
    }
    
}
