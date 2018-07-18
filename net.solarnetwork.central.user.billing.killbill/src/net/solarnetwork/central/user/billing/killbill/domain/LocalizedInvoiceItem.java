/* ==================================================================
 * LocalizedInvoiceItem.java - 31/08/2017 11:42:53 AM
 * 
 * Copyright 2017 SolarNetwork.net Dev Team
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

package net.solarnetwork.central.user.billing.killbill.domain;

import java.util.List;
import net.solarnetwork.central.user.billing.domain.LocalizedInvoiceItemInfo;
import net.solarnetwork.central.user.billing.domain.LocalizedInvoiceItemUsageRecordInfo;

/**
 * Localized Killbill {@link InvoiceItem}.
 * 
 * @author matt
 * @version 1.0
 */
public class LocalizedInvoiceItem extends InvoiceItem implements LocalizedInvoiceItemInfo {

	private static final long serialVersionUID = 8400188959819683313L;

	private final LocalizedInvoiceItemInfo delegate;

	/**
	 * Constructor.
	 */
	public LocalizedInvoiceItem(InvoiceItem item, LocalizedInvoiceItemInfo delegate) {
		super(item);
		this.delegate = delegate;
	}

	@Override
	public String getLocalizedDescription() {
		return delegate.getLocalizedDescription();
	}

	@Override
	public String getLocalizedStartDate() {
		return delegate.getLocalizedStartDate();
	}

	@Override
	public String getLocalizedEndDate() {
		return delegate.getLocalizedEndDate();
	}

	@Override
	public String getLocalizedAmount() {
		return delegate.getLocalizedAmount();
	}

	@Override
	public List<LocalizedInvoiceItemUsageRecordInfo> getLocalizedInvoiceItemUsageRecords() {
		return delegate.getLocalizedInvoiceItemUsageRecords();
	}

}
