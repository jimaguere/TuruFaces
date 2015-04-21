package com.turu.controlador;

import com.turu.entidad.RolSoftware;
import com.turu.controlador.util.JsfUtil;
import com.turu.controlador.util.PaginationHelper;
import com.turu.dao.MenuFacade;
import com.turu.dao.RolSoftwareFacade;
import com.turu.entidad.Menu;
import com.turu.entidad.RolSoftMenu;

import java.io.Serializable;
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

@ManagedBean(name = "rolSoftwareController")
@SessionScoped
public class RolSoftwareController implements Serializable {

    private RolSoftware current;
    private DataModel items = null;
    @EJB
    private com.turu.dao.RolSoftwareFacade ejbFacade;
    @EJB
    private MenuFacade menuEjbFacade;
    private PaginationHelper pagination;
    private int selectedItemIndex;
    private boolean insertar;
    private DualListModel<Menu> menus;

    public RolSoftwareController() {
    }

    public RolSoftware getSelected() {
        if (current == null) {
            current = new RolSoftware();
            selectedItemIndex = -1;
        }
        return current;
    }

    public boolean isInsertar() {
        return insertar;
    }

    public void setInsertar(boolean insertar) {
        this.insertar = insertar;
    }

    private RolSoftwareFacade getFacade() {
        return ejbFacade;
    }

    public MenuFacade getMenuEjbFacade() {
        return menuEjbFacade;
    }

    public void setMenuEjbFacade(MenuFacade menuEjbFacade) {
        this.menuEjbFacade = menuEjbFacade;
    }

    public DualListModel<Menu> getMenus() {
        return menus;
    }

    public void setMenus(DualListModel<Menu> menus) {
        this.menus = menus;
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

    public String prepareList() {
        recreateModel();
        return "List";
    }

    public String prepareView() {
        current = (RolSoftware) getItems().getRowData();
        selectedItemIndex = pagination.getPageFirstItem() + getItems().getRowIndex();
        return "View";
    }

    public String prepareCreate() {
        cargarPermisos();
        insertar=true;
        current = new RolSoftware();
        selectedItemIndex = -1;
        return "Create";
    }
    
    public void setearPermisos(){
        List<RolSoftMenu> listaRolMenu=new ArrayList<RolSoftMenu>();
        for(Menu menu:menus.getTarget()){
            RolSoftMenu rolMenu=new RolSoftMenu();
            rolMenu.setIdMenu(menu);
            rolMenu.setIdRol(current);
            listaRolMenu.add(rolMenu);
        }
        current.setRolSoftMenuList(listaRolMenu);
    }

    public String create() {
        try {
            setearPermisos();
            getFacade().create(current);
            JsfUtil.addSuccessMessage(ResourceBundle.getBundle("/Bundle").getString("RolSoftwareCreated"));
            return prepareList();
        } catch (Exception e) {
            JsfUtil.addErrorMessage(e, ResourceBundle.getBundle("/Bundle").getString("PersistenceErrorOccured"));
            return null;
        }
    }

    public String prepareEdit() {
        insertar=false;
        current = (RolSoftware) getItems().getRowData();
        selectedItemIndex = pagination.getPageFirstItem() + getItems().getRowIndex();
        cargarPermisosEditar();
        return "Edit";
    }

    public String update() {
        try {
            setearPermisos();
            getFacade().editarRolSoftware(current);
            JsfUtil.addSuccessMessage(ResourceBundle.getBundle("/Bundle").getString("RolSoftwareUpdated"));
            return prepareList();
        } catch (Exception e) {
            JsfUtil.addErrorMessage(e, ResourceBundle.getBundle("/Bundle").getString("PersistenceErrorOccured"));
            return null;
        }
    }

    public String destroy() {
        current = (RolSoftware) getItems().getRowData();
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
            JsfUtil.addSuccessMessage(ResourceBundle.getBundle("/Bundle").getString("RolSoftwareDeleted"));
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

    @FacesConverter(forClass = RolSoftware.class)
    public static class RolSoftwareControllerConverter implements Converter {

        public Object getAsObject(FacesContext facesContext, UIComponent component, String value) {
            if (value == null || value.length() == 0) {
                return null;
            }
            RolSoftwareController controller = (RolSoftwareController) facesContext.getApplication().getELResolver().
                    getValue(facesContext.getELContext(), null, "rolSoftwareController");
            return controller.ejbFacade.find(getKey(value));
        }

        java.lang.Integer getKey(String value) {
            java.lang.Integer key;
            key = Integer.valueOf(value);
            return key;
        }

        String getStringKey(java.lang.Integer value) {
            StringBuffer sb = new StringBuffer();
            sb.append(value);
            return sb.toString();
        }

        public String getAsString(FacesContext facesContext, UIComponent component, Object object) {
            if (object == null) {
                return null;
            }
            if (object instanceof RolSoftware) {
                RolSoftware o = (RolSoftware) object;
                return getStringKey(o.getIdRol());
            } else {
                throw new IllegalArgumentException("object " + object + " is of type " + object.getClass().getName() + "; expected type: " + RolSoftware.class.getName());
            }
        }
    }
    
    public void cargarPermisos(){
        List<Menu> source=this.menuEjbFacade.findAll();
        List<Menu> sourceAux=new ArrayList<Menu>();
        List<Menu> target=new ArrayList<Menu>();
        for(Menu menu1:source){
            if(menu1.getParentIdMenu()==null){
                sourceAux.add(menu1);
            }
        }
        this.menus=new DualListModel<Menu>(sourceAux, target);    
    }
    
    public void cargarPermisosEditar(){
        List<Menu> source=getMenuEjbFacade().findAll();
        List<Menu> sourceAux=new ArrayList<Menu>();
        List<Menu> target=new ArrayList<Menu>();
        for(Menu menu:source){
            if(menu.getParentIdMenu()==null){
                sourceAux.add(menu);
            }
        }
        for(RolSoftMenu menuRol:current.getRolSoftMenuList()){
            target.add(menuRol.getIdMenu());
        }
        sourceAux.removeAll(target);
        this.menus=new DualListModel<Menu>(sourceAux, target);   
    }
    
    @PostConstruct
    public void inicar(){
        cargarPermisos();
    }
}
