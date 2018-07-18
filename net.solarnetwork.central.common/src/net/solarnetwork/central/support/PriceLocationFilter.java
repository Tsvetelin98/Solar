/* ==================================================================
 * PriceLocationFilter.java - Nov 19, 2013 6:18:55 AM
 * 
 * Copyright 2007-2013 SolarNetwork.net Dev Team
 * 
 * This program is free software; you can redistribute it and/or 
 * modify it under the terms of the GNU General Public License as 
 * published by the Free Software Foundation; either version 2 of 
 * the License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful, 
 * but WITHOUT ANY WARRANTY; without even the implied warranty of 
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU 
 * General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License 
 * along with this program; if not, write to the Free Software 
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 
 * 02111-1307 USA
 * ==================================================================
 */

package net.solarnetwork.central.support;

import java.util.LinkedHashMap;
import java.util.Map;
import net.solarnetwork.central.domain.Location;
import net.solarnetwork.central.domain.PriceLocation;
import net.solarnetwork.central.domain.SolarLocation;
import net.solarnetwork.central.domain.SourceLocation;
import org.springframework.util.StringUtils;

/**
 * Filter for {@link PriceLocation}.
 * 
 * @author matt
 * @version 1.1
 */
public class PriceLocationFilter extends SourceLocationFilter {

	private static final long serialVersionUID = 8489957378330089844L;

	private String currency;

	/**
	 * Default constructor.
	 */
	public PriceLocationFilter() {
		super();
	}

	/**
	 * Construct with values.
	 * 
	 * @param source
	 *        the source name
	 * @param locationName
	 *        the location name
	 */
	public PriceLocationFilter(String source, String locationName) {
		super(source, locationName);
	}

	/**
	 * Copy constructor for a {@link SourceLocation}.
	 * 
	 * @param sourceLocation
	 *        the object to copy
	 */
	public PriceLocationFilter(SourceLocation sourceLocation) {
		super();
		setId(sourceLocation.getId());
		setSource(sourceLocation.getSource());
		Location loc = sourceLocation.getLocation();
		if ( loc instanceof SolarLocation ) {
			setLocation((SolarLocation) loc);
		} else {
			setLocation(new SolarLocation(sourceLocation.getLocation()));
		}
	}

	/**
	 * Change values that are non-null but empty to null.
	 * 
	 * <p>
	 * This method is helpful for web form submission, to remove filter values
	 * that are empty and would otherwise try to match on empty string values.
	 * </p>
	 */
	@Override
	public void removeEmptyValues() {
		super.removeEmptyValues();
		if ( !StringUtils.hasText(currency) ) {
			currency = null;
		}
	}

	@Override
	public Map<String, ?> getFilter() {
		Map<String, ?> filter = super.getFilter();
		if ( currency != null ) {
			Map<String, Object> f = new LinkedHashMap<String, Object>(filter);
			if ( currency != null ) {
				f.put("currency", currency);
			}
			filter = f;
		}
		return filter;
	}

	public String getCurrency() {
		return currency;
	}

	public void setCurrency(String currency) {
		this.currency = currency;
	}

}
