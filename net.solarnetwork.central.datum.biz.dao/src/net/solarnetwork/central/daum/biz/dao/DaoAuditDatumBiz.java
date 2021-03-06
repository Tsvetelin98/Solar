/* ==================================================================
 * DaoAuditDatumBiz.java - 12/07/2018 5:25:10 PM
 * 
 * Copyright 2018 SolarNetwork.net Dev Team
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

package net.solarnetwork.central.daum.biz.dao;

import java.util.List;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import net.solarnetwork.central.datum.biz.AuditDatumBiz;
import net.solarnetwork.central.datum.dao.GeneralNodeDatumDao;
import net.solarnetwork.central.datum.domain.AggregateGeneralNodeDatumFilter;
import net.solarnetwork.central.datum.domain.AuditDatumRecordCounts;
import net.solarnetwork.central.domain.FilterResults;
import net.solarnetwork.central.domain.SortDescriptor;

/**
 * DAO based implementation of {@link AuditDatumBiz}.
 * 
 * @author matt
 * @version 1.0
 */
public class DaoAuditDatumBiz implements AuditDatumBiz {

	private final GeneralNodeDatumDao datumDao;

	/**
	 * Constructor.
	 * 
	 * @param datumDao
	 *        the datum DAO to use
	 */
	public DaoAuditDatumBiz(GeneralNodeDatumDao datumDao) {
		super();
		this.datumDao = datumDao;
	}

	@Override
	@Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
	public FilterResults<AuditDatumRecordCounts> findFilteredAuditRecordCounts(
			AggregateGeneralNodeDatumFilter filter, List<SortDescriptor> sortDescriptors, Integer offset,
			Integer max) {
		return datumDao.findAuditRecordCountsFiltered(filter, sortDescriptors, offset, max);
	}

	@Override
	@Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
	public FilterResults<AuditDatumRecordCounts> findFilteredAccumulativeAuditRecordCounts(
			AggregateGeneralNodeDatumFilter filter, List<SortDescriptor> sortDescriptors, Integer offset,
			Integer max) {
		return datumDao.findAccumulativeAuditRecordCountsFiltered(filter, sortDescriptors, offset, max);
	}

}
