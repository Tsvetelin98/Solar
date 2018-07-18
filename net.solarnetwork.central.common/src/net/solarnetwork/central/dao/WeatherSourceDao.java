/* ==================================================================
 * WeatherSourceDao.java - Oct 23, 2011 4:55:14 PM
 * 
 * Copyright 2007-2011 SolarNetwork.net Dev Team
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
 * $Id$
 * ==================================================================
 */

package net.solarnetwork.central.dao;

import net.solarnetwork.central.domain.WeatherSource;

/**
 * DAO API for WeatherSource.
 * 
 * @author matt
 * @version $Revision$
 */
public interface WeatherSourceDao extends GenericDao<WeatherSource, Long> {

	/**
	 * Find a unique WeatherSource for a given name.
	 * 
	 * @param name the WeatherSource name
	 * @return the WeatherSource, or <em>null</em> if not found
	 */
	WeatherSource getWeatherSourceForName(String sourceName);

}
