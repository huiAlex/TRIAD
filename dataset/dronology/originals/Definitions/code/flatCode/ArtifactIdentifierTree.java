package edu.nd.dronology.monitoring.tree;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Map.Entry;
import java.util.Set;

import edu.nd.dronology.core.monitoring.ArtifactIdentifier;
import edu.nd.dronology.util.NullUtil;

/**
 * Based on the structure of {@link ArtifactIdentifier} builds up a tree of the "id parts".
 * 
 * @author Michael Vierhauser
 * 
 */

public class ArtifactIdentifierTree {

	private final TreeNode root;

	public ArtifactIdentifierTree(final ArtifactIdentifier root) {
		if (root == null) {
			throw new IllegalArgumentException("Parameter root can't be null!");
		}
		this.root = new TreeNode(root);
	}

	public void clear() {
		root.clear();
	}

	/**
	 * Will add id to the tree by creating appropriate child elements, enabling {@link #getAllRelatedIdentifier(ArtifactIdentifier)}.
	 * 
	 * @param id
	 */
	public void add(final ArtifactIdentifier id) {
		NullUtil.checkNull(id);
		add(root, id, id);
	}

	/**
	 * Will step-wise create child elements based on the part-ids of "current".
	 * 
	 * @param parent
	 *          Node has to exist already.
	 * @param source
	 *          The original full identifier.
	 * @param current
	 *          The currently remaining id parts to be mapped into the tree.
	 * @return The next-step child node to the id.
	 */
	private TreeNode add(TreeNode parent, ArtifactIdentifier source, ArtifactIdentifier current) {
		NullUtil.checkNull(parent, current);
		// Construct an ident only from the first id part.
		ArtifactIdentifier art = new ArtifactIdentifier(current.getId());
		// Get an existing or create a new child node for this identifier.
		TreeNode child = parent.getChild(art);
		if (current.getChild() != null) {
			// If the id has more child parts.
			return add(child, source, current.getChild());
		}
		child.addElement(source);
		return child;
	}

	/**
	 * 
	 * @param id
	 * @return All {@link ArtifactIdentifier} currently in the tree and children of the given id.
	 */
	public Set<ArtifactIdentifier> getAllRelatedIdentifier(ArtifactIdentifier id) {
		NullUtil.checkNull(id);
		add(id);
		TreeNode matching = findMatchingNode(root.getChildren().get(new ArtifactIdentifier(id.getId())), id);
		return getChildren(matching);
	}

	/**
	 * 
	 * @param id
	 * @return All {@link ArtifactIdentifier} currently in the tree and children of the given id.
	 */
	public Set<ArtifactIdentifier> getParents(ArtifactIdentifier id) {
		NullUtil.checkNull(id);
		add(id);
		TreeNode matching = findMatchingNode(root.getChildren().get(new ArtifactIdentifier(id.getId())), id);
		return getParent(matching);
	}

	private Set<ArtifactIdentifier> getParent(TreeNode matching) {
		Set<ArtifactIdentifier> matches = new HashSet<>();
		if (matching == root) {
			return matches;
		}
		getParent(matching, matches);
		return matches;

	}

	private void getParent(TreeNode node, Set<ArtifactIdentifier> matches) {
		if (node == root) {
			return;
		}
		matches.addAll(node.getElements());
		getParent(node.getParent(), matches);
	}

	/**
	 * 
	 * @param matching
	 * @return Unmodifiable set-copy of all identifiers of all {@link TreeNode#getElements()} of all child treenodes of matching.
	 */
	private Set<ArtifactIdentifier> getChildren(TreeNode matching) {
		NullUtil.checkNull(matching);
		Set<ArtifactIdentifier> matches = new HashSet<>();
		// Add all elements from this node.
		matches.addAll(matching.getElements());
		// Recursively add all elements from child treenodes.
		for (Entry<ArtifactIdentifier, TreeNode> n : matching.getChildren().entrySet()) {
			matches.addAll(getChildren(n.getValue()));
		}
		return Collections.unmodifiableSet(matches);
	}

	/**
	 * Multi-step lookup to find a child/grandchild/etc that exactly matches the id.
	 * 
	 * @param parent
	 * @param id
	 * @return The node corresponding to the id.
	 */
	private TreeNode findMatchingNode(TreeNode parent, ArtifactIdentifier id) {
		if (id.getChild() == null) {
			return parent;
		}
		ArtifactIdentifier newArt = new ArtifactIdentifier(id.getChild().getId());
		return findMatchingNode(parent.getChildren().get(newArt), id.getChild());
	}

	@Override
	public String toString() {
		return "ArtifactIdentifierTree [root=" + root + "]";
	}

	/**
	 * Log some diagnostic output.
	 */
	@SuppressWarnings("unused")
	private void toLog() {
		root.toLog();
	}


	public void remove(ArtifactIdentifier id) {
		TreeNode node = findMatchingNode(root, id);
		if (node != null && node.getChildren().size() == 0 && node.getParent() != null) {
			node.getParent().remove(node);
		}

	}

	public int size() {
		int size = 0;
		size += count(root.getChildren().values());
		return size;
	}

	private int count(Collection<TreeNode> values) {
		int count = 0;
		for (TreeNode n : values) {
			count += n.getChildren().size();
			count += count(n.getChildren().values());
		}
		return count;
	}
}
