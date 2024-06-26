/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ec.servicio;

import com.ec.entidad.Producto;
import com.ec.entidad.Producto;
import com.ec.entidad.Tipoambiente;
import com.ec.untilitario.ProductoProveedorCosto;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.Query;

/**
 *
 * @author gato
 */
public class ServicioProducto {

    private EntityManager em;

    public EntityManager getEm() {
        return em;
    }

    public void setEm(EntityManager em) {
        this.em = em;
    }

    public void crear(Producto producto) {

        try {
            em = HelperPersistencia.getEMF();
            em.getTransaction().begin();
            em.persist(producto);
            em.getTransaction().commit();
        } catch (Exception e) {
            System.out.println("Error en insertar producto " + e.getMessage());
            StackTraceElement[] elems = e.getStackTrace();
            for (int i = 0; i < elems.length; i++) {
                System.out.println("ERROR CREAR PRODUCTO COMPRA SRI " + elems[i].toString());
            }

        } finally {
            em.close();
        }

    }

    public void eliminar(Producto producto) {

        try {
            em = HelperPersistencia.getEMF();
            em.getTransaction().begin();
            em.remove(em.merge(producto));
            em.getTransaction().commit();

        } catch (Exception e) {
            System.out.println("Error en eliminar  producto" + e);
        } finally {
            em.close();
        }

    }

    public void modificar(Producto producto) {

        try {
            em = HelperPersistencia.getEMF();
            em.getTransaction().begin();
            em.merge(producto);
            em.getTransaction().commit();
        } catch (Exception e) {
            System.out.println("Error en insertar producto");
        } finally {
            em.close();
        }

    }

    public List<Producto> FindALlProducto(Tipoambiente codTipoambiente) {

        List<Producto> listaProductos = new ArrayList<Producto>();
        try {
            //Connection connection = em.unwrap(Connection.class);
            em = HelperPersistencia.getEMF();
            em.getTransaction().begin();
            Query query = em.createQuery("SELECT p FROM Producto p where p.codTipoambiente=:codTipoambiente ORDER BY p.prodNombre ASC");
           query.setParameter("codTipoambiente", codTipoambiente);
            listaProductos = (List<Producto>) query.getResultList();
            em.getTransaction().commit();
        } catch (Exception e) {
            System.out.println("Error en lsa consulta producto");
            e.printStackTrace();
        } finally {
            em.close();
        }

        return listaProductos;
    }

    public List<Producto> findLikeProdNombre(String buscar, Tipoambiente codTipoambiente) {

        List<Producto> listaProductos = new ArrayList<Producto>();
        try {
            //Connection connection = em.unwrap(Connection.class);
            em = HelperPersistencia.getEMF();
            em.getTransaction().begin();
            Query query = em.createQuery("SELECT p FROM Producto p WHERE p.prodNombre like :prodNombre AND p.codTipoambiente=:codTipoambiente ORDER BY p.prodNombre ASC");
            query.setParameter("prodNombre", "%" + buscar + "%");
            query.setParameter("codTipoambiente", codTipoambiente);
            query.setMaxResults(500);
            listaProductos = (List<Producto>) query.getResultList();
            em.getTransaction().commit();
        } catch (Exception e) {
            System.out.println("Error en lsa consulta producto");
        } finally {
            em.close();
        }

        return listaProductos;
    }

    public Producto findByProdCodigo(String buscar, Tipoambiente codTipoambiente) {

        Producto producto = null;
        try {
            //Connection connection = em.unwrap(Connection.class);
            em = HelperPersistencia.getEMF();
            em.getTransaction().begin();
//            
            Query query = em.createQuery("SELECT p FROM Producto p WHERE p.prodCodigo = :prodCodigo AND p.codTipoambiente=:codTipoambiente  ORDER BY p.prodNombre ASC");
            query.setParameter("prodCodigo", buscar);
            query.setParameter("codTipoambiente", codTipoambiente);
            if (query.getResultList().size() > 0) {
                producto = (Producto) query.getSingleResult();
            }

            em.getTransaction().commit();
        } catch (Exception e) {
            System.out.println("Error en lsa consulta producto");
        } finally {
            em.close();
        }

        return producto;
    }

    public List<Producto> findLikeProdCodigo(String buscar, Tipoambiente codTipoambiente) {

        List<Producto> listaProducto = new ArrayList<Producto>();
        try {
            //Connection connection = em.unwrap(Connection.class);
            em = HelperPersistencia.getEMF();
            em.getTransaction().begin();
            Query query = em.createQuery("SELECT p FROM Producto p WHERE p.prodCodigo like :prodCodigo AND p.codTipoambiente=:codTipoambiente  ORDER BY p.prodNombre ASC");
            query.setMaxResults(200);
            query.setParameter("prodCodigo", buscar);
            query.setParameter("codTipoambiente", codTipoambiente);

            listaProducto = (List<Producto>) query.getResultList();

            em.getTransaction().commit();
        } catch (Exception e) {
            System.out.println("Error en lsa consulta producto");
        } finally {
            em.close();
        }

        return listaProducto;
    }

    public List<ProductoProveedorCosto> findProductoProveedorCosto(String buscar) {

        List<ProductoProveedorCosto> listaProductoProveedor = new ArrayList<ProductoProveedorCosto>();
        try {
            //Connection connection = em.unwrap(Connection.class);
            em = HelperPersistencia.getEMF();
            em.getTransaction().begin();
            Query query = em.createQuery("SELECT NEW com.ec.untilitario.ProductoProveedorCosto(p)FROM Producto p WHERE p.prodNombre like :prodNombre");
            query.setParameter("prodNombre", "%" + buscar + "%");

            listaProductoProveedor = (List<ProductoProveedorCosto>) query.getResultList();

            em.getTransaction().commit();
        } catch (Exception e) {
            System.out.println("Error en al construir la clase producto proveedor " + e);
        } finally {
            em.close();
        }

        return listaProductoProveedor;
    }

    public int findCountPrincipal() {
        int valor = 0;
        List<Producto> listaProductoProveedor = new ArrayList<Producto>();
        try {
            //Connection connection = em.unwrap(Connection.class);
            em = HelperPersistencia.getEMF();
            em.getTransaction().begin();
            Query query = em.createNamedQuery("Producto.findCountPrincipal", Producto.class);
            // query.setParameter("prodPrincipal", "%" + buscar + "%");

            listaProductoProveedor = (List<Producto>) query.getResultList();

            valor = listaProductoProveedor.size();
            em.getTransaction().commit();
        } catch (Exception e) {
            System.out.println("Error en al construir la clase producto proveedor " + e);
        } finally {
            em.close();
        }

        return valor;
    }

    public List<Producto> findProductoPrincipal() {

        List<Producto> listaProductoProveedor = new ArrayList<Producto>();
        try {
            //Connection connection = em.unwrap(Connection.class);
            em = HelperPersistencia.getEMF();
            em.getTransaction().begin();
            Query query = em.createNamedQuery("Producto.findCountPrincipal", Producto.class);
            query.setMaxResults(24);
            // query.setParameter("prodPrincipal", "%" + buscar + "%");

            listaProductoProveedor = (List<Producto>) query.getResultList();
            em.getTransaction().commit();
        } catch (Exception e) {
            System.out.println("Error en al construir la clase producto proveedor " + e);
        } finally {
            em.close();
        }

        return listaProductoProveedor;
    }

    public void actulizarImpresion(List<String> productos) {

        try {
            em = HelperPersistencia.getEMF();
            em.getTransaction().begin();
            Query query = em.createQuery("UPDATE Producto set prodImprimeCodbar =TRUE WHERE prodCodigo IN :codigos");
            query.setParameter("codigos", productos);
            int valor = query.executeUpdate();

            Query query2 = em.createQuery("UPDATE Producto set prodImprimeCodbar =FALSE WHERE prodCodigo NOT IN :codigos");
            query2.setParameter("codigos", productos);
            int valor2 = query2.executeUpdate();

            em.getTransaction().commit();
        } catch (Exception e) {
            System.out.println("Error en insertar producto " + e.getMessage());
        } finally {
            em.close();
        }

    }

    /*PARA PRODUCTOS GLP*/
    public List<Producto> findLikeProdGlp(String buscar) {

        List<Producto> listaProductos = new ArrayList<Producto>();
        try {
            //Connection connection = em.unwrap(Connection.class);
            em = HelperPersistencia.getEMF();
            em.getTransaction().begin();
            Query query = em.createQuery("SELECT p FROM Producto p WHERE p.proGlp like :proGlp ORDER BY p.prodNombre ASC");
            query.setParameter("proGlp", "%" + buscar + "%");
            listaProductos = (List<Producto>) query.getResultList();
            em.getTransaction().commit();
        } catch (Exception e) {
            System.out.println("Error findLikeProdGlp " + e.getMessage());
        } finally {
            em.close();
        }

        return listaProductos;
    }
}
