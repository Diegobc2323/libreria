package Controladores;

import Entidades.Premio;
import Controladores.util.JsfUtil;
import Controladores.util.PaginationHelper;
import Facades.PremioFacade;

import java.io.Serializable;
import java.util.List;
import java.util.ResourceBundle;
import javax.ejb.EJB;
import javax.inject.Named;
import javax.enterprise.context.SessionScoped;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;
import javax.faces.model.DataModel;
import javax.faces.model.ListDataModel;
import javax.faces.model.SelectItem;

@Named("premioController")
@SessionScoped
public class PremioController implements Serializable {

    private Premio current;
    private DataModel items = null;
    @EJB
    private Facades.PremioFacade ejbFacade;
    private PaginationHelper pagination;
    private int selectedItemIndex;
    private Premio paraCrear;

    public PremioController() {
    
            paraCrear = new Premio();
    
    }

    public Premio getParaCrear() {
        return paraCrear;
    }

    public void setParaCrear(Premio paraCrear) {
        this.paraCrear = paraCrear;
    }
    
    public Premio getSelected() {
        if (current == null) {
            current = new Premio();
            selectedItemIndex = -1;
        }
        return current;
    }

    private PremioFacade getFacade() {
        return ejbFacade;
    }

    public PaginationHelper getPagination() {
        if (pagination == null) {
            pagination = new PaginationHelper(100) {

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
        current = (Premio) getItems().getRowData();
        selectedItemIndex = pagination.getPageFirstItem() + getItems().getRowIndex();
        return "View";
    }

    public String prepareCreate() {
        current = new Premio();
        selectedItemIndex = -1;
        return "Create";
    }

    public String create() {
        try {
            getFacade().create(paraCrear);
            JsfUtil.addSuccessMessage(ResourceBundle.getBundle("/Bundle").getString("PremioCreated"));
            //return prepareCreate();
            recreateModel();
            recreatePagination();
            paraCrear = new Premio();
            return "List_edicion";
        } catch (Exception e) {
            JsfUtil.addErrorMessage(e, ResourceBundle.getBundle("/Bundle").getString("PersistenceErrorOccured"));
            return null;
        }
    }

    public String prepareEdit() {
        current = (Premio) getItems().getRowData();
        selectedItemIndex = pagination.getPageFirstItem() + getItems().getRowIndex();
        return "Edit";
    }

    public String update() {
        try {
            getFacade().edit(current);
            JsfUtil.addSuccessMessage(ResourceBundle.getBundle("/Bundle").getString("PremioUpdated"));
            return "View";
        } catch (Exception e) {
            JsfUtil.addErrorMessage(e, ResourceBundle.getBundle("/Bundle").getString("PersistenceErrorOccured"));
            return null;
        }
    }

    public String destroy() {
        current = (Premio) getItems().getRowData();
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
            JsfUtil.addSuccessMessage(ResourceBundle.getBundle("/Bundle").getString("PremioDeleted"));
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
        return getSelectPremiosL(ejbFacade.findPremiosL(), true);
    }
    
    public SelectItem[] getItemsPremiosA() {
        return getSelectPremiosA(ejbFacade.findPremiosA(), true);
    }

    public Premio getPremio(java.lang.Integer id) {
        return ejbFacade.find(id);
    }
    
    public static SelectItem[] getSelectPremiosL(List<Premio> entities, boolean selectOne) {
        int size = entities.size();
        SelectItem[] items = new SelectItem[size];
        int i = 0;
        for (Premio premio : entities) {
            items[i++] = new SelectItem(premio, premio.getNomPremio());
        }
        return items;
    }
    
    public static SelectItem[] getSelectPremiosA(List<Premio> entities, boolean selectOne) {
        int size = entities.size();
        SelectItem[] items = new SelectItem[size];
        int i = 0;
        for (Premio premio : entities) {
            items[i++] = new SelectItem(premio, premio.getNomPremio());
        }
        return items;
    }

    @FacesConverter(forClass = Premio.class)
    public static class PremioControllerConverter implements Converter {

        @Override
        public Object getAsObject(FacesContext facesContext, UIComponent component, String value) {
            if (value == null || value.length() == 0) {
                return null;
            }
            PremioController controller = (PremioController) facesContext.getApplication().getELResolver().
                    getValue(facesContext.getELContext(), null, "premioController");
            return controller.getPremio(getKey(value));
        }

        java.lang.Integer getKey(String value) {
            java.lang.Integer key;
            key = Integer.valueOf(value);
            return key;
        }

        String getStringKey(java.lang.Integer value) {
            StringBuilder sb = new StringBuilder();
            sb.append(value);
            return sb.toString();
        }

        @Override
        public String getAsString(FacesContext facesContext, UIComponent component, Object object) {
            if (object == null) {
                return null;
            }
            if (object instanceof Premio) {
                Premio o = (Premio) object;
                return getStringKey(o.getCodPremio());
            } else {
                throw new IllegalArgumentException("object " + object + " is of type " + object.getClass().getName() + "; expected type: " + Premio.class.getName());
            }
        }

    }

}
