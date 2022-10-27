package Controladores;

import Entidades.AutorPremio;
import Controladores.util.JsfUtil;
import Controladores.util.PaginationHelper;
import Facades.AutorPremioFacade;

import java.io.Serializable;
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

@Named("autorPremioController")
@SessionScoped
public class AutorPremioController implements Serializable {

    private AutorPremio current;
    private DataModel items = null;
    @EJB
    private Facades.AutorPremioFacade ejbFacade;
    private PaginationHelper pagination;
    private int selectedItemIndex;

    public AutorPremioController() {
    }

    public AutorPremio getSelected() {
        if (current == null) {
            current = new AutorPremio();
            current.setAutorPremioPK(new Entidades.AutorPremioPK());
            selectedItemIndex = -1;
        }
        return current;
    }

    private AutorPremioFacade getFacade() {
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

    public String prepareList() {
        recreateModel();
        return "List";
    }

    public String prepareView() {
        current = (AutorPremio) getItems().getRowData();
        selectedItemIndex = pagination.getPageFirstItem() + getItems().getRowIndex();
        return "View";
    }

    public String prepareCreate() {
        current = new AutorPremio();
        current.setAutorPremioPK(new Entidades.AutorPremioPK());
        selectedItemIndex = -1;
        return "Create";
    }

    public String create() {
        try {
            current.getAutorPremioPK().setCodPremio(current.getPremio().getCodPremio());
            current.getAutorPremioPK().setCodAutor(current.getAutor().getCodAutor());
            getFacade().create(current);
            JsfUtil.addSuccessMessage(ResourceBundle.getBundle("/Bundle").getString("AutorPremioCreated"));
            return prepareCreate();
        } catch (Exception e) {
            JsfUtil.addErrorMessage(e, ResourceBundle.getBundle("/Bundle").getString("PersistenceErrorOccured"));
            return null;
        }
    }

    public String prepareEdit() {
        current = (AutorPremio) getItems().getRowData();
        selectedItemIndex = pagination.getPageFirstItem() + getItems().getRowIndex();
        return "Edit";
    }

    public String update() {
        try {
            current.getAutorPremioPK().setCodPremio(current.getPremio().getCodPremio());
            current.getAutorPremioPK().setCodAutor(current.getAutor().getCodAutor());
            getFacade().edit(current);
            JsfUtil.addSuccessMessage(ResourceBundle.getBundle("/Bundle").getString("AutorPremioUpdated"));
            return "View";
        } catch (Exception e) {
            JsfUtil.addErrorMessage(e, ResourceBundle.getBundle("/Bundle").getString("PersistenceErrorOccured"));
            return null;
        }
    }

    public String destroy() {
        current = (AutorPremio) getItems().getRowData();
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
            JsfUtil.addSuccessMessage(ResourceBundle.getBundle("/Bundle").getString("AutorPremioDeleted"));
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

    public AutorPremio getAutorPremio(Entidades.AutorPremioPK id) {
        return ejbFacade.find(id);
    }

    @FacesConverter(forClass = AutorPremio.class)
    public static class AutorPremioControllerConverter implements Converter {

        private static final String SEPARATOR = "#";
        private static final String SEPARATOR_ESCAPED = "\\#";

        @Override
        public Object getAsObject(FacesContext facesContext, UIComponent component, String value) {
            if (value == null || value.length() == 0) {
                return null;
            }
            AutorPremioController controller = (AutorPremioController) facesContext.getApplication().getELResolver().
                    getValue(facesContext.getELContext(), null, "autorPremioController");
            return controller.getAutorPremio(getKey(value));
        }

        Entidades.AutorPremioPK getKey(String value) {
            Entidades.AutorPremioPK key;
            String values[] = value.split(SEPARATOR_ESCAPED);
            key = new Entidades.AutorPremioPK();
            key.setCodAutor(Integer.parseInt(values[0]));
            key.setCodPremio(Integer.parseInt(values[1]));
            key.setAnio(Integer.parseInt(values[2]));
            return key;
        }

        String getStringKey(Entidades.AutorPremioPK value) {
            StringBuilder sb = new StringBuilder();
            sb.append(value.getCodAutor());
            sb.append(SEPARATOR);
            sb.append(value.getCodPremio());
            sb.append(SEPARATOR);
            sb.append(value.getAnio());
            return sb.toString();
        }

        @Override
        public String getAsString(FacesContext facesContext, UIComponent component, Object object) {
            if (object == null) {
                return null;
            }
            if (object instanceof AutorPremio) {
                AutorPremio o = (AutorPremio) object;
                return getStringKey(o.getAutorPremioPK());
            } else {
                throw new IllegalArgumentException("object " + object + " is of type " + object.getClass().getName() + "; expected type: " + AutorPremio.class.getName());
            }
        }

    }

}
