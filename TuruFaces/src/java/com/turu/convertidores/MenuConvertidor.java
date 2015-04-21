/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.turu.convertidores;

import com.turu.entidad.Menu;
import java.util.List;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.ConverterException;
import javax.faces.convert.FacesConverter;
import org.primefaces.component.picklist.PickList;
import org.primefaces.model.DualListModel;

/**
 *
 * @author mateo
 */
@FacesConverter("menuConverter")
public class MenuConvertidor implements Converter{
    @Override
    public Object getAsObject(FacesContext context, UIComponent component, String value) {
        return getObjectFromUIPickListComponent(component,value);
    }

    @Override
    public String getAsString(FacesContext context, UIComponent component, Object value) {
         String string;

        if(value == null){
            string="";
        }else{
            try{
                string = String.valueOf(((Menu)value).getIdMenu());
            }catch(ClassCastException cce){
                throw new ConverterException();
            }
        }
        return string;
    }
    @SuppressWarnings("unchecked")
    private Menu getObjectFromUIPickListComponent(UIComponent component, String value) {
        final DualListModel<Menu> dualList;
        try{
            dualList = (DualListModel<Menu>) ((PickList)component).getValue();
            Menu lin = getObjectFromList(dualList.getSource(),Integer.valueOf(value));
            if(lin==null){
                lin = getObjectFromList(dualList.getTarget(),Integer.valueOf(value));
            }
             
            return lin;
        }catch(ClassCastException cce){
            throw new ConverterException();
        }catch(NumberFormatException nfe){
            throw new ConverterException();
        }
    }
    private Menu getObjectFromList(final List<?> list, final Integer identifier) {
        for(final Object object:list){
            final Menu lin = (Menu) object;
            if(lin.getIdMenu().equals(identifier)){
                return lin;
            }
        }
        return null;
    }
}
