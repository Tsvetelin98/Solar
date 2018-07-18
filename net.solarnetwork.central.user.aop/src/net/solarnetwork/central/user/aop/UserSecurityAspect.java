/* ==================================================================
 * UserSecurityAspect.java - Oct 7, 2014 7:23:41 AM
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

package net.solarnetwork.central.user.aop;

import java.util.Set;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import net.solarnetwork.central.security.AuthorizationException;
import net.solarnetwork.central.user.biz.UserBiz;
import net.solarnetwork.central.user.dao.UserAuthTokenDao;
import net.solarnetwork.central.user.dao.UserNodeDao;
import net.solarnetwork.central.user.domain.UserAuthToken;
import net.solarnetwork.central.user.domain.UserAuthTokenType;
import net.solarnetwork.central.user.domain.UserNode;
import net.solarnetwork.central.user.support.AuthorizationSupport;

/**
 * Security enforcing AOP aspect for {@link UserBiz}.
 * 
 * @author matt
 * @version 1.1
 */
@Aspect
public class UserSecurityAspect extends AuthorizationSupport {

	private final UserAuthTokenDao userAuthTokenDao;

	/**
	 * Constructor.
	 * 
	 * @param userNodeDao
	 *        the UserNodeDao
	 * @param userAuthTokenDao
	 *        the UserAuthTokenDao
	 */
	public UserSecurityAspect(UserNodeDao userNodeDao, UserAuthTokenDao userAuthTokenDao) {
		super(userNodeDao);
		this.userAuthTokenDao = userAuthTokenDao;
	}

	@Pointcut("bean(aop*) && execution(* net.solarnetwork.central.user.biz.*UserBiz.getUser*(..)) && args(userId,..)")
	public void readUser(Long userId) {
	}

	@Pointcut("bean(aop*) && execution(* net.solarnetwork.central.user.biz.*UserBiz.getPendingUserNodeConfirmations(..)) && args(userId,..)")
	public void readUserNodeConfirmations(Long userId) {
	}

	@Pointcut("bean(aop*) && execution(* net.solarnetwork.central.user.biz.*UserBiz.getAllUserAuthTokens(..)) && args(userId,..)")
	public void readerUserAuthTokens(Long userId) {
	}

	@Pointcut("bean(aop*) && execution(* net.solarnetwork.central.user.biz.*UserBiz.getUserNode(..)) && args(userId,nodeId)")
	public void readUserNode(Long userId, Long nodeId) {
	}

	@Pointcut("bean(aop*) && execution(* net.solarnetwork.central.user.biz.*UserBiz.getArchivedUserNodes(..)) && args(userId)")
	public void readArchivedUserNodes(Long userId) {
	}

	@Pointcut("bean(aop*) && execution(* net.solarnetwork.central.user.biz.*UserBiz.updateUserNodeArchivedStatus(..)) && args(userId,nodeIds,..)")
	public void updateArchivedUserNodes(Long userId, Long[] nodeIds) {
	}

	@Pointcut("bean(aop*) && execution(* net.solarnetwork.central.user.biz.*UserBiz.generateUserAuthToken(..)) && args(userId,type,nodeIds)")
	public void generateAuthToken(Long userId, UserAuthTokenType type, Set<Long> nodeIds) {
	}

	@Pointcut("bean(aop*) && execution(* net.solarnetwork.central.user.biz.*UserBiz.deleteUserAuthToken(..)) && args(userId,token)")
	public void deleteAuthToken(Long userId, String token) {
	}

	@Pointcut("bean(aop*) && execution(* net.solarnetwork.central.user.biz.*UserBiz.updateUserAuthToken*(..)) && args(userId,token,..)")
	public void updateAuthToken(Long userId, String token) {
	}

	@Pointcut("bean(aop*) && execution(* net.solarnetwork.central.user.biz.*UserBiz.createAuthorizationV2Builder(..)) && args(userId,token,..)")
	public void createAuthorizationV2Builder(Long userId, String token) {
	}

	@Pointcut("bean(aop*) && execution(* net.solarnetwork.central.user.biz.*UserBiz.saveUserNode(..)) && args(entry)")
	public void updateUserNode(UserNode entry) {
	}

	@Before("readUser(userId) || readUserNodeConfirmations(userId) || readerUserAuthTokens(userId) || readArchivedUserNodes(userId)")
	public void userReadAccessCheck(Long userId) {
		requireUserReadAccess(userId);
	}

	@Before("readUserNode(userId, nodeId)")
	public void userNodeReadAccessCheck(Long userId, Long nodeId) {
		// the userReadAccessCheck method will also be called
		requireNodeWriteAccess(nodeId);
	}

	@Before("generateAuthToken(userId, type, nodeIds)")
	public void userReadAccessCheck(Long userId, UserAuthTokenType type, Set<Long> nodeIds) {
		requireUserWriteAccess(userId);
		if ( nodeIds != null ) {
			for ( Long nodeId : nodeIds ) {
				requireNodeWriteAccess(nodeId);
			}
		}
	}

	@Before("deleteAuthToken(userId, tokenId) || updateAuthToken(userId, tokenId) || createAuthorizationV2Builder(userId, tokenId)")
	public void updateAuthTokenAccessCheck(Long userId, String tokenId) {
		requireUserWriteAccess(userId);
		UserAuthToken token = userAuthTokenDao.get(tokenId);
		if ( token == null ) {
			throw new AuthorizationException(AuthorizationException.Reason.UNKNOWN_OBJECT, tokenId);
		}
		if ( userId.equals(token.getUserId()) == false ) {
			log.warn("Access DENIED to user {} for token {}; wrong user", userId, tokenId);
			throw new AuthorizationException(AuthorizationException.Reason.ACCESS_DENIED, tokenId);
		}
	}

	@Before("updateUserNode(entry)")
	public void updateUserNodeAccessCheck(UserNode entry) {
		if ( entry.getUser() == null ) {
			log.warn("Access DENIED to user node; no user ID");
			throw new AuthorizationException(AuthorizationException.Reason.UNKNOWN_OBJECT, null);
		}
		requireUserWriteAccess(entry.getUser().getId());
		if ( entry.getNode() == null ) {
			log.warn("Access DENIED to user node; no node ID");
			throw new AuthorizationException(AuthorizationException.Reason.UNKNOWN_OBJECT, null);
		}
		requireNodeWriteAccess(entry.getNode().getId());
	}

	@Before("updateArchivedUserNodes(userId, nodeIds)")
	public void updateArchivedUserNodesAccessCheck(Long userId, Long[] nodeIds) {
		if ( userId == null ) {
			log.warn("Access DENIED to user node; no user ID");
			throw new AuthorizationException(AuthorizationException.Reason.UNKNOWN_OBJECT, null);
		}
		if ( nodeIds == null || nodeIds.length < 1 ) {
			log.warn("Access DENIED to user nodes; no node IDs");
			throw new AuthorizationException(AuthorizationException.Reason.UNKNOWN_OBJECT, null);
		}
		requireUserWriteAccess(userId);
		for ( Long nodeId : nodeIds ) {
			requireNodeWriteAccess(nodeId);
		}
	}

}
