package de.symeda.sormas.ui.surveillance.caze;

import java.util.List;

import com.vaadin.data.util.BeanItem;
import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.data.util.MethodProperty;
import com.vaadin.data.util.filter.Compare.Equal;
import com.vaadin.data.util.filter.Or;
import com.vaadin.data.util.filter.SimpleStringFilter;
import com.vaadin.ui.Grid;

import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.I18nProperties;
import de.symeda.sormas.api.caze.CaseDataDto;
import de.symeda.sormas.api.caze.CaseStatus;
import de.symeda.sormas.ui.surveillance.ControllerProvider;
import de.symeda.sormas.ui.utils.UuidRenderer;

@SuppressWarnings("serial")
public class CaseGrid extends Grid {

	public CaseGrid() {
        setSizeFull();

        setSelectionMode(SelectionMode.NONE);

        BeanItemContainer<CaseDataDto> container = new BeanItemContainer<CaseDataDto>(CaseDataDto.class);
        setContainerDataSource(container);
        setColumns(CaseDataDto.UUID, CaseDataDto.DISEASE, CaseDataDto.CASE_STATUS, CaseDataDto.PERSON, 
        		CaseDataDto.HEALTH_FACILITY, CaseDataDto.REPORTING_USER, CaseDataDto.REPORT_DATE, 
        		CaseDataDto.SURVEILLANCE_OFFICER, CaseDataDto.INVESTIGATED_DATE);

        getColumn(CaseDataDto.UUID).setRenderer(new UuidRenderer());
        
        for (Column column : getColumns()) {
        	column.setHeaderCaption(I18nProperties.getFieldCaption(
        			CaseDataDto.I18N_PREFIX, column.getPropertyId().toString(), column.getHeaderCaption()));
        }
        
        addItemClickListener(e -> ControllerProvider.getCaseController().editData(
        		((CaseDataDto)e.getItemId()).getUuid()));
        
        reload();
	}
	
    /**
     * Filter the grid based on a search string that is searched for in the
     * product name, availability and category columns.
     *
     * @param filterString
     *            string to look for
     */
    public void setFilter(String filterString) {
    	getContainer().removeContainerFilters(CaseDataDto.PERSON);
        if (filterString.length() > 0) {
            SimpleStringFilter nameFilter = new SimpleStringFilter(CaseDataDto.PERSON, filterString, true, false);
            getContainer().addContainerFilter(
//            new Or(nameFilter, descFilter, statusFilter));
            new Or(nameFilter));
        }

    }
    
    public void setFilter(Disease disease) {
		getContainer().removeContainerFilters(CaseDataDto.DISEASE);
		if (disease != null) {
	    	Equal filter = new Equal(CaseDataDto.DISEASE, disease);  
	        getContainer().addContainerFilter(filter);
		}
	}

	public void setFilter(CaseStatus statusToFilter) {
		// TODO use query instead of filter?
    	removeAllStatusFilter();
    	if (statusToFilter != null) {
	    	Equal filter = new Equal(CaseDataDto.CASE_STATUS, statusToFilter);  
	        getContainer().addContainerFilter(filter);
    	}
    }
    
    public void removeAllStatusFilter() {
    	reload();
    	getContainer().removeContainerFilters(CaseDataDto.CASE_STATUS);
    }

    @SuppressWarnings("unchecked")
	private BeanItemContainer<CaseDataDto> getContainer() {
        return (BeanItemContainer<CaseDataDto>) super.getContainerDataSource();
    }
    
    public void reload() {
    	List<CaseDataDto> cases = ControllerProvider.getCaseController().getAllCaseData();
        getContainer().removeAllItems();
        getContainer().addAll(cases);    	
    }

    public void refresh(CaseDataDto caze) {
        // We avoid updating the whole table through the backend here so we can
        // get a partial update for the grid
        BeanItem<CaseDataDto> item = getContainer().getItem(caze);
        if (item != null) {
            // Updated product
            @SuppressWarnings("rawtypes")
			MethodProperty p = (MethodProperty) item.getItemProperty(CaseDataDto.UUID);
            p.fireValueChange();
        } else {
            // New product
            getContainer().addBean(caze);
        }
    }

    public void remove(CaseDataDto caze) {
        getContainer().removeItem(caze);
    }
}


