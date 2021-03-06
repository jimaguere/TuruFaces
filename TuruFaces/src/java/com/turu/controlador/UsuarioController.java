package com.turu.controlador;

import com.turu.controlador.util.JsfUtil;
import com.turu.controlador.util.PaginationHelper;
import com.turu.dao.RolSoftwareFacade;
import com.turu.dao.UsuarioFacade;
import com.turu.entidad.RolSoftware;
import com.turu.entidad.Usuario;
import com.turu.entidad.UsuarioRolSoftware;

import java.io.Serializable;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;
import javax.faces.model.DataModel;
import javax.faces.model.ListDataModel;
import javax.faces.model.SelectItem;
import org.primefaces.model.DualListModel;

@ManagedBean(name = "usuarioController")
@SessionScoped
public class UsuarioController implements Serializable {

    private Usuario current;
    private DataModel items = null;
    @EJB
    private UsuarioFacade ejbFacade;
    @EJB
    private RolSoftwareFacade rolEjbFacade;
    private PaginationHelper pagination;
    private int selectedItemIndex;
    boolean insertar;
    private DualListModel<RolSoftware> roles;

    public UsuarioController() {
    }

    public Usuario getSelected() {
        if (current == null) {
            current = new Usuario();
            selectedItemIndex = -1;
        }
        return current;
    }

    private UsuarioFacade getFacade() {
        return ejbFacade;
    }

    public PaginationHelper getPagination() {
        if (pagination == null) {
            pagination = new PaginationHelper(10) {
                @Override
                public int getItemsCount() {
                    return getFacade().count();
                }

                @Override
                public DataModel createPageDataModel() {
                    return new ListDataModel(getFacade().findRange(new int[]{getPageFirstItem(), getPageFirstItem() + getPageSize()}));
                }
            };
        }
        return pagination;
    }

    public boolean isInsertar() {
        return insertar;
    }

    public void setInsertar(boolean insertar) {
        this.insertar = insertar;
    }

    public DualListModel<RolSoftware> getRoles() {
        return roles;
    }

    public void setRoles(DualListModel<RolSoftware> roles) {
        this.roles = roles;
    }

    public String prepareList() {
        recreateModel();
        return "List";
    }

    public String prepareView() {
        current = (Usuario) getItems().getRowData();
        selectedItemIndex = pagination.getPageFirstItem() + getItems().getRowIndex();
        return "View";
    }

    public String prepareCreate() {
        cargarRoles();
        this.insertar = true;
        current = new Usuario();
        selectedItemIndex = -1;
        return "Create";
    }

    public void setearUsuariosRolSoftware() throws Exception {
        List<UsuarioRolSoftware> rolesUsuario = new ArrayList<UsuarioRolSoftware>();
        List<RolSoftware> rolesTarget = this.roles.getTarget();
        for (RolSoftware rolSoftware : rolesTarget) {
            UsuarioRolSoftware usuarioRol = new UsuarioRolSoftware();
            usuarioRol.setIdRol(rolSoftware);
            usuarioRol.setUsuario(current);
            rolesUsuario.add(usuarioRol);
        }
        current.setClave(md5(current.getClave()));
        current.setUsuarioRolSoftwareList(rolesUsuario);
    }

    public String create() {
        try {
            setearUsuariosRolSoftware();
            getFacade().create(current);
            JsfUtil.addSuccessMessage(ResourceBundle.getBundle("/Bundle").getString("UsuarioCreated"));
            return prepareList();
        } catch (Exception e) {
            System.out.println("error:" + e.toString());
            JsfUtil.addErrorMessage(e, ResourceBundle.getBundle("/Bundle").getString("PersistenceErrorOccured"));
            return null;
        }
    }

    public String prepareEdit() {
        this.insertar = false;
        current = (Usuario) getItems().getRowData();
        selectedItemIndex = pagination.getPageFirstItem() + getItems().getRowIndex();
        cargarRolesEditar();
        return prepareList();
    }

    public String update() {
        try {
            setearUsuariosRolSoftware();
            getFacade().editar(current);
            JsfUtil.addSuccessMessage(ResourceBundle.getBundle("/Bundle").getString("UsuarioUpdated"));
            return prepareList();
        } catch (Exception e) {
            JsfUtil.addErrorMessage(e, ResourceBundle.getBundle("/Bundle").getString("PersistenceErrorOccured"));
            return null;
        }
    }

    public String destroy() {
        current = (Usuario) getItems().getRowData();
        selectedItemIndex = pagination.getPageFirstItem() + getItems().getRowIndex();
        performDestroy();
        recreatePagination();
        recreateModel();
        return "List";
    }

    public String destroyAndView() {
        performDestroy();
        recreateModel();
        updateCurrentItem();
        if (selectedItemIndex >= 0) {
            return "View";
        } else {
            // all items were removed - go back to list
            recreateModel();
            return "List";
        }
    }

    private void performDestroy() {
        try {
            getFacade().remove(current);
            JsfUtil.addSuccessMessage(ResourceBundle.getBundle("/Bundle").getString("UsuarioDeleted"));
        } catch (Exception e) {
            JsfUtil.addErrorMessage(e, ResourceBundle.getBundle("/Bundle").getString("PersistenceErrorOccured"));
        }
    }

    private void updateCurrentItem() {
        int count = getFacade().count();
        if (selectedItemIndex >= count) {
            // selected index cannot be bigger than number of items:
            selectedItemIndex = count - 1;
            // go to previous page if last page disappeared:
            if (pagination.getPageFirstItem() >= count) {
                pagination.previousPage();
            }
        }
        if (selectedItemIndex >= 0) {
            current = getFacade().findRange(new int[]{selectedItemIndex, selectedItemIndex + 1}).get(0);
        }
    }

    public DataModel getItems() {
        if (items == null) {
            items = getPagination().createPageDataModel();
        }
        return items;
    }

    private void recreateModel() {
        items = null;
    }

    private void recreatePagination() {
        pagination = null;
    }

    public String next() {
        getPagination().nextPage();
        recreateModel();
        return "List";
    }

    public String previous() {
        getPagination().previousPage();
        recreateModel();
        return "List";
    }

    public SelectItem[] getItemsAvailableSelectMany() {
        return JsfUtil.getSelectItems(ejbFacade.findAll(), false);
    }

    public SelectItem[] getItemsAvailableSelectOne() {
        return JsfUtil.getSelectItems(ejbFacade.findAll(), true);
    }

    @FacesConverter(forClass = Usuario.class)
    public static class UsuarioControllerConverter implements Converter {

        public Object getAsObject(FacesContext facesContext, UIComponent component, String value) {
            if (value == null || value.length() == 0) {
                return null;
            }
            UsuarioController controller = (UsuarioController) facesContext.getApplication().getELResolver().
                    getValue(facesContext.getELContext(), null, "usuarioController");
            return controller.ejbFacade.find(getKey(value));
        }

        java.lang.String getKey(String value) {
            java.lang.String key;
            key = value;
            return key;
        }

        String getStringKey(java.lang.String value) {
            StringBuffer sb = new StringBuffer();
            sb.append(value);
            return sb.toString();
        }

        public String getAsString(FacesContext facesContext, UIComponent component, Object object) {
            if (object == null) {
                return null;
            }
            if (object instanceof Usuario) {
                Usuario o = (Usuario) object;
                return getStringKey(o.getUsuario());
            } else {
                throw new IllegalArgumentException("object " + object + " is of type " + object.getClass().getName() + "; expected type: " + Usuario.class.getName());
            }
        }
    }

    public void cargarRoles() {
        List<RolSoftware> lineasSource = rolEjbFacade.findAll();
        List<RolSoftware> lineasTarget = new ArrayList<RolSoftware>();
        this.roles = new DualListModel<RolSoftware>(lineasSource, lineasTarget);
    }

    public void cargarRolesEditar() {
        List<RolSoftware> lineasSource = rolEjbFacade.findAll();
        List<RolSoftware> lineasTarget = new ArrayList<RolSoftware>();
        for (UsuarioRolSoftware usuarioRol : current.getUsuarioRolSoftwareList()) {
            lineasTarget.add(usuarioRol.getIdRol());
        }
        lineasSource.removeAll(lineasTarget);
        this.roles = new DualListModel<RolSoftware>(lineasSource, lineasTarget);
    }

    @PostConstruct
    public void init() {
        cargarRoles();
        // this.roles = new DualListModel<RolSoftware>(new ArrayList<RolSoftware>(), new ArrayList<RolSoftware>());
    }

    private static String md5(String clear) throws Exception {
        MessageDigest md = MessageDigest.getInstance("MD5");
        byte[] b = md.digest(clear.getBytes());
        int size = b.length;
        StringBuffer h = new StringBuffer(size);
        for (int i = 0; i < size; i++) {
            int u = b[i] & 255;
            if (u < 16) {
                h.append("0" + Integer.toHexString(u));
            } else {
                h.append(Integer.toHexString(u));
            }
        }

        return h.toString();
    }
}
