/* ==================================================================
 * MyBatisPriceLocationDao.java - Nov 10, 2014 1:21:11 PM
 * 
 * Copyright 2007-2014 SolarNetwork.net Dev Team
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

package net.solarnetwork.central.dao.mybatis;

import java.util.HashMap;
import java.util.Map;
import net.solarnetwork.central.dao.PriceLocationDao;
import net.solarnetwork.central.dao.mybatis.support.BaseMyBatisFilterableDao;
import net.solarnetwork.central.domain.Location;
import net.solarnetwork.central.domain.PriceLocation;
import net.solarnetwork.central.domain.SourceLocation;
import net.solarnetwork.central.domain.SourceLocationMatch;
import net.solarnetwork.central.support.PriceLocationFilter;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

/**
 * MyBatis implementation of {@link PriceLocationDao}.
 * 
 * @author matt
 * @version 1.0
 */
public class MyBatisPriceLocationDao extends
		BaseMyBatisFilterableDao<PriceLocation, SourceLocationMatch, SourceLocation, Long>
		implements PriceLocationDao {

	/** The query name used for {@link #getPriceLocationForName(String,String)}. */
	public static final String QUERY_FOR_NAME = "get-PriceLocation-for-name";

	/**
	 * Default constructor.
	 */
	public MyBatisPriceLocationDao() {
		super(PriceLocation.class, Long.class, SourceLocationMatch.class);
	}

	@Override
	@Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
	public PriceLocation getPriceLocationForName(String sourceName, String locationName) {
		Map<String, Object> params = new HashMap<String, Object>(2);
		params.put("locationName", locationName);
		params.put("sourceName", sourceName);
		return selectFirst(QUERY_FOR_NAME, params);
	}

	@Override
	protected void postProcessFilterProperties(SourceLocation filter, Map<String, Object> sqlProps) {
		PriceLocationFilter pFilter;
		if ( filter instanceof PriceLocationFilter ) {
			pFilter = (PriceLocationFilter) filter;
		} else {
			// mapping expects a PriceLocationFilter, so replace the input with one of those
			pFilter = new PriceLocationFilter(filter);
			sqlProps.put(FILTER_PROPERTY, pFilter);
		}
		StringBuilder fts = new StringBuilder();
		spaceAppend(pFilter.getCurrency(), fts);
		if ( filter.getLocation() != null ) {
			Location loc = filter.getLocation();
			spaceAppend(loc.getName(), fts);
			spaceAppend(loc.getCountry(), fts);
			spaceAppend(loc.getRegion(), fts);
			spaceAppend(loc.getStateOrProvince(), fts);
			spaceAppend(loc.getLocality(), fts);
			spaceAppend(loc.getPostalCode(), fts);
		}
		if ( fts.length() > 0 ) {
			sqlProps.put("fts", fts.toString());
		}
	}

}
