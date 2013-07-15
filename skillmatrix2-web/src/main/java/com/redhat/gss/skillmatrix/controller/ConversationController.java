package com.redhat.gss.skillmatrix.controller;

import javax.enterprise.context.Conversation;
import javax.enterprise.context.ConversationScoped;
import javax.inject.Inject;
import javax.inject.Named;
import java.io.Serializable;
import java.util.logging.Logger;

/**
 * Simple bean for managing conversations
 * @author jtrantin
 *
 */
@Named
@ConversationScoped
public class ConversationController implements Serializable {
	private static final long serialVersionUID = -3175199016361927651L;

	@Inject
	private transient Logger log;

	@Inject
	private transient Conversation conversation;

	// actions methods
	/**
	 * Starts a conversation if not started already. Can be used as action method.
	 */
	public void beginConversation() {
		if(conversation.isTransient()) {
			conversation.begin();
			log.info("conversation started " + conversation.getId());
		}
	}

}
