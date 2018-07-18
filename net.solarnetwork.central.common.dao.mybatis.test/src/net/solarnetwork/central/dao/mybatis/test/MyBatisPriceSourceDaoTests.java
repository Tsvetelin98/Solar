/* ==================================================================
 * MyBatisPriceSourceDaoTests.java - Nov 10, 2014 1:34:42 PM
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

package net.solarnetwork.central.dao.mybatis.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import net.solarnetwork.central.dao.mybatis.MyBatisPriceSourceDao;
import net.solarnetwork.central.domain.EntityMatch;
import net.solarnetwork.central.domain.FilterResults;
import net.solarnetwork.central.domain.PriceSource;
import net.solarnetwork.central.support.SourceLocationFilter;
import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;

/**
 * Test cases for the {@link MyBatisPriceSourceDao} class.
 * 
 * @author matt
 * @version 1.0
 */
public class MyBatisPriceSourceDaoTests extends AbstractMyBatisDaoTestSupport {

	private MyBatisPriceSourceDao dao;

	private PriceSource priceSource = null;

	@Before
	public void setup() {
		dao = new MyBatisPriceSourceDao();
		dao.setSqlSessionFactory(getSqlSessionFactory());
	}

	@Test
	public void storeNew() {
		PriceSource d = new PriceSource();
		d.setCreated(new DateTime());
		d.setName("Test name");
		Long id = dao.store(d);
		assertNotNull(id);
		d.setId(id);
		priceSource = d;
	}

	private void validate(PriceSource src, PriceSource entity) {
		assertNotNull("PriceSource should exist", entity);
		assertNotNull("Created date should be set", entity.getCreated());
		assertEquals(src.getId(), entity.getId());
		assertEquals(src.getName(), entity.getName());
	}

	@Test
	public void getByPrimaryKey() {
		storeNew();
		PriceSource d = dao.get(priceSource.getId());
		validate(priceSource, d);
	}

	@Test
	public void update() {
		storeNew();
		PriceSource d = dao.get(priceSource.getId());
		d.setName("new name");
		Long newId = dao.store(d);
		assertEquals(d.getId(), newId);
		PriceSource d2 = dao.get(priceSource.getId());
		validate(d, d2);
	}

	@Test
	public void findByName() {
		storeNew();
		PriceSource s = dao.getPriceSourceForName("Test name");
		assertNotNull(s);
		validate(priceSource, s);
	}

	@Test
	public void searchByName() {
		storeNew();
		SourceLocationFilter filter = new SourceLocationFilter("Test name", null);
		FilterResults<EntityMatch> matches = dao.findFiltered(filter, null, null, null);
		assertNotNull(matches);
		assertEquals(Integer.valueOf(1), matches.getReturnedResultCount());
		assertNotNull(matches.getResults());
		EntityMatch m = matches.getResults().iterator().next();
		assertEquals(priceSource.getId(), m.getId());
	}

}
