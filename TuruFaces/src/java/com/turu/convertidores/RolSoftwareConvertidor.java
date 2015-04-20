/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.turu.convertidores;

import com.turu.entidad.RolSoftware;
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
@FacesConverter("rolConverter")
public class RolSoftwareConvertidor implements Converter{
    
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
                string = String.valueOf(((RolSoftware)value).getIdRol());
            }catch(ClassCastException cce){
                throw new ConverterException();
            }
        }
        return string;
    }
    @SuppressWarnings("unchecked")
    private RolSoftware getObjectFromUIPickListComponent(UIComponent component, String value) {
        final DualListModel<RolSoftware> dualList;
        try{
            dualList = (DualListModel<RolSoftware>) ((PickList)component).getValue();
            RolSoftware lin = getObjectFromList(dualList.getSource(),Integer.valueOf(value));
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
    private RolSoftware getObjectFromList(final List<?> list, final Integer identifier) {
        for(final Object object:list){
            final RolSoftware lin = (RolSoftware) object;
            if(lin.getIdRol().equals(identifier)){
                return lin;
            }
        }
        return null;
    }
}
